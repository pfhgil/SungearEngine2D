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

    in vec2 vs_textureCoords;

    void main()
    {
        #if FLIP_TEXTURE_Y == TRUE
            vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y));
        #else
            vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, vs_textureCoords.y));
        #endif

        fragColor = color * textureColor;
    }
#endif