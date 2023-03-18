#ifdef VERTEX
    // id аттрибута = 0. позиции вершин. входной параметр
    layout (location = 0) in vec2 positionAttribute;
    // id аттрибута = 2. текстурная координата вершины. входной параметр
    layout (location = 1) in vec2 textureCoordsAttribute;

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

    // ray tracing ------------------------------
    uniform mediump vec2 iResolution;
    uniform mediump vec2 iMouse;

    uniform mediump vec2 lightPos;

    uniform mediump vec3 cameraPosition;

    uniform mediump vec2 u_seed1;
    uniform mediump vec2 u_seed2;

    uniform highp float samplerPart;

    const float MAX_DIST = 99999.0;

    uvec4 R_STATE;

    uint TausStep(uint z, int S1, int S2, int S3, uint M)
    {
        uint b = (((z << S1) ^ z) >> S2);
        return (((z & M) << S3) ^ b);
    }

    uint LCGStep(uint z, uint A, uint C)
    {
        return (A * z + C);
    }

    vec2 hash22(vec2 p)
    {
        p += u_seed1.x;
        vec3 p3 = fract(vec3(p.xyx) * vec3(.1031, .1030, .0973));
        p3 += dot(p3, p3.yzx+33.33);
        return fract((p3.xx+p3.yz)*p3.zy);
    }

    float random()
    {
        R_STATE.x = TausStep(R_STATE.x, 13, 19, 12, uint(4294967294));
        R_STATE.y = TausStep(R_STATE.y, 2, 25, 4, uint(4294967288));
        R_STATE.z = TausStep(R_STATE.z, 3, 11, 17, uint(4294967280));
        R_STATE.w = LCGStep(R_STATE.w, uint(1664525), uint(1013904223));
        return 2.3283064365387e-10 * float((R_STATE.x ^ R_STATE.y ^ R_STATE.z ^ R_STATE.w));
    }

    vec3 randomOnSphere() {
        vec3 rand = vec3(random(), random(), random());
        float theta = rand.x * 2.0 * 3.14159265;
        float v = rand.y;
        float phi = acos(2.0 * v - 1.0);
        float r = pow(rand.z, 1.0 / 3.0);
        float x = r * sin(phi) * cos(theta);
        float y = r * sin(phi) * sin(theta);
        float z = r * cos(phi);
        return vec3(x, y, z);
    }

    mat2 rotate(float a)
    {
        float s = sin(a);
        float c = cos(a);

        return mat2(c, -s, s, c);
    }

    vec2 checkSphereIntersect(vec3 cameraPos, vec3 rayDirection, float radius)
    {
        float b = dot(cameraPos, rayDirection);
        float c = dot(cameraPos, cameraPos) - radius * radius;
        float h = b * b - c;
        if(h < 0.0) return vec2(-1.0);
        h = sqrt(h);
        return vec2(-b - h, -b + h);
    }


    vec2 boxIntersection(in vec3 ro, in vec3 rd, in vec3 rad, out vec3 oN)  {
        vec3 m = 1.0 / rd;
        vec3 n = m * ro;
        vec3 k = abs(m) * rad;
        vec3 t1 = -n - k;
        vec3 t2 = -n + k;
        float tN = max(max(t1.x, t1.y), t1.z);
        float tF = min(min(t2.x, t2.y), t2.z);
        if(tN > tF || tF < 0.0) return vec2(-1.0);
        oN = -sign(rd) * step(t1.yzx, t1.xyz) * step(t1.zxy, t1.xyz);
        return vec2(tN, tF);
    }

    float plaIntersect(in vec3 ro, in vec3 rd, in vec4 p) {
        return -(dot(ro, p.xyz) + p.w) / dot(rd, p.xyz);
    }

    vec3 getSky(vec3 rayDirection, vec3 lightDirection)
    {
        vec3 col = vec3(0.3, 0.6, 1.0);
        vec3 sun = vec3(0.95, 0.9, 1.0);
        sun *= max(0.0, pow(dot(rayDirection, lightDirection), 256.0));
        col *= max(0.0, dot(lightDirection, vec3(0.0, 0.0, -1.0)));
        return clamp(sun + col * 0.01, 0.0, 1.0);
    }

    vec4 castRay(inout vec3 rayOriginal, inout vec3 rayDirection, vec3 lightDirection)
    {
        vec4 col;
        vec2 minIt = vec2(MAX_DIST);
        vec2 it;
        vec3 n;
        mat2x4 spheres[2];
        spheres[0][0] = vec4(-1.0, 0.0, -0.01, 1.0);
        spheres[1][0] = vec4(0.0, 3.0, -0.01, 1.0);
        spheres[0][1] = vec4(0.0, 0.0, 1.0, 1.0);
        spheres[1][1] = vec4(1.0, 0.0, 0.5, 1.0);

        for(int i = 0; i < spheres.length(); i++) {
            it = checkSphereIntersect(rayOriginal - spheres[i][0].xyz, rayDirection, spheres[i][0].w);
            if(it.x > 0.0 && it.x < minIt.x) {
                minIt = it;
                vec3 itPos = rayOriginal + rayDirection * it.x;
                n = normalize(itPos - spheres[i][0].xyz);
                col = spheres[i][1];
            }
        }

        vec3 boxN;
        vec3 boxPos = vec3(3.0, 1.0, -0.001);
        it = boxIntersection(rayOriginal - boxPos, rayDirection, vec3(1.0), boxN);
        if(it.x > 0.0 && it.x < minIt.x) {
            minIt = it;
            n = boxN;
            col = vec4(0.9, 0.2, 0.2, -0.5);
        }

        vec3 planeNormal = vec3(0.0, 0.0, 1.0);
        it = vec2(plaIntersect(rayOriginal, rayDirection, vec4(planeNormal, 1.0)));
        if(it.x > 0.0 && it.x < minIt.x) {
            minIt = it;
            n = planeNormal;
            col = vec4(0.5, 0.25, 0.1, 0.01);
        }

        if(minIt.x == MAX_DIST) return vec4(getSky(rayDirection, lightDirection), -2.0);
        if(col.a == -2.0) return col;
        vec3 reflected = normalize(reflect(rayDirection, -n));

        if(col.a < 0.0) {
            float fresnel = 1.0 - abs(dot(-rayDirection, n));
            if(random() - 0.1 < fresnel * fresnel) {
                rayDirection = reflected;
                return col;
            }
            rayOriginal += rayDirection * (minIt.y + 0.001);
            rayDirection = refract(rayDirection, n, 1.0 / (1.0 - col.a));
            return col;
        }

        vec3 itPos = rayOriginal + rayDirection * it.x;
        vec3 r = randomOnSphere();
        vec3 diffuse = normalize(r * dot(r, n));
        rayOriginal += rayDirection * (minIt.x - 0.01);
        rayDirection = normalize(mix(diffuse, reflected, col.a));

        return col;
    }

    vec3 traceRay(vec3 rayOriginal, vec3 rayDirection, vec3 lightDirection)
    {
        vec3 col = vec3(1.0);
        for(float i = 0.0; i < 4.0; i += 1.0) {
            vec4 refCol = castRay(rayOriginal, rayDirection, lightDirection);
            col *= refCol.rgb;
            if(refCol.a == -2.0) return col;
        }

        return vec3(0.0);
    }

    void main()
    {
        //vec2 uv = vs_textureCoords;
        //vec2 uv = 2.0 * vec2(vs_textureCoords.x, 1.0 - vs_textureCoords.y) / iResolution.xy - 1.0;
        //uv.x *= iResolution.x / iResolution.y;
        vec2 uv = (vs_textureCoords - 0.5) * iResolution / iResolution.y;
        vec2 uvRes = hash22(uv + 1.0) * iResolution + iResolution;
        R_STATE.x = uint(u_seed1.x + uvRes.x);
        R_STATE.y = uint(u_seed1.y + uvRes.x);
        R_STATE.z = uint(u_seed2.x + uvRes.y);
        R_STATE.w = uint(u_seed2.y + uvRes.y);

        vec3 cameraPos = vec3(cameraPosition.x, cameraPosition.y, cameraPosition.z);
        vec3 lightPos = normalize(vec3(-0.5, -0.75, -1.0));

        vec3 rayDirection = normalize(vec3(1.0, uv));
        rayDirection.zx *= rotate(-iMouse.y / 1000.0);
        rayDirection.xy *= rotate(-iMouse.x / 1000.0);

        vec3 col = vec3(0.0);
        int samples = 128;
        for(int i = 0; i < samples; i++) {
            col += traceRay(cameraPos, rayDirection, lightPos);
        }

        col /= samples;
        float white = 20.0;
        col *= white * 16.0;
        col = (col * (1.0 + col / white / white)) / (1.0 + col);

        vec3 texCol = texture(sampler, vs_textureCoords).rgb;
        //col = mix(texCol, col, samplerPart);

        fragColor = vec4(col, 1.0);
    }

#endif