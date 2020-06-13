package com.aparapi.examples.view.f32;


public class projectionMat4x4 extends mat4x4 {

    public projectionMat4x4(float width, float height, float nearZ, float farZ, float fieldOfViewDeg){
        super(F32Mat4.createProjectionMatrix(width, height,  nearZ, farZ,  fieldOfViewDeg));

    }
}
