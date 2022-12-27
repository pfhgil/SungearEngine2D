package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.Script;

import java.util.function.Consumer;

public class ScriptableSystem extends System
{
    public Script script = new Script();

    public ScriptableSystem() { }

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
     * @see Script#collider2DEnter(Entity)
     */
    public void collider2DEnter(Entity otherObj)
    {

    }

    /**
     * Calls the collider2DExit method of the script if current scene is set.
     * @see Script#collider2DExit(Entity)
     */
    public void collider2DExit(Entity otherObj)
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
