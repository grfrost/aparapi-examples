package com.aparapi.examples.view;

public class Triangle2D {
    static final int SIZE = 3;
    static final int V0 = 0;
    static final int V1 = 1;
    static final int V2 = 2;
    public static int MAX = 1000;
    public static int count = 0;

    public static int[] entries = new int[MAX * SIZE];
    public static int[] colors = new int[MAX];
    public static float[] normals = new float[MAX];
    public static float side(float x, float y, float x0, float y0, float x1, float y1) {
        return (y1 - y0) * (x - x0) + (-x1 + x0) * (y - y0);
    }

    public static float side(int v, int v0, int v1) {
        v*=Vec2.SIZE;
        v0*=Vec2.SIZE;
        v1*=Vec2.SIZE;
        return (Vec2.entries[v1+Vec2.Y] - Vec2.entries[v0+Vec2.Y] * (Vec2.entries[v+Vec2.X] - Vec2.entries[v0+Vec2.X]) + (-Vec2.entries[v1+Vec2.X] + Vec2.entries[v0+Vec2.X]) * (Vec2.entries[v+Vec2.Y] - Vec2.entries[v0+Vec2.Y]));
    }

    public static boolean intriangle(float x, float y, float x0, float y0, float x1, float y1, float x2, float y2) {
        return side(x, y, x0, y0, x1, y1) >= 0 && side(x, y, x1, y1, x2, y2) >= 0 && side(x, y, x2, y2, x0, y0) >= 0;
    }
    public static boolean intriangle(int v, int v0, int v1, int v2){
        return side(v, v0, v1) >= 0 && side(v, v1, v2) >= 0 && side(v, v2, v0) >= 0;
    }

    public static boolean online(float x, float y, float x0, float y0, float x1, float y1, float deltaSquare) {
        float dxl = x1 - x0;
        float dyl = y1 - y0;
        float cross = (x - x0) * dyl - (y - y0) * dxl;
        if ((cross * cross) < deltaSquare) {
            if (dxl * dxl >= dyl * dyl)
                return dxl > 0 ? x0 <= x && x <= x1 : x1 <= x && x <= x0;
            else
                return dyl > 0 ? y0 <= y && y <= y1 : y1 <= y && y <= y0;
        } else {
            return false;
        }
    }

    static int createTriangle(float x0, float y0, float x1, float y1, float x2, float y2, int col, float normal) {
        entries[count * SIZE + V0] = Vec2.createVec2(x0,y0);
        // We need the triangle to be clock wound
        if (side(x0, y0, x1, y1, x2, y2) > 0) {
            entries[count * SIZE + V1] = Vec2.createVec2(x1,y1);
            entries[count * SIZE + V2] = Vec2.createVec2(x2,y2);
        } else {
            entries[count * SIZE + V1] = Vec2.createVec2(x2,y2);
            entries[count * SIZE + V2] = Vec2.createVec2(x1,y1);
        }
        colors[count] = col;
        normals[count] = normal;
        return count++;
    }

    static int createTriangle(int v0, int v1, int v2, int col, float normal) {
        entries[count * SIZE + V0] = v0;
        // We need the triangle to be clock wound
        if (side(v0, v1, v2) > 0) {
            entries[count * SIZE + V1] = v1;
            entries[count * SIZE + V2] = v2;
        } else {
            entries[count * SIZE + V1] = v2;
            entries[count * SIZE + V2] = v1;
        }
        colors[count] = col;
        normals[count] = normal;
        return count++;
    }
}
