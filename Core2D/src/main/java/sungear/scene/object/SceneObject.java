package sungear.scene.object;

import java.util.ArrayList;
import java.util.List;

public class SceneObject {
    private List<Attribute> attributes;

    public void addAttribute(Attribute attribute) {
        if (this.attributes == null) {
            this.attributes = new ArrayList<>();
        }
        this.attributes.add(attribute);
    }

    /**
     * TODO: find out more strict way to inject components into SceneObject
     * @param clazz
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T extends Attribute> T getAttribute(Class<T> clazz) {
        if (this.attributes != null) {
            for (Attribute attribute : attributes) {
                if (clazz.isInstance(attribute)) {
                    return (T) attribute;
                }
            }
        }
        return null;
    }

}
