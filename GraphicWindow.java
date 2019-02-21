
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
	private GroundWorld gw;
	private ForestWorld fw;

	public GraphicWindow(String title, int ws, int windowSizeX, int windowSizeY){

		frame = new JFrame(title); // creer une fenetre avec son nom
		frame.add(this); // ajoute l'image a cette fenetre
		frame.setSize(windowSizeX, windowSizeY); // définit la taille de la fenetre
		frame.setLocationRelativeTo(null); // place la fenetre au milieu de l'ecran
		frame.setVisible(true); // rend la fenetre visible
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //termine le processus en fermant cette fenetre
		
		gw = new GroundWorld();
		fw = new ForestWorld(gw);
	}

	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		drawGround(g2);
		drawForest(g2);
	}

	public void drawGround(Graphics2D g2){
		for ( int i = 0 ; i < gw.getGWorldSizeX() ; i++ ){
			for ( int j = 0 ; j < gw.getGWorldSizeY() ; j++ )
			{
				g2.drawImage(gw.getPict(gw.getCell(i,j)),gw.getGPictSize()*i,gw.getGPictSize()*j,gw.getGPictSize(),gw.getGPictSize(), frame);
			}
		}
	}

	public void drawForest(Graphics2D g2){
		for ( int j = 0 ; j < fw.getFWorldSizeY() ; j++ ){
			for ( int i = 0 ; i < fw.getFWorldSizeX() ; i++ )
			{
				g2.drawImage(fw.getPict(fw.getCell(i,j)),gw.getGPictSize()*i-5,gw.getGPictSize()*j-64,fw.getFPictSizeX(),fw.getFPictSizeY(), frame);
			}
		}
	}


	public void step(){
		gw.step();
		fw.step();
	}


}

