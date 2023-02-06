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

    uniform vec4 color;

    in vec2 vs_textureCoords;

    // сейчас мы передадим её
    uniform float time;

    void main()
    {
        float step = 0.01 + sin(time) * 0.05;
        //float step = 0.01;

        vec4 tex0 = texture(sampler, vec2(vs_textureCoords.x + step, 1.0 - vs_textureCoords.y));
        vec4 tex1 = texture(sampler, vec2(vs_textureCoords.x - step, 1.0 - vs_textureCoords.y));
        vec4 tex2 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y + step));
        vec4 tex3 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y - step));
        vec4 tex4 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y));
        //vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        // ВСЕ РА-БО-ТА-ЕТ!!!!!!!!!!!!!!!!!!!!!ё32ЦЙУЦУ
        //fragColor = color * textureColor;
        //fragColor = vec4(color.x, color.y * sin(time), color.z * cos(time), color.w) * textureColor;
        fragColor = (tex0 + tex1 + tex2 + tex3 + tex4) / 4.0f;
    }
#endif
