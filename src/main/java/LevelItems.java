import javafx.beans.binding.Bindings;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;


public class LevelItems extends Pane {

    public ImageView img;
    private final Effect shadow = new DropShadow(2, Color.LIGHTCYAN);
    private final Effect blur = new BoxBlur(1, 1, 3);

    public LevelItems(String URL){
        Polygon lvl = new Polygon(
                0, 0,
                300, 0,
                300, 300,
                0, 300,
                0, 300
        );
        img = new ImageView(new Image(new File("img/" + URL + ".jpg").toURI().toString()));
        img.effectProperty().bind(
                Bindings.when(hoverProperty()).
                        then(shadow).
                        otherwise(blur)
        );
        lvl.setStroke(Color.LIGHTGREEN);

        getChildren().addAll(lvl, img);
    }

    public void setOnAction(Runnable action) {
        setOnMouseClicked(e -> action.run());
    } //Définit une action à effectuer dès que le bouton est cliqué
}
