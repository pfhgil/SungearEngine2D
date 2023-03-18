// ATTENTION: do not write the shader version!
#ifdef VERTEX
    layout (location = 0) in vec2 positionAttribute;
    layout (location = 1) in vec2 textureCoordsAttribute;

    uniform mat4 mvpMatrix;

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
    uniform sampler2D sampler2;

    uniform vec4 color;

    in vec2 vs_textureCoords;

    void main()
    {
        vec4 tex0 = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));
        vec4 tex1 = texture(sampler2, vec2(vs_textureCoords.x,  vs_textureCoords.y));

        fragColor = vec4(1.0, 0.0, 0.0, 1.0);
    }
#endif
