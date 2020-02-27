

public class Main {

	public static void main(String[] args) {
		try {
			Level lvl = new Level(1);
			lvl.affiche();
			InputsWindow iw = new InputsWindow(lvl);
			//lvl2.play();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
