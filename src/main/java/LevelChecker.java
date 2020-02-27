
public class LevelChecker {

	Level lvl;
	public static int TRY_COUNTER = 0;

	public LevelChecker(Level level) {
		lvl = level;
	}

	public boolean searchPath() {
		TRY_COUNTER = 0;
		if( searchPath(lvl.clone(), 0, 0)) {
			return true;
		}else {
			System.out.println("\nNiveau non finissable.");
			return false;
		}
	}
	
	public void testClone() {
		Level temp = (Level) lvl.clone();
		System.out.println("normal");
		lvl.affiche();

		temp.affiche();
		System.out.println("cloné");
		temp.rotate(1,1);

		lvl.affiche();
		temp.affiche();
	}

	public boolean searchPath(Level level, int x, int y) {
		TRY_COUNTER++;
		level.selected_x = x;
		level.selected_y = y;
		level.setFull(y,x);
		level.afficheChemin();
		if (level.isEnd(x, y)) {
			level.printBlocker();
			level.afficheChemin();
			System.out.println("\nLe niveau est finissable :)");
			System.out.println("Solution trouvée en "+TRY_COUNTER+" opération.");
			return true;
		}
		// on clone le niveau
		if (possibleDir(level, x, y, "DOWN") && searchPath(level.clone(), x, y + 1))
			return true;
		if (possibleDir(level, x, y, "UP") && searchPath(level.clone(), x, y - 1))
			return true;
		if (possibleDir(level, x, y, "RIGHT") && searchPath(level.clone(), x + 1, y))
			return true;
		if (possibleDir(level, x, y, "LEFT") && searchPath(level.clone(), x - 1, y))
			return true;
		//down
		Level tempDown = level.clone();
		for(int i = 0 ; i < 3 ; i++) {
			if(tempDown.isInTab(y+1,x) &&!tempDown.isFull(y+1,x)) {
				tempDown.rotate(y+1,x);
				if (possibleDir(tempDown, x, y, "DOWN") && searchPath(tempDown.clone(), x, y+1))
				return true;
			}
		}
		//up
		Level tempUp = level.clone();
		for(int i = 0 ; i < 3 ; i++) {
			if(tempUp.isInTab(y-1,x) &&!tempUp.isFull(y-1,x)) {
				tempUp.rotate(y-1,x);
				if (possibleDir(tempUp, x, y, "UP") && searchPath(tempUp.clone(), x, y-1))
				return true;
			}
		}
		//right
		Level tempRight = level.clone();
		for(int i = 0 ; i < 3 ; i++) {
			if(tempRight.isInTab(y,x+1) && !tempRight.isFull(y,x+1)) {
				tempRight.rotate(y,x+1);
				if (possibleDir(tempRight, x, y, "RIGHT") && searchPath(tempRight.clone(), x + 1, y))
				return true;
			}
		}
		//left
		Level tempLeft = level.clone();
		for(int i = 0 ; i < 3 ; i++) {
			if(tempLeft.isInTab(y,x-1) && !tempLeft.isFull(y,x-1)) {
				tempLeft.rotate(y,x-1);
				if (possibleDir(tempLeft, x, y, "LEFT") && searchPath(tempLeft.clone(), x - 1, y))
				return true;
			}
		}
		return false;

	}

	boolean possibleDir(Level level, int x, int y, String dir) {
		int offset_x = 0;
		int offset_y = 0;
		switch (dir) {
		case "DOWN":
			offset_y = 1;
			break;
		case "UP":
			offset_y = -1;
			break;
		case "RIGHT":
			offset_x = 1;
			break;
		case "LEFT":
			offset_x = -1;
			break;
		}
		return (level.isInTab(y+offset_y, x+offset_x)
				&& level.connected(level.pieces[y][x], level.pieces[y + offset_y][x + offset_x], dir)
				&& !level.pieces[y + offset_y][x + offset_x].isFull());
	}
}
