
public abstract class Piece {
	boolean UP;
	boolean RIGHT;
	boolean DOWN;
	boolean LEFT;
	boolean full;
	char[] symbols;
	int index;

	
	
	/* Fais tourner la pièce de 90° */
	void rotate() {
		boolean tmp1 = UP;
		boolean tmp2 = RIGHT;
		UP =  LEFT;
		RIGHT = tmp1;
		tmp1 = DOWN;
		DOWN = tmp2;
		LEFT = tmp1;
		index=(index+1)%4;
	}
	
	boolean isFull() {
		return full;
	}
	
	void setFull(boolean b) {
		full = b;
	}
	
	boolean[] getExits() {
		return new boolean[]{UP,RIGHT,DOWN,LEFT};
	}
	
	abstract String getType();
	int getRotation() { return index; }
	
	boolean isUp() { return UP; }
	boolean isRight() { return RIGHT; }
	boolean isDown() { return DOWN; }
	boolean isLeft() { return LEFT; }
	
	public String toString() {
		return Character.toString(symbols[index]);
	}
	
	public String toJSONString() {
		return getType()+": "+getRotation();
	}
	
	public Piece clone() {
		Piece p = null;
		if(this instanceof PieceT) {
			p = new PieceT();
		}
		if(this instanceof PieceL) {
			p = new PieceL();
		}
		if(this instanceof PieceX) {
			p = new PieceX();
		}
		if(this instanceof PieceI) {
			p = new PieceI();
		}
		for(int i = 0; i < index;i++) {
			p.rotate();
		}
		if(isFull()) p.setFull(true);
		return p;
	}
}
