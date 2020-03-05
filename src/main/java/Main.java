import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;

public class Main {
	static MenuWindow mw;

	public static void main(String[] args) throws Exception {
		Main m = new Main();
		Menu m1 = new Menu(m);
		mw = new MenuWindow(m1);
	}
	void playLevel(int i){
		try {
			mw.dispatchEvent(new WindowEvent(mw, WindowEvent.WINDOW_CLOSING));
			Level lvl = new Level(i);
			InputsWindow iw = new InputsWindow((lvl));
		}catch(Exception e){

		}
	}
}