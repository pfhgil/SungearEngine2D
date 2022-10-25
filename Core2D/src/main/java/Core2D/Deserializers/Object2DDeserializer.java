package Core2D.Deserializers;

import Core2D.Component.Component;
import Core2D.Component.Components.Rigidbody2DComponent;
import Core2D.Component.Components.ScriptComponent;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.Drawable.Object2D;
import Core2D.Project.ProjectsManager;
import Core2D.Texture2D.Texture2D;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Tag;
import com.google.gson.*;
import org.joml.Vector4f;

import java.io.File;
import java.lang.reflect.Type;

// TODO: исправить загрузку ресурсов по пути
public class Object2DDeserializer implements JsonDeserializer<Object2D>
{
    @Override
    public Object2D deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Object2D object2D = new Object2D();

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        Tag tag = context.deserialize(jsonObject.get("tag"), Tag.class);
        Vector4f color = context.deserialize(jsonObject.get("color"), Vector4f.class);
        int drawingMode = jsonObject.get("drawingMode").getAsInt();
        boolean isUIElement = context.deserialize(jsonObject.get("isUIElement"), boolean.class);
        boolean active = context.deserialize(jsonObject.get("active"), boolean.class);
        JsonArray components = jsonObject.getAsJsonArray("components");
        int ID = jsonObject.get("ID").getAsInt();
        JsonElement layerNameJElement = jsonObject.get("layerName");
        String layerName = "default";
        if(layerNameJElement != null) {
            layerName = layerNameJElement.getAsString();
        }
        JsonArray childrenObjectsIDJArray = jsonObject.getAsJsonArray("childrenObjectsID");
        if(childrenObjectsIDJArray != null) {
            for(JsonElement element : childrenObjectsIDJArray) {
                object2D.getChildrenObjectsID().add(element.getAsInt());
            }
        }

        object2D.removeComponent(object2D.getComponent(TextureComponent.class));

        object2D.setName(name);
        object2D.setTag(tag.getName());
        tag.destroy();
        object2D.setColor(color);
        object2D.setDrawingMode(drawingMode);
        object2D.setUIElement(isUIElement);
        object2D.setActive(active);
        object2D.setID(ID);
        object2D.setLayerName(layerName);

        for(JsonElement element : components) {
            Component component = context.deserialize(element, Component.class);
            if(component instanceof TransformComponent) {
                object2D.getComponent(TransformComponent.class).set(component);
            } else if(component instanceof TextureComponent) {
                TextureComponent textureComponent = (TextureComponent) component;
                Texture2D texture2D = null;
                // если режим работы ядра в движке
                if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                    String textureFileName = new File(textureComponent.getTexture2D().source).getName();
                    String parentFileName = new File(textureComponent.getTexture2D().source).getParentFile().getName();
                    // найти текущий путь до текстуры
                    File textureFile = FileUtils.findFile(new File(ProjectsManager.getCurrentProject().getProjectPath()), parentFileName, textureFileName);

                    texture2D = new Texture2D(
                            textureFile.getPath(),
                            textureComponent.getTexture2D().param,
                            textureComponent.getTexture2D().getGLTextureBlock()
                    );
                // если режим работы в билде
                } else {
                    texture2D = new Texture2D(
                            Core2D.class.getResourceAsStream(textureComponent.getTexture2D().source),
                            textureComponent.getTexture2D().param,
                            textureComponent.getTexture2D().getGLTextureBlock()
                    );
                }

                texture2D.blendSourceFactor = textureComponent.getTexture2D().blendSourceFactor;
                texture2D.blendDestinationFactor = textureComponent.getTexture2D().blendDestinationFactor;

                object2D.addComponent(component);
                object2D.getComponent(TextureComponent.class).set(component);
                object2D.getComponent(TextureComponent.class).setTexture2D(texture2D);
            } else if(component instanceof Rigidbody2DComponent) {
                Rigidbody2DComponent rigidbody2DComponent = new Rigidbody2DComponent();
                object2D.addComponent(rigidbody2DComponent);
                rigidbody2DComponent.set(component);
            } else if(component instanceof ScriptComponent) {
                ScriptComponent scriptComponent = (ScriptComponent) component;
                if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                    String scriptFileName = new File(scriptComponent.getScript().getPath()).getName();
                    String parentFileName = new File(scriptComponent.getScript().getPath()).getParentFile().getName();
                    File scriptFile = FileUtils.findFile(new File(ProjectsManager.getCurrentProject().getProjectPath()), parentFileName, scriptFileName);

                    if (scriptFile != null) {
                        scriptComponent.getScript().setPath(scriptFile.getPath() + ".java");

                        ScriptComponent sc = new ScriptComponent();
                        object2D.addComponent(sc);
                        sc.set(scriptComponent);
                    }
                } else {
                    ScriptComponent sc = new ScriptComponent();
                    object2D.addComponent(sc);
                    sc.set(scriptComponent);
                }
            } else {
                object2D.addComponent(component);
            }
        }

        return object2D;
    }
}