

package com.aparapi.examples.view;

import com.aparapi.Kernel;
import com.aparapi.Range;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.*;

public class Main {
    public static class Mat4 {
        static final int SIZE = 16;
        static final int MAX_MAT4 = 100;
        static final int X0Y0 = 0;
        static final int X1Y0 = 1;
        static final int X2Y0 = 2;
        static final int X3Y0 = 3;
        static final int X0Y1 = 4;
        static final int X1Y1 = 5;
        static final int X2Y1 = 6;
        static final int X3Y1 = 7;
        static final int X0Y2 = 8;
        static final int X1Y2 = 9;
        static final int X2Y2 = 10;
        static final int X3Y2 = 11;
        static final int X0Y3 = 12;
        static final int X1Y3 = 13;
        static final int X2Y3 = 14;
        static final int X3Y3 = 15;
        static int count = 0;

        static float entries[] = new float[MAX_MAT4 * SIZE];

        static int createMat4(float x0y0, float x1y0, float x2y0, float x3y0,
                              float x0y1, float x1y1, float x2y1, float x3y1,
                              float x0y2, float x1y2, float x2y2, float x3y2,
                              float x0y3, float x1y3, float x2y3, float x3y3) {
            entries[count * SIZE + X0Y0] = x0y0;
            entries[count * SIZE + X1Y0] = x1y0;
            entries[count * SIZE + X2Y0] = x2y0;
            entries[count * SIZE + X3Y0] = x3y0;
            entries[count * SIZE + X0Y1] = x0y1;
            entries[count * SIZE + X1Y1] = x1y1;
            entries[count * SIZE + X2Y1] = x2y1;
            entries[count * SIZE + X3Y1] = x3y1;
            entries[count * SIZE + X0Y2] = x0y2;
            entries[count * SIZE + X1Y2] = x1y2;
            entries[count * SIZE + X2Y2] = x2y2;
            entries[count * SIZE + X3Y2] = x3y2;
            entries[count * SIZE + X0Y3] = x0y3;
            entries[count * SIZE + X1Y3] = x1y3;
            entries[count * SIZE + X2Y3] = x2y3;
            entries[count * SIZE + X3Y3] = x3y3;
            return count++;
        }

