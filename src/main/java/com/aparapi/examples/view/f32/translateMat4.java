package com.aparapi.examples.view.f32;

public class translateMat4 extends mat4 {
    public translateMat4(float x, float y, float z) {
        super(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                x, y, z, 1f

        );
    }

    public translateMat4(float v) {
        this(v,v,v);
    }
}
