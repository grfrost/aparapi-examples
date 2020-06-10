package com.aparapi.examples.view;

public class I32Vec2 {
    static final int SIZE = 2;
    static final int MAX = 200;
    static final int X = 0;
    static final int Y = 1;

    static int count = 0;
    static int entries[] = new int[MAX * SIZE];

    static int createVec2(int x, int y) {
        entries[count * SIZE + X] = x;
        entries[count * SIZE + Y] = y;
        return count++;
    }


    static int mulScaler(int i, int s) {
        i *= SIZE;
        return createVec2(entries[i + X] * s, entries[i + Y] * s);
    }

    static int addScaler(int i, int s) {
        i *= SIZE;
        return createVec2(entries[i + X] + s, entries[i + Y] + s);
    }

    static int divScaler(int i, int s) {
        i *= SIZE;
        return createVec2(entries[i + X] / s, entries[i + Y] / s);
    }

    static int addVec2(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec2(entries[lhs + X] + entries[rhs + X], entries[lhs + Y] + entries[rhs + Y]);
    }

    static int subVec2(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec2(entries[lhs + X] - entries[rhs + X], entries[lhs + Y] - entries[rhs + Y]);
    }


    static float dotProd(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return entries[lhs + X] * entries[rhs + X] + entries[lhs + Y] * entries[rhs + Y];
    }

    static String asString(int i) {
        i *= SIZE;
        return entries[i + X] + "," + entries[i + Y];
    }
}
