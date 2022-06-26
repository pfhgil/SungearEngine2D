package Core2D.Shader;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;

import java.io.Serializable;

import static org.lwjgl.opengl.GL20C.*;

public class ShaderProgram implements Serializable, AutoCloseable
{
    // id программы
    private transient int programHandler;

    // вершинный шейдер
    private transient Shader vertexShader;
    // фрагментный шейдер
    private transient Shader fragmentShader;

    public ShaderProgram(Shader vertexShader, Shader fragmentShader)
    {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;

        programHandler = glCreateProgram();

        // привязываю вершинный и фрагментный шейдер к программе
        glAttachShader(programHandler, vertexShader.getHandler());
        glAttachShader(programHandler, fragmentShader.getHandler());

        // соединяю шейдеры в одну программу
        glLinkProgram(programHandler);

        // получение статуса соединения шейдров
        int linkStatus = 0;
        linkStatus = glGetProgrami(programHandler, GL_LINK_STATUS);

        // если статус соединения шейдров = 0 (не соединены), то выводить ошибку и удалять программу и шейдеры из памяти
        if(linkStatus == 0) {
            // получение максимальной длины ошибки
            int maxErrorStringLength = 0;
            maxErrorStringLength = glGetProgrami(programHandler, GL_INFO_LOG_LENGTH);

            // получение строки ошибки
            String errorString = "";
            errorString = glGetProgramInfoLog(programHandler, maxErrorStringLength);

            // удаление программы
            destroy();
            // удаление шейдеров
            vertexShader.destroy();
            fragmentShader.destroy();

            // вывод ошибки в консоль
            System.out.println("Error while creating and linking program. Error is: " + errorString);
        }

        // отвязка шейдеров от программы
        detachShaders();
    }

    public void destroy()
    {
        // отвязка шейдеров от программы
        detachShaders();
        // удаление программы
        glDeleteProgram(programHandler);

        if(fragmentShader != null) fragmentShader.destroy();
        if(vertexShader != null) vertexShader.destroy();

        fragmentShader = null;
        vertexShader = null;

        try {
            close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }
    public void detachShaders()
    {
        // отвязка шейдеров от программы
        if(vertexShader != null) glDetachShader(programHandler, vertexShader.getHandler());
        if(fragmentShader != null) glDetachShader(programHandler, fragmentShader.getHandler());
    }
    public void bind()
    {
        glUseProgram(programHandler);
    }
    public void unBind()
    {
        glUseProgram(0);
    }

    // геттеры и сеттеры
    public Shader getVertexShader() { return vertexShader; }
    public void setVertexShader(Shader _vertexShader) { vertexShader = _vertexShader; }

    public Shader getFragmentShader() { return fragmentShader; }
    public void setFragmentShader(Shader _fragmentShader) { fragmentShader = _fragmentShader; }

    public int getHandler() { return programHandler; }

    @Override
    public void close() throws Exception {

    }
}
