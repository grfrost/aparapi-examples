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

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;



public class AparapiDetector2 extends Detector{

   class DetectorKernel extends Kernel {

      private int width;

      private int[] weightedGrayImage;

      private int[] weightedGrayImageSquared;

      final private int[] tree_startEnd;

      final private int[] stage_startEnd;

      final private float[] stage_thresh;

      static final private int FEATURE_FLOATS = HaarCascade.FEATURE_FLOATS;

      static final private int FEATURE_INTS = HaarCascade.FEATURE_INTS;

      static final private int RECT_FLOATS = HaarCascade.RECT_FLOATS;

      static final private int RECT_BYTES = HaarCascade.RECT_BYTES;

      static final private int STAGE_FLOATS = HaarCascade.STAGE_FLOATS;

      static final private int STAGE_INTS = HaarCascade.STAGE_INTS;

      static final private int TREE_INTS = HaarCascade.TREE_INTS;

      static final private int SCALE_INTS = ScaleInfo.SCALE_INTS;

      final private int[] feature_r1r2r3LnRn;

      final private byte[] rect_x1y1x2y2;

      final private float[] rect_w;

      final private float[] feature_LvRvThres;

      final int cascadeWidth;

      final int cascadeHeight;

      private int[] scale_ValueIJ;

      private int[] scale_Width;

      private int stageId = 0;

      private int[] scaleIdList;

      private int maxScaleId;

      public DetectorKernel(HaarCascade _haarCascade) {
         stage_startEnd = _haarCascade.stage_startEnd;
         stage_thresh = _haarCascade.stage_thresh;
         tree_startEnd = _haarCascade.tree_startEnd;
         feature_r1r2r3LnRn = _haarCascade.feature_r1r2r3LnRn;
         feature_LvRvThres = _haarCascade.feature_LvRvThres;
         rect_w = _haarCascade.rect_w;
         rect_x1y1x2y2 = _haarCascade.rect_x1y1x2y2;
         cascadeWidth = _haarCascade.cascadeWidth;
         cascadeHeight = _haarCascade.cascadeHeight;
      }

      @Override public void run() {
         int gid = getGlobalId(0);
         int scaleId = scaleIdList[gid];
         scaleIdList[gid] = -1;
         if (gid < maxScaleId) { // so that gid can be rounded up to next multiple of groupsize.

            int scale = scale_ValueIJ[scaleId * SCALE_INTS];
            int i = scale_ValueIJ[scaleId * SCALE_INTS + 1];
            int j = scale_ValueIJ[scaleId * SCALE_INTS + 2];

            int w = scale * cascadeWidth;
            int h = scale * cascadeHeight;

            int jwidth = j * width;
            int jhwidth = (j + h) * width;
            float inv_area = 1f / (w * h);
            float sum = 0;
            for (int treeId = stage_startEnd[stageId * STAGE_INTS]; treeId <= stage_startEnd[stageId * STAGE_INTS + 1]; treeId++) {
               int featureId = tree_startEnd[treeId * TREE_INTS];
              

               for (boolean done = false; !done;) {
                  int total_x = weightedGrayImage[i + w + jhwidth] + weightedGrayImage[i + jwidth] - weightedGrayImage[i + jhwidth]
                        - weightedGrayImage[i + w + jwidth];
                  int total_x2 = weightedGrayImageSquared[i + w + jhwidth] + weightedGrayImageSquared[i + jwidth]
                        - weightedGrayImageSquared[i + jhwidth] - weightedGrayImageSquared[i + w + jwidth];
                  float moy = total_x * inv_area;
                  float vnorm = total_x2 * inv_area - moy * moy;
                  vnorm = (vnorm > 1) ? sqrt(vnorm) : 1;

                  int rect_sum = 0;

                  for (int r = 0; r < 3; r++) {
                     int rectId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + r];
                     if (rectId != -1) {

                        int rx1 = i + (scale * rect_x1y1x2y2[rectId * RECT_BYTES]);
                        int rx2 = i + (scale * (rect_x1y1x2y2[rectId * RECT_BYTES] + rect_x1y1x2y2[rectId * RECT_BYTES + 1]));
                        int ry1 = (j + (scale * rect_x1y1x2y2[rectId * RECT_BYTES + 2])) * width;
                        int ry2 = (j + (scale * (rect_x1y1x2y2[rectId * RECT_BYTES + 2] + rect_x1y1x2y2[rectId * RECT_BYTES + 3])))
                              * width;
                        rect_sum += ((weightedGrayImage[rx2 + ry2] - weightedGrayImage[rx1 + ry2] - weightedGrayImage[rx2 + ry1] + weightedGrayImage[rx1
                              + ry1]) * rect_w[rectId * RECT_FLOATS]);
                     }
                  }

                  float rect_sum2 = rect_sum * inv_area;

                  if (rect_sum2 < feature_LvRvThres[featureId * FEATURE_FLOATS + 2] * vnorm) {

                     if (feature_r1r2r3LnRn[featureId * FEATURE_INTS + 3] == -1) {
                        sum += feature_LvRvThres[featureId * FEATURE_FLOATS];
                        done = true;
                     } else {
                        featureId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + 3];
                     }
                  } else {
                     if (feature_r1r2r3LnRn[featureId * FEATURE_INTS + 4] == -1) {
                        sum += feature_LvRvThres[featureId * FEATURE_FLOATS + 1];
                        done = true;
                     } else {
                        featureId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + 4];
                     }
                  }
               }

             
            }

