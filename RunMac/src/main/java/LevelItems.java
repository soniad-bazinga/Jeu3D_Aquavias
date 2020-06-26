import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class LevelItems extends Pane {
    Rectangle boop;
    boolean unlocked = false;

    public LevelItems(String URL){
        Image image = new Image (new File("img/levels/level" + URL + ".jpg").toURI().toString());

        ImagePattern i = new ImagePattern(image);

        boop = new Rectangle(200, 100);
        boop.setFill(i);

        boop.setOpacity(.5);

        boop.setVisible(true);

        boop.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                mouseOnAnimation();
            }
        });

        boop.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                mouseOffAnimation();
            }
        });


        getChildren().setAll(boop);
    }

    void mouseOnAnimation(){
        if(!unlocked) return;
        ScaleTransition st = new ScaleTransition(Duration.seconds(0.5));
        st.setOnFinished(e -> {
            TranslateTransition tt = new TranslateTransition((Duration.seconds(0.5)), this);
            tt.setToY(tt.getByY() - 10.0);
            tt.play();
        });
        st.play();
    }

    void mouseOffAnimation(){
        if(!unlocked) return;
        ScaleTransition st = new ScaleTransition(Duration.seconds(0.5));
        st.setOnFinished(e -> {
            TranslateTransition tt = new TranslateTransition((Duration.seconds(0.5)), this);
            tt.setToY(tt.getByY());
            tt.play();
        });
        st.play();
    }

    //Définit une action à effectuer dès que le bouton est cliqué
    public void setOnAction(Runnable action) {
        setOnMouseClicked(e -> {
            /* si le niveau est débloqué on peut lancer l'action */
            if(unlocked) action.run();
        });
    }

    public void setUnlocked(boolean unlock) {
        /*
            si on le débloque alors on réaffiche l'opacité a fond et les animations liées
         */
        if(unlock){
            boop.setOpacity(1);
            unlocked = true;
        }else{
            /* sinon on bloque tout */
            boop.setOpacity(.5);
            unlocked = false;
        }
    }
}
