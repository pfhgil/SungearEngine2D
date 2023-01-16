package sungear.scene;

import sungear.attribute.SceneObjectAttribute;

import java.util.ArrayList;
import java.util.List;

public class SceneObject {
    private List<SceneObjectAttribute> sceneObjectAttributes;

    public void addComponent(SceneObjectAttribute sceneObjectAttribute) {
        if (this.sceneObjectAttributes == null) {
            this.sceneObjectAttributes = new ArrayList<>();
        }
        this.sceneObjectAttributes.add(sceneObjectAttribute);
    }

    /**
     * TODO: find out more strict way to inject components into SceneObject
     * @param clazz
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T extends SceneObjectAttribute> T getComponent(Class<T> clazz) {
        if (this.sceneObjectAttributes != null) {
            for (SceneObjectAttribute sceneObjectAttribute : sceneObjectAttributes) {
                if (clazz.isInstance(sceneObjectAttribute)) {
                    return (T) sceneObjectAttribute;
                }
            }
        }
        return null;
    }

}
