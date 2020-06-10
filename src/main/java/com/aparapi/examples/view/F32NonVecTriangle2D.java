package com.aparapi.examples.view;

public class F32NonVecTriangle2D {

        static final int SIZE = 6;
        static final int X0 = 0;
        static final int Y0 = 1;
        static final int X1 = 2;
        static final int Y1 = 3;
        static final int X2 = 4;
        static final int Y2 = 5;
        public static int MAX = 10000;
        public static int count = 0;

        public static float[] entries = new float[MAX * SIZE];
        public static int[] colors = new int[MAX];

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

        static int createTriangle(float x0, float y0, float x1, float y1, float x2, float y2, int col) {
            entries[count * SIZE + X0] = x0;
            entries[count * SIZE + Y0] = y0;
            // We need the triangle to be clock wound
            if (side(x0, y0, x1, y1, x2, y2) > 0) {
                entries[count * SIZE + X1] = x1;
                entries[count * SIZE + Y1] = y1;
                entries[count * SIZE + X2] = x2;
                entries[count * SIZE + Y2] = y2;
            } else {
                entries[count * SIZE + X1] = x2;
                entries[count * SIZE + Y1] = y2;
                entries[count * SIZE + X2] = x1;
                entries[count * SIZE + Y2] = y1;
            }
            colors[count] = col;
            return count++;
        }

}
