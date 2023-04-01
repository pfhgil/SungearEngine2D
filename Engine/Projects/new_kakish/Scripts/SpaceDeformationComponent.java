import Core2D.ECS.*;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.*;
import Core2D.Graphics.OpenGL.FrameBuffer;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.*;
import Core2D.Log.*;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.*;

// Attention! We do not recommend writing logic in components. Try to declare only fields in components.
public class SpaceDeformationComponent extends Component
{
    private boolean firstTime = true;
    //

    @Override
    public void update()
    {
        /*
        if(firstTime) {
            for(ComponentsQuery componentsQuery : ECSWorld.getCurrentECSWorld().getComponentsQueries()) {
                for(Component component : componentsQuery.getComponents()) {
                    if(component instanceof Camera2DComponent camera2DComponent) {
                        camera2DComponent.camera2DCallbacks.add(new Camera2DComponent.Camera2DCallback() {
                            @Override
                            public void preRender() {

                            }

                            @Override
                            public void postRender() {

                            }
                        });
                    }
                }
            }

            firstTime = false;
        }

         */

        //
        CameraComponent camera2DComponent = entity.getComponent(CameraComponent.class);
        if(camera2DComponent == null) return;

        PostprocessingLayer postprocessingLayer = entity.getComponent(CameraComponent.class).postprocessingLayers
                .stream().filter(ppLayer -> ppLayer.getEntitiesLayerToRenderName().equals("sceneCopyLayer")).findFirst().orElse(null);

        if(postprocessingLayer != null) {
            FrameBuffer out = postprocessingLayer.getFrameBuffer();
            camera2DComponent.resultFrameBuffer.copyData(out);
        }
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {

    }

    // otherEntity - an entity, one of whose colliders entered one of the colliders of this entity.
    @Override
    public void collider2DEnter(Entity otherEntity)
    {

    }

    // otherEntity - an entity whose body came out of the colliders of this entity
    @Override
    public void collider2DExit(Entity otherEntity)
    {

    }

    // camera2DComponent - the camera that renders this entity.в
    @Override
    public void render(CameraComponent cameraComponent)
    {

    }

    // Use the "shader" parameter to render this entity.
    @Override
    public void render(CameraComponent cameraComponent, Shader shader)
    {

    }
}

