package Core2D.Deserializers;

import Core2D.AssetManager.AssetManager;
import Core2D.Component.Component;
import Core2D.Component.Components.*;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.GameObject.GameObject;
import Core2D.Project.ProjectsManager;
import Core2D.GameObject.RenderParts.Texture2D;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Tag;
import com.google.gson.*;
import org.joml.Vector4f;

import java.io.File;
import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject>
{
    @Override
    public GameObject deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        GameObject gameObject = new GameObject();

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        Tag tag = context.deserialize(jsonObject.get("tag"), Tag.class);
        Vector4f color = context.deserialize(jsonObject.get("color"), Vector4f.class);
        //int drawingMode = jsonObject.get("drawingMode").getAsInt();
        //boolean isUIElement = context.deserialize(jsonObject.get("isUIElement"), boolean.class);
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
                gameObject.getChildrenObjectsID().add(element.getAsInt());
            }
        }

        gameObject.name = name;
        gameObject.tag.set(tag);
        tag.destroy();
        gameObject.setColor(color);
        gameObject.active = active;
        gameObject.ID = ID;
        gameObject.layerName = layerName;

        System.out.println("\u001B[32m size of game object " + gameObject.name + " list of components: " + gameObject.getComponents().size() + " \u001B[32m");

        Rigidbody2DComponent rigidbody2DComponent = null;
        for(JsonElement element : components) {
            Component component = context.deserialize(element, Component.class);

            int lastComponentID = component.componentID;
            if(component instanceof TransformComponent) {
                gameObject.addComponent(component);
            } else if(component instanceof MeshRendererComponent) {
                MeshRendererComponent textureComponent = (MeshRendererComponent) component;
                Texture2D texture2D = new Texture2D();
                // если режим работы ядра в движке
                if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                    String textureFullPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + textureComponent.texture.path;

                    if(new File(textureFullPath).exists()) {
                        texture2D = new Texture2D(
                                AssetManager.getInstance().getTexture2DData(textureComponent.texture.path),
                                textureComponent.texture.getGLTextureBlock()
                        );
                        texture2D.path = textureComponent.texture.path;
                    } else { // для исправления текущих сцен, т.к. у их ресурсов стоит полный путь.
                        // чтобы это исправить загружаем по этому пути текстуру, находим относительный путь и присваиваем его source для того,
                        // чтобы в следующий раз выполнился блок кода выше
                        if(new File(textureComponent.texture.path).exists()) {
                            String relativePath = FileUtils.getRelativePath(
                                    new File(textureComponent.texture.path),
                                    new File(ProjectsManager.getCurrentProject().getProjectPath())
                            );
                            texture2D = new Texture2D(
                                    AssetManager.getInstance().getTexture2DData(relativePath),
                                    textureComponent.texture.getGLTextureBlock()
                            );
                            texture2D.path = relativePath;
                        }
                    }
                // если режим работы в билде
                } else {
                    texture2D = new Texture2D(
                            AssetManager.getInstance().getTexture2DData(textureComponent.texture.path),
                            textureComponent.texture.getGLTextureBlock()
                    );
                }

                //texture2D.blendSourceFactor = textureComponent.texture.blendSourceFactor;
                //texture2D.blendDestinationFactor = textureComponent.texture.blendDestinationFactor;

                gameObject.addComponent(component);
                gameObject.getComponent(MeshRendererComponent.class).set(component);
                gameObject.getComponent(MeshRendererComponent.class).texture.set(texture2D);
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
                        gameObject.addComponent(sc);
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
                            gameObject.addComponent(sc);
                            scriptComponent.getScript().path += ".java";
                            sc.set(scriptComponent);
                            relativePath = relativePath.replace(".java", "");
                            sc.getScript().path = relativePath;
                        }
                    }
                } else {
                    ScriptComponent sc = new ScriptComponent();
                    gameObject.addComponent(sc);
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

                gameObject.addComponent(audioComponent);
            } else {
                gameObject.addComponent(component);
            }

            component.componentID = lastComponentID;
            //object2D.getComponents().get(object2D.getComponents().size() - 1).componentID = lastComponentID;
        }

        // в самом конце добавляю rigidbody2d, чтобы не было путаницы с порядком десериализации колладейров и rigidbody2d
        if(rigidbody2DComponent != null) {
            int lastComponentID = rigidbody2DComponent.componentID;
            gameObject.addComponent(rigidbody2DComponent);
            rigidbody2DComponent.componentID = lastComponentID;
            //object2D.getComponents().get(object2D.getComponents().size() - 1).componentID = lastComponentID;
            rigidbody2DComponent.set(rigidbody2DComponent);
        }

        return gameObject;
    }
}
