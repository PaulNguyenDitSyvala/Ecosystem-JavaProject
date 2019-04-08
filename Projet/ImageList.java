
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageList {
	private int nbStates;
	private int nbFrames;
	private Image[][] tabImages;
	
	public ImageList(int nbS, int nbF, String type){
		nbStates = nbS;
		nbFrames = nbF;
		tabImages = new Image[nbStates][nbFrames];

		
		if (nbF != 1 ) {
			for (int i = 0; i < nbStates; i++) {
				for (int j = 0; j < nbFrames; j++) {
					try
					{
						tabImages[i][j] = ImageIO.read(new File("Images/"+type+"_"+i+"_"+j+".png"));
					}
					catch(Exception e)
					{
						e.printStackTrace();
						System.out.println("i : "+i+"     j : "+j+"    Images/"+type+"_"+i+"_"+j+".png");
						System.exit(-1);
					}
				}
			}
		}
		else {
			for (int i = 0; i < nbStates; i++) {
				try
				{
					tabImages[i][0] = ImageIO.read(new File("Images/"+type+"_"+i+".png"));
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
		
		
		
	}
	
	public Image image(int i, int j) {
		return tabImages[i][j];
	}
	
	public int getNbFrames() {
		return nbFrames;
	}
	
	public int getNbStates() {
		return nbStates;
	}
	
}
