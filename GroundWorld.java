import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;

public class GroundWorld{
	
	private int gPictSize;       // la taille d'un sprite image pour le terrain
	private int gPictNb;         // le nombre d'images différentes pour le terrain 
	private Image[] groundPict;  // tableau avec toutes les images de terrain

	private int gWorldSizeX;      // taille de la map selon X
	private int gWorldSizeY;      // taille de la map selon Y

	private int[][] gWorld;      // map du monde
	private int[][] gWorldBuff;	 // buffer pour mise a jour synchronisee du monde

	public GroundWorld(int numberOfPictures, int pictureSize, int worldSizeX, int worldSizeY){

		gPictSize = pictureSize;
		gPictNb = numberOfPictures;
		gWorldSizeX = worldSizeX;
		gWorldSizeY = worldSizeY;

		gWorld = new int[gWorldSizeX][gWorldSizeY];
		gWorldBuff = new int[gWorldSizeX][gWorldSizeY];
		groundPict = new Image[numberOfPictures];

		// on importe toutes les images de terrains dans le tableau d'images
		for (int i = 0; i< gPictNb; i++){
			try
			{
				groundPict[i] = ImageIO.read(new File("terrain_"+i+".png"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
		}
	
		randomStart();
	}

	public GroundWorld(){
		this(3,32,30,20);
	}

	public void randomStart(){
		for ( int i = 0 ; i < gWorld.length ; i++ ){
			for ( int j = 0 ; j < gWorld[0].length ; j++ ){

				if (Math.random() < 0.1){       // 10% d'eau
					gWorld[i][j] = 0;
				}
				else if (Math.random() < 0.7){  // 60% d'herbe
					gWorld[i][j] = 1;
				}
				else {													// 30% de terre
					gWorld[i][j] = 2;
				}
				gWorldBuff[i][j] = gWorld[i][j];// initialisation du buffer dans le cas ou on aura besoin par la suite
			}	
		}
			
	}

	public void step(){
		for ( int i = 0 ; i < gWorld.length ; i++ ){
			for ( int j = 0 ; j < gWorld[0].length ; j++ ){
				if (gWorld[i][j] == 2){          // si on se trouve sur de la terre, on va faire pousser de l'herbe (2% chance)
					if (Math.random() < 0.00){ 
						gWorldBuff[i][j] = 1;
					}
				}
			}
		}

		copyBuffer();
	}
	
	public void copyBuffer(){
		for ( int i = 0 ; i < gWorld.length ; i++ )
			for ( int j = 0 ; j < gWorld[0].length ; j++ )
				gWorld[i][j] = gWorldBuff[i][j];
	}

	public int[][] getGWorld(){
		return gWorld;
	}

	public int getCell(int x, int y){
		if ((x < gWorldSizeX) && (y < gWorldSizeY))
			return gWorld[x][y];
		return -1;
	}

	public Image[] getGroundPict(){
		return groundPict;
	}

	public Image getPict(int i){
		if (i < gPictNb)
			return groundPict[i];
		return null;
	}
	
	public int getGWorldSizeX(){
		return gWorldSizeX;
	}

	public int getGWorldSizeY(){
		return gWorldSizeY;
	}

	public int getGPictSize(){
		return gPictSize;
	}




}

		


