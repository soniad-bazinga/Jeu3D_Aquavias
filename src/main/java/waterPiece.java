import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.util.Duration;

public abstract class waterPiece extends Group {
    View view;
    waterGrid[][] water;
    static double waterSize;
    static double waitTime = .3f;
    int x,y;
    boolean flowing = true;

    int lastFlowX;
    int lastFlowY;

    public waterPiece(double size, View p, int x, int y){
        this.x = x; this. y = y;
        view = p;
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

        waterSize = size;

        setVisible(true);

        water = new waterGrid[3][3];

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3 ; j++){
                water[i][j] = new waterGrid(waterSize/3);
                water[i][j].setTranslateX(waterSize/3 *(i-1) * 2);
                water[i][j].setTranslateZ(waterSize/3 * (j-1)* 2);
                water[i][j].setFull(false);
                this.getChildren().add(water[i][j]);
            }
        }
        water[1][1].setPass(true);
    }

    void rotate(){
        /*
                   UP
             [0;0][0;1][0;2]
        LEFT [1;0][1;1][1;2] RIGHT
             [2;0][2;1][2;2]
                  DOWN
         */
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++) water[i][j].setFull(false);

        boolean tmp1 = water[0][1].canPass();
        boolean tmp2 = water[1][2].canPass();
        water[0][1].setPass(water[1][0].canPass());
        water[1][2].setPass(tmp1);
        tmp1 = water[2][1].canPass();
        water[2][1].setPass(tmp2);
        water[1][0].setPass(tmp1);
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
        lastFlowX = i;
        lastFlowY = j;
        /* si la pièce n'est pas pleine dans le modèle on arrête tout */
        if(!view.isLevelFull(x,y) || !flowing || view.isPaused()) return;
        /* on utilise pas encore le système de water tile, a modifer dans le futur */
        /* we first set the tiles i j full */
        water[i][j].setFull(true);
        /* we then wait using a timeline and call on every other parts */
        Timeline wait = new Timeline(new KeyFrame(Duration.seconds(waitTime), event ->{
            if(!flowing || !water[i][j].isFull()) return;
            if(inTab(i+1,j) && water[i+1][j].canPass() && !water[i+1][j].isFull()) flow(i+1,j);
            if(inTab(i-1,j) && water[i-1][j].canPass() && !water[i-1][j].isFull()) flow(i-1,j);
            if(inTab(i,j+1) && water[i][j+1].canPass() && !water[i][j+1].isFull()) flow(i,j+1);
            if(inTab(i,j-1) && water[i][j-1].canPass() && !water[i][j-1].isFull()) flow(i,j-1);
        }));
        /* we call it only one cycle (one time) */
        wait.setCycleCount(1);
        /* and play it */
        wait.play();
        if(isFull()){
            /* if the tile is complete, we then  call it on the neighbor tiles */
            Timeline wait_recall = new Timeline(new KeyFrame(Duration.seconds(waitTime), event -> {
                if(isFull()) view.flow(x, y);
            }));
            wait_recall.setCycleCount(1);
            wait_recall.play();
        }
    }

    boolean inTab(int i, int j){
        return(i>=0 && i<3 && j>=0 && j<3);
    }

    void setFull(boolean b){
        for(int i = 0 ; i < 3 ; i++){
            for(int j = 0; j < 3 ; j++){
                water[i][j].setFull(b);
            }
        }
    }

    public class waterGrid extends Box{
        boolean full;
        boolean pass;

        public waterGrid(double w){
            setScaleX(w);
            setScaleY(.5);
            setScaleZ(w);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(new Color(.61,.80,.87,.9));
            material.setSpecularColor(Color.AQUAMARINE);
            setMaterial(material);
        }

        void setPass(boolean b){ pass = b; }
        boolean canPass(){ return pass; }

        void setFull(boolean b){
            full = b;
            setVisible(b);
        }

        boolean isFull(){ return full; }

        public int getX(){ return x; }
        public int getY(){ return y; }
    }
}
