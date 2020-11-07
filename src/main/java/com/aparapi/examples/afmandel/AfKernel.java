package com.aparapi.examples.afmandel;

import com.aparapi.Kernel;

/**
 * Aparapi Fractals
 *  
 * the kernel executes the math with complex numbers
 * coordinates refer to complex plane
 * result is a vector of number of iterations, It is transformed in a color in the GUI 
 * 
 * @author marco.stefanetti at gmail.com
 */
public class AfKernel extends Kernel{

   private int[] rv;
   private int mi;
   private double cx1;
   private double cy1;
   private int W;
   private double xi;
   private double yj;

   public AfKernel()
   {

   }

   public void init(double _cx1, double _cy1, double _cx2, double _cy2, int _W, int _H, int max_iterations, int[] result)
   {
      rv = result;
      mi = max_iterations;
      cx1 = _cx1;
      cy1 = _cy1;
      W = _W;

      xi = (_cx2-cx1)/(double)_W;
      yj = (_cy2-cy1)/(double)_H;
   }

   @Override
   public void run() {
      final int k = getGlobalId();

      if(k<rv.length) {
         final double cx = cx1+k%W*xi;
         final double cy = cy1+k/W*yj;

         int t = 0;
         double xn=cx;
         double yn=cy;
         double y2=cy*cy;

         while( (t<mi) && (xn*xn+y2<4) ) {
            yn = 2f*xn*yn+cy;
            xn = xn*xn-y2+cx;
            y2=yn*yn;
            t++;
         }
         rv[k]= t;
      }
   }

}
