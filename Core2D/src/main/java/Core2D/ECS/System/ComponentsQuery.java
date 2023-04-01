package Core2D.ECS.System;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.ScriptComponent;

import java.util.ArrayList;
import java.util.List;

// связка компонентов, необходимых для одной задачи (например для рендера)
public class ComponentsQuery
{
    private List<Component> components = new ArrayList<>();

    // ID сущности, представителем которой является  данная связка компонентов
    public int entityID;

    public ComponentsQuery(int entityID)
    {
        this.entityID = entityID;
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for(var component : components) {
            if(componentClass.isAssignableFrom(component.getClass())) {
                return componentClass.cast(component);
            } else if(ScriptComponent.class.isAssignableFrom(component.getClass()) && componentClass.isAssignableFrom(((ScriptComponent) component).script.getScriptClass())) {
                return componentClass.cast(((ScriptComponent) component).script.getScriptClassInstance());
            }
        }

        return null;
    }

    public <T extends Component> List<T> getAllComponents(Class<T> componentClass)
    {
        List<T> componentsFound = new ArrayList<>();
        for(var component : components) {
            if(componentClass.isAssignableFrom(component.getClass())){
                componentsFound.add(componentClass.cast(component));
            } else if(ScriptComponent.class.isAssignableFrom(component.getClass()) && componentClass.isAssignableFrom(((ScriptComponent) component).script.getScriptClass())) {
                componentsFound.add(componentClass.cast(((ScriptComponent) component).script.getScriptClassInstance()));
            }
        }

        return componentsFound;
    }

    public List<Component> getComponents() { return components; }
}