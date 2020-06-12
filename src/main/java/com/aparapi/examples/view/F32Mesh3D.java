package com.aparapi.examples.view;

import java.util.regex.Matcher;

class F32Mesh3D {
    String name;
    int vecbase;
    int tribase;
    int sum;
    int center;

    F32Mesh3D(String name){
        this.name = name;
        this.vecbase = F32Vec3.count;
        this.tribase = F32Triangle3D.count;
    }
    final int SIZE = 1;
    final int MAX = 400;

    int count = 0;
     int entries[] = new int[MAX * SIZE];


    F32Mesh3D tri(int v0, int v1, int v2, int rgb) {
        int newFace = F32Triangle3D.createTriangle3D(v0, v1, v2, rgb);
        entries[count++]= newFace;
        int newFaceCentre =  F32Triangle3D.getCentre(newFace);
        if (count == 1 ){
            sum =newFaceCentre;
        }else{
            sum = F32Vec3.addVec3(sum , newFaceCentre);

            if (count >2) {
                center = F32Vec3.divScaler(sum, count);
                int newFaceCenterNormal = F32Triangle3D.normal(newFaceCentre);
            }
        }

        return this;
    }

    void fin(){
        cube(F32Vec3.getX(center),F32Vec3.getY(center), F32Vec3.getY(center), .1f );
    }

    F32Mesh3D quad(int v0, int v1, int v2, int v3, int col) {
  /*
       v0-----v1
        |\    |
        | \   |
        |  \  |
        |   \ |
        |    \|
       v3-----v2
   */

        tri(v0, v1, v2, col);

        tri(v0, v2, v3, col);
        return this;
    }
    F32Mesh3D pent(int v0, int v1, int v2, int v3, int v4, int col) {
  /*
       v0-----v1
       |\    | \
       | \   |  \
       |  \  |   v2
       |   \ |  /
       |    \| /
       v4-----v3
   */

        tri(v0, v1, v3, col);
        tri(v1, v2, v3, col);
        tri(v0, v3, v4, col);
        return this;
    }

    F32Mesh3D hex(int v0, int v1, int v2, int v3, int v4, int v5, int col) {
  /*
       v0-----v1
      / |\    | \
     /  | \   |  \
    v5  |  \  |   v2
     \  |   \ |  /
      \ |    \| /
       v4-----v3
   */

        tri(v0, v1, v3, col);
        tri(v1, v2, v3, col);
        tri(v0, v3, v4, col);
        tri(v0, v4, v5, col);
        return this;
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


    F32Mesh3D cube(
            float x,
            float y,
            float z,
            float s) {
        int a = vec3(x - (s * .5f), y - (s * .5f), z - (s * .5f));  //000  000 111 111
        int b = vec3(x - (s * .5f), y + (s * .5f), z - (s * .5f));  //010  010 101 101
        int c = vec3(x + (s * .5f), y + (s * .5f), z - (s * .5f));  //110  011 001 100
        int d = vec3(x + (s * .5f), y - (s * .5f), z - (s * .5f));  //100  001 011 110
        int e = vec3(x - (s * .5f), y + (s * .5f), z + (s * .5f));  //011  110 100 001
        int f = vec3(x + (s * .5f), y + (s * .5f), z + (s * .5f));  //111  111 000 000
        int g = vec3(x + (s * .5f), y - (s * .5f), z + (s * .5f));  //101  101 010 010
        int h = vec3(x - (s * .5f), y - (s * .5f), z + (s * .5f));  //001  100 110 011
        quad(a, b, c, d, 0xff0000); //front
        quad(b, e, f, c, 0x0000ff); //top
        quad(d, c, f, g, 0xffff00); //right
        quad(h, e, b, a, 0xffffff); //left
        quad(g, f, e, h, 0x00ff00);//back
        quad(g, h, a, d, 0xffa500);//bottom
        return this;
    }





    /*


http://paulbourke.net/dataformats/obj/

     */


    F32Mesh3D cubeoctahedron(
            float x,
            float y,
            float z,
            float s) {

        int v1 = vec3(x - (s * .30631559f), y - (s * .20791225f), z + (s * .12760004f));
        int v2 = vec3(x - (s * .12671047f), y - (s * .20791227f), z + (s * .30720518f));
        int v3 = vec3(x - (s * .12671045f), y - (s * .38751736f), z + (s * .12760002f));
        int v4 = vec3(x - (s * .30631556f), y - (s * .20791227f), z + (s * .48681026f));
        int v5 = vec3(x - (s * .48592068f), y - (s * .20791225f), z + (s * .30720514f));
        int v6 = vec3(x - (s * .30631556f), y - (s * .56712254f), z + (s * .48681026f));
        int v7 = vec3(x - (s * .12671047f), y - (s * .56712254f), z + (s * .30720512f));
        int v8 = vec3(x - (s * .12671042f), y - (s * .3875174f), z + (s * .48681026f));
        int v9 = vec3(x - (s * .48592068f), y - (s * .38751736f), z + (s * .1276f));
        int v10 = vec3(x - (s * .30631556f), y - (s * .56712254f), z + (s * .1276f));
        int v11 = vec3(x - (s * .48592068f), y - (s * .56712254f), z + (s * .30720512f));
        int v12 = vec3(x - (s * .48592068f), y - (s * .38751743f), z + (s * .4868103f));


        tri(v1, v2, v3, 0xff0000);
        tri(v4, v2, v5, 0x7f8000);
        tri(v5, v2, v1, 0x3fc000);
        tri(v6, v7, v8, 0x1fe000);
        tri(v9, v10, v11, 0x0ff000);
        tri(v8, v2, v4, 0x07f800);
        tri(v5, v1, v9, 0x03fc00);
        tri(v3, v7, v10, 0x01fe00);
        tri(v8, v7, v2, 0x00ff00);
        tri(v2, v7, v3, 0x007f80);
        tri(v8, v4, v6, 0x003fc0);
        tri(v6, v4, v12, 0x001fe0);
        tri(v11, v12, v9, 0x000ff0);
        tri(v9, v12, v5, 0x0007f8);
        tri(v7, v6, v10, 0x0003fc);
        tri(v6, v11, v10, 0x0001fe);
        tri(v1, v3, v9, 0x0000ff);
        tri(v9, v3, v10, 0x00007f);
        tri(v12, v4, v5, 0x00003f);
        tri(v6, v12, v11, 0x00001f);
        return this;
    }


     public F32Mesh3D rubric(float s) {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    cube(x * .5f, y * .5f, z * .5f, s);
                }
            }
        }
        return this;
    }

    public int vec3(float x, float y, float z) {
        return F32Vec3.createVec3(x,y, z);
    }


}
