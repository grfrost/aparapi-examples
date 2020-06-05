

package com.aparapi.examples.view;

import com.aparapi.Kernel;
import com.aparapi.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class Main{

   public static class View{
      private volatile Point point = null;
      private Object doorBell;
      private int width;
      private int height;
      private BufferedImage image;
      private BufferedImage offscreen;
      int[] offscreenRgb;
      int[] imageRgb;
      View (int width, int height) {
         this.width = width;
         this.height = height;
         image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
         offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
         offscreenRgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
         imageRgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
         doorBell = new Object();
      }
      void paint(Graphics2D g){
         g.drawImage(image, 0, 0, width, height, null);
      }
      void update() {
         System.arraycopy(offscreenRgb, 0, imageRgb, 0, offscreenRgb.length);
      }
      Point waitForPoint(){
         while (point == null) {
            synchronized (doorBell) {
               try {
                  doorBell.wait();
               } catch (final InterruptedException ie) {
                  ie.getStackTrace();
               }
            }
         }
         Point returnPoint = point;
         point = null;
         return returnPoint;
      }

      void ringDoorBell(Point point){
         this.point = point;
         synchronized (doorBell) {
            doorBell.notify();
         }
      }
   }

   public abstract static class ViewFrame extends JFrame{
      private View view;
      private JComponent viewer;

      ViewFrame(String name, View view){
         super(name);
         this.view = view;
         viewer = new JComponent(){
            @Override public void paintComponent(Graphics g) {
               view.paint((Graphics2D)g);
            }
         };
         viewer.setPreferredSize(new Dimension(view.width, view.height));
         viewer.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e) {
               view.ringDoorBell(e.getPoint());

            }
         });
         getContentPane().add(viewer);
         pack();
         setLocationRelativeTo(null);
         setVisible(true);
         addWindowListener(new WindowAdapter(){
            @Override public void windowClosing(WindowEvent _windowEvent) {
               shutdown();
               System.exit(0);
            }
         });
      }

      public abstract  void shutdown();
      void update() {
         view.update();
         viewer.repaint();
      }
   }



   public static class MandelKernel extends Kernel{

      /** RGB buffer used to store the Mandelbrot image. This buffer holds (width * height) RGB values. */
      private int[] rgb;

      /** Mandelbrot image width. */
      private int width;

      /** Mandelbrot image height. */
      private int height;

      /** Maximum iterations for Mandelbrot. */
      final private int maxIterations = 128;

      /** Palette which maps iteration values to RGB values. */
      @Constant final private int pallette[] = new int[maxIterations + 1];

      /** Mutable values of scale, offsetx and offsety so that we can modify the zoom level and position of a view. */
      private float scale = .0f;

      private float offsetx = .0f;

      private float offsety = .0f;

      /**
       * Initialize the Kernel.
       *  
       * @param _width Mandelbrot image width
       * @param _height Mandelbrot image height
       * @param _rgb Mandelbrot image RGB buffer
       */
      public MandelKernel(int _width, int _height, int[] _rgb) {
         //Initialize palette values
         for (int i = 0; i < maxIterations; i++) {
            final float h = i / (float) maxIterations;
            final float b = 1.0f - (h * h);
            pallette[i] = Color.HSBtoRGB(h, 1f, b);
         }

         width = _width;
         height = _height;
         rgb = _rgb;

      }

      public void resetImage(int _width, int _height, int[] _rgb) {
         width = _width;
         height = _height;
         rgb = _rgb;
      }

      public int getCount(float r, float i) {
         int count = 0;
         float zr = r;
         float zi = i;
         float new_zx = 0f;

         while ((count < maxIterations) && (((zr * zr) + (zi * zi)) < 8)) {
            new_zx = ((zr * zr) - (zi * zi)) + r;
            zi = (2 * zr * zi) + i;
            zr = new_zx;
            count++;
         }

         return count;
      }

      @Override public void run() {

         /** Determine which RGB value we are going to process (0..RGB.length). */
         final int gid = getGlobalId();

         int x = gid%width;
         int y = gid/width;

         int count =0;

         if (!(((x%10)==0) || ((y%10)==0))) {
            /** Translate the gid into an x an y value. */
            final float r = ((((x) * scale) - ((scale / 2) * width)) / width) + offsetx;
            final float i = ((((y) * scale) - ((scale / 2) * height)) / height) + offsety;

            count = getCount(r, i);
         }
         rgb[gid] = pallette[count];
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


   @SuppressWarnings("serial") public static void main(String[] _args) {
      final View view = new View(1024, 1024);
      final Range range = Range.create(view.width * view.height);

      // Create a Kernel passing the size, RGB buffer and the palette.
      final MandelKernel kernel = new MandelKernel(view.width, view.height, view.offscreenRgb);
      ViewFrame vf  = new ViewFrame("View", view){
         @Override
         public void shutdown() {
            kernel.dispose();
         }
      };

      final float defaultScale = 3f;

      // Set the default scale and offset, execute the kernel and force a repaint of the viewer.
      kernel.setScaleAndOffset(defaultScale, -1f, 0f);
      kernel.execute(range);

      vf.update();

      System.out.println("device=" + kernel.getTargetDevice());


      // Wait until the user selects a zoom-in point on the Mandelbrot view.
      while (true) {
         Point point = view.waitForPoint();
         float x = -1f;
         float y = 0f;
         float scale = defaultScale;
         final float tox = ((float) (point.x - (view.width / 2)) / view.width) * scale;
         final float toy = ((float) (point.y - (view.height / 2)) / view.height) * scale;

         // This is how many frames we will display as we zoom in and out.
         final int frames = 200;
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
