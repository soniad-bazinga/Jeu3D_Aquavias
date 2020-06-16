import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class cheatHandler {

    /* la list d'input tapé par l'user en temps réel */
    List<KeyCode> inputs;

    /* la taille max de cette list */
    int size;

    /* une liste de pair input / action */
    List<Pair<KeyCode[],Runnable>> cheats;

    MenuApplication menu;

    public cheatHandler(int size, MenuApplication m){
        inputs = new ArrayList<KeyCode>();
        cheats = new ArrayList<Pair<KeyCode[],Runnable>>();

        this.size = size;
        menu = m;

        /* on créer ici un cheat "Haut Haut Haut Bas" qui nous fait gagner un niveau */
        Pair<KeyCode[],Runnable> c1 = new Pair<KeyCode[],Runnable>(new KeyCode[]{KeyCode.UP, KeyCode.UP, KeyCode.UP, KeyCode.DOWN},() -> {
            menu.incrementeMax();
        });

        cheats.add(c1);
    }

    public void addInput(KeyEvent k){
        /* Si il y a plus de size inputs on enlève le premier (le plus ancien) */
        if(inputs.size() >= size) inputs.remove(0);

        /* puis on ajoute a la fin le nouveau venu */
        inputs.add(k.getCode());

        /* et on execute */
        executeCheat();
    }

    private void executeCheat(){
        for(Pair p : cheats){

            /* ici on regarde la liste de tout les cheats dispo, et vérifie si 1 correspond aux inputs actuels */
            KeyCode[] k = inputs.toArray(new KeyCode[0]);
            KeyCode[] cheat = (KeyCode[]) p.getKey();
            Runnable exec = (Runnable) p.getValue();

            /* si oui, on execute son action */
            if(Arrays.equals(k,cheat)) exec.run();
        }
    }

}
