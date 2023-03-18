#version 330 core

precision mediump float;

out mediump vec4 fragColor;

uniform sampler2D sampler;

uniform mediump vec4 color;

// минимальное значение проверки альфа для заливки
uniform float minCheckAlphaFilling;
// максимальное значение проверки альфа для заливки
uniform float maxCheckAlphaFilling;

// текущее значение прогресс бара
uniform float currentValue;
// максимальное значение прогресс бара
uniform float maxValue;

// цвет заливки
uniform vec4 fillingColor;

in vec2 vs_textureCoords;

void main()
{
    vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y));

    if(textureColor.w >= minCheckAlphaFilling && textureColor.w <= maxCheckAlphaFilling) {
        float limit = currentValue / maxValue;

        if(vs_textureCoords.x < limit) {
            textureColor = fillingColor;
        }
    }

    fragColor = color * textureColor;
}