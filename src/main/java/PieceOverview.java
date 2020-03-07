import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import java.net.URL;

public class PieceOverview extends Application{

    /* On definit la taille */
     static final int WIDTH = 720;
     static final int HEIGHT = 720;

     /* utiliser le mode "piece"+piece+".obj" a afficher ! :) */
    static String piece = "I";

    /* les variables qui stockeront : dragged = la position lors du click; angle = l'angle lors du click */
    static double dragged_X, dragged_Y;
    static double angle_X, angle_Y;

    /* des propriétés */
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    static final double PIECE_SIZE = 5.85;

    /* ses nouveaux attributs pour afficher le level */
    static Level level;
    static Piece[][] pieces;

    /* une matrice de piece3D (des groupes de mesh) */
    static Piece3D[][] models;

    /* une matrice de carrés bleus representant l'eau */
    waterTile[][] waterTiles;

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
        camera.setTranslateZ(-50);
        camera.setTranslateY(-120);
        camera.getTransforms().add(new Rotate(-70,Rotate.X_AXIS));
        camera.setFarClip(1000);

        /* On importe le model de la piece */
        Group root = new Group();

        /* On initialise nos deux tableaus */
        waterTiles = new waterTile[pieces.length][pieces[0].length];
        models = new Piece3D[pieces.length][pieces[0].length];

        /* On recopie à l'identique le niveau en 3d */
        for(int i = 0 ; i < models.length ; i++) {
            for(int j = 0 ; j < models[i].length ; j++) {
                if(pieces[i][j] == null) continue;
                models[i][j] = new Piece3D(i,j);
                /* # A CHANGER QUAND ON AURA TOUT LES MODES # */
                models[i][j].importModel(getClass().getResource("piece" + piece + "_simple.obj"));
                /* on place la pièce */
                models[i][j].setTranslateX(PIECE_SIZE * i-15);
                models[i][j].setTranslateY(10);
                models[i][j].setTranslateZ((PIECE_SIZE * j-15));
                /* si la piece [i][j] est pleine, on lui affiche une waterTile */
                /* mais avec une visibilité a false */
                /* comme ça la rotation de la waterTile sera toujours actualisée */
                waterTiles[i][j] = new waterTile();
                if(i != 0 && j != 0 ) waterTiles[i][j].setVisible(false);
                waterTiles[i][j].setTranslateX(PIECE_SIZE * i-15);
                waterTiles[i][j].setTranslateY(6.75);
                waterTiles[i][j].setTranslateZ((PIECE_SIZE * j-15));

                /* on les tournes comme il se doit :) */
                for(int r = 0 ; r < pieces[i][j].getRotation() ; r++){
                    waterTiles[i][j].getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
                    models[i][j].getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
                }
                /* puis on les ajoutes a root */
                root.getChildren().add(waterTiles[i][j]);
                root.getChildren().add(models[i][j]);
            }
        }

        /* On créer des objets permettants la transformation de la rotation sur les axes X et Y */
        Rotate xRotate, yRotate;
        root.getTransforms().addAll(
                xRotate = new Rotate(0,Rotate.X_AXIS),
                yRotate = new Rotate(0,Rotate.Y_AXIS)
        );

        /* on les lies aux propriétes */
        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        /* obselete, a revoir */
        /* lorsque l'on click, on sauvegarde la position de la souris actuel et l'angle de rotation actuel */
        /*stage.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->{
            dragged_X = event.getSceneX();
            dragged_Y = event.getSceneY();
            angle_X = angleX.get();
            angle_Y = angleY.get();
        });
        /* lorsque l'on déplace la souris, on modifie l'angle de la pièce */
         /*stage.addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->{
            angleX.set((angle_X - (dragged_X - event.getSceneY())%360));
            angleY.set((angle_Y + dragged_X - event.getSceneX())%360);
        }); */

        /* La scene, avec root, la taille et le depthbuffered activée */
        Scene scene = new Scene(root,WIDTH,HEIGHT,true);
        /* On lui lie la caméra */
        scene.setCamera(camera);

        scene.setOnMousePressed((MouseEvent me) -> {
            /* on récupère le résultat du click */
            PickResult pr = me.getPickResult();
            if(pr!=null && pr.getIntersectedNode() != null && !(pr.getIntersectedNode() instanceof waterTile)){
                /* ses meshs aussi */
                MeshView mv = (MeshView) pr.getIntersectedNode();
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
        waterTiles[x][y].getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
        /* on désactive l'eau */
        waterTiles[x][y].setVisible(false);
        /* on update */
        level.update();
        level.affiche();
    }

    void setFull(int i, int j, boolean b){
        /* on l'active ou désactive */
        waterTiles[i][j].setVisible(b);
    }

    /* Permet d'importer une pièce via une URL */

    private class Piece3D extends Group{
        private int i;
        private int j;

        private Piece3D(int i, int j) {
            this.i = i;
            this.j = j;
        }

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

    private class waterTile extends Box {

        waterTile(){
            setScaleX(1);
            setScaleY(.5);
            setScaleZ(PIECE_SIZE/2);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(new Color(.61,.80,.87,.8));
            material.setSpecularColor(Color.AQUAMARINE);
            setMaterial(material);
        }

        public String toString(){
            return "full";
        }
    }
}
