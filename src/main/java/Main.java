import java.io.IOException;

import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) {
		try {
			Level lvl2 = new Level(1);
			InputsWindow iw = new InputsWindow(lvl2);
			lvl2.affiche();
			//lvl2.play();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
