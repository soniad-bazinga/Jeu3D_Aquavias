import java.io.File;
import java.util.Objects;
import java.util.Scanner;

public class Menu {
    final File folder = new File("levels/");
    Scanner player;

    Menu() throws Exception {
        System.out.println("------ AQUAVIAS ------");
        System.out.println("Bonjour ! Vous êtes prêts à ramener de l'eau ?");
        player = new Scanner(System.in);
        String s = player.next();
        if (s.toLowerCase().equals("oui")) {
            System.out.println("------ AQUAVIAS ------");
            System.out.println("Super ! Choisissez votre niveau !");
            printFolderLevels(folder);
            s = player.next();
            while(!s.matches("-?\\d+(\\.\\d+)?") || Integer.parseInt(s) > numberOfLevels(folder) || Integer.parseInt(s) < 0){
                System.out.println("------ AQUAVIAS ------\n\n");
                System.out.println("Tu me prends pour un guignol ???\n(Excusez le, choisissez un numéro valable s'il vous plait)\n");
                printFolderLevels(folder);
                s = player.next();
            }
            Level play = new Level(Integer.parseInt(s));
            play.play();
        } else if (s.toLowerCase().equals("non")) {
            System.out.println("La fin du monde tel qu'on le connait peut donc commencer.");
        }

    }

    public void printFolderLevels(final File folder) throws Exception {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.getName().equals("id.json")) {
                String strNew = "Level " + fileEntry.getName().replaceAll("level", "");
                System.out.println(strNew.substring(0, strNew.length() - 5));
            }
        }
    }

    int numberOfLevels(final File folder){
        int nb = 0;
        for(final File fileEntry : Objects.requireNonNull(folder.listFiles())){
            if(!fileEntry.getName().equals("id.json")){
                nb+= 1;
            }
        }
        return nb;
    }
}
