import java.io.IOException;

import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) {
		try {
			Level lvl = new Level(1);
			System.out.println("\n Le niveau : \n");
			lvl.affiche();
			LevelChecker lvlcheck = new LevelChecker(lvl);
			//lvlcheck.testClone();
			lvlcheck.searchPath(); 
			//InputsWindow iw = new InputsWindow(lvl);
			//lvl2.play();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
