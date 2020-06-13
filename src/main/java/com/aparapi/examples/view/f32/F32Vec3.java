package com.aparapi.examples.view.f32;

public class F32Vec3 {
    static final int SIZE = 3;

    static final int X = 0;
    static final int Y = 1;
    static final int Z = 2;
    public static class Pool {
        public final int max;
        public int count = 0;
        public final float entries[];
        Pool(int max) {
            this.max = max;
            this.entries = new float[max * SIZE];
        }
    }
    public static Pool pool = new Pool(1600);

    public static int createVec3(float x, float y, float z) {
        pool.entries[pool.count * SIZE + X] = x;
        pool.entries[pool.count * SIZE + Y] = y;
        pool.entries[pool.count * SIZE + Z] = z;
        return pool.count++;
    }


    // return another vec3 after multiplying by m4
    // we pad this vec3 to vec 4 with '1' as w
    // we normalize the result
    static int mulMat4(int i, int m4) {
        i *= SIZE;
        m4 *= F32Mat4.SIZE;
        int o = createVec3(
                pool.entries[i + X] * F32Mat4.pool.entries[m4 + F32Mat4.X0Y0] + pool.entries[i + Y] * F32Mat4.pool.entries[m4 + F32Mat4.X0Y1] + pool.entries[i + Z] * F32Mat4.pool.entries[m4 + F32Mat4.X0Y2] + 1f * F32Mat4.pool.entries[m4 + F32Mat4.X0Y3],
                pool.entries[i + X] * F32Mat4.pool.entries[m4 + F32Mat4.X1Y0] + pool.entries[i + Y] * F32Mat4.pool.entries[m4 + F32Mat4.X1Y1] + pool.entries[i + Z] * F32Mat4.pool.entries[m4 + F32Mat4.X1Y2] + 1f * F32Mat4.pool.entries[m4 + F32Mat4.X1Y3],
                pool.entries[i + X] * F32Mat4.pool.entries[m4 + F32Mat4.X2Y0] + pool.entries[i + Y] * F32Mat4.pool.entries[m4 + F32Mat4.X2Y1] + pool.entries[i + Z] * F32Mat4.pool.entries[m4 + F32Mat4.X2Y2] + 1f * F32Mat4.pool.entries[m4 + F32Mat4.X2Y3]
        );

        float w = pool.entries[i + X] * F32Mat4.pool.entries[m4 + F32Mat4.X3Y0] + pool.entries[i + Y] * F32Mat4.pool.entries[m4 + F32Mat4.X3Y1] + pool.entries[i + Z] * F32Mat4.pool.entries[m4 + F32Mat4.X3Y2] + 1 * F32Mat4.pool.entries[m4 + F32Mat4.X3Y3];
        if (w != 0.0) {
            o = F32Vec3.divScaler(o, w);
        }
        return o;
    }

    static int mulScaler(int i, float s) {
        i *= SIZE;
        return createVec3(pool.entries[i + X] * s, pool.entries[i + Y] * s, pool.entries[i + Z] * s);
    }

    static int addScaler(int i, float s) {
        i *= SIZE;
        return createVec3(pool.entries[i + X] + s, pool.entries[i + Y] + s, pool.entries[i + Z] + s);
    }

    static int divScaler(int i, float s) {
        i *= SIZE;
        return createVec3(pool.entries[i + X] / s, pool.entries[i + Y] / s, pool.entries[i + Z] / s);
    }

    public static int addVec3(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec3(pool.entries[lhs + X] + pool.entries[rhs + X], pool.entries[lhs + Y] + pool.entries[rhs + Y], pool.entries[lhs + Z] + pool.entries[rhs + Z]);
    }

    public static int subVec3(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec3(pool.entries[lhs + X] - pool.entries[rhs + X], pool.entries[lhs + Y] - pool.entries[rhs + Y], pool.entries[lhs + Z] - pool.entries[rhs + Z]);
    }
    public static int mulVec3(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec3(pool.entries[lhs + X] * pool.entries[rhs + X], pool.entries[lhs + Y] * pool.entries[rhs + Y], pool.entries[lhs + Z] * pool.entries[rhs + Z]);
    }
    static int divVec3(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec3(pool.entries[lhs + X] / pool.entries[rhs + X], pool.entries[lhs + Y] / pool.entries[rhs + Y], pool.entries[lhs + Z] / pool.entries[rhs + Z]);
    }


    static float sumOfSquares(int i) {
        i *= SIZE;
        return pool.entries[i + X] * pool.entries[i + X] + pool.entries[i + Y] * pool.entries[i + Y] + pool.entries[i + Z] * pool.entries[i + Z];
    }
    public static float sumOf(int i) {
        i *= SIZE;
        return pool.entries[i + X]  + pool.entries[i + Y] + pool.entries[i + Z] ;
    }

    static float hypot(int i) {
        return (float) Math.sqrt(sumOfSquares(i));
    }

    static int crossProd(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;
        return createVec3(
                pool.entries[lhs + Y] * pool.entries[rhs + Z] - pool.entries[lhs + Z] * pool.entries[rhs + X],
                pool.entries[lhs + Z] * pool.entries[rhs + X] - pool.entries[lhs + X] * pool.entries[rhs + Z],
                pool.entries[lhs + X] * pool.entries[rhs + Y] - pool.entries[lhs + Y] * pool.entries[rhs + X]);

    }
    static float dotProdAsScaler(int lhs, int rhs) {
        lhs *= SIZE;
        rhs *= SIZE;

       return pool.entries[lhs + X] * pool.entries[rhs + X] + pool.entries[lhs + Y] * pool.entries[rhs + Y] +
               pool.entries[lhs + Z] * pool.entries[rhs + Z];

    }

    static String asString(int i) {
        i *= SIZE;
        return pool.entries[i + X] + "," + pool.entries[i + Y] + "," + pool.entries[i + Z];
    }

    public static float getX(int i) {
        i *= SIZE;
        return pool.entries[i + X];
    }

    public static float getY(int i) {
        i *= SIZE;
        return pool.entries[i + Y];
    }

    public static float getZ(int i) {
        i *= SIZE;
        return pool.entries[i + Z];
    }
}
