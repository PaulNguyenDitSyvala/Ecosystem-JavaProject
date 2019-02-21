import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;

public class ForestWorld{
	
	private int fPictSizeX;       // la taille d'un sprite image pour le terrain
	private int fPictSizeY;
	private int fPictNb;         // le nombre d'images différentes pour le terrain 
	private Image[] forestPict;  // tableau avec toutes les images de terrain

	private int fWorldSizeX;      // taille de la map selon X
	private int fWorldSizeY;      // taille de la map selon Y

	private int[][] fWorld;      // map du monde
	private int[][] fWorldBuff;	 // buffer pour mise a jour synchronisee du monde

	private GroundWorld gw;

	public ForestWorld(int numberOfPictures, int pictureSizeX, int pictureSizeY, int worldSizeX, int worldSizeY, GroundWorld ground ){

		fPictSizeX = pictureSizeX;
		fPictSizeY = pictureSizeY;
		fPictNb = numberOfPictures;
		fWorldSizeX = worldSizeX;
		fWorldSizeY = worldSizeY;
		gw = ground;

		fWorld = new int[fWorldSizeX][fWorldSizeY];
		fWorldBuff = new int[fWorldSizeX][fWorldSizeY];
		forestPict = new Image[numberOfPictures];

		// on importe toutes les images de terrains dans le tableau d'images
		for (int i = 0; i< fPictNb; i++){
			try
			{
				forestPict[i] = ImageIO.read(new File("tree_"+i+".png"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
		}
	
		randomStart();
	}

	public ForestWorld(GroundWorld gw){
		this(4,42,96,30,20,gw);
	}

	public void randomStart(){
		for ( int i = 0 ; i < fWorld.length ; i++ ){
			for ( int j = 0 ; j < fWorld[0].length ; j++ ){
				if (gw.getCell(i,j) == 0){    // si le terrain est de l'eau, on mettra la case innacessible pour les arbres
					fWorld[i][j] = 0;
				}
				else{
					if (Math.random() < 0.7){   // on va planter un arbre
						fWorld[i][j] = 1;
					}
					else{
						fWorld[i][j] = 0;
					}
				}
				fWorldBuff[i][j] = fWorld[i][j];// initialisation du buffer dans le cas ou on aura besoin par la suite

			}
		}

		fWorld[fWorldSizeX/2][fWorldSizeY/2] = 2;
		fWorldBuff[fWorldSizeX/2][fWorldSizeY/2] = 2;

	}
						

	public void step(){
		int nbFire = 0;
		for ( int i = 0 ; i < fWorld.length ; i++ ){
			for ( int j = 0 ; j < fWorld[0].length ; j++ ){
				if (fWorld[i][j] == 3){
					fWorldBuff[i][j] = 0;
				}
				else if (fWorld[i][j] == 2){
					fWorldBuff[i][j] = 3;
				}
				else if (fWorld[i][j] == 1){ 
					nbFire = 0;
					if (fWorld[(i-1+fWorldSizeX)%fWorldSizeX][j]==3) {nbFire++;}
					if (fWorld[(i+1+fWorldSizeX)%fWorldSizeX][j]==3) {nbFire++;}
					if (fWorld[i][(j-1+fWorldSizeY)%fWorldSizeY]==3) {nbFire++;}					
					if (fWorld[i][(j+1+fWorldSizeY)%fWorldSizeY]==3) {nbFire++;}

					if (nbFire > 0){
						fWorldBuff[i][j] = 2;
					}
					else{
						fWorldBuff[i][j] = 1;
						if (Math.random() < 0.00){ // combustion spontanée 
							fWorldBuff[i][j] = 2;
						}
					}
				}
				else if (fWorld[i][j] == 0){ 
					fWorldBuff[i][j] = 0;
				}
				else{
				}

			}
		}

		copyBuffer();
	}
	
	public void copyBuffer(){
		for ( int i = 0 ; i < fWorld.length ; i++ )
			for ( int j = 0 ; j < fWorld[0].length ; j++ )
				fWorld[i][j] = fWorldBuff[i][j];
	}

	public int[][] getFWorld(){
		return fWorld;
	}

	public int getCell(int x, int y){
		if ((x < fWorldSizeX) && (y < fWorldSizeY))
			return fWorld[x][y];
		return -1;
	}

	public Image[] getForestPict(){
		return forestPict;
	}

	public Image getPict(int i){
		if (i < fPictNb)
			return forestPict[i];
		return null;
	}
	
	public int getFWorldSizeX(){
		return fWorldSizeX;
	}

	public int getFWorldSizeY(){
		return fWorldSizeY;
	}

	public int getFPictSizeX(){
		return fPictSizeX;
	}

	public int getFPictSizeY(){
		return fPictSizeY;
	}


}

		


