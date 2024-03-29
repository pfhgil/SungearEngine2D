#version 330 core

precision mediump float;

out mediump vec4 fragColor;

// режим отрисовки
// 0 - без текстуры
// 1 - обычный (с накладыванием текстуры)
// 2 - для pick object (текстура используется. от нее берется только альфа)
uniform int drawMode;
uniform sampler2D sampler;

uniform mediump vec4 color;

in vec2 vs_textureCoords;

void main()
{
    if(drawMode == 0) {
        fragColor = color;
    } else if(drawMode == 1) {
        vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        fragColor = color * textureColor;
    } else if(drawMode == 2) {
        vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        fragColor = color * vec4(1.0, 1.0, 1.0, textureColor.w);
    }
}
