import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuTitle extends Pane{
        private Text part1;
        private Text part2;
        private Text text;

        public MenuTitle(String name) {
            /*On crée des Strings pour séparer les deux parties et espacer les caractères*/
            String one = "";
            String two = "";
            int i;
            for (i = 0; i < 3; i++) {
                one += " " + name.charAt(i);
            }
            for (int j = i; j < name.length(); j++) {
                two += name.charAt(j) + " ";
            }
            /*Une fois qu'elles sont séparées, on ajoute les couleurs correspondantes*/
            part1 = new Text(one);
            part2 = new Text(two);
            part1.setFill(Color.WHITE);
            part2.setFill(Color.BLUE);
            part1.setFont(Font.font("Roboto", 60));
            part2.setFont(Font.font("Roboto", 60));
            part2.setEffect(new DropShadow(30, Color.BLACK));

            part1.setText(part1.getText() + " " + part2.getText());  //Puis on assemble les "text" en une seule entité modifiable
            text = part1;

            getChildren().addAll(text); //Finalement on ajoute le text au Pane
        }

        public double getTitleWidth() {
            return text.getLayoutBounds().getWidth();
        } //Permet de récupérer la taille en longueur du titre

        public double getTitleHeight() {
            return text.getLayoutBounds().getHeight();
        } //Permet de récupérer la taille en hauteur du titre
}
