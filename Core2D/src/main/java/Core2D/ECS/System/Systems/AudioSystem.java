package Core2D.ECS.System.Systems;

import Core2D.Audio.AudioListener;
import Core2D.Audio.OpenAL;
import Core2D.DataClasses.AudioData;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Audio.AudioComponent;
import Core2D.ECS.Component.Components.Audio.AudioState;
import Core2D.ECS.Component.Components.Audio.AudioType;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Log.Log;
import Core2D.Utils.MatrixUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

public class AudioSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        TransformComponent transformComponent = componentsQuery.getComponent(TransformComponent.class);

        for(Component component : componentsQuery.getComponents()) {
            if (!component.active) continue;
            if(component instanceof AudioComponent audioComponent) {
                // обновление всех параметров (вроде не должно сильно нагружать систему)
                if(audioComponent.currentSecond != audioComponent.lastSecond) {
                    OpenAL.alCall((params) -> AL11.alSourcef(audioComponent.sourceHandler, AL11.AL_SEC_OFFSET, audioComponent.currentSecond));
                } else {
                    audioComponent.currentSecond = OpenAL.alCall((params) -> AL11.alGetSourcef(audioComponent.sourceHandler, AL11.AL_SEC_OFFSET), Float.class);
                    audioComponent.lastSecond = audioComponent.currentSecond;
                }

                OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.sourceHandler, AL10.AL_MAX_DISTANCE, audioComponent.maxDistance));
                OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.sourceHandler, AL10.AL_REFERENCE_DISTANCE, audioComponent.referenceDistance));
                OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.sourceHandler, AL10.AL_ROLLOFF_FACTOR, audioComponent.rolloffFactor));
                OpenAL.alCall((params) -> AL10.alSourcei(audioComponent.sourceHandler, AL10.AL_LOOPING, audioComponent.cyclic ? AL10.AL_TRUE : AL10.AL_FALSE));
                OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.sourceHandler, AL10.AL_PITCH, audioComponent.pitch));
                // --------------------------

                // если аудио звучит в мировом пространстве
                if (audioComponent.type == AudioType.WORLDSPACE && transformComponent != null) {
                    Vector3f position = MatrixUtils.getPosition(transformComponent.modelMatrix);

                    OpenAL.alCall((params) -> AL10.alSource3f(audioComponent.sourceHandler, AL10.AL_POSITION, position.x, position.y, position.z));

                    float distance = position.distance(AudioListener.getPosition());

                    float[] gain = {Math.max(0.0f, 1.0f - distance / audioComponent.maxDistance) * (audioComponent.volumePercent / 100.0f)};

                    if (Float.isNaN(gain[0])) {
                        gain[0] = 0.0f;
                    }

                    OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.sourceHandler, AL10.AL_GAIN, gain[0]));
                } else if (audioComponent.type == AudioType.BACKGROUND) {
                    OpenAL.alCall((params) -> AL10.alSource3f(audioComponent.sourceHandler, AL10.AL_POSITION, 0f, 0f, 0f));
                    OpenAL.alCall((params) -> AL10.alSourcef(audioComponent.sourceHandler, AL10.AL_GAIN, audioComponent.volumePercent / 100.0f));
                }

                //Log.CurrentSession.println("why the fuck is he: " + audioComponent, Log.MessageType.SUCCESS, true);

                if(audioComponent.lastState != audioComponent.state) {
                    audioComponent.lastState = audioComponent.state;

                    Log.CurrentSession.println("vlad zhoshe: " + audioComponent.state.toString(), Log.MessageType.SUCCESS, true);

                    switch (audioComponent.state) {
                        case PLAYING -> OpenAL.alCall((params) -> AL10.alSourcePlay(audioComponent.sourceHandler));
                        case PAUSED -> OpenAL.alCall((params) -> AL10.alSourcePause(audioComponent.sourceHandler));
                        case STOPPED -> OpenAL.alCall((params) -> AL10.alSourceStop(audioComponent.sourceHandler));
                    }
                }

                //boolean lastPlaying = audioComponent.playing;
                int openALAudioState = OpenAL.alCall((params) -> AL10.alGetSourcei(audioComponent.sourceHandler, AL10.AL_SOURCE_STATE), Integer.class);
                if (openALAudioState == AL10.AL_PLAYING) {
                    audioComponent.state = AudioState.PLAYING;
                } else if (openALAudioState == AL10.AL_STOPPED) {
                    audioComponent.lastSecond = audioComponent.currentSecond = 0.0f;
                    audioComponent.state = AudioState.STOPPED;
                } else if (openALAudioState== AL10.AL_PAUSED) {
                    audioComponent.state = AudioState.PAUSED;
                }
            }
        }
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime) {

    }

    public AudioComponent createAudioComponent(AudioData audioData)
    {
        AudioComponent audioComponent = new AudioComponent();

        audioComponent.sourceHandler = OpenAL.alCall(params -> AL10.alGenSources(), Integer.class);

        setAudioComponentData(audioComponent, audioData);

        return audioComponent;
    }

    public void setAudioComponentData(AudioComponent audioComponent, AudioData audioData)
    {
        if(!OpenAL.alCall(params -> AL10.alIsSource(audioComponent.sourceHandler), Boolean.class)) {
            audioComponent.sourceHandler = OpenAL.alCall(params -> AL10.alGenSources(), Integer.class);
        }

        OpenAL.alCall((params) -> AL10.alSourcei(audioComponent.sourceHandler, AL10.AL_BUFFER, audioData.getBufferHandler()));

        audioComponent.audioLengthInSeconds = audioData.getAudioLengthInSeconds();
    }
}
