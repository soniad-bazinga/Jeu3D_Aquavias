import javafx.application.Application;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MenuApplication extends Application {

        private static final int WIDTH = 1280;
        private static final int HEIGHT = 720;
        Level enCours;
        PieceOverview po;
        //Stage stage2 = new Stage();
        File levelsFolder = new File("levels");
        String [] lvls = levelsFolder.list();


    public List<Pair<String, Runnable>> menuData = Arrays.asList( //Définit une liste qui comprend tous les boutons sous un couple de String et d'action à effectuer
            //Boutton Nouvelle Partie du menu principal
            new Pair<String, Runnable>("Nouvelle Partie", () -> {
                    //stage2.close();
                    try{
                        enCours = new Level(1);
                        po = new PieceOverview(enCours);
                        //po.start(stage2);
                    } catch (Exception e){
                        System.out.println("Niveau manquant");
                    }
            }),
            //Boutton continuer du menu principal
            new Pair<String, Runnable>("Continuer", () -> {
                //stage2.close();
                try{
                    enCours = new Level(-1);
                    po = new PieceOverview(enCours);
                    //po.start(stage2);
                } catch (Exception e){
                    System.out.println("Niveau manquant");
                }
            }),
            new Pair<String, Runnable>("Choix du Niveau", () -> {System.out.println("Choisir le niveau"); menuLevelAnimation();}),
            new Pair<String, Runnable>("Réglages", () -> {System.out.println("Modifier les réglages du jeu");}),
            new Pair<String, Runnable>("Quitter le jeu", Platform::exit)
        );

    public Button retour = new Button();

    public ArrayList<Pair<String, Runnable>> levelData = new ArrayList<>();

    public Pane root = new Pane(); //Panneau sur lequel on va superposer tous les éléments

    public VBox menuBox = new VBox(); //Boite invisible qui contient les items du menu
    public GridPane LevelBox = new GridPane(); //Boite invisible qui contient les différents niveaux à séléctionner
    public HBox titleBox = new HBox(); //Boite invisible qui contient le titre
    double lineX = WIDTH / 2.0 - 100.0; //Ajoute le menu au centre
    double lineY = HEIGHT / 3.0 + 50.0; //Ajoute le menu au centre

    public MenuApplication(){
        super();
    }

    private Parent createContent() throws MalformedURLException {
        LevelBox.setHgap(25); //Cette ligne et la suivante décident de l'écart entre les "cases" de niveau dans le menu de séléction du niveau
        LevelBox.setVgap(10);

        //setButton();

        addBackground(); //Fonction qui choisit une image, la floute et l'ajoute en fond du menu principal

        addTitle();//Fonction qui ajoute le titre créé par MenuTitle.java

        addMenu(lineX + 5, lineY + 5); //Crée tous les items du menu et les ajoute au Pane parent (root)
        addLevelSelect(WIDTH/2.0 - 100.0, -HEIGHT + 200.0, 3);
        startAnimation(); //Crée les animations du menu

        return root;
    }
        private void setButton(){
            retour.setTranslateX(-50.0);
            retour.setTranslateY(HEIGHT + 50.0);
            retour.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    reverseLevelAnimation();
                }
            });
            root.getChildren().add(retour);
        }

        private void addBackground() throws MalformedURLException {
            File img = new File("img/hello.jpeg");
            String url = img.toURI().toURL().toString();
            ImageView imageView = new ImageView(new Image(url));
            imageView.setFitWidth(WIDTH);
            imageView.setFitHeight(HEIGHT);
            imageView.setEffect(new GaussianBlur());
            root.getChildren().add(imageView);
        }

        private void addTitle() {
            MenuTitle title = new MenuTitle("Aquavias");
            title.setTranslateX(WIDTH / 2.0 - title.getTitleWidth()/2);
            title.setTranslateY(HEIGHT / 3.0);

            titleBox.getChildren().add(title);
            root.getChildren().add(titleBox);
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

        private void menuLevelAnimation(){
            ScaleTransition st = new ScaleTransition((Duration.seconds(1)));
            st.setToY(1);
            st.setOnFinished(e -> {
                TranslateTransition tt = new TranslateTransition((Duration.seconds(1.5)), menuBox);
                TranslateTransition tt2 = new TranslateTransition((Duration.seconds(1)), titleBox);
                TranslateTransition tt3 = new TranslateTransition((Duration.seconds(1)), LevelBox);
                tt.setToY(1200);
                tt2.setToY(-150);
                tt3.setToY(HEIGHT/4.0);
                tt3.setToX(WIDTH/4.0);
                tt.play();
                tt2.play();
                tt3.play();
            });
            st.play();
        }

        private void reverseLevelAnimation(){
            ScaleTransition st = new ScaleTransition((Duration.seconds(1)));
            st.setToY(1);
            st.setOnFinished(e -> {
                TranslateTransition tt = new TranslateTransition((Duration.seconds(1.5)), menuBox);
                TranslateTransition tt2 = new TranslateTransition((Duration.seconds(1)), titleBox);
                TranslateTransition tt3 = new TranslateTransition((Duration.seconds(1)), LevelBox);
                TranslateTransition tt4 = new TranslateTransition((Duration.seconds(1)), retour);
                tt4.setToY(HEIGHT - 50.0);
                tt4.setToX(WIDTH - 50.0);
                tt3.setToX(WIDTH/2.0 - 100.0);
                tt3.setToY(-HEIGHT + 200.0);
                tt2.setToY(HEIGHT / 3.0);
                tt2.setToX(WIDTH / 2.0 - titleBox.getWidth()/2);
                tt.setToY(lineY + 5.0);
                tt.setToX(lineX + 5.0);
                tt.play();
                tt2.play();
                tt3.play();
                tt4.play();
            });
            st.play();
        }

        private void addMenu(double x, double y) {
            menuBox.setTranslateX(x);
            menuBox.setTranslateY(y);
            menuData.forEach(data -> {
                MenuItems item = new MenuItems(data.getKey());
                item.setOnAction(data.getValue());
                item.setTranslateX(-400);

                Rectangle clip = new Rectangle(300, 30);
                clip.translateXProperty().bind(item.translateXProperty().negate());

                item.setClip(clip);

                menuBox.getChildren().addAll(item);
            });

            root.getChildren().add(menuBox);
        }


        public void addLevelToList(List<Pair<String, Runnable>> list){
            for(int i = 0; i < lvls.length; i++){
                lvls[i] = lvls[i].split("\\.")[0];
                if(!lvls[i].equals("")) {
                    lvls[i] = lvls[i].substring(5);
                }
                if(lvls[i] != null) {
                    int finalI = i;
                    list.add(new Pair<>(lvls[i], () -> {
                        //stage2.close();
                        try {
                            enCours = new Level(Integer.parseInt(lvls[finalI]));
                            po = new PieceOverview(enCours);
                            //po.start(stage2);
                        } catch (Exception ex) {
                            System.out.println("Niveau manquant");
                        }
                    }));
                }
            }
        }

        private void addLevelSelect(double x, double y, int taillemax) {
            final int[] col = {0}; //Attribut colonne pour la création de la liste de niveaux visuelle
            final int[] ligne = {0};//Pareil mais pour les lignes
            LevelBox.setTranslateX(x);
            LevelBox.setTranslateY(y);
            addLevelToList(levelData);
            levelData.forEach(data -> {
                if (!data.getKey().equals("")){
                    LevelItems l = new LevelItems(data.getKey());
                    l.setOnAction(data.getValue());

                    Rectangle clip = new Rectangle(200, 100);//Coupe le Polygon dans LvlItems, s'il est plus grand que 200x100
                    clip.translateXProperty().bind(l.translateXProperty().negate());

                    l.setClip(clip);

                    LevelBox.add(l, col[0]%taillemax, ligne[0], 1, 1);// On ajoute le niveau à la colonne "colonne mod taillemax" et ligne
                    if ((col[0]+ 1)%taillemax == 0) ligne[0] = ligne[0] + 1; //Si on arrive au bout de la ligne (nb max d'éléments par ligne) on pas à la suivante
                    col[0] = col[0] + 1;//On passe à la colonne suivante
                }
            });
            root.getChildren().add(LevelBox);
        }

        @Override
        public void start(Stage primaryStage) throws MalformedURLException {
            Scene scene = new Scene(createContent());
            primaryStage.setTitle("Aquavias");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

    public static void main(String[] args) {
        launch(args);
    }
}
