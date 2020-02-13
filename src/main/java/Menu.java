import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

public class Menu {
    Scanner player;
    Menu(){
        System.out.println("------ AQUAVIAS ------");
        System.out.println("Bonjour ! Vous êtes prêts à ramener de l'eau ?");
        player = new Scanner(System.in);
        String s = player.next();
        if(s.toLowerCase().equals("oui")){
            System.out.println("Super ! Choisissez votre niveau !");
            FileReader levels = new FileReader()
        }
        else if(s.toLowerCase().equals("non")){
            System.out.println("La fin du monde tel qu'on le connait peut donc commencer.");
        }

    }
}
