import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import java.awt.*;
import java.io.File;

//Cette classe gère dans sa quasi totalité la séquence de fin de niveau
public class LevelEnd extends Group {
    StackPane boite;
    Rectangle bluebox;

    public LevelEnd(double x, char g){ //Le constructeur a besoin de la longueur de la fenètre pour placer la boite
                                       //mais également d'une variable pour savoir si la partie est gagnée ou non

        Rectangle behind = new Rectangle(1280, 720);
        behind.setFill(Paint.valueOf("BLACK"));
        behind.setOpacity(0.6);
        behind.setX(0);
        behind.setY(0);

        getChildren().add(behind);

        boite = new StackPane();
        boite.setLayoutX(x/2);
        boite.setLayoutY(210);

        bluebox = new Rectangle(600, 300);

        bluebox.setX(x/2);
        bluebox.setY(210);

        bluebox.setArcHeight(15);
        bluebox.setArcWidth(15);

        bluebox.setFill(new ImagePattern(new Image(new File("img/pausemenu.png").toURI().toString())));
        bluebox.setStroke(Paint.valueOf("GREY"));

        boite.getChildren().add(bluebox);

        addButtons(g);

        getChildren().add(boite);
    }

    public void addButtons(char win){
        if (win == 'v'){
            HBox hboite = new HBox(10);
            Button suivant = new Button("Niveau suivant");
            Button replay = new Button("Rejouer");
            Button quit = new Button("Retour au menu");

            hboite.setAlignment(Pos.CENTER);

            hboite.getChildren().addAll(suivant, replay, quit);

            boite.getChildren().add(hboite);
        } else {
            HBox hboite = new HBox(10);
            Button replay = new Button("Rejouer");
            Button quit = new Button("Retour au menu");

            hboite.getChildren().addAll(replay, quit);

            boite.getChildren().add(hboite);
        }
    }
}
