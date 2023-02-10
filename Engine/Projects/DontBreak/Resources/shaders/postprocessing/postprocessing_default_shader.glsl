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
        vec4 currentTextureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        fragColor = color * currentTextureColor;
    }
#endif