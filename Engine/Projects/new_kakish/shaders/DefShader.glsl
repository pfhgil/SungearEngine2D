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

    void main()
    {
        #if defined(FLIP_TEXTURES_Y) && FLIP_TEXTURES_Y == 1
            vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y));
        #else
            vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, vs_textureCoords.y));
        #endif

        fragColor = color * textureColor;
    }
#endif

