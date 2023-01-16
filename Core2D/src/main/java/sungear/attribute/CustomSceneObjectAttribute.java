package sungear.attribute;

public class CustomSceneObjectAttribute extends SceneObjectAttribute {

    public CustomSceneObjectAttribute customComponent;

    @Override
    public void update() {

    }

    public CustomSceneObjectAttribute(CustomSceneObjectAttribute customComponent) {
        this.customComponent = customComponent;
    }
}
