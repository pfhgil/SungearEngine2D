package Core2D.Graphics.OpenGL;

import Core2D.Log.Log;
import Core2D.Utils.Debugger;
import Core2D.Utils.ExceptionsUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46C;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class OpenGL
{
    public static final int GL_GPU_MEM_INFO_TOTAL_AVAILABLE_MEM_NVX = 0x9048;
    public static final int GL_GPU_MEM_INFO_CURRENT_AVAILABLE_MEM_NVX = 0x9049;

    // переменные для предотвращения бинда одних и тех же объектов гпу
    public static int currentVAOHandler = -1;
    public static int currentTextureHandler = -1;

    public static void init()
    {
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void glCall(Consumer<Object[]> func)
    {
        if(!Thread.currentThread().getName().equals("main")) {
            Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("OpenGL warning: the OpenGL function was not called in the main thread. ")), Log.MessageType.WARNING);
            return;
        }
        func.accept(null);
        checkForGLErrors();
    }

    public static <T> T glCall(Function<Object[], ?> func, Class<T> toClass)
    {
        if(!Thread.currentThread().getName().equals("main")) {
            Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("OpenGL warning: the OpenGL function was not called in the main thread. ")), Log.MessageType.WARNING);
            return toClass.cast(0);
        }
        Object obj = func.apply(null);
        checkForGLErrors();
        return toClass.cast(obj);
    }

    public static void glDrawElements(int mode, int count, int type, long indices)
    {
        OpenGL.glCall((params) -> GL46C.glDrawElements(mode, count, type, indices));

        Debugger.drawCallsNum++;
    }

    public static void glDrawArrays(int mode, int start, int count)
    {
        OpenGL.glCall((params) -> GL46C.glDrawArrays(mode, start, count));

        Debugger.drawCallsNum++;
    }

    public static void glBindTexture(int handler, int textureBlock)
    {
        if(currentTextureHandler != handler) {
            OpenGL.glCall((params) -> glActiveTexture(textureBlock));
            OpenGL.glCall((params) -> GL46C.glBindTexture(GL_TEXTURE_2D, handler));

            Debugger.textureBindCallsNum++;

            currentTextureHandler = handler;
        }
    }

    public static void checkForGLErrors()
    {
        int errCode = GL46C.glGetError();
        if(errCode != GL46C.GL_NO_ERROR) {
            String stringErr = switch(errCode) {
                case GL46C.GL_INVALID_ENUM -> "GL_INVALID_ENUM: enumeration parameter is not a legal enumeration for that function.";
                case GL46C.GL_INVALID_VALUE -> "GL_INVALID_VALUE: value parameter is not a legal value for that function.";
                case GL46C.GL_INVALID_OPERATION -> "GL_INVALID_OPERATION: set of state for a command is not legal for the parameters given to that command.";
                case GL46C.GL_STACK_OVERFLOW -> "GL_STACK_OVERFLOW: stack pushing operation cannot be done because it would overflow the limit of that stack's size.";
                case GL46C.GL_STACK_UNDERFLOW -> "GL_STACK_UNDERFLOW: stack popping operation cannot be done because the stack is already at its lowest point.";
                case GL46C.GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY: performing an operation that can allocate memory, and the memory cannot be allocated.";
                case GL46C.GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION: doing anything that would attempt to read from or write/render to a framebuffer that is not complete.";
                case GL46C.GL_CONTEXT_LOST -> "GL_CONTEXT_LOST: the OpenGL context has been lost, due to a graphics card reset.";

                default -> "Unknown error.";
            };

            Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("OpenGL error: " + stringErr + " (code: " + errCode + "). ")), Log.MessageType.ERROR);
        }
    }
}
