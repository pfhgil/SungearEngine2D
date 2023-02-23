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

    //
    uniform float programTime;

    uniform float s;

    // ----------
    vec2 canvasSize = vec2(1000.0, 1000.0);
    float wave_offset = 0.0;
    float wave_radius = 0.0;

    void main()
    {
        wave_offset = programTime / 5.0;
        wave_radius = programTime / 5.0;

        vec2 resClickPos = vec2(500.0,500.0) / canvasSize;
        vec2 distanceVec = resClickPos - vs_textureCoords;
        distanceVec = distanceVec * vec2(canvasSize.x / canvasSize.y, 1.0);
        float dist = sqrt(distanceVec.x * distanceVec.x + distanceVec.y * distanceVec.y);

        float sin_factor = sin(dist * 20.0 - programTime * 10.0) * 0.05;
        float discard_factor = clamp(wave_radius - abs(wave_offset - dist), 0.0, 1.0);
        //f

        vec2 offset = normalize(distanceVec) * sin_factor * discard_factor;
        vec2 resultUv = vs_textureCoords + offset;

        vec4 textureColor = texture(sampler, vec2(resultUv.x, 1.0 - resultUv.y));

        fragColor = color * textureColor;
    }
#endif
