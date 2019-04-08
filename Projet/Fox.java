import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import java.awt.Color;

public class Fox extends Agent {
	private static int AGE_CONSENT = 300;   //   age minimum pour la reproduction (en nombre d'iterations)
	protected static int WAIT_BEFORE_REPRODUCE = 200;     // le temps d'attente entre 2 tentatives successives de reproduction
	protected static int AGE_MAX = 1000;
	protected static int EATING_RECOVERY = 50;
	private static double P_REPRODUCE = 0.7;
	private static int counterFox = 0;    // pour compter le nombre de Fox cree
	private static int deadFox = 0;		// pour compter le nombre de Fox morts

	private String preyType2;		//ce que l'agent mange aussi...
	
	public Fox (int X, int Y, int maxH, int h, int nbS, int nbF, Ground ground, Forest forest, int s){
		super("fox","chicken","none",20,X,Y,maxH,h,nbS,nbF,ground,forest);
		speed = 8;
		travelTime = 24 / speed ;
		rangeColor = new Color(100,0,0,50);
		targetColor = new Color(255,0,0);
		counterFox++;
		ageMax = (this).getStaticAgeMax();
		preyType2 = "sheep";
		season = s;
		changeSeason(s);
		
	}

	
	public Fox (Ground ground, Forest forest, int x, int y, int s){
		this(x, y, 500, 500, 5, 3, ground, forest, s);
	} 

	public Fox (Ground ground, Forest forest, int x, int y){
		this(x, y, 500, 500, 5, 3, ground, forest, 1);
	} 

	public static Fox createFox(Ground ground, Forest forest){	
		
		int i,j;
		Fox newFox;
		do{
			i = (int)(Math.random()*(ground.getSizeX()));		
			j = (int)(Math.random()*(ground.getSizeY()));
		}while( (ground.get(i,j) == 0) || (forest.get(i,j) != 0) || (ground.get(i,j) == 4));
		// si on est dans l'eau, ou sur un arbre, ou dans la lave, il faut trouver d'autres coordonnées.
		newFox = new Fox(ground, forest, i, j);
		return newFox;
	}
	
	public void step(Night n, ArrayList<Agent> tabAgents){
		health-=2;
		age++;
		timer++;

		if (g.get(x,y) == 4){  // si l'agent est dans la lave : il meurt
			health = 0;
		}

		if (season != 3 && g.get(x,y) == 0){
			health = 0;
		}	

		if (health == 0) {  	// meurt car plus d'energie
			death();
		}
		if (age > AGE_MAX) {		// meurt de vieillesse
			death();
		}


		if (moving == false) {
			if (n.isNight() == true && (double)health/maxHealth > 0.2 ){
				orientation = 5;
				health+=2;
				//move();
				
			}
			else {
				pathToPrey = new LinkedList<NodeAStar>();
				visibleCells = new LinkedList<NodeAStar>();		
				numIte = travelTime-1;

				int [] tab2 = searchPredator();
				if (tab2[0] != 99999) {
					fleeingStep(tab2[0],tab2[1]);
					pathToPrey = new LinkedList<NodeAStar>();
					mode = 2;
				}

				else {

					int [] tab3 = searchPartner();
					if (tab3[0] != 99999 && (double)health/maxHealth > 0.4 && timer > WAIT_BEFORE_REPRODUCE && age > AGE_CONSENT ) {
						huntingStep(tab3[0], tab3[1]);
						mode = 3;
						if (tab3[0] == x && tab3[1] == y ){
							tryToReproduce(tabAgents);
							timer =0;
						}
					}

					else {
						int [] tab = searchPrey();
						if (tab[0] != 99999 && (double)health/maxHealth < 0.9) {
							huntingStep(tab[0], tab[1]);
							tryToEat();
							mode = 1;
						}
						else{
							randomStep();
							mode = 0;
						}
					}
				}
				moving = true;
			}	
		}


		numIte = (numIte +1 )% (travelTime);

		if (numIte == travelTime-1) {
			moving = false;
		}

		if (health > maxHealth) {
			health = maxHealth;
		}
		
	}

	public void tryToEat() {
		ArrayList<Agent>tab2 = Agent.searchAgentInXY(tabAgents, x, y);
		for (Agent a : tab2) {
			if (a.getType().equals(this.preyType) || a.type.equals(this.preyType2)) {
				eatPrey(a);
			}
		}
	}

	public void eatPrey(Agent a) {
		a.death();

		health += EATING_RECOVERY;
		if (health > maxHealth) {
			health = maxHealth;
		}
	}

	public void tryToReproduce(ArrayList<Agent> tab){
		if (Math.random() < P_REPRODUCE){
			Agent a = new Fox(x, y, maxHealth, health, 5, 3, g, f, this.season);
			tab.add(a);
		}
	}
	
	public boolean canEnterCell(int i, int j){
		if (g.get(i,j) == 0 && season != 3) return false; // si la case (i,j) contient de l'eau
		if (f.get(i,j) != 0) return false; // si la case (i,j) contient un arbre (donc Forest != 0)
		if (g.get(i,j) >= 4) return false; // si la case (i,j) est une case lave
		//if (g.getHeight(i,j) != g.getHeight(x, y)) return false; // si la case (i,j) est a une altitude differente de la case ou se trouve l'agent
		//if (g.getHeight2(i,j) > g.getHeight2(x, y)) return false; // si la case (i,j) est a une altitude plus elevee de la case ou se trouve l'agent

		return true; // apres avoir vérifié toutes les autres conditions, on peut entrer dans la case.                
	}

