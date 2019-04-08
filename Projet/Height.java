
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Height extends CellularAutomata {
	
	private static final int originX = Constantes.originX;			// Coordonnées X du point en haut a gauche sur le nuage de Perlin
	private static final int originY = Constantes.originY;			// Coordonnées Y du point en haut a gauche sur le nuage de Perlin
	private static final String PerlinCloud = Constantes.PerlinCloud; 
	
	public Height(int x, int y, int nbS, int nbF, int mode) {
		super(x, y, nbS, nbF,"border");
		init(mode);
	}
	
	public void init(int mode) {
		switch (mode) {
			case 0 : 
				init0();
				break;
			case 1 : 
				init1();
				break;
			case 2 : 
				init2(10);
				break;
			case 3 : 
				init3(PerlinCloud);
				break;
				
				
		}
		
	}
	
	public void init3(String source) {
		BufferedImage nuagePerlin = null;
		int color = 0;
		try
		{
			nuagePerlin = ImageIO.read(new File("Images/"+source));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				color = nuagePerlin.getRGB(i+originX,j+originY);			// on prend la couleur du pixel (i,j) de l'image de nuage
				world[i][j] = (color & 0x000000ff);  	// on fait un masquage sur la couleur bleue pour avoir des altitudes realistes
				worldbuff[i][j] = world[i][j];

			}

		}
		
	}
	
	public void step() {
		// vide car les hauteurs ne sont pas modifiees au cours du temps
	}

	public int get2(int i, int j){
		return (world[i][j] / 7 -1);
	}
}
