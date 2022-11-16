#ifdef VERTEX
    #version 330 core

    layout (location = 0) in vec2 positionAttribute[2];
    layout (location = 2) in mat4 modelMatrix;
    layout (location = 6) in vec4 color;

    out vec4 col;

    uniform mat4 projectionMatrix;
    uniform mat4 cameraMatrix;

    uniform int isUIInstancing;

    void main()
    {
        col = color;

        if (isUIInstancing == 0) {
            gl_Position = projectionMatrix * cameraMatrix * modelMatrix * vec4(positionAttribute[gl_VertexID], 0.0, 1.0);
        } else {
            gl_Position = projectionMatrix * modelMatrix * vec4(positionAttribute[gl_VertexID], 0.0, 1.0);
        }
    }
#endif

#ifdef FRAGMENT
    #version 330 core

    precision mediump float;

    out mediump vec4 fragColor;

    in vec4 col;

    void main()
    {
        fragColor = col;
    }
#endif
