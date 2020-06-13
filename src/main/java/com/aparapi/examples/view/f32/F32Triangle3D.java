package com.aparapi.examples.view.f32;

public class F32Triangle3D {
    static final int SIZE = 4;
    static final int MAX = 1600;
    static final int V0 = 0;
    static final int V1 = 1;
    static final int V2 = 2;
    static final int RGB = 3;

    public static class Pool {
        public final int max;
        public int count = 0;
        public final int entries[];
        Pool(int max) {
            this.max = max;
            this.entries = new int[max * SIZE];
        }
    }
    public static Pool pool = new Pool(1600);


    static int fillTriangle3D(int i, int v0, int v1, int v2, int rgb) {
        i *= SIZE;
        pool.entries[i + V0] = v0;
        pool.entries[i + V1] = v1;
        pool.entries[i + V2] = v2;
        pool.entries[i + RGB] = rgb;
        return i;
    }

    public static int createTriangle3D(int v0, int v1, int v2, int rgb) {
        fillTriangle3D(pool.count, v0, v1, v2, rgb);
        return pool.count++;
    }

    static String asString(int i) {
        i *= SIZE;
        return F32Vec3.asString(pool.entries[i + V0]) + " -> " + F32Vec3.asString(pool.entries[i + V1]) + " -> " + F32Vec3.asString(pool.entries[i + V2]) + " =" + String.format("0x%8x", pool.entries[i + RGB]);
    }

    public static int mulMat4(int i, int m4) {
        i *= SIZE;
        return createTriangle3D(F32Vec3.mulMat4(pool.entries[i + V0], m4), F32Vec3.mulMat4(pool.entries[i + V1], m4), F32Vec3.mulMat4(pool.entries[i + V2], m4), pool.entries[i + RGB]);
    }

    public static int addVec3(int i, int v3) {
        i *= SIZE;
        return createTriangle3D(F32Vec3.addVec3(pool.entries[i + V0], v3), F32Vec3.addVec3(pool.entries[i + V1], v3), F32Vec3.addVec3(pool.entries[i + V2], v3), pool.entries[i + RGB]);
    }

    public static int mulScaler(int i, float s) {
        i *= SIZE;
        return createTriangle3D(F32Vec3.mulScaler(pool.entries[i + V0], s), F32Vec3.mulScaler(pool.entries[i + V1], s), F32Vec3.mulScaler(pool.entries[i + V2], s), pool.entries[i + RGB]);
    }

    public static int addScaler(int i, float s) {
        i *= SIZE;
        return createTriangle3D(F32Vec3.addScaler(pool.entries[i + V0], s), F32Vec3.addScaler(pool.entries[i + V1], s), F32Vec3.addScaler(pool.entries[i + V2], s), pool.entries[i + RGB]);
    }

    public static int getCentre(int i){
        // the average of all the vertices
        return F32Vec3.divScaler(getVectorSum(i), 3);
    }

    public static int getVectorSum(int i){
        // the sum of all the vertices
        return F32Vec3.addVec3(F32Vec3.addVec3(getV0(i), getV1(i)), getV2(i));
    }


    public static int getV0(int i) {
        i *= SIZE;
        return F32Triangle3D.pool.entries[i + F32Triangle3D.V0];
    }

    public static int getV1(int i) {
        i *= SIZE;
        return F32Triangle3D.pool.entries[i + F32Triangle3D.V1];
    }

    public static int getV2(int i) {
        i *= SIZE;
        return F32Triangle3D.pool.entries[i + F32Triangle3D.V2];
    }

    public static int getRGB(int i) {
        i *= SIZE;
        return F32Triangle3D.pool.entries[i + F32Triangle3D.RGB];
    }


   // public static int createI32Triangle2D(int i, int rgb, float normal) {

  //  }

    public static int normal(int i) {

        int v0 = F32Triangle3D.getV0(i);
        int v1 = F32Triangle3D.getV1(i);
        int v2 = F32Triangle3D.getV2(i);

        int line1Vec3 = F32Vec3.subVec3(v1, v0);
        int line2Vec3 = F32Vec3.subVec3(v2, v0);

        return F32Vec3.crossProd(line1Vec3, line2Vec3);
    }

    public static int normalSumOfSquares(int i) {
        int normalVec3 = normal(i);
        return F32Vec3.divScaler(normalVec3,  F32Vec3.sumOfSquares(normalVec3));
    }


}
