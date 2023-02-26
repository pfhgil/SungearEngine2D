package Core2D.ECS.System;

import Core2D.ECS.Component.Component;

import java.util.ArrayList;
import java.util.List;

// связка компонентов, необходимых для одной задачи
public class ComponentsQuery
{
    private List<Component> components = new ArrayList<>();

    public int entityID;

    public List<Component> getComponents() { return components; }
}
