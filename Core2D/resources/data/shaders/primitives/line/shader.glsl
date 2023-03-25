#ifdef VERTEX
    // id аттрибута = 0. позиции вершин. входной параметр
    layout (location = 0) in vec2 positionAttribute;

    uniform mat4 mvpMatrix;

    uniform vec3 offset;

    uniform vec3 verticesPositions[2];

    flat out int vertexID;

    void main()
    {
        vertexID = gl_VertexID;
        gl_Position = mvpMatrix * vec4(offset + verticesPositions[vertexID], 1.0);
    }
#endif

#ifdef FRAGMENT
    out vec4 fragColor;

    uniform vec4 color;

    flat in int vertexID;

    void main()
    {
        fragColor = color;
    }
#endif