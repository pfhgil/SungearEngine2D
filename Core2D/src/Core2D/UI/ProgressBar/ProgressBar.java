package Core2D.UI.ProgressBar;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Resources;
import Core2D.Object2D.Object2D;
import Core2D.ShaderUtils.ShaderUtils;
import Core2D.Utils.Orientation;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46C;

// TODO: написать destroy метод
public class ProgressBar extends CommonDrawableObjectsParameters
{
    // минимальное значение проверки альфа для заливки
    private float minCheckAlphaFilling = 0.45f;
    // максимальное значение проверки альфа для заливки
    private float maxCheckAlphaFilling = 0.55f;

    // максимальное значение
    private float maxValue;
    // текущее значение
    private float currentValue;

    // цвет заливки
    private Vector4f fillingColor = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);

    // ориентация
    private int orientation = Orientation.HORIZONTAL;

    private Object2D progressBar;

    public ProgressBar(float maxValue)
    {
        this.maxValue = maxValue;

        create();
    }

    public ProgressBar(float maxValue, Vector4f fillingColor)
    {
        this.maxValue = maxValue;

        this.fillingColor = new Vector4f(fillingColor);

        create();
    }

    public ProgressBar(float maxValue, float minCheckAlphaFilling, float maxCheckAlphaFilling)
    {
        this.maxValue = maxValue;

        this.minCheckAlphaFilling = minCheckAlphaFilling;
        this.maxCheckAlphaFilling = maxCheckAlphaFilling;

        create();
    }

    public ProgressBar(float maxValue, Vector4f fillingColor, float minCheckAlphaFilling, float maxCheckAlphaFilling)
    {
        this.maxValue = maxValue;

        this.fillingColor = new Vector4f(fillingColor);

        this.minCheckAlphaFilling = minCheckAlphaFilling;
        this.maxCheckAlphaFilling = maxCheckAlphaFilling;

        create();
    }

    private void create()
    {
        progressBar = new Object2D();
        progressBar.loadShader(Resources.ShadersTexts.ProgressBar.fragmentShaderText, GL46C.GL_FRAGMENT_SHADER);
        progressBar.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.UI.ProgressBar.DEFAULT_PROGRESS_BAR_TEXTURE);
        progressBar.setUIElement(true);
        progressBar.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(0.64f * 2.0f, 0.16f * 2.0f));

        setCurrentValue(this.currentValue);
        setMaxValue(this.maxValue);

        setFillingColor(this.fillingColor);

        setMinCheckAlphaFilling(this.minCheckAlphaFilling);
        setMaxCheckAlphaFilling(this.maxCheckAlphaFilling);

        setOrientation(this.orientation);
    }

    @Override
    public void destroy()
    {
        shouldDestroy = true;

        fillingColor = null;

        progressBar.destroy();
        progressBar = null;
    }

    public Object2D getProgressBar() { return progressBar; }

    public float getMinCheckAlphaFilling() { return minCheckAlphaFilling; }
    public void setMinCheckAlphaFilling(float minCheckAlphaFilling)
    {
        this.minCheckAlphaFilling = minCheckAlphaFilling;

        progressBar.getShaderProgram().bind();

        ShaderUtils.setUniform(
                progressBar.getShaderProgram().getHandler(),
                "minCheckAlphaFilling",
                this.minCheckAlphaFilling
        );

        progressBar.getShaderProgram().unBind();
    }

    public float getMaxCheckAlphaFilling() { return maxCheckAlphaFilling; }
    public void setMaxCheckAlphaFilling(float maxCheckAlphaFilling)
    {
        this.maxCheckAlphaFilling = maxCheckAlphaFilling;

        progressBar.getShaderProgram().bind();

        ShaderUtils.setUniform(
                progressBar.getShaderProgram().getHandler(),
                "maxCheckAlphaFilling",
                this.maxCheckAlphaFilling
        );

        progressBar.getShaderProgram().unBind();
    }

    public float getMaxValue() { return maxValue; }
    public void setMaxValue(float maxValue)
    {
        this.maxValue = maxValue;

        progressBar.getShaderProgram().bind();

        ShaderUtils.setUniform(
                progressBar.getShaderProgram().getHandler(),
                "maxValue",
                this.maxValue
        );

        progressBar.getShaderProgram().unBind();
    }

    public float getCurrentValue() { return currentValue; }
    public void setCurrentValue(float currentValue)
    {
        this.currentValue = currentValue;
        if(this.currentValue > this.maxValue) {
            this.currentValue = this.maxValue;
        }

        progressBar.getShaderProgram().bind();

        ShaderUtils.setUniform(
                progressBar.getShaderProgram().getHandler(),
                "currentValue",
                this.currentValue
        );

        progressBar.getShaderProgram().unBind();
    }

    public Vector4f getFillingColor() { return fillingColor; }
    public void setFillingColor(Vector4f fillingColor)
    {
        this.fillingColor = fillingColor;

        progressBar.getShaderProgram().bind();

        ShaderUtils.setUniform(
                progressBar.getShaderProgram().getHandler(),
                "fillingColor",
                this.fillingColor
        );

        progressBar.getShaderProgram().unBind();
    }

    public int getOrientation() { return orientation; }
    public void setOrientation(int orientation)
    {
        this.orientation = orientation;

        if(orientation == Orientation.VERTICAL) {
            progressBar.getComponent(TransformComponent.class).getTransform().setRotation(90.0f);
        } else {
            progressBar.getComponent(TransformComponent.class).getTransform().setRotation(0.0f);
        }
    }
}
