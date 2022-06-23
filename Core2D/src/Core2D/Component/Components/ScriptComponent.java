package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Object2D.Object2D;
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
            System.out.println(getObject2D().getName());
            //script.setPath(((ScriptComponent) component).getScript().getPath());
            //script.setName(((ScriptComponent) component).getScript().getName());
            script.set(((ScriptComponent) component).getScript());

            component = null;
        }
    }

    @Override
    public void update()
    {
        if(SceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.update();
        }
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        if(SceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.deltaUpdate(deltaTime);
        }
    }

    public void collider2DEnter(Object2D otherObj)
    {
        if(SceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.collider2DEnter(otherObj);
        }
    }

    public void collider2DExit(Object2D otherObj)
    {
        if(SceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            script.collider2DExit(otherObj);
        }
    }

    @Override
    public void destroy()
    {
        script.destroy();
        script = null;
    }

    public Script getScript() { return script; }
}
