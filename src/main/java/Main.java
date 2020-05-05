import java.io.IOException;

import javafx.application.Application;
import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) throws Exception {
		Level level = new Level(2);
		level.new_update();
		level.affiche();
		View v = new View(level);
	}
}
