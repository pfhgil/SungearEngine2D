#version 330 core

precision mediump float;

out mediump vec4 fragColor;

uniform mediump vec4 color;

void main()
{
    fragColor = color;
}
