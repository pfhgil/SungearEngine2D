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

    uniform float programTime;

    uniform vec4 color;

    uniform int resolutionY;
    uniform int resolutionX;

    in vec2 vs_textureCoords;

    vec4 line0 = vec4(0.0, 0.55, 1.0, 0.56);
    vec4 line1 = vec4(0.0, 0.3, 1.0, 0.9);
    vec4 line2 = vec4(0.0, 0.8, 1.0, 0.6);
    vec4 line3 = vec4(0.0, 0.15, 1.0, 0.35);

    float getXp(vec4 point, vec2 uv)
    {
        vec2 v1 = vec2(point.z, point.w) - vec2(point.x, point.y);
        vec2 v2 = vec2(point.z, point.w) - uv;
        return v1.x * v2.y - v1.y * v2.x;
    }

    vec4 getColor(vec2 uv, float xp, float lineThickness, vec4 inColor) {
        if(uv.y < xp + lineThickness && uv.y > xp - lineThickness) {
            vec4 pixel1 = texture(sampler, vec2(uv.x, uv.y + lineThickness / 100.0));
            vec4 pixel2 = texture(sampler, vec2(uv.x, uv.y - lineThickness / 100.0));
            return vec4(
            mix(pixel1.x, pixel2.x, 0.1),
            mix(pixel1.y, pixel2.y, 0.1),
            mix(pixel1.z, pixel2.z, 0.1),
            mix(pixel1.w, pixel2.w, 0.1)
            );

        } else {
            return inColor;
        }
    }
//

    void main()
    {
        vec2 uv = vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y)/vec2(resolutionX, resolutionY);

        vec4 tex0 = texture(sampler, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));
        vec4 tex1 = texture(sampler0, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));
        vec4 tex2 = texture(sampler1, vec2(vs_textureCoords.x, 1.0f - vs_textureCoords.y));

        vec2 resUv = vec2(uv);

        //line0 += 0.005 * iTime;
        line1 -= 0.009 * programTime;
        line2 += 0.005 * programTime;
        line3 -= 0.009 * programTime;

        float offset = min(0.002 * programTime, 0.02);

        float xp0 = getXp(line0, uv);
        float xp1 = getXp(line1, uv);
        float xp2 = getXp(line2, uv);
        float xp3 = getXp(line3, uv);

        if(xp0 >= 0.0) {
            resUv.x += offset;
        }

        if(xp1 <= 0.0) {
            resUv.x -= offset;
        }

        if(xp2 >= 0.0) {
            resUv.x += offset;
        }

        if(xp3 >= 0.0) {
            resUv.x -= offset;
        }

        vec4 texColor = texture(sampler, resUv);

        float lineThickness = 1.5;

        texColor = getColor(resUv, resolutionY * xp0, lineThickness, texColor);
        texColor = getColor(resUv, resolutionY * xp1, lineThickness, texColor);
        texColor = getColor(resUv, resolutionY * xp2, lineThickness, texColor);
        texColor = getColor(resUv, resolutionY * xp3, lineThickness, texColor);


        //fragColor = color * (tex0 * sin(programTime / 4.0) + tex1 * cos(programTime / 4.0) + tex2 / 2.0);
        //fragColor = color * texture(sampler0, vec2(tex0.x + sin(programTime) * 0.1, tex0.y));

        fragColor = texColor;
    }
#endif
