package Core2D.Tasks;

/**
 * Tasks that will be displayed in the engine.
 * Use text variable to describe the progressbar,
 * destination variable for the final goal of the progressbar, and current variable for the current progress.
 */
public abstract class Task extends Thread
{
    public String text = "";
    public float destination = 0.0f;
    public float current = 0.0f;

    public Task()
    {

    }

    public Task (String text, float destination, float current)
    {
        this.text = text;
        this.destination = destination;
        this.current = current;
    }

    @Override
    public void run()
    {

    }
}