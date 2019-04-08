import java.util.ArrayList;

public class Forest extends CellularAutomata {
	
	private static double pTree = Constantes.pTree; 			// probabilité d'arbre au départ
	private static double pBurn = Constantes.pBurn; 			// probabilité de combustion spontanee d'un arbre
	private static double pRepopTree = Constantes.pRepopTree; // probabilité de repousse d'un arbre
	
	private Ground g;
	private double[][] modifierX;  // pour decaler les abscisses des arbres sur leur case en les affichant
	private double[][] modifierY;	// pour decaler les ordonnees des arbres sur leur case en les affichant
	private double[][] modifierSize; // pour modifier la taille des arbres
	private ArrayList<Agent> tabAgents;
	
	public Forest(int x, int y, int nbS, int nbF, int mode, Ground g) {
		super(x, y, nbS, nbF,"tree");
		modifierX = new double[x][y];
		modifierY = new double[x][y];
		modifierSize = new double [x][y];
		this.g = g;
		init(mode);
		
	}
	public void init(int mode) {
		switch (mode) {
			case 0 : 
				init0();
				pRepopTree = 0;
				break;
			case 1 : 
				init1();
				pRepopTree = 1;
				break;
			case 2 : 
				init2(10);
				break;
			case 3 : 
				init3();
				break;
			case 4 : 
				/*   sequences de halton
				init4();
				*/
				break;
			case 5 : 
				init5();
				break;

				
				
		}
		
	}
	
