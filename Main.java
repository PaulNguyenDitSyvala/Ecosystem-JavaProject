

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Main {
	public static void main(String[] args) {
		GraphicWindow screen = new GraphicWindow("Ecosysteme", 70, 600, 600);
		while(true){
		
			screen.step();
			try {
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) {
			}
			screen.repaint();

		}
	}
}
