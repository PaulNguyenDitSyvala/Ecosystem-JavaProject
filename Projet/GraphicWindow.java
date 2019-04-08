

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.ArrayList;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

public class GraphicWindow extends JPanel implements KeyListener,MouseListener,MouseWheelListener{
	
	private static final long serialVersionUID = 1L;
	private static int WINDOW_X = Constantes.WINDOW_X;
	private static int WINDOW_Y = Constantes.WINDOW_Y;

	private static final int SIZE_X = Constantes.SIZE_X;
	private static final int SIZE_Y = Constantes.SIZE_Y;
	private static final int SIZE_SPRITE = Constantes.SIZE_SPRITE;
	private static final int NB_RANDOM_AGENTS = Constantes.NB_RANDOM_AGENTS;
	private static final int NB_CHICKEN = Constantes.NB_CHICKEN;
	private static final int NB_SHEEP = Constantes.NB_SHEEP;
	private static final int NB_FOX = Constantes.NB_FOX;
	private static final int DAY_DURATION = 100;
	private static final int SEASON_DURATION = DAY_DURATION * 3;
	private static int[] tabAnim = new int [] {0,1,2,1,2,1,2,1,2,1,2,1};  
	private JFrame frame;

	private int clock;

	private int sizeX;
	private int sizeY;
	private int spLen;
	private int ite;    // iterations pour les animations
	
	private int originX;  // coordonnees pour la position de la camera sur la map
	private int originY;
	private int season;
	
	private Height h;
	private Ground gr;
	private Ecoulement ec;
	private Forest f;
  private Night n;
	private Rivers[] r;
	
	private ArrayList<Agent> tabAgents;
	private ArrayList<Agent> newAgents;

	private boolean displayPath;    // affiche les champs de vision et les chemins des agents
	private boolean displayHealthBar;		// affiche la vie et l'age des agents 
	private boolean displayMode;	  // affiche le mode (0:random, 1:chasse, 2:fuite, 3:reproduction)   et l'orientation de l'agent (0:gauche, 1:haut, 2:droite, 3:bas, 4:sur place, 5:dodo)
	
	public GraphicWindow(int X, int Y, int spriteLength, int hMode, int grMode, int fMode) {
		clock = 1;
		sizeX = X;
		sizeY = Y;
		season = 1;
		spLen = spriteLength;
		ite = 0;
		originX = 0;
		originY = 0;
		displayPath = false;
		displayHealthBar = false;
		displayMode = false;
		n = new Night("night_test");
		h = new Height(sizeX, sizeY, 9, 1, hMode);	
		//h = new Height(sizeX, sizeY, 9, 1, 1);	

		gr = new Ground(sizeX, sizeY, 5, 1, grMode, h);
		ec = new Ecoulement(sizeX, sizeY, 0, 0, gr, h, 4, 20,3);
		f = new Forest(sizeX, sizeY, 5, 3, fMode, gr);

		r = new Rivers[5];
		for (int i = 0; i< 5; i++){
			r[i] = new Rivers(sizeX, sizeY, gr, h);
		}

		tabAgents = new ArrayList<Agent>();
		newAgents = new ArrayList<Agent>();

		for (int i = 0; i < NB_RANDOM_AGENTS; i++) {
			tabAgents.add(Agent.createRandomAgent2(gr,f));
		}
		for (int i = 0; i < NB_FOX; i++) {
			tabAgents.add(Fox.createFox(gr,f));
		}
		for (int i = 0; i < NB_CHICKEN; i++) {
			tabAgents.add(Chicken.createChicken(gr,f));
		}

		for (int i = 0; i < NB_SHEEP; i++) {
			tabAgents.add(Sheep.createSheep(gr,f));
		}

		f.setTabAgent(tabAgents);
		for (Agent a : tabAgents) {
			a.setTabAgent(tabAgents);
		}

		if (WINDOW_X > 1500) {
			WINDOW_X = 1500;
		}
		
		if (WINDOW_Y > 900) {
			WINDOW_Y = 900;
		}
		frame = new JFrame("Ecosystème");
		frame.add(this);
		frame.setSize(WINDOW_X,WINDOW_Y);
		frame.setLocationRelativeTo(null); // place la fenetre au milieu de l'ecran
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //termine le processus en fermant cette fenetre
		
		addMouseListener(this); // pour lire les clics de la souris
		addMouseWheelListener(this); // pour lire la molette de la souris
		addKeyListener(this); // pour lire les entrees claviers
		setFocusable(true);

	}
	
	public GraphicWindow(int hMode, int grMode, int fMode){
		this(SIZE_X,SIZE_Y,SIZE_SPRITE,hMode,grMode,fMode);
	}
	
