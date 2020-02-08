import java.io.IOException;

import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) {
		Level lvl = new Level(20, 5);
		lvl.saveLevel();
		Level lvl2 = new Level(0);
		//lvl.saveLevel();
	}
}
