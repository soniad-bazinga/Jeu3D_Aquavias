import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.HashMap;

import static javafx.scene.media.MediaPlayer.INDEFINITE;

public class AudioController {

    MediaPlayer musique;
    HashMap<String,Media> sons = new HashMap<String,Media>();
    AudioClip son;
    Double volumeSon;

    public AudioController(){
        /* on definit la musique utilisé */
        String musicFile = "sounds/menumusic.wav";
        Media msq = new Media(new File(musicFile).toURI().toString());
        musique = new MediaPlayer(msq);
        musique.play();
        musique.setCycleCount(INDEFINITE);  //loop

        /* ici on ajoute tout les sons */
        addSounds();
    }

    private void addSounds(){
        /* on récupère tout les fichiers du dossier son */
        File directory = new File("sounds");
        /* on parcours chaque fichier dedans */
        for(File f : directory.listFiles()){
            /* on récupère le media correspondant au son */
            Media m = new Media(f.toURI().toString());
            /* et son nom avant le '.' */
            String s = f.getName().substring(0,f.getName().indexOf("."));
            /* et on l'ajoute a sons */
            sons.put(s,m);
        }
    }

    public void play(String s){
        son = new AudioClip(sons.get(s).getSource());

        son.setVolume(volumeSon);
        /* Bruit de vent trop faible, on triple son volume */
        if(s.contains("wind")) son.setVolume(volumeSon * 3);

        son.play();
    }

    double tempMusique;
    int cycle = 500;

    public void pauseMusique(){
        tempMusique = musique.getVolume();

        double reduce = tempMusique/cycle;

        Timeline lowerVolume = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            musique.setVolume(musique.getVolume() - reduce);
        }));
        lowerVolume.setCycleCount(cycle);
        lowerVolume.play();

        lowerVolume.setOnFinished(e -> musique.pause());
    }

    public void playMusique(){
        musique.play();

        double reduce = tempMusique/cycle;

        Timeline upVolume = new Timeline(new KeyFrame(Duration.millis(1),event -> {
            musique.setVolume(musique.getVolume() + reduce);
        }));
        upVolume.setCycleCount(cycle);
        upVolume.play();
    }

    void setSonsVolume(double volume){
        volumeSon = volume;
    }

    void setMusiqueVolume(double volume){
        musique.setVolume(volume);
    }
}
