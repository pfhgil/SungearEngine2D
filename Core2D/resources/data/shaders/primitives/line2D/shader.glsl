#ifdef VERTEX
    // id аттрибута = 0. позиции вершин. входной параметр
    layout (location = 0) in vec2 positionAttribute;

    uniform mat4 mvpMatrix;

    void main()
    {
        gl_Position = mvpMatrix * vec4(positionAttribute, 0.0, 1.0);
    }
#endif

#ifdef FRAGMENT
    precision mediump float;

    out mediump vec4 fragColor;

    uniform mediump vec4 color;

    void main()
    {
        fragColor = color;
    }
#endif