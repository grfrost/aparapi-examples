

package com.aparapi.examples.view;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.examples.view.f32.F32Mat4;
import com.aparapi.examples.view.f32.F32Mesh3D;
import com.aparapi.examples.view.f32.F32Triangle3D;
import com.aparapi.examples.view.f32.F32Vec3;
import com.aparapi.examples.view.f32.mat4;
import com.aparapi.examples.view.f32.projectionMat4;
import com.aparapi.examples.view.f32.rotationMat4;
import com.aparapi.examples.view.f32.scaleMat4;
import com.aparapi.examples.view.f32.translateMat4;
import com.aparapi.examples.view.f32.tri;
import com.aparapi.examples.view.f32.vec3;
import com.aparapi.examples.view.i32.I32Triangle2D;
import com.aparapi.examples.view.i32.I32Vec2;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Main {
    public static class Config {
        enum ColourMode {NORMALIZED_COLOUR, NORMALIZED_INV_COLOUR, COLOUR, NORMALIZED_WHITE, NORMALIZED_INV_WHITE, WHITE}
        enum DisplayMode {FILL, WIRE, WIRE_SHOW_HIDDEN, WIRE_AND_FILL}

        public static final ColourMode colourMode = ColourMode.COLOUR.COLOUR;
        public static final DisplayMode displayMode = DisplayMode.WIRE;
        public static final float deltaSquare = 10000f;
        public static final String eliteAsset = "BARREL";// null;//"COBRA";//"CONSTRICTOR";//COBRAMK1";
        public static final float thetaDelta = 0.001f;
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
        vec3 cameraVec3;
        vec3 lookDirVec3;
        mat4 projectionMat4;
        vec3 centerVec3;
        vec3 moveAwayVec3;

        static class Mark {
            int markedTriangles3D;
            int markedTriangles2D;
            int markedVec2;
            int markedVec3;
            int markedMat4;

            Mark() {
                markedTriangles3D = F32Triangle3D.pool.count;
                markedVec3 = F32Vec3.pool.count;
                markedMat4 = F32Mat4.pool.count;
                markedTriangles2D = I32Triangle2D.count;
                markedVec2 = I32Vec2.count;
            }

            void resetAll() {
                reset3D();
                I32Triangle2D.count = markedTriangles2D;
                I32Vec2.count = markedVec2;
            }

            void reset3D() {
                F32Triangle3D.pool.count = markedTriangles3D;
                F32Vec3.pool.count = markedVec3;
                F32Mat4.pool.count = markedMat4;
            }

        }

        Mark mark;

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


            // (new F32Mesh3D("rubric")).rubric(.49f);
            (new F32Mesh3D("cubeoctahedron")).cubeoctahedron(0, 0, 0, 4).fin();
            if (Config.eliteAsset != null) {
                Elite.load(Config.eliteAsset);
            } else {
                (new F32Mesh3D("cube")).cube(0, 0, 0, 2f);
            }
            //   Triangle3D.load(new File("/home/gfrost/github/grfrost/aparapi-build/foo.obj"));

            cameraVec3 = new vec3(0f, 0f, 0f);
            lookDirVec3 = new vec3(0f, 0f, 0f);//F32Vec3.createVec3(0, 0, 0);
            projectionMat4 = new projectionMat4(view.image.getWidth(), view.image.getHeight(), 0.1f, 5f, 90f);
            projectionMat4 = projectionMat4.mul(new scaleMat4(view.image.getHeight()/4));
            projectionMat4 = projectionMat4.mul(new translateMat4(view.image.getHeight()/2));

            centerVec3 = new vec3(view.image.getWidth() / 2, view.image.getHeight() / 2, 0);
            moveAwayVec3 = new vec3(0f, 0f, 12f);
            mark = new Mark();

        }

        Point waitForPoint(long timeout) {
            while (point == null) {
                synchronized (doorBell) {
                    try {
                        if (timeout > 0) {
                            doorBell.wait(timeout);
                        }
                        update();
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

        static class ZPos implements Comparable<ZPos> {

            int x0, y0, x1, y1, x2, y2;
            float z0, z1, z2;
            float z;
            float howVisible;
            int rgb;

            @Override
            public int compareTo(ZPos zPos) {
                return Float.compare(z, zPos.z);
            }

            ZPos(tri t, float howVisible) {
                vec3 v0 = t.v0();
                vec3 v1 = t.v1();
                vec3 v2 = t.v2();
                x0 = (int) v0.x();
                y0 = (int) v0.y();
                z0 = v0.z();
                x1 = (int) v1.x();
                y1 = (int) v1.y();
                z1 = v1.z();
                x2 = (int) v2.x();
                y2 = (int) v2.y();
                z2 = v2.z();
                this.rgb = t.rgb();
                this.howVisible = howVisible;
                z = Math.min(z0, Math.min(z1, z2));
            }


            int create() {
                int r = ((rgb & 0xff0000) >> 16);
                int g = ((rgb & 0x00ff00) >> 8);
                int b = ((rgb & 0x0000ff) >> 0);

                if (Config.colourMode == Config.ColourMode.NORMALIZED_COLOUR) {
                    r = r - (int) (20 * howVisible);
                    g = g - (int) (20 * howVisible);
                    b = b - (int) (20 * howVisible);
                } else if (Config.colourMode == Config.ColourMode.NORMALIZED_INV_COLOUR) {
                    r = r + (int) (20 * howVisible);
                    g = g + (int) (20 * howVisible);
                    b = b + (int) (20 * howVisible);
                } else if (Config.colourMode == Config.ColourMode.NORMALIZED_WHITE) {
                    r = g = b = (int) (0x7f - (20 * howVisible));
                } else if (Config.colourMode == Config.ColourMode.NORMALIZED_INV_WHITE) {
                    r = g = b = (int) (0x7f + (20 * howVisible));
                } else if (Config.colourMode == Config.ColourMode.WHITE) {
                    r = g = b = 0xff;
                }

                return I32Triangle2D.createTriangle(x0, y0, x1, y1, x2, y2, (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff));

            }
        }

        void update() {
            final long elapsedMillis = System.currentTimeMillis() - startMillis;
            float theta = elapsedMillis * Config.thetaDelta;

            if ((frames++ % 50) == 0) {
                System.out.println("Frames " + frames + " Theta = " + theta + " FPS = " + ((frames * 1000) / elapsedMillis) + " Vertices " + kernel.vec2EntriesCount);
            }

            mark.resetAll();

            mat4 xyzRot4x4 = new rotationMat4(theta * 2, theta / 2, theta);

            Mark resetMark = new Mark();

            List<ZPos> zpos = new ArrayList<>();
            // Loop through the triangles
            boolean showHidden = Config.displayMode == Config.DisplayMode.WIRE_SHOW_HIDDEN;

            for (tri t : tri.all()) {
                // here we rotate and then move into the Z plane.
                t = t.mul(xyzRot4x4).add(moveAwayVec3);
                float howVisible = 1f;
                boolean isVisible = showHidden;

                if (!showHidden) {
                    // here we decide whether the camera can see the plane that the translated triangle is on.
                    // so we need the normal to the triangle in the coordinate system

                    // Now we work out where the camera is relative to a line projected from the plane to the camera
                    // if camera is at 0,0,0 clearly this is a no-op

                    // We need a point on the triangle it looks like assume we can use any, I choose the center of the triangle
                    // intuition suggests the one with the minimal Z is best no?

                    // We subtract the camera from our point on the triangle so we can compare

                    vec3 cameraDeltaVec3 =  t.center().sub(cameraVec3); // clearly our default camera is 0,0,0

                    howVisible = cameraDeltaVec3.mul( t.normalSumOfSquares()).sumOf();

                    // howVisible is a 'scalar'
                    // it's magnitude indicating how much it is 'facing away from' the camera.
                    // it's sign indicates if the camera can indeed see the location.
                    isVisible = howVisible < 0.0;
                }

                if (isVisible) {
                    // Projected triangle is still in unit 1 space!!
                    // now project the 3d triangle to 2d plane.
                    // Scale up to quarter screen height then add half height of screen

                    t = t.mul(projectionMat4);//  projection matrix also scales to screen and translate half a screen

                    zpos.add(new ZPos(t, howVisible));
                }

                resetMark.reset3D(); // do not move this up.
            }


            Collections.sort(zpos);

            for (ZPos z : zpos) {
                z.create();
            }

            kernel.triangle2DEntries = I32Triangle2D.entries;
            kernel.triangle2DEntriesCount = I32Triangle2D.count;
            kernel.vec2Entries = I32Vec2.entries;
            kernel.vec2EntriesCount = I32Vec2.count;
            kernel.colors = I32Triangle2D.colors;
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

        Range range;
        int triangle2DEntries[];
        int triangle2DEntriesCount;
        int vec2Entries[];
        int vec2EntriesCount;
        int colors[];


        public RasterKernel(View view) {
            this.view = view;
            this.width = view.image.getWidth();
            this.height = view.image.getHeight();
            this.range = Range.create(width * height);
            this.rgb = view.offscreenRgb;
        }

        public void resetImage(int _width, int _height, int[] _rgb) {
            width = _width;
            height = _height;
            rgb = _rgb;
        }

        public static boolean wire = Config.displayMode == Config.DisplayMode.WIRE || Config.displayMode == Config.DisplayMode.WIRE_AND_FILL || Config.displayMode == Config.DisplayMode.WIRE_SHOW_HIDDEN;
        public static boolean fill = Config.displayMode == Config.DisplayMode.WIRE_AND_FILL || Config.displayMode == Config.DisplayMode.FILL;
        public static float deltaSquare = Config.deltaSquare;

        public void run() {
            final int gid = getGlobalId();
            int x = gid % width;
            int y = gid / width;
            int col = 0x00000;
            for (int t = 0; t < triangle2DEntriesCount; t++) {
                int v0 = triangle2DEntries[I32Triangle2D.SIZE * t + I32Triangle2D.V0];
                int v1 = triangle2DEntries[I32Triangle2D.SIZE * t + I32Triangle2D.V1];
                int v2 = triangle2DEntries[I32Triangle2D.SIZE * t + I32Triangle2D.V2];
                int x0 = vec2Entries[v0 * I32Vec2.SIZE + I32Vec2.X];
                int y0 = vec2Entries[v0 * I32Vec2.SIZE + I32Vec2.Y];
                int x1 = vec2Entries[v1 * I32Vec2.SIZE + I32Vec2.X];
                int y1 = vec2Entries[v1 * I32Vec2.SIZE + I32Vec2.Y];
                int x2 = vec2Entries[v2 * I32Vec2.SIZE + I32Vec2.X];
                int y2 = vec2Entries[v2 * I32Vec2.SIZE + I32Vec2.Y];
                if (fill && I32Triangle2D.intriangle(x, y, x0, y0, x1, y1, x2, y2)) {
                    col = colors[t];
                } else if (wire && I32Triangle2D.onedge(x, y, x0, y0, x1, y1, x2, y2, deltaSquare)) {
                    col = 0xffffff;//colors[t];
                }
            }

            rgb[gid] = col;
        }

    }


    @SuppressWarnings("serial")
    public static void main(String[] _args) {
        final View view = new View(1024 + 256, 1024 + 256);
        final RasterKernel kernel = new RasterKernel(view);
        kernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.GPU);
        ViewFrame vf = new ViewFrame("View", kernel);

        for (Point point = vf.waitForPoint(0); point != null; point = vf.waitForPoint(10)) {
            System.out.println("You pressed " + point);
        }

    }

}
