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
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class PieceOverview extends Application{

    /* On definit la taille */
     static final int WIDTH = 720;
     static final int HEIGHT = 720;

     /* utiliser le mode "piece"+piece+".obj" a afficher ! :) */
    static String piece = "I";

    static final double PIECE_SIZE = 4;

    /* ses nouveaux attributs pour afficher le level */
    static Level level;

    /* une matrice de piece3D (des groupes de mesh) */
    static Piece3D[][] models;

    /* une matrice de carrés bleus representant l'eau */
    waterPiece[][] waterPieces;

    /* Créé un aperçu de piece */
    public PieceOverview(Level level){
       super();
       PieceOverview.level = level;
    }

    public PieceOverview(){
        super();
    }



    @Override
    public void start(Stage stage) {

        lastPlayed(level); //Sauvegarde le niveau que l'on charge en tant que "dernier niveau joué"

        level.setOverviewer(this);

        /* On créer une caméra qui pointe vers 0,0 (true) et la recule sur l'axe Z */
        PerspectiveCamera camera  = new PerspectiveCamera(true);

        /*camera.setTranslateZ(-(level.HEIGHT * PIECE_SIZE) * 1.3);
        System.out.println(level.HEIGHT * PIECE_SIZE);
        System.out.println(level.WIDTH * PIECE_SIZE);
        camera.setTranslateY(-((level.WIDTH * PIECE_SIZE + level.HEIGHT * PIECE_SIZE )));
        camera.setTranslateX(level.WIDTH* PIECE_SIZE * 1.5);*
         */
        camera.setTranslateX(level.HEIGHT * PIECE_SIZE + (level.HEIGHT * 1.5 * PIECE_SIZE));
        camera.setTranslateZ(-level.WIDTH/2.0 * PIECE_SIZE);
        camera.setTranslateY(-50);
        camera.getTransforms().add(new Rotate(-35, Rotate.X_AXIS));
        camera.getTransforms().add(new Rotate(-35, Rotate.Z_AXIS));
        camera.getTransforms().add(new Rotate(-45, Rotate.Y_AXIS));

        //camera.getTransforms().add(new Rotate(-45,Rotate.Y_AXIS));
        //camera.getTransforms().add(new Rotate(-45,Rotate.X_AXIS));

        /*camera.setTranslateZ(-70);
        camera.setTranslateY(-100);
        camera.setTranslateX(70);
        camera.getTransforms().add(new Rotate(-45,Rotate.Y_AXIS));
        camera.getTransforms().add(new Rotate(-45,Rotate.X_AXIS));*/

        camera.setFarClip(1000);

        /* On importe le model de la piece */
        Group root = new Group();

        /* On initialise nos deux tableaus */
        waterPieces = new waterPiece[level.pieces.length][level.pieces[0].length];
        models = new Piece3D[level.pieces.length][level.pieces[0].length];

        /* On recopie à l'identique le niveau en 3d */
        for(int i = 0 ; i < models.length ; i++) {
            for(int j = 0 ; j < models[i].length ; j++) {
                if(level.pieces[i][j] == null) continue;
                models[i][j] = new Piece3D();
                /* # A CHANGER QUAND ON AURA TOUT LES MODES # */
                models[i][j].importModel("pieces3D/pieceI.obj");
                /* on place la pièce */
                models[i][j].setTranslateX(PIECE_SIZE * i);
                models[i][j].setTranslateY(0);
                models[i][j].setTranslateZ((PIECE_SIZE * j));
                /* si la piece [i][j] est pleine, on lui affiche une waterTile */
                /* mais avec une visibilité a false */
                /* comme ça la rotation de la waterTile sera toujours actualisée */
                waterPieces[i][j] = new waterPiece(level.pieces[i][j].getType(), PIECE_SIZE/2, this, i , j);
                waterPieces[i][j].setTranslateX(PIECE_SIZE * i);
                waterPieces[i][j].setTranslateY(-1.2);
                waterPieces[i][j].setTranslateZ((PIECE_SIZE * j));
                /* debug */
                //models[i][j].setVisible(false);
                if(!level.pieces[i][j].isFull()) waterPieces[i][j].setFull(false);
                /* on les tournes comme il se doit :) */
                for(int r = 0 ; r < level.pieces[i][j].getRotation() ; r++){
                    waterPieces[i][j].rotate();
                    models[i][j].getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
                }
                /* puis on les ajoutes a root */
                root.getChildren().add(waterPieces[i][j]);
                root.getChildren().add(models[i][j]);
            }
        }

        /* La scene, avec root, la taille et le depthbuffered activée */
        Scene scene = new Scene(root,WIDTH,HEIGHT,true, SceneAntialiasing.BALANCED);
        /* On lui lie la caméra */
        scene.setCamera(camera);

        scene.setOnMousePressed((MouseEvent me) -> {
            /* on récupère le résultat du click */
            PickResult pr = me.getPickResult();
            if(pr!=null && pr.getIntersectedNode() != null){
                Node mv;
                if(pr.getIntersectedNode() instanceof MeshView) {
                    /* ses meshs aussi */
                    mv = (MeshView) pr.getIntersectedNode();
                }else{
                    /* si c'est une waterTile */
                    mv = (waterPiece.waterGrid) pr.getIntersectedNode();
                }
                /* puis on récupère ses coordonnées sur l'axe x et y */
                /*
                    (On récupère les coordonnées dans le plan, auxquels on ajoute la taille de la piece / 2 (la moitiée
                    de pièce qui "dépasse" au début, et qu'on divise par la taille de la pièce pour obtenir
                    leurs coordonnées sur le tableau level
                 */
                int x_coord = (int) Math.round((mv.localToScene(mv.getBoundsInLocal()).getMinX()+PIECE_SIZE/2)/PIECE_SIZE);
                int y_coord = (int) Math.round((mv.localToScene(mv.getBoundsInLocal()).getMinZ()+PIECE_SIZE/2)/PIECE_SIZE);
                /* on rotate le tout */
                rotate(x_coord,y_coord);
            }
        });

        /* On nome la fenetre, y ajoute la scene et on l'affiche */
        stage.setTitle("Aperçu de pièce");
        stage.setScene(scene);
        stage.show();
        start_water();
    }

    ArrayList<Coordonnes> pile = new ArrayList<>();

    void rotate(int x,int y){
        if(x == 0 && y == 0) return;
        for(Coordonnes c : pile){
            System.out.println(c.getI()+" "+c.getJ());
        }
        /* on rotate le jeu, les pièces, et les pièces d'eau */
        level.new_rotate(x,y);
        models[x][y].getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
        waterPieces[x][y].rotate();
        waterPieces[x][y].setFull(false);
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
        if (!pile.isEmpty()) {
            flow(pile.get(0).getI(), pile.get(0).getJ());
        } else {
            flow(0, 0);
        }
        /* on update */
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
        //System.out.print(i+" ; "+j);
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

    boolean pileContains(int x, int y){
        for(Coordonnes c : pile){
            if(x == c.getI() && y == c.getJ()) return true;
        }
        return false;
    }


    void setFull(int i, int j, boolean b){
        /* on l'active ou désactive */
        waterPieces[i][j].setFull(b);
    }

    /* Permet d'importer une pièce via une URL */

    private class Piece3D extends Group{

        public void importModel(String url){

            /* on créer une groupe vide */

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

    void lastPlayed(Level lvl) { //Copie le niveau chargé (donc le dernier niveau joué) dans le fichier level-1.json qui sert à reprendre une partie
        File source = new File("/Users/lskr/IdeaProjects/Aquavias/aquavias/levels/level" + lvl.ID + ".json");
        File dest = new File("/Users/lskr/IdeaProjects/Aquavias/aquavias/levels/level-1.json");

        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = fis.read(buffer)) > 0) {

                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
