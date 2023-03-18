// ATTENTION: do not write the shader version!
#ifdef VERTEX
    layout (location = 0) in vec2 positionAttribute;
    layout (location = 1) in vec2 textureCoordsAttribute;

    uniform mat4 mvpMatrix;

    out vec2 vs_textureCoords;

    void main()
    {
        vs_textureCoords = textureCoordsAttribute;

        gl_Position = mvpMatrix * vec4(positionAttribute, 0.0, 1.0);
    }
#endif

#ifdef FRAGMENT
    out vec4 fragColor;

    uniform sampler2D sampler;
    uniform sampler2D noise;

    uniform vec4 color;

    in vec2 vs_textureCoords;

    // сейчас мы передадим её
    uniform float time;

    //vec4 blur()

    vec4 u_mix(vec4 v0, vec4 v1, float factor)
    {
        return vec4(
        mix(v0.x, v1.x, factor),
        mix(v0.y, v1.y, factor),
        mix(v0.z, v1.z, factor),
        mix(v0.w, v1.w, factor)
        );
    }

    void main()
    {
        float step = 0.01 + sin(time) * 0.05;
        //float step = 0.01;

        float steps = 0.0;
        float mst = 0.04;
        float st = 0.03;
        vec4 outputTex = vec4(0.0);
        for(float i = 0.0; i < mst; i += st) {
            for(float k = 0.0; k < mst; k += st) {
                //
                outputTex += texture(sampler, vec2(vs_textureCoords.x + i, 1.0 - vs_textureCoords.y + k)) / texture(noise, vec2(vs_textureCoords.x + sin(time), 1.0 - vs_textureCoords.y + cos(time)));
                steps += 1.0;
            }
        }

        outputTex /= steps;

        //vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        // ВСЕ РА-БО-ТА-ЕТ!!!!!!!!!!!!!!!!!!!!!ё32ЦЙУЦУ
        //fragColor = color * textureColor;
        //fragColor = vec4(color.x, color.y * sin(time), color.z * cos(time), color.w) * textureColor;
        fragColor = outputTex;
    }
#endif
