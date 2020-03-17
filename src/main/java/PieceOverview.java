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
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.event.ActionEvent;
import java.net.URL;

public class PieceOverview extends Application{

    /* On definit la taille */
     static final int WIDTH = 720;
     static final int HEIGHT = 720;

     /* utiliser le mode "piece"+piece+".obj" a afficher ! :) */
    static String piece = "I";

    static final double PIECE_SIZE = 5.85;

    /* ses nouveaux attributs pour afficher le level */
    static Level level;
    static Piece[][] pieces;

    /* une matrice de piece3D (des groupes de mesh) */
    static Piece3D[][] models;

    /* une matrice de carrés bleus representant l'eau */
    waterPiece[][] waterPieces;

    /* Créé un aperçu de piece */
    public PieceOverview(Level level){
       super();
       this.level = level;
       this.pieces = level.getPieces();
       launch();
    }

    public PieceOverview(){
        super();
    }



    @Override
    public void start(Stage stage){

        level.setOverviewer(this);

        /* On créer une caméra qui pointe vers 0,0 (true) et la recule sur l'axe Z */
        PerspectiveCamera camera  = new PerspectiveCamera(true);
        camera.setTranslateZ(-70);
        camera.setTranslateY(-100);
        camera.setTranslateX(70);
        camera.getTransforms().add(new Rotate(-45,Rotate.Y_AXIS));
        camera.getTransforms().add(new Rotate(-45,Rotate.X_AXIS));

        camera.setFarClip(1000);

        /* On importe le model de la piece */
        Group root = new Group();

        /* On initialise nos deux tableaus */
        waterPieces = new waterPiece[pieces.length][pieces[0].length];
        models = new Piece3D[pieces.length][pieces[0].length];

        /* On recopie à l'identique le niveau en 3d */
        for(int i = 0 ; i < models.length ; i++) {
            for(int j = 0 ; j < models[i].length ; j++) {
                if(pieces[i][j] == null) continue;
                models[i][j] = new Piece3D();
                /* # A CHANGER QUAND ON AURA TOUT LES MODES # */
                models[i][j].importModel(getClass().getResource("piece" + piece + "_simple.obj"));
                /* on place la pièce */
                models[i][j].setTranslateX(PIECE_SIZE * i-15);
                models[i][j].setTranslateY(10);
                models[i][j].setTranslateZ((PIECE_SIZE * j-15));
                /* si la piece [i][j] est pleine, on lui affiche une waterTile */
                /* mais avec une visibilité a false */
                /* comme ça la rotation de la waterTile sera toujours actualisée */
                waterPieces[i][j] = new waterPiece(pieces[i][j].getType(), PIECE_SIZE/2);
                waterPieces[i][j].setTranslateX(PIECE_SIZE * i-15);
                waterPieces[i][j].setTranslateY(6.5);
                waterPieces[i][j].setTranslateZ((PIECE_SIZE * j-15));
                /* debug */
                //models[i][j].setVisible(false);
                if(!pieces[i][j].isFull()) waterPieces[i][j].setVisible(false);
                /* on les tournes comme il se doit :) */
                for(int r = 0 ; r < pieces[i][j].getRotation() ; r++){
                    waterPieces[i][j].getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
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
                int x_coord = (int) Math.abs(Math.round(mv.localToScene(mv.getBoundsInLocal()).getMinX()/PIECE_SIZE)+models.length-2);
                int y_coord = (int) Math.abs(Math.round(mv.localToScene(mv.getBoundsInLocal()).getMinZ()/PIECE_SIZE)+models.length-2);
                /* on rotate le tout */
                rotate(x_coord,y_coord);
            }
        });

        /* On nome la fenetre, y ajoute la scene et on l'affiche */
        stage.setTitle("Aperçu de pièce");
        stage.setScene(scene);
        stage.show();
    }

    void rotate(int x,int y){
        /* on rotate le jeu, les pièces, et les pièces d'eau */
        level.rotate(x,y);
        models[x][y].getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
        waterPieces[x][y].rotate();
        /* on désactive l'eau */
        waterPieces[x][y].setVisible(false);
        /* on update */
        level.update();
        level.affiche();
    }

    void setFull(int i, int j, boolean b){
        /* on l'active ou désactive */
        waterPieces[i][j].setVisible(b);
    }

    void updating(){
        Timeline test = new Timeline(new KeyFrame(Duration.seconds(.1), event ->{
            System.out.println("yo");
        }));
        test.setCycleCount(5);
        test.play();
    }

    private Slider prepareSlider(){
        Slider slider = new Slider();
        slider.setMax(1000);
        slider.setMin(-1000);
        slider.setPrefWidth(300d);
        slider.setLayoutX(-150);
        slider.setLayoutY(200);
        slider.setShowTickLabels(true);
        slider.setTranslateZ(5);
        slider.setStyle("-fx-base: black");
        return slider;
    }

    /* Permet d'importer une pièce via une URL */

    private class Piece3D extends Group{

        public void importModel(URL url){

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
}
