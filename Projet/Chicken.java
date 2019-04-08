import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import java.util.ArrayList;

public class Chicken extends Agent {
	private static int AGE_CONSENT = 10;  			//   age minimum pour la reproduction (en nombre d'iterations)
	protected static int WAIT_BEFORE_REPRODUCE = 5;   	// le temps d'attente entre 2 tentatives successives de reproduction
	protected static int AGE_MAX = 200;
	protected static int EATING_RECOVERY = 50;
	private static double P_REPRODUCE = 0.7;
	private static int counterChicken = 0;    		// pour compter le nombre de Chicken cree
	private static int deadChicken = 0;			// pour compter le nombre de Chicken morts
  	
	public Chicken (int X, int Y, int maxH, int h, int nbS, int nbF, Ground ground, Forest forest){
		super("chicken","grass","fox",5,X,Y,maxH,h,nbS,nbF,ground,forest);
		speed = 3;
		travelTime = 24 / speed ;
		ageMax = (this).getStaticAgeMax();
		rangeColor = new Color(0,0,100,50);
		targetColor = new Color(0,0,255);
		counterChicken++;
	}
	
	public Chicken (Ground ground, Forest forest, int x, int y){
		this(x, y, 200, 200, 5, 3, ground, forest);
	} 
	
	public static Chicken createChicken(Ground ground, Forest forest){	
		
		int i,j;
		Chicken newChicken;
		do{
			i = (int)(Math.random()*(ground.getSizeX()));		
			j = (int)(Math.random()*(ground.getSizeY()));
		}while( (ground.get(i,j) == 0) || (forest.get(i,j) != 0) || (ground.get(i,j) == 4));
		// si on est dans l'eau, ou sur un arbre, ou dans la lave, il faut trouver d'autres coordonnées.
		newChicken = new Chicken(ground, forest, i, j);
		return newChicken;
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
			Agent a = new Chicken(x, y,  maxHealth, health, 5, 3, g, f);
			tab.add(a);
		}
	}
	
	public void fleeingStep(int x2, int y2) {
		orientation = setDirectionAStar(x2,y2);
		speed = 12;
		travelTime = 24 / speed ;
		
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
		speed = 3;
		travelTime = 24 / speed ;
		move();
	}
	
	public void randomStep() {
		orientation = (int)(Math.random() *5);	
		speed = 3;
		travelTime = 24 / speed ;
		move();
	}

	public void death() {
		if (this.isdead == false){
			this.isdead = true;
			deadChicken++;
		}
	}
	
	public static int chickenAlive(){
		return counterChicken-deadChicken;
	}

	public static int chickenBorn(){
		return counterChicken;
	}

	public static int getStaticAgeMax(){
		return AGE_MAX;
	}
}
