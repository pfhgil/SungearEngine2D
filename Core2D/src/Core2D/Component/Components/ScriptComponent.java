package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.Script;
import Core2D.Utils.ExceptionsUtils;

public class ScriptComponent extends Component implements AutoCloseable
{
    private Script script;

    public ScriptComponent() { this.script = new Script(); }

    @Override
    public void set(Component component)
    {
        if(component instanceof ScriptComponent) {
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
        //script.destroy();
        //script = null;

        object2D = null;

        try {
            close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    public Script getScript() { return script; }

    @Override
    public void close() throws Exception {

    }
}
