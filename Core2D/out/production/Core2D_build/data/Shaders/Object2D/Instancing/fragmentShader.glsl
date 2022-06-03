#version 330 core

precision mediump float;

out mediump vec4 fragColor;

uniform sampler2D sampler;

in vec2 vs_textureCoords;

in vec4 col;

void main()
{
    vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y));

    //if(col.w <= 0.3) discard;
    fragColor = col * textureColor;
}
