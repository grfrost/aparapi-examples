package com.aparapi.examples.view.f32;

public class rotationMat4x4 extends mat4x4 {
    public rotationMat4x4(float thetaX, float thetaY, float thetaZ){
        super( F32Mat4.mulMat4(F32Mat4.mulMat4(F32Mat4.createRotXMat4(thetaX), F32Mat4.createRotYMat4(thetaY)),F32Mat4.createRotZMat4(thetaZ)));
    }
}
