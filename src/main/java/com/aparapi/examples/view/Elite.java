package com.aparapi.examples.view;

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
                                float x = hex2Float(lm.group(1));
                                float y = hex2Float(lm.group(2));
                                float z = hex2Float(lm.group(3));
                                mesh.vec3(x, y, z);

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
                                float nx = hex2Float(lm.group(3));
                                float ny = hex2Float(lm.group(4));
                                float nz = hex2Float(lm.group(5));
                                boolean abinormal = true;//!(nx < 0 || ny < 0 || nz < 0);

                                int v0 = mesh.vecbase + Integer.parseInt(lm.group(6));
                                int v1 = mesh.vecbase + Integer.parseInt(lm.group(7));
                                int v2 = mesh.vecbase + Integer.parseInt(lm.group(8));

                                if (lm.groupCount()==8){
                                    mesh.tri(v0, v1, v2, abinormal ? 0x00f000 : 0x000f00);
                                }else {
                                    int v3 = mesh.vecbase + Integer.parseInt(lm.group(9));
                                    if (lm.groupCount() == 9) {
                                        mesh.quad(v0, v1,v2, v3,  abinormal?0xf00000:0x0f0000);
                                    } else {
                                        int v4 = mesh.vecbase + Integer.parseInt(lm.group(10));
                                        if (lm.groupCount() == 10) {
                                            mesh.pent(v0, v1, v2, v3, v4, abinormal ? 0x0000f0 : 0x00000f);
                                        } else {
                                            int v5 = mesh.vecbase + Integer.parseInt(lm.group(11));
                                            System.out.println("normals {"+nx+","+ny+","+nz+"} abinormal="+abinormal);
                                            mesh.hex(v0, v1, v2, v3, v4, v5, abinormal ? 0xffffff : 0x0f0f0f);
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
