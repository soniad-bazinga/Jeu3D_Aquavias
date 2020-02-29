import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main3D extends Application{

    private static final int WIDTH = 720;
    private static final int HEIGHT = 720;

    @Override
    public void start(Stage stage){

        PerspectiveCamera camera  = new PerspectiveCamera(true);
        camera.setTranslateZ(-3.5);

        Group model = importModel(getClass().getResource("Ballon/football_ball_OBJ.obj"));
        model.getTransforms().add(new Rotate(90,Rotate.Y_AXIS));

        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch(event.getCode()){
                case Z :  model.getTransforms().add(new Rotate(5,Rotate.X_AXIS)); break;
                case S : model.getTransforms().add(new Rotate(-5,Rotate.X_AXIS)); break;
                case Q :  model.getTransforms().add(new Rotate(5,Rotate.Y_AXIS)); break;
                case D : model.getTransforms().add(new Rotate(-5,Rotate.Y_AXIS)); break;
                case A :  model.getTransforms().add(new Rotate(-5,Rotate.Z_AXIS)); break;
                case E : model.getTransforms().add(new Rotate(5,Rotate.Z_AXIS));break;
            }
        });

        Group root = new Group(model);

        Scene scene = new Scene(root,WIDTH,HEIGHT,true);
        scene.setCamera(camera);

        stage.setTitle("Ballon");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args){
        launch(args);
    }

    Group importModel(URL url){
        Group modelRoot = new Group();

        ObjModelImporter objModelImporter = new ObjModelImporter();
        objModelImporter.read(url);

        for(MeshView view : objModelImporter.getImport()){
            modelRoot.getChildren().addAll(view);
        }

        return modelRoot;
    }

}
