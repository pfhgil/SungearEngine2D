#ifdef VERTEX
    // id аттрибута = 0. позиции вершин. входной параметр
    layout (location = 0) in vec2 positionAttribute;
    // id аттрибута = 2. текстурная координата вершины. входной параметр
    layout (location = 1) in vec2 textureCoordsAttribute;

    uniform mat4 mvpMatrix;

    void main()
    {
        gl_Position = mvpMatrix * vec4(positionAttribute, 0.0, 1.0);
    }
#endif

#ifdef FRAGMENT
    out vec4 fragColor;

    uniform vec4 color;

    void main()
    {
        fragColor = color;
    }
#endif