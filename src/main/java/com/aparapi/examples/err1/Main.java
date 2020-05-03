package com.aparapi.examples.err1;
import com.aparapi.Kernel;
import com.aparapi.Range;

public class Main {
    public static void main(String[] args) {
        float[] data = {0};
        float[] result = {0};
        Range range = Range.create(1);
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                //result[0] = pow(data[0], 2);
                result[0] = exp(data[0] * log(2));  
                //result[0] = (float)pow((double)data[0], (double)2);
            }
        };
        for (;data[0]<100000f; data[0]+=1f){
            kernel.execute(range);
            if (result[0] != data[0] * data[0]) {
                System.out.println(data[0]+" BAD "+Math.abs(result[0] - data[0] * data[0]));
            }
        }
    }
}
