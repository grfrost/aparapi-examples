package com.aparapi.examples.view.f32;


import java.util.ArrayList;
import java.util.List;

public class tri {
    private int id;

    public tri( int id) {
        this.id = id;
    }

    public static List<tri> all() {
        List<tri> all = new ArrayList<>();
        for (int t = 0; t < F32Triangle3D.pool.count; t++) {
            all.add(new tri(t));
        }
        return all;
    }

    public tri mul(mat4 m) {
        return new tri(F32Triangle3D.mulMat4(id, m.id));
    }

    public tri add(vec3 v) {
        return new tri(F32Triangle3D.addVec3(id, v.id));

    }

    public vec3 normalSumOfSquares() {
        return new vec3(F32Triangle3D.normalSumOfSquares(id));
    }

    public vec3 normal() {
        return new vec3(F32Triangle3D.normal(id));
    }

    public vec3 v0() {
        return new vec3(F32Triangle3D.getV0(id));
    }

    public vec3 v1() {
        return new vec3(F32Triangle3D.getV1(id));
    }

    public vec3 v2() {
        return new vec3(F32Triangle3D.getV2(id));
    }

    public tri mul(float s) {
        return new tri(F32Triangle3D.mulScaler(id, s));
    }

    public tri add(float s) {
        return new tri(F32Triangle3D.addScaler(id, s));
    }

    public int rgb() {
        return F32Triangle3D.getRGB(id);
    }

    public vec3 center() {
        return new vec3(F32Triangle3D.getCentre(id));
    }
}
