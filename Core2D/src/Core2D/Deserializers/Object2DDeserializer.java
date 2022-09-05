package Core2D.Deserializers;

import Core2D.Component.Component;
import Core2D.Component.Components.Rigidbody2DComponent;
import Core2D.Component.Components.ScriptComponent;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.Object2D.Object2D;
import Core2D.Project.ProjectsManager;
import Core2D.Texture2D.Texture2D;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Tag;
import com.google.gson.*;
import org.joml.Vector4f;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// TODO: исправить загрузку ресурсов по пути
public class Object2DDeserializer implements JsonDeserializer<Object2D>
{
    @Override
    public Object2D deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
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
        List<Integer> childrenObjectsID = new ArrayList<>();
        if(childrenObjectsIDJArray != null) {
            for(JsonElement element : childrenObjectsIDJArray) {
                childrenObjectsID.add(context.deserialize(element, int.class));
            }
        }

        Object2D object2D = new Object2D();
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
        object2D.setChildrenObjectsID(childrenObjectsID);

        for(JsonElement element : components) {
            Component component = context.deserialize(element, Component.class);
            if(component instanceof TransformComponent) {
                object2D.getComponent(TransformComponent.class).set(component);
            } else if(component instanceof TextureComponent) {
                TextureComponent textureComponent = (TextureComponent) component;
                Texture2D texture2D = null;
                if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                    String textureFileName = new File(textureComponent.getTexture2D().getSource()).getName();
                    String parentFileName = new File(textureComponent.getTexture2D().getSource()).getParentFile().getName();
                    File textureFile = FileUtils.findFile(new File(ProjectsManager.getCurrentProject().getProjectPath()), parentFileName, textureFileName);

                    //System.out.println("texture: " + textureFile.getPath());

                    texture2D = new Texture2D(
                            textureFile.getPath(),
                            textureComponent.getTexture2D().getParam(),
                            textureComponent.getTexture2D().getGLTextureBlock()
                    );
                } else {
                    texture2D = new Texture2D(
                            textureComponent.getTexture2D().getSource(),
                            textureComponent.getTexture2D().getParam(),
                            textureComponent.getTexture2D().getGLTextureBlock()
                    );
                }

                object2D.addComponent(component);
                object2D.getComponent(TextureComponent.class).set(component);
                object2D.getComponent(TextureComponent.class).setTexture2D(texture2D);
            } else if(component instanceof Rigidbody2DComponent) {
                Rigidbody2DComponent rigidbody2DComponent = new Rigidbody2DComponent();
                object2D.addComponent(rigidbody2DComponent);
                rigidbody2DComponent.set(component);
            } else if(component instanceof ScriptComponent) {
                ScriptComponent scriptComponent = (ScriptComponent) component;
                String scriptFileName = new File(scriptComponent.getScript().getPath()).getName();
                String parentFileName = new File(scriptComponent.getScript().getPath()).getParentFile().getName();
                File scriptFile = FileUtils.findFile(new File(ProjectsManager.getCurrentProject().getProjectPath()), parentFileName, scriptFileName);

                if(scriptFile != null) {
                    System.out.println(scriptFile.getPath());
                    scriptComponent.getScript().setPath(scriptFile.getPath() + ".java");
                    //File scriptFile = new File(scriptComponent.getScript().getPath() + ".java");

                    ScriptComponent sc = new ScriptComponent();
                    object2D.addComponent(sc);
                    sc.set(scriptComponent);
                }
            } else {
                object2D.addComponent(component);
            }

            component = null;
        }

        return object2D;
    }
}
