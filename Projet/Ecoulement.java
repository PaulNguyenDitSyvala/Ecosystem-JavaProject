
// automate cellulaire representant la quantité de lave dans chaque case

public class Ecoulement extends CellularAutomata {

	private Ground g;
	private Height h;
	private int typeEcoulement;
	private int vitesseEcoulement;

	public Ecoulement(int x, int y, int nbS, int nbF, Ground g, Height h, int type, int vitesse, int mode){
		super(x, y, nbS, nbF,"NoImages");
		this.g = g;
		this.h = h;
		typeEcoulement = type;
		vitesseEcoulement = vitesse;
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
				init2(10000);
				break;
			case 3 : 
				init3();
				break;
					
		}
	}

	public void init3() {
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				if (g.get(i,j) == typeEcoulement){
					world[i][j] = 9000;
				}
				else {
					world[i][j] = 0;
				}
				worldbuff[i][j] = world[i][j];

			}

		}
	}
	
		// melange de tableau de coordonnees pour la mise a jour asynchrone
	public int[][][] coordonneesAleatoires(int tailleX, int tailleY){
		int[][][] tab = new int[tailleX][tailleY][2];
		for (int i = 0; i < tailleX; i++){
			for (int j = 0; j < tailleY; j++){
				tab[i][j][0] = i;
				tab[i][j][1] = j;
				//System.out.print("("+String.format("%2d",tab[i][j][0])+","+String.format("%2d",tab[i][j][1])+")");
			}
			//System.out.println("");
		}
		int x = 0;
		int y = 0;
		int x2 = 0;
		int y2 = 0;
		for (int i = 0; i < tailleX; i++){
			for (int j = 0; j < tailleY; j++){
				x = (int)( Math.random()*(tailleX - i)+i);
				if (x == i)
					y = (int)( Math.random()*(tailleY - j)+j);
				else 
					y = (int)( Math.random()*(tailleY));

				x2 = tab[i][j][0];
				y2 = tab[i][j][1];
				tab[i][j][0] = x;
				tab[i][j][1] = y;
				tab[x][y][0] = x2;
				tab[x][y][1] = y2;
			}
		}
		return tab;
	}
				




	public void step() {
		
		int[][][]tab = coordonneesAleatoires(sizeX, sizeY);
		int x = 0;
		int y = 0;
		int quantite = 0;
		for ( int i = 0 ; i < sizeX ; i++ ){
			for ( int j = 0 ; j < sizeY ; j++ ){
				x = tab[i][j][0];
				y = tab[i][j][1];

				quantite = vitesseEcoulement*(1+h.get((x-1+sizeX)%sizeX,y)- h.get(x,y));
				if (h.get((x-1+sizeX)%sizeX,y)>= h.get(x,y) && world[(x-1+sizeX)%sizeX][y] > quantite && g.get(x,y) != 0){
					world[x][y] += quantite;
					world[(x-1+sizeX)%sizeX][y] -= quantite;
				}

				quantite = vitesseEcoulement*(1+h.get((x+1+sizeX)%sizeX,y)-h.get(x,y));
				if (h.get((x+1+sizeX)%sizeX,y)>= h.get(x,y) && world[(x+1+sizeX)%sizeX][y] > quantite && g.get(x,y) != 0 ){
					world[x][y] += quantite;
					world[(x+1+sizeX)%sizeX][y] -= quantite;
				}
				
				quantite = vitesseEcoulement*(1+h.get(x,(y-1+sizeY)%sizeY)-h.get(x,y));
				if (h.get(x,(y-1+sizeY)%sizeY)>= h.get(x,y) && world[x][(y-1+sizeY)%sizeY] > quantite && g.get(x,y) != 0 ){
					world[x][y] += quantite;
					world[x][(y-1+sizeY)%sizeY] -= quantite;
				}

				quantite = vitesseEcoulement*(1+h.get(x,(y+1+sizeY)%sizeY)-h.get(x,y));
				if (h.get(x,(y+1+sizeY)%sizeY)>= h.get(x,y) && world[x][(y+1+sizeY)%sizeY] > quantite && g.get(x,y) != 0 ){
					world[x][y] += quantite;
					world[x][(y+1+sizeY)%sizeY] -= quantite;
				}




				if (world[x][y] > 0 && g.get(x,y) != 0)
					g.setb(x,y,4);

			}
		}/*
		for (int i = 0; i < sizeY; i++){
			for (int j = 0; j < sizeY; j++){
				System.out.print(String.format("%3d",world[i][j])+" ");
			}
			System.out.println("");
		}*/
	}










}
