package com.aparapi.examples.afmandel;

import com.aparapi.Range;

/**
 * Aparapi Fractals
 * 
 * the main class coordinates the GUI and kernel's executions
 * 
 * @author marco.stefanetti at gmail.com
 */
public class Main {
	
	protected class GoThread extends Thread {
		private double cx1 = -2;
		private double cy1 = -2;
		private double cx2 = +2;
		private double cy2 = +2;
		private double steps=1;
		
		public void init(double tx1, double ty1, double tx2, double ty2, double _steps) {
			cx1=tx1;
			cy1=ty1;
			cx2=tx2;
			cy2=ty2;
			steps=_steps;
		}

		public void interrupt() {
			running=false;
		}
		
		public void run() {
			running=true;
			go(cx1,cy1,cx2,cy2,steps);
			running=false;
		}
	}

	protected int W = 800;
	protected int H = 600;
	final protected int __MIN_ITERATIONS=512;
	protected int max_iterations = 512;

	protected int[] iterations;

	protected double cx1 = -2;
	protected double cy1 = -2;
	protected double cx2 = +2;
	protected double cy2 = +2;

	private AfGUI gui;

	// Aparapi
	private Range range;
	private AfKernel kernel;

	protected boolean running=false;
	protected GoThread got = new Main.GoThread();

	public Main() {

		// Aparapi
		kernel = new AfKernel();		

		// create the GUI, the GUI after resizing will calll the init
		gui = new AfGUI(this);

		System.out.println(kernel.toString());
		System.out.println(kernel.getTargetDevice().toString());
		System.out.println();
		System.out.println("double-click : recenter");
		System.out.println("mouse wheel  : zoom");
		System.out.println("mouse drag   : move");
		System.out.println();

	}
	
	public void init(int _W, int _H)
	{
		W=_W;
		H=_H;
		iterations = new int[W*H];
		
		// Aparapi
		range = Range.create(W*H);
		
		System.out.printf("Aparapi Fractals - canvas : %dx%d - max_iterations : %,d \n",W,H,max_iterations);

	}
	
	public void move(int x, int y, double zoom)
	{
		got.interrupt();
		
		double nx1 = cx1+x*(cx2-cx1)/W;
		double ny1 = cy1+y*(cy2-cy1)/H;
        
		double cw = zoom * (cx2-cx1);
		
        double ch = zoom * (cy2-cy1);
		//double ch = cw * (double) H / (double) W;  // always proportional
        
        // stop when too small
      //if(cw<0.0000000000001d)
		if(cw<0.000000000001d)
        {
        	System.out.printf("!!! Zoom limit !!! x range : %2.20f \n",cw);
        	return;
        }
        
        // too big or too far, the set is between -2 and 2
        if(        (cw>10) 
        		|| (nx1>2) 
        		|| (nx1<-2) 
        		|| (ch>10) 
        		|| (ny1>2) 
        		|| (ny1<-2) )
        	return;
        
        
        go( nx1 - 0.5f * cw, ny1 - 0.5f * ch, nx1 + 0.5f * cw, ny1 + 0.5f * ch);
		
	}
	
	private long go(double tx1, double ty1, double tx2, double ty2)
	{
		cx1 = tx1;
		cy1 = (ty2+ty1)/2d-0.5d*(tx2-tx1)*H/W;
		cx2 = tx2;
		cy2 = (ty2+ty1)/2d+0.5d*(tx2-tx1)*H/W;

		// while zooming increase iterations
        max_iterations = (int) (__MIN_ITERATIONS * ( 1d + 0.5d * Math.log(1/(cx2-cx1)) ) );
        if(max_iterations<__MIN_ITERATIONS)
        	max_iterations = __MIN_ITERATIONS;
        
        return refresh();
	}
	
	public long refresh() {
		
		long startTime = System.nanoTime();
		
		if(kernel==null)
			return 0;
		
		// Aparapi
		kernel.init(cx1, cy1, cx2, cy2, W, H, max_iterations, iterations);
		kernel.execute(range);

		long endTime = System.nanoTime();
        long timeElapsed = (endTime - startTime) / 1000000;
		System.out.printf("Mandelbrot %2.16f,%2.16f %2.16f,%2.16f - Iterations : %10d - Elapsed : %d ms \n",cx1,cy1,cx2,cy2,max_iterations,timeElapsed );

		gui.refresh();

		return timeElapsed;
 }
	
	
	public void threadGoHome(int steps)
	{
		threadGo(-2d,-2d,2d,2d,steps);	
	}
	
	public void threadGo(double tx1, double ty1, double tx2, double ty2, double steps) {
		
		got.interrupt();
		try {
			got.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		got=new GoThread();
		got.init(tx1, ty1, tx2, ty2, steps);
		got.start();
		
	}

	

	protected void go(double tx1, double ty1, double tx2, double ty2, double steps)  {

		// start center
		double sox = (cx2+cx1)/2d;
		double soy = (cy2+cy1)/2d;

		// initial with and height
		double dsx = (cx2-cx1);
		double dsy = (cy2-cy1);

		// target center
		double tox = (tx2+tx1)/2d;
		double toy = (ty2+ty1)/2d;

		// target with and height
		double dtx = (tx2-tx1);
		double dty = (ty2-ty1);

		/*
		 * d(0)=dsx
		 * ...
		 * d(n+1)=d(n)*f=d(0)*f^n
		 * ...
		 * d(N)=dtx=d(0)*f^N=dsx*f^N
		 * 
		 *  f^N=dtx/dsx f=(dtx/dsx)^(1/n)  
		 */
		double fx = Math.pow(dtx/dsx, 1/(steps-1));
		double fy = Math.pow(dty/dsy, 1/(steps-1));
		
		// f^n
		double fxn = 1;
		double fyn = 1;
		
		// new coordinates
		double nx1;
		double ny1;
		double nx2;
		double ny2;
		
		/*
		System.out.println("fx:"+fx+" fy:"+fy);
		System.out.println("dx:"+dsx);
		System.out.println("tx:"+(tx2-tx1));
		System.out.println(Math.pow(fx, steps-1));
		System.out.println((tx2-tx1)/dsx);
		*/
		
		double ndx;
		double ndy;
		
		double mx;
		double my;
		
		double nox;
		double noy;
		
		long min_sleep=20;
		
				for(double s=0;s<steps;s++)
				{
					if(!running)
						return;
					
					// activation function, to move fast but smootly on new center 
					mx = 2d*(1d/(1+Math.exp(-(35d*s)/steps))-0.5d);
					//System.out.println("mx:"+mx);
					my = mx;
					
					nox = sox+(tox-sox)*mx;
					noy = soy+(toy-soy)*my;
				
					ndx = dsx*fxn;
					ndy = dsy*fyn;
					
					nx1 = nox-ndx/2d;
					ny1 = noy-ndy/2d;
					nx2 = nox+ndx/2d;
					ny2 = noy+ndy/2d;
					long t = go(nx1,ny1,nx2,ny2);
					
					fxn*=fx;
					fyn*=fy;
					
					if(t<min_sleep)
						try {
							System.out.printf("sleeping %d ms ... \n",min_sleep-t);
							Thread.sleep(min_sleep-t);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					
				}

		go(tx1,ty1,tx2,ty2);
	}

	public static void main(String[] args){
		
		System.setProperty("com.aparapi.dumpProfilesOnExit", "true");

		Main f = new Main();
		f.threadGoHome(1);
  	      
	   }

	

}
