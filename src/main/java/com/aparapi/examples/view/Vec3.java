package com.aparapi.examples.view;

public class Vec3 {
    static final int SIZE = 3;
    static final int MAX = 400;
    static final int X = 0;
    static final int Y = 1;
    static final int Z = 2;
    static int count = 0;
    static float entries[] = new float[MAX * SIZE];

    static int createVec3(float x, float y, float z) {
        entries[count * SIZE + X] = x;
        entries[count * SIZE + Y] = y;
        entries[count * SIZE + Z] = z;
        return count++;
    }


    // return another vec3 after multiplying by m4
    // we pad this vec3 to vec 4 with '1' as w
    // we normalize the result
    static int mulMat4(int i, int m4) {
        i *= SIZE;
        m4 *= Mat4.SIZE;
        int o = createVec3(
                entries[i + X] * Mat4.entries[m4 + Mat4.X0Y0] + entries[i + Y] * Mat4.entries[m4 + Mat4.X0Y1] + entries[i + Z] * Mat4.entries[m4 + Mat4.X0Y2] + 1f * Mat4.entries[m4 + Mat4.X0Y3],
                entries[i + X] * Mat4.entries[m4 + Mat4.X1Y0] + entries[i + Y] * Mat4.entries[m4 + Mat4.X1Y1] + entries[i + Z] * Mat4.entries[m4 + Mat4.X1Y2] + 1f * Mat4.entries[m4 + Mat4.X1Y3],
                entries[i + X] * Mat4.entries[m4 + Mat4.X2Y0] + entries[i + Y] * Mat4.entries[m4 + Mat4.X2Y1] + entries[i + Z] * Mat4.entries[m4 + Mat4.X2Y2] + 1f * Mat4.entries[m4 + Mat4.X2Y3]
        );

        float w = entries[i + X] * Mat4.entries[m4 + Mat4.X3Y0] + entries[i + Y] * Mat4.entries[m4 + Mat4.X3Y1] + entries[i + Z] * Mat4.entries[m4 + Mat4.X3Y2] + 1 * Mat4.entries[m4 + Mat4.X3Y3];
        if (w != 0.0) {
            o = Vec3.divScaler(o, w);
        }
        return o;
    }

    static int mulScaler(int i, float s) {
        i *= SIZE;
        return createVec3(entries[i + X] * s, entries[i + Y] * s, entries[i + Z] * s);
    }

    static int addScaler(int i, float s) {
        i *= SIZE;
        return createVec3(entries[i + X] + s, entries[i + Y] + s, entries[i + Z] + s);
    }

    static int divScaler(int i, float s) {
        i *= SIZE;
        return createVec3(entries[i + X] / s, entries[i + Y] / s, entries[i + Z] / s);
    }

    static int addVec3(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec3(entries[lhs + X] + entries[rhs + X], entries[lhs + Y] + entries[rhs + Y], entries[lhs + Z] + entries[rhs + Z]);
    }

    static int subVec3(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec3(entries[lhs + X] - entries[rhs + X], entries[lhs + Y] - entries[rhs + Y], entries[lhs + Z] - entries[rhs + Z]);
    }

    static float sumOfSquares(int i) {
        i *= SIZE;
        return entries[i + X] * entries[i + X] + entries[i + Y] * entries[i + Y] + entries[i + Z] * entries[i + Z];
    }

    static float hypot(int i) {
        return (float) Math.sqrt(sumOfSquares(i));
    }

    static int dotProd(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec3(
                entries[lhs + Y] * entries[rhs + Z] - entries[lhs + Z] * entries[rhs + X],
                entries[lhs + Z] * entries[rhs + X] - entries[lhs + X] * entries[rhs + Z],
                entries[lhs + X] * entries[rhs + Y] - entries[lhs + Y] * entries[rhs + X]);
    }

    static String asString(int i) {
        i *= SIZE;
        return entries[i + X] + "," + entries[i + Y] + "," + entries[i + Z];
    }

    public static float getX(int i) {
        i *= SIZE;
        return entries[i + X];
    }

    public static float getY(int i) {
        i *= SIZE;
        return entries[i + Y];
    }

    public static float getZ(int i) {
        i *= SIZE;
        return entries[i + Z];
    }
}
