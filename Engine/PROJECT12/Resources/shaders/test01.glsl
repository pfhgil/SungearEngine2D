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

    uniform sampler2D sampler0;

    uniform sampler2D sampler1;

    uniform vec4 color;

    in vec2 vs_textureCoords;

    void main()
    {
        vec4 tex0 = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));
        vec4 tex1 = texture(sampler0, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));
        vec4 tex2 = texture(sampler1, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        fragColor = color * (tex0 / 2.0 + tex1 / 2.0 + tex2 / 2.0);
    }
#endif