package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Drawable.Object2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.Script;

public class ScriptComponent extends Component
{
    private Script script;

    public ScriptComponent() { this.script = new Script(); }

    @Override
    public void set(Component component)
    {
        if(component instanceof ScriptComponent) {
            script.set(((ScriptComponent) component).getScript());
        }
    }

    @Override
    public void update()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.update();
        }
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.deltaUpdate(deltaTime);
        }
    }

    public void collider2DEnter(Object2D otherObj)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.collider2DEnter(otherObj);
        }
    }

    public void collider2DExit(Object2D otherObj)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.collider2DExit(otherObj);
        }
    }

    public Script getScript() { return script; }
}