        static int mulMat4(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return createMat4(
                    entries[lhs + X0Y0] * entries[rhs + X0Y0] + entries[lhs + X1Y0] * entries[rhs + X0Y1] + entries[lhs + X2Y0] * entries[rhs + X0Y2] + entries[lhs + X3Y0] * entries[rhs + X0Y3],
                    entries[lhs + X0Y0] * entries[rhs + X1Y0] + entries[lhs + X1Y0] * entries[rhs + X1Y1] + entries[lhs + X2Y0] * entries[rhs + X1Y2] + entries[lhs + X3Y0] * entries[rhs + X1Y3],
                    entries[lhs + X0Y0] * entries[rhs + X2Y0] + entries[lhs + X1Y0] * entries[rhs + X2Y1] + entries[lhs + X2Y0] * entries[rhs + X2Y2] + entries[lhs + X3Y0] * entries[rhs + X2Y3],
                    entries[lhs + X0Y0] * entries[rhs + X3Y0] + entries[lhs + X1Y0] * entries[rhs + X3Y1] + entries[lhs + X2Y0] * entries[rhs + X3Y2] + entries[lhs + X3Y0] * entries[rhs + X3Y3],

                    entries[lhs + X0Y1] * entries[rhs + X0Y0] + entries[lhs + X1Y1] * entries[rhs + X0Y1] + entries[lhs + X2Y1] * entries[rhs + X0Y2] + entries[lhs + X3Y1] * entries[rhs + X0Y3],
                    entries[lhs + X0Y1] * entries[rhs + X1Y0] + entries[lhs + X1Y1] * entries[rhs + X1Y1] + entries[lhs + X2Y1] * entries[rhs + X1Y2] + entries[lhs + X3Y1] * entries[rhs + X1Y3],
                    entries[lhs + X0Y1] * entries[rhs + X2Y0] + entries[lhs + X1Y1] * entries[rhs + X2Y1] + entries[lhs + X2Y1] * entries[rhs + X2Y2] + entries[lhs + X3Y1] * entries[rhs + X2Y3],
                    entries[lhs + X0Y1] * entries[rhs + X3Y0] + entries[lhs + X1Y1] * entries[rhs + X3Y1] + entries[lhs + X2Y1] * entries[rhs + X3Y2] + entries[lhs + X3Y1] * entries[rhs + X3Y3],

                    entries[lhs + X0Y2] * entries[rhs + X0Y0] + entries[lhs + X1Y2] * entries[rhs + X0Y1] + entries[lhs + X2Y2] * entries[rhs + X0Y2] + entries[lhs + X3Y2] * entries[rhs + X0Y3],
                    entries[lhs + X0Y2] * entries[rhs + X1Y0] + entries[lhs + X1Y2] * entries[rhs + X1Y1] + entries[lhs + X2Y2] * entries[rhs + X1Y2] + entries[lhs + X3Y2] * entries[rhs + X1Y3],
                    entries[lhs + X0Y2] * entries[rhs + X2Y0] + entries[lhs + X1Y2] * entries[rhs + X2Y1] + entries[lhs + X2Y2] * entries[rhs + X2Y2] + entries[lhs + X3Y2] * entries[rhs + X2Y3],
                    entries[lhs + X0Y2] * entries[rhs + X3Y0] + entries[lhs + X1Y2] * entries[rhs + X3Y1] + entries[lhs + X2Y2] * entries[rhs + X3Y2] + entries[lhs + X3Y2] * entries[rhs + X3Y3],

                    entries[lhs + X0Y3] * entries[rhs + X0Y0] + entries[lhs + X1Y3] * entries[rhs + X0Y1] + entries[lhs + X2Y3] * entries[rhs + X0Y2] + entries[lhs + X3Y3] * entries[rhs + X0Y3],
                    entries[lhs + X0Y3] * entries[rhs + X1Y0] + entries[lhs + X1Y3] * entries[rhs + X1Y1] + entries[lhs + X2Y3] * entries[rhs + X1Y2] + entries[lhs + X3Y3] * entries[rhs + X1Y3],
                    entries[lhs + X0Y3] * entries[rhs + X2Y0] + entries[lhs + X1Y3] * entries[rhs + X2Y1] + entries[lhs + X2Y3] * entries[rhs + X2Y2] + entries[lhs + X3Y3] * entries[rhs + X2Y3],
                    entries[lhs + X0Y3] * entries[rhs + X3Y0] + entries[lhs + X1Y3] * entries[rhs + X3Y1] + entries[lhs + X2Y3] * entries[rhs + X3Y2] + entries[lhs + X3Y3] * entries[rhs + X3Y3]

            );
        }

        static String asString(int i) {
            i *= SIZE;
            return String.format("|%5.2f, %5.2f, %5.2f, %5.2f|\n"+
                            "|%5.2f, %5.2f, %5.2f, %5.2f|\n"+
                            "|%5.2f, %5.2f, %5.2f, %5.2f|\n"+
                            "|%5.2f, %5.2f, %5.2f, %5.2f|\n",
                            entries[i + X0Y0], entries[i + X1Y0] ,entries[i + X2Y0], entries[i + X3Y0],
                            entries[i + X0Y1], entries[i + X1Y1], entries[i + X2Y1], entries[i + X3Y1],
                            entries[i + X0Y2], entries[i + X1Y2], entries[i + X2Y2], entries[i + X3Y2],
                            entries[i + X0Y3], entries[i + X1Y3], entries[i + X2Y3], entries[i + X3Y3]);
        }

        static int createProjectionMatrix(float width, float height, float near, float far, float fieldOfViewDeg) {

            // Projection Matrix

            float aspectRatio = height / width;
            float fieldOfViewRadians = (float) (1.0f / Math.tan((fieldOfViewDeg * 0.5f) / 180.0 * Math.PI));

                /*  https://youtu.be/ih20l3pJoeU?t=973

                 --------------------            far
                  \                /              ^    ^
                   \              /               |    |   far-near
                    \            /                |    |
                     \__________/         near    |    v
                                           ^      |
                                           v      v
                         \^/
                       [x,y,z]

                */

            return createMat4(
                    aspectRatio * fieldOfViewRadians, 0f, 0f, 0f,
                    0f, fieldOfViewRadians, 0f, 0f,
                    0f, 0f, far / (far - near), (-far * near) / (far - near),
                    0f, 0f, (-far * near) / (far - near), 0f);

        }