	public void death() {
		this.isdead = true;
		deadFox++;
	}
	
	public static int foxAlive(){
		return counterFox-deadFox;
	}

	public static int foxBorn(){
		return counterFox;
	}

	public static int getStaticAgeMax(){
		return AGE_MAX;
	}
	
public int[] searchPreyAgent() {
		int i = 0;
		int j = 0;
		ArrayList<Agent> tabA = null;
		
		for (i = 1; i < range; i++) {  
			for (j = 0; j< i; j++) {
				if (canEnterCell((x-i+j+1+g.getSizeX())%g.getSizeX(),(y+j+1+g.getSizeY())%g.getSizeY()) == true) {
					visibleCells.addFirst(new NodeAStar((x-i+j+1+g.getSizeX())%g.getSizeX(),(y+j+1+g.getSizeY())%g.getSizeY(),true));
					tabA = Agent.searchAgentInXY(tabAgents, (x-i+j+1+g.getSizeX())%g.getSizeX(),(y+j+1+g.getSizeY())%g.getSizeY());     // on cherche tous les agents sur cette case
					if (tabA.size() != 0) {    												 // s'il y a un agent sur cette case ... 
						for (Agent a : tabA) {  											 // pour chaque agent sur cette case, on verifie si c'est une proie
							if (a.type.equals(this.preyType) || a.type.equals(this.preyType2) ) {
								int[] tab = new int[2];
								tab[0]= x-i+j+1;
								tab[1]= y+j+1;
								return tab;
							}
						}
					}

				}
			}
			
			for (j = 0; j< i; j++) {
				if (canEnterCell((x+i-j+g.getSizeX())%g.getSizeX(),(y+j+g.getSizeY())%g.getSizeY()) == true) {
					visibleCells.addFirst(new NodeAStar((x+i-j+g.getSizeX())%g.getSizeX(),(y+j+g.getSizeY())%g.getSizeY(),true));
					tabA = Agent.searchAgentInXY(tabAgents,(x+i-j+g.getSizeX())%g.getSizeX(),(y+j+g.getSizeY())%g.getSizeY());    	 // on cherche tous les agents sur cette case
					if (tabA.size() != 0) {    												 // s'il y a un agent sur cette case ... 
						for (Agent a : tabA) {  											 // pour chaque agent sur cette case, on verifie si c'est une proie
							if (a.type.equals(this.preyType) || a.type.equals(this.preyType2) ) {
								int[] tab = new int[2];
								tab[0]= x+i-j;
								tab[1]= y+j;
								return tab;
							}
						}
					}
				}
			}
			
			for (j = 0; j< i; j++) {
				if (canEnterCell((x+i-j-1+g.getSizeX())%g.getSizeX(),(y-j-1+g.getSizeY())%g.getSizeY()) == true) {
					visibleCells.addFirst(new NodeAStar((x+i-j-1+g.getSizeX())%g.getSizeX(),(y-j-1+g.getSizeY())%g.getSizeY(),true));
					tabA = Agent.searchAgentInXY(tabAgents,(x+i-j-1+g.getSizeX())%g.getSizeX(),(y-j-1+g.getSizeY())%g.getSizeY());    	 // on cherche tous les agents sur cette case
					if (tabA.size() != 0) {    												 // s'il y a un agent sur cette case ... 
						for (Agent a : tabA) {  											 // pour chaque agent sur cette case, on verifie si c'est une proie
							if (a.type.equals(this.preyType) || a.type.equals(this.preyType2) ) {
								int[] tab = new int[2];
								tab[0]= x+i-j-1;
								tab[1]= y-j-1;
								return tab;
							}
						}
					}
				}
			}
			
			for (j = 0; j< i; j++) {
				if (canEnterCell((x-i+j+g.getSizeX())%g.getSizeX(),(y-j+g.getSizeY())%g.getSizeY()) == true) {
					visibleCells.addFirst(new NodeAStar((x-i+j+g.getSizeX())%g.getSizeX(),(y-j+g.getSizeY())%g.getSizeY(),true));
					tabA = Agent.searchAgentInXY(tabAgents,(x-i+j+g.getSizeX())%g.getSizeX(),(y-j+g.getSizeY())%g.getSizeY());    	 // on cherche tous les agents sur cette case
					if (tabA.size() != 0) {    												 // s'il y a un agent sur cette case ... 
						for (Agent a : tabA) {  											 // pour chaque agent sur cette case, on verifie si c'est une proie
							if (a.type.equals(this.preyType) || a.type.equals(this.preyType2) ) {
								int[] tab = new int[2];
								tab[0]= x-i+j;
								tab[1]= y-j;
								return tab;
							}
						}
					}
				}
			}
		}
		// si on ne trouve pas de proie... 
		int[] tab = new int[2];
		tab[0]= 99999;
		tab[1]= 99999;
		return tab;		
	}

	public void summerMode(){
		changeImageList("fox");
	}

	public void springMode(){
		changeImageList("fox");
	}

	public void winterMode(){
		changeImageList("winterfox");
	}

	public void fallMode(){
		changeImageList("fox");
	}

	public void changeSeason( int season){
		switch (season) {
			case 1 :
				summerMode();
				break;
			case 2 :
				fallMode();
				break;
			case 3 :
				winterMode();
				break;
			case 4 :
				springMode();
				break;
		}
	}
			



}
