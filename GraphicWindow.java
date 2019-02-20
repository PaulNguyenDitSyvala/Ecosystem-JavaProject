
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphicWindow extends JPanel {

	private JFrame frame;
	private Image[] pictArray;
	private int pictSize;
	private int pictNb;
	private int[][] world;
	private int[][] bufferWorld;
	private int worldSize;

	public GraphicWindow(String title, int ws, int windowSizeX, int windowSizeY){

		pictNb = 4;
		pictArray = new Image[pictNb];

		for (int i = 0; i< pictNb; i++){
			try
			{
				pictArray[i] = ImageIO.read(new File(i+".png"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
		}

		frame = new JFrame(title); // creer une fenetre avec son nom
		frame.add(this); // ajoute l'image a cette fenetre
		frame.setSize(windowSizeX, windowSizeY); // définit la taille de la fenetre
		frame.setLocationRelativeTo(null); // place la fenetre au milieu de l'ecran
		frame.setVisible(true); // rend la fenetre visible
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //termine le processus en fermant cette fenetre
		
		pictSize = 32;
		worldSize = ws;
		world = new int[ws][ws];
		bufferWorld = new int[ws][ws];
		randomStep();

	}

		
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < world.length ; i++ )
			for ( int j = 0 ; j < world[0].length ; j++ )
			{
				g2.drawImage(pictArray[world[i][j]],pictSize*i,pictSize*j,pictSize,pictSize, frame);
			}
	}

	public void randomStep(){
		for ( int i = 0 ; i < world.length ; i++ )
			for ( int j = 0 ; j <world[0].length ; j++ )
				world[i][j] = 1;
	}

	public void step(){
		int nbFire = 0;
		for ( int i = 0 ; i < world.length ; i++ ){
			for ( int j = 0 ; j <world[0].length ; j++ ){
				switch (world[i][j]) {
					case 0 : 		bufferWorld[i][j] = 0;
											break;

					case 1 : 		if (Math.random() < 0.05){ // probabilité d'un arbre qui pousse
												bufferWorld[i][j] = 2;
											}
											else{
												bufferWorld[i][j] = 1;
											}
											break;

					case 2 : 		nbFire = 0;
											if (world[(i-1+worldSize)%worldSize][j]==3) {nbFire++;}
											if (world[(i+1+worldSize)%worldSize][j]==3) {nbFire++;}
											if (world[i][(j-1+worldSize)%worldSize]==3) {nbFire++;}					
											if (world[i][(j+1+worldSize)%worldSize]==3) {nbFire++;}

											if (nbFire > 0){
												bufferWorld[i][j] = 3;
											}
											else{
												if (Math.random() < 0.02){ // combustion spontanée 
													bufferWorld[i][j] = 3;
												}
												else{
													bufferWorld[i][j] = 2;
												}
											}
											break;

					case 3 : 		bufferWorld[i][j] = 1;
											break;
				}
			}
		}
		for ( int i = 0 ; i < world.length ; i++ ){
			for ( int j = 0 ; j <world[0].length ; j++ ){
				world[i][j] = bufferWorld[i][j];
			}
		}
		
	}


}