        static int createRotXMat4(float thetaRadians) {
            float sinTheta = (float) Math.sin(thetaRadians);
            float cosTheta = (float) Math.cos(thetaRadians);
            return createMat4(
                    1, 0, 0, 0,
                    0, cosTheta, -sinTheta, 0,
                    0, sinTheta, cosTheta, 0,
                    0, 0, 0, 1

            );
        }

        static int createRotZMat4(float thetaRadians) {
            float sinTheta = (float) Math.sin(thetaRadians);
            float cosTheta = (float) Math.cos(thetaRadians);
            return createMat4(
                    cosTheta, sinTheta, 0, 0,
                    -sinTheta, cosTheta, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            );
        }

        static int createRotYMat4(float thetaRadians) {
            float sinTheta = (float) Math.sin(thetaRadians);
            float cosTheta = (float) Math.cos(thetaRadians);
            return createMat4(
                    cosTheta, 0, sinTheta, 0,
                    0, 1, 0, 0,
                    -sinTheta, 0, cosTheta, 0,
                    0, 0, 0, 1
            );
        }


    }

    public static class Vec2 {
        static final int SIZE = 3;
        static final int MAX_VEC2 = 100;
        static final int X = 0;
        static final int Y = 1;

        static int vec2Count = 0;
        static float vec2s[] = new float[MAX_VEC2 * SIZE];

        static int createVec2(float x, float y) {
            vec2s[vec2Count * SIZE + X] = x;
            vec2s[vec2Count * SIZE + Y] = y;
            return vec2Count++;
        }


        static int mulScaler(int i, float s) {
            i *= SIZE;
            return createVec2(vec2s[i + X] * s, vec2s[i + Y] * s);
        }

        static int addScaler(int i, float s) {
            i *= SIZE;
            return createVec2(vec2s[i + X] + s, vec2s[i + Y] + s);
        }

        static int divScaler(int i, float s) {
            i *= SIZE;
            return createVec2(vec2s[i + X] / s, vec2s[i + Y] / s);
        }

        static int addVec2(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return createVec2(vec2s[lhs + X] + vec2s[rhs + X], vec2s[lhs + Y] + vec2s[rhs + Y]);
        }

        static int subVec2(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return createVec2(vec2s[lhs + X] - vec2s[rhs + X], vec2s[lhs + Y] - vec2s[rhs + Y]);
        }


        static float dotProd(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return vec2s[lhs + X] * vec2s[rhs + X] + vec2s[lhs + Y] * vec2s[rhs + Y];
        }

        static String asString(int i) {
            i *= SIZE;
            return vec2s[i + X] + "," + vec2s[i + Y];
        }
    }

    public static class Vec3 {
        static final int SIZE = 3;
        static final int MAX = 400;
        static final int X = 0;
        static final int Y = 1;
        static final int Z = 2;
        static int count = 0;
        static float entries[] = new float[MAX * SIZE];

        static int createVec3(float x, float y, float z) {
            entries[count * SIZE + X] = x;
            entries[count * SIZE + Y] = y;
            entries[count * SIZE + Z] = z;
            return count++;
        }


        // return another vec3 after multiplying by m4
        // we pad this vec3 to vec 4 with '1' as w
        // we normalize the result
        static int mulMat4(int i, int m4) {
            i *= SIZE;
            m4 *= Mat4.SIZE;
            int o = createVec3(
                    entries[i + X] * Mat4.entries[m4 + Mat4.X0Y0] + entries[i + Y] * Mat4.entries[m4 + Mat4.X0Y1] + entries[i + Z] * Mat4.entries[m4 + Mat4.X0Y2] + 1f * Mat4.entries[m4 + Mat4.X0Y3],
                    entries[i + X] * Mat4.entries[m4 + Mat4.X1Y0] + entries[i + Y] * Mat4.entries[m4 + Mat4.X1Y1] + entries[i + Z] * Mat4.entries[m4 + Mat4.X1Y2] + 1f * Mat4.entries[m4 + Mat4.X1Y3],
                    entries[i + X] * Mat4.entries[m4 + Mat4.X2Y0] + entries[i + Y] * Mat4.entries[m4 + Mat4.X2Y1] + entries[i + Z] * Mat4.entries[m4 + Mat4.X2Y2] + 1f * Mat4.entries[m4 + Mat4.X2Y3]
            );

            float w = entries[i + X] * Mat4.entries[m4 + Mat4.X3Y0] + entries[i + Y] * Mat4.entries[m4 + Mat4.X3Y1] + entries[i + Z] * Mat4.entries[m4 + Mat4.X3Y2] + 1 * Mat4.entries[m4 + Mat4.X3Y3];
            if (w != 0.0) {
                o =  Vec3.divScaler(o, w);
            }
            return o;
        }

