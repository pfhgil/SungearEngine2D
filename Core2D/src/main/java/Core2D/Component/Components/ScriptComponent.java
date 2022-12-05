package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.GameObject.GameObject;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.Script;

/**
 * The ScriptComponent.
 * @see Script
 */
public class ScriptComponent extends Component
{
    private Script script;

    public ScriptComponent() { this.script = new Script(); }

    /**
     * Applies component parameters to this component.
     * @see Component#set(Component)
     * @param component ScriptComponent.
     */
    @Override
    public ScriptComponent set(Component component)
    {
        if(component instanceof ScriptComponent) {
            script.set(((ScriptComponent) component).getScript());
        }

        return this;
    }

    /**
     * Calls the update method of the script if current scene is set.
     * @see Component#update()
     * @see Script#update()
     */
    @Override
    public void update()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.update();
        }
        super.update();
    }

    /**
     * Calls the deltaUpdate method of the script if current scene is set.
     * @see Component#deltaUpdate(float)
     * @see Script#deltaUpdate(float)
     */
    @Override
    public void deltaUpdate(float deltaTime)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.deltaUpdate(deltaTime);
        }
    }

    /**
     * Calls the collider2DEnter method of the script if current scene is set.
     * @see Script#collider2DEnter(GameObject)
     */
    public void collider2DEnter(GameObject otherObj)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.collider2DEnter(otherObj);
        }
    }

    /**
     * Calls the collider2DExit method of the script if current scene is set.
     * @see Script#collider2DExit(GameObject)
     */
    public void collider2DExit(GameObject otherObj)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.collider2DExit(otherObj);
        }
    }

    public Script getScript() { return script; }
}
