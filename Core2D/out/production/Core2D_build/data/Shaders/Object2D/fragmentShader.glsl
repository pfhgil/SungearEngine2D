#version 330 core

precision mediump float;

out mediump vec4 fragColor;

uniform int useSampler;
uniform sampler2D sampler;

uniform mediump vec4 color;

in vec2 vs_textureCoords;

void main()
{
    if(useSampler == 1) {
        vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        fragColor = color * textureColor;
    } else {
        fragColor = color;
    }
}
