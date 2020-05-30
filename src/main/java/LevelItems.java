import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class LevelItems extends Pane {

    public LevelItems(String URL){
        Image image = new Image (new File("img/levels/level" + URL + ".jpg").toURI().toString());

        ImagePattern i = new ImagePattern(image);

        Rectangle boop = new Rectangle(200, 100);
        boop.setFill(i);

        boop.setVisible(true);


        getChildren().setAll(boop);
    }

    public void setOnAction(Runnable action) {
        setOnMouseClicked(e -> action.run());
    } //Définit une action à effectuer dès que le bouton est cliqué
}
