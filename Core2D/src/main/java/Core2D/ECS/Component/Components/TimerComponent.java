package Core2D.ECS.Component.Components;

import Core2D.ECS.Component.Component;
import Core2D.Timer.Timer;

public class TimerComponent extends Component
{
    private Timer timer = new Timer(0.0f);

    @Override
    public void update()
    {
        timer.startFrame();
    }

    public Timer getTimer() { return timer; }
}
