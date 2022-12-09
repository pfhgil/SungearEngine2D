package Core2D.Component.Components;

import Core2D.Component.Component;
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
