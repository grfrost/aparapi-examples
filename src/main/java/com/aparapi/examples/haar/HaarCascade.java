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
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class HaarCascade{

   private class Stage{
      final int id;

      final List<Tree> trees = new ArrayList<Tree>();

      final float threshold;

      public Stage(int _id, float _threshold) {
         id = _id;
         threshold = _threshold;
      }

      public Tree addTree(Tree tree) {
         trees.add(tree);
         return (tree);
      }
   }

   private class Tree{
      final int id;

      final List<Feature> features = new ArrayList<Feature>();

      public Tree(int _id) {
         id = _id;
      }

      public Feature addFeature(Feature feature) {
         features.add(feature);
         return (feature);
      }
   }

   private class Feature{

      final int id;

      final List<Rect> rects = new ArrayList<Rect>();

      final float threshold;

      final float left_val;

      final float right_val;

      final int left_node;

      final int right_node;

      final Tree tree;

      public Feature(int _id, Tree _tree, float _threshold, float _left_val, int _left_node, float _right_val, int _right_node) {
         id = _id;
         tree = _tree;

         threshold = _threshold;
         left_val = _left_val;
         left_node = _left_node;
         right_val = _right_val;
         right_node = _right_node;
      }

      public Rect add(Rect rect) {
         rects.add(rect);
         return (rect);
      }

   }

   private class Rect{
      final int id; // we use this to access from global parallel arrays

      final byte x1, x2, y1, y2;

      final float weight;

      public Rect(int _id, byte _x1, byte _x2, byte _y1, byte _y2, float _weight) {
         id = _id;
         x1 = _x1;
         x2 = _x2;
         y1 = _y1;
         y2 = _y2;
         weight = _weight;
      }
   }

   final static int FEATURE_INTS = 5;

   final static int FEATURE_FLOATS = 3;

   int[] feature_r1r2r3LnRn;

   float[] feature_LvRvThres;

   int feature_ids;

   final static int RECT_BYTES = 4;

   final static int RECT_FLOATS = 1;

   byte rect_x1y1x2y2[];

   float rect_w[];

   int rect_ids;

   final static int STAGE_INTS = 2;

   final static int STAGE_FLOATS = 1;

   int stage_ids;

   int stage_startEnd[];

   float stage_thresh[];

   final static int TREE_INTS = 2;

   int tree_ids;

   int tree_startEnd[];

   /** The list of classifiers that the test image should pass to be considered as an image.*/
   int[] stageIds;

   int cascadeWidth;

   int cascadeHeight;

   /**Factory method. Builds a detector from an XML file.
    * @param filename The XML file (generated by OpenCV) describing the HaarCascade.
    * @return The corresponding detector.
    */
   public static HaarCascade create(String filename) {

      org.jdom.Document document = null;
      SAXBuilder sxb = new SAXBuilder();
      try {
         document = sxb.build(new File(filename));
      } catch (Exception e) {
         e.printStackTrace();
      }

      return new HaarCascade(document);

   }

   public static HaarCascade create(InputStream is) {

      org.jdom.Document document = null;
      SAXBuilder sxb = new SAXBuilder();
      try {
         document = sxb.build(is);
      } catch (Exception e) {
         e.printStackTrace();
      }

      return new HaarCascade(document);

   }

   /** Detector constructor.
    * Builds, from a XML document (i.e. the result of parsing an XML file, the corresponding Haar cascade.
    * @param document The XML document (parsing of file generated by OpenCV) describing the Haar cascade.
    * 
    * http://code.google.com/p/jjil/wiki/ImplementingHaarCascade
    */

   static class Itr implements Iterable<Element>{
      Element element;

      Itr(Element _element) {
         element = _element;
      }

      @SuppressWarnings("unchecked") @Override public Iterator<Element> iterator() {
         return (element.getChildren("_").iterator());
      }

   }

   /** Detector constructor.
    * Builds, from a XML document (i.e. the result of parsing an XML file, the corresponding Haar cascade.
    * @param document The XML document (parsing of file generated by OpenCV) describing the Haar cascade.
    * 
    * http://code.google.com/p/jjil/wiki/ImplementingHaarCascade
    */
   public HaarCascade(Document document) {
      List<Tree> tree_instances = new ArrayList<Tree>();
      List<Feature> feature_instances = new ArrayList<Feature>();
      List<Rect> rect_instances = new ArrayList<Rect>();
      List<Stage> stage_instances = new ArrayList<Stage>();
      List<Stage> stageList = new LinkedList<Stage>();

      Element racineElement = (Element) document.getRootElement().getChildren().get(0);
      Element sizeElement = racineElement.getChild("size");
      String[] dims = sizeElement.getTextTrim().split(" ");
      cascadeWidth = Integer.parseInt(dims[0]);
      cascadeHeight = Integer.parseInt(dims[1]);
      Element stagesElement = racineElement.getChild("stages");
      for (Element stageElement : new Itr(stagesElement)) {
         Element stageThresholdElement = stageElement.getChild("stage_threshold");
         Stage stage = new Stage(stage_ids++, Float.parseFloat(stageThresholdElement.getText()));
         stage_instances.add(stage);
         Element treesElement = stageElement.getChild("trees");
         for (Element treeElement : new Itr(treesElement)) {
            Tree tree = new Tree(tree_ids++);
            tree_instances.add(stage.addTree(tree));
            for (Element featureElement : new Itr(treeElement)) {
               Element leftNodeElement = featureElement.getChild("left_node");
               Element rightNodeElement = featureElement.getChild("right_node");
               Element rightValElement = featureElement.getChild("right_val");
               Element leftValElement = featureElement.getChild("left_val");
               Feature feature = new Feature(feature_ids++, tree,//
                     Float.parseFloat(featureElement.getChild("threshold").getText()),//
                     (leftValElement != null) ? Float.parseFloat(leftValElement.getText()) : 0f,//
                     (leftNodeElement != null) ? Integer.parseInt(leftNodeElement.getText()) : -1,//
                     (rightValElement != null) ? Float.parseFloat(rightValElement.getText()) : 0f,//
                     (rightNodeElement != null) ? Integer.parseInt(rightNodeElement.getText()) : -1// 
               );
               feature_instances.add(tree.addFeature(feature));
               Element rectsElement = featureElement.getChild("feature").getChild("rects");
               for (Element rectElement : new Itr(rectsElement)) {
                  String[] rectValues = rectElement.getTextTrim().split(" ");
                  Rect rect = new Rect(rect_ids++, (byte)Integer.parseInt(rectValues[0]), (byte)Integer.parseInt(rectValues[1]),
                        (byte)Integer.parseInt(rectValues[2]), (byte)Integer.parseInt(rectValues[3]), Float.parseFloat(rectValues[4]));
                  rect_instances.add(feature.add(rect));
               }
            }

         }
         stageList.add(stage);
      }

      // now we take the above generated data structure apart and create a data parallel friendly form. 

      stageIds = new int[stageList.size()];
      for (int i = 0; i < stageIds.length; i++) {
         stageIds[i] = stageList.get(i).id;
      }

      rect_x1y1x2y2 = new byte[rect_ids * RECT_BYTES];
      rect_w = new float[rect_ids * RECT_FLOATS];
      for (int i = 0; i < rect_ids; i++) {
         Rect r = rect_instances.get(i);
         rect_w[i * RECT_FLOATS + 0] = r.weight;
         rect_x1y1x2y2[i * RECT_BYTES + 0] = r.x1;
         rect_x1y1x2y2[i * RECT_BYTES + 1] = r.y1;
         rect_x1y1x2y2[i * RECT_BYTES + 2] = r.x2;
         rect_x1y1x2y2[i * RECT_BYTES + 3] = r.y2;
      }

      feature_r1r2r3LnRn = new int[feature_ids * FEATURE_INTS];
      feature_LvRvThres = new float[feature_ids * FEATURE_FLOATS];
      for (int i = 0; i < feature_ids; i++) {
         Feature f = feature_instances.get(i);
         feature_LvRvThres[i * FEATURE_FLOATS + 0] = f.left_val;
         feature_LvRvThres[i * FEATURE_FLOATS + 1] = f.right_val;
         feature_LvRvThres[i * FEATURE_FLOATS + 2] = f.threshold;
         feature_r1r2r3LnRn[i * FEATURE_INTS + 0] = (f.rects.size() > 0) ? f.rects.get(0).id : -1;
         feature_r1r2r3LnRn[i * FEATURE_INTS + 1] = (f.rects.size() > 1) ? f.rects.get(1).id : -1;
         feature_r1r2r3LnRn[i * FEATURE_INTS + 2] = (f.rects.size() > 2) ? f.rects.get(2).id : -1;
         feature_r1r2r3LnRn[i * FEATURE_INTS + 3] = (f.left_node == -1) ? -1 : f.tree.features.get(f.left_node).id;
         feature_r1r2r3LnRn[i * FEATURE_INTS + 4] = (f.right_node == -1) ? -1 : f.tree.features.get(f.right_node).id;
      }

      tree_startEnd = new int[tree_ids * TREE_INTS];
      System.out.println("tree_ids="+tree_ids);

      for (int i = 0; i < tree_ids; i++) {
         Tree t = tree_instances.get(i);
         tree_startEnd[i * TREE_INTS + 0] = t.features.get(0).id;
         tree_startEnd[i * TREE_INTS + 1] = t.features.get(t.features.size() - 1).id;
      }

      stage_startEnd = new int[stage_ids * STAGE_INTS];
      stage_thresh = new float[stage_ids * STAGE_FLOATS];
      for (int i = 0; i < stage_ids; i++) {
         Stage t = stage_instances.get(i);
         stage_startEnd[i * STAGE_INTS + 0] = t.trees.get(0).id;
         stage_startEnd[i * STAGE_INTS + 1] = t.trees.get(t.trees.size() - 1).id;
         stage_thresh[i * STAGE_FLOATS + 0] = t.threshold;
      }
   }

   boolean pass(int stageId, int[] grayImage, int[] squares, int width, int height, int i, int j, float scale) {

      float sum = 0;
      for (int treeId = stage_startEnd[stageId * STAGE_INTS + 0]; treeId <= stage_startEnd[stageId * STAGE_INTS + 1]; treeId++) {

         //  System.out.println("stage id " + stageId + "  tree id" + treeId);
         int featureId = tree_startEnd[treeId * TREE_INTS + 0];
         float thresh = 0f;
         boolean done = false;
         while (!done) {
            //  System.out.println("feature id "+featureId);

            int w = (int) (scale * cascadeWidth);
            int h = (int) (scale * cascadeHeight);
            double inv_area = 1. / (w * h);
            //System.out.println("w2 : "+w2);
            int total_x = grayImage[i + w + (j + h) * width] + grayImage[i + (j) * width] - grayImage[i + (j + h) * width]
                  - grayImage[i + w + (j) * width];
            int total_x2 = squares[i + w + (j + h) * width] + squares[i + (j) * width] - squares[i + (j + h) * width]
                  - squares[i + w + (j) * width];
            double moy = total_x * inv_area;
            double vnorm = total_x2 * inv_area - moy * moy;
            vnorm = (vnorm > 1) ? Math.sqrt(vnorm) : 1;
            // System.out.println(vnorm);
            int rect_sum = 0;
            for (int r = 0; r < 3; r++) {
               int rectId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + r];
               if (rectId != -1) {
                  // System.out.println("rect " + r + " id " + rectId);
                  int x1 = rect_x1y1x2y2[rectId * RECT_BYTES + 0];
                  int y1 = rect_x1y1x2y2[rectId * RECT_BYTES + 1];
                  int x2 = rect_x1y1x2y2[rectId * RECT_BYTES + 2];
                  int y2 = rect_x1y1x2y2[rectId * RECT_BYTES + 3];
                  float weight = rect_w[rectId * RECT_FLOATS + 0];
                  int rx1 = i + (int) (scale * x1);
                  int rx2 = i + (int) (scale * (x1 + y1));
                  int ry1 = j + (int) (scale * x2);
                  int ry2 = j + (int) (scale * (x2 + y2));
                  //System.out.println((rx2-rx1)*(ry2-ry1)+" "+r.weight);
                  rect_sum += (int) ((grayImage[rx2 + (ry2) * width] - grayImage[rx1 + (ry2) * width]
                        - grayImage[rx2 + (ry1) * width] + grayImage[rx1 + (ry1) * width]) * weight);
               }
            }
            // System.out.println(rect_sum);
            double rect_sum2 = rect_sum * inv_area;

            // System.out.println(rect_sum2+" "+ Feature.LvRvThres[featureId * Feature.FLOATS + 2]*vnorm);  

            if (rect_sum2 < feature_LvRvThres[featureId * FEATURE_FLOATS + 2] * vnorm) {

               int leftNodeId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + 3];
               if (leftNodeId == -1) {
                  //  System.out.println("left-val");
                  thresh = feature_LvRvThres[featureId * FEATURE_FLOATS + 0];
                  done = true;
               } else {
                  // System.out.println("left");
                  featureId = leftNodeId;
               }
            } else {
               int rightNodeId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + 4];
               if (rightNodeId == -1) {
                  // System.out.println("right-val");
                  thresh = feature_LvRvThres[featureId * FEATURE_FLOATS + 1];
                  done = true;
               } else {
                  //  System.out.println("right");
                  featureId = rightNodeId;
               }
            }
         }

         sum += thresh;
      }
      //System.out.println(sum+" "+threshold);

      return sum > stage_thresh[stageId * STAGE_FLOATS + 0];
   }

   public Rectangle getFeature(int[] grayImage, int[] squares, int width2, int height2, int i_final, int j_final,
         float scale_final, int size_final) {
      boolean pass = true;
      Rectangle rectangle = null;
      for (int stageId : stageIds) {
         if (!pass(stageId, grayImage, squares, width2, height2, i_final, j_final, scale_final)) {
            pass = false;
            break;
         }
      }
      if (pass) {

         rectangle = new Rectangle(i_final, j_final, size_final, size_final);

      }
      return (rectangle);
   }

}