            if (sum > stage_thresh[stageId * STAGE_FLOATS]) {
               scaleIdList[gid] = scaleId;

            }

         }

      }

   }

   DetectorKernel kernel;

   private Device device;

   public AparapiDetector2(HaarCascade haarCascade, float baseScale, float scaleInc, float increment, boolean doCannyPruning) {
      super(haarCascade, baseScale, scaleInc, increment, doCannyPruning);
      device = Device.best();
      kernel = new DetectorKernel(haarCascade);
      kernel.setExplicit(true);
   }

   ScaleInfo scaleInfo = null;

   Range range = null;

   int[] defaultIds = null;

   int maxWorkItemSize;

   @Override List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {

      final List<Rectangle> features = new ArrayList<Rectangle>();
      if (scaleInfo == null) {
         scaleInfo = new ScaleInfo(width, height, maxScale);

         defaultIds = new int[scaleInfo.scaleIds];
         for (int i = 0; i < scaleInfo.scaleIds; i++) {
            defaultIds[i] = i;
         }
         kernel.scaleIdList = new int[scaleInfo.scaleIds];
         kernel.width = width;
         kernel.scale_ValueIJ = scaleInfo.scale_ValueIJ;
         kernel.scale_Width = scaleInfo.scale_Width;
         maxWorkItemSize = device.getMaxWorkItemSize()[0];
      }
      kernel.weightedGrayImage = weightedGrayImage;
      kernel.weightedGrayImageSquared = weightedGrayImageSquared;
      kernel.maxScaleId = scaleInfo.scaleIds;

      System.arraycopy(defaultIds, 0, kernel.scaleIdList, 0, scaleInfo.scaleIds);
      kernel.put(kernel.scaleIdList);

      for (kernel.stageId = 0; kernel.maxScaleId > 0 && kernel.stageId < haarCascade.stage_ids; kernel.stageId++) {
         range = device.createRange(kernel.maxScaleId + maxWorkItemSize - (kernel.maxScaleId % maxWorkItemSize));

         kernel.put(kernel.scaleIdList).execute(range).get(kernel.scaleIdList);
         int lastMaxScaleId = kernel.maxScaleId;
         kernel.maxScaleId=0;
         for (int i = 0; i < lastMaxScaleId; i++) {
            if (kernel.scaleIdList[i] >= 0) {
               kernel.scaleIdList[kernel.maxScaleId++] = kernel.scaleIdList[i];
            }
         }
      }
      if (kernel.maxScaleId > 0) {
         kernel.get(kernel.scaleIdList);
         for (int i = 0; i < kernel.maxScaleId; i++) {

            int scaleId = kernel.scaleIdList[i];
            if (scaleId >= 0) {
               int x = kernel.scale_ValueIJ[scaleId * kernel.SCALE_INTS + 1];
               int y = kernel.scale_ValueIJ[scaleId * kernel.SCALE_INTS + 2];
               int scaledFeatureWidth = kernel.scale_Width[scaleId];
               features.add(new Rectangle(x, y, scaledFeatureWidth, scaledFeatureWidth));
            }
         }

      }
      return (features);
   }

}
