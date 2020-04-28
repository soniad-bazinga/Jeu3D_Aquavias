import javafx.application.Application;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;


public class MenuApplication extends Application {

        private static final int WIDTH = 1280;
        private static final int HEIGHT = 720;
        //Level enCours;

    public List<Pair<String, Runnable>> menuData = Arrays.asList(
                new Pair<String, Runnable>("Nouvelle Partie", () -> {
                    /*try{
                        enCours = new Level(1);
                    } catch (Exception e){
                        System.out.println("Niveau manquant");
                    }*/
                }),
                new Pair<String, Runnable>("Continuer", () -> {System.out.println("Continuer la partie");}),
                new Pair<String, Runnable>("Choix du Niveau", () -> {System.out.println("Choisir le niveau");}),
                new Pair<String, Runnable>("Quitter le jeu", Platform::exit)
        );

        public Pane root = new Pane();
        public VBox menuBox = new VBox(-5);

    public MenuApplication(){
    }

    private Parent createContent() throws MalformedURLException {
            addBackground();
            addTitle();

            double lineX = WIDTH / 2.0 - 100.0;
            double lineY = HEIGHT / 3.0 + 50.0;

            addMenu(lineX + 5, lineY + 5);

            startAnimation();

            return root;
        }

        private void addBackground() throws MalformedURLException {
            File img = new File("/Users/lskr/IdeaProjects/Aquavias/aquavias/img/hello.jpeg");
            String url = img.toURI().toURL().toString();
            ImageView imageView = new ImageView(new Image(url));
            imageView.setFitWidth(WIDTH);
            imageView.setFitHeight(HEIGHT);
            imageView.setEffect(new GaussianBlur());

            root.getChildren().add(imageView);
        }

        private void addTitle() {
            MenuTitle title = new MenuTitle("Aquavias");
            title.setTranslateX(WIDTH / 2.0 - title.getTitleWidth() / 2);
            title.setTranslateY(HEIGHT / 3.0);

            root.getChildren().add(title);
        }

        private void startAnimation() {
            ScaleTransition st = new ScaleTransition(Duration.seconds(1));
            st.setToY(1);
            st.setOnFinished(e -> {

                for (int i = 0; i < menuBox.getChildren().size(); i++) {
                    Node n = menuBox.getChildren().get(i);

                    TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
                    tt.setToX(0);
                    tt.setOnFinished(e2 -> n.setClip(null));
                    tt.play();
                }
            });
            st.play();
        }

        private void addMenu(double x, double y) {
            menuBox.setTranslateX(x);
            menuBox.setTranslateY(y);
            menuData.forEach(data -> {
                MenuItems item = new MenuItems(data.getKey());
                item.setOnAction(data.getValue());
                item.setTranslateX(-300);

                Rectangle clip = new Rectangle(300, 30);
                clip.translateXProperty().bind(item.translateXProperty().negate());

                item.setClip(clip);

                menuBox.getChildren().addAll(item);
            });

            root.getChildren().add(menuBox);
        }

        @Override
        public void start(Stage primaryStage) throws MalformedURLException {
            Scene scene = new Scene(createContent());
            primaryStage.setTitle("Aquavias bro");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

    public static void main(String[] args) {
        launch(args);
    }
}