        static int mulScaler(int i, float s) {
            i *= SIZE;
            return createVec3(entries[i + X] * s, entries[i + Y] * s, entries[i + Z] * s);
        }

        static int addScaler(int i, float s) {
            i *= SIZE;
            return createVec3(entries[i + X] + s, entries[i + Y] + s, entries[i + Z] + s);
        }

        static int divScaler(int i, float s) {
            i *= SIZE;
            return createVec3(entries[i + X] / s, entries[i + Y] / s, entries[i + Z] / s);
        }

        static int addVec3(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return createVec3(entries[lhs + X] + entries[rhs + X], entries[lhs + Y] + entries[rhs + Y], entries[lhs + Z] + entries[rhs + Z]);
        }

        static int subVec3(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return createVec3(entries[lhs + X] - entries[rhs + X], entries[lhs + Y] - entries[rhs + Y], entries[lhs + Z] - entries[rhs + Z]);
        }
        static float sumOfSquares(int i) {
            i *= SIZE;
            return entries[i + X] * entries[i + X]+ entries[i + Y] * entries[i + Y] +  entries[i + Z] * entries[i + Z];
        }
        static float hypot(int i) {
            return (float)Math.sqrt(sumOfSquares(i));
        }

        static int dotProd(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return createVec3(
                    entries[lhs + Y] * entries[rhs + Z] - entries[lhs + Z] * entries[rhs + X],
                    entries[lhs + Z] * entries[rhs + X] - entries[lhs + X] * entries[rhs + Z],
                    entries[lhs + X] * entries[rhs + Y] - entries[lhs + Y] * entries[rhs + X]);
        }

        static String asString(int i) {
            i *= SIZE;
            return entries[i + X] + "," + entries[i + Y] + "," + entries[i + Z];
        }

        public static float getX(int i) {
            i*=SIZE;
            return entries[i+X];
        }
        public static float getY(int i) {
            i*=SIZE;
            return entries[i+Y];
        }
        public static float getZ(int i) {
            i*=SIZE;
            return entries[i+Z];
        }
    }

    static class Triangle3D {
        static final int SIZE = 4;
        static final int MAX = 100;
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
            ViewFrame.dump(count-1);

            return createTriangle3D(v0, v2, v3, col) - 1;
        }


        static void cube(){
          quad(Vec3.createVec3(0,0,0), Vec3.createVec3(0,1,0), Vec3.createVec3(1,1,0), Vec3.createVec3(1,0,0), 0xff0000); //front
          quad(Vec3.createVec3(0,1,0), Vec3.createVec3(0,1,1), Vec3.createVec3(1,1,1), Vec3.createVec3(1,1,0), 0x0000ff); //top
          quad(Vec3.createVec3(1,0,0), Vec3.createVec3(1,1,0), Vec3.createVec3(1,1,1), Vec3.createVec3(1,0,1), 0xffff00); //right
          quad(Vec3.createVec3(0,0,1), Vec3.createVec3(0,1,1), Vec3.createVec3(0,1,0), Vec3.createVec3(0,0,0), 0xffffff); //left
          quad(Vec3.createVec3(1,0,1), Vec3.createVec3(1,1,1), Vec3.createVec3(0,1,1), Vec3.createVec3(0,0,1), 0x00ff00);//back
          quad(Vec3.createVec3(1,0,1), Vec3.createVec3(0,0,1), Vec3.createVec3(0,0,0), Vec3.createVec3(1,0,0), 0xffa500);//bottom
        }


