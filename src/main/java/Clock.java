import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Clock extends Pane {

    private Timeline animation; //Variable qui gère le temps
    public int tmp; //Entier nécessaire pour savoir combien de temps doit direr le niveau
    Text compteur = new Text(); //Texte sur lequel on "imprimer" l'esprit restant
    String s = ""; //String sur laquelle on va modifier le temps restant
    StackPane stack = new StackPane(); //Panneau sur lequel superposer les différents éléments


    public Clock(int time){ //Time est la variable "ccompteur" contenue dans le json du niveau
        tmp = time+1;

        if(animation != null) animation.stop(); //Au cas où l'animation s'est mal finie, on la nettoie avant de recommencer

        KeyFrame frame = new KeyFrame(Duration.seconds(1), e -> timelabel());
        animation = new Timeline(frame);
        animation.setCycleCount(Timeline.INDEFINITE); //L'animation se répète indéfiniment
        animation.play();

        compteur.setFill(new Color(0,0,0,.6));

        Rectangle r = new Rectangle();

        r.setEffect(new DropShadow());

        stack.getChildren().addAll(r, compteur);

        r.setWidth(150);
        r.setHeight(50);

        r.setFill(new Color(0,0,0,.05));

        getChildren().add(stack);
    }

    private void timelabel(){
        if (tmp > 0) tmp--;
        s = "[" + tmp + "]";
        compteur.setText(s);
    }

    void pause(){
        animation.pause();
    }

    void play(){
        animation.play();
    }

    void stop(){
        animation.stop();
    }


}
