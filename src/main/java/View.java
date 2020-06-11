import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class View extends Scene{

    /* On definit la taille */
     static final int WIDTH = 720;
     static final int HEIGHT = 720;

    static final double PIECE_SIZE = 2;

    static int rotateTime = 200;

    AnchorPane globalRoot;

    /* ses nouveaux attributs pour afficher le level */
    static Level level;

    /* une matrice de piece3D (des groupes de mesh) */
    static Piece3D[][] models;

    static Text compteur;

    /* une matrice de carrés bleus representant l'eau */
    waterPiece[][] waterPieces;

    /* La pile des ajouts de waterTile */
    ArrayList<Coordonnes> pile = new ArrayList<Coordonnes>();

    /*Panneau de fin de niveau*/
    static LevelEnd fin;

    /* Créé un aperçu de piece */
    public View(Level level){
       super(new Group(), 1280, 720, true);
       level.new_update();
       setUp(level);
    }

    public View() {
        super(new Group(), 1280, 720, true);
    }

    public void setUp(Level level){

        this.level = level;

        level.setOverviewer(this);

        globalRoot = new AnchorPane();

        StackPane stack = new StackPane();

        initalizeCounter(stack);

        globalRoot.getChildren().add(stack);

        //Scene scene = new Scene(globalRoot, 1280,720,true);
        this.setRoot(globalRoot);

        /* On créer une caméra qui pointe vers 0,0 (true) et la recule sur l'axe Z */
        PerspectiveCamera camera  = new PerspectiveCamera(true);

        /* on appelle l'initalisateur de caméra */
        initalizeCamera(camera);

        /* on définit sa distance de rendu */
        camera.setFarClip(1000);

        /* On importe le model de la piece */
        Group root3D = new Group();

        /* On initialise nos deux tableaus */
        /* waterPieces représente les carrés d'eau */
        waterPieces = new waterPiece[level.pieces.length][level.pieces[0].length];
        /* models les modèles de pièces */
        models = new Piece3D[level.pieces.length][level.pieces[0].length];

        /*On appelle l'initalisateur de ces tableaux */
        initalizeBoards(root3D);

        /* La scene, avec root, la taille et le depthbuffered activée */
        /* le depthbuffer permet de ne pas avoir toutes les faces de pièces visible en même temps */
        SubScene subScene = new SubScene(root3D, 1280, 720, true, SceneAntialiasing.BALANCED);

        /* On lui lie la caméra */
        subScene.setCamera(camera);

        globalRoot.getChildren().add(subScene);

        /* On gère maintenant les cliques de souris */
        subScene.setOnMousePressed((MouseEvent me) -> {

            /* on récupère le résultat du click */
            PickResult pr = me.getPickResult();

            /* Si il est non nul alors */
            if(pr!=null && pr.getIntersectedNode() != null){

                if(pr.getIntersectedNode() instanceof waterPiece.waterTile){
                    /*
                        On a cliqué sur l'eau
                        une waterTile possède des coordonées, utilisons les !
                     */
                    waterPiece.waterTile p = (waterPiece.waterTile) pr.getIntersectedNode();
                    rotate(p.getX(),p.getY());
                }else {
                    /* Sinon, on recupère le Node associé */
                    Node mv = (MeshView) pr.getIntersectedNode();

                    /* puis on récupère ses coordonnées sur l'axe x et y */
                /*
                    (On récupère les coordonnées dans le plan, auxquels on ajoute la taille de la piece / 2 (la moitiée
                    de pièce qui "dépasse" au début, et qu'on divise par la taille de la pièce pour obtenir
                    leurs coordonnées sur le tableau level
                 */
                    int x_coord = (int) Math.round((mv.localToScene(mv.getBoundsInLocal()).getMinX() + PIECE_SIZE / 2) / PIECE_SIZE);
                    int y_coord = (int) Math.round((mv.localToScene(mv.getBoundsInLocal()).getMinZ() + PIECE_SIZE / 2) / PIECE_SIZE);

                    /* on rotate le tout */
                    rotate(x_coord, y_coord);
                }
            }
        });

        /* puis on démarre l'ecoulement de l'eau */
        start_water();
    }

    void initalizeCamera(Camera camera){
    	/* Encore à modifier, il ne s'adapte pas bien à tout types de niveaux */
    	//On place l'origine x à la moitié de la largeur (qui est en fait la hauteur) du niveau
    	//On ajoute 1 car l'origine est initialisé à 0
    	
    	camera.setTranslateX(PIECE_SIZE*level.HEIGHT/2);
        camera.setTranslateY(-PIECE_SIZE/2);
        //camera.setTranslateZ(-(level.HEIGHT * PIECE_SIZE *2));
    	camera.setTranslateZ(-(level.HEIGHT+level.WIDTH+2)*2/2);
        
    	
        camera.getTransforms().add(new Rotate(-55, Rotate.X_AXIS));
        camera.getTransforms().add(new Rotate(-35, Rotate.Y_AXIS));
        camera.getTransforms().add(new Rotate(-35, Rotate.Z_AXIS));
        
        /*
        camera.setLayoutX(-rotation-level.WIDTH);
        camera.setLayoutY(rotation+level.HEIGHT/2);*/
        camera.setLayoutX((level.HEIGHT+level.WIDTH)*38/18.9);
        camera.setLayoutY((level.HEIGHT+level.WIDTH)*-43/18.9);

    }

    void initalizeBoards(Group root){
        /* On recopie à l'identique le niveau en 3d */
        /* on parcours le tableau */
        for(int i = 0 ; i < models.length ; i++) {
            for(int j = 0 ; j < models[i].length ; j++) {
                if(level.pieces[i][j] == null) continue;
                /* chaque modèle une pièce 3D */
                models[i][j] = new Piece3D();
                /* On importe selon le type de pièce */
                models[i][j].importModel("model_test/piece"+level.pieces[i][j].getType()+".obj");
                /* on place la pièce en coordonnées [i;j] (décale de la taille d'une pièce) */
                models[i][j].setTranslateX(PIECE_SIZE * i);
                models[i][j].setTranslateY(0);
                models[i][j].setTranslateZ(PIECE_SIZE * j);

                /* on créé le type de pièce correspondant */
                switch(level.pieces[i][j].getType()){
                    case("L") : waterPieces[i][j] = new waterPieceL(PIECE_SIZE/2,this,i,j); break;
                    case("T") : waterPieces[i][j] = new waterPieceT(PIECE_SIZE/2,this,i,j); break;
                    case("I") : waterPieces[i][j] = new waterPieceI(PIECE_SIZE/2,this,i,j); break;
                    case("X") : waterPieces[i][j] = new waterPieceX(PIECE_SIZE/2,this,i,j); break;
                }
                /* on place l'eau au même coordonnées que les modèles */
                waterPieces[i][j].setTranslateX(PIECE_SIZE * i);

                /* arbitraire, dépend du type de pièce, règle la hauteur de l'eau */
                waterPieces[i][j].setTranslateY(-1.2);
                waterPieces[i][j].setTranslateZ((PIECE_SIZE * j));

                /* si la pièce n'est pas vide, on la vide d'eau dans la vue */
                if(!level.pieces[i][j].isFull()) waterPieces[i][j].setFull(false);

                /* on les tournes comme il se doit :) */
                for(int r = 0 ; r < level.pieces[i][j].getRotation() ; r++){
                    waterPieces[i][j].rotate();
                    models[i][j].getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
                }
                /*
                    PROBLEME LORS DE LA CREATION DE PIECE L
                    ELLE DOIT ETRE TOURNE 2* DANS LA VUE, POUR ETRE EN ACCORD AVEC LE JEU
                    CE N'EST QUE TEMPORAIRE
                 */
                if(level.pieces[i][j].getType() == "L"){
                    for(int r = 0 ; r < 3 ; r++)  models[i][j].getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
                }
                /* puis on les ajoutes a root */
                root.getChildren().add(waterPieces[i][j]);
                root.getChildren().add(models[i][j]);
            }
        }
    }

    void initalizeCounter(StackPane stack){
        Text compteur = new Text(level.compteurToString());

        compteur.setFill(new Color(0,0,0,.6));

        this.compteur = compteur;

        Rectangle r = new Rectangle();

        r.setEffect(new DropShadow());

        stack.getChildren().addAll(r, compteur);

        r.setWidth(150);
        r.setHeight(50);

        r.setFill(new Color(0,0,0,.05));
    }

    void rotate(int x,int y){
        //Si la partie est finie, la rotation ne fonctionne plus
        if(level.estFinie(false)) {
            if(level.Victory()) fin = new LevelEnd(WIDTH, 'v');
            else fin = new LevelEnd(WIDTH, 'd');

            globalRoot.getChildren().add(fin);

            return;
        }

        /* Si la rotation n'est pas finie, on peut pas en commencer une autre */
        if(models[x][y].getRotate() % 90 != 0) return;

        /* Si c'est la première piece, on ne peut pas la tourner */
        if(x == 0 && y == 0) return;

        /* on rotate le jeu, les pièces, et les pièces d'eau */
        /* on commence par la tourner dans le modèle */
        level.new_rotate(x,y);

        /* puis dans la vue */
        /* sur les modèles */
        RotateTransition rt= new RotateTransition(Duration.millis(rotateTime), models[x][y]);
        rt.setAxis(Rotate.Y_AXIS);
        rt.setByAngle(90);
        rt.setCycleCount(1);
        rt.setAutoReverse(true);
        rt.play();

        /* puis les pieces d'eau */
        waterPieces[x][y].rotate();
        /*
            on va ensuite vider chaque pièce pleine ajouté (durant la
            propagation de l'eau) APRES la pièce que l'ont vient
            de tourner
         */
        if(pileContains(x,y)) {
            while (!pile.isEmpty() && (pile.get(0).getI() != x || pile.get(0).getJ() != y)) {
                waterPieces[pile.get(0).getI()][pile.get(0).getJ()].setFull(false);
                waterPieces[pile.get(0).getI()][pile.get(0).getJ()].flowing = false;
                pile.remove(0);
            }
            /* si la pile n'est pas vide, on enlève aussi la piece qu'on vient de tourner */
            if (!pile.isEmpty()) {
                waterPieces[pile.get(0).getI()][pile.get(0).getJ()].setFull(false);
                waterPieces[pile.get(0).getI()][pile.get(0).getJ()].flowing = false;
                pile.remove(0);
            }
        }
        /*
            Si la pile n'est pas vide, on part de la pièce au sommet de la pile
            Sinon on repart de la première piece
         */
        /* On attend la fin de l'animation avant de relancer la fonction d'écoulement */
        if (!pile.isEmpty()) {
            Timeline wait = new Timeline(new KeyFrame(Duration.millis(rotateTime * 2), event ->{
                flow(pile.get(0).getI(), pile.get(0).getJ());
            }));
            wait.setCycleCount(1);
            wait.play();
        } else {
            Timeline wait = new Timeline(new KeyFrame(Duration.millis(rotateTime * 2), event ->{
                flow(0, 0);
            }));
            wait.setCycleCount(1);
            wait.play();
        }
        /* on update dans le modèle */

        compteur.setText(level.compteurToString());

        level.new_update();
        level.affiche();
    }


    void start_water(){
        /* on lance la fonction a la case 0 */
        waterPieces[0][0].flow(1,0);
    }

    /*
        droite : [1;0]
        gauche : [1;2]
        bas : [0;1]
        haut : [2;1]

     */

    /* sur la même base qu'update */
    void flow(int i,int j){
        if(!isWaterPieceFull(i,j)) return;
        /*
            Cette fonction marche de la manière suivante :
            - Elle regarde si elle est connectées aux pièces d'a côté (comme sur level)
            - Verifie sur dans le modèle elle est pleine (donc il faut la remplir dans la vue
            - Dis à la pièce d'effectuer sa fonction d'écoulement progressif
         */
        /* On ajoute chaque nouvel piece */
        if (level.isInTab(i + 1, j) && level.connected(level.pieces[i][j], level.pieces[i + 1][j], "DOWN") && !waterPieces[i + 1][j].isFull()){
            pile.add(0,new Coordonnes(i+1,j));
            waterPieces[i+1][j].flowing  = true;
            waterPieces[i+1][j].flow(0,1);
        }
        if (level.isInTab(i - 1, j) && level.connected(level.pieces[i][j], level.pieces[i - 1][j], "UP") && !waterPieces[i - 1][j].isFull()){
            pile.add(0,new Coordonnes(i-1,j));
            waterPieces[i-1][j].flowing = true;
            waterPieces[i-1][j].flow(2,1);
        }
        if (level.isInTab(i, j + 1) && level.connected(level.pieces[i][j], level.pieces[i][j + 1], "RIGHT") && !waterPieces[i][j + 1].isFull()){
            pile.add(0,new Coordonnes(i,j+1));
            waterPieces[i][j+1].flowing = true;
            waterPieces[i][j+1].flow(1,0);
        }
        if (level.isInTab(i, j-1) && level.connected(level.pieces[i][j], level.pieces[i][j - 1], "LEFT") && !waterPieces[i][j - 1].isFull()){
            pile.add(0,new Coordonnes(i,j-1));
            waterPieces[i][j-1].flowing = true;
            waterPieces[i][j-1].flow(1,2);
        }
    }

    /* Pour vérifier si la pile contient la pièce de Coordoonées x;y */
    boolean pileContains(int x, int y){
        for(Coordonnes c : pile){
            if(x == c.getI() && y == c.getJ()) return true;
        }
        return false;
    }

    boolean isLevelFull(int x, int y){
        return level.isFull(x,y);
    }

    boolean isWaterPieceFull(int x, int y){
        return waterPieces[x][y].isFull();
    }

    /* Rempli/Vide la pièce de coordoonées i;j */
    void setFull(int i, int j, boolean b){
        /* on l'active ou désactive */
        waterPieces[i][j].setFull(b);
    }

    /* Permet d'importer une pièce via une URL */

    private class Piece3D extends Group{

        public void importModel(String url){

            /* On utilise l'API d'import de modelobj pour javafx */
            /* on créer un objet vide */
            ObjModelImporter objModelImporter = new ObjModelImporter();

            /* et on utilise la fonction read sur l'url */
            objModelImporter.read(url);

            /* puis pour chaque meshview dans l'objModel, on l'ajoute a modelRoot (le groupe) */
            for(MeshView view : objModelImporter.getImport()){
                this.getChildren().addAll(view);
            }
        }
    }

    /* Couples de valeurs, représentant des coordonnées */
    private class Coordonnes{
        int i;
        int j;

        public Coordonnes(int i, int j){
            this.i = i;
            this.j = j;
        }

        int getI(){ return i;}
        int getJ(){ return j;}

        waterPiece getPiece(){ return waterPieces[i][j]; }
    }

    //Cette classe gère dans sa quasi totalité la séquence de fin de niveau
    static class LevelEnd extends Group {
        StackPane boite;
        Rectangle bluebox;

        public LevelEnd(double x, char g){ //Le constructeur a besoin de la longueur de la fenètre pour placer la boite
            //mais également d'une variable pour savoir si la partie est gagnée ou non

            Rectangle behind = new Rectangle(1280, 720);
            behind.setFill(Paint.valueOf("BLACK"));
            behind.setOpacity(0.6);
            behind.setX(0);
            behind.setY(0);

            getChildren().add(behind);

            boite = new StackPane();
            boite.setLayoutX(x/2);
            boite.setLayoutY(210);

            bluebox = new Rectangle(600, 300);

            bluebox.setX(x/2);
            bluebox.setY(210);

            bluebox.setArcHeight(15);
            bluebox.setArcWidth(15);

            bluebox.setFill(new ImagePattern(new Image(new File("img/pausemenu.png").toURI().toString())));
            bluebox.setStroke(Paint.valueOf("GREY"));

            boite.getChildren().add(bluebox);

            addButtons(g);

            getChildren().add(boite);
        }

        public void addButtons(char win){
            if (win == 'v'){
                HBox hboite = new HBox(10);
                Button suivant = new Button("Niveau suivant");
                suivant.setOnAction(e -> {
                    try {
                        Level next = new Level(level.ID + 1);
                    } catch (Exception exception) {
                        System.out.println("Pas de suite !! Vous avez fini le jeu bravo !");
                    }
                    
                });
                Button replay = new Button("Rejouer");
                Button quit = new Button("Retour au menu");

                hboite.setAlignment(Pos.CENTER);

                hboite.getChildren().addAll(suivant, replay, quit);

                boite.getChildren().add(hboite);
            } else {
                HBox hboite = new HBox(10);
                Button replay = new Button("Rejouer");
                Button quit = new Button("Retour au menu");

                hboite.getChildren().addAll(replay, quit);

                boite.getChildren().add(hboite);
            }
        }
    }
}
