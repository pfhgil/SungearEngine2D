package Core2D.Timer;

import java.util.ArrayList;
import java.util.List;

public class TimersManager
{
    private List<Timer> allTimers = new ArrayList<>();

    public void UpdateTimers()
    {
        for(int i = 0; i < allTimers.size(); i++) {
            allTimers.get(i).startFrame();
        }
    }

    public Timer GetTimer(String name)
    {
        for(int i = 0; i < allTimers.size(); i++) {
            if(name.equals(allTimers.get(i).getName())) {
                return allTimers.get(i);
            }
        }

        return null;
    }

    public List<Timer> getAllTimers() { return allTimers; }
}
