package chess;
/**@author Jeff
 * @author Brian
 * Bishop piece. Only travels in an L shape*/
public class Knight extends Piece{
	public Knight (String c, int x, int y) {
		this.xpos = x;
		this.ypos = y;
		this.setColor(c);
		this.type = 'N';
	}
	public boolean validMove(int oldX, int oldY, int newX, int newY) {
		/**@author Jeff
		 * @author Brian
		 * Checks if the move is valid for this piece
		 */
		deltaX = Math.abs(oldX-newX);
		deltaY = Math.abs(oldY-newY);
		if ( (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2)){

			return true;
		}
		return false;
	}
}