        public static int getV0(int i) {
            i*=SIZE;
            return Triangle3D.entries[i+Triangle3D.V0];
        }
        public static int getV1(int i) {
            i*=SIZE;
            return Triangle3D.entries[i+Triangle3D.V1];
        }
        public static int getV2(int i) {
            i*=SIZE;
            return Triangle3D.entries[i+Triangle3D.V2];
        }
        public static int getRGB(int i) {
            i*=SIZE;
            return Triangle3D.entries[i+Triangle3D.RGB];
        }
    }


    public static class Triangle2D {
        static final int SIZE = 6;
        static final int X0 = 0;
        static final int Y0 = 1;
        static final int X1 = 2;
        static final int Y1 = 3;
        static final int X2 = 4;
        static final int Y2 = 5;
        public static int MAX = 1000;
        public static int count = 0;

        private static float entries[] = new float[MAX * SIZE];
        private static int colors[] = new int[MAX];

        public static float side(float x, float y, float x0, float y0, float x1, float y1) {
            return (y1 - y0) * (x - x0) + (-x1 + x0) * (y - y0);
        }

        public static boolean intriangle(float x, float y, float x0, float y0, float x1, float y1, float x2, float y2) {
            return side(x, y, x0, y0, x1, y1) >= 0 && side(x, y, x1, y1, x2, y2) >= 0 && side(x, y, x2, y2, x0, y0) >= 0;
        }

        public static boolean online(float x, float y, float x0, float y0, float x1, float y1, float deltaSquare) {
            float dxl = x1 - x0;
            float dyl = y1 - y0;
            float cross = (x - x0) * dyl - (y - y0) * dxl;
            if (cross * cross < deltaSquare) {
                if (dxl * dxl >= dyl * dyl)
                    return dxl > 0 ? x0 <= x && x <= x1 : x1 <= x && x <= x0;
                else
                    return dyl > 0 ? y0 <= y && y <= y1 : y1 <= y && y <= y0;
            } else {
                return false;
            }
        }

        static int createTriangle(float x0, float y0, float x1, float y1, float x2, float y2, int col) {
            entries[count * SIZE + X0] = x0;
            entries[count * SIZE + Y0] = y0;
            // We need the triangle to be clock wound
            if (side(x0, y0, x1, y1, x2, y2) > 0) {
                entries[count * SIZE + X1] = x1;
                entries[count * SIZE + Y1] = y1;
                entries[count * SIZE + X2] = x2;
                entries[count * SIZE + Y2] = y2;
            } else {
                entries[count * SIZE + X1] = x2;
                entries[count * SIZE + Y1] = y2;
                entries[count * SIZE + X2] = x1;
                entries[count * SIZE + Y2] = y1;
            }
            colors[count] = col;
            return count++;
        }

        static int createRandomTriangle(int col) {
            float x0 = (float) (Math.random() - .5);
            float y0 = (float) (Math.random() - .5);
            float x1 = x0 + (float) (Math.random() - .5);
            float y1 = y0 + (float) (Math.random() - .5);
            float x2 = x1 + (float) (Math.random() - .5);
            float y2 = y1 + (float) (Math.random() - .5);
            return createTriangle(x0, y0, x1, y1, x2, y2, col);
        }
    }
    public static class View {
        private BufferedImage image;
        int[] offscreenRgb;

        View(int width, int height) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            offscreenRgb = new int[((DataBufferInt) image.getRaster().getDataBuffer()).getData().length];
        }

        void paint(Graphics2D g) {
            g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        }

        void update() {
            System.arraycopy(offscreenRgb, 0, ((DataBufferInt) image.getRaster().getDataBuffer()).getData(), 0, offscreenRgb.length);
        }
    }

    public static class ViewFrame extends JFrame {
        private RasterKernel kernel;
        private volatile Point point = null;
        private Object doorBell;
        private View view;
        private JComponent viewer;
        final long startMillis;
        long frames;
        int cameraVec3;
        int lookDirVec3;
        int projectionMat4;
        int centerVec3;
        int moveAwayVec3;
        int markedTriangles;
        int markedVec3;
        int markedMat4;

        ViewFrame(String name, RasterKernel kernel) {
            super(name);
            startMillis = System.currentTimeMillis();
            this.kernel = kernel;
            this.doorBell = new Object();
            this.view = kernel.view;
            this.viewer = new JComponent() {
                @Override
                public void paintComponent(Graphics g) {
                    view.paint((Graphics2D) g);
                }
            };
            viewer.setPreferredSize(new Dimension(view.image.getWidth(), view.image.getHeight()));
            viewer.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ringDoorBell(e.getPoint());

                }
            });
            getContentPane().add(viewer);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent _windowEvent) {
                    kernel.dispose();
                    System.exit(0);
                }
            });

            Triangle3D.cube();



            cameraVec3 = Vec3.createVec3(0,0,0);
            lookDirVec3 = Vec3.createVec3(0,0,0);
            projectionMat4 = Mat4.createProjectionMatrix(view.image.getWidth(), view.image.getHeight(), 0.1f, 1000f, 90f);
            centerVec3 = Vec3.createVec3(view.image.getWidth()/2,view.image.getHeight()/2,0);
            moveAwayVec3 = Vec3.createVec3(0,0,6);

            markedTriangles = Triangle3D.count;
            markedVec3 = Vec3.count;
            markedMat4 = Mat4.count;
        }

        Point waitForPoint(long timeout, float scale, float x, float y) {
            while (point == null) {
                synchronized (doorBell) {
                    try {
                        doorBell.wait(timeout);
                        update(scale, x, y);
                    } catch (final InterruptedException ie) {
                        ie.getStackTrace();
                    }
                }
            }
            Point returnPoint = point;
            point = null;
            return returnPoint;
        }

        void ringDoorBell(Point point) {
            this.point = point;
            synchronized (doorBell) {
                doorBell.notify();
            }
        }

        static void dump(int tri){
            int v0 = Triangle3D.getV0(tri);
            int v1 = Triangle3D.getV1(tri);
            int v2 = Triangle3D.getV2(tri);
            float x0 = Vec3.getX(v0);
            float y0 = Vec3.getY(v0);
            float x1 = Vec3.getX(v1);
            float y1 = Vec3.getY(v1);
            float x2 = Vec3.getX(v2);
            float y2 = Vec3.getY(v2);
            tri = tri;
        }

        void update(float scale, float x, float y) {
            final long elapsedMillis = System.currentTimeMillis() - startMillis;
            float theta = elapsedMillis*.001f;
            if( (frames++ %50) ==0) {
                System.out.println("Frames "+frames+" Theta = "+theta+" FPS = " + ((frames * 1000) / elapsedMillis));
            }

            Vec3.count=markedVec3;
            Triangle3D.count=markedTriangles;
            Mat4.count = markedMat4;
            int  rotXMat4 = Mat4.createRotXMat4(theta*2);
            int rotYMat4 = Mat4.createRotYMat4(theta/2);
            int rotZMat4 = Mat4.createRotZMat4(theta);
            int  rotXYMat4 = Mat4.mulMat4(rotXMat4, rotYMat4);
            int  rotXYZMat4 = Mat4.mulMat4(rotXYMat4, rotZMat4);
            int resetVec3 = Vec3.count;
            int resetTriangle3 = Triangle3D.count;
            int resetMat4 = Mat4.count;
            Triangle2D.count=0;
            for (int t = 0; t<Triangle3D.count; t++){
                int rotatedTri =  Triangle3D.mulMat4(t, rotXYZMat4);
                int translatedTri =  Triangle3D.addVec3(rotatedTri, moveAwayVec3);
                int v0 = Triangle3D.getV0(translatedTri);
                int v1 = Triangle3D.getV1(translatedTri);
                int v2 = Triangle3D.getV2(translatedTri);

                int line1Vec3 = Vec3.subVec3(v1, v0);
                int line2Vec3 = Vec3.subVec3(v2, v0);
                int normalVec3 = Vec3.dotProd(line1Vec3, line2Vec3);

                float sumOfSquares =  Vec3.sumOfSquares(normalVec3);

                int rgb =  Triangle3D.getRGB(translatedTri);

                if (sumOfSquares!=0){
                    normalVec3 = Vec3.divScaler(normalVec3, sumOfSquares);
                    float normalX = Vec3.getX(normalVec3);
                    float normalY = Vec3.getY(normalVec3);
                    float normalZ = Vec3.getZ(normalVec3);
                    float v0x = Vec3.getX(v0);
                    float v0y = Vec3.getY(v0);
                    float v0z = Vec3.getZ(v0);
                    float camerax = Vec3.getX(cameraVec3);
                    float cameray = Vec3.getY(cameraVec3);
                    float cameraz = Vec3.getZ(cameraVec3);
                    if((normalX * (v0x - camerax) +
                            normalY * (v0y - cameray) +
                            normalZ * (v0z - cameraz)) <= 0.0){

                        int projected = Triangle3D.mulMat4(translatedTri, projectionMat4);
                        int centered = Triangle3D.mulScaler(projected, .5f);
                        v0 = Triangle3D.getV0(centered);
                        v1 = Triangle3D.getV1(centered);
                        v2 = Triangle3D.getV2(centered);
                        v0x = Vec3.getX(v0);
                        v0y = Vec3.getY(v0);
                        float v1x = Vec3.getX(v1);
                        float v1y = Vec3.getY(v1);
                        float v2x = Vec3.getX(v2);
                        float v2y = Vec3.getY(v2);


                        Triangle2D.createTriangle(v0x, v0y, v1x, v1y, v2x, v2y, rgb);
                    }
                }

                Vec3.count=resetVec3;
                Triangle3D.count=resetTriangle3;
                Mat4.count = resetMat4;
            }

            kernel.triangles=Triangle2D.entries;
            kernel.colors = Triangle2D.colors;
            kernel.triangleCount = Triangle2D.count;
            kernel.setScaleAndOffset(scale, x, y);
            kernel.execute(kernel.range);
            view.update();
            viewer.repaint();
        }
    }

    public static class RasterKernel extends Kernel {
        View view;
        private int[] rgb;
        private int width;
        private int height;

        private float scale = .0f;
        private float offsetx = .0f;
        private float offsety = .0f;


        static final float deltaSquare = 0.000001f;

        Range range;

        float triangles[];
        float triangleCount;
        int colors[];

        public RasterKernel(View view) {
            this.view = view;
            this.width = view.image.getWidth();
            this.height = view.image.getHeight();
            this.range = Range.create(width * height);
            this.rgb = view.offscreenRgb;
            this.triangles = Triangle2D.entries;
            this.triangleCount = Triangle2D.count;
        }

        public void resetImage(int _width, int _height, int[] _rgb) {
            width = _width;
            height = _height;
            rgb = _rgb;
        }

        @Override
        public void run() {
            final int gid = getGlobalId();

            final float x = ((((gid % width) * scale) - ((scale / 2) * width)) / width) + offsetx;
            final float y = ((((gid / width) * scale) - ((scale / 2) * height)) / height) + offsety;

            int col = 0x00000;
            for (int t = 0; t < triangleCount; t++) {
                float x0 = triangles[Triangle2D.X0 + t*Triangle2D.SIZE];
                float y0 = triangles[Triangle2D.Y0 + t*Triangle2D.SIZE];
                float x1 = triangles[Triangle2D.X1 + t*Triangle2D.SIZE];
                float y1 = triangles[Triangle2D.Y1 + t*Triangle2D.SIZE];
                float x2 = triangles[Triangle2D.X2 + t*Triangle2D.SIZE];
                float y2 = triangles[Triangle2D.Y2 + t*Triangle2D.SIZE];
                if (Triangle2D.intriangle(x, y, x0, y0, x1, y1, x2, y2)) {
                    col = colors[t];
              //  } else if (Triangle2D.online(x, y, x0, y0, x1, y1, deltaSquare) || Triangle2D.online(x, y, x1, y1, x2, y2, deltaSquare) || Triangle2D.online(x, y, x2, y2, x0, y0, deltaSquare)) {
                 //   col = 0xFF0000;
                }
            }

            rgb[gid] = col;
        }

        public void setScaleAndOffset(float _scale, float _offsetx, float _offsety) {
            offsetx = _offsetx;
            offsety = _offsety;
            scale = _scale;
        }

        public int[] getRgbs() {
            return rgb;
        }

    }


    @SuppressWarnings("serial")
    public static void main(String[] _args) {
        final View view = new View(1024, 1024);
        for (int i = 0; i < 200; i++) {
            Triangle2D.createRandomTriangle(0xffa500);
        }
        final RasterKernel kernel = new RasterKernel(view);
         // kernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.JTP);
        ViewFrame vf = new ViewFrame("View", kernel);

        final float defaultScale = 3f;
        for (Point point = vf.waitForPoint(10, defaultScale, 0, 0); point != null; point = vf.waitForPoint(10, defaultScale, 0, 0)) {
            //Tween interval for animation.
            final float tox = ((float) (point.x - (view.image.getWidth() / 2)) / view.image.getWidth()) * defaultScale;
            final float toy = ((float) (point.y - (view.image.getHeight() / 2)) / view.image.getHeight()) * defaultScale;

            // This is how many frames we will display as we zoom in and out.
            final int frames = 100;
            float scale = defaultScale;
            final long startMillis = System.currentTimeMillis();
            float x = 0f;
            float y = 0f;
            for (int sign = -1; sign < 2; sign += 2) {
                for (int i = 0; i < (frames - 4); i++) {
                    scale = scale + ((sign * defaultScale) / frames);
                    x = x - (sign * (tox / frames));
                    y = y - (sign * (toy / frames));
                    vf.update(scale, x, y);

                }
            }
        }

    }

}
