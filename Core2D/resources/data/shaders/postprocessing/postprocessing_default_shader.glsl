#ifdef VERTEX
    // id аттрибута = 0. позиции вершин. входной параметр
    layout (location = 0) in vec2 positionAttribute;
    // id аттрибута = 2. текстурная координата вершины. входной параметр
    layout (location = 1) in vec2 textureCoordsAttribute;

    out vec2 vs_textureCoords;

    void main()
    {
        vs_textureCoords = textureCoordsAttribute;

        gl_Position = vec4(positionAttribute, 0.0, 1.0);
    }
#endif

#ifdef FRAGMENT
    out vec4 fragColor;

    uniform sampler2D sampler;

    uniform vec4 color;

    uniform float time;

    in vec2 vs_textureCoords;

    void main()
    {
        /*
        vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        fragColor = textureColor;
        */

        float step = 0.01 + sin(time) * 0.01 - cos(time) * 0.01;
        //float step = 0.01;

        vec4 tex0 = texture(sampler, vec2(vs_textureCoords.x + step, 1.0 - vs_textureCoords.y));
        vec4 tex1 = texture(sampler, vec2(vs_textureCoords.x - step, 1.0 - vs_textureCoords.y));
        vec4 tex2 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y + step));
        vec4 tex3 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y - step));
        vec4 tex4 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y));
        //vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        //fragColor = color * textureColor;
        //fragColor = vec4(color.x, color.y * sin(time), color.z * cos(time), color.w) * textureColor;
        fragColor = (tex0 + tex1 + tex2 + tex3 + tex4) / 4.0f;
    }
#endif