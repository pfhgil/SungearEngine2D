package Core2D.Instancing;

import Core2D.AssetManager.AssetManager;
import Core2D.Camera2D.CamerasManager;
import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Object2D.Object2D;
import Core2D.Shader.ShaderProgram;
import Core2D.ShaderUtils.ShaderUtils;
import Core2D.Texture2D.Texture2D;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.opengl.GL33C.glVertexAttribDivisor;

// доделать
public class ObjectsInstancing extends CommonDrawableObjectsParameters
{
    private List<Object2D> drawableObjects2D;

    private Texture2D atlasTexture2D;

    private boolean isUIInstancing;

    private float[] verticesPositionsData = new float[] {
            0.0f, 0.0f,

            0.0f, 100,

            100, 100,

            100, 0.0f,
    };

    private float[] matricesData;

    private float[] textureCoordsData;

    private float[] colorData;

    private short[] indices = new short[] { 0, 1, 2, 0, 2, 3 };

    private ShaderProgram shaderProgram;

    private int verticesDataBuffer;
    private int matricesDataBuffer;
    private int textureCoordsBuffer;
    private int colorBuffer;

    private int indexBuffer;
    private int vao;

    public ObjectsInstancing(Object2D[] drawableObjects2D, Texture2D atlasTexture2D, boolean isUIInstancing)
    {
        if(drawableObjects2D != null) {
            this.drawableObjects2D = new ArrayList<>(Arrays.asList(drawableObjects2D));
        } else {
            this.drawableObjects2D = new ArrayList<>();
        }
        drawableObjects2D = null;

        this.atlasTexture2D = atlasTexture2D;
        this.isUIInstancing = isUIInstancing;

        atlasTexture2D = null;

        create();
    }

    private void create()
    {
        shaderProgram = AssetManager.getShaderProgram("objects2DInstancingProgram");

        shaderProgram.bind();

        if(CamerasManager.getMainCamera2D() != null) {
            ShaderUtils.setUniform(
                    shaderProgram.getHandler(),
                    "projectionMatrix",
                    CamerasManager.getMainCamera2D().getProjectionMatrix()
            );
        }

        ShaderUtils.setUniform(
                shaderProgram.getHandler(),
                "isUIInstancing",
                isUIInstancing
        );

        shaderProgram.unBind();

        updateArray();

        verticesDataBuffer = glGenBuffers();
        textureCoordsBuffer = glGenBuffers();
        matricesDataBuffer = glGenBuffers();
        colorBuffer = glGenBuffers();

        indexBuffer = glGenBuffers();

        vao = glGenVertexArrays();
        GL46C.glBindVertexArray(vao);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, verticesDataBuffer);
        glBufferData(GL_ARRAY_BUFFER, verticesPositionsData, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 2 * 4, 0);

        glBindVertexArray(0);

        int vec2Size = 2 * 4;

        glBindBuffer(GL_ARRAY_BUFFER, textureCoordsBuffer);
        glBufferData(GL_ARRAY_BUFFER, textureCoordsData, GL_DYNAMIC_DRAW);

