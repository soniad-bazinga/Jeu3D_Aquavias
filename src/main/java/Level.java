import java.util.Random;
import java.util.Scanner;

public class Level {
	Piece[][] pieces;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLUE = "\u001b[36m";
	public final int WIDTH;
	public final int HEIGHT;
	private final char type; //Type of lvl (Leaking, Move Counter, Normal) -> 'l' = Leaking, 'c' = Move counter, 'n' = normal

	public Level(int w, int h, char t) {
		type = t;
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

	boolean isLeaking() {
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 1; j < pieces[i].length; j++) {
				if (pieces[i][j] != null) {
					if (pieces[i][j].isFull()) { //If the piece contains water
						if (pieces[i][j].isDown()) { //If the piece has an exit facing down
							if (i == pieces.length - 1) {
								System.out.println("La pièce aux coordonnées (" + i + ", " + j + ") fuit.");
								return true; //If the piece is in the last line of the array/level
							} else {
								if (pieces[i + 1][j] != null) { //If there is a piece below
									if (!connected(pieces[i][j], pieces[i + 1][j], "DOWN")) { //If the two pieces don't connect
										System.out.println("La pièce aux coordonnées (" + i + ", " + j + ") fuit.");
										return true; //It is leaking
									}
								}
								return true; //If there is nothing below, it is leaking
							}
						} else if (pieces[i][j].isLeft()) { //If the piece has an exit facing left
							if (j == 1 && i != 0) {
								System.out.println("La pièce aux coordonnées (" + i + ", " + j + ") fuit.");
								return true; //If it's the first in its line, it's leaking
							}
							if (pieces[i][j - 1] != null) {
								if (!connected(pieces[i][j], pieces[i][j - 1], "LEFT")) { //If the two pieces don't connect
									System.out.println("La pièce aux coordonnées (" + i + ", " + j + ") fuit.");
									return true; //It is leaking
								}
							}
							return true; //If there's nothing left of the piece
						} else if (pieces[i][j].isRight()) { //If the piece has an exit facing right
							if (j == pieces[i].length - 1 && i != pieces.length - 1) {
								System.out.println("La pièce aux coordonnées (" + i + ", " + j + ") fuit.");
								return true; //If it's the last in its line, it's leaking
							}
							if (pieces[i][j + 1] != null) {
								if (!connected(pieces[i][j], pieces[i][j + 1], "RIGHT")) { //If the two pieces don't connect
									System.out.println("La pièce aux coordonnées (" + i + ", " + j + ") fuit.");
									return true; //It is leaking
								}
							}
							return true; //If there's nothing right of the piece
						} else if (pieces[i][j].isUp()) { //If the piece has an exit facing up
							if (i == 0) {
								System.out.println("La pièce aux coordonnées (" + i + ", " + j + ") fuit.");
								return true; //If it's in the first line, it's leaking
							}
							if (pieces[i - 1][j] != null) {
								if (!connected(pieces[i][j], pieces[i - 1][j], "LEFT")) { //If the two pieces don't connect
									System.out.println("La pièce aux coordonnées (" + i + ", " + j + ") fuit.");
									return true; //It is leaking
								}
							}
							return true; //If there's nothing above the piece, then it's leaking
						}
					}
				}
			}
		}
		return false;
	}

	void affiche() { /* print the state of the game */
		for (Piece[] piece : pieces) {
			for (Piece value : piece) {
				if (value != null) {
					if (value.isFull())
						System.out.print(ANSI_BLUE); /* if piece is full print it in blue */
					System.out.print(value.toString());
					if (value.isFull())
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
	}

	void play() { /* basic method to play (very primitive, such basic) */
		Scanner sc = new Scanner(System.in);
		while (true) {
			affiche();
			String s = sc.next();
			int x = Character.getNumericValue(s.charAt(0));
			int y = Character.getNumericValue(s.charAt(1));
			rotate(x, y);
			update(x, y);
			System.out.println();
		}
	}

	void update(int i, int j) {                                                                                    // primitive method to update piece, the
		if (isInTab(i + 1, j) && connected(pieces[i][j], pieces[i + 1][j], "DOWN")) {    // if statements are good, gotta keep them
			if (isFull(i + 1, j)) {                                                            // but we should make it recursive
				setFull(i, j);                                                            // and check if its connected to the source
			}
		}
		if (isInTab(i - 1, j) && connected(pieces[i][j], pieces[i - 1][j], "UP")) {
			if (isFull(i - 1, j)) {
				setFull(i, j);
			}
		}
		if (isInTab(i, j + 1) && connected(pieces[i][j], pieces[i][j + 1], "RIGHT")) {
			if (isFull(i, j + 1)) {
				setFull(i, j);
			}
		}
		if (isInTab(i, j - 1) && connected(pieces[i][j], pieces[i][j - 1], "LEFT")) {
			if (isFull(i, j - 1)) {
				setFull(i, j);
			}
		}
		if (Character.toLowerCase(type) == 'l') {
			if (isLeaking()) System.out.println("Careful! There's a leak!");
		}
		if (Victory()) //Victory !!
			System.out.print("You Win!!");
	}

	boolean Victory() {
		return (pieces[HEIGHT - 1][WIDTH - 1].isFull());
	}

	void setFull(int i, int j) { /* to set piece i,j full */
		pieces[i][j].setFull(true);
	}

	boolean isInTab(int i, int j) { /* check if the piece is in the array */
		return (i < HEIGHT && j < WIDTH && i >= 0 && j >= 0);
	}

	boolean isFull(int i, int j) { /* check if piece i,j is full */
		return pieces[i][j].isFull();
	}

	boolean connected(Piece p1, Piece p2, String direction) { /* check if 2 piece are connected given a certain position */
		if (p1 == null || p2 == null) return false;
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
