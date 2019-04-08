
public class Ground extends CellularAutomata {

	private static final double pGrass = Constantes.pGrass;				// probabilité d'herbe au départ
	private static final double pRepopGrass = Constantes.pRepopGrass; 	// probabilité que l'herbe repousse
	
	
	private Height h;
	
	public Ground(int x, int y, int nbS, int nbF, int mode, Height h) {
		super(x, y, nbS, nbF,"terrain");
		this.h = h;
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
				init2(getStates());
				break;
			case 3 : 
				init3();
				break;
				
				
		}
		
	}
	
	public void init3() {

		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {

				// altitude < 0 : en dessous du niveau de la mer -> ocean
				if (h.get(i, j)/7-1 < 0) {
					world[i][j] = 0;
				}
				// altitude entre 0 et 3 : terre et herbe
				else if (h.get(i,j)/7-1 >=0 && h.get(i,j)/7-1 <4){
					if (Math.random() < pGrass) {
						world[i][j] = 1;
					}
					else {
						world[i][j] = 2;
					}
				}
				// altitude >= 8 : lave de volcan
				else if (h.get(i,j)/7-1 >=7){
					world[i][j] = 4;
				}

				// altitude > 4 : montagnes
				else {
					world[i][j] = 3;
				}
				
				worldbuff[i][j] = world[i][j];
			}
		}
	}

	
	public void step() {
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				
				// si c'est de la terre, l'herbe peut repousser
				if (world[i][j] == 2) {
					if (Math.random() < pRepopGrass) {
						worldbuff[i][j] = 1;
					}
				}
				
			}
		}
		
		// mise a jour de l'automate cellulaire (tabCourant = nouvTableau)
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				world[i][j] = worldbuff[i][j];
			}
		}
		
	}
	
	public int getHeight(int i, int j) {
		return h.get(i, j);
	}

	public int getHeight2(int i, int j) {
		return h.get2(i, j);
	}
	
}
