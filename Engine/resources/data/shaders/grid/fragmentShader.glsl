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
uniform float cameraScale;
uniform mat4 modelMatrix;

in vec2 vs_textureCoords;
in vec2 posAttr;
float gridScale = 0.05;

float rlog10 = log(10.);

float log10(float x) { return log(x) / rlog10; }

float getScaleMultiple(float scale){
    return pow(10., (floor(log10(scale))));
}
float distToLine(float pos, float mul){
    return pos/mul - floor(pos/mul + gridScale);
}
void main()
{
    if(drawMode == 0) {
        //dispose;
    }
    if(color == vec4(1)) {

    }
    vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));
    vec4 color = vec4(1);
    float multiplier = getScaleMultiple(cameraScale);
    vec2 fragPosition = (modelMatrix * vec4(posAttr, 1.0, 1.0)).xy;
    color -= distToLine(fragPosition.x, multiplier)<=gridScale || distToLine(fragPosition.y, multiplier)<=gridScale ?
            vec4(1) : vec4(0);
    fragColor = vec4(color.xyz, 1.0);
}
