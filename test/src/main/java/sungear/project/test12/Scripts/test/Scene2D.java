package sungear.project.test12.Scripts.test;

import java.util.ArrayList;
import java.util.List;

public class Scene2D
{
    private List<System> systems = new ArrayList<>();

    public void update()
    {
        for(System system : systems) {
            system.update();
        }
    }

    public void deltaUpdate(float deltaTime)
    {
        for(System system : systems) {
            system.deltaUpdate(deltaTime);
        }
    }
}
