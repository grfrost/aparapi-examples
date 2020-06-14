package com.aparapi.examples.view.f32;
// https://medium.com/swlh/understanding-3d-matrix-transforms-with-pixijs-c76da3f8bd8
public class scaleMat4 extends mat4 {
    public scaleMat4(float x, float y, float z) {
        super(
                        x, 0f, 0f, 0f,
                        0f, y, 0f, 0f,
                        0f, 0f, z, 0f,
                        0f, 0f, 0f, 1f

        );
    }

    public scaleMat4(float v) {
        this(v,v,v);
    }
}
