package Core2D.Audio;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Audio.AudioComponent;
import Core2D.ECS.Entity;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import org.lwjgl.openal.AL10;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AudioManager
{
    private static List<Integer> audioSources = new ArrayList<>();

    public static void stopAllSources()
    {
        int length = audioSources.size();
        for(int i = 0; i < length; i++) {
            int id = audioSources.get(i);
            OpenAL.alCall((params) -> AL10.alSourceStop(id));
        }
    }

    public static void destroyAllSources()
    {
        if(audioSources.size() != 0) {
            Iterator<Integer> iter = audioSources.iterator();
            while(iter.hasNext()) {
                int id = iter.next();;
                OpenAL.alCall((params) -> AL10.alDeleteSources(id));
                iter.remove();
            }
        }
    }

    public static void destroyCurrentScene2DAllSources()
    {
        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            destroyScene2DAllSources(SceneManager.currentSceneManager.getCurrentScene2D());
        }
    }

    public static void destroyScene2DAllSources(Scene2D scene2D)
    {
        for(Layer layer : scene2D.getLayering().getLayers()) {
            int gameObjectsNum = layer.getEntities().size();
            for(int i = 0; i < gameObjectsNum; i++) {
                Entity entity = layer.getEntities().get(i);
                int componentsNum = entity.getComponents().size();
                for (int k = 0; k < componentsNum; k++) {
                    Component component = entity.getComponents().get(k);
                    if (component instanceof AudioComponent audioComponent) {
                        Log.Console.println("destroyed source: " + audioComponent.audio.source);
                        audioComponent.audio.destroy();
                    }
                }
            }
        }
    }

    public static List<Integer> getAudioSources() { return audioSources; }
}
