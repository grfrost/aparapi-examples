

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
        final long startMillis;
        long frames;
        int cameraVec3;
        int lookDirVec3;
        int projectionMat4;
        int centerVec3;
        int moveAwayVec3;
        int markedTriangles3D;
        int markedTriangles2D;
        int markedVec2;
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

            for (int x = -1; x < 0; x++) {
                for (int y = -1; y < 0; y++) {
                    for (int z = -1; z < 2; z++) {
                        Triangle3D.cube(x * .5f, y * .5f, z * .5f, .4f);
                    }
                }
            }
            Triangle3D.cubeoctahedron(0, 0, 0, 2);
            // Triangle3D.load(new File("/home/gfrost/github/grfrost/aparapi-build/foo.obj"));

            cameraVec3 = Vec3.createVec3(0, 0, 0);
            lookDirVec3 = Vec3.createVec3(0, 0, 0);
            projectionMat4 = Mat4.createProjectionMatrix(view.image.getWidth(), view.image.getHeight(), 0.1f, 5f, 90f);
            centerVec3 = Vec3.createVec3(view.image.getWidth() / 2, view.image.getHeight() / 2, 0);
            moveAwayVec3 = Vec3.createVec3(0, 0, 6);

            markedTriangles3D = Triangle3D.count;
            markedTriangles2D = Triangle2D.count;
            markedVec3 = Vec3.count;
            markedMat4 = Mat4.count;
            markedVec2 = Vec2.count;
        }

        Point waitForPoint(long timeout) {
            while (point == null) {
                synchronized (doorBell) {
                    try {
                        doorBell.wait(timeout);
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

        void update() {
            final long elapsedMillis = System.currentTimeMillis() - startMillis;
            float theta = elapsedMillis * .001f;

            if ((frames++ % 50) == 0) {
                System.out.println("Frames " + frames + " Theta = " + theta + " FPS = " + ((frames * 1000) / elapsedMillis));
            }

            Vec3.count = markedVec3;
            Vec2.count = markedVec2;
            Triangle3D.count = markedTriangles3D;
            Triangle2D.count = markedTriangles2D;
            Mat4.count = markedMat4;

            int rotXMat4 = Mat4.createRotXMat4(theta * 2);
            int rotYMat4 = Mat4.createRotYMat4(theta / 2);
            int rotZMat4 = Mat4.createRotZMat4(theta);
            int rotXYMat4 = Mat4.mulMat4(rotXMat4, rotYMat4);
            int rotXYZMat4 = Mat4.mulMat4(rotXYMat4, rotZMat4);

            int resetVec3 = Vec3.count;
            int resetTriangle3 = Triangle3D.count;
            int resetMat4 = Mat4.count;

            Triangle2D.count = 0;
         //   NonVecTriangle2D.count = 0;
            for (int t = 0; t < Triangle3D.count; t++) {
                int rotatedTri = Triangle3D.mulMat4(t, rotXYZMat4);
                int translatedTri = Triangle3D.addVec3(rotatedTri, moveAwayVec3);
                int v0 = Triangle3D.getV0(translatedTri);
                int v1 = Triangle3D.getV1(translatedTri);
                int v2 = Triangle3D.getV2(translatedTri);

                int line1Vec3 = Vec3.subVec3(v1, v0);
                int line2Vec3 = Vec3.subVec3(v2, v0);
                int normalVec3 = Vec3.dotProd(line1Vec3, line2Vec3);

                float sumOfSquares = Vec3.sumOfSquares(normalVec3);

                int rgb = Triangle3D.getRGB(translatedTri);

                if (sumOfSquares != 0) {
                    normalVec3 = Vec3.divScaler(normalVec3, sumOfSquares);
                    int v0Minuscamera = Vec3.subVec3(v0, cameraVec3);
                    int play = Vec3.mulVec3(v0Minuscamera, normalVec3);
                    if (Vec3.sumOf(play) <= 0.0) {
                        int projected = Triangle3D.mulMat4(translatedTri, projectionMat4);
                        int centered = Triangle3D.mulScaler(projected, view.image.getHeight() / 4);
                        centered = Triangle3D.addScaler(centered, view.image.getHeight() / 2);
                        Triangle3D.createTriangle2D(centered, rgb);
                     //   Triangle3D.createNonVecTriangle2D(centered, rgb);
                    }
                }

                Vec3.count = resetVec3;
                Triangle3D.count = resetTriangle3;
                Mat4.count = resetMat4;
            }
            kernel.triangle2DEntries = Triangle2D.entries;
            kernel.triangle2DEntriesCount = Triangle2D.count;
            kernel.vec2Entries = Vec2.entries;
            kernel.vec2EntriesCount = Vec2.count;
            kernel.colors = Triangle2D.colors;
         //   kernel.triangles = NonVecTriangle2D.entries;
          //  kernel.triangleCount = NonVecTriangle2D.count;
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
        static final float deltaSquare = 10000f;
        Range range;
        int triangle2DEntries[];
        int triangle2DEntriesCount;
        float vec2Entries[];
        int vec2EntriesCount;
        int colors[];
        int triangleCount;
        float[] triangles;


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
/*
        public void nonvecrun() {
            final int gid = getGlobalId();
            float x = gid % width;
            float y = gid / width;
            int col = 0x00000;
            for (int t = 0; t < triangleCount; t++) {
                float x0 = triangles[NonVecTriangle2D.X0 + t * NonVecTriangle2D.SIZE];
                float y0 = triangles[NonVecTriangle2D.Y0 + t * NonVecTriangle2D.SIZE];
                float x1 = triangles[NonVecTriangle2D.X1 + t * NonVecTriangle2D.SIZE];
                float y1 = triangles[NonVecTriangle2D.Y1 + t * NonVecTriangle2D.SIZE];
                float x2 = triangles[NonVecTriangle2D.X2 + t * NonVecTriangle2D.SIZE];
                float y2 = triangles[NonVecTriangle2D.Y2 + t * NonVecTriangle2D.SIZE];
                if (NonVecTriangle2D.intriangle(x, y, x0, y0, x1, y1, x2, y2)) {
                    col = colors[t];
                } else
                  if (NonVecTriangle2D.online(x, y, x0, y0, x1, y1, deltaSquare) || NonVecTriangle2D.online(x, y, x1, y1, x2, y2, deltaSquare) || NonVecTriangle2D.online(x, y, x2, y2, x0, y0, deltaSquare)) {
                    col = 0xFF0000;
                }
            }
            rgb[gid] = col;
        }
*/

        public void run() {
            final int gid = getGlobalId();
            float x = gid % width;
            float y = gid / width;
            int col = 0x00000;
            for (int t = 0; t < triangle2DEntriesCount; t++) {
                int v0 = triangle2DEntries[Triangle2D.SIZE * t + Triangle2D.V0];
                int v1 = triangle2DEntries[Triangle2D.SIZE * t + Triangle2D.V1];
                int v2 = triangle2DEntries[Triangle2D.SIZE * t + Triangle2D.V2];
                float x0 = vec2Entries[v0 * Vec2.SIZE + Vec2.X];
                float y0 = vec2Entries[v0 * Vec2.SIZE + Vec2.Y];
                float x1 = vec2Entries[v1 * Vec2.SIZE + Vec2.X];
                float y1 = vec2Entries[v1 * Vec2.SIZE + Vec2.Y];
                float x2 = vec2Entries[v2 * Vec2.SIZE + Vec2.X];
                float y2 = vec2Entries[v2 * Vec2.SIZE + Vec2.Y];
                if (Triangle2D.intriangle(x, y, x0, y0, x1, y1, x2, y2)) {
                    col = colors[t];
                    } else if (Triangle2D.online(x, y, x0, y0, x1, y1, deltaSquare) || Triangle2D.online(x, y, x1, y1, x2, y2, deltaSquare) || Triangle2D.online(x, y, x2, y2, x0, y0, deltaSquare)) {
                    col = 0x000000;

                }
            }

            rgb[gid] = col;
        }

    }


    @SuppressWarnings("serial")
    public static void main(String[] _args) {
        final View view = new View(1024, 1024);
        final RasterKernel kernel = new RasterKernel(view);
        kernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.CPU);
        ViewFrame vf = new ViewFrame("View", kernel);

        for (Point point = vf.waitForPoint(10); point != null; point = vf.waitForPoint(10)) {
            System.out.println("You pressed " + point);
        }

    }

}
