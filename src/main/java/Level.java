import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Level {
	Piece[][] pieces;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLUE = "\u001b[36m";
	public static final String ANSI_BOLD = "\u001B[1m";
	public final int ID;
	public final int WIDTH;
	public final int HEIGHT;
	int selected_x;
	int selected_y;

	public Level(int w, int h) {
		WIDTH = w;
		HEIGHT = h;
		pieces = new Piece[h][w + 2]; /* first column and last column are empty and only contains start and end */
		pieces[0][0] = new PieceI(); /* placing first piece at 0,0 */
		pieces[0][0].rotate();
		pieces[0][0].setFull(true);
		pieces[h - 1][w + 1] = new PieceI();
		pieces[h - 1][w + 1].rotate(); /* placing last piece at max coord */
		ID = 0; /* to change with file storage */
	}

	public Level(int id) {
		System.out.println("Chargement du niveau...\n");
		FileReader reader;
		int w = -1;
		int h = -1;
		try {
			reader = new FileReader("levels/level" + id + ".json"); /* on récupère le fichier contenant le lvl */
			JSONParser jsonParser = new JSONParser();
			JSONObject obj = (JSONObject) jsonParser.parse(reader); /* on le parse */
			w = Math.toIntExact((long) obj.get("WIDTH")); /* on récupère les données */
			h = Math.toIntExact((long) obj.get("HEIGHT"));
			id = Math.toIntExact((long) obj.get("ID"));
			JSONArray y = (JSONArray) obj.get("Pieces"); /* on récupère l'array Y (vertical) contenant les array X (horizontaux) */
			/* same que dans level normal, peut être combiner les 2? */
			pieces = new Piece[h][w + 2]; /* first column and last column are empty and only contains start and end */
			pieces[0][0] = new PieceI(); /* placing first piece at 0,0 */
			pieces[0][0].rotate();
			pieces[0][0].setFull(true);
			pieces[h - 1][w + 1] = new PieceI();
			pieces[h - 1][w + 1].rotate(); /* placing last piece at max coord */
			Iterator<JSONArray> iterator = y.iterator(); /* on créer un itérateur pour y */
			int i = 0;
			int j = 0;
			while (iterator.hasNext()) { /* on parcourt le tableau en récuperant chaque JOBject piece */
				JSONArray x = iterator.next();
				Iterator<JSONObject> iteratorX = x.iterator();
				while (iteratorX.hasNext()) {
					JSONObject p = iteratorX.next();
					String type = (String) p.get("TYPE");
					if(!type.equals("NONE")) { /* puis si c'est une pièce non nul on l'initalise */
						int rotation = Math.toIntExact((long) p.get("ROTATION"));
						pieces[j][i] = getPiece(type,rotation);
						if((boolean) p.get("FULL")) pieces[j][i].setFull(true);
					}
					i++;
				}
				i=0;
				j++;
			}
			System.out.println("Niveau chargé.");
		} catch (IOException | ParseException e) {
			System.out.println("Impossible de charger le niveau");
		}
		WIDTH = w;
		HEIGHT = h;
		ID = 0; /* to change with file storage */
	}

	Piece getPiece(String s, int i) {
		Piece pc;
		switch (s) {
		case ("I"):
			pc = new PieceI();
			break;
		case ("T"):
			pc = new PieceT();
			break;
		case ("L"):
			pc = new PieceL();
			break;
		default:
			pc = new PieceX();
			break;
		}
		for (int j = 0; j < i; j++) {
			pc.rotate();
		}
		return pc;
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
	}

	void play() { /* basic method to play (very primitive, such basic) */
		Scanner sc = new Scanner(System.in);
		while (true) {
			affiche();
			getPiecePos();
			rotate(selected_y, selected_x);
			update(selected_y, selected_x);
			printBlocker();
		}
	}

	void update(int i, int j) { // primitive method to update piece, the
		if (isInTab(i + 1, j) && connected(pieces[i][j], pieces[i + 1][j], "DOWN")) { // if statements are good, gotta
																						// keep them
			if (isFull(i + 1, j)) { // but we should make it recursive
				setFull(i, j); // and check if its connected to the source
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
	}

	void setFull(int i, int j) { /* to set piece i,j full */
		pieces[i][j].setFull(true);
	}

	boolean isInTab(int i, int j) { /* check if the piece is in the array */
		return (i < HEIGHT && j <= WIDTH && i >= 0 && j > 0);
	}

	boolean isVerticalyOk(int i) {
		return (i < HEIGHT && i >= 0);
	}

	boolean isHorizontalyOk(int j) {
		return (j <= WIDTH && j > 0);
	}

	boolean isFull(int i, int j) { /* check if piece i,j is full */
		return pieces[i][j].isFull();
	}

	boolean connected(Piece p1, Piece p2,
			String direction) { /* check if 2 piece are connected given a certain position */
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

	void getPiecePos() {
		System.out.println("\nPlease select X position.");
		int scannerX = getInputInt();
		while (!isHorizontalyOk(scannerX)) {
			System.out.println("Position is invalid.");
			scannerX = getInputInt();
		}
		System.out.println("Please select Y position.");
		int scannerY = getInputInt();
		while (!isVerticalyOk(scannerY)) {
			System.out.println("Position is invalid.");
			scannerY = getInputInt();
		}
		if (isInTab(scannerY, scannerX) && pieces[scannerY][scannerX] != null) {
			System.out.println("Rotating piece [" + scannerX + ";" + scannerY + "]");
			selected_x = scannerX;
			selected_y = scannerY;
		} else {
			System.out.println("There is no piece here!");
			getPiecePos();
		}
	}

	int getInputInt() {
		Scanner scX = new Scanner(System.in);
		while (!scX.hasNextInt()) {
			System.out.println("This is not a number.");
			scX.next();
		}
		return scX.nextInt();
	}

	void printBlocker() {
		System.out.print("\n" + ANSI_BOLD + "#");
		for (int i = 0; i < WIDTH; i++)
			System.out.print("=");
		System.out.println("#" + ANSI_RESET + "\n");
	}

	void saveLevel() { /* pour sauvegarder le niveau */
		System.out.println("Sauvegarde du niveau...\n");
		JSONObject obj = new JSONObject();
		JSONArray y = new JSONArray(); /* on créer un array, qui sera l'array vertical (y) */
		for (int i = 0; i < HEIGHT; i++) {
			JSONArray x = new JSONArray(); /* ici on créer repetitivement des array horizontaux */
			for (int j = 0; j < WIDTH + 2; j++) {
				JSONObject p = new JSONObject(); /* on créer un JSONObject immitant une pièce, avec les arguments ci dessous */
				if (pieces[i][j] != null) {
					p.put("TYPE",pieces[i][j].getType());
					p.put("ROTATION", pieces[i][j].getRotation());
					if(pieces[i][j].isFull()) {
						p.put("FULL",true);
					}else {
						p.put("FULL",false);
					}
				} else {
					p.put("TYPE","NONE");
				}
				x.add(p);
			}
			y.add(x);
		}
		obj.put("ID", ID); /* puis on rajoute qques infos utiles */
		obj.put("HEIGHT", HEIGHT);
		obj.put("WIDTH", WIDTH);
		obj.put("Pieces", y);
		try {
			FileWriter file = new FileWriter("levels/level" + ID + ".json"); /* enfin ici, on sauvegarde le fichier */
			file.write(obj.toString());
			System.out.println("Niveau sauvegardé.");
			file.close();
		} catch (IOException e) {
			System.out.println("Impossible de sauvegarder le niveau.");
		}

	}
}
