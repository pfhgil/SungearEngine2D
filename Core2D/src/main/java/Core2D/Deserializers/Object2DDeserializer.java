package Core2D.Deserializers;

import Core2D.Audio.Audio;
import Core2D.Audio.AudioInfo;
import Core2D.Component.Component;
import Core2D.Component.Components.*;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.Drawable.Object2D;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Texture2D.Texture2D;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Tag;
import com.google.gson.*;
import org.joml.Vector4f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

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

        Rigidbody2DComponent rigidbody2DComponent = null;
        for(JsonElement element : components) {
            Component component = context.deserialize(element, Component.class);

            int lastComponentID = component.componentID;
            if(component instanceof TransformComponent) {
                object2D.getComponent(TransformComponent.class).set(component);
            } else if(component instanceof TextureComponent) {
                TextureComponent textureComponent = (TextureComponent) component;
                Texture2D texture2D = null;
                // если режим работы ядра в движке
                if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                    String textureFullPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + textureComponent.getTexture2D().path;
                    String textureLastPath = textureComponent.getTexture2D().path;

                    if(new File(textureFullPath).exists()) {
                        texture2D = new Texture2D(
                                textureFullPath,
                                textureComponent.getTexture2D().param,
                                textureComponent.getTexture2D().getGLTextureBlock()
                        );
                        texture2D.path = textureLastPath;
                    } else { // для исправления текущих сцен, т.к. у их ресурсов стоит полный путь.
                        // чтобы это исправить загружаем по этому пути текстуру, находим относительный путь и присваиваем его source для того,
                        // чтобы в следующий раз выполнился блок кода выше
                        if(new File(textureComponent.getTexture2D().path).exists()) {
                            String relativePath = FileUtils.getRelativePath(
                                    new File(textureComponent.getTexture2D().path),
                                    new File(ProjectsManager.getCurrentProject().getProjectPath())
                            );
                            texture2D = new Texture2D(
                                    textureComponent.getTexture2D().path,
                                    textureComponent.getTexture2D().param,
                                    textureComponent.getTexture2D().getGLTextureBlock()
                            );
                            texture2D.path = relativePath;
                        }
                    }
                // если режим работы в билде
                } else {
                    texture2D = new Texture2D(
                            Core2D.class.getResourceAsStream(textureComponent.getTexture2D().path),
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
                rigidbody2DComponent = (Rigidbody2DComponent) component;
            } else if(component instanceof ScriptComponent) {
                ScriptComponent scriptComponent = (ScriptComponent) component;
                if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                    scriptComponent.getScript().path = scriptComponent.getScript().path.replaceAll(".java", "");

                    String fullScriptPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + scriptComponent.getScript().path + ".java";
                    String lastScriptPath = scriptComponent.getScript().path;

                    if(new File(fullScriptPath).exists()) {
                        ScriptComponent sc = new ScriptComponent();
                        scriptComponent.getScript().path = fullScriptPath;
                        object2D.addComponent(sc);
                        sc.set(scriptComponent);
                        sc.getScript().path = lastScriptPath;
                    } else {// для исправления текущих сцен, т.к. у их ресурсов стоит полный путь.
                        // чтобы это исправить загружаем по этому пути скрипт, находим относительный путь и присваиваем его path для того,
                        // чтобы в следующий раз выполнился блок кода вышe
                        if(new File(scriptComponent.getScript().path + ".java").exists()) {
                            String relativePath = FileUtils.getRelativePath(
                                    new File(scriptComponent.getScript().path + ".java"),
                                    new File(ProjectsManager.getCurrentProject().getProjectPath())
                            );
                            ScriptComponent sc = new ScriptComponent();
                            object2D.addComponent(sc);
                            scriptComponent.getScript().path += ".java";
                            sc.set(scriptComponent);
                            relativePath = relativePath.replace(".java", "");
                            sc.getScript().path = relativePath;
                        }
                    }
                } else {
                    ScriptComponent sc = new ScriptComponent();
                    object2D.addComponent(sc);
                    sc.set(scriptComponent);
                }
            } else if(component instanceof AudioComponent) {
                AudioComponent audioComponent = (AudioComponent) component;
                // если режим работы ядра в движке
                if (Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                    String audioFullPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + audioComponent.audio.path;
                    String audioLastPath = audioComponent.audio.path;

                    if (new File(audioFullPath).exists()) {
                        audioComponent.audio.loadAndSetup(audioFullPath);
                        audioComponent.audio.path = audioLastPath;
                    } else { // для исправления текущих сцен, т.к. у их ресурсов стоит полный путь.
                        // чтобы это исправить загружаем по этому пути текстуру, находим относительный путь и присваиваем его source для того,
                        // чтобы в следующий раз выполнился блок кода выше
                        if (new File(audioComponent.audio.path).exists()) {
                            String relativePath = FileUtils.getRelativePath(
                                    new File(audioComponent.audio.path),
                                    new File(ProjectsManager.getCurrentProject().getProjectPath())
                            );

                            audioComponent.audio.loadAndSetup(audioComponent.audio.path);
                            audioComponent.audio.path = relativePath;
                        }
                    }
                    // если режим работы в билде
                } else {
                    audioComponent.audio.loadAndSetup(Core2D.class.getResourceAsStream(audioComponent.audio.path));
                }

                object2D.addComponent(audioComponent);
            } else {
                object2D.addComponent(component);
            }

            component.componentID = lastComponentID;
            //object2D.getComponents().get(object2D.getComponents().size() - 1).componentID = lastComponentID;
        }

        // в самом конце добавляю rigidbody2d, чтобы не было путаницы с порядком десериализации колладейров и rigidbody2d
        if(rigidbody2DComponent != null) {
            int lastComponentID = rigidbody2DComponent.componentID;
            object2D.addComponent(rigidbody2DComponent);
            rigidbody2DComponent.componentID = lastComponentID;
            //object2D.getComponents().get(object2D.getComponents().size() - 1).componentID = lastComponentID;
            rigidbody2DComponent.set(rigidbody2DComponent);
        }

        return object2D;
    }
}