        glBindVertexArray(vao);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, vec2Size * 4, 0);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, vec2Size * 4, vec2Size);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 2, GL11.GL_FLOAT, false, vec2Size * 4, vec2Size * 2);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 2, GL11.GL_FLOAT, false, vec2Size * 4, vec2Size * 3);

        glVertexAttribDivisor(1, 1);
        glVertexAttribDivisor(2, 1);
        glVertexAttribDivisor(3, 1);
        glVertexAttribDivisor(4, 1);

        glBindVertexArray(0);


        glBindBuffer(GL_ARRAY_BUFFER, matricesDataBuffer);
        glBufferData(GL_ARRAY_BUFFER, matricesData, GL_DYNAMIC_DRAW);

        glBindVertexArray(vao);
        int vec4Size = 4 * 4;

        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, vec4Size * 4, 0);
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 4, GL11.GL_FLOAT, false, vec4Size * 4, vec4Size);
        glEnableVertexAttribArray(7);
        glVertexAttribPointer(7, 4, GL11.GL_FLOAT, false, vec4Size * 4, (vec4Size * 2));
        glEnableVertexAttribArray(8);
        glVertexAttribPointer(8, 4, GL11.GL_FLOAT, false, vec4Size * 4, (vec4Size * 3));

        glVertexAttribDivisor(5, 1);
        glVertexAttribDivisor(6, 1);
        glVertexAttribDivisor(7, 1);
        glVertexAttribDivisor(8, 1);

        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        glBufferData(GL_ARRAY_BUFFER, colorData, GL_DYNAMIC_DRAW);

        glBindVertexArray(vao);

        glEnableVertexAttribArray(9);
        glVertexAttribPointer(9, 4, GL11.GL_FLOAT, false, vec4Size, 0);

        glVertexAttribDivisor(9, 1);

        glBindVertexArray(0);
    }

    private void updateArray()
    {
        matricesData = new float[drawableObjects2D.size() * 16];
        textureCoordsData = new float[drawableObjects2D.size() * 8];
        colorData = new float[drawableObjects2D.size() * 4];

        int iter0 = 0;
        int iter1 = 0;
        int iter2 = 0;

        int k = 0;

        for(int i = 0; i < drawableObjects2D.size(); i++) {
            if(drawableObjects2D.get(i).isActive()) {
                float[] modelMatrix = new float[16];
                modelMatrix = drawableObjects2D.get(i).getComponent(TransformComponent.class).getTransform().getModelMatrix().get(modelMatrix);

                float[] texCoords = drawableObjects2D.get(i).getComponent(TextureComponent.class).getUV();

                for (k = 0; k < modelMatrix.length; k++) {
                    matricesData[iter1] = modelMatrix[k];
                    iter1++;
                }

                for (k = 0; k < texCoords.length; k++) {
                    textureCoordsData[iter0] = texCoords[k];
                    iter0++;
                }

                Vector4f color = drawableObjects2D.get(i).getColor();
                for (k = 0; k < 4; k++) {
                    colorData[iter2] = color.get(k);
                    iter2++;
                }

                color = null;
                modelMatrix = null;
                texCoords = null;
            }
        }
    }

    @Override
    public void update()
    {
        updateVBO();
    }

    private void updateVBO()
    {
        updateArray();

        glBindBuffer(GL_ARRAY_BUFFER, textureCoordsBuffer);
        glBufferData(GL_ARRAY_BUFFER, textureCoordsData, GL_DYNAMIC_DRAW);
        //glBufferSubData(GL_ARRAY_BUFFER, 0, textureCoordsData);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, matricesDataBuffer);
        glBufferData(GL_ARRAY_BUFFER, matricesData, GL_DYNAMIC_DRAW);
        //glBufferSubData(GL_ARRAY_BUFFER, 0, matricesData);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        glBufferData(GL_ARRAY_BUFFER, colorData, GL_DYNAMIC_DRAW);
        //glBufferSubData(GL_ARRAY_BUFFER, 0, colorData);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        for(int i = 0; i < drawableObjects2D.size(); i++) {
            drawableObjects2D.get(i).deltaUpdate(deltaTime);
        }
    }

    @Override
    public void destroy()
    {
        shouldDestroy = true;

        Iterator<Object2D> objects2DIterator = drawableObjects2D.iterator();
        while(objects2DIterator.hasNext()) {
            Object2D object2D = objects2DIterator.next();
            object2D.destroy();
            object2D = null;
            objects2DIterator.remove();
        }
        objects2DIterator = null;
        drawableObjects2D = null;

        atlasTexture2D = null;

        verticesPositionsData = null;

        matricesData = null;

        textureCoordsData = null;

        colorData = null;

        indices = null;

        //destroyParams();
    }

    public List<Object2D> getDrawableObjects2D() { return drawableObjects2D; }

    public boolean isUIInstancing() { return isUIInstancing; }
    public void setUIInstancing(boolean UIInstancing) { isUIInstancing = UIInstancing; }

    public ShaderProgram getShaderProgram() { return shaderProgram; }
    public void setShaderProgram(ShaderProgram shaderProgram) { this.shaderProgram = shaderProgram; }

    public Texture2D getAtlasTexture2D() { return atlasTexture2D; }

    public int getVAOID() { return vao; }
}
