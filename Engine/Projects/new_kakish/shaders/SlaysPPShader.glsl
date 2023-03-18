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

    uniform vec4 color;

    in vec2 vs_textureCoords;

    vec4 blur(float r, float st, float coeff)
    {
        //
        vec4 ret = vec4(0.0);

        float steps = 0.0;

        for(float i = -r; i < r; i += st)
        {
            for(float k = -r; k < r; k += st)
            {
                ret += texture(sampler, vec2(vs_textureCoords.x + i, 1.0 - vs_textureCoords.y + k));

                steps += 1.0;
            }
        }

        return ret / (steps * coeff);
    }

    void main()
    {
        //vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y));
        float st = 0.002;

        vec4 tex0 = texture(sampler, vec2(vs_textureCoords.x + st, 1.0 - vs_textureCoords.y));
        vec4 tex1 = texture(sampler, vec2(vs_textureCoords.x - st, 1.0 - vs_textureCoords.y));
        vec4 tex2 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y + st));
        vec4 tex3 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y - st));
        vec4 tex4 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y));

        //fragColor = color * (tex0 + tex1 + tex2 + tex3 + tex4) / 4.5;
        fragColor = color * blur(0.002, 0.001, 0.8);
    }
#endif

