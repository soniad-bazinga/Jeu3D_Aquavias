import javafx.beans.binding.Bindings;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuItems extends Pane{

        private static AudioController audioController;

        private Text text; //Le texte qui est présent dans chacune des boites du menu

        private Effect shadow = new DropShadow(5, Color.BLACK); //L'effet d'ombre derrière le texte quand on passe la souris dessus
        private Effect blur = new BoxBlur(1, 1, 3); //L'effet de flou lorsque l'on ne passe pas dessus

        public MenuItems(String name) {  //Ce constructeur crée les boites des items du menu, de la forme à la couleur/aux effets et leur assigne également un nom "name"
            Polygon bg = new Polygon(
                    0, 0,
                    200, 0,
                    200, 30,
                    0, 30,
                    0, 30
            );
            bg.setStroke(Color.WHITE); //Permet de définir la couleur du contour des boites

            bg.fillProperty().bind( //Le bind permet de définir des évènements lorsqu'une certaine condition est remplie
                    Bindings.when(pressedProperty())
                            .then(Color.LIGHTCYAN) //Ici, la couleur de la boite sera un cyan clair lorsque l'on clique dessus
                            .otherwise(Color.LIGHTBLUE) //Mais arborera un bleu clair autrement
            );

            text = new Text(name); //Le titre de la boite est ajouté
            text.setTranslateX(15); //On essaie de le placer le plus au centre de la boite possible
            text.setTranslateY(20);
            text.setFont(Font.font("Roboto")); //On définit une police satisfaisante à l'oeil
            text.setFill(Color.WHITE);

            text.effectProperty().bind(
                    Bindings.when(hoverProperty())
                            .then(shadow) //Amène une ombre sous le texte lorsqu'on le clique
                            .otherwise(blur) //Est flouté autrement
            );

            getChildren().addAll(bg, text); //On ajoute la boite et le texte au parent
        }

        public void setOnAction(Runnable action) {
            setOnMouseClicked(e -> {
                action.run();
                /* on joue le son quande on clique */
                audioController.play("click");
            });
            /* on joue le son en hover */
            setOnMouseEntered(e -> audioController.play("hover"));
        } //Définit une action à effectuer dès que le bouton est cliqué

        public static void setAudioController(AudioController a){
            audioController = a;
        }
}
