package com.aparapi.examples.afmandel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * 
 * Aparapi Fractals
 * 
 * a JFrame, a JPanel and mouse handling
 * 
 * there is no Aparapi code here, only swing and mouse
 * 
 * @author marco.stefanetti at gmail.com
 * 
 */



public class AfGUI {

	// a pointer to the main to read calculated iterations and 
	private Main main;

	// colors palette is precalculated in colors[]
	private int __COLORS=128;
	private int __BLACK=Color.BLACK.getRGB();
	private int[] colors=new int[__COLORS];

	// swing
	private JFrame frame;
	private BufferedImage image;
	private Container contentPane; 
	private JPanel imagePanel;
	private JPanel inputPanel;

	// fields
	private JTextField tcx1;
	private JTextField tcy1;
	private JTextField tcx2;
	private JTextField tcy2;
	private JTextField tcdx;
	private JTextField tcdy;

	private JTextField tcW;
	private JTextField tcH;

	// mouse
	boolean dragging = false;
	private int px;
	private int py;
	

	public AfGUI(Main _main) {

		main = _main;
		
		for(int c=0;c<__COLORS;c++)
		{
			float hue = (float) c/ (float) __COLORS;
			float saturation = 1.0f;
			float brightness = 1.0f;
			Color color = Color.getHSBColor(hue, saturation, brightness);
			colors[c] = color.getRGB();
		}
		

		frame = new JFrame("Aparapai Fractals - Mandlbrot set");
		frame.setSize(main.W, main.H);
		frame.setPreferredSize(new Dimension(800,600));
		frame.setMinimumSize(new Dimension(100,100));
		frame.setBackground(Color.BLACK);
		
		contentPane = frame.getContentPane();
		
		// ----- image -----

		Border imageBevel = BorderFactory.createLoweredBevelBorder();
		JPanel imageBorderedPanel = new JPanel(new GridBagLayout());
        imageBorderedPanel.setBorder(imageBevel);
        
		imagePanel = new JPanel() {
			private static final long serialVersionUID = -2006337199526432552L;
			public void paint(Graphics g) {
				g.drawImage(image, 0, 0, this);
			}
		};
		imagePanel.setBackground(Color.BLACK);
		
		imageBorderedPanel.add(imagePanel,new GridBagConstraints(1,1,1,1,1,1,GridBagConstraints.LINE_START,GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0,0));
		//imageBorderedPanel.add(Box.createRigidArea(new Dimension(300, 1)),new GridBagConstraints(1,2,0,0,0,0,GridBagConstraints.LINE_START,GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0,0));


		// ----- inputs -----

		Border inputBevel = BorderFactory.createRaisedBevelBorder();
		JPanel inputBorderedPanel = new JPanel(new GridBagLayout());
		inputBorderedPanel.setBorder(inputBevel);

		inputPanel = new JPanel(); 
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		
		{
		JLabel title=new JLabel("Aparapi Fractals");
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(title);
		}
		
		inputPanel.add(Box.createRigidArea(new Dimension(1, 10)));

		JButton homeButton = new JButton("Home");
		JButton greenButton = new JButton("Green");
		JButton peterButton = new JButton("Peter");
		{
		JPanel buttons=new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(homeButton);
		buttons.add(Box.createRigidArea(new Dimension(10, 0)));
		buttons.add(greenButton);
		buttons.add(Box.createRigidArea(new Dimension(10, 0)));
		buttons.add(peterButton);
		buttons.add(Box.createRigidArea(new Dimension(10, 0)));
		buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(buttons);
		}
		
		inputPanel.add(Box.createRigidArea(new Dimension(1, 10)));

		inputPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		inputPanel.add(Box.createRigidArea(new Dimension(1, 10)));
		
		{
		JLabel complexplane=new JLabel("Complex Plane");
		complexplane.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(complexplane);
		}
		
		inputPanel.add(Box.createRigidArea(new Dimension(1, 10)));

		{
		JPanel c1p=new JPanel();
		c1p.setLayout(new BoxLayout(c1p, BoxLayout.X_AXIS));
		JLabel lcx1 = new JLabel("x1");
		c1p.add(lcx1);
		tcx1 = new JTextField(1);
		c1p.add(tcx1);
		c1p.add(Box.createRigidArea(new Dimension(10, 0)));
		JLabel lcy1 = new JLabel("y1");
		c1p.add(lcy1);
		tcy1 = new JTextField(1);
		c1p.add(tcy1);
		c1p.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(c1p);
		}
		
		{
		JPanel c2p=new JPanel();
		c2p.setLayout(new BoxLayout(c2p, BoxLayout.X_AXIS));
		JLabel lcx2 = new JLabel("x2");
		c2p.add(lcx2);
		tcx2 = new JTextField(1);
		c2p.add(tcx2);
		c2p.add(Box.createRigidArea(new Dimension(10, 0)));
		JLabel lcy2 = new JLabel("y2");
		c2p.add(lcy2);
		tcy2 = new JTextField(1);
		c2p.add(tcy2);
		c2p.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(c2p);
		}
		
		{
		JPanel cd=new JPanel();
		cd.setLayout(new BoxLayout(cd, BoxLayout.X_AXIS));
		JLabel cdx = new JLabel("dx");
		cd.add(cdx);
		tcdx = new JTextField(1);
		cd.add(tcdx);
		cd.add(Box.createRigidArea(new Dimension(10, 0)));
		JLabel cdy = new JLabel("dy");
		cd.add(cdy);
		tcdy = new JTextField(1);
		cd.add(tcdy);
		cd.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(cd);
		}
		
		inputPanel.add(Box.createRigidArea(new Dimension(1, 10)));

		inputPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		inputPanel.add(Box.createRigidArea(new Dimension(1, 10)));
		
		JLabel canvas=new JLabel("Image Canvas");
		canvas.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(canvas);
		
		inputPanel.add(Box.createRigidArea(new Dimension(1, 10)));

		{
		JPanel cp=new JPanel();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		JLabel cW = new JLabel(" W");
		cp.add(cW);
		tcW = new JTextField(1);
		cp.add(tcW);
		cp.add(Box.createRigidArea(new Dimension(10, 0)));
		JLabel cH = new JLabel(" H");
		cp.add(cH);
		tcH = new JTextField(1);
		cp.add(tcH);
		cp.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(cp);
		}
		

		inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		inputPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		
		inputBorderedPanel.add(inputPanel,new GridBagConstraints(1,1,1,1,1,1,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5), 0,0));
		
	
		// ----- frame -----

