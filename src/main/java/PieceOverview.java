import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
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

    Piece3D[][] models = new Piece3D[5][5];

    /* Créé un aperçu de piece */
    public PieceOverview(){
        /*switch(s){
            case "L" : piece = "L"; break;
            case "T" : piece = "T"; break;
            case "I" : piece = "I"; break;
            default : piece = "X"; break;
        }*/
    }

    @Override
    public void start(Stage stage){

        /* On créer une caméra qui pointe vers 0,0 (true) et la recule sur l'axe Z */
        PerspectiveCamera camera  = new PerspectiveCamera(true);
        camera.setTranslateZ(-90.5);
        camera.setFarClip(1000);

        /* On importe le model de la piece */
        Group root = new Group();

        for(int i = 0 ; i < models.length ; i++) {
            for(int j = 0 ; j < models[i].length ; j++) {
                models[i][j] = new Piece3D(i,j);
                models[i][j].importModel(getClass().getResource("piece" + piece + "_simple.obj"));
                models[i][j].setTranslateX(PIECE_SIZE * i-15);
                models[i][j].setTranslateY(10);
                models[i][j].setTranslateZ((PIECE_SIZE * j-15));
                root.getChildren().add(models[i][j]);
            }
        }

        /* Ici on definit les interractions possibles */
      /*  stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch(event.getCode()){
                case Z :  model.getTransforms().add(new Rotate(5,Rotate.X_AXIS)); break;
                case S : model.getTransforms().add(new Rotate(-5,Rotate.X_AXIS)); break;
                case Q :  model.getTransforms().add(new Rotate(5,Rotate.Y_AXIS)); break;
                case D : model.getTransforms().add(new Rotate(-5,Rotate.Y_AXIS)); break;
                case A :  model.getTransforms().add(new Rotate(-5,Rotate.Z_AXIS)); break;
                case E : model.getTransforms().add(new Rotate(5,Rotate.Z_AXIS));break;
            }
        }); */



        /* On créer le groupe racine */

        /* On créer des objets permettants la transformation de la rotation sur les axes X et Y */
        Rotate xRotate, yRotate;
        root.getTransforms().addAll(
                xRotate = new Rotate(0,Rotate.X_AXIS),
                yRotate = new Rotate(0,Rotate.Y_AXIS)
        );

        /* on les lies aux propriétes */
        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        /* lorsque l'on click, on sauvegarde la position de la souris actuel et l'angle de rotation actuel */
        stage.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->{
            dragged_X = event.getSceneX();
            dragged_Y = event.getSceneY();
            angle_X = angleX.get();
            angle_Y = angleY.get();
        });
        /* lorsque l'on déplace la souris, on modifie l'angle de la pièce */
        stage.addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->{
            angleX.set((angle_X - (dragged_X - event.getSceneY())%360));
            angleY.set((angle_Y + dragged_X - event.getSceneX())%360);
        });

        /* La scene, avec root, la taille et le depthbuffered activée */
        Scene scene = new Scene(root,WIDTH,HEIGHT,true);
        /* On lui lie la caméra */
        scene.setCamera(camera);

        scene.setOnMousePressed((MouseEvent me) -> {
            PickResult pr = me.getPickResult();
            System.out.println(pr);
            if(pr!=null && pr.getIntersectedNode() != null){
                System.out.println(pr.getIntersectedTexCoord());
                double distance=pr.getIntersectedDistance();
                MeshView mv = (MeshView) pr.getIntersectedNode();
                mv.getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
            }
        });

        /* On nome la fenetre, y ajoute la scene et on l'affiche */
        stage.setTitle("Aperçu de pièce");
        stage.setScene(scene);
        stage.show();

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
}
