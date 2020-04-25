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

        private Text text;

        private Effect shadow = new DropShadow(5, Color.BLACK);
        private Effect blur = new BoxBlur(1, 1, 3);

        public MenuItems(String name) {
            Polygon bg = new Polygon(
                    0, 0,
                    200, 0,
                    200, 30,
                    0, 30,
                    0, 30
            );
            bg.setStroke(Color.WHITE);

            bg.fillProperty().bind(
                    Bindings.when(pressedProperty())
                            .then(Color.BLUE)
                            .otherwise(Color.LIGHTBLUE)
            );

            text = new Text(name);
            text.setTranslateX(15);
            text.setTranslateY(20);
            text.setFont(Font.font("Roboto"));
            text.setFill(Color.WHITE);

            text.effectProperty().bind(
                    Bindings.when(hoverProperty())
                            .then(shadow)
                            .otherwise(blur)
            );

            getChildren().addAll(bg, text);
        }

        public void setOnAction(Runnable action) {
            setOnMouseClicked(e -> action.run());
        }
}
