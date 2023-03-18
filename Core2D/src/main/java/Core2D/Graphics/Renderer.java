package Core2D.Graphics;

import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Scene2D.SceneManager;

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
            Camera2DComponent camera2DComponent = componentsQuery.getComponent(Camera2DComponent.class);

            if(camera2DComponent != null) {
                ECSWorld.getCurrentECSWorld().meshesRendererSystem.renderEntity(entity, camera2DComponent, shader);
                ECSWorld.getCurrentECSWorld().primitivesRendererSystem.renderEntity(entity, camera2DComponent, shader);
            }
        }
    }

    public void render(Entity entity, Camera2DComponent camera2DComponent, Shader shader)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        ECSWorld.getCurrentECSWorld().meshesRendererSystem.renderEntity(entity, camera2DComponent, shader);
        ECSWorld.getCurrentECSWorld().primitivesRendererSystem.renderEntity(entity, camera2DComponent, shader);
    }

    public void render(Entity entity, Camera2DComponent camera2DComponent)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        ECSWorld.getCurrentECSWorld().meshesRendererSystem.renderEntity(entity, camera2DComponent);
        ECSWorld.getCurrentECSWorld().primitivesRendererSystem.renderEntity(entity, camera2DComponent);
    }

    public void render(Layering layering, Camera2DComponent camera2DComponent)
    {
        if(layering.isShouldDestroy()) return;
        int layersNum = layering.getLayers().size();
        for(int i = 0; i < layersNum; i++) {
            if(layering.isShouldDestroy()) break;
            render(layering.getLayers().get(i), camera2DComponent);
        }
    }

    public void render(Layer layer, Camera2DComponent camera2DComponent)
    {
        if(layer.isShouldDestroy()) return;

        int renderingObjectsNum = layer.getEntities().size();
        for(int i = 0; i < renderingObjectsNum; i++) {
            if(layer.isShouldDestroy()) break;
            render(layer.getEntities().get(i), camera2DComponent);
        }
    }
}