	public GraphicWindow(){
		this(SIZE_X,SIZE_Y,SIZE_SPRITE,3,3,3);
	}

	public void paint(Graphics g){
		
 		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		int height = 0;
		int maxFrames = 12;
		step();
		int frames = ite % maxFrames;
		
		for ( int i = 0+originX ; i < (WINDOW_X / spLen)+1+originX && i < SIZE_X; i++ ) {
			for ( int j = 0+originY ; j <  (WINDOW_Y / spLen)+1+originY && j < SIZE_Y; j++ ) {
				// on dessine le denivele de hauteur
				height = h.get(i,j)/7-1;
				
				for (int a=0; a < height; a++) {
					g2.drawImage(h.image(0,0),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*a,spLen,spLen, frame);
				}
				if (height <0) {
					height = 0;
				}
				// on dessine le terrain
				g2.drawImage(gr.image(gr.get(i,j),0),spLen*(i-originX),spLen*(j-originY) - (4*spLen/32)*height,spLen,spLen, frame);	

				// on dessine les rivieres
				if (season != 3){
					for (int k = 0; k<5; k++){ 
						Rivers r1 = r[k];
						while (r1 != null){
							g2.drawImage(r1.image(tabAnim[frames],0),spLen*(r1.getX()-originX),spLen*(r1.getY()-originY) - (4*spLen/32)*height,spLen,spLen, frame);	
							r1 = r1.getNext();
						}
					}
				}
				// on dessine pour les 4 directions, le rebord si on se trouve au dessus de la case d'a coté
				if (j > 0) 
					if (h.get(i,j)/7-1 > h.get(i,j-1)/7-1 ) 
						g2.drawImage(h.image(1,0),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height,spLen,spLen, frame);
				
				if (j < sizeY-1)
					if (h.get(i,j)/7-1 > h.get(i,j+1)/7-1 )
						g2.drawImage(h.image(4,0),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height,spLen,spLen, frame);
				
				if (i > 0)
					if (h.get(i,j)/7-1 > h.get(i-1,j)/7-1 )
						g2.drawImage(h.image(2,0),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height,spLen,spLen, frame);
				
				if (i < sizeX-1)
					if (h.get(i,j)/7-1 > h.get(i+1,j)/7-1 )
						g2.drawImage(h.image(3,0),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height,spLen,spLen, frame);

				
				// on dessine pour les 4 directions, l'ombre si on se trouve au dessous de la case d'a coté
				
				if (j > 0)
					if (h.get(i,j)/7-1 < h.get(i,j-1)/7-1 )
						g2.drawImage(h.image(5,0),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height,spLen,spLen, frame);
				
				if (j < sizeY-1)
					if (h.get(i,j)/7-1 < h.get(i,j+1)/7-1 )
						g2.drawImage(h.image(8,0),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height,spLen,spLen, frame);
				
				if (i > 0)
					if (h.get(i,j)/7-1 < h.get(i-1,j)/7-1 )
						g2.drawImage(h.image(6,0),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height,spLen,spLen, frame);

				if (i < sizeX-1)
					if (h.get(i,j)/7-1 < h.get(i+1,j)/7-1 )
						g2.drawImage(h.image(7,0),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height,spLen,spLen, frame);
				
				
			}
		}
		
		for ( int i = 0+originX ; i <  (WINDOW_X / spLen)+1+originX && i < SIZE_X; i++ ) {
			for ( int j = 0+originY ; j <  (WINDOW_Y / spLen)+1+originY && j < SIZE_Y; j++ ) {
				// on dessine l'arbre

				g2.drawImage(f.image(f.get(i,j),tabAnim[frames]),(int)(spLen*(i-originX)-(0*spLen/32)+f.getModX(i,j)*spLen-((f.getModS(i,j)-1)/2*(spLen+0/32.0*spLen))),(int)(spLen*(j-originY)-(4*spLen/32.0)*height-(23*spLen/32.0)+f.getModY(i,j)*spLen-((f.getModS(i,j)-1)*(spLen+(23/32.0*spLen)))),(int)(spLen*(1.0+0.0/32.0)*f.getModS(i, j)),(int)(spLen*(1.0+23.0/32.0)*f.getModS(i, j)), frame);

				// on cherche s'il y a un agent sur la case, et on le (ou les) dessine.

				for (Agent a : Agent.searchAgentInXY(tabAgents, i, j)){
					if ( displayHealthBar == true) {
						if ((double)((double)a.getHealth()/a.getMaxHealth()) > 0.5){
							g2.setColor(Color.GREEN);
						}
						else if ((double)((double)a.getHealth()/a.getMaxHealth()) > 0.2){
							g2.setColor(Color.YELLOW);
						}
						else{
							g2.setColor(Color.RED);
						}
					}
					//System.out.println(a.name +" :    Age : " + a.getAge() + "    age Max : " +a.getAgeMax() + "    ??? : " + (double)(a.getAge())/a.getAgeMax());

					switch (a.getOrientation()){
						case 0 : 
							g2.drawImage(a.image(0,a.getIndiceImg()),spLen*(i-originX)- (spLen*(a.getNumIte())/a.getTravelTime())+spLen,spLen*(j-originY)-(4*spLen/32)*height-(spLen/8),spLen,spLen, frame);
							if ( displayHealthBar == true) {
								g2.fillRect(spLen*(i-originX)- (spLen*(a.getNumIte())/a.getTravelTime())+spLen,spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20*2,(int)(spLen*a.getHealth()/(double)(a.getMaxHealth())),spLen/20);
								g2.setColor(Color.BLUE);
								g2.fillRect(spLen*(i-originX)- (spLen*(a.getNumIte())/a.getTravelTime())+spLen,spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20,(int)(spLen*(double)(a.getAge())/a.getAgeMax()),spLen/20);
							}
							if (displayMode == true){
								g2.setColor(a.getTargetColor());
								g2.drawString(""+a.getMode()+"  "+a.getOrientation(),spLen*(i-originX)- (spLen*(a.getNumIte())/a.getTravelTime())+spLen,spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20*3);
							}
							break;
						case 1 : 
							g2.drawImage(a.image(1,a.getIndiceImg()),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen*(a.getNumIte())/a.getTravelTime())+spLen-(spLen/8),spLen,spLen, frame);
							if ( displayHealthBar == true) {
								g2.fillRect(spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen*(a.getNumIte())/a.getTravelTime())+spLen-(spLen/8)-spLen/20*2,(int)(spLen*a.getHealth()/(double)(a.getMaxHealth())),spLen/20);
								g2.setColor(Color.BLUE);
								g2.fillRect(spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen*(a.getNumIte())/a.getTravelTime())+spLen-(spLen/8)-spLen/20,(int)(spLen*(double)(a.getAge())/a.getAgeMax()),spLen/20);
							}
							if (displayMode == true){
								g2.setColor(a.getTargetColor());
								g2.drawString(""+a.getMode()+"  "+a.getOrientation(),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen*(a.getNumIte())/a.getTravelTime())+spLen-(spLen/8)-spLen/20*3);
							}
							break;
						case 2 : 
							g2.drawImage(a.image(2,a.getIndiceImg()),spLen*(i-originX)+ (spLen*(a.getNumIte())/a.getTravelTime())-spLen,spLen*(j-originY)-(4*spLen/32)*height-(spLen/8),spLen,spLen, frame);
							if ( displayHealthBar == true) {							
								g2.fillRect(spLen*(i-originX)+ (spLen*(a.getNumIte())/a.getTravelTime())-spLen,spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20*2,(int)(spLen*a.getHealth()/(double)(a.getMaxHealth())),spLen/20);
								g2.setColor(Color.BLUE);
								g2.fillRect(spLen*(i-originX)+ (spLen*(a.getNumIte())/a.getTravelTime())-spLen,spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20,(int)(spLen*(double)(a.getAge())/a.getAgeMax()),spLen/20);
							}
							if (displayMode == true){
								g2.setColor(a.getTargetColor());
								g2.drawString(""+a.getMode()+"  "+a.getOrientation(),spLen*(i-originX)+ (spLen*(a.getNumIte())/a.getTravelTime())-spLen,spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20*3);
							}
							break;
						case 3 : 
							g2.drawImage(a.image(3,a.getIndiceImg()),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height+(spLen*(a.getNumIte())/a.getTravelTime())-spLen-(spLen/8),spLen,spLen, frame);
							if ( displayHealthBar == true) {							
								g2.fillRect(spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height+(spLen*(a.getNumIte())/a.getTravelTime())-spLen-(spLen/8)-spLen/20*2,(int)(spLen*a.getHealth()/(double)(a.getMaxHealth())),spLen/20);
								g2.setColor(Color.BLUE);
								g2.fillRect(spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height+(spLen*(a.getNumIte())/a.getTravelTime())-spLen-(spLen/8)-spLen/20,(int)(spLen*(double)(a.getAge())/a.getAgeMax()),spLen/20);
							}
							if (displayMode == true){
								g2.setColor(a.getTargetColor());
								g2.drawString(""+a.getMode()+"  "+a.getOrientation(),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height+(spLen*(a.getNumIte())/a.getTravelTime())-spLen-(spLen/8)-spLen/20*3);
							}
							break;
						case 4 : 
							g2.drawImage(a.image(3,0),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen/8),spLen,spLen, frame);
							if ( displayHealthBar == true) {						
								g2.fillRect(spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20*2,(int)(spLen*a.getHealth()/(double)(a.getMaxHealth())),spLen/20);
								g2.setColor(Color.BLUE);
								g2.fillRect(spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20,(int)(spLen*(double)(a.getAge())/a.getAgeMax()),spLen/20);
							}
							if (displayMode == true){
								g2.setColor(a.getTargetColor());
								g2.drawString(""+a.getMode()+"  "+a.getOrientation(),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20*3);
							}
							break;
						case 5 : 
							g2.drawImage(a.image(4,tabAnim[frames]),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen/8),spLen,spLen, frame);
							if ( displayHealthBar == true) {
								g2.fillRect(spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20*2,(int)(spLen*a.getHealth()/(double)(a.getMaxHealth())),spLen/20);
								g2.setColor(Color.BLUE);
								g2.fillRect(spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20,(int)(spLen*(double)(a.getAge())/a.getAgeMax()),spLen/20);
							}
							if (displayMode == true){
								g2.setColor(a.getTargetColor());
								g2.drawString(""+a.getMode()+"  "+a.getOrientation(),spLen*(i-originX),spLen*(j-originY)-(4*spLen/32)*height-(spLen/8)-spLen/20*3);
							}
							break;
					}

					if (displayPath == true){

						//System.out.println(a.getName());
						//System.out.print("chemin2 : ");
						//System.out.print(a.getPathToPrey().size()+"     ");

						g2.setColor(a.getRangeColor());
						for (NodeAStar node : a.getVisibleCells()){    			// affiche le champ de vision de l'agent
						//	System.out.print("("+node.getX()+","+node.getY()+") ");
							g2.fillRect(spLen*(node.getX()-originX),spLen*(node.getY()-originY),spLen,spLen);
						}
						g2.setColor(a.getTargetColor());
						g2.drawRect(spLen*(i-originX),spLen*(j-originY),spLen,spLen);  	// affiche la case courante de l'agent
						if (a.getPathToPrey().isEmpty() == false){  			// affiche la cible de l'agent
							g2.fillRect(spLen*(a.getPathToPrey().getLast().getX()-originX)+spLen*3/8,spLen*(a.getPathToPrey().getLast().getY()-originY)+spLen*3/8,spLen/4,spLen/4);
						}
						for (NodeAStar node : a.getPathToPrey()){			// affiche le chemin vers la cible
						//	System.out.print("("+node.getX()+","+node.getY()+") ");
							g2.drawRect(spLen*(node.getX()-originX)+spLen*3/8,spLen*(node.getY()-originY)+spLen*3/8,spLen/4,spLen/4);
						}



						g2.setColor(Color.BLACK);
						//System.out.println();
						//System.out.println("FIN2");
					}
						
					
				}
				//g2.drawString("("+i+","+j+")", spLen*(i-originX),spLen*(j-originY));
			}
		} 
	
		if (n.isNight()){
			g2.drawImage(n.getImage(),0,0,WINDOW_X,WINDOW_Y, frame);
		}
		ite++;
    clock ++;
		
	}
	
	public void step() {

		ec.step();

		gr.step();
		
		if (ite%3 == 0) {
			f.step();
			ite = 0;
		}
		
   	if (clock % DAY_DURATION == DAY_DURATION/2)
			n.sunset();

		if (clock % DAY_DURATION == 0)
			n.sunrise();

		if (clock % SEASON_DURATION == 0){
			season = (season+1);   // entre 1 et 4  
			if (season == 5){
				season = 1;
			}
			setSeason(season);
		}
		
		for (Agent a : tabAgents) {
			a.step(n, newAgents);
		}

		ArrayList<Agent> tab = new ArrayList<Agent>();
		for (Agent a : tabAgents) {
			if (a.getDead() == false) {
				tab.add(a);
			}
		}
		
		tabAgents = tab;



		for (Agent a : newAgents) {
			tabAgents.add(a);
		}

		f.setTabAgent(tabAgents);
		for (Agent a : tabAgents) {
			a.setTabAgent(tabAgents);
		}
		WINDOW_X = frame.getWidth();
		WINDOW_Y = frame.getHeight();
		frame.setSize(WINDOW_X,WINDOW_Y);

		newAgents = new ArrayList<Agent>();
	}
	

	 public void keyPressed(KeyEvent evt){
		 int code = evt.getKeyCode();

		 switch(code) {
	        case KeyEvent.VK_DOWN:
						if (originY < SIZE_Y)
							originY ++;
	        	break;
	        	
	        case KeyEvent.VK_UP:
						if (originY > 0)
							originY --;
	        	break;
	            
	        case KeyEvent.VK_LEFT:
						if (originX > 0)
							originX --;
	        	break;
	            
	        case KeyEvent.VK_RIGHT :
						if (originX < SIZE_X)
							originX ++;	            
						break;
				
	        case 65 : // lettre a
						if (spLen < 200) {
							spLen ++;	
						}
						break;
				
	        case 90 :  // lettre z 
						if (spLen > 20) {
							spLen --;
						}
						break;
				
	        case 81 :  // lettre q
	      	 	setSeason(1);   //ete
	        	break;
				
	        case 83 :  // lettre s
	      	 	setSeason(2);   //automne
						break;
			
	        case 68 :  // lettre d
	      	 	setSeason(3);   //hiver
						break;
				
	        case 70 :  // lettre f
	      	 	setSeason(4);   //printemps
						break;

					case KeyEvent.VK_E :
						if (displayPath == true){
							displayPath = false;
						}
						else{
							displayPath = true;
						}
						break;
					
					case KeyEvent.VK_R :
						if (displayHealthBar == true){
							displayHealthBar = false;
						}
						else{
							displayHealthBar = true;
						}
						break;

					case KeyEvent.VK_T :
						if (displayMode == true){
							displayMode = false;
						}
						else{
							displayMode = true;
						}
						break;

		}
	 }

	 public void keyReleased(KeyEvent evt){} 

	 public void keyTyped(KeyEvent evt) {}
	
	 public void mouseClicked(MouseEvent e) {
	 	if (e.getButton() == 1){
			int a = e.getX()/spLen + originX;   // on recupere les coordonnees (a,b) de la case cliquee
			int b = e.getY()/spLen + originY;
			spLen += 3;
			originX = a - ((WINDOW_X / spLen)+1)/2;
			originY = b - ((WINDOW_Y / spLen)+1)/2;

			if (originX<0)
				originX=0;
			if (originY<0)
				originY=0;
		}
		if (e.getButton() == 3){
			int a = e.getX()/spLen+ originX;   // on recupere les coordonnees (a,b) de la case cliquee
			int b = e.getY()/spLen+ originY;
			if (spLen >20)
				spLen -= 3;
			originX = a - ((WINDOW_X / spLen)+1)/2;
			originY = b - ((WINDOW_Y / spLen)+1)/2;

			if (originX<0)
				originX=0;
			if (originY<0)
				originY=0;
		}

			
			
	 }
	 public void mousePressed(MouseEvent e) {}
	 public void mouseReleased(MouseEvent e) {}
	 public void mouseEntered(MouseEvent e) {}
	 public void mouseExited(MouseEvent e) {}
	 public void mouseWheelMoved(MouseWheelEvent e) {
		int c = e.getWheelRotation();
		int a = e.getX()/spLen + originX;   // on recupere les coordonnees (a,b) de la case cliquee
		int b = e.getY()/spLen + originY;
		


		originX = a - ((WINDOW_X / spLen)+1)/2;
		originY = b - ((WINDOW_Y / spLen)+1)/2;

		spLen += c;
		if (spLen < 20)
			spLen = 20;


		if (originX<0)
			originX=0;
		if (originY<0)
			originY=0;
	 }

	 public JFrame getFrame() {
		 return frame;
	 }

	 public void setSeason(int i){
		 season = i;
		 switch (i){
  				case 1 :  
	      	 	f.changeImageList("tree");			// ete
						gr.changeImageList("terrain");
						for(Agent a : tabAgents){
							a.summerMode();
							a.setSeason(1);
						}
	        	break;
				
	        case 2 :  
	        	f.changeImageList("tree_red");		// automne
						gr.changeImageList("terrainFall");
						for(Agent a : tabAgents){
							a.fallMode();
							a.setSeason(2);
						}
						break;
			
	        case 3 :  
	        	f.changeImageList("tree_blue");		// hiver
						gr.changeImageList("terrainWinter");
						for(Agent a : tabAgents){
							a.winterMode();
							a.setSeason(3);
						}
						break;
				
	        case 4 :  
	        	f.changeImageList("tree_pink");  	// printemps
						gr.changeImageList("terrain");
						for(Agent a : tabAgents){
							a.springMode();
							a.setSeason(4);
						}
						break;

			}
	 }

}
