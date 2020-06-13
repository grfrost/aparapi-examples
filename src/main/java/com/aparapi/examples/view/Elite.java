package com.aparapi.examples.view;

import com.aparapi.examples.view.f32.F32Mesh3D;
import com.aparapi.examples.view.f32.F32Vec3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Elite {
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

    static float hex2Float(String s) {
        return (s.startsWith("-"))? (-Integer.parseInt(s.substring(2), 16) / 64f): (Integer.parseInt(s.substring(1), 16) / 64f);
    }

    enum State {AWAITING_NAME, AWAITING_LAZER, AWAITING_COUNTS, AWAITING_VERTICES, AWAITING_HUE_LIG_SAT, AWAITING_FACES}

    static void load(String name) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("/home/gfrost/github/grfrost/aparapi-build/examples/github/aparapi-examples/src/main/java/com/aparapi/examples/view/Elite.txt")));

            State state = State.AWAITING_NAME;
            F32Mesh3D mesh= null;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                Matcher lm;
                if ((lm = remPattern.matcher(line)).matches()
                        || (lm = emptyPattern.matcher(line)).matches()
                        ||(lm = colonPattern.matcher(line)).matches()) {
                } else {
                    switch (state) {
                        case AWAITING_NAME: {
                            if ((lm = namePattern.matcher(line)).matches() && lm.group(1).equals(name)) {
                                state = State.AWAITING_LAZER;
                                mesh = new F32Mesh3D(name);

                            }
                            break;
                        }
                        case AWAITING_LAZER: {
                            if ((lm = frontLaserVertexPattern.matcher(line)).matches()) {
                                state = State.AWAITING_COUNTS;
                            }
                            break;
                        }
                        case AWAITING_COUNTS: {
                            if ((lm = vertexCountPattern.matcher(line)).matches()) {
                                state = State.AWAITING_VERTICES;
                            }
                            break;
                        }
                        case AWAITING_VERTICES: {
                            if ((lm = verticesPattern.matcher(line)).matches()) {
                                state = State.AWAITING_FACES;
                            }
                            break;
                        }
                        case AWAITING_FACES: {
                            if ((lm = vertexPattern.matcher(line)).matches()) {
                                mesh.vec3(hex2Float(lm.group(1)),  hex2Float(lm.group(2)), hex2Float(lm.group(3)));
                            } else if ((lm = facesPattern.matcher(line)).matches()) {
                                state = State.AWAITING_HUE_LIG_SAT;
                            }
                            break;
                        }
                        case AWAITING_HUE_LIG_SAT: {
                            if ((lm = face6Pattern.matcher(line)).matches()
                                 || (lm = face5Pattern.matcher(line)).matches()
                                    || (lm = face4Pattern.matcher(line)).matches()
                                    || (lm = face3Pattern.matcher(line)).matches()
                            ) {
                               // showGroups("FACE ", lm);
                                int vN = F32Vec3.createVec3( hex2Float(lm.group(3)),hex2Float(lm.group(4)),hex2Float(lm.group(5)));
                                int v0 = mesh.vecEntries[Integer.parseInt(lm.group(6))];
                                int v1 = mesh.vecEntries[Integer.parseInt(lm.group(7))];
                                int v2 = mesh.vecEntries[Integer.parseInt(lm.group(8))];

                                if (lm.groupCount()==8){
                                    mesh.tri(v0, v1, v2,  0x00ff00, vN );
                                }else {
                                    int v3 = mesh.vecEntries[Integer.parseInt(lm.group(9))];
                                    if (lm.groupCount() == 9) {
                                        mesh.quad(v0, v1,v2, v3,  0xff0000, vN);
                                    } else {
                                        int v4 = mesh.vecEntries[Integer.parseInt(lm.group(10))];
                                        if (lm.groupCount() == 10) {
                                            mesh.pent(v0, v1, v2, v3, v4, 0x0000ff, vN);
                                        } else {
                                            int v5 =  mesh.vecEntries[Integer.parseInt(lm.group(11))];
                                          //  System.out.println("normals {"+nx+","+ny+","+nz+"} abinormal="+abinormal);
                                            mesh.hex(v0, v1, v2, v3, v4, v5, 0xfff000, vN);
                                        }
                                    }
                                }
                            } else if ((lm = hueLigSatPattern.matcher(line)).matches()) {
                                mesh.fin();
                                return;
                            } else {
                                System.out.println("In " + state + " skipping " + line);
                            }
                            break;
                        }
                        default: {
                           // System.out.println("WHAt " + line);
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


}
