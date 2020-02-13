import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;

public class Main {

<<<<<<< HEAD
	public static void main(String[] args) throws Exception {
		Level l = new Level(5, 8, 'f', 4586958);
		Menu m1 = new Menu();
=======
	public static void main(String[] args) {
		try {
			Level lvl2 = new Level(1);
			InputsWindow iw = new InputsWindow(lvl2);
			lvl2.affiche();
			//lvl2.play();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
>>>>>>> 71939cc7fc1fc0570db21e6f7ab1400b27d18074
	}
}
