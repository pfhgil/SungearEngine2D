package Core2D.ECS.Component.Components.Audio;

public enum AudioType
{
    BACKGROUND,
    WORLDSPACE;

    @Override
    public String toString() {
        return switch(this) {
            case BACKGROUND -> "Background";
            case WORLDSPACE -> "Worldspace";
        };
    }
}
