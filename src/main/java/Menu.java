import java.io.File;
import java.util.Objects;
import java.util.Scanner;

public class Menu {
    Main main;
    final File folder = new File("levels/");
    Scanner player;
    int selected_option  = 0;
    int selected_level = 0;
    int level_number = 0;
    boolean selecting_level = false;
    boolean closed = false;

    public static final String ANSI_SELECTED = "\u001b[48;5;240m";
    public static final String ANSI_RESET = "\u001B[0m";

    Menu(Main m) throws Exception {
        main = m;
        //afficheStart();
        /*player = new Scanner(System.in);
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
    */
    }

    public void printFolderLevels(final File folder) throws Exception {
        int i = 0;
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.getName().equals("id.json")) {
                if(i == selected_level) System.out.print(ANSI_SELECTED);
                System.out.print("* Level " + i);
                if(i == selected_level) System.out.print(ANSI_RESET);
                System.out.println();
                i++;
            }
        }
        level_number = i;
    }

    void switchSelected(int dir){
        if(selecting_level) return;
        if(dir == 1 && selected_option != 1) selected_option++;
        if(dir == -1 && selected_option != 0) selected_option--;
        afficheStart();
    }

    void switchLevel(int dir){
    	//dir=1 on descend dans le menu, -1 on monte
        if(!selecting_level) return;
        if(dir == 1 && selected_level<(level_number-1)) selected_level++;
        if(dir == -1 && selected_level > 0) selected_level--;
        if(!(dir == -1 && selected_level == -1) && !(dir == 1 && selected_level == level_number))
        afficheLevels();
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

    void afficheStart(){
        clearScreen();
        System.out.println("------ AQUAVIAS ------");
        System.out.println("Bonjour ! Vous êtes prêts à ramener de l'eau ?");
        System.out.print(" ");
        if(selected_option == 0) System.out.print(ANSI_SELECTED);
        System.out.print("oui");
        if(selected_option == 0){
            System.out.print(ANSI_RESET);
            System.out.print(" ");
        }else{
            System.out.print(" ");
            System.out.print(ANSI_SELECTED);
        }
        System.out.print("non");
        System.out.print(ANSI_RESET);
    }

    void afficheLevels(){
        clearScreen();
        System.out.println("------ AQUAVIAS ------");
        System.out.println("Super ! Choisissez votre niveau !");
        try {
            printFolderLevels(folder);
        }catch(Exception e){
            System.out.println("Il y a... dû avoir une erreur?");
            System.exit(0);
        }
    }

    void pressEnter(){
        if(!selecting_level){
            if(selected_option == 1){
                System.out.println("\n\nLa fin du monde tel qu'on le connait peut donc commencer.");
                System.exit(0);
            }
            selecting_level = true;
            afficheLevels();
        }else{
            try {
                closed = true;
                //main.playLevel(selected_level);
            }catch(Exception e){
                System.out.println("Il y a... dû avoir une erreur?");
                System.exit(0);
            }
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    boolean isClosed() { return closed; }

    int getLevel() { return selected_level; }
}
