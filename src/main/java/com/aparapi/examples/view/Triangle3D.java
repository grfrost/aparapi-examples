package com.aparapi.examples.view;

class Triangle3D {
    static final int SIZE = 4;
    static final int MAX = 100;
    static final int V0 = 0;
    static final int V1 = 1;
    static final int V2 = 2;
    static final int RGB = 3;

    static int count = 0;
    static int entries[] = new int[MAX * SIZE];

    static int fillTriangle3D(int i, int v0, int v1, int v2, int rgb) {
        i *= SIZE;
        entries[i + V0] = v0;
        entries[i + V1] = v1;
        entries[i + V2] = v2;
        entries[i + RGB] = rgb;
        return i;
    }

    static int createTriangle3D(int v0, int v1, int v2, int rgb) {
        fillTriangle3D(count, v0, v1, v2, rgb);
        return count++;
    }

    static String asString(int i) {
        i *= SIZE;
        return Vec3.asString(entries[i + V0]) + " -> " + Vec3.asString(entries[i + V1]) + " -> " + Vec3.asString(entries[i + V2]) + " =" + String.format("0x%8x", entries[i + RGB]);
    }

    static int mulMat4(int i, int m4) {
        i *= SIZE;
        return createTriangle3D(Vec3.mulMat4(entries[i + V0], m4), Vec3.mulMat4(entries[i + V1], m4), Vec3.mulMat4(entries[i + V2], m4), entries[i + RGB]);
    }

    static int addVec3(int i, int v3) {
        i *= SIZE;
        return createTriangle3D(Vec3.addVec3(entries[i + V0], v3), Vec3.addVec3(entries[i + V1], v3), Vec3.addVec3(entries[i + V2], v3), entries[i + RGB]);
    }

    static int mulScaler(int i, float s) {
        i *= SIZE;
        return createTriangle3D(Vec3.mulScaler(entries[i + V0], s), Vec3.mulScaler(entries[i + V1], s), Vec3.mulScaler(entries[i + V2], s), entries[i + RGB]);
    }
    static int addScaler(int i, float s) {
        i *= SIZE;
        return createTriangle3D(Vec3.addScaler(entries[i + V0], s), Vec3.addScaler(entries[i + V1], s), Vec3.addScaler(entries[i + V2], s), entries[i + RGB]);
    }

    static int quad(int v0, int v1, int v2, int v3, int col) {
  /*
       v0-----v1
        |\    |
        | \   |
        |  \  |
        |   \ |
        |    \|
       v3-----v2
   */

        createTriangle3D(v0, v1, v2, col);
        Main.ViewFrame.dump(count - 1);

        return createTriangle3D(v0, v2, v3, col) - 1;
    }

    /*
                -----------
              /|          /|
             / |         / |
            ------------   |
           |   |        |  |
           |    --------|--
           |  /         | /
           | /          |/
            ------------

     */


    static void cube() {
        quad(Vec3.createVec3(-.5f, -.5f, -.5f), Vec3.createVec3(-.5f, .5f, -.5f), Vec3.createVec3(.5f, .5f, -.5f), Vec3.createVec3(.5f, -.5f, -.5f), 0xff0000); //front
        quad(Vec3.createVec3(-.5f, .5f, -.5f), Vec3.createVec3(-.5f, .5f, .5f), Vec3.createVec3(.5f, .5f, .5f), Vec3.createVec3(.5f, .5f, -.5f), 0x0000ff); //top
        quad(Vec3.createVec3(.5f, -.5f, -.5f), Vec3.createVec3(.5f, .5f, -.5f), Vec3.createVec3(.5f, .5f, .5f), Vec3.createVec3(.5f, -.5f, .5f), 0xffff00); //right
        quad(Vec3.createVec3(-.5f, -.5f, .5f), Vec3.createVec3(-.5f, .5f, .5f), Vec3.createVec3(-.5f, .5f, -.5f), Vec3.createVec3(-.5f, -.5f, -.5f), 0xffffff); //left
        quad(Vec3.createVec3(.5f, -.5f, .5f), Vec3.createVec3(.5f, .5f, .5f), Vec3.createVec3(-.5f, .5f, .5f), Vec3.createVec3(-.5f, -.5f, .5f), 0x00ff00);//back
        quad(Vec3.createVec3(.5f, -.5f, .5f), Vec3.createVec3(-.5f, -.5f, .5f), Vec3.createVec3(-.5f, -.5f, -.5f), Vec3.createVec3(.5f, -.5f, -.5f), 0xffa500);//bottom
    }


    public static int getV0(int i) {
        i *= SIZE;
        return Triangle3D.entries[i + Triangle3D.V0];
    }

    public static int getV1(int i) {
        i *= SIZE;
        return Triangle3D.entries[i + Triangle3D.V1];
    }

    public static int getV2(int i) {
        i *= SIZE;
        return Triangle3D.entries[i + Triangle3D.V2];
    }

    public static int getRGB(int i) {
        i *= SIZE;
        return Triangle3D.entries[i + Triangle3D.RGB];
    }
}
