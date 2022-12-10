package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.GameObject.GameObject;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.Script;

import java.util.function.Consumer;

/**
 * The ScriptComponent.
 * @see Script
 */
public class ScriptComponent extends Component
{
    public Script script;

    public ScriptComponent() { this.script = new Script(); }

    public ScriptComponent(ScriptComponent component)
    {
        this.script = new Script();
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
    }

    /**
     * Calls the deltaUpdate method of the script if current scene is set.
     * @see Component#deltaUpdate(float)
     * @see Script#deltaUpdate(float)
     */
    @Override
    public void deltaUpdate(float deltaTime)
    {

    }

    /**
     * Calls the collider2DEnter method of the script if current scene is set.
     * @see Script#collider2DEnter(GameObject)
     */
    public void collider2DEnter(GameObject otherObj)
    {

    }

    /**
     * Calls the collider2DExit method of the script if current scene is set.
     * @see Script#collider2DExit(GameObject)
     */
    public void collider2DExit(GameObject otherObj)
    {

    }

    // сделано для избежания рекурсивных вызовов дефолтных методов (update, deltaUpdate, collider2DEnter, collider2DExit).
    // они могут ссылаться сами на себя, если в ребенке, наследующем данный класс не реализованы какие-либо из этих четырек методов
    public void callMethod(Consumer<Object[]> func)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts) {
            func.accept(null);
        }
    }
}
