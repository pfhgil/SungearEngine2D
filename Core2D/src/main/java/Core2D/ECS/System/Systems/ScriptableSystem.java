package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.CameraComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.Script;

public class ScriptableSystem extends System
{
    public Script script = new Script();

    public ScriptableSystem() { }

    public void set(ScriptableSystem scriptableSystem)
    {
        script.set(scriptableSystem.script);
    }

    /**
     * Calls the update method of the script if current scene is set.
     * @see Component#update()
     * @see Script#update()
     */
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        //super.update(componentsQuery);
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        script.update();
    }

    /**
     * Calls the deltaUpdate method of the script if current scene is set.
     * @see Component#deltaUpdate(float)
     * @see Script#deltaUpdate(float)
     */
    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
    {
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        script.deltaUpdate(deltaTime);
    }

    /**
     * Calls the collider2DEnter method of the script if current scene is set.
     * @see Script#collider2DEnter(Entity)
     */
    @Override
    public void collider2DEnter(Entity otherEntity)
    {
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        script.collider2DEnter(otherEntity);
    }

    /**
     * Calls the collider2DExit method of the script if current scene is set.
     * @see Script#collider2DExit(Entity)
     */
    @Override
    public void collider2DExit(Entity otherEntity)
    {
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        script.collider2DExit(otherEntity);
    }

    @Override
    public void renderEntity(Entity entity, CameraComponent cameraComponent)
    {
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        // FIXME:
        //script.render(camera2DComponent);
    }

    @Override
    public void renderEntity(Entity entity, CameraComponent cameraComponent, Shader shader)
    {
        if(!SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) return;
        // FIXME:
        // script.render(camera2DComponent, shader);
    }
}
