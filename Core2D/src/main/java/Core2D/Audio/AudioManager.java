package Core2D.Audio;

import Core2D.Component.Component;
import Core2D.Component.Components.AudioComponent;
import Core2D.GameObject.GameObject;
import Core2D.Layering.Layer;
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
            OpenAL.alCall((params) -> AL10.alSourceStop(id), id);
        }
    }

    public static void destroyAllSources()
    {
        if(audioSources.size() != 0) {
            Iterator<Integer> iter = audioSources.iterator();
            while(iter.hasNext()) {
                int id = iter.next();;
                OpenAL.alCall((params) -> AL10.alDeleteSources(id), id);
                iter.remove();
            }
        }
    }

    public static void destroyCurrentScene2DAllSources()
    {
        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for(Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                int renderingObjectsNum = layer.getGameObjects().size();
                for(int i = 0; i < renderingObjectsNum; i++) {
                    GameObject gameObject = layer.getGameObjects().get(i);
                    int componentsNum = gameObject.getComponents().size();
                    for (int k = 0; k < componentsNum; k++) {
                        Component component = gameObject.getComponents().get(k);
                        if (component instanceof AudioComponent audioComponent) {
                            System.out.println("destroyed source: " + audioComponent.audio.source);
                            audioComponent.audio.destroy();
                        }
                    }
                }
            }
        }
    }

    public static List<Integer> getAudioSources() { return audioSources; }
}
