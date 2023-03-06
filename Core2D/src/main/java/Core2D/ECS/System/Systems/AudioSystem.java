package Core2D.ECS.System.Systems;

import Core2D.Audio.AudioListener;
import Core2D.Audio.OpenAL;
import Core2D.DataClasses.AudioData;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Audio.AudioComponent;
import Core2D.ECS.Component.Components.Audio.AudioType;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Utils.MatrixUtils;
import org.joml.Vector2f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

public class AudioSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        if(!active) return;

        TransformComponent transformComponent = componentsQuery.getComponent(TransformComponent.class);

        for(Component component : componentsQuery.getComponents()) {
            if (!component.active) continue;
            if(component instanceof AudioComponent audioComponent) {
                // обновление всех параметров (вроде не должно сильно нагружать систему)
                if(audioComponent.currentSecond != audioComponent.lastSecond) {
                    OpenAL.alCall((params) -> AL11.alSourcef(audioComponent.handler, AL11.AL_SEC_OFFSET, audioComponent.currentSecond));
                } else {
                    audioComponent.currentSecond = OpenAL.alCall((params) -> AL11.alGetSourcef(audioComponent.handler, AL11.AL_SEC_OFFSET), Float.class);
                }

                OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.handler, AL10.AL_MAX_DISTANCE, audioComponent.maxDistance));
                OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.handler, AL10.AL_REFERENCE_DISTANCE, audioComponent.referenceDistance));
                OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.handler, AL10.AL_ROLLOFF_FACTOR, audioComponent.rolloffFactor));
                OpenAL.alCall((params) -> AL10.alSourcei(audioComponent.handler, AL10.AL_LOOPING, audioComponent.cyclic ? AL10.AL_TRUE : AL10.AL_FALSE));
                // --------------------------

                // если аудио звучит в мировом пространстве
                if (audioComponent.type == AudioType.WORLDSPACE && transformComponent != null) {
                    Vector2f position = MatrixUtils.getPosition(transformComponent.modelMatrix);

                    OpenAL.alCall((params) -> AL10.alSource3f(audioComponent.handler, AL10.AL_POSITION, position.x, position.y, 0f));

                    float distance = position.distance(new Vector2f(AudioListener.getPosition().x, AudioListener.getPosition().y));

                    float[] gain = {Math.max(0.0f, 1.0f - distance / audioComponent.maxDistance) * (audioComponent.volumePercent / 100.0f)};

                    if (Float.isNaN(gain[0])) {
                        gain[0] = 0.0f;
                    }

                    OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.handler, AL10.AL_GAIN, gain[0]));
                } else if (audioComponent.type == AudioType.BACKGROUND) {
                    OpenAL.alCall((params) -> AL10.alSource3f(audioComponent.handler, AL10.AL_POSITION, 0f, 0f, 0f));
                    OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.handler, AL10.AL_GAIN, audioComponent.volumePercent / 100.0f));
                }

                boolean lastPlaying = audioComponent.playing;
                audioComponent.state = OpenAL.alCall((params) -> AL10.alGetSourcei(audioComponent.handler, AL10.AL_SOURCE_STATE), Integer.class);
                if (audioComponent.state == AL10.AL_PLAYING) {
                    audioComponent.playing = true;
                    audioComponent.paused = false;
                } else if (audioComponent.state == AL10.AL_STOPPED) {
                    audioComponent.playing = false;
                    audioComponent.paused = false;
                } else if (audioComponent.state == AL10.AL_PAUSED) {
                    audioComponent.playing = true;
                    audioComponent.paused = true;
                }

                // аудио перестало играть
                if (lastPlaying && !audioComponent.playing) {
                    audioComponent.currentSecond = 0.0f;
                }
            }
        }
    }

    public static AudioComponent createAudioComponent(AudioData audioData)
    {
        AudioComponent audioComponent = new AudioComponent();

        audioComponent.handler = OpenAL.alCall(params -> AL10.alGenBuffers(), Integer.class);

        OpenAL.alCall(params -> AL10.alBufferData(audioComponent.handler, audioData.getFormat(), audioData.getData(), (int) audioData.getSampleRate()));

        return audioComponent;
    }
}
