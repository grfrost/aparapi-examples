

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
        private int width;
        private int height;
        private BufferedImage image;
        private BufferedImage offscreen;
        int[] offscreenRgb;
        int[] imageRgb;

        View(int width, int height) {
            this.width = width;
            this.height = height;
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            offscreenRgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
            imageRgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        }
        void paint(Graphics2D g) {
            g.drawImage(image, 0, 0, width, height, null);
        }
        void update() {
            System.arraycopy(offscreenRgb, 0, imageRgb, 0, offscreenRgb.length);
        }
    }

    public static class ViewFrame extends JFrame {
        private volatile Point point = null;
        private Object doorBell;
        private View view;
        private JComponent viewer;

        ViewFrame(String name, View view, Kernel kernel) {
            super(name);
            this.doorBell = new Object();
            this.view = view;
          //  this.kernel = kernel;
            this.viewer = new JComponent() {
                @Override
                public void paintComponent(Graphics g) {
                    view.paint((Graphics2D) g);
                }
            };
            viewer.setPreferredSize(new Dimension(view.width, view.height));
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

        Point waitForPoint(long timeout) {
            while (point == null) {
                synchronized (doorBell) {
                    try {
                        doorBell.wait(timeout);
                        update();
                        viewer.repaint();
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

            view.update();
            viewer.repaint();
        }
    }


    public static class RasterKernel extends Kernel {
        private int[] rgb;
        private int width;
        private int height;
        private float scale = .0f;
        private float offsetx = .0f;
        private float offsety = .0f;

        static final int X1 = 0;
        static final int Y1 = 1;
        static final int X2 = 2;
        static final int Y2 = 3;
        static final int X3 = 4;
        static final int Y3 = 5;

        public static int MAX_TRIANGLES = 1000;
        public int triangleCount = 0;

        private float triangles[] = new float[MAX_TRIANGLES * 6];

        /**
         * Initialize the Kernel.
         *
         * @param _width  Mandelbrot image width
         * @param _height Mandelbrot image height
         * @param _rgb    Mandelbrot image RGB buffer
         */
        public RasterKernel(int _width, int _height, int[] _rgb) {
            width = _width;
            height = _height;
            rgb = _rgb;

        }

        void addTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
            triangles[triangleCount * 6 + 0] = x1;
            triangles[triangleCount * 6 + 1] = y1;
            // We need the triangle to be clock wound
            if (side(x1,y1, x2, y2, x3, y3)>0){
                triangles[triangleCount * 6 + 2] = x2;
                triangles[triangleCount * 6 + 3] = y2;
                triangles[triangleCount * 6 + 4] = x3;
                triangles[triangleCount * 6 + 5] = y3;
            }else{
                triangles[triangleCount * 6 + 2] = x3;
                triangles[triangleCount * 6 + 3] = y3;
                triangles[triangleCount * 6 + 4] = x2;
                triangles[triangleCount * 6 + 5] = y2;
            }
            triangleCount++;
        }

        public void resetImage(int _width, int _height, int[] _rgb) {
            width = _width;
            height = _height;
            rgb = _rgb;
        }

        float side(float x, float y, float x1, float y1, float x2, float y2) {
            return (y2 - y1) * (x - x1) + (-x2 + x1) * (y - y1);
        }

        boolean intriangle(float x, float y, float x1, float y1, float x2, float y2, float x3, float y3) {
            return side(x, y, x1, y1, x2, y2) >= 0 && side(x, y, x2, y2, x3, y3) >= 0 && side(x, y, x3, y3, x1, y1) >= 0;
        }

        boolean online(float x, float y, float x1, float y1, float x2, float y2) {
            float dxl = x2 - x1;
            float dyl = y2 - y1;
            float cross = (x - x1) * dyl - (y - y1) * dxl;
            if (cross*cross  < .000001f) {
                if (dxl * dxl >= dyl * dyl)
                    return dxl > 0 ? x1 <= x && x <= x2 : x2 <= x && x <= x1;
                else
                    return dyl > 0 ? y1 <= y && y <= y2 : y2 <= y && y <= y1;
            } else {
                return false;
            }
        }

        @Override
        public void run() {
            final int gid = getGlobalId();

            int x = gid % width;
            int y = gid / width;

            final float r = ((((x) * scale) - ((scale / 2) * width)) / width) + offsetx;
            final float i = ((((y) * scale) - ((scale / 2) * height)) / height) + offsety;

            int col =  0x00000;
            for (int t = 0;  t < triangleCount*6; t+=6) {
                float x1 =  triangles[X1 + t];
                float y1 =  triangles[Y1 + t];
                float x2 =  triangles[X2 + t];
                float y2 =  triangles[Y2 + t];
                float x3 =  triangles[X3 + t];
                float y3 =  triangles[Y3 + t];
                if (intriangle(r, i, x1, y1, x2, y2, x3, y3)) {
                    col = 0x00001*t;
                }else   if (online(r, i, x1, y1, x2, y2) || online(r, i, x2, y2, x3, y3) || online(r, i, x3, y3,x1,y1)){
                    col = 0x010000*t;
                }
            }

            rgb[gid]=col;
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
        final View view = new View(2*1024, 2*1024);
        final Range range = Range.create(view.width * view.height);
        final RasterKernel kernel = new RasterKernel(view.width, view.height, view.offscreenRgb);
        //kernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.JTP);
        ViewFrame vf = new ViewFrame("View", view, kernel) ;

        final float defaultScale = 2f;

        kernel.setScaleAndOffset(defaultScale, -0f, 0f);

        for (int i = 0; i < 200; i++) {
            float x1 =  (float)(Math.random()  - .5);
            float y1 =  (float)(Math.random() - .5);
            float x2 = x1 + (float) (Math.random()-.5);
            float y2 = y1 + (float) (Math.random()-.5);
            float x3 = x2 + (float) (Math.random()-.5);
            float y3 = y2 + (float) (Math.random()-.5);
            kernel.addTriangle(x1,y1, x2,y2,x3,y3);
        }
        kernel.execute(range);
        vf.update();

        System.out.println("device=" + kernel.getTargetDevice());

        for (Point point = vf.waitForPoint(10); point != null; point=vf.waitForPoint(10)){
            float x = 0f;
            float y = 0f;
            float scale = defaultScale;
            final float tox = ((float) (point.x - (view.width / 2)) / view.width) * scale;
            final float toy = ((float) (point.y - (view.height / 2)) / view.height) * scale;

            // This is how many frames we will display as we zoom in and out.
            final int frames = 100;
            final long startMillis = System.currentTimeMillis();
            for (int sign = -1; sign < 2; sign += 2) {
                for (int i = 0; i < (frames - 4); i++) {
                    scale = scale + ((sign * defaultScale) / frames);
                    x = x - (sign * (tox / frames));
                    y = y - (sign * (toy / frames));
                    // Set the scale and offset, execute the kernel and force a repaint of the viewer.
                    kernel.setScaleAndOffset(scale, x, y);
                    kernel.execute(range);
                    vf.update();

                }
            }
            final long elapsedMillis = System.currentTimeMillis() - startMillis;
            System.out.println("FPS = " + ((frames * 1000) / elapsedMillis));
        }

    }

}
