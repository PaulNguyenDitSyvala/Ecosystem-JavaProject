
public class Constantes {
	
	//Main.java
	public static final int SLEEP = 150;					// le temps entre chaque "step" en ms
	
	//GraphicWindow.java
	public static final int SIZE_X = 60;				// largeur du monde en nb de cases
	public static final int SIZE_Y = 40;				// hauteur du monde en nb de cases
	public static final int SIZE_SPRITE = 40;			// taille des cases en pixels
	public static final int WINDOW_X = SIZE_X*SIZE_SPRITE;		// taille de la fenetre
	public static final int WINDOW_Y = SIZE_Y*SIZE_SPRITE;
	public static final int NB_RANDOM_AGENTS = 0;			// le nombre d'agents au hazard
	public static final int NB_FOX = 20;				// le nombre de renards
	public static final int NB_CHICKEN = 40;			// le nombre de poulets
	public static final int NB_SHEEP = 40;				// le nombre de moutons
	
	//Forest.java
	public static final double pTree = 0.4;			// probabilité d'arbre au départ
	public static final double pBurn = 0.005;			// probabilité de combustion spontanee d'un arbre
	public static final double pRepopTree = 0.01;		// probabilité de repousse d'un arbre
	
	//Ground.java
	public static final double pGrass = 0.4;			// probabilité d'herbe au départ
	public static final double pRepopGrass = 0.002;			// probabilité que l'herbe repousse
	
	//Height.java
	public static final int originX = 200;				// Coordonnées X du point en haut a gauche sur le nuage de Perlin
	public static final int originY = 100; 				// Coordonnées Y du point en haut a gauche sur le nuage de Perlin
	public static final String PerlinCloud = "test_nebula2.png";	 // nuage utilise pour la generation de terrain
}
