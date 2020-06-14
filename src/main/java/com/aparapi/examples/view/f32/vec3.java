package com.aparapi.examples.view.f32;

public class vec3{
    int id;
    vec3(int id){
        this.id = id;
    }

    public vec3(float x, float y, float z) {
        this(F32Vec3.createVec3(x,y,z));
    }

    public vec3 sub(vec3 v) {
        return new vec3(F32Vec3.subVec3(id, v.id));
    }
    public vec3 add(vec3 v) {
        return new vec3(F32Vec3.addVec3(id, v.id));
    }
    public vec3 mul(vec3 v) {
        return new vec3(F32Vec3.mulVec3(id, v.id));
    }

    public float dotProd(vec3 v){
        return F32Vec3.dotProd(id, v.id);
    }
    public float sumOf(){
        return F32Vec3.sumOf(id);
    }

    public float x() {
        return F32Vec3.getX(id);
    }
    public float y() {
        return F32Vec3.getY(id);
    }
    public float z() {
        return F32Vec3.getZ(id);
    }
}
