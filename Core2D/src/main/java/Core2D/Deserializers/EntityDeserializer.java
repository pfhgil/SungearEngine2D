package Core2D.Deserializers;

import Core2D.AssetManager.AssetManager;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Audio.AudioComponent;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.Physics.Rigidbody2DComponent;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Component.Components.Shader.TextureComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Project.ProjectsManager;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Tag;
import com.google.gson.*;
import org.joml.Vector4f;

import java.io.File;
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
        JsonArray childrenObjectsIDJArray = jsonObject.getAsJsonArray("childrenEntitiesID");
        if(childrenObjectsIDJArray != null) {
            for(JsonElement element : childrenObjectsIDJArray) {
                entity.getChildrenEntitiesID().add(element.getAsInt());
            }
        }

        entity.name = name;
        entity.tag.set(tag);
        tag.destroy();
        entity.setColor(color);
        entity.active = active;
        entity.ID = ID;
        entity.layerName = layerName;

        /*
        for(JsonElement element : systems) {
            System system = context.deserialize(element, System.class);
            if(system instanceof ScriptableSystem scriptableSystem) {
                if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                    scriptableSystem.script.path = scriptableSystem.script.path.replaceAll(".java", "");

                    String fullScriptPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + scriptableSystem.script.path + ".java";
                    String lastScriptPath = scriptableSystem.script.path;

                    String scriptToLoadPath = "";
                    String scriptToAddPath = "";

                    if(new File(fullScriptPath).exists()) {
                        scriptToLoadPath = fullScriptPath;
                        scriptToAddPath = lastScriptPath;
                    } else {// для исправления текущих сцен, т.к. у их ресурсов стоит полный путь.
                        // чтобы это исправить загружаем по этому пути скрипт, находим относительный путь и присваиваем его path для того,
                        // чтобы в следующий раз выполнился блок кода вышe
                        if(new File(scriptableSystem.script.path + ".java").exists()) {
                            String relativePath = FileUtils.getRelativePath(
                                    new File(scriptableSystem.script.path + ".java"),
                                    new File(ProjectsManager.getCurrentProject().getProjectPath())
                            );
                            scriptToLoadPath = scriptableSystem.script.path + ".java";
                            scriptToAddPath = relativePath.replace(".java", "");
                        }
                    }

                    scriptableSystem.script.path = scriptToLoadPath;
                    // load the script component class
                    scriptableSystem.set(scriptableSystem);
                    scriptableSystem.script.path = scriptToAddPath;
                    entity.addSystem(scriptableSystem);
                } else {
                    ScriptableSystem sc = new ScriptableSystem();
                    entity.addSystem(sc);
                    sc.set(scriptableSystem);
                }
            } else {
                if(system == null) continue;
                entity.addSystem(system);
            }
        }

         */
        //System.out.println("\u001B[32m size of game object " + gameObject.name + " list of components: " + gameObject.getComponents().size() + " \u001B[32m");

        for(JsonElement element : components) {
            Component component = context.deserialize(element, Component.class);

            if(component == null) continue;

            int lastComponentID = component.ID;
            if(component instanceof MeshComponent meshComponent) {
                meshComponent.getShader().fixUniforms();
                Shader shader = new Shader();
                shader.getShaderUniforms().addAll(meshComponent.getShader().getShaderUniforms());
                shader.compile(AssetManager.getInstance().getShaderData(meshComponent.getShader().path));

                Texture2D texture = new Texture2D(
                        AssetManager.getInstance().getTexture2DData(meshComponent.getTexture().path),
                        meshComponent.getTexture().getGLTextureBlock()
                );

                //texture2D.blendSourceFactor = textureComponent.texture.blendSourceFactor;
                //texture2D.blendDestinationFactor = textureComponent.texture.blendDestinationFactor;


                meshComponent.setTexture(texture);
                meshComponent.setShader(shader);
                entity.addComponent(meshComponent);

                meshComponent.ID = lastComponentID;
                //newMeshComponent.set(component);
            } else if(component instanceof TextureComponent textureComponent) {
                Texture2D texture = new Texture2D(
                        AssetManager.getInstance().getTexture2DData(textureComponent.getTexture().path),
                        textureComponent.getTexture().getGLTextureBlock()
                );

                textureComponent.setTexture(texture);
                entity.addComponent(textureComponent);

                textureComponent.ID = lastComponentID;
            } else if(component instanceof Rigidbody2DComponent rigidbody2DComponent) {
                rigidbody2DComponent.set(component);

                entity.addComponent(rigidbody2DComponent);

                rigidbody2DComponent.ID = lastComponentID;
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
                        }
                    }

                    scriptComponent.script.path = scriptToLoadPath;
                    // load the script component class
                    scriptComponent.set(scriptComponent);
                    scriptComponent.script.path = scriptToAddPath;
                    entity.addComponent(scriptComponent);

                    scriptComponent.ID = lastComponentID;
                } else {
                    ScriptComponent sc = new ScriptComponent();
                    entity.addComponent(sc);
                    sc.set(scriptComponent);

                    sc.ID = lastComponentID;
                }
            } else if(component instanceof AudioComponent audioComponent) {
                /*
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

                audioComponent.ID = lastComponentID;

                 */
            } else if(component instanceof Camera2DComponent camera2DComponent) {
                Shader defaultShader = new Shader(AssetManager.getInstance().getShaderData(camera2DComponent.postprocessingDefaultShader.path));

                ECSWorld.getCurrentECSWorld().camerasManagerSystem.setPostprocessingDefaultShader(camera2DComponent, defaultShader);
                //camera2DComponent.setPostprocessingDefaultShader(defaultShader);
                for(int i = 0; i < camera2DComponent.postprocessingLayers.size(); i++) {
                    PostprocessingLayer ppLayer = camera2DComponent.postprocessingLayers.get(i);

                    ppLayer.getShader().fixUniforms();
                    Shader layerShader = new Shader();
                    layerShader.getShaderUniforms().addAll(ppLayer.getShader().getShaderUniforms());
                    layerShader.compile(AssetManager.getInstance().getShaderData(ppLayer.getShader().path));

                    ppLayer.setShader(layerShader);
                    ppLayer.init();
                }
                entity.addComponent(camera2DComponent);

                camera2DComponent.ID = lastComponentID;
            } else {
                entity.addComponent(component);
            }

            component.ID = lastComponentID;
        }

        return entity;
    }
}
