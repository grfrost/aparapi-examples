package com.aparapi.examples.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Blender {


    static Pattern vpattern = Pattern.compile("^ *v *([0-9.e]+) *([0-9.e]+) *([0-9.e]+) *$");
    static Pattern fpattern = Pattern.compile("^ *f *([0-9]+) *([0-9]+) *([0-9]+) *$");

    static void load(File f) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            final int MAX_VERT = 500;
            int verticesCount = 1;
            int vertices[] = new int[MAX_VERT];
            int facesCount = 1;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                Matcher matcher = vpattern.matcher(line);
                if (matcher.matches()) {
                    vertices[verticesCount++] = F32Vec3.createVec3(Float.parseFloat(matcher.group(1)), Float.parseFloat(matcher.group(2)), Float.parseFloat(matcher.group(3)));
                } else {
                    matcher = fpattern.matcher(line);
                    if (matcher.matches()) {
                        F32Triangle3D.createTriangle3D(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), 0xFF000 >> (facesCount++));
                    } else {
                        System.out.println("Skipping " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
