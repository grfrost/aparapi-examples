package com.aparapi.examples.haar;

/**
This project is based on the open source jviolajones project created by Simon
Houllier and is used with his permission. Simon's jviolajones project offers 
a pure Java implementation of the Viola-Jones algorithm.

http://en.wikipedia.org/wiki/Viola%E2%80%93Jones_object_detection_framework

The original Java source code for jviolajones can be found here
http://code.google.com/p/jviolajones/ and is subject to the
gnu lesser public license  http://www.gnu.org/licenses/lgpl.html

Many thanks to Simon for his excellent project and for permission to use it 
as the basis of an Aparapi example.
**/

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

public abstract class Detector{

   public class ScaleInfo{
      final static int SCALE_INTS = 3;

      int[] scale_ValueIJ;

      int[] scale_Width;

      int scaleIds = 0;

      List<Scale> scaleInstances = new ArrayList<Scale>();

      public class Scale{
         public Scale(short _value, short _width, short _i, short _j) {
            value = _value;
            i = _i;
            j = _j;
            width = _width;
         }

         int width;

         int i;

         int j;

         int value;

         public String toString() {
            return ("Scale value=" + value + " i=" + i + " j=" + j + " width=" + width);
         }
      }

      public ScaleInfo(int _width, int _height, float _maxScale) {

         for (float scale = baseScale; scale < _maxScale; scale *= scale_inc) {
            short scaledFeatureStep = (short) (scale * haarCascade.cascadeWidth * increment);
            short scaledFeatureWidth = (short) (scale * haarCascade.cascadeWidth);
            for (short j = 0; j < _height - scaledFeatureWidth; j += scaledFeatureStep) {
               for (short i = 0; i < _width - scaledFeatureWidth; i += scaledFeatureStep) {
                  scaleInstances.add(new Scale((short) scale, scaledFeatureWidth, i, j));
               }
            }
         }
         Comparator<Scale> comparator = null;

         comparator = new Comparator<Scale>(){
            @Override public int compare(Scale o1, Scale o2) {
               return (Float.compare(o1.value, o2.value) == 0 ? Integer.compare(o1.j, o2.j) : Float.compare(o1.value, o2.value));
            }
         };

         new Comparator<Scale>(){
            @Override public int compare(Scale o1, Scale o2) {
               return (Float.compare(o2.value, o1.value) == 0 ? Integer.compare(o2.j, o1.j) : Float.compare(o2.value, o1.value));
            }
         };

         new Comparator<Scale>(){
            @Override public int compare(Scale o1, Scale o2) {
               return (Integer.compare(o1.j, o2.j) == 0 ? Float.compare(o1.value, o2.value) : Integer.compare(o1.j, o2.j));
            }
         };

         new Comparator<Scale>(){
            @Override public int compare(Scale o1, Scale o2) {
               return (Integer.compare(o2.j, o1.j) == 0 ? Float.compare(o2.value, o1.value) : Integer.compare(o2.j, o1.j));
            }
         };

         if (comparator != null) {
            Collections.sort(scaleInstances, comparator);
         }
         scaleIds = scaleInstances.size();
         scale_ValueIJ = new int[scaleIds * SCALE_INTS];
         scale_Width = new int[scaleIds];
         for (int scaleId = 0; scaleId < scaleIds; scaleId++) {
            Scale scale = scaleInstances.get(scaleId);
            scale_Width[scaleId] = scale.width;
            scale_ValueIJ[scaleId * SCALE_INTS + 0] = scale.value;
            scale_ValueIJ[scaleId * SCALE_INTS + 1] = scale.i;
            scale_ValueIJ[scaleId * SCALE_INTS + 2] = scale.j;
         }
      }
   }

   final HaarCascade haarCascade;

   final float baseScale;

   final float scale_inc;

   final float increment;

   final CannyPruner cannyPruner;

   Detector(HaarCascade _haarCascade, float _baseScale, float _scale_inc, float _increment, boolean _doCannyPruning) {
      haarCascade = _haarCascade;
      baseScale = _baseScale;
      scale_inc = _scale_inc;
      increment = _increment;

      if (_doCannyPruning) {
         cannyPruner = new CannyPruner();
      } else {
         cannyPruner = null;
      }
   }

   /** Returns the list of detected objects in an image applying the Viola-Jones algorithm.
    * 
    * The algorithm tests, from sliding windows on the image, of variable size, which regions should be considered as searched objects.
    * Please see Wikipedia for a description of the algorithm.
    * @param file The image file to scan.
    * @param baseScale The initial ratio between the window size and the Haar classifier size (default 2).
    * @param scale_inc The scale increment of the window size, at each step (default 1.25).
    * @param increment The shift of the window at each sub-step, in terms of percentage of the window size.
    * @return the list of rectangles containing searched objects, expressed in pixels.
    */
   public List<Rectangle> getFeatures(String file) {

      try {
         BufferedImage image = ImageIO.read(new File(file));

         return getFeatures(image);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return null;

   }

   int[] grayImage;

   int[] weightedGrayImage;

   int[] weightedGrayImageSquared;

   public List<Rectangle> getFeatures(BufferedImage image) {

      final int width = image.getWidth();
      final int height = image.getHeight();
      final float maxScale = (Math.min((width + 0.f) / haarCascade.cascadeWidth, (height + 0.0f) / haarCascade.cascadeHeight));
      // if (grayImage == null || grayImage.length != (width * height)) {
      grayImage = new int[width * height];
      //}
      weightedGrayImage = new int[width * height];
      weightedGrayImageSquared = new int[width * height];

      byte[] imagePixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

      for (int j = 0; j < height; j++) {
         int col = 0;
         int col2 = 0;
         for (int i = 0; i < width; i++) {
            int red = imagePixels[0 + 3 * (i + j * width)] & 0xff;
            int green = imagePixels[1 + 3 * (i + j * width)] & 0xff;
            int blue = imagePixels[2 + 3 * (i + j * width)] & 0xff;
            int value = (30 * red + 59 * green + 11 * blue) / 100;
            grayImage[(i + j * width)] = value;
            col += value;
            weightedGrayImage[(i + j * width)] = (j > 0 ? weightedGrayImage[(i + j * width) - width] : 0) + col; // NOT data parallel !
            col2 += value * value;
            weightedGrayImageSquared[(i + j * width)] = (j > 0 ? weightedGrayImageSquared[(i + j * width) - width] : 0) + col2; // NOT data parallel
         }
      }

      final int[] cannyIntegral = (cannyPruner == null) ? null : cannyPruner.getIntegralCanny(weightedGrayImageSquared, width,
            height);

      final List<Rectangle> ret = getFeatures(width, height, maxScale, weightedGrayImage, weightedGrayImageSquared, cannyIntegral);

      return (ret);
   }

   abstract List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral);

}
