package Core2D.DataClasses;

import Core2D.Graphics.OpenGL.*;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class MeshData
{
    public String name = "";

    public transient int[] indices;

    public transient float[] verticesPositions;
    public transient float[] verticesUV;
    public transient float[] verticesNormals;

    // opengl --------------------------------
    private transient VertexArray vertexArray;
    private transient VertexBuffer positionsBuffer;
    private transient VertexBuffer uvBuffer;
    private transient VertexBuffer normalsBuffer;
    private transient IndexBuffer indexBuffer;

    public MeshData(int verticesNum, int indicesNum)
    {
        //verticesData = new VertexData[verticesNum];
        indices = new int[indicesNum];

        verticesPositions = new float[verticesNum * 3];
        verticesUV = new float[verticesNum * 3];
        verticesNormals = new float[verticesNum * 3];
    }

    public void createVAO()
    {
        vertexArray = new VertexArray();

        positionsBuffer = new VertexBuffer(verticesPositions);
        positionsBuffer.setLayout(new BufferLayout(new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT3)));
        vertexArray.putVBO(positionsBuffer, false);

        uvBuffer = new VertexBuffer(verticesUV);
        uvBuffer.setLayout(new BufferLayout(new VertexAttribute(1, "textureCoordsAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT3)));
        vertexArray.putVBO(uvBuffer, false);

        normalsBuffer = new VertexBuffer(verticesNormals);
        normalsBuffer.setLayout(new BufferLayout(new VertexAttribute(2, "normalPositionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT3)));
        vertexArray.putVBO(normalsBuffer, false);

        indexBuffer = new IndexBuffer(indices);

        vertexArray.putIBO(indexBuffer);
    }

    public void setVertexPosition(int idx, float x, float y, float z)
    {
        verticesPositions[idx * 3] = x;
        verticesPositions[idx * 3 + 1] = y;
        verticesPositions[idx * 3 + 2] = z;
    }

    public void setVertexUV(int idx, float x, float y, float z)
    {
        verticesUV[idx * 3] = x;
        verticesUV[idx * 3 + 1] = y;
        verticesUV[idx * 3 + 2] = z;
    }

    public void setVertexNormal(int idx, float x, float y, float z)
    {
        verticesNormals[idx * 3] = x;
        verticesNormals[idx * 3 + 1] = y;
        verticesNormals[idx * 3 + 2] = z;
    }

    public void setIndex(int faceIdx, int indexIdx, int value)
    {
        indices[faceIdx * 3 + indexIdx] = value;
    }

    public Vector3f getVertexPosition(int idx)
    {
        return new Vector3f(verticesPositions[idx * 3], verticesPositions[idx * 3 + 1], verticesPositions[idx * 3 + 2]);
    }

    public Vector3f getVertexUV(int idx)
    {
        return new Vector3f(verticesUV[idx * 3], verticesUV[idx * 3 + 1], verticesUV[idx * 3 + 2]);
    }

    public Vector3f getVertexNormal(int idx)
    {
        return new Vector3f(verticesNormals[idx * 3], verticesNormals[idx * 3 + 1], verticesNormals[idx * 3 + 2]);
    }

    public Vector3i getFaceIndices(int faceIdx)
    {
        return new Vector3i(indices[faceIdx * 3], indices[faceIdx * 3 + 1], indices[faceIdx * 3 + 2]);
    }

    public void destroy()
    {
        positionsBuffer.destroy();
        uvBuffer.destroy();
        normalsBuffer.destroy();
        indexBuffer.destroy();
        vertexArray.destroy();
    }

    public VertexArray getVertexArray() { return vertexArray; }

    public VertexBuffer getPositionsBuffer() { return positionsBuffer; }

    public VertexBuffer getUvBuffer() { return uvBuffer; }

    public VertexBuffer getNormalsBuffer() { return normalsBuffer; }

    public IndexBuffer getIndexBuffer() { return indexBuffer; }
}
