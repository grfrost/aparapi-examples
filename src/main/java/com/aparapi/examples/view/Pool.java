package com.aparapi.examples.view;

public class Pool {

    public static final int MAXF = 1000;
    public static final int MAXI = 1000;
    public int countI = 0;
    public int countF =0;
    public float[] floats = new float[MAXF];
    public int[] ints = new int[MAXI];
    int i(int i){
        ints[countI]= i;
        return countI++;
    }
    int f(float f){
        floats[countF]= f;
        return countF++;
    }
    int vec2(int vec2){
       return i(vec2);
    }
    int vec3(int vec3){
       return i(vec3);
    }
    int tri2D(int tri2D){
        return i(tri2D);
    }
    int tri3D(int tri3D){
        return i(tri3D);
    }
    int xy(int x, int y){
        int first = i(x);
        i(y);
        return first;
    }
    int xy(float x, float y){
        int first = f(x);
        f(y);
        return first;
    }
    int xyz(float x, float y, float z){
        int first = xy(x, y);
        f(z);
        return first;
    }
}
