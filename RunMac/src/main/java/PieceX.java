

public class PieceX extends Piece{
	public PieceX() {
		UP = true; RIGHT = true; DOWN = true; LEFT = true;
		symbols = new char[] {'╬','╬','╬','╬'};
	}
	
	String getType() { return "X"; }
}
