package com.aparapi.examples.view;

public class Vec2 {
    static final int SIZE = 3;
    static final int MAX_VEC2 = 100;
    static final int X = 0;
    static final int Y = 1;

    static int vec2Count = 0;
    static float vec2s[] = new float[MAX_VEC2 * SIZE];

    static int createVec2(float x, float y) {
        vec2s[vec2Count * SIZE + X] = x;
        vec2s[vec2Count * SIZE + Y] = y;
        return vec2Count++;
    }


    static int mulScaler(int i, float s) {
        i *= SIZE;
        return createVec2(vec2s[i + X] * s, vec2s[i + Y] * s);
    }

    static int addScaler(int i, float s) {
        i *= SIZE;
        return createVec2(vec2s[i + X] + s, vec2s[i + Y] + s);
    }

    static int divScaler(int i, float s) {
        i *= SIZE;
        return createVec2(vec2s[i + X] / s, vec2s[i + Y] / s);
    }

    static int addVec2(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec2(vec2s[lhs + X] + vec2s[rhs + X], vec2s[lhs + Y] + vec2s[rhs + Y]);
    }

    static int subVec2(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec2(vec2s[lhs + X] - vec2s[rhs + X], vec2s[lhs + Y] - vec2s[rhs + Y]);
    }


    static float dotProd(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return vec2s[lhs + X] * vec2s[rhs + X] + vec2s[lhs + Y] * vec2s[rhs + Y];
    }

    static String asString(int i) {
        i *= SIZE;
        return vec2s[i + X] + "," + vec2s[i + Y];
    }
}
