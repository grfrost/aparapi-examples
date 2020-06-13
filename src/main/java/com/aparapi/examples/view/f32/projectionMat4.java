package com.aparapi.examples.view.f32;


public class projectionMat4 extends mat4 {

    public projectionMat4(float width, float height, float nearZ, float farZ, float fieldOfViewDeg){
        super(F32Mat4.createProjectionMatrix(width, height,  nearZ, farZ,  fieldOfViewDeg));

    }
}
