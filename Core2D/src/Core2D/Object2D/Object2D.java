package Core2D.Object2D;

import Core2D.Camera2D.Camera2D;
import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Component.Component;
import Core2D.Component.Components.Rigidbody2DComponent;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Component.NonDuplicated;
import Core2D.Component.NonRemovable;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Resources;
import Core2D.Core2D.Settings;
import Core2D.Log.Log;
import Core2D.Scene2D.SceneManager;
import Core2D.Shader.Shader;
import Core2D.Shader.ShaderProgram;
import Core2D.ShaderUtils.*;
import Core2D.Utils.ExceptionsUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;

public class Object2D extends CommonDrawableObjectsParameters implements Serializable, AutoCloseable
{
    // лист компонентов
    private List<Component> components = new ArrayList<>();

    // model view projection matrix
    private transient Matrix4f mvpMatrix;

    // камера, в виде которой будет объект
    private transient Camera2D attachedCamera2D;

    // цвет
    private Vector4f color;

    // размер объекта (дефолт - 100x100)
    private transient Vector2f size = new Vector2f(100.0f, 100.0f);

    // индексы (первый и второй треугольник)
    // использую тип short для меньшей нагрузки на память видеокарты и ram (2 (short), 4 байт (int))
    private transient short[] indices = new short[] { 0, 1, 2, 0, 2, 3 };

    // массив данных о вершинах
    // первые строки - позиции вершин, вторые строки - текстурные координаты
    private transient float[] data = new float[] {
            0.0f, 0.0f,
            0, 0,

            0.0f, size.y,
            0, 0,

            size.x, size.y,
            0, 0,

            size.x, 0.0f,
            0, 0,
    };

    // шейдерная программа объекта
    private transient ShaderProgram shaderProgram;

    // VAO четырехугольника (VAO - Vertex Array Object. Хранит в себе указатели на VBO, IBO и т.д.)
    private transient VertexArrayObject vertexArrayObject;

    private int drawingMode = GL_TRIANGLES;

    // имя объекта
    private String name = "default";

    // является ли ui элементом
    private boolean isUIElement = false;

    // цвета для picking`а мышкой
    private transient Vector3f pickColor = new Vector3f();

