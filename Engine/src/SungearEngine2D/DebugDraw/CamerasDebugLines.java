package SungearEngine2D.DebugDraw;

import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.Graphics;
import Core2D.Transform.Transform;
import Core2D.Utils.MathUtils;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Resources;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class CamerasDebugLines
{
    //#FIXME private static Line2D[] inspectorCamera2DLines = new Line2D[4];
    //#FIXME private static Line2D[] mainCamera2DLines = new Line2D[4];

    private static Entity inspectorCamera2DIconObject2D = Entity.createAsObject2D();
    private static Entity mainCamera2DIconObject2D = Entity.createAsObject2D();

    public static void init()
    {
        /*for(int i = 0; i < inspectorCamera2DLines.length; i++) {
            inspectorCamera2DLines[i] = new Line2D();
            inspectorCamera2DLines[i].setColor(new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));
            inspectorCamera2DLines[i].setLineWidth(4.0f);

            mainCamera2DLines[i] = new Line2D();
            mainCamera2DLines[i].setColor(new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));
            mainCamera2DLines[i].setLineWidth(4.0f);
        }*///#FIXME

        inspectorCamera2DIconObject2D.getComponent(MeshComponent.class).texture.set(Resources.Textures.Icons.cameraIcon);
        Vector2f cameraSize = new Vector2f(Resources.Textures.Icons.cameraIcon.getTexture2DData().getWidth(),
                Resources.Textures.Icons.cameraIcon.getTexture2DData().getHeight());
        inspectorCamera2DIconObject2D.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(cameraSize.x / 100.0f, cameraSize.y / 100.0f));

        mainCamera2DIconObject2D.getComponent(MeshComponent.class).texture.set(Resources.Textures.Icons.cameraIcon);
        mainCamera2DIconObject2D.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(cameraSize.x / 100.0f, cameraSize.y / 100.0f));
    }

    public static void draw() {
        Vector2i windowSize = Core2D.getWindow().getSize();
        if (currentSceneManager.getCurrentScene2D() != null
                && currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null
                && ViewsManager.getInspectorView().getCurrentInspectingObject() != null
                && ((Entity) ViewsManager.getInspectorView().getCurrentInspectingObject()).ID != currentSceneManager.getCurrentScene2D().getSceneMainCamera2D().ID) {
            Entity camera2D = (Entity) ViewsManager.getInspectorView().getCurrentInspectingObject();

            TransformComponent cameraTransformComponent = camera2D.getComponent(TransformComponent.class);
            if(cameraTransformComponent != null) {
                Transform cameraTransform = cameraTransformComponent.getTransform();
                Vector2f pointOne = new Vector2f(-cameraTransform.getPosition().x - windowSize.x / 2.0f,
                        -cameraTransform.getPosition().y - windowSize.y / 2.0f);
                Vector2f pointThree = new Vector2f((-cameraTransform.getPosition().x + windowSize.x / 2.0f),
                        (-cameraTransform.getPosition().y + windowSize.y / 2.0f));
                Vector2f localCentre = new Vector2f((pointThree.x - pointOne.x) / 2.0f, (pointThree.y - pointOne.y) / 2.0f);
                Vector2f globalCenter = new Vector2f(
                        new Vector2f(cameraTransform.getPosition()).add(new Vector2f(windowSize).div(2)))
                        .negate()
                        .add(localCentre);
                Vector2f halfSize = new Vector2f(windowSize.x, windowSize.y);

                Vector2f min = new Vector2f(globalCenter).sub(new Vector2f(halfSize).mul(0.5f));
                Vector2f max = new Vector2f(globalCenter).add(new Vector2f(halfSize).mul(0.5f));

                Vector2f[] vertices = {
                        new Vector2f(min.x / cameraTransform.getScale().x, min.y / cameraTransform.getScale().y),
                        new Vector2f(min.x / cameraTransform.getScale().x, max.y / cameraTransform.getScale().y),
                        new Vector2f(max.x / cameraTransform.getScale().x, max.y / cameraTransform.getScale().y),
                        new Vector2f(max.x / cameraTransform.getScale().x, min.y / cameraTransform.getScale().y)
                };

                if (cameraTransform.getRotation() != 0.0f) {
                    for (Vector2f vert : vertices) {
                        MathUtils.rotate(vert, -cameraTransform.getRotation(), globalCenter);
                    }
                }

            /*inspectorCamera2DLines[0].setStart(vertices[0]);
            inspectorCamera2DLines[0].setEnd(vertices[1]);

            inspectorCamera2DLines[1].setStart(vertices[1]);
            inspectorCamera2DLines[1].setEnd(vertices[2]);

            inspectorCamera2DLines[2].setStart(vertices[2]);
            inspectorCamera2DLines[2].setEnd(vertices[3]);

            inspectorCamera2DLines[3].setStart(vertices[3]);
            inspectorCamera2DLines[3].setEnd(vertices[0]);*///#FIXME

                Transform cameraIconObject2DTransform = inspectorCamera2DIconObject2D.getComponent(TransformComponent.class).getTransform();
                cameraIconObject2DTransform.setPosition(
                        new Vector2f(cameraTransform.getPosition())
                );

            /*Graphics.getMainRenderer().render(inspectorCamera2DLines[0]);
            Graphics.getMainRenderer().render(inspectorCamera2DLines[1]);
            Graphics.getMainRenderer().render(inspectorCamera2DLines[2]);
            Graphics.getMainRenderer().render(inspectorCamera2DLines[3]);*///#FIXME

                Graphics.getMainRenderer().render(inspectorCamera2DIconObject2D);
            }
        }
        if (currentSceneManager.getCurrentScene2D() != null && currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
            Entity camera2D = currentSceneManager.getCurrentScene2D().getSceneMainCamera2D();

            TransformComponent cameraTransformComponent = camera2D.getComponent(TransformComponent.class);
            if(cameraTransformComponent != null) {
                Transform cameraTransform = cameraTransformComponent.getTransform();

                Vector2f pointOne = new Vector2f(-cameraTransform.getPosition().x - windowSize.x / 2.0f,
                        -cameraTransform.getPosition().y - windowSize.y / 2.0f);
                Vector2f pointThree = new Vector2f((-cameraTransform.getPosition().x + windowSize.x / 2.0f),
                        (-cameraTransform.getPosition().y + windowSize.y / 2.0f));
                Vector2f localCentre = new Vector2f((pointThree.x - pointOne.x) / 2.0f, (pointThree.y - pointOne.y) / 2.0f);
                Vector2f globalCenter = new Vector2f(
                        new Vector2f(cameraTransform.getPosition()).add(new Vector2f(windowSize).div(2)))
                        .negate()
                        .add(localCentre);
                Vector2f halfSize = new Vector2f(windowSize.x, windowSize.y);

                Vector2f min = new Vector2f(globalCenter).sub(new Vector2f(halfSize).mul(0.5f));
                Vector2f max = new Vector2f(globalCenter).add(new Vector2f(halfSize).mul(0.5f));

                Vector2f[] vertices = {
                        new Vector2f(min.x / cameraTransform.getScale().x, min.y / cameraTransform.getScale().y),
                        new Vector2f(min.x / cameraTransform.getScale().x, max.y / cameraTransform.getScale().y),
                        new Vector2f(max.x / cameraTransform.getScale().x, max.y / cameraTransform.getScale().y),
                        new Vector2f(max.x / cameraTransform.getScale().x, min.y / cameraTransform.getScale().y)
                };

                if (cameraTransform.getRotation() != 0.0f) {
                    for (Vector2f vert : vertices) {
                        MathUtils.rotate(vert, -cameraTransform.getRotation(), globalCenter);
                    }
                }

            /*mainCamera2DLines[0].setStart(vertices[0]);
            mainCamera2DLines[0].setEnd(vertices[1]);

            mainCamera2DLines[1].setStart(vertices[1]);
            mainCamera2DLines[1].setEnd(vertices[2]);

            mainCamera2DLines[2].setStart(vertices[2]);
            mainCamera2DLines[2].setEnd(vertices[3]);

            mainCamera2DLines[3].setStart(vertices[3]);
            mainCamera2DLines[3].setEnd(vertices[0]);*///#FIXME

                Transform cameraIconObject2DTransform = mainCamera2DIconObject2D.getComponent(TransformComponent.class).getTransform();
                cameraIconObject2DTransform.setPosition(
                        new Vector2f(cameraTransform.getPosition())
                );

            /*Graphics.getMainRenderer().render(mainCamera2DLines[0]);
            Graphics.getMainRenderer().render(mainCamera2DLines[1]);
            Graphics.getMainRenderer().render(mainCamera2DLines[2]);
            Graphics.getMainRenderer().render(mainCamera2DLines[3]);*/ //#FIXME

                Graphics.getMainRenderer().render(mainCamera2DIconObject2D);
            }
        }

        inspectorCamera2DIconObject2D.getComponent(TransformComponent.class).getTransform().update(0.0f);
        mainCamera2DIconObject2D.getComponent(TransformComponent.class).getTransform().update(0.0f);
    }
}
