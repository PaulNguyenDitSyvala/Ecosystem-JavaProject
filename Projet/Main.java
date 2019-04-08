
public class Main {
	public static int SLEEP = Constantes.SLEEP;

	public static void main(String[] args) {
		GraphicWindow gw = new GraphicWindow();
		int i = 0;
		while (true) {
			
			try {
				Thread.sleep(SLEEP);
			}
			catch(Exception e) {
			}
			gw.repaint();
			System.out.println("iteration : "+i);
			System.out.println("Nombre de Renards vivants (crees): " +Fox.foxAlive() + "("+ Fox.foxBorn() + ")");
			System.out.println("Nombre de Poulets vivants (crees): " +Chicken.chickenAlive() + "("+ Chicken.chickenBorn() + ")");
			System.out.println("Nombre de Moutons vivants (crees): " +Sheep.sheepAlive() + "("+ Sheep.sheepBorn() + ")");
			i++;
		}
	}
}
