/*	noeud d'un graph pour l'algorithme A-star
	un noeud est represente par : 
		- une paire de coordonnees (x,y)
		- un noeud pere 
		- la valeur du noeud depuis le point de depart
		- une estimation de la distance restante jusqu'au but
*/

public class NodeAStar {

	protected static final int MOVEMENT_COST = 1;

	private int x;
	private int y;

	//private int i;
	//private int j;

	private int valFromOrigin; 
	private int valToGoal;
	private NodeAStar parent;
	private boolean walkable;

	public NodeAStar (/*int I, int J, */int X, int Y, boolean w) {
		//i = I;
		// = J;
		x = X;
		y = Y;
		walkable = w;
		valFromOrigin = 0;
		valToGoal = 0;
	}

	public void setValOrigin(NodeAStar parent){
		valFromOrigin = (parent.getValOrigin() + MOVEMENT_COST);
	}

	public int calculateValOrigin(NodeAStar parent){
		return (parent.getValOrigin() + MOVEMENT_COST);
	}

	public void setValToGoal(NodeAStar goal){
		valToGoal = (Math.abs(getX() - goal.getX()) + Math.abs(getY() - goal.getY())) * MOVEMENT_COST;
	}

	int getX() {
		return x;
	}
	
	int getY() {
		return y;
	}
/*
	int getI() {
		return i;
	}
	
	int getJ() {
		return j;
	}		
*/
	public boolean isWalkable(){
		return walkable;
	}

	int getValOrigin() {
		return valFromOrigin;
	}

	int getValToGoal() {
		return valToGoal;
	}

	int getTotalValue(){
		return valFromOrigin+valToGoal;
	}
	
	NodeAStar getParent() {
		return parent;
	}

	public void setX(int x){
		this.x = x;
	}

	public void setY(int y){
		this.y = y;
	}
/*
	public void setI(int i){
		this.i = i;
	}

	public void setJ(int j){
		this.j = j;
	}
*/
	public void setWalkable(boolean walkable){
		this.walkable = walkable;
	}

	public void setParent(NodeAStar parent){
		this.parent = parent;
	}

	public boolean equals(Object o){
		if (o == null)
			return false;

		if ((o instanceof NodeAStar) == false)
			return false;

		if (o == this)
			return true;

		NodeAStar n = (NodeAStar) o;

		if (n.getX() == x && n.getY() == y && n.isWalkable() == walkable)
			return true;

		return false;
	}




}
