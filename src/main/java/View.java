import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;

public class View extends Application{

    /* On definit la taille */
     static final int WIDTH = 720;
     static final int HEIGHT = 720;

     /* utiliser le mode "piece"+piece+".obj" a afficher ! :) */
    static String piece = "I";

    static final double PIECE_SIZE = 2;

    /* ses nouveaux attributs pour afficher le level */
    static Level level;

    /* une matrice de piece3D (des groupes de mesh) */
    static Piece3D[][] models;

    /* une matrice de carrés bleus representant l'eau */
    waterPiece[][] waterPieces;

    /* La pile des ajouts de waterTile */
    ArrayList<Coordonnes> pile = new ArrayList<Coordonnes>();

    /* Créé un aperçu de piece */
    public View(Level level){
       super();
       this.level = level;
       launch();
    }

    public View(){
        super();
    }



    @Override
    public void start(Stage stage){

        level.setOverviewer(this);

        /* On créer une caméra qui pointe vers 0,0 (true) et la recule sur l'axe Z */
        PerspectiveCamera camera  = new PerspectiveCamera(true);

        /* on appelle l'initalisateur de caméra */
        initalizeCamera(camera);

        /* on définit sa distance de rendu */
        camera.setFarClip(10000);

        /* On importe le model de la piece */
        Group root = new Group();

        /* On initialise nos deux tableaus */
        /* waterPieces représente les carrés d'eau */
        waterPieces = new waterPiece[level.pieces.length][level.pieces[0].length];
        /* models les modèles de pièces */
        models = new Piece3D[level.pieces.length][level.pieces[0].length];

        /*On appelle l'initalisateur de ces tableaux */
        initalizeBoards(root);

        /* La scene, avec root, la taille et le depthbuffered activée */
        /* le depthbuffer permet de ne pas avoir toutes les faces de pièces visible en même temps */
        Scene scene = new Scene(root,WIDTH,HEIGHT,true, SceneAntialiasing.BALANCED);

        /* On lui lie la caméra */
        scene.setCamera(camera);
        
        /* On gère maintenant les cliques de souris */
        scene.setOnMousePressed((MouseEvent me) -> {

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

        /* On nome la fenetre, y ajoute la scene et on l'affiche */
        stage.setTitle("A Q U A V I A S");
        stage.setScene(scene);
        stage.show();

        /* puis on démarre l'ecoulement de l'eau */
        start_water();
    }

    void initalizeCamera(Camera camera){
        /* Encore à modifier, il ne s'adapte pas bien à tout types de niveaux */
    	//On place l'origine x à la moitié de la largeur (qui est en fait la hauteur) du niveau
    	//On ajoute 1 car l'origine est initialisé à 0
    	double rotation=-35;
    	double diagonale=Math.sqrt(level.HEIGHT*level.HEIGHT+level.WIDTH*level.WIDTH);
    	
    	camera.setTranslateX(PIECE_SIZE*level.HEIGHT/2);
        camera.setTranslateY(-PIECE_SIZE/2);
        //camera.setTranslateZ(-(level.HEIGHT * PIECE_SIZE *2));
    	camera.setTranslateZ(-(level.HEIGHT+level.WIDTH+2)*2/2);
        
    	
        camera.getTransforms().add(new Rotate(-55, Rotate.X_AXIS));
        camera.getTransforms().add(new Rotate(rotation, Rotate.Y_AXIS));
        camera.getTransforms().add(new Rotate(rotation, Rotate.Z_AXIS));
        
        /*
        camera.setLayoutX(-rotation-level.WIDTH);
        camera.setLayoutY(rotation+level.HEIGHT/2);*/
        System.out.println(level.HEIGHT*1.3+level.WIDTH*1.7);
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

    void rotate(int x,int y){
        /* Si c'est la première piece, on ne peut pas la tourner */
        if(x == 0 && y == 0) return;
        /* on rotate le jeu, les pièces, et les pièces d'eau */
        /* on commence par la tourner dans le modèle */
        level.new_rotate(x,y);
        /* puis dans la vue */
        /* sur les modèles */
        models[x][y].getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
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
        if (!pile.isEmpty()) {
            flow(pile.get(0).getI(), pile.get(0).getJ());
        } else {
            flow(0, 0);
        }
        /* on update dans le modèle */
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
}
