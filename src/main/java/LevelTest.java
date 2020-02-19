import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelTest {

    private Level l = new Level(4, 4);


    @org.junit.jupiter.api.Test
    void update() {
        l.pieces[0][1] = new PieceL();
        l.pieces[0][1].rotate();
        l.pieces[0][1].rotate();
        l.affiche();
        assertTrue(l.connected(l.pieces[0][1], l.pieces[0][0], "LEFT"), "they are connected by the left.");
        assertFalse(l.connected(l.pieces[0][1], l.pieces[0][0], "RIGHT"), "they are not connected by the right! ");
        assertTrue(l.pieces[0][0].isFull(), "It is full with water");
        assertTrue(l.connected(l.pieces[0][0], l.pieces[0][1], "RIGHT"), "they are connected by the right.");
        l.update();
        l.affiche();
        assertTrue(l.pieces[0][1].isFull(), "It is full with water");


    }

    @org.junit.jupiter.api.Test
    void setFull() {
        Piece p1 = new PieceI();
        l.pieces[0][1] = p1;
        l.setFull(0, 1);
        assertTrue(l.pieces[0][1].isFull(), "it is full with water");
        assertTrue(p1.isFull(), "it is full with water");
        l.affiche();

    }

    @org.junit.jupiter.api.Test
    void isInTab() {
        assertFalse(l.isInTab(0, 0), "this method doesn't take account of the first piece at [0,0]");
        assertTrue(l.isInTab(l.HEIGHT - 1, l.WIDTH + 1), "this method take account of the last piece.");
    }


    @org.junit.jupiter.api.Test
    void connected() {
        Piece p1 = new PieceI();
        Piece p2 = new PieceL();
        assertTrue(l.connected(p1, p2, "DOWN"), "p1 and p2 are connected from below.");

        Piece p3 = new PieceL();
        Piece p4 = new PieceI();
        p4.rotate();
        assertTrue(l.connected(p3, p4, "RIGHT"), "p1 and p2 are connected by the right.");

        Piece p5 = new PieceT();
        Piece p6 = new PieceI();
        p5.rotate();
        p6.rotate();
        assertTrue(l.connected(p5, p6, "LEFT"), "p1 and p2 are connected from below");


    }

    @Test
    void theEnd() {
    }

    @Test
    void theStart() {
    }

    @Test
    void canGo() {
    }

    @Test
    void possible() {
    }

    @Test
    void recursiveSolve() {
        l.pieces[0][1]= l.getPiece("I", 0);
        l.pieces[0][2]= l.getPiece("L", 1);
        l.pieces[0][3]= l.getPiece("T", 1);
        l.pieces[0][4]= l.getPiece("I", 1);
        l.pieces[1][1]= l.getPiece("L", 0);
        l.pieces[1][2]= l.getPiece("L", 0);
        l.pieces[1][3]= l.getPiece("T", 3);
        l.pieces[1][4]= l.getPiece("L", 3);
        l.pieces[2][1]= l.getPiece("I", 3);
        l.pieces[2][2]= l.getPiece("L", 3);
        l.pieces[2][3]= l.getPiece("T", 3);
        l.pieces[2][4]= l.getPiece("I", 3);
        l.pieces[3][1]= l.getPiece("L", 3);
        l.pieces[3][2]= l.getPiece("T", 3);
        l.pieces[3][3]= l.getPiece("L", 3);
        l.pieces[3][4]= l.getPiece("I", 3);


        l.affiche();

        assertTrue(l.recursiveSolve(0,0), "this level has a solution");



    }
}


