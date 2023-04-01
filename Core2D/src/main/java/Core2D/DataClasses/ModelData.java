package Core2D.DataClasses;

import Core2D.Log.Log;
import Core2D.Settings.ImportSettings;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.assimp.*;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class ModelData extends Data
{
    public MeshData[] meshesData;

    // TODO: сделать загрузку моделек по пути
    @Override
    public ModelData load(String path)
    {
        return this;
    }

    @Override
    public ModelData load(InputStream inputStream, String path)
    {
        this.path = path;

        try(inputStream) {
            ByteBuffer resource = Utils.resourceToByteBuffer(inputStream);

            try(AIScene aiScene = Assimp.aiImportFileFromMemory(resource, ImportSettings.aiImportFlags, "")) {
                processAIScene(aiScene);
            }
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
        return this;
    }

    private void processAIScene(AIScene aiScene)
    {
        if(aiScene != null) {
            meshesData = new MeshData[aiScene.mNumMeshes()];

            for(int i = 0; i < meshesData.length; i++) {
                try(AIMesh aiMesh = AIMesh.create(aiScene.mMeshes().get(i))) {
                    meshesData[i] = processAIMesh(aiMesh);
                    meshesData[i].createVAO();
                }
            }
        }
    }

    private MeshData processAIMesh(AIMesh aiMesh)
    {
        if(aiMesh != null) {
            MeshData meshData = new MeshData(aiMesh.mNumVertices(), aiMesh.mNumFaces() * 3);

            int i = 0;
            int k = 0;

            for(i = 0; i < aiMesh.mNumVertices(); i++) {
                AIVector3D vertexPos = aiMesh.mVertices().get(i);

                meshData.setVertexPosition(i, vertexPos.x(), vertexPos.y(), vertexPos.z());
            }

            for(i = 0; i < aiMesh.mNumFaces(); i++) {
                AIFace aiFace = aiMesh.mFaces().get(i);

                for(k = 0; k < aiFace.mNumIndices(); k++) {
                    meshData.setIndex(i, k, aiFace.mIndices().get(k));
                }
            }

            int texPosCount = 0;
            for(i = 0; i < aiMesh.mTextureCoords().limit(); i++) {
                AIVector3D.Buffer texPosBuf = aiMesh.mTextureCoords(i);

                //Log.CurrentSession.println("tex: " + texPosBuf, Log.MessageType.SUCCESS);

                if(texPosBuf == null) continue;

                for(k = 0; k < texPosBuf.limit(); k++) {
                    AIVector3D texPos = texPosBuf.get(k);

                    meshData.setVertexUV(texPosCount, texPos.x(), texPos.y(), texPos.z());

                    /*
                    Vector3f tp = meshData.getVertexUV(texPosCount);
                    Log.CurrentSession.println("uv: " + tp.x + ", " + tp.y, Log.MessageType.SUCCESS);

                     */

                    texPosCount++;
                }
            }

            for (i = 0; i < aiMesh.mNormals().limit(); i++) {
                AIVector3D normalPosBuffer = aiMesh.mNormals().get(i);

                meshData.setVertexNormal(i, normalPosBuffer.x(), normalPosBuffer.y(), normalPosBuffer.z());
            }

            return meshData;
        }

        return null;
    }
}
