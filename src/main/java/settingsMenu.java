import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;

public class settingsMenu extends GridPane {

    MediaPlayer mediaPlayer;
    double MUSIQUE;
    double SONS;
    int WIDTH;
    int HEIGHT;
    MenuItems retour;
    Slider musique;
    Slider sons;

    public settingsMenu(MediaPlayer player, Color textColor){
        loadSettings();
        mediaPlayer = player;

        setHgap(25);
        setVgap(20);

        /* les contraintes de colonnes */
        /* on en créer 4 chacune de 25% */
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);
        getColumnConstraints().addAll(col1,col2,col3,col4);

        /* Le slider pour la musique, son label et sa valeur */
        musique = new Slider(0,100,MUSIQUE);

        Label labelMusique = new Label("Musique :");
        labelMusique.setTextFill(textColor);

        Label niveauMusique = new Label(Integer.toString((int)musique.getValue()));
        niveauMusique.setTextFill(textColor);

        add(labelMusique,0,0,1,1);
        add(musique,1,0,2,1);
        add(niveauMusique,3,0,1,1);

        /* Le slider pour les sons, son label et sa valeur */
        sons = new Slider(0,100,SONS);

        Label labelSons = new Label("Sons :");
        labelSons.setTextFill(textColor);

        Label niveauSons = new Label(Integer.toString((int)sons.getValue()));
        niveauSons.setTextFill(textColor);

        add(labelSons,0,1,1,1);
        add(sons,1,1,2,1);
        add(niveauSons,3,1,1,1);

        /* Lorsque l'on bouge le slider, le texte affichant sa valeur change et le niveau de la musique aussi */
        musique.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                niveauMusique.setText(Integer.toString((int)musique.getValue()));
                mediaPlayer.setVolume(musique.getValue()/100);
            }
        });

        /* comme la musique mais pour les sons */
        sons.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                niveauSons.setText(Integer.toString((int)sons.getValue()));
            }
        });

        /* le bouton retour, qui n'effectue aucune action */
        retour = new MenuItems("Retour");

        /* le bouton pour sauvegarder les changements */
        MenuItems sauvegarder = new MenuItems("Sauvegarder les changements");
        sauvegarder.setOnAction(new Runnable(){
            @Override
            public void run(){
                MUSIQUE = musique.getValue();
                SONS = sons.getValue();
                saveSettings();
            }
        });

        /* on leurs enlève l'auto focus */
        musique.setFocusTraversable(false);
        sons.setFocusTraversable(false);

        add(retour,0,3,2,1);
        add(sauvegarder,2,3,2,1);

        /* puis on applique les changement du volume au controleur audio */
        mediaPlayer.setVolume(MUSIQUE/100);
    }

    /* pour change l'action de retour */
    void setRetourAction(Runnable r){
        retour.setOnAction(r);
    }

    /* changer les valeurs si jamais on ne sauvegarde pas */
    void updateValues(){
        musique.setValue(MUSIQUE);
        sons.setValue(SONS);
        mediaPlayer.setVolume(MUSIQUE/100);
    }

    /* pour charger les réglages */
    void loadSettings(){
        try {
            FileReader reader = new FileReader("settings.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject obj = (JSONObject) jsonParser.parse(reader);

            WIDTH = Math.toIntExact((long) obj.get("WIDTH"));
            HEIGHT = Math.toIntExact((long) obj.get("HEIGHT"));

            MUSIQUE =  ((Number) obj.get("MUSIQUE")).doubleValue();
            SONS = ((Number) obj.get("SONS")).doubleValue();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    /* pour sauvegarder les réglages */
    void saveSettings(){
        try{
            FileReader reader = new FileReader("settings.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject obj = (JSONObject) jsonParser.parse(reader);

            obj.put("WIDTH",WIDTH);
            obj.put("HEIGHT",HEIGHT);
            obj.put("MUSIQUE",MUSIQUE);
            obj.put("SONS",SONS);

            FileWriter writer = new FileWriter("settings.json");
            writer.write(obj.toJSONString());
            writer.close();

        }catch(Exception e){
            System.out.println(e);
        }
    }

    public int getWindowHeight(){ return HEIGHT; }
    public int getWindowWidth(){ return WIDTH; }
}
