package chess;
/**@author Jeff
 * @author Brian
 * Pawn piece. Travels forward. Promoted if it reaches
 * the other side of the board. Moves diagonally if eating something*/
public class Pawn extends Piece{
	public Pawn (String c, int x, int y) {
		this.xpos = x;
		this.ypos = y;
		this.setColor(c);
		this.type = 'P';
	}

	public boolean validMove(int oldX, int oldY, int newX, int newY) {
		/**@author Jeff
		 * @author Brian
		 * Checks if the move is valid for this piece
		 */
		deltaX = Math.abs(oldX-newX);
		deltaY = Math.abs(oldY-newY);
		if (this.color == 'w') {
			if (oldY > newY) {
				eating = false;
				return false;
			}
		}
		if (this.color == 'b') {
			if (oldY < newY) {
				eating = false;
				return false;
			}
		}
		if (!this.movedYet) {
			//First move
			if (deltaX == 0 && ( deltaY == 1 || deltaY == 2 ) ){
				eating = false;
				return true;
			}
		}
		else {
			if ( ( (deltaX == 0 && deltaY == 1 ) && !eating) || ( eating && deltaX == 1 && deltaY == 1)){

				eating=false;
				return true;
			}
		}
		return false;
	}

}