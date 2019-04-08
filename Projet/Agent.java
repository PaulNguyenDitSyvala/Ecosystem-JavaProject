import java.awt.Image;
import java.awt.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Agent{
	protected static int AGE_CONSENT = 50;   //   age minimum pour la reproduction (en nombre d'iterations)
	protected static int AGE_MAX = 1000;
	protected static int WAIT_BEFORE_REPRODUCE = 50;     // le temps d'attente entre 2 tentatives successives de reproduction
	protected static int counter = 0;      // on va l'incrementer pour chaque nouvel agent créé
	
	protected String name;
	protected String type;
	protected String preyType;		//ce que l'agent mange
	protected String predatorType;  // ce qui mange l'agent
	protected int age;
	protected int ageMax;
	protected int range;        // la portee de la vision de l'agent
	protected int x;
	protected int y;	
	protected int gender;	   //  0 = male    1 = femelle 
	protected int timer;		// pour compter le temps avant de tenter une nouvelle reproduction des agents.
	protected int orientation; // 0=gauche   1=haut   2=droite   3=bas   4= sur place
	protected int maxHealth;
	protected int health;
	protected int mode; 		// 0: random  1: hunting prey   2: fleeing predator   3: searching partner for reproduction
	protected int season;		// 1: summer  2: fall  3: winter  4: spring
	
	protected int speed = 6;  
	protected int travelTime = 24 / speed ;     // combien d'iteration l'agent met pour se deplacer d'une case
	protected static int[] tabAnim = new int [] {0,1,0,2,0,1,0,2,0,1,0,2};  
	
	protected boolean moving;
	protected boolean isdead;  
	protected int numIte;    // pour compter le nombre 'iteration avant de deplacer a nouveau l'agent

	protected ImageList images;    // une matrice d'images pour les sprites de l'agent
	
	protected Ground g;
	protected Forest f;
	protected ArrayList<Agent> tabAgents;
	
	protected LinkedList<NodeAStar> pathToPrey;
	protected LinkedList<NodeAStar> visibleCells;
	protected Color rangeColor;
	protected Color targetColor;
	
	public Agent(String type, String preyT, String predatorT, int range, int X, int Y, int maxH, int h, int nbS, int nbF, Ground ground, Forest forest){
		name = type +"_"+ counter;
		this.type = type;
		preyType = preyT;
		predatorType = predatorT;
		age = 0;
		mode = 0;
		season = 1;
		this.range = range;
		x = X;
		y = Y;
		ageMax = (this).getStaticAgeMax();
		gender = (int)(Math.random()*2);
		timer = (int)(Math.random() * 120);
		orientation = 0;
		maxHealth = maxH;
		health = h;
		g = ground;
		f = forest;
		tabAgents = null;
		
		numIte = 0;
		moving = false;
		isdead = false;
		images = new ImageList(nbS,nbF,type);
		pathToPrey = new LinkedList<NodeAStar>();
		visibleCells = new LinkedList<NodeAStar>();		
		rangeColor = new Color(100,100,100,100);
		targetColor = new Color(0,0,0,255);
		
		counter++;
	}
	
	public Agent(Ground ground, Forest forest, int x, int y){
		this("chicken","grass","fox", 3, x, y, 100, 100, 4, 3, ground, forest);
	} 

	/*
	public static Agent createRandomAgent(Ground ground, Forest forest){	
		
		int i,j;
		do{
			i = (int)(Math.random()*(ground.getSizeX()));		
			j = (int)(Math.random()*(ground.getSizeY()));
		}while( (ground.get(i,j) == 0) || (forest.get(i,j) != 0) );
		// si on est dans l'eau, ou sur un arbre, il faut trouver d'autres coordonnées.

		Agent newAgent = new Agent(ground, forest, i, j);
		return newAgent;
	} */
	
	public static Agent createRandomAgent2(Ground ground, Forest forest){	
		
		int i,j;
		Agent newAgent;
		do{
			i = (int)(Math.random()*(ground.getSizeX()));		
			j = (int)(Math.random()*(ground.getSizeY()));
		}while( (ground.get(i,j) == 0) || (forest.get(i,j) != 0) ||(ground.get(i,j) == 4)  );
		// si on est dans l'eau, ou sur un arbre, il faut trouver d'autres coordonnées.
		double a = Math.random();
		if (a < 0.34) {
			newAgent = new Fox(ground, forest, i, j);
		}
		else if (a < 0.67){
			newAgent = new Sheep(ground, forest, i, j);
		}
		else {
			newAgent = new Chicken(ground, forest, i, j);
		}
		return newAgent;
	}
	
	public void step(Night n, ArrayList<Agent> tabAgents){
		health-=2;
		age++;
		timer++;

		if (g.get(x,y) == 4){  // si l'agent est dans la lave : il meurt
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
				int [] tab = searchPrey();
				int [] tab2 = searchPredator();
				int [] tab3 = searchPartner();
				numIte = travelTime-1;
				if (tab2[0] != 99999) {
					fleeingStep(tab2[0],tab2[1]);
					pathToPrey = new LinkedList<NodeAStar>();
					mode = 2;
				}
				else if (tab3[0] != 99999 && (double)health/maxHealth > 0.4 && timer > WAIT_BEFORE_REPRODUCE && age > AGE_CONSENT ) {
					huntingStep(tab3[0], tab3[1]);
					mode = 3;
					if (tab3[0] == x && tab3[1] == y ){
						tryToReproduce(tabAgents);
						timer =0;
					}
				}
				else if (tab[0] != 99999 && (double)health/maxHealth < 0.9) {
					huntingStep(tab[0], tab[1]);
					tryToEat();
					mode = 1;
				}
				else {
					randomStep();
					mode = 0;
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
	
	public void randomStep() {
		pathToPrey = new LinkedList<NodeAStar>();
		orientation = (int)(Math.random() *5);
		move();
	}

	public void huntingStep(int x2, int y2) {
		orientation = setDirectionAStar(x2,y2);
		move();
	}
	
	public void fleeingStep(int x2, int y2) {
		orientation = setDirectionAStar(x2,y2);

		if (orientation != 4) {
			int i =(int)(Math.random()*3)+1;
			if (Math.random() < 0.6)
				orientation = (orientation + 2) %4;
			else
				orientation = (orientation + i) %4;

		}
		move();
	}


	int setDirectionAStar(int x3, int y3){


		int x2 = (x3+g.getSizeX())%g.getSizeX();
		int y2 = (y3+g.getSizeY())%g.getSizeY();

		pathToPrey = new LinkedList<NodeAStar>();
		if (x2 == x && y2 == y) {    // si on veut se deplacer vers l'endroit ou on est deja : rester ou on est.
			return 4;
		}

		NodeAStar[][] NodeMap = new NodeAStar[ g.getSizeX()][ g.getSizeY()];

		for (int i= 0 ; i <  g.getSizeX() ; i++){
			for (int j= 0 ; j < g.getSizeY() ; j++){

				if (canEnterCell(i,j) == false){
					NodeMap[i][j] = new NodeAStar(i,j,false); 
				}
				/*

				if (g.get(i,j) < 1) {
					NodeMap[i][j] = new NodeAStar(i,j,false);    // si dans l'eau
	 			}
				else if (g.get(i,j) > 3 ){
					NodeMap[i][j] = new NodeAStar(i,j,false);   // si montagne ou lave
				}
				else if (f.get(i,j) > 0 ){
					NodeMap[i][j] = new NodeAStar(i,j,false);    // si il y a un arbre
				}
				else if (g.getHeight2(i,j) > g.getHeight2(x,y)){
					NodeMap[i][j] = new NodeAStar(i,j,false);    // si la hauteur est plus haute
				}		
				*/

				// sinon, on peut aller sur cette case
				else{
					NodeMap[i][j] = new NodeAStar(i,j,true);
				}
			}
		}
		NodeAStar goal = NodeMap[(x2+ g.getSizeX())% g.getSizeX()][(y2 +  g.getSizeY()) %  g.getSizeY()];

		List<NodeAStar> openList = new LinkedList<NodeAStar>();
		List<NodeAStar> closedList = new LinkedList<NodeAStar>();

		NodeMap[x][y].setValToGoal(goal);
		openList.add(NodeMap[x][y]);

		boolean AStarFinished = false;

		while (AStarFinished == false){

			NodeAStar cheapest = openList.get(0);
			for (int i = 0; i < openList.size(); i++){

				if (openList.get(i).getTotalValue() < cheapest.getTotalValue()){
					cheapest = openList.get(i);
				}
			}
			NodeAStar current = cheapest;

			openList.remove(current);	
			closedList.add(current);

			if ((current.getX() == x2) && (current.getY() == y2)){
				// Retourne une LinkedList contenant tous les nodes visités
				while (current.getX() != x || current.getY() != y){
					pathToPrey.addFirst(current);
					current = current.getParent();
				}
				AStarFinished = true;
			}
			if (AStarFinished == false){

				List<NodeAStar> adjacentNodes = new LinkedList<NodeAStar>();

				NodeAStar adjacent;

				// On regarde si la case a gauche est accessible
				adjacent = NodeMap[(current.getX()-1+ g.getSizeX())% g.getSizeX()][current.getY()];
				if (adjacent != null && adjacent.isWalkable() == true && closedList.contains(adjacent) == false){
					adjacentNodes.add(adjacent);
				}
				
				// On regarde si la case a droite est accessible
				adjacent = NodeMap[(current.getX()+1+ g.getSizeX())% g.getSizeX()][current.getY()];
				if (adjacent != null && adjacent.isWalkable() == true && closedList.contains(adjacent) == false){
					adjacentNodes.add(adjacent);
				}

				// On regarde si la case en haut est accessible
				adjacent = NodeMap[current.getX()][(current.getY()-1 +  g.getSizeY())% g.getSizeY()];
				if (adjacent != null && adjacent.isWalkable() == true && closedList.contains(adjacent) == false){
					adjacentNodes.add(adjacent);
				}

				// On regarde si la case en bas est accessible
				adjacent = NodeMap[current.getX()][(current.getY()+1 +  g.getSizeY())% g.getSizeY()];
				if (adjacent != null && adjacent.isWalkable() == true && closedList.contains(adjacent) == false){
					adjacentNodes.add(adjacent);
				}

				for (NodeAStar a : adjacentNodes){

					if (openList.contains(a) == false){
						a.setParent(current);
						a.setValToGoal(goal);
						a.setValOrigin(current);
						openList.add(a);
					}
					else if (a.getValOrigin() > a.calculateValOrigin(current)){
						a.setParent(current);
						a.setValOrigin(current);
					}
				}
				
				if (openList.isEmpty() == true){
					AStarFinished = true;
				}
			}
		}

		if (pathToPrey.isEmpty() == true){
			return (int)(Math.random() *5);
		}
		
		if (pathToPrey.getFirst().getX() < x){
			return 0;
		}

		if (pathToPrey.getFirst().getY() < y){
			return 1;
		}

		if (pathToPrey.getFirst().getX() > x){
			return 2;
		}

		if (pathToPrey.getFirst().getY() > y){
			return 3;
		}

		return (int)(Math.random() *5);
	}



	public int setDirection(int x2, int y2) {
		int orient = 4;
		
		if (x2 == x && y2 == y) {    // si on veut se deplacer vers l'endroit ou on est deja : rester ou on est.
			return orient;
		}
		
		
		if ((x2-x)*(x2-x) == (y2-y)*(y2-y)) {
			if (Math.random() > 0.5) {
				if (x2 > x) {
					orient = 2;
					return orient;
				}
				else {
					orient = 0;
					return orient;
				}
			}
			else {
				if (y2 > y) {
					orient = 3;
					return orient;
				}
				else {
					orient = 1;
					return orient;
				}
			}
		}
		
		
		if ((x2-x)*(x2-x) > (y2-y)*(y2-y)) { 
			if (x2 > x) {
				orient = 2;
				return orient;
			}
			else {
				orient = 0;
				return orient;

			}
		}
		else {
			if (y2 > y) {
				orient = 3;
				return orient;
			}
			else {
				orient = 1;
				return orient;
			}
		}
	}
	
	public void move() {
		boolean accessibleCellFound = false;
		int counter = 0;
		
		while (accessibleCellFound == false) {
			switch (orientation){
				case 0: 	
					if (canEnterCell((x-1+g.getSizeX())%g.getSizeX(),y)){
						x = (x-1+g.getSizeX())%g.getSizeX(); 
						accessibleCellFound = true;
					}
					break;
				case 1: 
					if (canEnterCell(x,(y-1+g.getSizeY())%g.getSizeY())){
						y = (y-1+g.getSizeY())%g.getSizeY();
						accessibleCellFound = true;
					}
					break;
				case 2: 
					if (canEnterCell((x+1+g.getSizeX())%g.getSizeX(),y)){
						x = (x+1+g.getSizeX())%g.getSizeX(); 
						accessibleCellFound = true;
					}
					break;
				case 3: 
					if (canEnterCell(x,(y+1+g.getSizeY())%g.getSizeY())){
						y = (y+1+g.getSizeY())%g.getSizeY();
						accessibleCellFound = true;
					}
					break;
				case 4 : 
					accessibleCellFound = true;
					break;
				
				case 5 : 
					accessibleCellFound = true;
					break;	
			}


			if (accessibleCellFound == false) {
				counter++;
				orientation = (orientation+1) %4;
				if (counter > 3) {
					orientation = 4;
				}
			}
		}
		
	}
	
	public boolean canEnterCell(int i, int j){
		if (g.get(i,j) == 0 && season != 3) return false; // si la case (i,j) contient de l'eau
		if (g.get(i,j) == 4) return false; // si la case (i,j) contient de la lave
		if (f.get(i,j) != 0) return false; // si la case (i,j) contient un arbre (donc Forest != 0)

		return true; // apres avoir vérifié toutes les autres conditions, on peut entrer dans la case.                
	}

	public static ArrayList<Agent> searchAgentInXY(ArrayList<Agent> tab, int X, int Y){
		ArrayList<Agent> tabAgentXY = new ArrayList<Agent>();
		for (Agent a : tab){
			if ((a.x == X) && (a.y == Y)){
				tabAgentXY.add(a);
			}
		}
		return tabAgentXY;
	}
	
	public int[] searchPrey(){
		int[] tab = null;
		if (preyType.equals("grass")) {
			tab = searchPreyGrass();
		}
		else {
			tab = searchPreyAgent();
		}
		return tab;
	}
	
	public int[] searchPreyGrass(){     // on cherche une case avec de l'herbe ( pour les herbivores)
		int i = 0;
		int j = 0;
		
		for (i = 1; i < range; i++) {   //coin bas gauche
			for (j = 0; j< i; j++) {
				if (canEnterCell((x-i+j+1+g.getSizeX())%g.getSizeX(),(y+j+1+g.getSizeY())%g.getSizeY()) == true){
					visibleCells.addFirst(new NodeAStar((x-i+j+1+g.getSizeX())%g.getSizeX(),(y+j+1+g.getSizeY())%g.getSizeY(),true));
					if (g.get((x-i+j+1+g.getSizeX())%g.getSizeX(),(y+j+1+g.getSizeY())%g.getSizeY()) == 1) {     // s'il y a de l'herbe sur cette coordonnee, on retourne cette position comme resultat.
						int[] tab = new int[2];
						tab[0]= x-i+j+1;
						tab[1]= y+j+1;
						return tab;
					}
				}
			}
			
			for (j = 0; j< i; j++) {   ///coin bas droite
				if (canEnterCell((x+i-j+g.getSizeX())%g.getSizeX(),(y+j+g.getSizeY())%g.getSizeY()) == true){
					visibleCells.addFirst(new NodeAStar((x+i-j+g.getSizeX())%g.getSizeX(),(y+j+g.getSizeY())%g.getSizeY(),true));
					if (g.get((x+i-j+g.getSizeX())%g.getSizeX(),(y+j+g.getSizeY())%g.getSizeY()) == 1) {     // s'il y a de l'herbe sur cette coordonnee, on retourne cette position comme resultat.
						int[] tab = new int[2];
						tab[0]= x+i-j;
						tab[1]= y+j;
						return tab;
					}
				}
			}
			
			for (j = 0; j< i; j++) {   //coin haut droite
				if (canEnterCell((x+i-j-1+g.getSizeX())%g.getSizeX(),(y-j-1+g.getSizeY())%g.getSizeY()) == true){
					visibleCells.addFirst(new NodeAStar((x+i-j-1+g.getSizeX())%g.getSizeX(),(y-j-1+g.getSizeY())%g.getSizeY(),true));
					if (g.get((x+i-j-1+g.getSizeX())%g.getSizeX(),(y-j-1+g.getSizeY())%g.getSizeY()) == 1) {     // s'il y a de l'herbe sur cette coordonnee, on retourne cette position comme resultat.
						int[] tab = new int[2];
						tab[0]= x+i-j-1;
						tab[1]= y-j-1;
						return tab;
					}
				}
			}
			
			for (j = 0; j< i; j++) {   //coin haut gauche
				if ( canEnterCell((x-i+j+g.getSizeX())%g.getSizeX(),(y-j+g.getSizeY())%g.getSizeY()) == true){
					visibleCells.addFirst(new NodeAStar((x-i+j+g.getSizeX())%g.getSizeX(),(y-j+g.getSizeY())%g.getSizeY(),true));
					if (g.get((x-i+j+g.getSizeX())%g.getSizeX(),(y-j+g.getSizeY())%g.getSizeY()) == 1){     // s'il y a de l'herbe sur cette coordonnee, on retourne cette position comme resultat.
						int[] tab = new int[2];
						tab[0]= x-i+j;
						tab[1]= y-j;
						return tab;
					}
				}
			}
		}
		// si on ne trouve pas d'herbe... 
		int[] tab = new int[2];
		tab[0]= 99999;
		tab[1]= 99999;
		return tab;
		
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
							if (a.type.equals(this.preyType)) {
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
							if (a.type.equals(this.preyType)) {
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
							if (a.type.equals(this.preyType)) {
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
							if (a.type.equals(this.preyType)) {
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

public int[] searchPartner() {
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
							if (a.type.equals(this.type) && a.gender != this.gender && a.age > AGE_CONSENT && a.timer > WAIT_BEFORE_REPRODUCE) {
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
							if (a.type.equals(this.type) && a.gender != this.gender && a.age > AGE_CONSENT && a.timer > WAIT_BEFORE_REPRODUCE) {
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
							if (a.type.equals(this.type) && a.gender != this.gender && a.age > AGE_CONSENT && a.timer > WAIT_BEFORE_REPRODUCE) {
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
						for (Agent a : tabA) {  											 // pour chaque agent sur cette case, on verifie si c'est un partenaire pour la reproduction
							if (a.type.equals(this.type) && a.gender != this.gender && a.age > AGE_CONSENT && a.timer > WAIT_BEFORE_REPRODUCE) {
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
	
	public int[] searchPredator() {
		int i = 0;
		int j = 0;
		ArrayList<Agent> tabA = null;
		
		for (i = 1; i < range; i++) {  
			for (j = 0; j< i; j++) {
				tabA = Agent.searchAgentInXY(tabAgents,  (x-i+j+1+g.getSizeX())%g.getSizeX(),(y+j+1+g.getSizeY())%g.getSizeY());     // on cherche tous les agents sur cette case
				if (tabA.size() != 0) {    												 // s'il y a un agent sur cette case ... 
					for (Agent a : tabA) {  											 // pour chaque agent sur cette case, on verifie si c'est un predteur
						if (a.type.equals(this.predatorType)) {
							int[] tab = new int[2];
							tab[0]= x-i+j+1;
							tab[1]= y+j+1;
							return tab;
						}
					}
				}
			}
			
			for (j = 0; j< i; j++) {
				tabA = Agent.searchAgentInXY(tabAgents,(x+i-j+g.getSizeX())%g.getSizeX(),(y+j+g.getSizeY())%g.getSizeY());    	 // on cherche tous les agents sur cette case
				if (tabA.size() != 0) {    												 // s'il y a un agent sur cette case ... 
					for (Agent a : tabA) {  											 // pour chaque agent sur cette case, on verifie si c'est un predateur
						if (a.type.equals(this.predatorType)) {
							int[] tab = new int[2];
							tab[0]= x+i-j;
							tab[1]= y+j;
							return tab;
						}
					}
				}
			}
			
			for (j = 0; j< i; j++) {
				tabA = Agent.searchAgentInXY(tabAgents,(x+i-j-1+g.getSizeX())%g.getSizeX(),(y-j-1+g.getSizeY())%g.getSizeY());    	 // on cherche tous les agents sur cette case
				if (tabA.size() != 0) {    												 // s'il y a un agent sur cette case ... 
					for (Agent a : tabA) {  											 // pour chaque agent sur cette case, on verifie si c'est un predateur
						if (a.type.equals(this.predatorType)) {
							int[] tab = new int[2];
							tab[0]= x+i-j-1;
							tab[1]= y-j-1;
							return tab;
						}
					}
				}
			}
			
			for (j = 0; j< i; j++) {
				tabA = Agent.searchAgentInXY(tabAgents,(x-i+j+g.getSizeX())%g.getSizeX(),(y-j+g.getSizeY())%g.getSizeY());    	 // on cherche tous les agents sur cette case
				if (tabA.size() != 0) {    												 // s'il y a un agent sur cette case ... 
					for (Agent a : tabA) {  											 // pour chaque agent sur cette case, on verifie si c'est un predateur
						if (a.type.equals(this.predatorType)) {
							int[] tab = new int[2];
							tab[0]= x-i+j;
							tab[1]= y-j;
							return tab;
						}
					}
				}
			}
		}
		// si on ne trouve pas de predateur... 
		int[] tab = new int[2];
		tab[0]= 99999;
		tab[1]= 99999;
		return tab;		
	}
	
	public void tryToEat() {
		
	}

	public void tryToReproduce(ArrayList<Agent> tabAgents){

	}
	
	public void eatPrey(Agent a) {
		a.death();

		health += 50;
		if (health > maxHealth) {
			health = maxHealth;
		}
	}
	
	public void eatGrass(int x2, int y2) {
		g.setb(x2, y2, 2);  // On met de la terre au lieu d'herbe a la case (x2,y2)

		health += 25;
		if (health > maxHealth) {
			health = maxHealth;
		}
	}
	
	public void death() {
		this.isdead = true;
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public String getPreyType(){
		return preyType;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getOrientation(){
		return orientation;
	}

	public int getMaxHealth(){
		return maxHealth;
	}
	
	public int getHealth(){
		return health;
	}

	public int getAge(){
		return age;
	}

	public Image image(int i, int j) {
		return images.image(i, j);
	}
	
	public int getStates() {
		return images.getNbStates();
	}
	
	public int getNumIte() {
		return numIte;
	}
	
	public int getTravelTime() {
		return travelTime;
	}
	
	public int getIndiceImg() {
		return tabAnim[numIte];
	}
	
	public void setTabAgent(ArrayList<Agent> tab) {
		tabAgents = tab;
	}
	
	public boolean getDead() {
		return isdead;
	}

	public int getMode(){
		return mode;
	}

	public LinkedList<NodeAStar> getPathToPrey(){
		return pathToPrey;
	}

	public LinkedList<NodeAStar> getVisibleCells(){
		return visibleCells;
	}

	public Color getRangeColor(){
		return rangeColor;
	}

	public Color getTargetColor(){
		return targetColor;
	}

	public String getGender(){
		if (gender == 0){
			return "male";
		}
		else { 
			return "femelle";
		}
	}

	public static int getStaticAgeMax(){
		return AGE_MAX;
	}

	public int getAgeMax(){
		return ageMax;
	}

	public void setSeason(int i){
		season = i;
	}

	public void summerMode(){
	}

	public void springMode(){
	}

	public void winterMode(){
	}

	public void fallMode(){
	}

	public void changeImageList(String s) {
		ImageList newList = new ImageList(images.getNbStates(), images.getNbFrames(), s);
		images = newList;
	}

}
