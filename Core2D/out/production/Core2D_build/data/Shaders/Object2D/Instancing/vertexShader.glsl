#version 330 core

layout (location = 0) in vec2 positionAttribute;
layout (location = 1) in vec2 texCoords[4];
layout (location = 5) in mat4 modelMatrix;
layout (location = 9) in vec4 color;

out vec2 vs_textureCoords;
out vec4 col;

uniform mat4 projectionMatrix;
uniform mat4 cameraMatrix;

uniform int isUIInstancing;

void main()
{
    col = color;

    vs_textureCoords = texCoords[gl_VertexID];

    if(isUIInstancing == 0) {
        gl_Position = projectionMatrix * cameraMatrix * modelMatrix * vec4(positionAttribute, 0.0, 1.0);
    } else {
        gl_Position = projectionMatrix * modelMatrix * vec4(positionAttribute, 0.0, 1.0);
    }
}