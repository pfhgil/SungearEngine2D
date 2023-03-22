package Core2D.Deserializers;

import Core2D.AssetManager.AssetManager;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.DataClasses.ShaderData;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Audio.AudioComponent;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.Physics.Rigidbody2DComponent;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Component.Components.Shader.TextureComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Utils.Tag;
import com.google.gson.*;
import org.joml.Vector4f;

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
        boolean active = context.deserialize(jsonObject.get("active"), boolean.class);
        JsonArray components = jsonObject.getAsJsonArray("components");
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

        for(JsonElement element : components) {
            Component component = context.deserialize(element, Component.class);

            if(component == null) continue;

            int lastComponentID = component.ID;
            if(component instanceof MeshComponent meshComponent) {
                meshComponent.getShader().fixUniforms();

                Shader shader = new Shader();

                for(Shader.ShaderDefine shaderDefine : meshComponent.getShader().getShaderDefines()) {
                    shader.getShaderDefine(shaderDefine.name).value = shaderDefine.value;
                }

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
                    scriptComponent.set(scriptComponent);
                    entity.addComponent(scriptComponent);

                    scriptComponent.ID = lastComponentID;
                } else {
                    ScriptComponent sc = new ScriptComponent();
                    entity.addComponent(sc);
                    sc.set(scriptComponent);

                    sc.ID = lastComponentID;
                }
            } else if(component instanceof AudioComponent audioComponent) {
                entity.addComponent(audioComponent);

                audioComponent.ID = lastComponentID;
            } else if(component instanceof CameraComponent cameraComponent) {
                Shader defaultShader = new Shader(AssetManager.getInstance().getShaderData(cameraComponent.postprocessingDefaultShader.path));

                for(Shader.ShaderDefine shaderDefine : cameraComponent.postprocessingDefaultShader.getShaderDefines()) {
                    defaultShader.getShaderDefine(shaderDefine.name).value = shaderDefine.value;
                }

                defaultShader.compile((ShaderData) AssetManager.getInstance().reloadAsset(defaultShader.path, ShaderData.class).getAssetObject());

                ECSWorld.getCurrentECSWorld().camerasManagerSystem.setPostprocessingDefaultShader(cameraComponent, defaultShader);
                //camera2DComponent.setPostprocessingDefaultShader(defaultShader);
                for(int i = 0; i < cameraComponent.postprocessingLayers.size(); i++) {
                    PostprocessingLayer ppLayer = cameraComponent.postprocessingLayers.get(i);

                    ppLayer.getShader().fixUniforms();

                    Shader layerShader = new Shader();

                    for(Shader.ShaderDefine shaderDefine : ppLayer.getShader().getShaderDefines()) {
                        layerShader.getShaderDefine(shaderDefine.name).value = shaderDefine.value;
                    }

                    layerShader.getShaderUniforms().addAll(ppLayer.getShader().getShaderUniforms());
                    layerShader.compile(AssetManager.getInstance().getShaderData(ppLayer.getShader().path));

                    ppLayer.setShader(layerShader);
                    ppLayer.init();
                }
                entity.addComponent(cameraComponent);

                cameraComponent.ID = lastComponentID;
            } else {
                entity.addComponent(component);
            }

            component.ID = lastComponentID;
        }

        return entity;
    }
}
