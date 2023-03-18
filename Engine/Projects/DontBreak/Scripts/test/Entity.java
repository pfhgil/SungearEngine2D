package sungear.project.test12.Scripts.test;

import java.util.ArrayList;
import java.util.List;

public class Entity
{
    private List<Component> components = new ArrayList<>();

    public boolean active = true;

    public Layer layer;

    public void addComponent(Component component)
    {
        component.entity = this;
        component.init();
        components.add(component);


    }
}
