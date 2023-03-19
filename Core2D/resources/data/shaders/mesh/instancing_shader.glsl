#ifdef VERTEX
    #version 330 core

    layout (location = 0) in vec2 positionAttribute;
    layout (location = 1) in vec2 texCoords[4];
    layout (location = 5) in mat4 modelMatrix;
    layout (location = 9) in vec4 color;

    out vec2 vs_textureCoords;
    out vec4 col;

    uniform mat4 projectionMatrix;
    uniform mat4 cameraMatrix;

    uniform int isUIInstancing;

    void main()
    {
        col = color;

        vs_textureCoords = texCoords[gl_VertexID];

        if(isUIInstancing == 0) {
            gl_Position = projectionMatrix * cameraMatrix * modelMatrix * vec4(positionAttribute, 0.0, 1.0);
        } else {
            gl_Position = projectionMatrix * modelMatrix * vec4(positionAttribute, 0.0, 1.0);
        }
    }
#endif

#ifdef FRAGMENT
    #version 330 core

    precision mediump float;

    out mediump vec4 fragColor;

    uniform sampler2D sampler;

    in vec2 vs_textureCoords;

    in vec4 col;

    void main()
    {
        #if defined(FLIP_TEXTURES_Y) && FLIP_TEXTURES_Y == 1
            vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y));
        #else
            vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, vs_textureCoords.y));
        #endif

        //if(col.w <= 0.3) discard;
        fragColor = col * textureColor;
    }
#endif