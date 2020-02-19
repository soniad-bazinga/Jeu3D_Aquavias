
public class LevelChecker {

	Level lvl;

	public LevelChecker(Level level) {
		lvl = level;
	}

	public boolean searchPath() {
		return searchPath(lvl, 0, 0);
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
		System.out.println("\n");
		if (level.isEnd(x, y))
			return true;
		// check for every dir
		Level temp = (Level) level.clone();
		temp.affiche();
		temp.selected_x = x;
		temp.selected_y = y;
		temp.setFull(y,x);
		if (possibleDir(temp, x, y, "DOWN") && searchPath(temp, x, y + 1))
			return true;
		for(int i = 0 ; i < 3 ; i++) {
			if(temp.isInTab(y+1,x) &&!temp.isFull(y+1,x)) {
				temp.rotate(y+1,x);
				temp.affiche();
				if (possibleDir(temp, x, y, "DOWN") && searchPath(temp, x, y+1))
				return true;
			}
		}
		if (possibleDir(temp, x, y, "UP") && searchPath(temp, x, y - 1))
			return true;
		for(int i = 0 ; i < 3 ; i++) {
			if(temp.isInTab(y-1,x) &&!temp.isFull(y-1,x)) {
				temp.rotate(y-1,x);
				temp.affiche();
				if (possibleDir(temp, x, y, "UP") && searchPath(temp, x, y-1))
				return true;
			}
		}
		if (possibleDir(temp, x, y, "RIGHT") && searchPath(temp, x + 1, y)) {
			return true;
		}
		for(int i = 0 ; i < 3 ; i++) {
			if(temp.isInTab(y,x+1) && !temp.isFull(y,x+1)) {
				temp.rotate(y,x+1);
				temp.affiche();
				if (possibleDir(temp, x, y, "RIGHT") && searchPath(temp, x + 1, y))
				return true;
			}
		}
		if (possibleDir(temp, x, y, "LEFT") && searchPath(temp, x - 1, y))
			return true;
		for(int i = 0 ; i < 3 ; i++) {
			if(temp.isInTab(y,x-1) && !temp.isFull(y,x-1)) {
				temp.rotate(y,x-1);
				temp.affiche();
				if (possibleDir(temp, x, y, "LEFT") && searchPath(temp, x - 1, y))
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
