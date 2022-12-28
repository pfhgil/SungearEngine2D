package Core2D.Deserializers;

import Core2D.AssetManager.AssetManager;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.Project.ProjectsManager;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Tag;
import com.google.gson.*;
import org.joml.Vector4f;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class EntityDeserializer implements JsonDeserializer<Entity>
{
    @Override
    public Entity deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Entity entity = new Entity();

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        Tag tag = context.deserialize(jsonObject.get("tag"), Tag.class);
        Vector4f color = context.deserialize(jsonObject.get("color"), Vector4f.class);
        //int drawingMode = jsonObject.get("drawingMode").getAsInt();
        //boolean isUIElement = context.deserialize(jsonObject.get("isUIElement"), boolean.class);
        boolean active = context.deserialize(jsonObject.get("active"), boolean.class);
        JsonArray components = jsonObject.getAsJsonArray("components");
        JsonArray systems = jsonObject.getAsJsonArray("systems");
        int ID = jsonObject.get("ID").getAsInt();
        JsonElement layerNameJElement = jsonObject.get("layerName");
        String layerName = "default";
        if(layerNameJElement != null) {
            layerName = layerNameJElement.getAsString();
        }
        JsonArray childrenObjectsIDJArray = jsonObject.getAsJsonArray("childrenObjectsID");
        if(childrenObjectsIDJArray != null) {
            for(JsonElement element : childrenObjectsIDJArray) {
                entity.getChildrenObjectsID().add(element.getAsInt());
            }
        }

        entity.name = name;
        entity.tag.set(tag);
        tag.destroy();
        entity.setColor(color);
        entity.active = active;
        entity.ID = ID;
        entity.layerName = layerName;

        for(JsonElement element : systems) {
            System system = context.deserialize(element, Component.class);
            entity.addSystem(system);
        }
        //System.out.println("\u001B[32m size of game object " + gameObject.name + " list of components: " + gameObject.getComponents().size() + " \u001B[32m");

        for(JsonElement element : components) {
            Component component = context.deserialize(element, Component.class);

            int lastComponentID = component.componentID;
            if(component instanceof TransformComponent) {
                entity.addComponent(component);
            } else if(component instanceof MeshComponent textureComponent) {
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

                MeshComponent meshComponent = new MeshComponent();
                entity.addComponent(meshComponent);
                entity.getComponent(MeshComponent.class).set(component);
                entity.getComponent(MeshComponent.class).texture.set(texture2D);
            } else if(component instanceof Rigidbody2DComponent rigidbody2DComponent) {
                rigidbody2DComponent.set(component);
                entity.addComponent(rigidbody2DComponent);
            } else if(component instanceof ScriptComponent scriptComponent) {
                if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                    scriptComponent.script.path = scriptComponent.script.path.replaceAll(".java", "");

                    String fullScriptPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + scriptComponent.script.path + ".java";
                    String lastScriptPath = scriptComponent.script.path;

                    String scriptToLoadPath = "";
                    String scriptToAddPath = "";

                    if(new File(fullScriptPath).exists()) {
                        scriptToLoadPath = fullScriptPath;
                        scriptToAddPath = lastScriptPath;
                        //scriptComponent.script.path = fullScriptPath;
                        //((ScriptComponent) sc).script.path = lastScriptPath;
                    } else {// для исправления текущих сцен, т.к. у их ресурсов стоит полный путь.
                        // чтобы это исправить загружаем по этому пути скрипт, находим относительный путь и присваиваем его path для того,
                        // чтобы в следующий раз выполнился блок кода вышe
                        if(new File(scriptComponent.script.path + ".java").exists()) {
                            String relativePath = FileUtils.getRelativePath(
                                    new File(scriptComponent.script.path + ".java"),
                                    new File(ProjectsManager.getCurrentProject().getProjectPath())
                            );
                            scriptToLoadPath = scriptComponent.script.path + ".java";
                            scriptToAddPath = relativePath.replace(".java", "");
                            //scriptComponent.script.path += ".java";
                            //relativePath = relativePath.replace(".java", "");
                            //((ScriptComponent) sc).script.path = relativePath;
                        }
                    }

                    scriptComponent.script.path = scriptToLoadPath;
                    // load the script component class
                    scriptComponent.set(scriptComponent);
                    scriptComponent.script.path = scriptToAddPath;
                    entity.addComponent(scriptComponent);
                } else {
                    ScriptComponent sc = new ScriptComponent();
                    entity.addComponent(sc);
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

                entity.addComponent(audioComponent);
            } else {
                entity.addComponent(component);
            }

            component.componentID = lastComponentID;
            //object2D.getComponents().get(object2D.getComponents().size() - 1).componentID = lastComponentID;
        }

        return entity;
    }
}
