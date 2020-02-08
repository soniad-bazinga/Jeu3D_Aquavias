import java.util.Random;
import java.util.Scanner;

public class Level {
	Piece[][] pieces;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLUE = "\u001b[36m";
	public final int WIDTH;
	public final int HEIGHT;

	public Level(int w, int h) {
		WIDTH = w;
		HEIGHT = h;
		pieces = new Piece[h][w + 2]; /* first column and last column are empty and only contains start and end */
		pieces[0][0] = new PieceI(); /* placing first piece at 0,0 */
		pieces[0][0].rotate();
		pieces[0][0].setFull(true);
		pieces[h - 1][w + 1] = new PieceI();
		pieces[h - 1][w + 1].rotate(); /* placing last piece at max coord */
		for (int i = 0; i < h; i++) {
			for (int j = 1; j < w + 1; j++) { /* placing random pieces for the moment */
				Random r = new Random();
				int rand = r.nextInt(3);
				switch (rand) {
				case 0:
					pieces[i][j] = new PieceT();
					break;
				case 1:
					pieces[i][j] = new PieceI();
					break;
				case 2:
					pieces[i][j] = new PieceL();
					break;
				}
			}
		}
	}

	void affiche() { /* print the state of the game */
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[i].length; j++) {
				if (pieces[i][j] != null) {
					if (pieces[i][j].isFull())
						System.out.print(ANSI_BLUE); /* if piece is full print it in blue */
					System.out.print(pieces[i][j].toString());
					if (pieces[i][j].isFull())
						System.out.print(ANSI_RESET); /* and stop the blue */
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
	}

	void rotate(int i, int j) { /* rotate the piece at i,j coord, but reset it before rotating */
		if (i < pieces.length && j < pieces[i].length && pieces[i][j] != null)
			pieces[i][j].setFull(false);
		pieces[i][j].rotate();
		update();
	}

	void play() { /* basic method to play (very primitive, such basic) */
		Scanner sc = new Scanner(System.in);
		update();
		while (true) {
			affiche();
			String s = sc.next();
			int x = Character.getNumericValue(s.charAt(0));
			int y = Character.getNumericValue(s.charAt(1));
			rotate(x, y);
			System.out.println();
		}
	}
	
	void update() {	
		//vide d'abord entièrement l'eau du circuit
		//puis appelle update dès la source
		voidAll();
		update(0,0);
	}
	
	private void voidAll() {	//vide l'eau de tout le circuit sauf de la source
		for (int i=0;i<HEIGHT;i++) {
			for(int j=1;j<WIDTH;j++) {
				if(pieces[i][j]!=null)
					pieces[i][j].setFull(false);
			}
		}
	}
	
	void update(int i, int j) {
		//vérifie que les pièces limitrophes existent, qu'elles sont connectées à l'actuelle et qu'elles ne sont pas déjà remplies
		if (isInTab(i + 1, j) && connected(pieces[i][j], pieces[i + 1][j], "DOWN")&&!pieces[i + 1][j].isFull()) { 
			setFull(i+1, j);
			update(i+1,j);
		}
		if (isInTab(i - 1, j) && connected(pieces[i][j], pieces[i - 1][j], "UP")&&!pieces[i - 1][j].isFull()) {
			setFull(i-1, j);
			update(i-1,j);
		}
		if (isInTab(i, j + 1) && connected(pieces[i][j], pieces[i][j + 1], "RIGHT")&&!pieces[i][j + 1].isFull()) {
			setFull(i, j+1);
			update(i,j+1);

		}
		if (isInTab(i, j - 1) && connected(pieces[i][j], pieces[i][j - 1], "LEFT")&&!pieces[i][j - 1].isFull()) {
			setFull(i, j-1);
			update(i,j-1);
		}
	}

	void setFull(int i, int j) { /* to set piece i,j full */
		pieces[i][j].setFull(true);
	}

	boolean isInTab(int i, int j) { /* check if the piece is in the array */
		if(i < HEIGHT && j < WIDTH+2 && i >= 0 && j >= 0)
			return pieces[i][j]!=null;
		return false;
	}

	boolean isFull(int i, int j) { /* check if piece i,j is full */
		return pieces[i][j].isFull();
	}

	boolean connected(Piece p1, Piece p2,
			String direction) {
		if (p1 == null || p2 == null)
			return false;
		switch (direction) {
		case "UP":
			return (p1.isUp() && p2.isDown());
		case "RIGHT":
			return (p1.isRight() && p2.isLeft());
		case "DOWN":
			return (p1.isDown() && p2.isUp());
		case "LEFT":
			return (p1.isLeft() && p2.isRight());
		}
		return false;
	}
}
