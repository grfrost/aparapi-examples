package com.aparapi.examples.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class F32Triangle3D {
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
        return F32Vec3.asString(entries[i + V0]) + " -> " + F32Vec3.asString(entries[i + V1]) + " -> " + F32Vec3.asString(entries[i + V2]) + " =" + String.format("0x%8x", entries[i + RGB]);
    }

    static int mulMat4(int i, int m4) {
        i *= SIZE;
        return createTriangle3D(F32Vec3.mulMat4(entries[i + V0], m4), F32Vec3.mulMat4(entries[i + V1], m4), F32Vec3.mulMat4(entries[i + V2], m4), entries[i + RGB]);
    }

    static int addVec3(int i, int v3) {
        i *= SIZE;
        return createTriangle3D(F32Vec3.addVec3(entries[i + V0], v3), F32Vec3.addVec3(entries[i + V1], v3), F32Vec3.addVec3(entries[i + V2], v3), entries[i + RGB]);
    }

    static int mulScaler(int i, float s) {
        i *= SIZE;
        return createTriangle3D(F32Vec3.mulScaler(entries[i + V0], s), F32Vec3.mulScaler(entries[i + V1], s), F32Vec3.mulScaler(entries[i + V2], s), entries[i + RGB]);
    }

    static int addScaler(int i, float s) {
        i *= SIZE;
        return createTriangle3D(F32Vec3.addScaler(entries[i + V0], s), F32Vec3.addScaler(entries[i + V1], s), F32Vec3.addScaler(entries[i + V2], s), entries[i + RGB]);
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
    static int pent(int v0, int v1, int v2, int v3, int v4, int col) {
  /*
       v0-----v1
       |\    | \
       | \   |  \
       |  \  |   v2
       |   \ |  /
       |    \| /
       v4-----v3
   */

        createTriangle3D(v0, v1, v3, col);
        createTriangle3D(v1, v2, v3, col);

        return createTriangle3D(v0, v3, v4, col) -1;
    }

    static int hex(int v0, int v1, int v2, int v3, int v4, int v5, int col) {
  /*
       v0-----v1
      / |\    | \
     /  | \   |  \
    v5  |  \  |   v2
     \  |   \ |  /
      \ |    \| /
       v4-----v3
   */

        createTriangle3D(v0, v1, v3, col);
        createTriangle3D(v1, v2, v3, col);

        createTriangle3D(v0, v3, v4, col);
                return createTriangle3D(v0, v4, v5, col)- 1;
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
            float s) {
        int a = F32Vec3.createVec3(x - (s * .5f), y - (s * .5f), z - (s * .5f));  //000  000 111 111
        int b = F32Vec3.createVec3(x - (s * .5f), y + (s * .5f), z - (s * .5f));  //010  010 101 101
        int c = F32Vec3.createVec3(x + (s * .5f), y + (s * .5f), z - (s * .5f));  //110  011 001 100
        int d = F32Vec3.createVec3(x + (s * .5f), y - (s * .5f), z - (s * .5f));  //100  001 011 110
        int e = F32Vec3.createVec3(x - (s * .5f), y + (s * .5f), z + (s * .5f));  //011  110 100 001
        int f = F32Vec3.createVec3(x + (s * .5f), y + (s * .5f), z + (s * .5f));  //111  111 000 000
        int g = F32Vec3.createVec3(x + (s * .5f), y - (s * .5f), z + (s * .5f));  //101  101 010 010
        int h = F32Vec3.createVec3(x - (s * .5f), y - (s * .5f), z + (s * .5f));  //001  100 110 011
        quad(a, b, c, d, 0xff0000); //front
        quad(b, e, f, c, 0x0000ff); //top
        quad(d, c, f, g, 0xffff00); //right
        quad(h, e, b, a, 0xffffff); //left
        quad(g, f, e, h, 0x00ff00);//back
        quad(g, h, a, d, 0xffa500);//bottom
    }


    static Pattern vpattern = Pattern.compile("^ *v *([0-9.e]+) *([0-9.e]+) *([0-9.e]+) *$");
    static Pattern fpattern = Pattern.compile("^ *f *([0-9]+) *([0-9]+) *([0-9]+) *$");

    static void load(File f) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            final int MAX_VERT = 500;
            int verticesCount = 1;
            int vertices[] = new int[MAX_VERT];
            int facesCount = 1;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                Matcher matcher = vpattern.matcher(line);
                if (matcher.matches()) {
                    vertices[verticesCount++] = F32Vec3.createVec3(Float.parseFloat(matcher.group(1)), Float.parseFloat(matcher.group(2)), Float.parseFloat(matcher.group(3)));
                } else {
                    matcher = fpattern.matcher(line);
                    if (matcher.matches()) {
                        createTriangle3D(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), 0xFF000 >> (facesCount++));
                    } else {
                        System.out.println("Skipping " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    static Pattern remPattern = Pattern.compile("^ *REM(.*)$");
    static Pattern colonPattern = Pattern.compile("^ *(:) *$");
    static Pattern verticesPattern = Pattern.compile("^ *(vertices) *$");
    static Pattern facesPattern = Pattern.compile("^ *(faces) *$");
    static Pattern hueLigSatPattern = Pattern.compile("^ *(hue-lig-sat) *$");
    static String hexRegex = "((?:-?&[0-9a-fA-F][0-9a-fA-F])|0)";
    static String commaRegex = " *, *";
    static String hexOrColorCommaRegex = "(" + hexRegex + "|(?:(?:[a-zA-Z][a-zA-Z0-9]*)))" + commaRegex;

    static String hexCommaRegex = hexRegex + commaRegex;
    static String decRegex = "([0-9]+)";
    static String decCommaRegex = decRegex + commaRegex;
    static Pattern face6Pattern = Pattern.compile("^ *"
            + hexOrColorCommaRegex + hexCommaRegex + hexCommaRegex + hexCommaRegex
            + "6" + commaRegex + decCommaRegex + decCommaRegex + decCommaRegex + decCommaRegex + decCommaRegex + decRegex + " *$");
    static Pattern face5Pattern = Pattern.compile("^ *"
            + hexOrColorCommaRegex + hexCommaRegex + hexCommaRegex + hexCommaRegex
            + "5" + commaRegex + decCommaRegex + decCommaRegex + decCommaRegex + decCommaRegex + decRegex + " *$");
    static Pattern face4Pattern = Pattern.compile("^ *"
            + hexOrColorCommaRegex + hexCommaRegex + hexCommaRegex + hexCommaRegex
            + "4" + commaRegex + decCommaRegex + decCommaRegex + decCommaRegex + decRegex + " *$");
    static Pattern face3Pattern = Pattern.compile("^ *"
            + hexOrColorCommaRegex + hexCommaRegex + hexCommaRegex + hexCommaRegex
            + "3" + commaRegex + decCommaRegex + decCommaRegex + decRegex + " *$");
    static Pattern frontLaserVertexPattern = Pattern.compile("^ *" + hexRegex + " *$");
    static Pattern vertexPattern = Pattern.compile("^ *" + hexCommaRegex + hexCommaRegex + hexRegex + " *$");

    static Pattern vertexCountPattern = Pattern.compile("^ *" + hexCommaRegex + hexRegex + " *$");
    static Pattern namePattern = Pattern.compile("^ *([A-Za-z][0-9A-Za-z]+) *$");
    static Pattern emptyPattern = Pattern.compile("^ *$");

    static String getGroups(Matcher m) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= m.groupCount(); i++) {
            sb.append("#" + i + "{" + m.group(i) + "}");
        }
        return sb.toString();
    }

    static void showGroups(String label, Matcher m) {
        System.out.println(label + ":  " + getGroups(m));
    }

    static class vtex {
        float x, y, z;

        static float getFloat(String s) {
            float v = 0f;
            if (s.startsWith("-")) {
                v = (float) (-Integer.parseInt(s.substring(2), 16) / 128f);
            } else {
                v = (float) (Integer.parseInt(s.substring(1), 16) / 128f);
            }
            return v;
        }

        vtex(Matcher m) {
            String s = getGroups(m);
            x = getFloat(m.group(1));
            y = getFloat(m.group(2));
            z = getFloat(m.group(3));
        }

        public String toString() {
            return "{" + x + "," + y + "," + z + "}";
        }
    }

    static class normal {
        float x, y, z;

        static float getFloat(String s) {
            float v = 0f;
            if (s.startsWith("-")) {
                v = (float) (-Integer.parseInt(s.substring(2), 16) / 64f);
            } else {
                v = (float) (Integer.parseInt(s.substring(1), 16) / 64f);
            }
            return v;
        }

        normal(Matcher m) {
            String s = getGroups(m);
            x = getFloat(m.group(3));
            y = getFloat(m.group(4));
            z = getFloat(m.group(4));
        }

        public String toString() {
            return "{" + x + "," + y + "," + z + "}";
        }
    }

    enum State {AWAITING_NAME, AWAITING_LAZER, AWAITING_COUNTS, AWAITING_VERTICES, AWAITING_HUE_LIG_SAT, AWAITING_FACES}

    ;

    static void eliteload(String name) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("/home/gfrost/github/grfrost/aparapi-build/examples/github/aparapi-examples/src/main/java/com/aparapi/examples/view/Elite.txt")));

            List<vtex> vertices = new ArrayList<>();
            int vec3base = F32Vec3.count;
            State state = State.AWAITING_NAME;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                Matcher lm;
                if ((lm = remPattern.matcher(line)).matches()) {
                    // System.out.println("REM");
                } else if ((lm = emptyPattern.matcher(line)).matches()) {
                } else if ((lm = colonPattern.matcher(line)).matches()) {
                    // System.out.println("EMPTY");
                } else {
                    switch (state) {
                        case AWAITING_NAME: {
                            if ((lm = namePattern.matcher(line)).matches()) {
                                //showGroups("NAMES", lm);
                                if (lm.group(1).equals(name)) {
                                    state = State.AWAITING_LAZER;
                                  //  vertices = new ArrayList<>();
                                    System.out.println(name + "{");
                                }
                            } else {
                                System.out.println("In " + state + " skipping " + line);
                            }
                            break;
                        }
                        case AWAITING_LAZER: {
                            if ((lm = frontLaserVertexPattern.matcher(line)).matches()) {
                                state = State.AWAITING_COUNTS;
                            } else {
                                System.out.println("In " + state + " skipping " + line);
                            }
                            break;
                        }
                        case AWAITING_COUNTS: {
                            if ((lm = vertexCountPattern.matcher(line)).matches()) {
                                state = State.AWAITING_VERTICES;
                            } else {
                                System.out.println("In " + state + " skipping " + line);
                            }
                            break;
                        }
                        case AWAITING_VERTICES: {
                            if ((lm = verticesPattern.matcher(line)).matches()) {
                                state = State.AWAITING_FACES;
                            } else {
                                System.out.println("In " + state + " skipping " + line);
                            }
                            break;
                        }
                        case AWAITING_FACES: {
                            if ((lm = vertexPattern.matcher(line)).matches()) {

                                vtex v = new vtex(lm);
                               // vertices.add(v);
                                F32Vec3.createVec3(v.x,v.y, v.z);
                                System.out.println("  " + v);

                            } else if ((lm = facesPattern.matcher(line)).matches()) {
                                state = State.AWAITING_HUE_LIG_SAT;

                            } else {
                                System.out.println("In " + state + " skipping " + line);
                            }
                            break;
                        }
                        case AWAITING_HUE_LIG_SAT: {
                            if ((lm = face6Pattern.matcher(line)).matches()) {
                                showGroups("FACE6", lm);
                                String s =getGroups(lm);
                                normal n = new normal(lm);
                                int v0 = vec3base + Integer.parseInt(lm.group(6));
                                int  v1 = vec3base + Integer.parseInt(lm.group(7));
                                int  v2 = vec3base + Integer.parseInt(lm.group(8));
                                int v3 = vec3base + Integer.parseInt(lm.group(9));

                                int  v4 = vec3base + Integer.parseInt(lm.group(10));
                                int v5 = vec3base + Integer.parseInt(lm.group(11));
                                hex(v0, v1,v2, v3, v4, v5,  (n.x<0||n.y<0||n.z<0)?0xffffff:0x0f0f0f);
                            } else if ((lm = face5Pattern.matcher(line)).matches()) {
                                showGroups("FACE5", lm);
                                normal n = new normal(lm);
                                String s =getGroups(lm);
                                int v0 = vec3base + Integer.parseInt(lm.group(6));
                                int  v1 = vec3base + Integer.parseInt(lm.group(7));
                                int  v2 = vec3base + Integer.parseInt(lm.group(8));
                                int v3 = vec3base + Integer.parseInt(lm.group(9));

                                int  v4 = vec3base + Integer.parseInt(lm.group(10));

                                pent(v0, v1,v2, v3, v4, (n.x<0||n.y<0||n.z<0)?0x0000f0:0x00000f);
                            } else if ((lm = face4Pattern.matcher(line)).matches()) {
                                showGroups("FACE4", lm);
                                String s =getGroups(lm);
                                normal n = new normal(lm);
                                int v0 = vec3base + Integer.parseInt(lm.group(6));
                                int  v1 = vec3base + Integer.parseInt(lm.group(7));
                                int  v2 = vec3base + Integer.parseInt(lm.group(8));
                                int v3 = vec3base + Integer.parseInt(lm.group(9));
                                quad(v0, v1,v2, v3,  (n.x<0||n.y<0||n.z<0)?0xf00000:0x0f0000);
                            } else if ((lm = face3Pattern.matcher(line)).matches()) {
                               // showGroups("FACE3 ", lm);
                                String s =getGroups(lm);
                                normal n = new normal(lm);
                                int v0 = vec3base + Integer.parseInt(lm.group(6));
                                int  v1 = vec3base + Integer.parseInt(lm.group(7));
                                int  v2 = vec3base + Integer.parseInt(lm.group(8));
                                createTriangle3D(v0, v1, v2, (n.x<0||n.y<0||n.z<0)?0x00f000:0x000f00);
                            } else if ((lm = hueLigSatPattern.matcher(line)).matches()) {
                                System.out.println("}");
                                return;
                            } else {
                                System.out.println("In " + state + " skipping " + line);
                            }
                            break;
                        }
                        default: {

                            System.out.println("WHAt " + line);

                        }
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
            float s) {

        int v1 = F32Vec3.createVec3(x - (s * .30631559f), y - (s * .20791225f), z + (s * .12760004f));
        int v2 = F32Vec3.createVec3(x - (s * .12671047f), y - (s * .20791227f), z + (s * .30720518f));
        int v3 = F32Vec3.createVec3(x - (s * .12671045f), y - (s * .38751736f), z + (s * .12760002f));
        int v4 = F32Vec3.createVec3(x - (s * .30631556f), y - (s * .20791227f), z + (s * .48681026f));
        int v5 = F32Vec3.createVec3(x - (s * .48592068f), y - (s * .20791225f), z + (s * .30720514f));
        int v6 = F32Vec3.createVec3(x - (s * .30631556f), y - (s * .56712254f), z + (s * .48681026f));
        int v7 = F32Vec3.createVec3(x - (s * .12671047f), y - (s * .56712254f), z + (s * .30720512f));
        int v8 = F32Vec3.createVec3(x - (s * .12671042f), y - (s * .3875174f), z + (s * .48681026f));
        int v9 = F32Vec3.createVec3(x - (s * .48592068f), y - (s * .38751736f), z + (s * .1276f));
        int v10 = F32Vec3.createVec3(x - (s * .30631556f), y - (s * .56712254f), z + (s * .1276f));
        int v11 = F32Vec3.createVec3(x - (s * .48592068f), y - (s * .56712254f), z + (s * .30720512f));
        int v12 = F32Vec3.createVec3(x - (s * .48592068f), y - (s * .38751743f), z + (s * .4868103f));


        createTriangle3D(v1, v2, v3, 0xff0000);
        createTriangle3D(v4, v2, v5, 0x7f8000);
        createTriangle3D(v5, v2, v1, 0x3fc000);
        createTriangle3D(v6, v7, v8, 0x1fe000);
        createTriangle3D(v9, v10, v11, 0x0ff000);
        createTriangle3D(v8, v2, v4, 0x07f800);
        createTriangle3D(v5, v1, v9, 0x03fc00);
        createTriangle3D(v3, v7, v10, 0x01fe00);
        createTriangle3D(v8, v7, v2, 0x00ff00);
        createTriangle3D(v2, v7, v3, 0x007f80);
        createTriangle3D(v8, v4, v6, 0x003fc0);
        createTriangle3D(v6, v4, v12, 0x001fe0);
        createTriangle3D(v11, v12, v9, 0x000ff0);
        createTriangle3D(v9, v12, v5, 0x0007f8);
        createTriangle3D(v7, v6, v10, 0x0003fc);
        createTriangle3D(v6, v11, v10, 0x0001fe);
        createTriangle3D(v1, v3, v9, 0x0000ff);
        createTriangle3D(v9, v3, v10, 0x00007f);
        createTriangle3D(v12, v4, v5, 0x00003f);
        createTriangle3D(v6, v12, v11, 0x00001f);


    }


    public static int getV0(int i) {
        i *= SIZE;
        return F32Triangle3D.entries[i + F32Triangle3D.V0];
    }

    public static int getV1(int i) {
        i *= SIZE;
        return F32Triangle3D.entries[i + F32Triangle3D.V1];
    }

    public static int getV2(int i) {
        i *= SIZE;
        return F32Triangle3D.entries[i + F32Triangle3D.V2];
    }

    public static int getRGB(int i) {
        i *= SIZE;
        return F32Triangle3D.entries[i + F32Triangle3D.RGB];
    }


    public static int createI32Triangle2D(int i, int rgb, float normal) {
        int v0 = F32Triangle3D.getV0(i);
        int v1 = F32Triangle3D.getV1(i);
        int v2 = F32Triangle3D.getV2(i);
        return I32Triangle2D.createTriangle((int) F32Vec3.getX(v0), (int) F32Vec3.getY(v0), (int) F32Vec3.getX(v1),
                (int) F32Vec3.getY(v1), (int) F32Vec3.getX(v2), (int) F32Vec3.getY(v2), rgb, normal);
    }

}
