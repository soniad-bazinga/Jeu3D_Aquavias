

public abstract class Piece {
	boolean UP;
	boolean RIGHT;
	boolean DOWN;
	boolean LEFT;
	boolean full;
	char[] symbols;
	int index;
	
	
	
	
	/* rotate 90° */
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
}
