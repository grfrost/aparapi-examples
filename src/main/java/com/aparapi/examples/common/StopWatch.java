package com.aparapi.examples.common;


public class StopWatch{
   String message = "timer";

   long start = 0L;

   public StopWatch(String _message) {
      message = _message;
      start();
   }

   public StopWatch() {
      this("timer");
   }

   public void start() {
      start = System.nanoTime();
   }

   public void stop() {
      print(message);
   }

   private long end() {
      return ((System.nanoTime() - start) / 1000000);
   }

   public void print(String _str) {
      System.out.println(_str + " " + end());
      start();
   }

}
