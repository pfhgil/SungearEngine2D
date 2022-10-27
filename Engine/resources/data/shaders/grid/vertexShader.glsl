#version 330 core

// id аттрибута = 0. позиции вершин. входной параметр
layout (location = 0) in vec2 positionAttribute;
// id аттрибута = 2. текстурная координата вершины. входной параметр
layout (location = 1) in vec2 textureCoordsAttribute;

uniform mat4 mvpMatrix;


out vec2 vs_textureCoords;
out vec2 posAttr;

void main()
{
    vs_textureCoords = textureCoordsAttribute;
    posAttr = positionAttribute;

    gl_Position = mvpMatrix * vec4(positionAttribute, 0.0, 1.0);
}