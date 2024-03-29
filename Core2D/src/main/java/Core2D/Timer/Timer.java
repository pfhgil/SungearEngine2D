package Core2D.Timer;

import java.util.ArrayList;
import java.util.List;

public class Timer
{
    private String name = "timer";

    private long startTime = 0;
    private long lastTime = 0;

    private long difference = 0;

    private float deltaTime = 0;
    // сумма всех delta time`ов
    private float deltaTimesSum = 0;
    // всего delta time`ов
    private int deltaTimesNum = 0;
    // среднее delta time
    private float averageDeltaTime = 0;

    private long framesPerDestTime = 0;

    private long FPS = 0;

    private float destTime = 0;

    private boolean active = false;

    private float maxDelta;

    private boolean cyclic = false;

    private List<TimerCallback> timerCallbacks = new ArrayList<>();

    private boolean firstTime = true;

    public Timer(float destTime)
    {
        this.destTime = destTime;
    }

    public Timer(float destTime, boolean cyclic)
    {
        this.destTime = destTime;
        this.cyclic = cyclic;
    }

    public Timer(TimerCallback timerCallback, float destTime)
    {
        this.timerCallbacks.add(timerCallback);
        this.destTime = destTime;
    }

    public Timer(TimerCallback timerCallback, float destTime, boolean cyclic)
    {
        this.timerCallbacks.add(timerCallback);
        this.destTime = destTime;
        this.cyclic = cyclic;
    }

    public void start()
    {
        active = true;
        startTime = System.nanoTime();
    }

    public void stop()
    {
        active = false;
    }

    public void startFrame()
    {
        if(active) {
            if(firstTime) {
                startTime = System.nanoTime();
                firstTime = false;
            }

            difference = (System.nanoTime() - startTime);

            long currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1000.0f / 1000.0f / 1000.0f;
            lastTime = currentTime;

            framesPerDestTime++;

            if(difference / 1000.0f / 1000.0f / 1000.0f >= destTime) {
                start();

                if(timerCallbacks != null) {
                    for(TimerCallback timerCallback : timerCallbacks) {
                        timerCallback.update();
                    }
                }

                FPS = framesPerDestTime;
                framesPerDestTime = 0;

                if(!cyclic) {
                    active = false;
                }
            }

            deltaUpdate();
        }
    }

    private void deltaUpdate()
    {
        if(maxDelta != 0.0f) {
            // ограничение frameTime
            deltaTime = Math.min(deltaTime, maxDelta);
        }

        deltaTimesSum += deltaTime;
        deltaTimesNum++;

        averageDeltaTime = deltaTimesSum / deltaTimesNum;

        if(timerCallbacks != null) {
            for(TimerCallback timerCallback : timerCallbacks) {
                timerCallback.deltaUpdate(deltaTime);
            }
        }
    }

    public static float getTimeInSeconds() {
        return (float) System.nanoTime() / (float) 1000000000L;
    }
    public static long getTimeInMilliseconds() {
        return System.currentTimeMillis();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isActive() { return active; }

    public float getMaxDelta() { return maxDelta; }
    public void setMaxDelta(float maxDelta) { this.maxDelta = maxDelta; }

    public float getDeltaTime() { return deltaTime; }

    public float getAverageDeltaTime() { return averageDeltaTime; }

    public long getFPS() { return FPS; }

    public boolean isCyclic() { return cyclic; }
    public void setCyclic(boolean cyclic) { this.cyclic = cyclic; }

    public List<TimerCallback> getTimerCallbacks() { return timerCallbacks; }
}