		contentPane.setLayout(new GridBagLayout());
		contentPane.add(imageBorderedPanel, new GridBagConstraints(1,1,1,1,0.8,1,GridBagConstraints.NORTH,GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0,0));
		contentPane.add(inputBorderedPanel, new GridBagConstraints(2,1,1,1,0.2,1,GridBagConstraints.NORTH,GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0,0));

		frame.pack();

		//frame.setSize( (int)(main.W*1.2d), main.H );


		frame.setLocationByPlatform( true );
		frame.setVisible(true);
		
		squareCanvas();


		// events
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	
		    	/* seems to work, but bad resfresh effect
		    	if(frame.getWidth()<300)
		    		frame.setSize(300, frame.getHeight());
		    	if(frame.getHeight()<300)
		    		frame.setSize(frame.getWidth(), 300);
*/
		    	
		    	resize();
				main.move(main.W / 2, main.H / 2, 1);
		    }
		});

		homeButton.addActionListener(new ActionListener() {
		    @Override
			public void actionPerformed(ActionEvent e) {
     			main.threadGoHome(10);				
			}

		});
		
		peterButton.addActionListener(new ActionListener() {
		    @Override
			public void actionPerformed(ActionEvent e) {
		    	//main.threadGo(0.4501805067076704d,-0.4073023947870866d, 0.4521069201493164d,-0.4057202510358668d, 80d);		
     			main.threadGoHome(10);				
		    	main.threadGo(0.1329154829709444d,0.6706861374788056d,0.1329154829710550d,0.6706861374789002d,100d);
			}

		});
		
		greenButton.addActionListener(new ActionListener() {
		    @Override
			public void actionPerformed(ActionEvent e) {
				//main.threadGo(-1.4048611937118272d,0.1249659025968425d,-1.4048596113288580d,0.1249672021918686d,100d);
		    	//main.threadGo(-0.7379598288771779d,-0.2191410537220759d,-0.7379598288770718d,-0.2191410537219850d,80d);
		    	
		    	//main.threadGo(-0.750222d,0.031161d,-0.749191d,0.031752d,100d);
     			main.threadGoHome(10);				
		    	main.threadGo(-0.7499210743120112d,0.0315822442174014d,-0.7499210743119092d,0.0315822442175254d,100d); 
		    	
			}
		});
		
		imagePanel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				e.consume();

				int amount = (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) ? e.getUnitsToScroll()
						: (e.getWheelRotation() < 0 ? -1 : 1);

				Logger.getGlobal().info( String.format("wheel %d \n", amount) );

				double zoom = 1f + ((double) amount * 5d / 100d);
				main.move(main.W / 2, main.H / 2, zoom);
				
			}
		});

		imagePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    Logger.getGlobal().info("Double Click");
    				main.move(e.getPoint().x, main.H - e.getPoint().y -1, 1f);
				}

			}
		
			@Override
			public void mousePressed(MouseEvent e) {
				Logger.getGlobal().info(String.format("mouse pressed %d %d \n", e.getPoint().x, e.getPoint().y));
				px = e.getPoint().x;
				py = e.getPoint().y;
				dragging=true;
			}

			 @Override
			    public void mouseReleased(MouseEvent event) {
			 
				 dragging = false;
			 }

		});

		imagePanel.addMouseMotionListener(new MouseMotionAdapter() {
			
			
			 @Override
			 public void mouseDragged(MouseEvent e) {
				 
				 if(dragging)
				 {
					 int nx = e.getPoint().x;
					 int ny = e.getPoint().y;
					 
					 int dx = px - nx;
					 int dy = py - ny;					 
						 
					 Logger.getGlobal().info(String.format("mouse drag %d,%d \n", dx, dy));
					 main.move(main.W/2 + dx, main.H/2 - dy, 1f);
					 
					 px=nx;
					 py=ny;
				 }
			 } 
			 
		});

		
		resize();
	}
	
	public void squareCanvas()
	{
		// try to have a squared image
		int fw = frame.getWidth();
		int fh = frame.getHeight();
		int cw = imagePanel.getWidth();
		int ch = imagePanel.getHeight();
		frame.setSize( fw, fh -ch + cw);
	}


	public void resize()
	{
        int W = imagePanel.getWidth();
        if(W<100)
        	W=100;
        int H = imagePanel.getHeight();
        if(H<100)
			H=100;
        image = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        main.init(W, H);
	}

	public void setColor(int i, int j, int iterations) {

		int c = colors[iterations%__COLORS];
		
		// the Mandelbrot set is black
		if (iterations >= main.max_iterations) 
			c = __BLACK;

		image.setRGB(i, main.H - j -1, c);

	}

	public void refresh() {

		for (int k = 0; k < main.iterations.length; k++)
			setColor(k % main.W, k / main.W, main.iterations[k]);
		
		imagePanel.update(imagePanel.getGraphics());
		
		//frame.repaint();
		
		tcx1.setText(String.format("%2.16f",main.cx1));
		tcy1.setText(String.format("%2.16f",main.cy1));
		tcx2.setText(String.format("%2.16f",main.cx2));
		tcy2.setText(String.format("%2.16f",main.cy2));
		tcdx.setText(String.format("%2.16f",main.cx2-main.cx1));
		tcdy.setText(String.format("%2.16f",main.cy2-main.cy1));

		tcW.setText(String.format("%5d",main.W));
		tcH.setText(String.format("%5d",main.H));
	}

}
