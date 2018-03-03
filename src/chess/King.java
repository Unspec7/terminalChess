package chess;
/**@author Jeff
 * @author Brian
 * Bishop piece. Can travel in any direction
 * but only one square at a time*/
public class King extends Piece{
	public King (String c, int x, int y) {
		this.xpos = x;
		this.ypos = y;
		this.setColor(c);
		this.type = 'K';
	}
	public boolean validMove(int oldX, int oldY, int newX, int newY) {
		/**@author Jeff
		 * @author Brian
		 * Checks if the move is valid for this piece
		 */
		deltaX = Math.abs(oldX-newX);
		deltaY = Math.abs(oldY-newY);
		if (deltaX > 1 || deltaY > 1){
			return false;
		}

		return true;
	}
}
