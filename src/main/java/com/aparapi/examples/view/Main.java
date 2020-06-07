

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
                } else if (Triangle2D.online(x, y, x0, y0, x1, y1, deltaSquare) || Triangle2D.online(x, y, x1, y1, x2, y2, deltaSquare) || Triangle2D.online(x, y, x2, y2, x0, y0, deltaSquare)) {
                    col = 0x000000;
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
