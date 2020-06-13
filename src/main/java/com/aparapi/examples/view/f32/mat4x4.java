package com.aparapi.examples.view.f32;

public class mat4x4 {
    public int id;

    protected mat4x4(int id) {
        this.id = id;
    }

    public mat4x4(float x0y0, float x1y0, float x2y0, float x3y0,
           float x0y1, float x1y1, float x2y1, float x3y1,
           float x0y2, float x1y2, float x2y2, float x3y2,
           float x0y3, float x1y3, float x2y3, float x3y3) {
        this(F32Mat4.createMat4(x0y0, x1y0, x2y0, x3y0,
                x0y1, x1y1, x2y1, x3y1,
                x0y2, x1y2, x2y2, x3y2,
                x0y3, x1y3, x2y3, x3y3));
    }

}
