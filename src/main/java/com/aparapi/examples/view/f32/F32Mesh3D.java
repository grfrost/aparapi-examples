package com.aparapi.examples.view.f32;

public class F32Mesh3D {
    String name;

    int triSum;
    int triCenter;

    public F32Mesh3D(String name){
        this.name = name;
    }
    final int SIZE = 1;
    final int MAX = 400;
    public final static int ABINORMAL = -1;

    public int triCount = 0;
    public int triEntries[] = new int[MAX * SIZE];
    public int normalEntries[] = new int[MAX *SIZE];
    public int vecCount = 0;
    public int vecEntries[] = new int[MAX * SIZE];

    public F32Mesh3D tri(int v0, int v1, int v2, int rgb, int vN) {
        int newTri = F32Triangle3D.createTriangle3D(v0, v1, v2, rgb);
        normalEntries[triCount] = vN;
        triEntries[triCount++]= newTri;
        int newTriCentreVec3 =  F32Triangle3D.getCentre(newTri);
        if (vN != ABINORMAL) {
            int normFromTriVec3 = F32Triangle3D.normal(newTri);

            float normDotProd = F32Vec3.dotProdAsScaler(normFromTriVec3, vN);

          //  float normaDotProdNormalized = F32Vec3.sumOfSquares(normDotProd);
            System.out.print("norms "+F32Vec3.asString(vN)+ " vs "+F32Vec3.asString(normFromTriVec3));
            System.out.println("   normDotProd "+normDotProd);
            if (normDotProd < 0) {
                F32Triangle3D.fillTriangle3D(newTri, v0, v2, v1, rgb);
            }
           // System.out.println("   normDotProdNormalized "+normaDotProdNormalized);
        }
        if (triCount == 1 ){
            triSum =newTriCentreVec3;
        }else{
            triSum = F32Vec3.addVec3(triSum, newTriCentreVec3);
            if (triCount >2) {
                triCenter = F32Vec3.divScaler(triSum, triCount+1);
                int newFaceCenterNormal = F32Triangle3D.normal(newTriCentreVec3);
            }
        }

        return this;
    }
    F32Mesh3D tri(int v0, int v1, int v2, int rgb) {
        return tri(v0, v1, v2, rgb, ABINORMAL);

    }

    public void fin(){
     //   cube(F32Vec3.getX(triCenter),F32Vec3.getY(triCenter), F32Vec3.getZ(triCenter), .1f );
      //  vecCenter = F32Vec3.divScaler(vecSum, 3*(vecCount+3));
      //  cube(F32Vec3.getX(vecCenter),F32Vec3.getY(vecCenter), F32Vec3.getZ(vecCenter), .02f );
    }

    public F32Mesh3D quad(int v0, int v1, int v2, int v3, int rgb, int vN) {
  /*
       v0-----v1
        |\    |
        | \   |
        |  \  |
        |   \ |
        |    \|
       v3-----v2
   */

        tri(v0, v1, v2, rgb, vN);
        tri(v0, v2, v3, rgb, vN);
        return this;
    }
    F32Mesh3D quad(int v0, int v1, int v2, int v3, int rgb) {
        return quad(v0, v1, v2, v3, rgb, ABINORMAL);
    }

    public F32Mesh3D pent(int v0, int v1, int v2, int v3, int v4, int rgb, int vN) {
  /*
       v0-----v1
       |\    | \
       | \   |  \
       |  \  |   v2
       |   \ |  /
       |    \| /
       v4-----v3
   */

        tri(v0, v1, v3, rgb, vN);
        tri(v1, v2, v3, rgb, vN);
        tri(v0, v3, v4, rgb, vN);
        return this;
    }
    F32Mesh3D pent(int v0, int v1, int v2, int v3, int v4, int rgb){
        return pent(v0, v1, v2, v3, v4, rgb, ABINORMAL);
    }
    public F32Mesh3D hex(int v0, int v1, int v2, int v3, int v4, int v5, int rgb, int vN) {
  /*
       v0-----v1
      / |\    | \
     /  | \   |  \
    v5  |  \  |   v2
     \  |   \ |  /
      \ |    \| /
       v4-----v3
   */

        tri(v0, v1, v3, rgb, vN);
        tri(v1, v2, v3, rgb, vN);
        tri(v0, v3, v4, rgb, vN);
        tri(v0, v4, v5, rgb, vN);
        return this;
    }
    F32Mesh3D hex(int v0, int v1, int v2, int v3, int v4, int v5, int rgb) {
        return hex(v0, v1, v2, v3, v4 ,v5, rgb, ABINORMAL);
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


    public F32Mesh3D cube(
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


    public F32Mesh3D cubeoctahedron(
            float x,
            float y,
            float z,
            float s) {

        int v1 = vec3(x + (s * .30631559f), y + (s * .20791225f), z + (s * .12760004f));
        int v2 = vec3(x + (s * .12671047f), y + (s * .20791227f), z + (s * .30720518f));
        int v3 = vec3(x + (s * .12671045f), y + (s * .38751736f), z + (s * .12760002f));
        int v4 = vec3(x + (s * .30631556f), y + (s * .20791227f), z + (s * .48681026f));
        int v5 = vec3(x + (s * .48592068f), y + (s * .20791225f), z + (s * .30720514f));
        int v6 = vec3(x + (s * .30631556f), y + (s * .56712254f), z + (s * .48681026f));
        int v7 = vec3(x + (s * .12671047f), y + (s * .56712254f), z + (s * .30720512f));
        int v8 = vec3(x + (s * .12671042f), y + (s * .3875174f), z + (s * .48681026f));
        int v9 = vec3(x + (s * .48592068f), y + (s * .38751736f), z + (s * .1276f));
        int v10 = vec3(x + (s * .30631556f), y + (s * .56712254f), z + (s * .1276f));
        int v11 = vec3(x + (s * .48592068f), y + (s * .56712254f), z + (s * .30720512f));
        int v12 = vec3(x + (s * .48592068f), y + (s * .38751743f), z + (s * .4868103f));




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
        int newVec = F32Vec3.createVec3(x,y, z);
        vecEntries[vecCount++]=newVec;
    //    if (vecCount == 1 ){
         //   vecSum =newVec;
       // }else{
      //      vecSum = F32Vec3.addVec3(vecSum, newVec);
      //  }
        return newVec;
    }


}
