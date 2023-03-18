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

    uniform float time;

    // Код для размытия с помощью GLSL
    vec4 blur(sampler2D image, vec2 uv, float radius)
    {
        vec2 off = vec2(1.0, 0.0);
        vec3 color = vec3(0.0, 0.0, 0.0);

        float l = 0.0;
        for (float i = -radius / 2.0; i <= radius / 2.0; i += 1.0)
        {
            for(float k = -radius / 2.0; k <= radius / 2.0; k += 1.0)
            {
                vec2 newUv = vec2(uv.x + i, uv.y + k);
                if(pow(i, 2.0) + pow(k, 2.0) <= radius * radius)
                {
                    l += 1.0;
                    color += texture2D(image, uv + vec2(i, k)).rgb;
                }
               // color += texture2D(image, uv + vec2(i, k)).rgb;
            }
        }

        //return vec4(color / (radius * radius), 1.0);
        return vec4(color / l, 1.0);
    }

    void main()
    {
        /*
        float step = 0.01 + sin(time) * 0.01 - cos(time) * 0.01;

        vec4 tex0 = texture(sampler, vec2(vs_textureCoords.x + step, 1.0 - vs_textureCoords.y));
        vec4 tex1 = texture(sampler, vec2(vs_textureCoords.x - step, 1.0 - vs_textureCoords.y));
        vec4 tex2 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y + step));
        vec4 tex3 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y - step));
        vec4 tex4 = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y));


        vec4 currentTextureColor = (tex0 + tex1 + tex2 + tex3 + tex4) / 4.0f;
        */

        vec4 blr = blur(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y), 1.0);
        //
        fragColor = color * blr;
        //fsdfdsfdcfdf
    }
#endif
