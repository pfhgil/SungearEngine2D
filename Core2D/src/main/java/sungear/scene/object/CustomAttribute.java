package sungear.scene.object;

public class CustomAttribute extends Attribute {

    public CustomAttribute customComponent;

    @Override
    public void update() {

    }

    public CustomAttribute(CustomAttribute customComponent) {
        this.customComponent = customComponent;
    }
}
