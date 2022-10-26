#version 330 core

precision mediump float;

out mediump vec4 fragColor;

in vec4 col;

void main()
{
    fragColor = col;
}
