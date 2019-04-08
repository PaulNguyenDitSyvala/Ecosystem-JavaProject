import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import java.util.ArrayList;

public class Sheep extends Agent {
	private static int AGE_CONSENT = 20;   //   age minimum pour la reproduction (en nombre d'iterations)
	protected static int WAIT_BEFORE_REPRODUCE = 15;     // le temps d'attente entre 2 tentatives successives de reproduction
	protected static int AGE_MAX = 500;
	protected static int EATING_RECOVERY = 100;
	private static double P_REPRODUCE = 0.9;
	private static int counterSheep = 0;    // pour compter le nombre de Sheep cree
	private static int deadSheep = 0;		// pour compter le nombre de Sheep morts
  	
	public Sheep (int X, int Y, int maxH, int h, int nbS, int nbF, Ground ground, Forest forest){
		super("sheep","grass","fox",15,X,Y,maxH,h,nbS,nbF,ground,forest);
		speed = 6;
		travelTime = 24 / speed ;
		rangeColor = new Color(0,100,0,50);
		targetColor = new Color(0,255,0);
		counterSheep++;
		ageMax = (this).getStaticAgeMax();
	}
	
	public Sheep (Ground ground, Forest forest, int x, int y){
		this(x, y, 500, 500, 5, 3, ground, forest);
	} 
	
	public static Sheep createSheep(Ground ground, Forest forest){	
		
		int i,j;
		Sheep newSheep;
		do{
			i = (int)(Math.random()*(ground.getSizeX()));		
			j = (int)(Math.random()*(ground.getSizeY()));
		}while( (ground.get(i,j) == 0) || (forest.get(i,j) != 0) || (ground.get(i,j) == 4));
		// si on est dans l'eau, ou sur un arbre, ou dans la lave, il faut trouver d'autres coordonnées.
		newSheep = new Sheep(ground, forest, i, j);
		return newSheep;
	}


	public boolean canEnterCell(int i, int j){
		if (g.get(i,j) == 0 && season != 3) return false; // si la case (i,j) contient de l'eau
		if (f.get(i,j) != 0) return false; // si la case (i,j) contient un arbre (donc Forest != 0)
		if (g.get(i,j) >= 4) return false; // si la case (i,j) est une case lave
		if (g.getHeight(i,j) != g.getHeight(x, y)) return false; // si la case (i,j) est a une altitude differente de la case ou se trouve l'agent

		//if (g.getHeight2(i,j) > g.getHeight2(x, y)) return false; // si la case (i,j) est a une altitude plus elevee que la case ou se trouve l'agent
		//if (g.getHeight2(i,j) < g.getHeight2(x, y)) return false; // si la case (i,j) est a une altitude plus basse que la case ou se trouve l'agent

		return true; // apres avoir vérifié toutes les autres conditions, on peut entrer dans la case.                
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
		if (age > AGE_MAX) {	// meurt de vieillesse
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
					int [] tab2 = searchPredator();
					if (tab2[0] != 99999 && (double)health/maxHealth > 0.8) {
						fleeingStep(tab2[0],tab2[1]);
						tryToEat();
						mode = 2;
					}
					else {
						int [] tab = searchPrey();
						if (tab[0] != 99999 && (double)health/maxHealth < 0.95) {
							huntingStep(tab[0], tab[1]);
							tryToEat();
							mode = 1;
						}
						else {
							randomStep();
							tryToEat();      // les moutons essayent de manger meme quand ils n'ont pas faim.... contrairement aux poulets
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
		if (g.get(x, y) == 1) {
			eatGrass(x, y);	
		}
	}

	public void eatGrass(int x2, int y2) {
		g.setb(x2, y2, 2);  // On met de la terre au lieu d'herbe a la case (x2,y2)

		health += EATING_RECOVERY;
		if (health > maxHealth) {
			health = maxHealth;
		}
	}

	public void tryToReproduce(ArrayList<Agent> tab){
		if (Math.random() < P_REPRODUCE){
			Agent a = new Sheep(x, y,  maxHealth, health, 5, 3, g, f);
			tab.add(a);
		}
	}
	
	public void fleeingStep(int x2, int y2) {
		orientation = setDirectionAStar(x2,y2);

		if (orientation != 4) {
			int i =(int)(Math.random()*3)+1;
			if (Math.random() < 1)
				orientation = (orientation + 2) %4;
			else
				orientation = (orientation + i) %4;

		}
		move();
	}
	
	public void huntingStep(int x2, int y2) {
		orientation = setDirectionAStar(x2,y2);
		move();
	}
	
	public void randomStep() {
		orientation = (int)(Math.random() *5);
		move();
	}

	public void death() {
		if (this.isdead == false){
			this.isdead = true;
			deadSheep++;
		}
	}
	
	public static int sheepAlive(){
		return counterSheep-deadSheep;
	}

	public static int sheepBorn(){
		return counterSheep;
	}

	public static int getStaticAgeMax(){
		return AGE_MAX;
	}
}
