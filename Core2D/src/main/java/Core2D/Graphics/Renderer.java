package Core2D.Graphics;

import Core2D.Camera2D.CamerasManager;
import Core2D.Component.Component;
import Core2D.Component.Components.TextureComponent;
import Core2D.Drawable.AtlasDrawing;
import Core2D.Drawable.Drawable;
import Core2D.Drawable.Instancing.LinesInstancing;
import Core2D.Drawable.Instancing.ObjectsInstancing;
import Core2D.Drawable.Object2D;
import Core2D.Drawable.Primitives.Line2D;
import Core2D.Drawable.UI.Button.Button;
import Core2D.Drawable.UI.InputField.InputField;
import Core2D.Drawable.UI.ProgressBar.ProgressBar;
import Core2D.Drawable.UI.Text.Text;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.ShaderUtils.ShaderUtils;
import Core2D.Utils.WrappedObject;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL46C;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_LINES;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Renderer
{
    public void render(Object2D object2D)
    {
        if(object2D.isActive() && !object2D.isShouldDestroy()) {
            for(Component component : object2D.getComponents()) {
                component.update();
            }

            TextureComponent textureComponent = object2D.getComponent(TextureComponent.class);
            // двойная проверка, потому что произошло обновление компонентов (в скрипт компоненте объект может удалиться
            // или сцены могут смениться => флаг isShouldDestroy становится true
            if(textureComponent != null && object2D.isActive() && !object2D.isShouldDestroy()) {
                // использую VAO
                object2D.getVertexArrayObject().bind();

                // использую текстуру
                textureComponent.getTexture2D().bind();

                // использовать шейдер
                object2D.getShaderProgram().bind();

                object2D.update();

                ShaderUtils.setUniform(
                        object2D.getShaderProgram().getHandler(),
                        "mvpMatrix",
                        object2D.getMvpMatrix()
                );

                ShaderUtils.setUniform(
                        object2D.getShaderProgram().getHandler(),
                        "color",
                        object2D.getColor()
                );

                ShaderUtils.setUniform(
                        object2D.getShaderProgram().getHandler(),
                        "drawMode",
                        object2D.getComponent(TextureComponent.class).getTextureDrawMode()
                );

                ShaderUtils.setUniform(
                        object2D.getShaderProgram().getHandler(),
                        "sampler",
                        textureComponent.getTexture2D().getFormattedTextureBlock()
                );

                // нарисовать два треугольника
                glDrawElements(object2D.getDrawingMode(), 6, GL_UNSIGNED_SHORT, 0);

                // прекращаю использование шейдерной программы
                object2D.getShaderProgram().unBind();

                // прекращаю использование текстуры
                textureComponent.getTexture2D().unBind();

                // прекращаю использование VAO
                object2D.getVertexArrayObject().unBind();
            } else {
                renderWithoutTexture(object2D);
            }
        }
    }

    public void render(Text text)
    {
        if(text.isActive() && !text.isShouldDestroy()) {
            render(text.getTextObjectsInstancing());
        }
    }

    public void render(ProgressBar progressBar)
    {
        if(progressBar.isActive() && !progressBar.isShouldDestroy()) {
            render(progressBar.getProgressBar());
        }
    }

    public void render(Line2D line2D)
    {
        if(line2D.isActive() && !line2D.isShouldDestroy()) {
            line2D.getVertexArrayObject().bind();

            // использовать шейдер
            line2D.getShaderProgram().bind();

            line2D.update();

            ShaderUtils.setUniform(
                    line2D.getShaderProgram().getHandler(),
                    "mvpMatrix",
                    line2D.getMvpMatrix()
            );
            ShaderUtils.setUniform(
                    line2D.getShaderProgram().getHandler(),
                    "color",
                    new Vector4f(line2D.getColor())
            );

            glLineWidth(line2D.getLineWidth());
            glDrawElements(GL11.GL_LINES, 2, GL_UNSIGNED_SHORT, 0);
            glLineWidth(1.0f);

            line2D.getShaderProgram().unBind();

            line2D.getVertexArrayObject().unBind();
        }
    }

    public void render(InputField inputField)
    {
        if(inputField.isActive() && !inputField.isShouldDestroy()) {
            render(inputField.getInputField());
            render(inputField.getText());
            if(inputField.isSelected()) {
                render(inputField.getCursor().getCursor());
            }

            inputField.update();
        }
    }

    public void render(ObjectsInstancing objectsInstancing)
    {
        if(objectsInstancing.isActive() &&
                objectsInstancing.getDrawableObjects2D().size() != 0 &&
                !objectsInstancing.isShouldDestroy()) {
            objectsInstancing.update();

            objectsInstancing.getAtlasTexture2D().bind();

            glBindVertexArray(objectsInstancing.getVAOID());

            objectsInstancing.getShaderProgram().bind();

            if (CamerasManager.getMainCamera2D() != null) {
                ShaderUtils.setUniform(
                        objectsInstancing.getShaderProgram().getHandler(),
                        "cameraMatrix",
                        CamerasManager.getMainCamera2D().getViewMatrix()
                );

                ShaderUtils.setUniform(
                        objectsInstancing.getShaderProgram().getHandler(),
                        "projectionMatrix",
                        CamerasManager.getMainCamera2D().getProjectionMatrix()
                );
            }

            ShaderUtils.setUniform(
                    objectsInstancing.getShaderProgram().getHandler(),
                    "isUIInstancing",
                    objectsInstancing.isUIInstancing()
            );

            GL46C.glDrawElementsInstanced(GL_TRIANGLES, 6, GL11C.GL_UNSIGNED_SHORT, 0, objectsInstancing.getDrawableObjects2D().size());

            objectsInstancing.getShaderProgram().unBind();

            glBindVertexArray(0);

            objectsInstancing.getAtlasTexture2D().unBind();
        }
    }

    public void render(LinesInstancing linesInstancing)
    {
        if(linesInstancing.isActive() &&
                linesInstancing.getDrawableLines2D().size() != 0 &&
                !linesInstancing.isShouldDestroy()) {
            linesInstancing.update();

            glBindVertexArray(linesInstancing.getVAOID());

            linesInstancing.getShaderProgram().bind();

            if(CamerasManager.getMainCamera2D() != null) {
                ShaderUtils.setUniform(
                        linesInstancing.getShaderProgram().getHandler(),
                        "projectionMatrix",
                        CamerasManager.getMainCamera2D().getProjectionMatrix()
                );

                ShaderUtils.setUniform(
                        linesInstancing.getShaderProgram().getHandler(),
                        "cameraMatrix",
                        CamerasManager.getMainCamera2D().getViewMatrix()
                );
            }

            ShaderUtils.setUniform(
                    linesInstancing.getShaderProgram().getHandler(),
                    "isUIInstancing",
                    linesInstancing.isUIInstancing()
            );

            GL46C.glLineWidth(linesInstancing.getLinesWidth());
            GL46C.glDrawElementsInstanced(GL_LINES, 2, GL11C.GL_UNSIGNED_SHORT, 0, linesInstancing.getDrawableLines2D().size());
            GL46C.glLineWidth(1.0f);

            linesInstancing.getShaderProgram().unBind();

            glBindVertexArray(0);
        }
    }

    public void render(Button button)
    {
        if(button.isActive() && !button.isShouldDestroy()) {
            button.update();
            render(button.getButton());
            if(button.getText() != null) render(button.getText());
        }
    }

    public void render(AtlasDrawing atlasDrawing)
    {
        if(atlasDrawing.isActive() && !atlasDrawing.isShouldDestroy()) {
            atlasDrawing.getAtlasTexture2D().bind();

            for (Object2D drawableObject2D : atlasDrawing.getDrawableObjects2D()) {
                renderWithoutTexture(drawableObject2D);
            }

            atlasDrawing.getAtlasTexture2D().unBind();
        }
    }

    public void render(Layering layering)
    {
        if(layering.isShouldDestroy()) return;
        int layersNum = layering.getLayers().size();
        for(int i = 0; i < layersNum; i++) {
            if(layering.isShouldDestroy()) break;
            render(layering.getLayers().get(i));
        }
    }

    public void render(Layer layer)
    {
        if(layer.isShouldDestroy()) return;

        int renderingObjectsNum = layer.getRenderingObjects().size();
        for(int i = 0; i < renderingObjectsNum; i++) {
            if(layer.isShouldDestroy()) break;
            render(layer.getRenderingObjects().get(i));
        }
    }

    public void render(WrappedObject wrappedObject)
    {
        Object object = wrappedObject.getObject();
        if(object instanceof Drawable && ((Drawable) object).isActive()) {
            ((Drawable) object).render();
        }
    }

    public void renderWithoutTexture(Object2D object2D)
    {
        if(object2D.isActive() && !object2D.isShouldDestroy()) {
            // использую VAO
            object2D.getVertexArrayObject().bind();

            // использовать шейдер
            object2D.getShaderProgram().bind();

            object2D.update();

            ShaderUtils.setUniform(
                    object2D.getShaderProgram().getHandler(),
                    "mvpMatrix",
                    object2D.getMvpMatrix()
            );

            ShaderUtils.setUniform(
                    object2D.getShaderProgram().getHandler(),
                    "color",
                    object2D.getColor()
            );

            // нарисовать два треугольника
            glDrawElements(object2D.getDrawingMode(), 6, GL_UNSIGNED_SHORT, 0);

            // прекращаю использование шейдерной программы
            object2D.getShaderProgram().unBind();

            // прекращаю использование VAO
            object2D.getVertexArrayObject().unBind();
        }
    }
}
