#version 400

precision mediump float;

out mediump vec4 fragColor;

uniform sampler2D sampler;

in vec2 vs_textureCoords;

in vec4 col;

const float GLYPH_WIDTH = 0.5;
const float GLYPH_EDGE_WIDTH = 0.225;

void main()
{
    // нахожу расстояние от центра символа, до текущей текстурной координаты (хранится в альфа компоненте)
    float distance = 1.0 - texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y)).a;

    // вычисляю результат альфа пикселя для сглаживания
    float pixelAlphaComponent = 1.0 - smoothstep(GLYPH_WIDTH, GLYPH_WIDTH + GLYPH_EDGE_WIDTH, distance);

    vec3 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y)).rgb;

    fragColor = vec4(col.rgb, col.a * pixelAlphaComponent);
}
