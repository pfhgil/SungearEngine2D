package Core2D.ECS.Component.Components;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.Script;

/**
 * The ScriptComponent.
 * @see Script
 */
public class ScriptComponent extends Component
{
    public Script script = new Script();

    public ScriptComponent() { }

    public ScriptComponent(ScriptComponent component)
    {
        set(component);
    }

    /**
     * Applies component parameters to this component.
     * @see Component#set(Component)
     * @param component ScriptComponent.
     */
    @Override
    public void set(Component component)
    {
        if(component instanceof ScriptComponent) {
            script.set(((ScriptComponent) component).script);
        }
    }

    /**
     * Calls the update method of the script if current scene is set.
     * @see Component#update()
     * @see Script#update()
     */
    @Override
    public void update()
    {
        super.update();
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        script.update();
    }

    /**
     * Calls the deltaUpdate method of the script if current scene is set.
     * @see Component#deltaUpdate(float)
     * @see Script#deltaUpdate(float)
     */
    @Override
    public void deltaUpdate(float deltaTime)
    {
        //s
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        script.deltaUpdate(deltaTime);
    }

    /**
     * Calls the collider2DEnter method of the script if current scene is set.
     * @see Script#collider2DEnter(Entity)
     */
    public void collider2DEnter(Entity otherObj)
    {
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        script.collider2DEnter(otherObj);
    }

    /**
     * Calls the collider2DExit method of the script if current scene is set.
     * @see Script#collider2DExit(Entity)
     */
    public void collider2DExit(Entity otherObj)
    {
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        script.collider2DExit(otherObj);
    }

    @Override
    public void render(CameraComponent cameraComponent)
    {
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        script.render(cameraComponent);
    }

    @Override
    public void render(CameraComponent cameraComponent, Shader shader)
    {
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        script.render(cameraComponent, shader);
    }
}
