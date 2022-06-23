package Core2D.Graphics;

import Core2D.AtlasDrawing.AtlasDrawing;
import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Component.Component;
import Core2D.Component.Components.TextureComponent;
import Core2D.Controllers.PC.Mouse;
import Core2D.Core2D.Core2D;
import Core2D.Instancing.ObjectsInstancing;
import Core2D.Instancing.Primitives.LinesInstancing;
import Core2D.Layering.Layer;
import Core2D.Layering.LayerObject;
import Core2D.Layering.Layering;
import Core2D.Object2D.Object2D;
import Core2D.Primitives.Line2D;
import Core2D.ShaderUtils.ShaderUtils;
import Core2D.UI.Button.Button;
import Core2D.UI.InputField.InputField;
import Core2D.UI.ProgressBar.ProgressBar;
import Core2D.UI.Text.Text;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL46C;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11C.GL_LINES;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Renderer
{
    public void render(Object2D object2D)
    {
        if(object2D.isActive()) {
            for(Component component : object2D.getComponents()) {
                component.update();
            }

            TextureComponent textureComponent = object2D.getComponent(TextureComponent.class);
            if(textureComponent != null) {
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

            textureComponent = null;
        }
    }

    public void render(Text text)
    {
        if(text.isActive()) {
            render(text.getTextObjectsInstancing());
        }
    }

    public void render(ProgressBar progressBar)
    {
        if(progressBar.isActive()) {
            render(progressBar.getProgressBar());
        }
    }

    public void render(Line2D line2D)
    {
        if(line2D.isActive()) {
            line2D.getVertexArrayObject().bind();

            // использовать шейдер
            line2D.getShaderProgram().bind();

            line2D.update();
            ShaderUtils.setUniform(
                    line2D.getShaderProgram().getHandler(),
                    "mvpMatrix",
                    line2D.getMvpMatrix()
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
        if(inputField.isActive()) {
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
        if(objectsInstancing.isActive() && objectsInstancing.getDrawableObjects2D().size() != 0) {
            objectsInstancing.update();

            objectsInstancing.getAtlasTexture2D().bind();

            glBindVertexArray(objectsInstancing.getVAOID());

            objectsInstancing.getShaderProgram().bind();

            if (objectsInstancing.getDrawableObjects2D().get(0).getAttachedCamera2D() != null) {
                ShaderUtils.setUniform(
                        objectsInstancing.getShaderProgram().getHandler(),
                        "cameraMatrix",
                        objectsInstancing.getDrawableObjects2D().get(0).getAttachedCamera2D().getTransform().getModelMatrix()
                );
            }

            GL46C.glDrawElementsInstanced(GL_TRIANGLES, 6, GL11C.GL_UNSIGNED_SHORT, 0, objectsInstancing.getDrawableObjects2D().size());

            objectsInstancing.getShaderProgram().unBind();

            glBindVertexArray(0);

            objectsInstancing.getAtlasTexture2D().unBind();
        }
    }

    public void render(LinesInstancing linesInstancing)
    {
        if(linesInstancing.isActive() && linesInstancing.getDrawableLines2D().size() != 0) {
            linesInstancing.update();

            glBindVertexArray(linesInstancing.getVAOID());

            linesInstancing.getShaderProgram().bind();

            ShaderUtils.setUniform(
                    linesInstancing.getShaderProgram().getHandler(),
                    "projectionMatrix",
                    Core2D.getProjectionMatrix()
            );

            if (linesInstancing.getDrawableLines2D().get(0).getAttachedCamera2D() != null) {
                ShaderUtils.setUniform(
                        linesInstancing.getShaderProgram().getHandler(),
                        "cameraMatrix",
                        linesInstancing.getDrawableLines2D().get(0).getAttachedCamera2D().getTransform().getModelMatrix()
                );
            }

            GL46C.glLineWidth(linesInstancing.getLinesWidth());
            GL46C.glDrawElementsInstanced(GL_LINES, 2, GL11C.GL_UNSIGNED_SHORT, 0, linesInstancing.getDrawableLines2D().size());
            GL46C.glLineWidth(1.0f);

            linesInstancing.getShaderProgram().unBind();

            glBindVertexArray(0);
        }
    }

    public void render(Button button)
    {
        if(button.isActive()) {
            button.update();
            render(button.getButton());
            if(button.getText() != null) render(button.getText());
        }
    }

    public void render(AtlasDrawing atlasDrawing)
    {
        if(atlasDrawing.isActive()) {
            atlasDrawing.getAtlasTexture2D().bind();

            for (Object2D drawableObject2D : atlasDrawing.getDrawableObjects2D()) {
                renderWithoutTexture(drawableObject2D);
            }

            atlasDrawing.getAtlasTexture2D().unBind();
        }
    }

    public void render(Layering layering)
    {
        for(Layer layer : layering.getLayers()) {
            render(layer);
        }
    }

    public void render(Layer layer)
    {
        for(int i = 0; i < layer.getRenderingObjects().size(); i++) {
            render(layer.getRenderingObjects().get(i));
        }
    }

    public void render(LayerObject layerObject)
    {
        Object object = layerObject.getObject();
        if(object instanceof CommonDrawableObjectsParameters && ((CommonDrawableObjectsParameters) object).isActive()) {
            if(object instanceof Line2D) {
                render((Line2D) object);
            } else if(object instanceof Object2D) {
                render((Object2D) object);
            } else if(object instanceof Text) {
                render((Text) object);
            } else if(object instanceof ProgressBar) {
                render((ProgressBar) object);
            } else if(object instanceof InputField) {
                render((InputField) object);
            } else if(object instanceof ObjectsInstancing) {
                render((ObjectsInstancing) object);
            } else if(object instanceof LinesInstancing) {
                render((LinesInstancing) object);
            } else if(object instanceof Button) {
                render((Button) object);
            } else if(object instanceof AtlasDrawing) {
                render((AtlasDrawing) object);
            }
        }
        object = null;
    }

    public void renderWithoutTexture(Object2D object2D)
    {
        if(object2D.isActive()) {
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

            // нарисовать два треугольника
            glDrawElements(object2D.getDrawingMode(), 6, GL_UNSIGNED_SHORT, 0);

            // прекращаю использование шейдерной программы
            object2D.getShaderProgram().unBind();

            // прекращаю использование VAO
            object2D.getVertexArrayObject().unBind();
        }
    }
}
