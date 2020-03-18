import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.util.Duration;

public class waterPiece extends Group {
    PieceOverview overview;
    waterGrid[][] water;
    static double WATER_SIZE;
    static int col = 0;
    int x,y;

    public waterPiece(String c, double s, PieceOverview p, int x, int y){
        this.x = x; this. y = y;
        overview = p;
        /*
        water est un tableau de 3*3
        dont les cases representant les coins
        [ (0;0), (0;2), (2;0), (2;2) ]
        sont inutilisées
        la case du centre est toujours pleine car l'eau passe toujours par là
        le reste des cases dépendent de la forme et de la rotation
        par exemple une piece L sera comme ça :
        le tableau est toujours rempli de waterTile
        elle seront juste affichés (ou non)

        [.][x][.]
        [ ][x][x]
        [.][ ][.]

        */

        WATER_SIZE = s;

        water = new waterGrid[3][3];

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3 ; j++){
                water[i][j] = new waterGrid(3);
                water[i][j].setTranslateX(WATER_SIZE/3 *(i-1) * 2);
                water[i][j].setTranslateZ(WATER_SIZE/3 * (j-1)* 2);
                this.getChildren().add(water[i][j]);
            }
        }
        //getChildren().add(new Box(WATER_SIZE,WATER_SIZE,WATER_SIZE));
        water[1][1].setPass(true);
        switch(c){
            case "L" :
                water[0][1].setPass(true);
                water[1][2].setPass(true);
                break;
            case "I" :
                water[0][1].setPass(true);
                water[2][1].setPass(true);
                break;
            case "T" :
                water[1][0].setPass(true);
                water[1][2].setPass(true);
                water[2][1].setPass(true);
                break;
            default:
                /* piece en X mais j'ai la flemme */
                break;
        }
    }

    void rotate(){
        /*
                   UP
             [0;0][0;1][0;2]
        LEFT [1;0][1;1][1;2] RIGHT
             [2;0][2;1][2;2]
                  DOWN
         */

        boolean tmp1 = water[0][1].isFull();
        boolean tmp2 = water[1][2].isFull();
        water[0][1].setFull(water[1][0].isFull());
        water[1][2].setFull(tmp1);
        tmp1 = water[2][1].isFull();
        water[2][1].setFull(tmp2);
        water[1][0].setFull(tmp1);
    }

    boolean isFull(){
        for(int i = 0 ; i < 3 ; i ++){
            for(int  j = 0 ; j < 3 ; j++){
                if(water[i][j].canPass() && !water[i][j].isFull()) return false;
            }
        }
        return true;
    }


    void flow(int i, int j){
        water[i][j].setFull(true);
        Timeline test = new Timeline(new KeyFrame(Duration.seconds(1), event ->{
            if(inTab(i+1,j) && water[i+1][j].canPass() && !water[i+1][j].isFull()) flow(i+1,j);
            if(inTab(i-1,j) && water[i-1][j].canPass() && !water[i-1][j].isFull()) flow(i-1,j);
            if(inTab(i,j+1) && water[i][j+1].canPass() && !water[i][j+1].isFull()) flow(i,j+1);
            if(inTab(i,j-1) && water[i][j-1].canPass() && !water[i][j-1].isFull()) flow(i,j-1);
        }));
        test.setCycleCount(1);
        test.play();
        if(isFull()) overview.flow(x,y);
    }

    boolean inTab(int i, int j){
        return(i>=0 && i<3 && j>=0 && j<3);
    }

    public class waterGrid extends Group{
        double WATER_GRID_SIZE;
        int WATER_GRID_LENGTH;
        double WATER_TILE_SIZE;
        waterTile[][] tiles;
        int x_progression,y_progression;
        boolean full;
        boolean pass;


        public waterGrid(int w){
            full = false;
            WATER_GRID_LENGTH = w; //Le nombre de sous divisions d'une waterTile
            WATER_GRID_SIZE = WATER_SIZE/3; //une water grid est divisé en 3 (comme reprensenté en haut)
            WATER_TILE_SIZE = WATER_GRID_SIZE/WATER_GRID_LENGTH; // la taille d'une tile, soit la taille de la gride / le nombre d'elements
            /*System.out.println("WATER_GRID_LENGTH : "+WATER_GRID_LENGTH+
                    "\nWATER_GRID_SIZE :"+WATER_GRID_SIZE+
                    "\nWATER_TILE_SIZE :"+WATER_TILE_SIZE); */
            x_progression = 0;
            y_progression = 0;
            tiles = new waterTile[WATER_GRID_LENGTH][WATER_GRID_LENGTH];
            for(int i = 0; i < WATER_GRID_LENGTH ; i++){
                for(int j = 0 ; j < WATER_GRID_LENGTH ; j++){
                    tiles[i][j] = new waterTile(WATER_TILE_SIZE);
                    tiles[i][j].setTranslateX(WATER_TILE_SIZE*(i-1)*2);
                    tiles[i][j].setTranslateZ(WATER_TILE_SIZE*(j-1)*2);
                    getChildren().add(tiles[i][j]);

                }
            }
        }

        void setPass(boolean b){ pass = b; }
        boolean canPass(){ return pass; }

        void reset(){
            x_progression = 0;
            y_progression = 0;
            for(int i = 0; i < WATER_GRID_LENGTH; i++){
                for(int j = 0 ; j < WATER_GRID_LENGTH; j++){
                    tiles[i][j].setVisible(false);
                }
            }
        }

        void setFull(boolean b){
            full = b;
            x_progression = WATER_GRID_LENGTH;
            y_progression = WATER_GRID_LENGTH;
            for(int i = 0; i < WATER_GRID_LENGTH; i++){
                for(int j = 0 ; j < WATER_GRID_LENGTH; j++){
                    tiles[i][j].setVisible(b);
                }
            }
        }

        boolean isFull(){ return full; }

        void flow_x(boolean forward){
            /* On créer un semblant d'ecoulement de l'eau :
                On déplace l'eau par collonne
             */
            if(x_progression > 0 && x_progression < WATER_GRID_SIZE){
                for(int i = 0; i < WATER_GRID_SIZE ; i++){
                    if(forward) {
                        tiles[x_progression + 1][i].setVisible(true);
                    }else{
                        tiles[x_progression][i].setVisible(false);
                    }
                }
            }
        }

        void flow_y(boolean forward){
            if(y_progression > 0 && y_progression < WATER_GRID_SIZE){
                for(int i = 0; i < WATER_GRID_SIZE ; i++){
                    if(forward) {
                        tiles[i][y_progression+1].setVisible(true);
                    }else{
                        tiles[i][y_progression].setVisible(false);
                    }
                }
            }
        }

    }

    public class waterTile extends Box {
        waterTile(double size){
            setVisible(false);
            setScaleX(size);
            setScaleY(.5);
            setScaleZ(size);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(new Color(.61,.80,.87,.9));
            material.setSpecularColor(Color.AQUAMARINE);
            setMaterial(material);
        }
    }
}
