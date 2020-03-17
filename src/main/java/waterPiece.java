import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class waterPiece extends Group {
    waterTile[][] water;
    static double WATER_SIZE;
    static int col = 0;
    Color[] test = {Color.RED, Color.BLUE, Color.YELLOW};

    public waterPiece(String c, double s){
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

        water = new waterTile[3][3];

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3 ; j++){
                water[i][j] = new waterTile((double) WATER_SIZE/3);
                water[i][j].setTranslateX(WATER_SIZE/3 *(i-1) * 2);
                water[i][j].setTranslateZ(WATER_SIZE/3 * (j-1)* 2);
                water[i][j].setVisible(false);
                this.getChildren().add(water[i][j]);
            }
        }
        //getChildren().add(new Box(WATER_SIZE,WATER_SIZE,WATER_SIZE));
        water[1][1].setVisible(true);
        switch(c){
            case "L" :
                water[0][1].setVisible(true);
                water[1][2].setVisible(true);
                break;
            case "I" :
                water[0][1].setVisible(true);
                water[2][1].setVisible(true);
                break;
            case "T" :
                water[1][0].setVisible(true);
                water[1][2].setVisible(true);
                water[2][1].setVisible(true);
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

        boolean tmp1 = water[0][1].isVisible();
        boolean tmp2 = water[1][2].isVisible();
        water[0][1].setVisible(water[1][0].isVisible());
        water[1][2].setVisible(tmp1);
        tmp1 = water[2][1].isVisible();
        water[2][1].setVisible(tmp2);
        water[1][0].setVisible(tmp1);

    }
    public class waterGrid extends Group{
        double WATER_GRID_SIZE;
        int WATER_GRID_LENGTH;
        double WATER_TILE_SIZE;
        waterTile[][] tiles;
        int x_progression,y_progression;


        public waterGrid(double w){
            this.getChildren().add(new Box(w,w,w));
            /*WATER_GRID_LENGTH = w; //Le nombre de sous divisions d'une waterTile
            WATER_GRID_SIZE = WATER_SIZE/3; //une water grid est divisé en 3 (comme reprensenté en haut)
            WATER_TILE_SIZE = WATER_GRID_SIZE/WATER_GRID_LENGTH; // la taille d'une tile, soit la taille de la gride / le nombre d'elements
            x_progression = 0;
            y_progression = 0;
            tiles = new waterTile[WATER_GRID_LENGTH][WATER_GRID_LENGTH];
            for(int i = 0; i < WATER_GRID_LENGTH ; i++){
                for(int j = 0 ; j < WATER_GRID_LENGTH ; j++){
                    tiles[i][j] = new waterTile(WATER_TILE_SIZE,c);
                    tiles[i][j].setTranslateX(WATER_TILE_SIZE*(i-1)*2);
                    tiles[i][j].setTranslateZ(WATER_TILE_SIZE*(j-1)*2);
                    getChildren().add(tiles[i][j]);

                }
            }*/
        }

        void reset(){
            x_progression = 0;
            y_progression = 0;
            for(int i = 0; i < WATER_GRID_LENGTH; i++){
                for(int j = 0 ; j < WATER_GRID_LENGTH; j++){
                    tiles[i][j].setVisible(false);
                }
            }
        }

        void setFull(){
            x_progression = WATER_GRID_LENGTH;
            y_progression = WATER_GRID_LENGTH;
            for(int i = 0; i < WATER_GRID_LENGTH; i++){
                for(int j = 0 ; j < WATER_GRID_LENGTH; j++){
                    tiles[i][j].setVisible(true);
                }
            }
        }

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
            material.setDiffuseColor(new Color(.61,.80,.87,.8));
            material.setSpecularColor(Color.AQUAMARINE);
            setMaterial(material);
        }
    }
}
