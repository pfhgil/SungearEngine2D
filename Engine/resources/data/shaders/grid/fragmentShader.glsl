#version 330 core

precision highp float;

out highp vec4 fragColor;

// режим отрисовки
// 0 - без текстуры
// 1 - обычный (с накладыванием текстуры)
// 2 - для pick object (текстура используется. от нее берется только альфа)
uniform int drawMode;
uniform sampler2D sampler;

uniform vec2 cameraScale;

uniform mediump vec4 color;

in vec2 vs_textureCoords;

void main()
{
    if(drawMode == 0) {
        fragColor = color;
    } else if(drawMode == 1) {
        vec2 texCoord = vec2(vs_textureCoords);
        texCoord /= cameraScale;

        vec4 resultCol = vec4(1.0, 1.0, 1.0, 0.0);

        float lineWidth = 0.0005;
        float lineDivisor = 0.005;

        vec2 normalizedCoord = texCoord - ((lineDivisor + lineWidth) * vec2(int(texCoord.x / (lineDivisor + lineWidth)), int(texCoord.y / (lineDivisor + lineWidth))));

        if(normalizedCoord.y > (lineDivisor - lineWidth)) {
            float a = 0.0;
            if(normalizedCoord.y < lineDivisor) {
                a = normalizedCoord.y / lineDivisor;
            } else {
                a = lineDivisor / normalizedCoord.y;
            }

            float res = 1.0 - a;
            resultCol = vec4(res, res, res, 1);
        }
        if(normalizedCoord.x > (lineDivisor - lineWidth)) {
            float a = 0.0;
            if(normalizedCoord.x < lineDivisor) {
                a = normalizedCoord.x / lineDivisor;
            } else {
                a = lineDivisor / normalizedCoord.x;
            }

            float res = 1.0 - a;
            resultCol = vec4(res, res, res, 1);
        }

        fragColor = resultCol;
    } else if(drawMode == 2) {
        vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        fragColor = color * vec4(1.0, 1.0, 1.0, textureColor.w);
    }
}
