#ifdef VERTEX
    // id аттрибута = 0. позиции вершин. входной параметр
    layout (location = 0) in vec2 positionAttribute;
    // id аттрибута = 2. текстурная координата вершины. входной параметр
    layout (location = 1) in vec2 textureCoordsAttribute;

    uniform mat4 mvpMatrix;


    out vec2 vs_textureCoords;
    out vec2 posAttr;

    void main()
    {
        vs_textureCoords = textureCoordsAttribute;
        posAttr = positionAttribute;

        gl_Position = mvpMatrix * vec4(positionAttribute, 0.0, 1.0);
    }
#endif

#ifdef FRAGMENT
    precision highp float;

    out highp vec4 fragColor;

    // режим отрисовки
    // 0 - без текстуры
    // 1 - обычный (с накладыванием текстуры)
    // 2 - для pick object (текстура используется. от нее берется только альфа)
    uniform int drawMode;
    uniform sampler2D sampler;
    uniform int level;

    uniform vec2 cameraScale;

    uniform mediump vec4 color;

    in vec2 vs_textureCoords;

    float grid(vec2 st, float res)
    {
        vec2 grid = fract(st * res);
        return (step(res, grid.x) * step(res, grid.y));
    }

    void main()
    {
        if(drawMode == 0) {
            fragColor = color;
        } else if(drawMode == 1) {
            /*
            vec2 texCoord = vec2(vs_textureCoords);
            texCoord /= cameraScale;

            vec4 resultCol = vec4(1.0, 1.0, 1.0, 0.0);

            float lineWidth = 0.0005;
            float lineDivisor = 0.005;

            vec2 normalizedCoord = texCoord - ((lineDivisor + lineWidth) * vec2(int(texCoord.x / (lineDivisor + lineWidth)), int(texCoord.y / (lineDivisor + lineWidth))));

            if(normalizedCoord.y > (lineDivisor - lineWidth)) {
                float a = 0.0;
                if(normalizedCoord.y < lineDivisor) {
                    a = normalizedCoord.y / lineDivisor;
                } else {
                    a = lineDivisor / normalizedCoord.y;
                }

                float res = 1.0 - a;
                resultCol = vec4(res, res, res, 1);
            }
            if(normalizedCoord.x > (lineDivisor - lineWidth)) {
                float a = 0.0;
                if(normalizedCoord.x < lineDivisor) {
                    a = normalizedCoord.x / lineDivisor;
                } else {
                    a = lineDivisor / normalizedCoord.x;
                }

                resultCol = vec4(res, res, res, 1);
            }
            */
            float a = 10;
            vec2 grid_uv = vs_textureCoords.xy * (1.0f / cameraScale.x * level * 100.0); // scale
            float x = grid(grid_uv, cameraScale.x * level / 100.0); // resolution

            fragColor.rgb = vec3(0.5) * x;
            fragColor.a = 1.0;
        } else if(drawMode == 2) {
            vec4 textureColor = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

            fragColor = color * vec4(1.0, 1.0, 1.0, textureColor.w);
        }
    }
#endif