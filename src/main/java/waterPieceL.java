public class waterPieceL extends waterPiece{
    /*
                   UP
             [0;0][0;1][0;2]
        LEFT [1;0][1;1][1;2] RIGHT
             [2;0][2;1][2;2]
                  DOWN
         */
    public waterPieceL(double size, View p, int x, int y) {
        super(size, p, x, y);
        water[0][1].setPass(true);
        water[1][2].setPass(true);
    }
}
