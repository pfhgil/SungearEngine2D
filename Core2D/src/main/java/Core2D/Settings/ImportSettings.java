package Core2D.Settings;

import org.lwjgl.assimp.Assimp;

public class ImportSettings
{
    public static int aiImportFlags = Assimp.aiProcess_SplitLargeMeshes | Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenNormals | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_GenUVCoords;
}
