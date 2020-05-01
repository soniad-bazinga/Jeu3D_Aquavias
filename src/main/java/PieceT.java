

public class PieceT extends Piece{
	
	public PieceT() {
		UP = false; RIGHT = true; DOWN = true; LEFT = true;
		symbols = new char[]{'╦','╣','╩','╠'};
	}
	
	String getType() { return "T"; }
}