	public void init3() {
		int feu = 1; // pour placer un départ de feu aleatoire
		
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				// si terrain = ocean ou montagne : pas d'arbre				
				if ((g.get(i, j) == 0) || (g.get(i,j) >= 3)) {
					world[i][j] = 0;
				}

				// si terre ou herbe : on peut planter un arbre
				else {
					if (Math.random() < pTree) {
						world[i][j] = 1;
						modifierX[i][j] = (Math.random()-0.5)/3;
						modifierY[i][j] = (Math.random()-0.5)/3;//(int)(Math.random()*SIZE_SPRITE/4);
						modifierSize[i][j] = (0.5);

						if ((Math.random() < 0.5) && (feu == 1)) {
							world[i][j] = 2;
							feu = 0;
						}
					}
					else {
						world[i][j] = 0;
					}
				}
				
				worldbuff[i][j] = world[i][j];
			}
		}
	}
	/*
	public void init4() {
		// sequence de halton 2,3
		// sequence de halton 5,7
		
		int nbArbres = (int)(sizeX * sizeY / pTree);  // nombre d'arbres = nombre de cases total / densite d'arbres
		
		tab2 = new ArrayList<Double>
		
		
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				// si terrain = ocean ou montagne : pas d'arbre				
				if ((g.get(i, j) == 0) || (g.get(i,j) == 3)) {
					world[i][j] = 0;
				}

				// si terre ou herbe : on peut planter un arbre
				else {
					if (Math.random() < pTree) {
						world[i][j] = 1;
						modifierX[i][j] = (int)(Math.random()*SIZE_SPRITE/2-SIZE_SPRITE/4);
						modifierY[i][j] = (int)(Math.random()*SIZE_SPRITE/2-SIZE_SPRITE/4);
						modifierSize[i][j] = (int)(Math.random()*SIZE_SPRITE/2-SIZE_SPRITE/4);

						if ((Math.random() < 0.5) && (feu == 1)) {
							world[i][j] = 2;
							feu = 0;
						}
					}
					else {
						world[i][j] = 0;
					}
				}
				
				worldbuff[i][j] = world[i][j];
			}
		}
	}
	*/
	

	public void init5() {
		for (int i = 0; i < sizeX; i+=4) {
			for (int j = 0; j < sizeY; j+=4) {

				if (i>2 && j>2){
					if (Math.random() > 0.6){
						world[i-2][j-2] = 1;
						world[i-2][j-1] = 1;
						world[i-2][j] = 1;
						world[i-1][j-2] = 0;
						world[i-1][j-1] = 0;
						world[i-1][j] = 1;
						world[i][j-2] = 1;
						world[i][j-1] = 1;
						world[i][j] = 1;

						worldbuff[i-2][j-2] = 1;
						worldbuff[i-2][j-1] = 1;
						worldbuff[i-2][j] = 1;
						worldbuff[i-1][j-2] = 0;
						worldbuff[i-1][j-1] = 0;
						worldbuff[i-1][j] = 1;
						worldbuff[i][j-2] = 1;
						worldbuff[i][j-1] = 1;
						worldbuff[i][j] = 1;
						
					}
					else {
						world[i-2][j-2] = 0;
						world[i-2][j-1] = 0;
						world[i-2][j] = 0;
						world[i-1][j-2] = 0;
						world[i-1][j-1] = 0;
						world[i-1][j] = 0;
						world[i][j-2] = 0;
						world[i][j-1] = 0;
						world[i][j] = 0;

						worldbuff[i-2][j-2] = 0;
						worldbuff[i-2][j-1] = 0;
						worldbuff[i-2][j] = 0;
						worldbuff[i-1][j-2] = 0;
						worldbuff[i-1][j-1] = 0;
						worldbuff[i-1][j] = 0;
						worldbuff[i][j-2] = 0;
						worldbuff[i][j-1] = 0;
						worldbuff[i][j] = 0;



					}

					modifierX[i-2][j-2] = (Math.random()-0.5)/3;
					modifierX[i-2][j-1] = (Math.random()-0.5)/3;
					modifierX[i-2][j] = (Math.random()-0.5)/3;
					modifierX[i-1][j-2] = (Math.random()-0.5)/3;
					modifierX[i-1][j-1] = (Math.random()-0.5)/3;
					modifierX[i-1][j] = (Math.random()-0.5)/3;
					modifierX[i][j-2] = (Math.random()-0.5)/3;
					modifierX[i][j-1] = (Math.random()-0.5)/3;
					modifierX[i][j] = (Math.random()-0.5)/3;

					modifierY[i-2][j-2] = (Math.random()-0.5)/3;
					modifierY[i-2][j-1] = (Math.random()-0.5)/3;
					modifierY[i-2][j] = (Math.random()-0.5)/3;
					modifierY[i-1][j-2] = (Math.random()-0.5)/3;
					modifierY[i-1][j-1] = (Math.random()-0.5)/3;
					modifierY[i-1][j] = (Math.random()-0.5)/3;
					modifierY[i][j-2] = (Math.random()-0.5)/3;
					modifierY[i][j-1] = (Math.random()-0.5)/3;
					modifierY[i][j] = (Math.random()-0.5)/3;

					modifierSize[i-2][j-2] = (0.5);
					modifierSize[i-2][j-1] = (0.5);
					modifierSize[i-2][j] = (0.5);
					modifierSize[i-1][j-2] = (0.5);
					modifierSize[i-1][j-1] = (0.5);
					modifierSize[i-1][j] = (0.5);
					modifierSize[i][j-2] = (0.5);
					modifierSize[i][j-1] = (0.5);
					modifierSize[i][j] = (0.5);
				}
				else {
					world[i][j] = 0;
					worldbuff[i][j] = 0;
					modifierX[i][j] = (Math.random()-0.5)/3;
					modifierY[i][j] = (Math.random()-0.5)/3;
					modifierSize[i][j] = (0.5);
				}

			}
				
		}

	}
		
	public void step() {
		int nbFire = 0;
		for ( int i = 0 ; i < sizeX ; i++ ){
			for ( int j = 0 ; j < sizeY ; j++ ){
				if (world[i][j] == 3){
					worldbuff[i][j] = 0;
				}

				// des etats intermediaires por que les arbres soient en feu plus longtemps
				else if (world[i][j] == 20){
					worldbuff[i][j] = 21;
				}
				else if (world[i][j] == 21){
					worldbuff[i][j] = 22;
				}
				else if (world[i][j] == 22){
					worldbuff[i][j] = 23;
				}				
				else if (world[i][j] == 23){
					worldbuff[i][j] = 24;
				}				
				else if (world[i][j] == 24){
					worldbuff[i][j] = 25;
				}
				else if (world[i][j] == 25){
					worldbuff[i][j] = 2;
				}



				else if (world[i][j] == 2){
					worldbuff[i][j] = 3;
					if (g.get(i,j) == 1) { 
						g.setb(i,j,2);  //on brule l'herbe pour la remplacer par de la terre
					}
				}
				else if (world[i][j] == 1){ 
					nbFire = 0;
					if (world[(i-1+sizeX)%sizeX][j]>=20) {nbFire++;}
					if (world[(i+1+sizeX)%sizeX][j]>=20) {nbFire++;}
					if (world[i][(j-1+sizeY)%sizeY]>=20) {nbFire++;}					
					if (world[i][(j+1+sizeY)%sizeY]>=20) {nbFire++;}

					if (nbFire > 0){
						worldbuff[i][j] = 20;
					}
					else{
						worldbuff[i][j] = 1;
						if (modifierSize[i][j] < 1.25) {
							modifierSize[i][j] += (0.03);
						}
						if (Math.random() < pBurn){ // combustion spontanée 
							worldbuff[i][j] = 20;
						}
					}
					if (g.get(i,j) == 4){     // l'arbre brule s'il se trouve sur un terrain de lave
						worldbuff[i][j] = 20;
					}
					if (g.get(i,j) == 0){     // l'arbre est noyé et disparait s'il se trouve dans l'eau
						worldbuff[i][j] = 0;
					}
				}
				else if (world[i][j] == 0){ 
					if (Math.random() < pRepopTree){ 	    // repousse d'un arbre 
						if ((g.get(i,j) != 0) && (g.get(i,j) < 3)){       // si ce n'est pas une case ocean ou montagne
							if (Agent.searchAgentInXY(tabAgents,i,j).size() == 0) {   //s'il n'y a pas déja un agent sur la case
								worldbuff[i][j] = 4;
								modifierX[i][j] = (Math.random()-0.5)/3;
								modifierY[i][j] = (Math.random()-0.5)/3;//(int)(Math.random()*SIZE_SPRITE/2-SIZE_SPRITE/2);	
								modifierSize[i][j] = (0.5);
}
						}
						else{
							worldbuff[i][j] = 0;
						}		
					}
					else {
						worldbuff[i][j] = 0;
					}
				}
				else if (world[i][j] == 4) {
					worldbuff[i][j] = 1;

				}
				else{
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
	
	public void setTabAgent(ArrayList<Agent> tab) {
		tabAgents = tab;
	}
	
	public ArrayList<Agent> getTabAgent(){
		return tabAgents;
	}
	
	public double getModX(int i, int j) {
		return modifierX[i][j];
	}
	
	public double getModY(int i, int j) {
		return modifierY[i][j];
	}
	
	public double getModS(int i, int j) {
		return modifierSize[i][j];
	}

	public int get(int i, int j){
		if (world[i][j] >= 20)
			return 2;
		
		else {
			return world[i][j];
		}
	}
}
