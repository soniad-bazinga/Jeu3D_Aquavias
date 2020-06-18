import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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
        Media son1 = new Media(new File("sounds/hover.mp3").toURI().toString());
        sons.put("hover",son1);

        Media son2 = new Media(new File("sounds/click.mp3").toURI().toString());
        sons.put("click",son2);

        Media son3 = new Media(new File("sounds/rotation.mp3").toURI().toString());
        sons.put("rotation",son3);
    }

    public void play(String s){
        son = new AudioClip(sons.get(s).getSource());
        son.setVolume(volumeSon);
        son.play();
    }

    void setSonsVolume(double volume){
        volumeSon = volume;
    }

    void setMusiqueVolume(double volume){
        musique.setVolume(volume);
    }
}
