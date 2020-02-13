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
	public static final String ANSI_SELECTED = "\u001b[48;5;240m";
	public int ID;
	public final int WIDTH;
	public final int HEIGHT;
	int selected_x;
	int selected_y;
	private char type;
	int compteur;

<<<<<<< src/main/java/Level.java
        /*
         * Cette fonction créée seulement un tableau vide, avec une entrée et une sortie
         */
        t = Character.toLowerCase(t);
        if (t == 'f' || t == 'c' || t == 'n') type = t;
        else type = 'n';
        compteur = compt;
        WIDTH = w;
        HEIGHT = h;

        setTab(w, h);
        /*
         * On ne créer pas d'ID ici, l'id est créé seulement au moment de la sauvegarde
         * d'un niveau
         */
    }

    @SuppressWarnings("unchecked")
    public Level(int id) throws Exception {
        System.out.println("Chargement du niveau...\n");
        FileReader reader;

        /* on récupère le fichier contenant le lvl */

        reader = new FileReader("levels/level" + id + ".json");

        /* on le parse */

        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(reader);

        /* on récupère les données de la taille et de l'id, et les initialise */

        int w = Math.toIntExact((long) obj.get("WIDTH"));
        int h = Math.toIntExact((long) obj.get("HEIGHT"));
        ID = Math.toIntExact((long) obj.get("ID"));
        WIDTH = w;
        HEIGHT = w;
        setTab(w, h);

        /* on récupère l'array Y (vertical) contenant les array X (horizontaux) */

        JSONArray y = (JSONArray) obj
                .get("Pieces");

        /* on créer un itérateur pour y */

        Iterator<JSONArray> iterator = y.iterator();

        /* et des indexs */

        int i = 0;
        int j = 0;

        /* on parcourt le tableau en récuperant chaque JSONObject piece */

        while (iterator.hasNext()) {
            JSONArray x = iterator.next();
            Iterator<JSONObject> iteratorX = x.iterator();

            /* Ici on passe aux sous tableaux */

            while (iteratorX.hasNext()) {
                JSONObject p = iteratorX.next();

                /* On vérifie si le type de la pièce, pour voir si elle est null ou non */

                String type = (String) p.get("TYPE");

                /* puis si c'est une pièce non nul on l'initalise */

                if (!type.equals("NONE")) {
                    int rotation = Math.toIntExact((long) p.get("ROTATION"));
                    pieces[j][i] = getPiece(type, rotation);
                    if ((boolean) p.get("FULL"))
                        pieces[j][i].setFull(true);
                }
                i++;
            }
            i = 0;
            j++;
        }

        /* Tout s'est bien déroulé */

        System.out.println("Niveau chargé.");
    }

    void setTab(int w, int h) {
        pieces = new Piece[h][w + 2]; /* first column and last column are empty and only contains start and end */
        pieces[0][0] = new PieceI(); /* placing first piece at 0,0 */
        pieces[0][0].rotate();
        pieces[0][0].setFull(true);
        pieces[h - 1][w + 1] = new PieceI();
        pieces[h - 1][w + 1].rotate(); /* placing last piece at max coord */
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
    System.out.println("----- AQUAVIAS -----");
        if (type == 'c' && compteur > 10) System.out.println("---- Coups restants : " + compteur + " ----");
        else if (type == 'c' && compteur > 1) System.out.println("---- Plus que " + compteur + " coups ! ----");
        else if (type == 'c' && compteur == 1) System.out.println("----  C'est votre denier coup ! ----");
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
        update();
    }

    void play() { /* basic method to play (very primitive, such basic) */
        Scanner sc = new Scanner(System.in);
        while (true) {
            affiche();
            getPiecePos();
            rotate(selected_x, selected_y);
            update();
            System.out.println();
        }
    }

    void update() {
        //vide d'abord entièrement l'eau du circuit
        //puis appelle update dès la source
        if(type == 'c') {
            if(!Victory() && compteur > 0) {
                voidAll();
                update(0, 0);
            }
        } else {
            if(!Victory){
                voidAll();
                update(0, 0);
            }
        }
    }

    private void voidAll() {    //vide l'eau de tout le circuit sauf de la source
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 1; j < WIDTH; j++) {
                if (pieces[i][j] != null)
                    pieces[i][j].setFull(false);
            }
        }
    }

    void update(int i, int j) {
        //vérifie que les pièces limitrophes existent, qu'elles sont connectées à l'actuelle et qu'elles ne sont pas déjà remplies
        if (isInTab(i + 1, j) && connected(pieces[i][j], pieces[i + 1][j], "DOWN") && !pieces[i + 1][j].isFull()) {
            setFull(i + 1, j);
            update(i + 1, j);
        }
        if (isInTab(i - 1, j) && connected(pieces[i][j], pieces[i - 1][j], "UP") && !pieces[i - 1][j].isFull()) {
            setFull(i - 1, j);
            update(i - 1, j);
        }
        if (isInTab(i, j + 1) && connected(pieces[i][j], pieces[i][j + 1], "RIGHT") && !pieces[i][j + 1].isFull()) {
            setFull(i, j + 1);
            update(i, j + 1);

        }
        if (isInTab(i, j - 1) && connected(pieces[i][j], pieces[i][j - 1], "LEFT") && !pieces[i][j - 1].isFull()) {
            setFull(i, j - 1);
            update(i, j - 1);
        }

        if (type == 'c') {
            if (DefeatCount()) System.out.println("Vous avez perdu...");
            else compteur--;
        } else if (type == 'f') {
            if (isLeaking()) System.out.println("Attention ! Il y a une fuite !");
        }

        if (Victory())
            System.out.print("Victoire !");

    }

    boolean Victory() {
        if(type == 'n') return (pieces[HEIGHT - 1][WIDTH + 1].isFull());
        if(type == 'f') return (pieces[HEIGHT - 1][WIDTH + 1].isFull()) && !isLeaking();
        if(type == 'c') return (pieces[HEIGHT - 1][WIDTH + 1].isFull() && compteur > 0);
        return false;
    }

    boolean DefeatCount() {
        return compteur == 0;
    }


    boolean isLeaking() {
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                if (pieces[i][j] != null) {
                    if (pieces[i][j].isFull()) { //If the piece contains water
                        if (pieces[i][j].isDown()) { //If the piece has an exit facing down
                            //If there is nothing below, it is leaking
                            if (i == pieces.length - 1) {
                                System.out.println("La pièce aux coordonnées (" + i + ", " + j + ") fuit.");
                            } else {
                                if (pieces[i + 1][j] != null) { //If there is a piece below
                                    if (!connected(pieces[i][j], pieces[i + 1][j], "DOWN")) { //If the two pieces don't connect
                                        System.out.println("La pièce aux coordonnées (" + i + ", " + j + ") fuit.");
                                        return true; //It is leaking
                                    }
                                }
                            }
                            return true; //If the piece is in the last line of the array/level
                        } else if (pieces[i][j].isLeft() && i != 0 && j != 0) { //If the piece has an exit facing left
                            if (j == 1) {
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

    void setFull(int i, int j) { /* to set piece i,j full */
        pieces[i][j].setFull(true);
    }

    boolean isInTab(int i, int j) { /* check if the piece is in the array */
        return (i < HEIGHT && j <= WIDTH + 2 && i >= 0 && j > 0);
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

    @SuppressWarnings("unchecked")
    void saveLevel() { /* pour sauvegarder le niveau */

        System.out.println("Sauvegarde du niveau...\n");
        JSONObject obj = new JSONObject();

        /* on créer un array, qui sera l'array vertical (y) */

        JSONArray y = new JSONArray();
        for (int i = 0; i < HEIGHT; i++) {

            /* ici on créer repetitivement des array horizontaux */

            JSONArray x = new JSONArray();
            for (int j = 0; j < WIDTH + 2; j++) {

                /* On créer un JSONObject contenant les attributs d'une pièce */
                /* son type, sa rotation et si elle est pleine ou non */

                JSONObject p = new JSONObject();
                if (pieces[i][j] != null) {
                    p.put("TYPE", pieces[i][j].getType());
                    p.put("ROTATION", pieces[i][j].getRotation());
                    if (pieces[i][j].isFull()) {
                        p.put("FULL", true);
                    } else {
                        p.put("FULL", false);
                    }
                } else {
                    p.put("TYPE", "NONE");
                }
                x.add(p);
            }
            y.add(x);
        }

        /* puis on rajoute qques infos utiles */

        obj.put("HEIGHT", HEIGHT);
        obj.put("WIDTH", WIDTH);
        obj.put("Pieces", y);
        try {

            /* on récupère l'id dans id.json, puis on l'incrémente de 1 */

            FileReader reader = new FileReader("levels/id.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject JSONId = (JSONObject) jsonParser.parse(reader); /* on le parse */
            int id = Math.toIntExact((long) JSONId.get("ID"));
            ID = id;

            /* on rajoute l'id dans le json du niveau */

            obj.put("ID", ID);

            /* l'id est stocké, on l'incrémente */

            JSONObject JSONnewId = new JSONObject();
            JSONnewId.put("ID", id + 1);
            FileWriter IDfile = new FileWriter("levels/id.json"); /* enfin ici, on sauvegarde le fichier */
            IDfile.write(JSONnewId.toString());
            IDfile.close();

            /* l'id a été incrementé, on sauvegarde maintenant le niveau avec l'ancien id */

            FileWriter file = new FileWriter("levels/level" + id + ".json"); /* enfin ici, on sauvegarde le fichier */
            file.write(obj.toString());
            System.out.println("Niveau sauvegardé.");
            file.close();
        } catch (IOException | ParseException e) {
            System.out.println("Impossible de sauvegarder le niveau.");
        }

    }
=======
	public Level(int w, int h) {

		/*
		 * Cette fonction créée seulement un tableau vide, avec une entrée et une sortie
		 */

		WIDTH = w;
		HEIGHT = h;

		setTab(w, h);

		/*
		 * On ne créer pas d'ID ici, l'id est créé seulement au moment de la sauvegarde
		 * d'un niveau
		 */
		System.out.println("Level created");
	}

	@SuppressWarnings("unchecked")
	public Level(int id) throws Exception {

		System.out.println("Chargement du niveau...\n");
		FileReader reader;

		/* on récupère le fichier contenant le lvl */

		reader = new FileReader("levels/level" + id + ".json");

		/* on le parse */

		JSONParser jsonParser = new JSONParser();
		JSONObject obj = (JSONObject) jsonParser.parse(reader);

		/* on récupère les données de la taille et de l'id, et les initialise */

		int w = Math.toIntExact((long) obj.get("WIDTH"));
		int h = Math.toIntExact((long) obj.get("HEIGHT"));
		ID = Math.toIntExact((long) obj.get("ID"));
		WIDTH = w;
		HEIGHT = h;
		setTab(w, h);

		/* on récupère l'array Y (vertical) contenant les array X (horizontaux) */

		JSONArray y = (JSONArray) obj.get("Pieces");

		/* on créer un itérateur pour y */

		Iterator<JSONArray> iterator = y.iterator();

		/* et des indexs */

		int i = 0;
		int j = 0;

		/* on parcourt le tableau en récuperant chaque JSONObject piece */

		while (iterator.hasNext()) {
			JSONArray x = iterator.next();
			Iterator<JSONObject> iteratorX = x.iterator();

			/* Ici on passe aux sous tableaux */

			while (iteratorX.hasNext()) {
				JSONObject p = iteratorX.next();

				/* On vérifie si le type de la pièce, pour voir si elle est null ou non */

				String type = (String) p.get("TYPE");

				/* puis si c'est une pièce non nul on l'initalise */

				if (!type.equals("NONE")) {
					int rotation = Math.toIntExact((long) p.get("ROTATION"));
					pieces[j][i] = getPiece(type, rotation);
					if ((boolean) p.get("FULL"))
						pieces[j][i].setFull(true);
				}
				i++;
			}
			i = 0;
			j++;
		}

		/* Tout s'est bien déroulé */

		System.out.println("Niveau chargé.");
	}

	void setTab(int w, int h) {
		pieces = new Piece[h][w + 2]; /*
										 * La première et la dernière colonne sont presque vides, elles ne contiennent
										 * que les pièces de début et de fin
										 */
		pieces[0][0] = new PieceI(); /* On place la première pièce à 0, 0 */
		pieces[0][0].rotate();
		pieces[0][0].setFull(true);
		pieces[h - 1][w + 1] = new PieceI();
		pieces[h - 1][w + 1].rotate(); /* Et la dernière pièce au dernier indice du tableau */
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

	void affiche() { /* Affiche l'état du jeu */
		System.out.print("  ");
		for (int i=0;i<WIDTH+2;i++) {
			System.out.print(i);
		}
		System.out.println();
		for (int i = 0; i < pieces.length; i++) {
			System.out.print(i+" ");
			for (int j = 0; j < pieces[i].length; j++) {
				if (pieces[i][j] != null) {
					if (i == selected_y && j == selected_x)
						System.out.print(ANSI_SELECTED);
					if (pieces[i][j].isFull())
						System.out.print(ANSI_BLUE); /* Si la pièce contient de l'eau elle s'affiche en bleu */
					System.out.print(pieces[i][j].toString());
					if (pieces[i][j].isFull() || (i == selected_y && j == selected_x))
						System.out.print(ANSI_RESET); /* Et on arrête le bleu */
				} else {
					if (i == selected_y && j == selected_x)
						System.out.print(ANSI_SELECTED);
					System.out.print(" ");
					if (i == selected_y && j == selected_x)
						System.out.print(ANSI_RESET);
				}
			}
			System.out.println();
		}
	}

	void rotate(int i, int j) { /*
								 * Fait tourner la pièces de coordonnées "i" et "j" mais reset l'eau qu'elle
								 * contient avant
								 */
		if (i < pieces.length && j < pieces[i].length && pieces[i][j] != null) {
			pieces[i][j].setFull(false);
			pieces[i][j].rotate();
		}
	}

	void play() { /* Méthod basique pour jouer (very primitive, such basic) */
		while (!Victory) {
			update();
			affiche();
			getPiecePos();
			rotate(selected_y, selected_x);
			System.out.println();
		}
	}
	
	void update() {
		// vide d'abord entièrement l'eau du circuit
		// puis appelle update dès la source
		voidAll();
		update(0,0);
	}
	
	private void voidAll() {	//vide l'eau de tout le circuit sauf de la source
		for (int i=0;i<HEIGHT;i++) {
			for(int j=1;j<WIDTH+2;j++) {
				if(pieces[i][j]!=null)
					pieces[i][j].setFull(false);
			}
		}
	}

	void update(int i, int j) {
		//vérifie que les pièces limitrophes existent, qu'elles sont connectées à l'actuelle et qu'elles ne sont pas déjà remplies
		if(i==HEIGHT-1 && j==WIDTH+1) return;
		if (isInTab(i + 1, j) && connected(pieces[i][j], pieces[i + 1][j], "DOWN")&&!pieces[i + 1][j].isFull()) { 
			setFull(i+1, j);
			update(i+1,j);
		}
		if (isInTab(i - 1, j) && connected(pieces[i][j], pieces[i - 1][j], "UP") && !pieces[i - 1][j].isFull()) {
			setFull(i - 1, j);
			update(i - 1, j);
		}
		if (isInTab(i, j + 1) && connected(pieces[i][j], pieces[i][j + 1], "RIGHT") && !pieces[i][j + 1].isFull()) {
			setFull(i, j + 1);
			update(i, j + 1);

		}
		if (isInTab(i, j - 1) && connected(pieces[i][j], pieces[i][j - 1], "LEFT") && !pieces[i][j - 1].isFull()) {
			setFull(i, j - 1);
			update(i, j - 1);
		}
	}

	void setFull(int i, int j) { /* Pour remplir la pièce de coordonnées i et j */
		pieces[i][j].setFull(true);
	}

	boolean isInTab(int i, int j) { /* Vérifie que la pièce de coordonnées i et j est dans el tableau */
		return (i < HEIGHT && j < WIDTH + 2 && i >= 0 && j > 0);
	}

	boolean isVerticalyOk(int i) {
		return (i < HEIGHT && i >= 0);
	}

	boolean isHorizontalyOk(int j) {
		return (j <= WIDTH && j > 0);
	}

	boolean isFull(int i, int j) { /* Vérifie que le pièce de coordonnées i et j est pleine */
		return pieces[i][j].isFull();
	}

	boolean connected(Piece p1, Piece p2,
			String direction) { /* Vérifie le connection des pièces étant donnée une direction */
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

	@SuppressWarnings("unchecked")
	void saveLevel() { /* pour sauvegarder le niveau */

		System.out.println("Sauvegarde du niveau...\n");
		JSONObject obj = new JSONObject();

		/* on créer un array, qui sera l'array vertical (y) */

		JSONArray y = new JSONArray();
		for (int i = 0; i < HEIGHT; i++) {

			/* ici on créer repetitivement des array horizontaux */

			JSONArray x = new JSONArray();
			for (int j = 0; j < WIDTH + 2; j++) {

				/* On créer un JSONObject contenant les attributs d'une pièce */
				/* son type, sa rotation et si elle est pleine ou non */

				JSONObject p = new JSONObject();
				if (pieces[i][j] != null) {
					p.put("TYPE", pieces[i][j].getType());
					p.put("ROTATION", pieces[i][j].getRotation());
					if (pieces[i][j].isFull()) {
						p.put("FULL", true);
					} else {
						p.put("FULL", false);
					}
				} else {
					p.put("TYPE", "NONE");
				}
				x.add(p);
			}
			y.add(x);
		}

		/* puis on rajoute qques infos utiles */

		obj.put("HEIGHT", HEIGHT);
		obj.put("WIDTH", WIDTH);
		obj.put("Pieces", y);
		try {

			/* on récupère l'id dans id.json, puis on l'incrémente de 1 */

			FileReader reader = new FileReader("levels/id.json");
			JSONParser jsonParser = new JSONParser();
			JSONObject JSONId = (JSONObject) jsonParser.parse(reader); /* on le parse */
			int id = Math.toIntExact((long) JSONId.get("ID"));
			ID = id;

			/* on rajoute l'id dans le json du niveau */

			obj.put("ID", ID);

			/* l'id est stocké, on l'incrémente */

			JSONObject JSONnewId = new JSONObject();
			JSONnewId.put("ID", id + 1);
			FileWriter IDfile = new FileWriter("levels/id.json"); /* enfin ici, on sauvegarde le fichier */
			IDfile.write(JSONnewId.toString());
			IDfile.close();

			/* l'id a été incrementé, on sauvegarde maintenant le niveau avec l'ancien id */

			FileWriter file = new FileWriter("levels/level" + id + ".json"); /* enfin ici, on sauvegarde le fichier */
			file.write(obj.toString());
			System.out.println("Niveau sauvegardé.");
			file.close();
		} catch (IOException | ParseException e) {
			System.out.println(e+"Impossible de sauvegarder le niveau.");
		}
	}

	void movePointer(String dir) {
		int x = selected_x;
		int y = selected_y;
		switch (dir) {
		case "UP":
			y--;
			break;
		case "DOWN":
			y++;
			break;
		case "RIGHT":
			x++;
			break;
		case "LEFT":
			x--;
			break;
		}
		if (isInTab(y, x)) {
			selected_x = x;
			selected_y = y;
			affiche();
		}
	}

	void rotatePointer() {
		if (pieces[selected_y][selected_x] != null) {
			rotate(selected_y, selected_x);
			update();
			affiche();
		}
	}
>>>>>>> src/main/java/Level.java
}
