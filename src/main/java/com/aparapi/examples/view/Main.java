

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

        ViewFrame(String name, RasterKernel kernel) {
            super(name);
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

        void update(float scale, float x, float y) {
            kernel.setScaleAndOffset(scale, x, y);
            kernel.execute(kernel.range);
            view.update();
            viewer.repaint();
        }
    }


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
        static int mat4Count = 0;
        static float mat4s[] = new float[MAX_MAT4 * SIZE];

        static int createMat4(float x0y0, float x1y0, float x2y0, float x3y0,
                       float x0y1, float x1y1, float x2y1, float x3y1,
                       float x0y2, float x1y2, float x2y2, float x3y2,
                       float x0y3, float x1y3, float x2y3, float x3y3) {
            mat4s[mat4Count * SIZE + X0Y0] = x0y0;
            mat4s[mat4Count * SIZE + X1Y0] = x1y0;
            mat4s[mat4Count * SIZE + X2Y0] = x2y0;
            mat4s[mat4Count * SIZE + X3Y0] = x3y0;
            mat4s[mat4Count * SIZE + X0Y1] = x0y1;
            mat4s[mat4Count * SIZE + X1Y1] = x1y1;
            mat4s[mat4Count * SIZE + X2Y1] = x2y1;
            mat4s[mat4Count * SIZE + X3Y1] = x3y1;
            mat4s[mat4Count * SIZE + X0Y2] = x0y2;
            mat4s[mat4Count * SIZE + X1Y2] = x1y2;
            mat4s[mat4Count * SIZE + X2Y2] = x2y2;
            mat4s[mat4Count * SIZE + X3Y2] = x3y2;
            mat4s[mat4Count * SIZE + X0Y3] = x0y3;
            mat4s[mat4Count * SIZE + X1Y3] = x1y3;
            mat4s[mat4Count * SIZE + X2Y3] = x2y3;
            mat4s[mat4Count * SIZE + X3Y3] = x3y3;
            mat4Count++;
            return mat4Count;
        }

        static int mulMat4(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return createMat4(
                    mat4s[lhs + X0Y0] * mat4s[rhs + X0Y0] + mat4s[lhs + X1Y0] * mat4s[rhs + X0Y1] + mat4s[lhs + X2Y0] * mat4s[rhs + X0Y2] + mat4s[lhs + X3Y0] * mat4s[rhs + X0Y3],
                    mat4s[lhs + X0Y0] * mat4s[rhs + X1Y0] + mat4s[lhs + X1Y0] * mat4s[rhs + X1Y1] + mat4s[lhs + X2Y0] * mat4s[rhs + X1Y2] + mat4s[lhs + X3Y0] * mat4s[rhs + X1Y3],
                    mat4s[lhs + X0Y0] * mat4s[rhs + X2Y0] + mat4s[lhs + X1Y0] * mat4s[rhs + X2Y1] + mat4s[lhs + X2Y0] * mat4s[rhs + X2Y2] + mat4s[lhs + X3Y0] * mat4s[rhs + X2Y3],
                    mat4s[lhs + X0Y0] * mat4s[rhs + X3Y0] + mat4s[lhs + X1Y0] * mat4s[rhs + X3Y1] + mat4s[lhs + X2Y0] * mat4s[rhs + X3Y2] + mat4s[lhs + X3Y0] * mat4s[rhs + X3Y3],

                    mat4s[lhs + X0Y1] * mat4s[rhs + X0Y0] + mat4s[lhs + X1Y1] * mat4s[rhs + X0Y1] + mat4s[lhs + X2Y1] * mat4s[rhs + X0Y2] + mat4s[lhs + X3Y1] * mat4s[rhs + X0Y3],
                    mat4s[lhs + X0Y1] * mat4s[rhs + X1Y0] + mat4s[lhs + X1Y1] * mat4s[rhs + X1Y1] + mat4s[lhs + X2Y1] * mat4s[rhs + X1Y2] + mat4s[lhs + X3Y1] * mat4s[rhs + X1Y3],
                    mat4s[lhs + X0Y1] * mat4s[rhs + X2Y0] + mat4s[lhs + X1Y1] * mat4s[rhs + X2Y1] + mat4s[lhs + X2Y1] * mat4s[rhs + X2Y2] + mat4s[lhs + X3Y1] * mat4s[rhs + X2Y3],
                    mat4s[lhs + X0Y1] * mat4s[rhs + X3Y0] + mat4s[lhs + X1Y1] * mat4s[rhs + X3Y1] + mat4s[lhs + X2Y1] * mat4s[rhs + X3Y2] + mat4s[lhs + X3Y1] * mat4s[rhs + X3Y3],

                    mat4s[lhs + X0Y2] * mat4s[rhs + X0Y0] + mat4s[lhs + X1Y2] * mat4s[rhs + X0Y1] + mat4s[lhs + X2Y2] * mat4s[rhs + X0Y2] + mat4s[lhs + X3Y2] * mat4s[rhs + X0Y3],
                    mat4s[lhs + X0Y2] * mat4s[rhs + X1Y0] + mat4s[lhs + X1Y2] * mat4s[rhs + X1Y1] + mat4s[lhs + X2Y2] * mat4s[rhs + X1Y2] + mat4s[lhs + X3Y2] * mat4s[rhs + X1Y3],
                    mat4s[lhs + X0Y2] * mat4s[rhs + X2Y0] + mat4s[lhs + X1Y2] * mat4s[rhs + X2Y1] + mat4s[lhs + X2Y2] * mat4s[rhs + X2Y2] + mat4s[lhs + X3Y2] * mat4s[rhs + X2Y3],
                    mat4s[lhs + X0Y2] * mat4s[rhs + X3Y0] + mat4s[lhs + X1Y2] * mat4s[rhs + X3Y1] + mat4s[lhs + X2Y2] * mat4s[rhs + X3Y2] + mat4s[lhs + X3Y2] * mat4s[rhs + X3Y3],

                    mat4s[lhs + X0Y3] * mat4s[rhs + X0Y0] + mat4s[lhs + X1Y3] * mat4s[rhs + X0Y1] + mat4s[lhs + X2Y3] * mat4s[rhs + X0Y2] + mat4s[lhs + X3Y3] * mat4s[rhs + X0Y3],
                    mat4s[lhs + X0Y3] * mat4s[rhs + X1Y0] + mat4s[lhs + X1Y3] * mat4s[rhs + X1Y1] + mat4s[lhs + X2Y3] * mat4s[rhs + X1Y2] + mat4s[lhs + X3Y3] * mat4s[rhs + X1Y3],
                    mat4s[lhs + X0Y3] * mat4s[rhs + X2Y0] + mat4s[lhs + X1Y3] * mat4s[rhs + X2Y1] + mat4s[lhs + X2Y3] * mat4s[rhs + X2Y2] + mat4s[lhs + X3Y3] * mat4s[rhs + X2Y3],
                    mat4s[lhs + X0Y3] * mat4s[rhs + X3Y0] + mat4s[lhs + X1Y3] * mat4s[rhs + X3Y1] + mat4s[lhs + X2Y3] * mat4s[rhs + X3Y2] + mat4s[lhs + X3Y3] * mat4s[rhs + X3Y3]

            );
        }

        static String asString(int i) {
            i *= SIZE;
            return
                    mat4s[i + X0Y0] + ", " + mat4s[i + X1Y0] + ", " + mat4s[i + X2Y0] + ", " + mat4s[i + X3Y0] + "\n" +
                            mat4s[i + X0Y1] + ", " + mat4s[i + X1Y1] + ", " + mat4s[i + X2Y1] + ", " + mat4s[i + X3Y1] + "\n" +
                            mat4s[i + X0Y2] + ", " + mat4s[i + X1Y2] + ", " + mat4s[i + X2Y2] + ", " + mat4s[i + X3Y2] + "\n" +
                            mat4s[i + X0Y3] + ", " + mat4s[i + X1Y3] + ", " + mat4s[i + X2Y3] + ", " + mat4s[i + X3Y3];
        }


    }

    public static class Vec3 {
        static final int SIZE = 3;
        static final int MAX_VEC3 = 100;
        static final int X = 0;
        static final int Y = 1;
        static final int Z = 2;

        static int vec3Count = 0;
        static float vec3s[] = new float[MAX_VEC3 * SIZE];

        static int createVec3(float x, float y, float z) {
            vec3s[vec3Count * SIZE + X] = x;
            vec3s[vec3Count * SIZE + Y] = x;
            vec3s[vec3Count * SIZE + Z] = z;
            vec3Count++;
            return vec3Count;
        }


        // return another vec3 after multiplying by m4
        // we pad this vec3 to vec 4 with '1' as w
        // we normalize the result
           static int  mulMat4(int i, int m4){
            i*=SIZE;
            m4*=Mat4.SIZE;
                int o = createVec3(
                        vec3s[i+X] * Mat4.mat4s[m4+Mat4.X0Y0] + vec3s[i+Y] * Mat4.mat4s[m4+Mat4.X0Y1] + vec3s[i+Z] * Mat4.mat4s[m4+Mat4.X0Y2] + 1f * Mat4.mat4s[m4+Mat4.X0Y3],
                        vec3s[i+X] * Mat4.mat4s[m4+Mat4.X1Y0] + vec3s[i+Y] * Mat4.mat4s[m4+Mat4.X1Y1] + vec3s[i+Z] * Mat4.mat4s[m4+Mat4.X1Y2] + 1f * Mat4.mat4s[m4+Mat4.X1Y3],
                        vec3s[i+X] * Mat4.mat4s[m4+Mat4.X2Y0] + vec3s[i+Y] * Mat4.mat4s[m4+Mat4.X2Y1] + vec3s[i+Z] * Mat4.mat4s[m4+Mat4.X2Y2] + 1f * Mat4.mat4s[m4+Mat4.X2Y3]
                );

                float w = vec3s[i+X] * Mat4.mat4s[m4+Mat4.X3Y0] + vec3s[i+Y] * Mat4.mat4s[m4+Mat4.X3Y1] + vec3s[i+Z] * Mat4.mat4s[m4+Mat4.X3Y2] + 1 * Mat4.mat4s[m4+Mat4.X3Y3];
                if (w != 0.0) {
                    vec3s[o*SIZE+X] /= w; vec3s[o*SIZE+Y] /= w; vec3s[o*SIZE+Z] /= w;
                }
                return o;
            }

        static int mulScaler(int i, float s) {
            i *= SIZE;
            return createVec3(vec3s[i + X] * s, vec3s[i + Y] * s, vec3s[i + Z] * s);
        }

        static int addScaler(int i, float s) {
            i *= SIZE;
            return createVec3(vec3s[i + X] + s, vec3s[i + Y] + s, vec3s[i + Z] + s);
        }

        static int divScaler(int i, float s) {
            i *= SIZE;
            return createVec3(vec3s[i + X] / s, vec3s[i + Y] / s, vec3s[i + Z] / s);
        }

        static int addVec3(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return createVec3(vec3s[lhs + X] + vec3s[rhs + X], vec3s[lhs + Z] + vec3s[rhs + Z], vec3s[lhs + Z] + vec3s[rhs + Z]);
        }

        static int subVec3(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return createVec3(vec3s[lhs + X] - vec3s[rhs + X], vec3s[lhs + Z] - vec3s[rhs + Z], vec3s[lhs + Z] - vec3s[rhs + Z]);
        }


        static int dotProd(int lhs, int rhs) {
            lhs *= SIZE;
            rhs *= SIZE;
            return createVec3(
                    vec3s[lhs + Y] * vec3s[rhs + Z] - vec3s[lhs + Z] * vec3s[rhs + X],
                    vec3s[lhs + Z] * vec3s[rhs + X] - vec3s[lhs + X] * vec3s[rhs + Z],
                    vec3s[lhs + X] * vec3s[rhs + Y] - vec3s[lhs + Y] * vec3s[rhs + X]);
        }

        static String asString(int i) {
            i *= SIZE;
            return vec3s[i + X] + "," + vec3s[i + Y] + "," + vec3s[i + Z];
        }


    }

    public static class Triangles2D {
        static final int SIZE = 6;
        static final int X0 = 0;
        static final int Y0 = 1;
        static final int X1 = 2;
        static final int Y1 = 3;
        static final int X2 = 4;
        static final int Y2 = 5;
        public static int MAX_TRIANGLES = 1000;
        public static int triangleCount = 0;

        private static float triangles[] = new float[MAX_TRIANGLES * SIZE];

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

        static int createTriangle(float x0, float y0, float x1, float y1, float x2, float y2) {
            triangles[triangleCount * SIZE + X0] = x0;
            triangles[triangleCount * SIZE + Y0] = y0;
            // We need the triangle to be clock wound
            if (side(x0, y0, x1, y1, x2, y2) > 0) {
                triangles[triangleCount * SIZE + X1] = x1;
                triangles[triangleCount * SIZE + Y1] = y1;
                triangles[triangleCount * SIZE + X2] = x2;
                triangles[triangleCount * SIZE + Y2] = y2;
            } else {
                triangles[triangleCount * SIZE + X1] = x2;
                triangles[triangleCount * SIZE + Y1] = y2;
                triangles[triangleCount * SIZE + X2] = x1;
                triangles[triangleCount * SIZE + Y2] = y1;
            }
            triangleCount++;
            return triangleCount;
        }

        static int createRandomTriangle() {
            float x0 = (float) (Math.random() - .5);
            float y0 = (float) (Math.random() - .5);
            float x1 = x0 + (float) (Math.random() - .5);
            float y1 = y0 + (float) (Math.random() - .5);
            float x2 = x1 + (float) (Math.random() - .5);
            float y2 = y1 + (float) (Math.random() - .5);
            return createTriangle(x0, y0, x1, y1, x2, y2);
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

        public RasterKernel(View view) {
            this.view = view;
            this.width = view.image.getWidth();
            this.height = view.image.getHeight();
            this.range = Range.create(width * height);
            this.rgb = view.offscreenRgb;
            this.triangles = Triangles2D.triangles;
            this.triangleCount = Triangles2D.triangleCount;
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
            for (int t = 0; t < triangleCount * Triangles2D.SIZE; t += Triangles2D.SIZE) {
                float x0 = triangles[Triangles2D.X0 + t];
                float y0 = triangles[Triangles2D.Y0 + t];
                float x1 = triangles[Triangles2D.X1 + t];
                float y1 = triangles[Triangles2D.Y1 + t];
                float x2 = triangles[Triangles2D.X2 + t];
                float y2 = triangles[Triangles2D.Y2 + t];
                if (Triangles2D.intriangle(x, y, x0, y0, x1, y1, x2, y2)) {
                    col = 0x00001 * t;
                } else if (Triangles2D.online(x, y, x0, y0, x1, y1, deltaSquare) || Triangles2D.online(x, y, x1, y1, x2, y2, deltaSquare) || Triangles2D.online(x, y, x2, y2, x0, y0, deltaSquare)) {
                    col = 0x010000 * t;
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
       // Triangles2D vertices = new Triangles2D();
        for (int i = 0; i < 200; i++) {
            Triangles2D.createRandomTriangle();
        }
        final RasterKernel kernel = new RasterKernel(view);
        //  kernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.GPU);
        ViewFrame vf = new ViewFrame("View", kernel);

        final float defaultScale = 4f;
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
            final long elapsedMillis = System.currentTimeMillis() - startMillis;
            System.out.println("FPS = " + ((frames * 1000) / elapsedMillis));
        }

    }

}
