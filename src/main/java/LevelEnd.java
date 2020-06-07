import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.awt.*;

public class LevelEnd extends Pane {
    Rectangle box;



    public LevelEnd(){
        box = new Rectangle();

        box.setX(getParent().getScaleX()/5);
        box.setY(getParent().getScaleY()/5);
        box.setArcHeight(15);
        box.setArcWidth(15);

        box.setFill(Paint.valueOf("BLACK"));

        getChildren().add(box);


    }
}
