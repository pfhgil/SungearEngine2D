package Core2D.Graphics;

import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;

public class Renderer
{
    // рендерит во все камеры на сцене
    public void render(Entity entity)
    {
        render(entity, (Shader) null);
    }

    // рендерит во все камеры на сцене
    public void render(Entity entity, Shader shader)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        for(ComponentsQuery componentsQuery : ECSWorld.getCurrentECSWorld().getComponentsQueries()) {
            CameraComponent cameraComponent = componentsQuery.getComponent(CameraComponent.class);

            if(cameraComponent != null) {
                ECSWorld.getCurrentECSWorld().meshesRendererSystem.renderEntity(entity, cameraComponent, shader);
                ECSWorld.getCurrentECSWorld().primitivesSystem.renderEntity(entity, cameraComponent, shader);
            }
        }
    }

    public void render(Entity entity, CameraComponent cameraComponent, Shader shader)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        ECSWorld.getCurrentECSWorld().meshesRendererSystem.renderEntity(entity, cameraComponent, shader);
        ECSWorld.getCurrentECSWorld().primitivesSystem.renderEntity(entity, cameraComponent, shader);
    }

    public void render(Entity entity, CameraComponent cameraComponent)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        ECSWorld.getCurrentECSWorld().meshesRendererSystem.renderEntity(entity, cameraComponent);
        ECSWorld.getCurrentECSWorld().primitivesSystem.renderEntity(entity, cameraComponent);
    }

    public void render(Layering layering, CameraComponent cameraComponent)
    {
        if(layering.isShouldDestroy()) return;
        int layersNum = layering.getLayers().size();
        for(int i = 0; i < layersNum; i++) {
            if(layering.isShouldDestroy()) break;
            render(layering.getLayers().get(i), cameraComponent);
        }
    }

    public void render(Layer layer, CameraComponent cameraComponent)
    {
        if(layer.isShouldDestroy()) return;

        int renderingObjectsNum = layer.getEntities().size();
        for(int i = 0; i < renderingObjectsNum; i++) {
            if(layer.isShouldDestroy()) break;
            render(layer.getEntities().get(i), cameraComponent);
        }
    }
}
