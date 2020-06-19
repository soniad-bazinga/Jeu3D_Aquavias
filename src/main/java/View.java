import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class View extends Scene{

    /* On definit la taille */
    static final int WIDTH = 1280;
    static final int HEIGHT = 720;

    static final double PIECE_SIZE = 2;

    static int rotateTime = 200;

    AnchorPane globalRoot;

    /* ses nouveaux attributs pour afficher le level */
    static Level level;

    /* une matrice de piece3D (des groupes de mesh) */
    static Piece3D[][] models;

    static Piece3D tableau;
    static Piece3D numModel2;
    static Piece3D numModel1;

    Group root3D;

    static Text compteur;

    /* une matrice de carrés bleus representant l'eau */
    waterPiece[][] waterPieces;

    /* La pile des ajouts de waterTile */
    ArrayList<Coordonnes> pile = new ArrayList<Coordonnes>();

    MenuApplication menu;

    boolean paused = false;

    boolean loading = false;

    AnchorPane pauseMenu;
    Map<String, Pane> pauseMenuWindows = new HashMap<String, Pane>();

    /*Panneau de fin de niveau*/
    static LevelEnd fin;

    /*En cas de niveau à temps*/
    Clock timer;

    /* Créé un aperçu de piece */
    public View(Level level, MenuApplication menu){
        super(new Group(), 1280, 720, true);
        level.new_update();
        this.menu = menu;
        setUp(level);
    }

    public View() {
        super(new Group(), 1280, 720, true);
    }

    public void setUp(Level level){

        this.level = level;

        level.setOverviewer(this);

        globalRoot = new AnchorPane();

        StackPane stack = new StackPane();

        globalRoot.getChildren().add(stack);

        //Scene scene = new Scene(globalRoot, 1280,720,true);
        this.setRoot(globalRoot);

        /* On crée une caméra qui pointe vers 0,0 (true) et la recule sur l'axe Z */
        PerspectiveCamera camera  = new PerspectiveCamera(true);

        /* on appelle l'initalisateur de caméra */
        initalizeCamera(camera);

        /* on définit sa distance de rendu */
        camera.setFarClip(1000);

        /* On importe le model de la piece */
        root3D = new Group();


        /* On initialise nos deux tableaus */
        /* waterPieces représente les carrés d'eau */
        waterPieces = new waterPiece[level.pieces.length][level.pieces[0].length];
        /* models les modèles de pièces */
        models = new Piece3D[level.pieces.length][level.pieces[0].length];

        tableau= new Piece3D();

        numModel2= new Piece3D();
        numModel1= new Piece3D();

        addTableau();

        /*On appelle l'initalisateur de ces tableaux */
        initalizeBoards(root3D);

        /* La scene, avec root, la taille et le depthbuffered activée */
        /* le depthbuffer permet de ne pas avoir toutes les faces de pièces visible en même temps */
        SubScene subScene = new SubScene(root3D, 1280, 720, true, SceneAntialiasing.BALANCED);

        /* On lui lie la caméra */
        subScene.setCamera(camera);

        globalRoot.getChildren().add(subScene);

        /* On gère maintenant les cliques de souris */
        subScene.setOnMousePressed((MouseEvent me) -> {

            /* on récupère le résultat du click */
            PickResult pr = me.getPickResult();

            /* Si il est non nul alors */
            if(pr!=null && pr.getIntersectedNode() != null){

                if(pr.getIntersectedNode() instanceof waterPiece.waterTile){
                    /*
                        On a cliqué sur l'eau
                        une waterTile possède des coordonées, utilisons les !
                     */
                    waterPiece.waterTile p = (waterPiece.waterTile) pr.getIntersectedNode();
                    rotate(p.getX(),p.getY());
                }else {
                    /* Sinon, on recupère le Node associé */
                    Node mv = (MeshView) pr.getIntersectedNode();

                    /* puis on récupère ses coordonnées sur l'axe x et y */
                /*
                    (On récupère les coordonnées dans le plan, auxquels on ajoute la taille de la piece / 2 (la moitiée
                    de pièce qui "dépasse" au début, et qu'on divise par la taille de la pièce pour obtenir
                    leurs coordonnées sur le tableau level
                 */
                    int x_coord = (int) Math.round((mv.localToScene(mv.getBoundsInLocal()).getMinX() + PIECE_SIZE / 2) / PIECE_SIZE);
                    int y_coord = (int) Math.round((mv.localToScene(mv.getBoundsInLocal()).getMinZ() + PIECE_SIZE / 2) / PIECE_SIZE);

                    /* on rotate le tout */
                    rotate(x_coord, y_coord);
                }
            }
        });

        /* on rend la root invisible pour la transition */
        globalRoot.setOpacity(0);

        /* puis on démarre l'ecoulement de l'eau */
        start_water();

        /* on initalise le menu de pause */
        initializePauseMenu();

        /* et on le rend invisible */
        pauseMenu.setVisible(false);

        /* si jamais on appuis sur echap, on appel pause() */
        setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE){
                pause();
            }
        });

        globalRoot.getChildren().add(pauseMenu);

        if (level.type =='f'){ //Si le niveau est de type f, on ajoute une Clock pour le timer
            timer = new Clock(level.compteur);
            globalRoot.getChildren().add(timer);
        }
    }

    /* gère le menu de pause */
    void pause(){
        /* si on est dans un chargement, ne rien faire */
        if(loading) return;

        /* on inverse la visibilité du menu (visible => !visible) */
        pauseMenu.setVisible(!pauseMenu.isVisible());
        paused = pauseMenu.isVisible();

        /* si jamais on sort du menu pause */
        if(!paused){
            /* si la pile est non vide, on rappel la fonction d'ecoulement sur le dernier en date */
            if(!pile.isEmpty()){
                waterPiece wp = waterPieces[pile.get(0).getI()][pile.get(0).getJ()];
                wp.flow(wp.lastFlowX,wp.lastFlowY);
            }else{
                /* sinon depuis la source */
                waterPieces[0][0].flow(1,0);
            }
        }else{
            /* si on est dans le menu de pause */
            pauseShow("pause");
        }
    }

    void initializePauseMenu(){
        pauseMenu = new AnchorPane();

        /* le fond du menu pause, un rectangle noir légerement opaque */
        Rectangle menuBackground = new Rectangle(WIDTH,HEIGHT);

        menuBackground.setOpacity(.6);

        pauseMenu.getChildren().add(menuBackground);

        /* on ajoute un petit titre */
        Text title = new Text("Niveau "+(level.ID+1));
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Roboto", 60));

        pauseMenu.getChildren().add(title);

        title.setLayoutX((WIDTH - title.getLayoutBounds().getWidth())/2);
        title.setLayoutY(HEIGHT/3);

        /* le groupe qui contient les boutons de selection */
        VBox pMenuElements = new VBox(15);

        pauseMenu.getChildren().add(pMenuElements);

        /* le bouton de retour pour sortir de la pause */
        MenuItems retour = new MenuItems("Retour");

        retour.setOnAction(new Runnable() {
            public void run(){
                pause();
            }
        });

        /* le bouton pour revenir au menu principal */
        MenuItems leave = new MenuItems("Revenir au menu principal");

        leave.setOnAction(new Runnable(){
            public void run(){
                /* on affiche la fenetre de sortie */
                pauseShow("exit");
            }
        });

        /* le bouton d'options */
        MenuItems options = new MenuItems("Options");

        options.setOnAction(new Runnable() {
            @Override
            public void run() {
                /* on affiche la fenetre de réglages */
                pauseShow("settings");
            }
        });

        pMenuElements.getChildren().addAll(options,leave,retour);

        /* puis on le met au milieu de l'ecran */
        pMenuElements.setTranslateX(WIDTH / 2.0 - 100.0);
        pMenuElements.setTranslateY(HEIGHT / 3.0 + 50.0);

        /* on l'ajoute en tant que clé "pause" et valeur lui même */
        pauseMenuWindows.put("pause",pMenuElements);

        initializeExitPause();
        initializeSettingsPause();

    }

    /* on initialise le menu de sorti de niveau */
    void initializeExitPause(){
        GridPane confirmation = new GridPane();

        confirmation.setHgap(25);
        confirmation.setVgap(20);

        /* les contraintes de colonnes, 2 colonnes de 200px */
        ColumnConstraints c1 = new ColumnConstraints(200);
        ColumnConstraints c2 = new ColumnConstraints(200);
        confirmation.getColumnConstraints().addAll(c1,c2);

        /* le message */
        Label t = new Label("Voulez vous vraiment quitter le niveau?");

        /* affiché en blanc */
        t.setTextFill(Color.WHITE);

        /* le bouton "oui", faisant alors quitter le niveau */
        MenuItems yes = new MenuItems("Oui");
        yes.setOnAction(new Runnable() {
            @Override
            public void run() {
                fadeOut(EventHandler ->{
                    menu.fadeIn();
                });
            }
        });

        /* le bouton "non", retournant sur le menu précedent */
        MenuItems no = new MenuItems("Non");
        no.setOnAction(new Runnable() {
            @Override
            public void run() {
                pauseShow("pause");
            }
        });

        /* on les ajoutes proprements */
        confirmation.add(t,0,0,2,1);
        GridPane.setHalignment(t, HPos.CENTER);
        confirmation.add(yes,0,1,1,1);
        confirmation.add(no,1,1,1,1);

        pauseMenu.getChildren().add(confirmation);

        /* on l'ajoute en tant que clé "exit" et valeur lui même */
        pauseMenuWindows.put("exit",confirmation);

        confirmation.setTranslateX(WIDTH * 2);
        confirmation.setTranslateY(HEIGHT * 2);
    }

    void pauseShow(String s){
        for(Map.Entry<String, Pane> p : pauseMenuWindows.entrySet()){
            /* on les positionne en dehors de l'écran */
            p.getValue().setTranslateX(WIDTH * 2);
            p.getValue().setTranslateY(HEIGHT * 2);
        }

        /* on affiche la pane de clé s au milieu */
        Pane p = pauseMenuWindows.get(s);

        p.setTranslateX((WIDTH - p.getWidth())/2);
        p.setTranslateY((HEIGHT - p.getHeight())/2);

    }

    void initializeSettingsPause(){
        settingsMenu s = new settingsMenu(menu.getMediaPlayer(), Color.WHITE);

        s.setTranslateX(WIDTH * 2);
        s.setTranslateY(HEIGHT * 2);

        Runnable r = new Runnable(){

            public void run(){
                pauseShow("pause");
                s.updateValues();
            }
        };

        s.setRetourAction(r);

        pauseMenu.getChildren().add(s);

        pauseMenuWindows.put("settings",s);
    }

    void initalizeCamera(Camera camera){
        /* Encore à modifier, il ne s'adapte pas bien à tout types de niveaux */
        //On place l'origine x à la moitié de la largeur (qui est en fait la hauteur) du niveau
        //On ajoute 1 car l'origine est initialisé à 0

        camera.setTranslateX(PIECE_SIZE*level.HEIGHT/2);
        camera.setTranslateY(-PIECE_SIZE/2);
        //camera.setTranslateZ(-(level.HEIGHT * PIECE_SIZE *2));
        camera.setTranslateZ(-(level.HEIGHT+level.WIDTH+2)*2/2);


        camera.getTransforms().add(new Rotate(-55, Rotate.X_AXIS));
        camera.getTransforms().add(new Rotate(-35, Rotate.Y_AXIS));
        camera.getTransforms().add(new Rotate(-35, Rotate.Z_AXIS));

        /*
        camera.setLayoutX(-rotation-level.WIDTH);
        camera.setLayoutY(rotation+level.HEIGHT/2);*/
        camera.setLayoutX((level.HEIGHT+level.WIDTH)*38/18.9);
        camera.setLayoutY((level.HEIGHT+level.WIDTH)*-43/18.9);

    }

    void initalizeBoards(Group root){
        /* On recopie à l'identique le niveau en 3d */
        /* on parcours le tableau */
        for(int i = 0 ; i < models.length ; i++) {
            for(int j = 0 ; j < models[i].length ; j++) {
                if(level.pieces[i][j] == null) continue;
                /* chaque modèle une pièce 3D */
                models[i][j] = new Piece3D();
                /* On importe selon le type de pièce */
                models[i][j].importModel("model_test/piece"+level.pieces[i][j].getType()+".obj");
                /* on place la pièce en coordonnées [i;j] (décale de la taille d'une pièce) */
                models[i][j].setTranslateX(PIECE_SIZE * i);
                models[i][j].setTranslateY(0);
                models[i][j].setTranslateZ(PIECE_SIZE * j);

                /* on créé le type de pièce correspondant */
                switch(level.pieces[i][j].getType()){
                    case("L") : waterPieces[i][j] = new waterPieceL(PIECE_SIZE/2,this,i,j); break;
                    case("T") : waterPieces[i][j] = new waterPieceT(PIECE_SIZE/2,this,i,j); break;
                    case("I") : waterPieces[i][j] = new waterPieceI(PIECE_SIZE/2,this,i,j); break;
                    case("X") : waterPieces[i][j] = new waterPieceX(PIECE_SIZE/2,this,i,j); break;
                }
                /* on place l'eau au même coordonnées que les modèles */
                waterPieces[i][j].setTranslateX(PIECE_SIZE * i);

                /* arbitraire, dépend du type de pièce, règle la hauteur de l'eau */
                waterPieces[i][j].setTranslateY(-1.2);
                waterPieces[i][j].setTranslateZ((PIECE_SIZE * j));

                /* si la pièce n'est pas vide, on la vide d'eau dans la vue */
                if(!level.pieces[i][j].isFull()) waterPieces[i][j].setFull(false);

                /* on les tournes comme il se doit :) */
                for(int r = 0 ; r < level.pieces[i][j].getRotation() ; r++){
                    waterPieces[i][j].rotate();
                    models[i][j].getTransforms().add(new Rotate(90,Rotate.Y_AXIS));
                }
                /*
                    PROBLEME LORS DE LA CREATION DE PIECE L
                    ELLE DOIT ETRE TOURNE 2* DANS LA VUE, POUR ETRE EN ACCORD AVEC LE JEU
                    CE N'EST QUE TEMPORAIRE
                 */
                if(level.pieces[i][j].getType() == "L"){
                    for(int r = 0 ; r < 3 ; r++)  models[i][j].getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
                }
                /* puis on les ajoutes a root */
                root.getChildren().add(waterPieces[i][j]);
                root.getChildren().add(models[i][j]);
            }
        }
    }

    void addTableau() {
        if(level.type != 'f') { //Si le niveau est de type f, il n'est pas nécessaire de rajouter le compteur de coups

            tableau.importModel("model_test/tableau.obj");  //on ajoute le modèle 3D du tableau d'affichage

            //apply the wood texture


            tableau.setTranslateX(-6);
            tableau.setTranslateY(-1);
            tableau.setTranslateZ(10);

            tableau.setScaleX(40);
            tableau.setScaleY(40);
            tableau.setScaleZ(40);

            tableau.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
            root3D.getChildren().add(tableau);

            numModel2.getTransforms().add(new Rotate(272,Rotate.Y_AXIS));  //on initialise la rotation des modèles 3D des chiffres affichés sur le tableau
            numModel1.getTransforms().add(new Rotate(272,Rotate.Y_AXIS));
            addNumberModels(level.compteur);  //on affiche le nombre de coups au départ

        }
    }

    void addNumberModels(int num ){ //charge les modèles 3D des nombres qui seront affichés sur le tableau
        //ils représentent le nombre de coup qu'il reste à faire

        int num1= num%10;
        int num2= num/10;

        if(num <10){
            numModel2.setVisible(false);
            numModel2.importModel("model_test/0.2.obj"); //valeur par défaut sinon on a une nullPointer error  b

            numModel1.importModel("model_test/"+num1+".2.obj");
            numModel1.setTranslateX(-5.6);
            numModel1.setTranslateY(-1.8);
            numModel1.setTranslateZ(10);

            numModel1.setScaleX(3.3);
            numModel1.setScaleY(3.3);
            numModel1.setScaleZ(3.3);
        }else {


            //unité

            numModel1.importModel("model_test/" + num1 + ".obj");
            numModel1.setTranslateX(-5.6);
            numModel1.setTranslateY(-1.8);
            numModel1.setTranslateZ(10.7);

            numModel1.setScaleX(3.3);
            numModel1.setScaleY(3.3);
            numModel1.setScaleZ(3.3);

            //dizaine

            numModel2.importModel("model_test/" + num2 + ".obj");
            numModel2.setTranslateX(-5.6);
            numModel2.setTranslateY(-1.8);
            numModel2.setTranslateZ(9.3);

            numModel2.setScaleX(3.3);
            numModel2.setScaleY(3.3);
            numModel2.setScaleZ(3.3);
        }

        root3D.getChildren().addAll(numModel1, numModel2);

    }

    void fadeIn(){
        loading = true;
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(1000));
        fade.setNode(globalRoot);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setOnFinished(EventHandler ->{
            loading = false;
        });
        fade.play();
    }

    /* L'EventHandler permet de specifier l'action de fin d'animation */
    void fadeOut(EventHandler e){
        loading = true;
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(1000));
        fade.setNode(globalRoot);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e);
        fade.play();
    }

    void win(){
        loading = true;
        //Si le niveau est de type f (avec un timer)
        if(level.type == 'f'){
            if(timer.tmp <= 0){ //On vérifie que le timer est bien arrivé à la fin
                fin = new LevelEnd('d'); //Si oui, c'est la version défaite que l'on appel alors
                globalRoot.getChildren().add(fin);
                return;
            } else if (level.estFinie(false)){ //Sinon, on vérifie seulement que le jeu soit terminé
                fin = new LevelEnd('v');//Pour envoyer la version victoire
                globalRoot.getChildren().add(fin);
                return;
            }
        }

        else if(level.estFinie(false) && level.type != 'f') { //Autrement, (dans les deux autres cas de niveau possible
            if (level.Victory()) fin = new LevelEnd('v'); //Si la partie est gagnée, on envoie la version victoire
            else fin = new LevelEnd('d'); //Sinon, la version défaite
            globalRoot.getChildren().add(fin);
            return;
        }
    }

    void rotate(int x,int y){
        //Si la partie est finie, la rotation ne fonctionne plus

        //au cas où on clique sur un élément du décor
        if(!level.isInTab(x,y)) return;


        /* Si la rotation n'est pas finie, on peut pas en commencer une autre */
        if(models[x][y].getRotate() % 90 != 0) return;

        /* Si c'est la première piece, on ne peut pas la tourner */
        if(x == 0 && y == 0) return;

        /* on rotate le jeu, les pièces, et les pièces d'eau */
        /* on commence par la tourner dans le modèle */
        level.new_rotate(x,y);

        /* puis dans la vue */
        /* sur les modèles avec l'animation */
        RotateTransition rt= new RotateTransition(Duration.millis(rotateTime), models[x][y]);
        rt.setAxis(Rotate.Y_AXIS);
        rt.setByAngle(90);
        rt.setCycleCount(1);
        rt.setAutoReverse(true);
        rt.play();

        /* puis les pieces d'eau */
        waterPieces[x][y].rotate();
        /*
            on va ensuite vider chaque pièce pleine ajouté (durant la
            propagation de l'eau) APRES la pièce que l'ont vient
            de tourner
         */
        if(pileContains(x,y)) {
            while (!pile.isEmpty() && (pile.get(0).getI() != x || pile.get(0).getJ() != y)) {
                waterPieces[pile.get(0).getI()][pile.get(0).getJ()].setFull(false);
                waterPieces[pile.get(0).getI()][pile.get(0).getJ()].flowing = false;
                pile.remove(0);
            }
            /* si la pile n'est pas vide, on enlève aussi la piece qu'on vient de tourner */
            if (!pile.isEmpty()) {
                waterPieces[pile.get(0).getI()][pile.get(0).getJ()].setFull(false);
                waterPieces[pile.get(0).getI()][pile.get(0).getJ()].flowing = false;
                pile.remove(0);
            }
        }
        /*
            Si la pile n'est pas vide, on part de la pièce au sommet de la pile
            Sinon on repart de la première piece
         */
        /* On attend la fin de l'animation avant de relancer la fonction d'écoulement */
        Timeline wait = new Timeline(new KeyFrame(Duration.millis(rotateTime * 2), event ->{
            if (!pile.isEmpty()) {
                flow(pile.get(0).getI(), pile.get(0).getJ());
            } else {
                flow(0, 0);
            }
        }));
        wait.setCycleCount(1);
        wait.play();

        /* on update dans le modèle */

        if(level.type != 'f'){
            changeNumer();        //changer les nombre sur le tableau d'affichage du compteur
        }

        level.new_update();
        level.affiche();
    }

    void changeNumer(){  //on doit vider d'abord pour remplir à nouveau avec les nouveaux models 3D
        root3D.getChildren().remove(numModel1);
        root3D.getChildren().remove(numModel2);
        numModel2.closeModel();
        numModel1.closeModel();
        addNumberModels(level.compteur);
    }

    void start_water(){
        /* on lance la fonction a la case 0 */
        waterPieces[0][0].flow(1,0);
    }

    /*
        droite : [1;0]
        gauche : [1;2]
        bas : [0;1]
        haut : [2;1]

     */

    /* sur la même base qu'update */
    void flow(int i,int j){
        if(!isWaterPieceFull(i,j) || paused) return;
        if(i == level.HEIGHT - 1 && j == level.WIDTH + 1 || level.compteur <= 0) win(); //si l'eau à atteint la fin
        /*
            Cette fonction marche de la manière suivante :
            - Elle regarde si elle est connectées aux pièces d'a côté (comme sur level)
            - Verifie sur dans le modèle elle est pleine (donc il faut la remplir dans la vue
            - Dis à la pièce d'effectuer sa fonction d'écoulement progressif
         */
        /* On ajoute chaque nouvel piece */
        if (level.isInTab(i + 1, j) && level.connected(level.pieces[i][j], level.pieces[i + 1][j], "DOWN") && !waterPieces[i + 1][j].isFull()){
            pile.add(0,new Coordonnes(i+1,j));
            waterPieces[i+1][j].flowing  = true;
            waterPieces[i+1][j].flow(0,1);
        }
        if (level.isInTab(i - 1, j) && level.connected(level.pieces[i][j], level.pieces[i - 1][j], "UP") && !waterPieces[i - 1][j].isFull()){
            pile.add(0,new Coordonnes(i-1,j));
            waterPieces[i-1][j].flowing = true;
            waterPieces[i-1][j].flow(2,1);
        }
        if (level.isInTab(i, j + 1) && level.connected(level.pieces[i][j], level.pieces[i][j + 1], "RIGHT") && !waterPieces[i][j + 1].isFull()){
            pile.add(0,new Coordonnes(i,j+1));
            waterPieces[i][j+1].flowing = true;
            waterPieces[i][j+1].flow(1,0);
        }
        if (level.isInTab(i, j-1) && level.connected(level.pieces[i][j], level.pieces[i][j - 1], "LEFT") && !waterPieces[i][j - 1].isFull()){
            pile.add(0,new Coordonnes(i,j-1));
            waterPieces[i][j-1].flowing = true;
            waterPieces[i][j-1].flow(1,2);
        }
    }

    /* Pour vérifier si la pile contient la pièce de Coordoonées x;y */
    boolean pileContains(int x, int y){
        for(Coordonnes c : pile){
            if(x == c.getI() && y == c.getJ()) return true;
        }
        return false;
    }

    boolean isLevelFull(int x, int y){
        return level.isFull(x,y);
    }

    boolean isWaterPieceFull(int x, int y){
        return waterPieces[x][y].isFull();
    }

    boolean isPaused(){ return paused; }

    /* Rempli/Vide la pièce de coordoonées i;j */
    void setFull(int i, int j, boolean b){
        /* on l'active ou désactive */
        waterPieces[i][j].setFull(b);
    }

    /* Permet d'importer une pièce via une URL */

    private static class Piece3D extends Group{

        private ObjModelImporter objModelImporter;
        public void importModel(String url){

            /* On utilise l'API d'import de modelobj pour javafx */
            /* on créer un objet vide */
            objModelImporter = new ObjModelImporter();

            /* et on utilise la fonction read sur l'url */
            objModelImporter.read(url);

            /* puis pour chaque meshview dans l'objModel, on l'ajoute a modelRoot (le groupe) */
            for(MeshView view : objModelImporter.getImport()){
                this.getChildren().addAll(view);
            }

        }

        public void closeModel(){
            for(MeshView view: objModelImporter.getImport()){
                this.getChildren().removeAll(view);
            }
            objModelImporter.close();
        }

    }

    /* Couples de valeurs, représentant des coordonnées */
    private class Coordonnes{
        int i;
        int j;

        public Coordonnes(int i, int j){
            this.i = i;
            this.j = j;
        }

        int getI(){ return i;}
        int getJ(){ return j;}

        waterPiece getPiece(){ return waterPieces[i][j]; }
    }

    //Cette classe gère dans sa quasi totalité la séquence de fin de niveau
    class LevelEnd extends Group {
        StackPane boite;
        Rectangle bluebox;

        public LevelEnd(char g){ //Le constructeur a besoin de la longueur de la fenètre pour placer la boite
            //mais également d'une variable pour savoir si la partie est gagnée ou non

            Rectangle behind = new Rectangle(WIDTH, HEIGHT);
            behind.setFill(Paint.valueOf("BLACK"));
            behind.setOpacity(0.6);
            behind.setX(0);
            behind.setY(0);

            getChildren().add(behind);

            boite = new StackPane();
            boite.setLayoutX(340);
            boite.setLayoutY(210);

            bluebox = new Rectangle(600, 300);

            bluebox.setArcHeight(15);
            bluebox.setArcWidth(15);

            bluebox.setFill(new ImagePattern(new Image(new File("img/pausemenu.png").toURI().toString())));
            bluebox.setStroke(Paint.valueOf("GREY"));

            boite.getChildren().add(bluebox);

            addButtons(g);

            Text status = new Text();

            if(g == 'v'){
                status.setText("Victoire !");
            }else{
                status.setText("Perdu...");
            }

            /* on ajoute le texte de status (victoire/défaite) */
            status.setFill(Color.WHITE);
            status.setFont(Font.font("Roboto", 60));

            status.setLayoutX((WIDTH - status.getLayoutBounds().getWidth())/2);
            status.setLayoutY(HEIGHT/2.5);

            getChildren().addAll(boite,status);

        }

        public void addButtons(char win){

            HBox hboite = new HBox(10);

            if (win == 'v') {

                Button suivant = new Button("Niveau suivant");
                suivant.setOnAction(e -> {
                    fadeOut(EventHandler -> {
                        try {
                            menu.nextLevel(level.ID);
                        } catch (Exception exception) {
                            System.out.println("Bravo ! vous avez terminé le jeu brave puiseur");
                        }
                    });
                });

                hboite.getChildren().add(suivant);
            }

            Button replay = new Button("Rejouer");
            replay.setOnAction(e -> {
                try {
                    fadeOut(EventHandler -> {
                        try {
                            menu.fadeOut(new Level(level.ID));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });

            Button quit = new Button("Retour au menu");
            quit.setOnAction(e -> {
                fadeOut(EventHandler ->{
                    if(win == 'v'){
                        menu.incrementeMax();
                        menu.updateLastPlayed(level.ID + 1);
                    }
                    menu.fadeIn();
                });
            });
            hboite.setAlignment(Pos.CENTER);

            hboite.getChildren().addAll(replay, quit);

            boite.getChildren().add(hboite);
        }
    }
}
