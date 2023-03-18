package Core2D.ECS.Component.Components;

import Core2D.ECS.Component.Components.Shader.ShaderUniformFloatComponent;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class ProgramTimeComponent extends ShaderUniformFloatComponent
{
    @Override
    public void update()
    {
        uniformValue = getProgramTimeInFloat();
    }

    public double getProgramTimeInDouble()
    {
        return glfwGetTime();
    }

    public float getProgramTimeInFloat()
    {
        return (float) glfwGetTime();
    }
}
