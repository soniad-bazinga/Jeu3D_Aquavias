import java.io.IOException;

import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) {
		try {
			Level lvl = new Level(15,5);
			System.out.println("\n Le niveau : \n");
			lvl.randomizeLevel();
			lvl.affiche();
			System.out.println("\nTrouvons un chemin :\n ");
			LevelChecker lvlcheck = new LevelChecker(lvl);
			lvlcheck.searchPath(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