    public Object2D()
    {
        // загружаю шейдеры
        Shader vertexShader = new Shader(Resources.ShadersTexts.Object2D.vertexShaderText, GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(Resources.ShadersTexts.Object2D.fragmentShaderText, GL_FRAGMENT_SHADER);

        // создаю шейдерную программу
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        vertexShader = null;
        fragmentShader = null;

        Vector4f col = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        setColor(col);
        col = null;

        loadVAO();

        addComponent(new TransformComponent());
        addComponent(new TextureComponent());
        getComponent(TextureComponent.class).setTexture2D(Resources.Textures.WHITE_TEXTURE);

        if(Core2D.currentCamera2D != null) {
            setAttachedCamera2D(Core2D.currentCamera2D);
        }

        if(Settings.Other.Picking.currentPickingColor.x < 255.0f) {
            Settings.Other.Picking.currentPickingColor.x++;
        } else {
            if (Settings.Other.Picking.currentPickingColor.y < 255.0f) {
                Settings.Other.Picking.currentPickingColor.x = 0.0f;
                Settings.Other.Picking.currentPickingColor.y++;
            } else if(Settings.Other.Picking.currentPickingColor.x == 255.0f && Settings.Other.Picking.currentPickingColor.y == 255.0f) {
                Settings.Other.Picking.currentPickingColor.x = 0.0f;
                Settings.Other.Picking.currentPickingColor.y = 0.0f;
                Settings.Other.Picking.currentPickingColor.z++;
            }
        }

        pickColor = null;
        pickColor = new Vector3f(Settings.Other.Picking.currentPickingColor);
    }

    // копировать объект
    public Object2D(Object2D object2D)
    {
        // загружаю шейдеры
        Shader vertexShader = new Shader(Resources.ShadersTexts.Object2D.vertexShaderText, GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(Resources.ShadersTexts.Object2D.fragmentShaderText, GL_FRAGMENT_SHADER);

        // создаю шейдерную программу
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        vertexShader = null;
        fragmentShader = null;

        setColor(new Vector4f(object2D.getColor().x, object2D.getColor().y, object2D.getColor().z, object2D.getColor().w));

        data = object2D.getData();

        loadVAO();

        Transform objectTransform = object2D.getComponent(TransformComponent.class).getTransform();
        addComponent(new TransformComponent(objectTransform));

        TextureComponent objectTextureComponent = object2D.getComponent(TextureComponent.class);
        objectTextureComponent.setTexture2D(objectTextureComponent.getTexture2D());
        addComponent(objectTextureComponent);
        objectTextureComponent = null;

        if(object2D.getComponent(Rigidbody2DComponent.class) != null) {
            Rigidbody2DComponent rigidbody2DComponent = new Rigidbody2DComponent();
            rigidbody2DComponent.set(object2D.getComponent(Rigidbody2DComponent.class));
            addComponent(rigidbody2DComponent);
            rigidbody2DComponent = null;
        }

        active = object2D.isActive();

        drawingMode = object2D.getDrawingMode();

        tag = object2D.getTag();

        isUIElement = object2D.isUIElement;

        if(Core2D.currentCamera2D != null) {
            setAttachedCamera2D(object2D.getAttachedCamera2D());
        }

        object2D = null;

        if(Settings.Other.Picking.currentPickingColor.x < 255.0f) {
            Settings.Other.Picking.currentPickingColor.x++;
        } else {
            if (Settings.Other.Picking.currentPickingColor.y < 255.0f) {
                Settings.Other.Picking.currentPickingColor.x = 0.0f;
                Settings.Other.Picking.currentPickingColor.y++;
            } else if(Settings.Other.Picking.currentPickingColor.x == 255.0f && Settings.Other.Picking.currentPickingColor.y == 255.0f) {
                Settings.Other.Picking.currentPickingColor.x = 0.0f;
                Settings.Other.Picking.currentPickingColor.y = 0.0f;
                Settings.Other.Picking.currentPickingColor.z++;
            }
        }

        pickColor = null;
        pickColor = new Vector3f(Settings.Other.Picking.currentPickingColor);
    }

    /**
     * @return New Object2D on current scene
     */
    public static Object2D instantiate()
    {
        if(SceneManager.getCurrentScene2D() != null) {
            Object2D object2D = new Object2D();
            object2D.setLayer(SceneManager.getCurrentScene2D().getLayering().getLayer("default"));

            return object2D;
        }

        return null;
    }

    private void loadVAO()
    {
        vertexArrayObject = new VertexArrayObject();
        // VBO вершин (VBO - Vertex Buffer Object. Может хранить в себе цвета, позиции вершин и т.д.)
        VertexBufferObject vertexBufferObject = new VertexBufferObject(data);
        // IBO вершин (IBO - Index Buffer Object. IBO хранит в себе индексы вершин, по которым будут соединяться вершины)
        IndexBufferObject indexBufferObject = new IndexBufferObject(indices);

        // создаю описание аттрибутов в шейдерной программе
        BufferLayout attributesLayout = new BufferLayout(
                new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2),
                new VertexAttribute(1, "textureCoordsAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2)
        );

        vertexBufferObject.setLayout(attributesLayout);
        vertexArrayObject.putVBO(vertexBufferObject, false);
        vertexArrayObject.putIBO(indexBufferObject);

        attributesLayout = null;
        vertexBufferObject = null;
        indexBufferObject = null;

        // отвязываю vao
        vertexArrayObject.unBind();
    }

    @Override
    public void update()
    {
        updateMVPMatrix();
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        if(active) {
            for(Component component : components) {
                component.deltaUpdate(deltaTime);
            }
        }
    }

    private void updateMVPMatrix()
    {
        if(attachedCamera2D != null && !isUIElement) {
            mvpMatrix = new Matrix4f(Core2D.getProjectionMatrix()).mul(attachedCamera2D.getTransform().getModelMatrix()).mul(getComponent(TransformComponent.class).getTransform().getModelMatrix());
        } else {
            mvpMatrix = new Matrix4f(Core2D.getProjectionMatrix()).mul(getComponent(TransformComponent.class).getTransform().getModelMatrix());
        }
    }

    @Override
    public void destroy()
    {
        shouldDestroy = true;

        Iterator<Component> componentsIterator = components.iterator();
        while(componentsIterator.hasNext()) {
            Component component = componentsIterator.next();
            component.destroy();
            component = null;
            componentsIterator.remove();
        }
        components = null;
        componentsIterator = null;

        mvpMatrix = null;

        attachedCamera2D = null;

        color = null;

        pickColor = null;

        size = null;

        indices = null;

        data = null;

        shaderProgram.delete();
        shaderProgram = null;

        vertexArrayObject.destroy();
        vertexArrayObject = null;

        tag = null;

        destroyLayerObject();

        try {
            close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }
    }

    public void addComponent(Component component)
    {
        for(Component currentComponent : components) {
            if(currentComponent.getClass().equals(component.getClass()) && currentComponent instanceof NonDuplicated) {
                Log.showErrorDialog("Component " + component.getClass().getName() + " already exists");
                throw new RuntimeException("Component " + component.getClass().getName() + " already exists");
            }
        }

        components.add(component);
        component.setObject2D(this);
        component.init();

        component = null;
    }

    public <T extends Component> List<T> getAllComponents(Class<T> componentClass)
    {
        List<T> componentsFound = new ArrayList<>();
        for(Component component : components) {
            if(component.getClass().isAssignableFrom(componentClass)) {
                componentsFound.add(componentClass.cast(component));
            }
        }

        return componentsFound;
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for(Component component : components) {
            if(component.getClass().isAssignableFrom(componentClass)) {
                return componentClass.cast(component);
            }
        }

        return null;
    }

    public void removeComponent(Component component)
    {
        if(component instanceof NonRemovable) {
            Log.showErrorDialog("Component " + component.getClass().getName() + " is non-removable");

            component = null;
            throw new RuntimeException("Component " + component.getClass().getName() + " is non-removable");
        } else {
            component.destroy();
            components.remove(component);
            component = null;
        }
    }

    public List<Component> getComponents() { return components; }

    public VertexArrayObject getVertexArrayObject() { return vertexArrayObject; }

    public Vector4f getColor() { return color; }
    public void setColor(Vector4f color)
    {
        this.color = new Vector4f(color);
        color = null;

        shaderProgram.bind();

        ShaderUtils.setUniform(
                shaderProgram.getHandler(),
                "color",
                new Vector4f(this.color)
        );

        shaderProgram.unBind();
    }

    public ShaderProgram getShaderProgram() { return shaderProgram; }

    public Matrix4f getMvpMatrix() { return mvpMatrix; }

    public Camera2D getAttachedCamera2D() { return attachedCamera2D; }
    public void setAttachedCamera2D(Camera2D attachedCamera2D)
    {
        this.attachedCamera2D = attachedCamera2D;
        attachedCamera2D = null;
    }

    public float[] getData() { return data; }

    public int getDrawingMode() { return drawingMode; }
    public void setDrawingMode(int drawingMode) { this.drawingMode = drawingMode; }

    public String getName() { return name; }
    public void setName(String name)
    {
        this.name = name;
        name = null;
    }

    public boolean isUIElement() { return isUIElement; }
    public void setUIElement(boolean UIElement) { isUIElement = UIElement; }

    //public List<SpriteAnimation> getSpriteAnimations() { return spriteAnimations; }

    public void loadShaders(String vertexShaderCode, String fragmentShaderCode)
    {
        // загружаю шейдеры
        Shader vertexShader = new Shader(vertexShaderCode, GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(fragmentShaderCode, GL_FRAGMENT_SHADER);

        vertexShaderCode = null;
        fragmentShaderCode = null;

        shaderProgram.delete();

        // создаю шейдерную программу
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        Vector4f col = new Vector4f(color.x, color.y, color.z, color.w);
        setColor(col);
        col = null;
    }

    public void loadShader(String shaderCode, int shaderType)
    {
        Shader vertexShader;
        Shader fragmentShader;

        if(shaderType == GL_VERTEX_SHADER) {
            vertexShader = new Shader(shaderCode, shaderType);

            fragmentShader = new Shader(Resources.ShadersTexts.Object2D.fragmentShaderText, GL_FRAGMENT_SHADER);
        } else if(shaderType == GL_FRAGMENT_SHADER) {
            fragmentShader = new Shader(shaderCode, shaderType);

            vertexShader = new Shader(Resources.ShadersTexts.Object2D.vertexShaderText, GL_VERTEX_SHADER);
        } else {
            Log.CurrentSession.println("Failed to load shader type id " + shaderType + "!");

            vertexShader = new Shader(Resources.ShadersTexts.Object2D.vertexShaderText, GL_VERTEX_SHADER);
            fragmentShader = new Shader(Resources.ShadersTexts.Object2D.fragmentShaderText, GL_FRAGMENT_SHADER);
        }

        shaderProgram.delete();

        shaderCode = null;

        // создаю шейдерную программу
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        Vector4f col = new Vector4f(color.x, color.y, color.z, color.w);
        setColor(col);
        col = null;
    }

    public Vector3f getPickColor() { return pickColor; }

    @Override
    public void close() throws Exception {

    }
}