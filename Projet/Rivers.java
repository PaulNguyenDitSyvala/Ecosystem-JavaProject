
import java.awt.Image;

public class Rivers{
	private int x;
	private int y;

	private boolean endOfRiver;
	private ImageList images;

	private Rivers next;
	private Height h;
	private Ground g;

	public Rivers(int X, int Y, int orientIN, Ground G, Height H){
		g = G;
		h = H;
		x = (X+g.getSizeX())%g.getSizeX();
		y = (Y+g.getSizeY())%g.getSizeY();


		// tableau contenant les coordonnees des 4 cases adjacentes et leur hauteur
		// 0: gauche  1: haut   2: droite    3: bas 

		int [][] tab = new int[4][3];
		
		tab[0][0] = (x-1+g.getSizeX())%g.getSizeX();
		tab[0][1] = y;
		tab[0][2] = h.get(tab[0][0], tab[0][1]);

		tab[1][0] = x;
		tab[1][1] = (y-1+g.getSizeY())%g.getSizeY();
		tab[1][2] = h.get(tab[1][0], tab[1][1]);

		tab[2][0] = (x+1+g.getSizeX())%g.getSizeX();
		tab[2][1] = y;
		tab[2][2] = h.get(tab[2][0], tab[2][1]);

		tab[3][0] = x;
		tab[3][1] = (y+1+g.getSizeY())%g.getSizeY();
		tab[3][2] = h.get(tab[3][0], tab[3][1]);

		
		if (orientIN == 4){
			int out = 0;
			int i = 1;
			for (i=1; i< 4; i++){
				if (tab[i][2] < tab[out][2]){
					out = i;
				}
			}

			images = new ImageList(3,1,"river_4");
			g.setb(x,y,0); 
			g.set(x,y,0);

			if (g.get(tab[out][0],tab[out][1]) != 0){
				next = new Rivers(tab[out][0], tab[out][1], out, g, h);
			}
			else {
				next = null;
			}
		}
		else {
			int out = orientIN;
			int i = 0;
			for (i=0; i< 4; i++){
				if (i != (orientIN+2)%4){
					if (tab[i][2] < tab[out][2]){
						out = i;
					}
					else if (tab[i][2] == tab[out][2] && h.get(x,y) < tab[(orientIN+2)%4][2]){
						out = i;					
					}
					else if (tab[i][2] == tab[out][2] && h.get(x,y) == tab[(orientIN+2)%4][2] && g.get(tab[(orientIN+2)%4][0],tab[(orientIN+2)%4][1]) != 0){
						out = i;					
					}
				}
			}
			/*
			int out2 = 0;
			int out3 = 0;
			int first = 0;
			for (i=0; i< 4; i++){
				if (i != (orientIN+2)%4 && i != out){
					if (first == 0){
						out2 = i;
						out3 = i;
						first = 1;
					}
					else{
						if (tab[i][2] < tab[out2][2]){
							out2 = i;
						}
						else if (tab[i][2] == tab[out2][2] && h.get(x,y) < tab[(orientIN+2)%4][2]){
							out2 = i;					
						}
						else if (tab[i][2] == tab[out2][2] && h.get(x,y) == tab[(orientIN+2)%4][2] && g.get(tab[(orientIN+2)%4][0],tab[(orientIN+2)%4][1]) != 0){
							out2 = i;					
						}
						else{
							out3 = i;
						}
					}
				}
			}

			*/
			images = new ImageList(3,1,"river_"+(orientIN+2)%4+"_"+out);
			g.setb(x,y,0); 
			g.set(x,y,0);

			if (g.get(tab[out][0],tab[out][1]) != 0){
				next = new Rivers(tab[out][0], tab[out][1], out, g, h);
			}/*
			else if (g.get(tab[out2][0],tab[out2][1]) != 0){
				next = new Rivers(tab[out2][0], tab[out2][1], out2, g, h);
			}
			else if (g.get(tab[out3][0],tab[out3][1]) != 0){
				next = new Rivers(tab[out3][0], tab[out3][1], out3, g, h);
			}*/
			else{
				next = null;
			}
		}
	}


	
	public Rivers(int Xmax, int Ymax, Ground G, Height H){
		this((int)(Math.random()*Xmax), (int)(Math.random()*Ymax), 4, G, H);
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public Image image(int i, int j) {
		return images.image(i, j);
	}
	
	public Rivers getNext(){
		return next;
	}
}
