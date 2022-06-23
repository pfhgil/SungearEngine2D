package Core2D.Instancing.Primitives;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Resources;
import Core2D.Primitives.Line2D;
import Core2D.Shader.Shader;
import Core2D.Shader.ShaderProgram;
import Core2D.ShaderUtils.ShaderUtils;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.opengl.GL33C.glVertexAttribDivisor;

public class LinesInstancing extends CommonDrawableObjectsParameters
{
    private List<Line2D> drawableLines2D;
    private boolean isUIInstancing;

    private float[] verticesPositionsData;
    private float[] matricesData;
    private float[] colorData;

    private short[] indices = new short[] { 0, 1 };

    private ShaderProgram shaderProgram;

    private int verticesPositionsDataBuffer;
    private int matricesDataBuffer;
    private int colorBuffer;

    private int indexBuffer;
    private int vao;

    private float linesWidth = 1.0f;

    public LinesInstancing(Line2D[] drawableLines2D, boolean isUIInstancing)
    {
        if(drawableLines2D != null) {
            this.drawableLines2D = new ArrayList<>(Arrays.asList(drawableLines2D));
        } else {
            this.drawableLines2D = new ArrayList<>();
        }
        drawableLines2D = null;

        this.isUIInstancing = isUIInstancing;

        create();
    }

    private void create()
    {
        // загружаю шейдеры
        Shader vertexShader = new Shader(Resources.ShadersTexts.Instancing.Primitives.Line2D.vertexShaderText, GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(Resources.ShadersTexts.Instancing.Primitives.Line2D.fragmentShaderText, GL_FRAGMENT_SHADER);

        // создаю шейдерную программу
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        vertexShader = null;
        fragmentShader = null;

        shaderProgram.bind();

        ShaderUtils.setUniform(
                shaderProgram.getHandler(),
                "projectionMatrix",
                Core2D.getProjectionMatrix()
        );

        ShaderUtils.setUniform(
                shaderProgram.getHandler(),
                "isUIInstancing",
                isUIInstancing
        );

        shaderProgram.unBind();

        updateArray();

        verticesPositionsDataBuffer = glGenBuffers();
        matricesDataBuffer = glGenBuffers();
        colorBuffer = glGenBuffers();

        indexBuffer = glGenBuffers();

        vao = glGenVertexArrays();
        GL46C.glBindVertexArray(vao);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        int vec2Size = 2 * 4;

        glBindBuffer(GL_ARRAY_BUFFER, verticesPositionsDataBuffer);
        glBufferData(GL_ARRAY_BUFFER, verticesPositionsData, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, vec2Size * 2, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, vec2Size * 2, vec2Size);

        glVertexAttribDivisor(0, 1);
        glVertexAttribDivisor(1, 1);

        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, matricesDataBuffer);
        glBufferData(GL_ARRAY_BUFFER, matricesData, GL_DYNAMIC_DRAW);

        glBindVertexArray(vao);
        int vec4Size = 4 * 4;

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, vec4Size * 4, 0);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, vec4Size * 4, vec4Size);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, vec4Size * 4, (vec4Size * 2));
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, vec4Size * 4, (vec4Size * 3));

        glVertexAttribDivisor(2, 1);
        glVertexAttribDivisor(3, 1);
        glVertexAttribDivisor(4, 1);
        glVertexAttribDivisor(5, 1);

        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        glBufferData(GL_ARRAY_BUFFER, colorData, GL_DYNAMIC_DRAW);

        glBindVertexArray(vao);

        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 4, GL11.GL_FLOAT, false, vec4Size, 0);

        glVertexAttribDivisor(6, 1);

        glBindVertexArray(0);
    }

    private void updateArray()
    {
        verticesPositionsData = new float[drawableLines2D.size() * 4];
        matricesData = new float[drawableLines2D.size() * 16];
        colorData = new float[drawableLines2D.size() * 4];

        int iter0 = 0;
        int iter1 = 0;
        int iter2 = 0;

        int k = 0;

        for(int i = 0; i < drawableLines2D.size(); i++) {
            if(drawableLines2D.get(i).isActive()) {
                float[] modelMatrix = new float[16];
                modelMatrix = drawableLines2D.get(i).getTransform().getModelMatrix().get(modelMatrix);

                for(k = 0; k < 4; k++) {
                    verticesPositionsData[iter0] = drawableLines2D.get(i).getData()[k];
                    iter0++;
                }

                for(k = 0; k < modelMatrix.length; k++) {
                    matricesData[iter1] = modelMatrix[k];
                    iter1++;
                }

                Vector4f color = drawableLines2D.get(i).getColor();
                for (k = 0; k < 4; k++) {
                    colorData[iter2] = color.get(k);
                    iter2++;
                }

                color = null;
                modelMatrix = null;
            }
        }
    }

    @Override
    public void update()
    {
        updateVBO();
    }

    private void updateVBO()
    {
        updateArray();

        glBindBuffer(GL_ARRAY_BUFFER, verticesPositionsDataBuffer);
        glBufferData(GL_ARRAY_BUFFER, verticesPositionsData, GL_DYNAMIC_DRAW);
        //glBufferSubData(GL_ARRAY_BUFFER, 0, textureCoordsData);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, matricesDataBuffer);
        glBufferData(GL_ARRAY_BUFFER, matricesData, GL_DYNAMIC_DRAW);
        //glBufferSubData(GL_ARRAY_BUFFER, 0, matricesData);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        glBufferData(GL_ARRAY_BUFFER, colorData, GL_DYNAMIC_DRAW);
        //glBufferSubData(GL_ARRAY_BUFFER, 0, colorData);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void destroy()
    {
        shouldDestroy = true;

        Iterator<Line2D> lines2DIterator = drawableLines2D.iterator();
        while(lines2DIterator.hasNext()) {
            Line2D line2D = lines2DIterator.next();
            line2D.destroy();
            line2D = null;
            lines2DIterator.remove();
        }
        lines2DIterator = null;
        drawableLines2D = null;

        verticesPositionsData = null;

        matricesData = null;

        colorData = null;

        indices = null;

        shaderProgram.delete();
        shaderProgram = null;

        destroyLayerObject();
    }

    public boolean isUIInstancing() { return isUIInstancing; }
    public void setUIInstancing(boolean UIInstancing)
    {
        isUIInstancing = UIInstancing;

        shaderProgram.bind();

        ShaderUtils.setUniform(
                shaderProgram.getHandler(),
                "isUIInstancing",
                isUIInstancing
        );

        shaderProgram.unBind();
    }

    public float getLinesWidth() { return linesWidth; }
    public void setLinesWidth(float linesWidth) { this.linesWidth = linesWidth; }

    public List<Line2D> getDrawableLines2D() { return drawableLines2D; }

    public ShaderProgram getShaderProgram() { return shaderProgram; }

    public int getVAOID() { return vao; }
}
