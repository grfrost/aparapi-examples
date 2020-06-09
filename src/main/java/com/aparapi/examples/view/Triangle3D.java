package com.aparapi.examples.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Triangle3D {
    static final int SIZE = 4;
    static final int MAX = 400;
    static final int V0 = 0;
    static final int V1 = 1;
    static final int V2 = 2;
    static final int RGB = 3;

    static int count = 0;
    static int entries[] = new int[MAX * SIZE];

    static int fillTriangle3D(int i, int v0, int v1, int v2, int rgb) {
        i *= SIZE;
        entries[i + V0] = v0;
        entries[i + V1] = v1;
        entries[i + V2] = v2;
        entries[i + RGB] = rgb;
        return i;
    }

    static int createTriangle3D(int v0, int v1, int v2, int rgb) {
        fillTriangle3D(count, v0, v1, v2, rgb);
        return count++;
    }

    static String asString(int i) {
        i *= SIZE;
        return Vec3.asString(entries[i + V0]) + " -> " + Vec3.asString(entries[i + V1]) + " -> " + Vec3.asString(entries[i + V2]) + " =" + String.format("0x%8x", entries[i + RGB]);
    }

    static int mulMat4(int i, int m4) {
        i *= SIZE;
        return createTriangle3D(Vec3.mulMat4(entries[i + V0], m4), Vec3.mulMat4(entries[i + V1], m4), Vec3.mulMat4(entries[i + V2], m4), entries[i + RGB]);
    }

    static int addVec3(int i, int v3) {
        i *= SIZE;
        return createTriangle3D(Vec3.addVec3(entries[i + V0], v3), Vec3.addVec3(entries[i + V1], v3), Vec3.addVec3(entries[i + V2], v3), entries[i + RGB]);
    }

    static int mulScaler(int i, float s) {
        i *= SIZE;
        return createTriangle3D(Vec3.mulScaler(entries[i + V0], s), Vec3.mulScaler(entries[i + V1], s), Vec3.mulScaler(entries[i + V2], s), entries[i + RGB]);
    }
    static int addScaler(int i, float s) {
        i *= SIZE;
        return createTriangle3D(Vec3.addScaler(entries[i + V0], s), Vec3.addScaler(entries[i + V1], s), Vec3.addScaler(entries[i + V2], s), entries[i + RGB]);
    }

    static int quad(int v0, int v1, int v2, int v3, int col) {
  /*
       v0-----v1
        |\    |
        | \   |
        |  \  |
        |   \ |
        |    \|
       v3-----v2
   */

        createTriangle3D(v0, v1, v2, col);

        return createTriangle3D(v0, v2, v3, col) - 1;
    }

    /*
               a-----------d
              /|          /|
             / |         / |
           h------------g  |
           |   |        |  |
           |   b--------|--c
           |  /         | /
           | /          |/
           e------------f

     */


    static void cube(
        float x,
        float y,
        float z,
        float s){
        int a = Vec3.createVec3(x-(s*.5f), y-(s*.5f), z-(s*.5f));  //000  000 111 111
        int b = Vec3.createVec3(x-(s*.5f), y+(s*.5f), z-(s*.5f));  //010  010 101 101
        int c = Vec3.createVec3(x+(s*.5f), y+(s*.5f), z-(s*.5f));  //110  011 001 100
        int d = Vec3.createVec3(x+(s*.5f), y-(s*.5f), z-(s*.5f));  //100  001 011 110
        int e = Vec3.createVec3(x-(s*.5f), y+(s*.5f), z+(s*.5f));  //011  110 100 001
        int f = Vec3.createVec3(x+(s*.5f), y+(s*.5f), z+(s*.5f));  //111  111 000 000
        int g = Vec3.createVec3(x+(s*.5f), y-(s*.5f), z+(s*.5f));  //101  101 010 010
        int h = Vec3.createVec3(x-(s*.5f), y-(s*.5f), z+(s*.5f));  //001  100 110 011
        quad(a, b, c, d, 0xff0000); //front
        quad(b, e, f, c, 0x0000ff); //top
        quad(d, c, f, g, 0xffff00); //right
        quad(h, e, b, a, 0xffffff); //left
        quad(g, f, e, h, 0x00ff00);//back
        quad(g, h, a, d, 0xffa500);//bottom
    }


    static Pattern vpattern = Pattern.compile("^ *v *([0-9.e]+) *([0-9.e]+) *([0-9.e]+) *$");
    static Pattern fpattern = Pattern.compile("^ *f *([0-9]+) *([0-9]+) *([0-9]+) *$");
    static void load(File f){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            final int MAX_VERT = 500;
            int verticesCount=1;
            int vertices[] =  new int[MAX_VERT];
            int facesCount = 1;
            for (String line = reader.readLine(); line != null; line = reader.readLine()){
                Matcher matcher = vpattern.matcher(line);
                if (matcher.matches()){
                    vertices[verticesCount++] = Vec3.createVec3( Float.parseFloat(matcher.group(1)), Float.parseFloat(matcher.group(2)), Float.parseFloat(matcher.group(3)));
                }else{
                    matcher = fpattern.matcher(line);
                    if (matcher.matches()){
                        createTriangle3D( Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), 0xFF000>>(facesCount++));
                    }else{
                        System.out.println("Skipping "+line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /*


http://paulbourke.net/dataformats/obj/

     */


    static void cubeoctahedron(
            float x,
            float y,
            float z,
            float s){

        int v1 = Vec3.createVec3(x-(s*.30631559f), y-(s*.20791225f), z+(s*.12760004f));
        int v2 = Vec3.createVec3(x-(s*.12671047f), y-(s*.20791227f), z+(s*.30720518f));
        int v3 =Vec3.createVec3( x-(s*.12671045f), y-(s*.38751736f), z+(s*.12760002f));
        int v4 = Vec3.createVec3(x-(s*.30631556f), y-(s*.20791227f) ,z+(s*.48681026f));
        int v5 = Vec3.createVec3(x-(s*.48592068f), y-(s*.20791225f), z+(s*.30720514f));
        int v6 = Vec3.createVec3(x-(s*.30631556f) ,y-(s*.56712254f) ,z+(s*.48681026f));
        int v7 = Vec3.createVec3(x-(s*.12671047f), y-(s*.56712254f) ,z+(s*.30720512f));
        int v8 = Vec3.createVec3(x-(s*.12671042f), y-(s*.3875174f) ,z+(s*.48681026f));
        int v9 = Vec3.createVec3(x-(s*.48592068f), y-(s*.38751736f), z+(s*.1276f));
        int v10 = Vec3.createVec3(x -(s*.30631556f),y -(s*.56712254f) ,z+(s*.1276f));
        int v11 = Vec3.createVec3(x-(s*.48592068f) ,y-(s*.56712254f), z+(s*.30720512f));
        int v12= Vec3.createVec3( x-(s*.48592068f), y-(s*.38751743f) ,z+(s*.4868103f));



        createTriangle3D(  v1,  v2,  v3,  0xff0000);
        createTriangle3D(  v4,  v2,  v5,  0x7f8000);
        createTriangle3D(  v5,  v2,  v1,  0x3fc000);
        createTriangle3D(  v6,  v7,  v8,  0x1fe000);
        createTriangle3D(  v9,  v10,  v11,0x0ff000);
        createTriangle3D(  v8,  v2,  v4,  0x07f800);
        createTriangle3D(  v5,  v1,  v9,  0x03fc00);
        createTriangle3D(  v3,  v7,  v10, 0x01fe00);
        createTriangle3D(  v8,  v7,  v2,  0x00ff00);
        createTriangle3D(  v2,  v7,  v3,  0x007f80);
        createTriangle3D(  v8,  v4,  v6,  0x003fc0);
        createTriangle3D(  v6,  v4,  v12, 0x001fe0);
        createTriangle3D(  v11,  v12, v9, 0x000ff0);
        createTriangle3D(  v9,  v12,  v5, 0x0007f8);
        createTriangle3D(  v7,  v6,  v10, 0x0003fc);
        createTriangle3D(  v6,  v11, v10, 0x0001fe);
        createTriangle3D(  v1,  v3,  v9,  0x0000ff);
        createTriangle3D(  v9,  v3,  v10, 0x00007f);
        createTriangle3D(  v12,  v4,  v5, 0x00003f);
        createTriangle3D(  v6,  v12, v11, 0x00001f);
        

    }


    public static int getV0(int i) {
        i *= SIZE;
        return Triangle3D.entries[i + Triangle3D.V0];
    }

    public static int getV1(int i) {
        i *= SIZE;
        return Triangle3D.entries[i + Triangle3D.V1];
    }

    public static int getV2(int i) {
        i *= SIZE;
        return Triangle3D.entries[i + Triangle3D.V2];
    }

    public static int getRGB(int i) {
        i *= SIZE;
        return Triangle3D.entries[i + Triangle3D.RGB];
    }


    public static int createTriangle2D(int i, int rgb, float normal) {
        int v0 = Triangle3D.getV0(i);
        int v1 = Triangle3D.getV1(i);
        int v2 = Triangle3D.getV2(i);
        return Triangle2D.createTriangle(Vec3.getX(v0), Vec3.getY(v0), Vec3.getX(v1), Vec3.getY(v1), Vec3.getX(v2), Vec3.getY(v2), rgb, normal);
    }
    public static int createNonVecTriangle2D(int i, int rgb) {
        int v0 = Triangle3D.getV0(i);
        int v1 = Triangle3D.getV1(i);
        int v2 = Triangle3D.getV2(i);
        return NonVecTriangle2D.createTriangle(Vec3.getX(v0), Vec3.getY(v0), Vec3.getX(v1), Vec3.getY(v1), Vec3.getX(v2), Vec3.getY(v2), rgb);
    }
}
