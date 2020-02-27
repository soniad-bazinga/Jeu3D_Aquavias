import java.io.IOException;

import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) {
		try {
			Level lvl = new Level(0);
			System.out.println("\nTrouvons un chemin :\n ");
			lvl.randomizeLevel(3);
			LevelChecker lvlcheck = new LevelChecker(lvl);
			lvlcheck.searchPath(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
