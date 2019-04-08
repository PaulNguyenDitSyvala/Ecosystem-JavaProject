	
import java.awt.Image;

public abstract class CellularAutomata{

	protected int[][] world;
	protected int[][] worldbuff;
	protected int sizeX;
	protected int sizeY;
	protected ImageList images;
	
	public CellularAutomata(int x, int y, int nbS, int nbF, String type){
	  sizeX = x;
	  sizeY = y;
	  world = new int[x][y];
	  worldbuff = new int[x][y];
	  if (type.equals("NoImages") == false)
	  	images = new ImageList(nbS,nbF,type);
	}
	
	public abstract void init(int mode);
	// mode 0 : all 0
	// mode 1 : all 1
	// mode 2 : Math.random()
	// mode 3+ : depend de l'automate cellulaire.
	
	
	
	public void init0() {
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				world[i][j] = 0;
				worldbuff[i][j] = 0;
			}
		}
	}
	

	public void init1() {
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				world[i][j] = 1;
				worldbuff[i][j] = 1;
			}
		}
	}

	public void init2(int nbStates) {
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				world[i][j] = (int)(Math.random()*nbStates);
				worldbuff[i][j] = world[i][j];
			}
		}
	}
	
	public abstract void step();
	
	public void set(int i, int j, int n){
	  if (i < sizeX && j < sizeY)
	    world[i][j] = n;
	}
	
	public int get(int i, int j){
	  int i2 = (i+sizeX)%sizeX;
	  int j2 = (j+sizeX)%sizeX;
	  return world[i2][j2];
	}
	
	public void setb(int i, int j, int n){
	  if (i < sizeX && j < sizeY)
	    worldbuff[i][j] = n;
	}
	
	public int getb(int i, int j){
	  return worldbuff[i][j];
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	
	public Image image(int i, int j) {
		return images.image(i, j);
	}
	
	public int getStates() {
		return images.getNbStates();
	}
	
	public void changeImageList(String s) {
		ImageList newList = new ImageList(images.getNbStates(), images.getNbFrames(), s);
		images = newList;
	}
	
}
