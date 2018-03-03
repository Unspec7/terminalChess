/**@author Jeff
 * @author Brian
 * This is the main board file. It controls most of the piece movement and holds the board itself*/

package chess;
public class Board {
	private Piece[][] board;
	Piece potentialKingslayer;
	public boolean checkmateDetected = false;
	private Piece kingInPeril = new King("black", 0, 0);
	private boolean enpassantReady = false;
	private Piece doubleStepPawn = new Pawn("black", 0, 0);
	public Board() {
		board = new Piece[8][8];
		newGame();
	}	
	
	public void printBoard() {
		/**@author Brian
		 * Prints the board.
		 */
		System.out.println();
		for (int j = 7; j > -1; j--) {
			for (int i = 0; i < 8; i++) {
					if (board[i][j] != null){
						System.out.print(board[i][j].toString()+" ");
					}
					else{
						//Checking if black
						if(i % 2 == j % 2){
							System.out.print("## ");
						} else {
							System.out.print("   ");
						}
					}
				}
			System.out.println(j+1);
			}
		System.out.println(" a  b  c  d  e  f  g  h");
		System.out.println();
		}


	public boolean move(String input, boolean blackTurn) {
		/**@author Brian
		 * Move engine.
		 * @param input Input that is given
		 * @param blackTurn Keeps track of who's turn it is
		 * @return boolean Returns weather or not it was successfully moved
		 */
		char turn;
		if (blackTurn) {
			turn = 'b';
		} else {
			turn = 'w';
		}
		String [] inArr = input.split(" ");
		String file = "abcdefgh";
		String promotions = "NBRQ";
		boolean queening = true;
		if (inArr.length==3) {
			if (promotions.contains(inArr[2])) {
				queening = false;
			}
			if (inArr[2].equals("draw?"));
			else if (inArr[2].length()!= 1 || !promotions.contains(inArr[2])) {
				return false;
			}

		} else
		if (inArr.length!=2) {
			return false;
		}
		if (inArr[0].length() != 2) {
			return false;
		}
		if (inArr[1].length() != 2) {
			return false;
		}

		int oldX = file.indexOf(inArr[0].charAt(0));
		int oldY = Character.getNumericValue(inArr[0].charAt(1))-1;
		int newX = file.indexOf(inArr[1].charAt(0));
		int newY = Character.getNumericValue(inArr[1].charAt(1))-1;
		if (newX > 7 || newY > 7 || newX < 0 || newY < 0) {
			return false;
		}
		if (board[oldX][oldY]==null) {

			return false;
		}
		if (oldX==newX && oldY==newY) {
			return false;
		}
		Piece temp = board[oldX][oldY];
		if (turn != temp.color){

			return false;
		}

		if (board[newX][newY] != null) {	
			board[oldX][oldY].eating = true;
		}
		boolean successfulMove = false;
		if (temp.type == 'K' && oldY == newY && Math.abs(oldX-newX) == 2 ){//If this is a castling move
			Piece tempRook = null;
			if (oldX < newX){
				//Right side castle
				if (blackTurn){
					tempRook = board[7][7];
				}
				else{
					tempRook = board[7][0];
				}
			}
			else if (oldX > newX){
				//Left side castle
				if (blackTurn){
					tempRook = board[0][7];
				}
				else{
					tempRook = board[0][0];
				}
			}
			if (tempRook == null){
				return false;
			}
			if ( canCastle(temp, tempRook) ){
				castle(temp, tempRook);
				return true;
			}
			return false;
		}
		else{
			if (enpassantReady) {
				if (board[oldX][oldY].type == 'P') {
					if (oldX<7) {
						if (board[oldX+1][oldY] == doubleStepPawn) {
							if (newX == doubleStepPawn.xpos) {
								board[oldX][oldY].eating = true;
								board[oldX][oldY].enpassantReady = true;
								if (board[oldX][oldY].validMove(oldX, oldY, newX, newY) && newX == doubleStepPawn.xpos) {
									board[oldX][oldY].eating = true;
									board[doubleStepPawn.xpos][doubleStepPawn.ypos] = null;
								}
							}					
						}
					}
					if (oldX>0) {
						if (board[oldX-1][oldY] == doubleStepPawn) {
							if (newX == doubleStepPawn.xpos) {
								board[oldX][oldY].eating = true;
								board[oldX][oldY].enpassantReady = true;
								if (board[oldX][oldY].validMove(oldX, oldY, newX, newY) && newX == doubleStepPawn.xpos) {
									board[oldX][oldY].eating = true;
									board[doubleStepPawn.xpos][doubleStepPawn.ypos] = null;
								}
							}
						}
					}
				}
			}
			successfulMove = temp.validMove(oldX, oldY, newX, newY);
		}
		if (successfulMove) {
			if (board[oldX][oldY].type !='N') {
				if (!checkPath(oldX, oldY, newX, newY)) {
					return false;
				}
			}
			//char castleAttempt = attemptedCastle(oldX, oldY, newX, newY);
			if (board[newX][newY] != null) {	
				if (board[oldX][oldY].color == board[newX][newY].color) {

					return false;	//team kill 
				}
			}
			Piece tempHold = board[newX][newY];
			
			board[oldX][oldY] = null;
			board[newX][newY] = temp;
			//Checks if you're putting your own
			//king in check with this move
			
			if (check(!blackTurn, false, 0, 0)){
				board[newX][newY] = tempHold;
				board[oldX][oldY] = temp;
				return false;
			}
			board[newX][newY].xpos = newX;
			board[newX][newY].ypos = newY;
			
			if (board[newX][newY].color == 'w' && newY==7 && board[newX][newY].type == 'P') {
				if (queening) {
					board[newX][newY] = new Queen("white", newX, newY);
				} else {
					if (inArr[2].equals("R")){
						board[newX][newY] = new Rook("white", newX, newY);
					}
					if (inArr[2].equals("N")){
						board[newX][newY] = new Knight("white", newX, newY);
					}
					if (inArr[2].equals("B")){
						board[newX][newY] = new Bishop("white", newX, newY);
					}
					if (inArr[2].equals("Q")){
						board[newX][newY] = new Queen("white", newX, newY);
					}
				}
			}
			if (board[newX][newY].color == 'b' && newY==0 && board[newX][newY].type == 'P') {
				if (queening) {
					board[newX][newY] = new Queen("black", newX, newY);
				} else {
					if (inArr[2].equals("R")){
						board[newX][newY] = new Rook("black", newX, newY);
					}
					if (inArr[2].equals("N")){
						board[newX][newY] = new Knight("black", newX, newY);
					}
					if (inArr[2].equals("B")){
						board[newX][newY] = new Bishop("black", newX, newY);
					}
					if (inArr[2].equals("Q")){
						board[newX][newY] = new Queen("black", newX, newY);
					}
				}
			}
			//Checks if you have check on the other player
			
			if (check(blackTurn, false, 0, 0)) {
				if (checkmate(blackTurn)) {
					printBoard();
					System.out.println("Checkmate");
					System.out.println("");
					if (blackTurn){
						System.out.println("Black wins");
					}
					else{
						System.out.println("White wins");
					}
					System.exit(0);
				} else {
				System.out.println("Check");
				}
			}
			if ( stalemate(blackTurn) ){
				printBoard();
				System.out.println("Stalemate");
				System.exit(0);
			}
			board[newX][newY].movedYet = true;
			board[newX][newY].enpassantReady = false;
			if (Math.abs(oldY-newY) == 2 && board[newX][newY].type=='P') {
				doubleStepPawn = board[newX][newY];
				enpassantReady = true;
			} else{
				enpassantReady = false;
			}
			
			return true;
		}

		return false;
	}
	private boolean canCastle(Piece tempKing, Piece tempRook){
		/**@author Jeff
		 * Checks if you can castle
		 * @param tempKing The king you're trying to castle
		 * @param tempRook The rook you're trying to castle with
		 * @return boolean Returns if you can castle or not
		 */
		//If king or rook has moved
		if(tempKing.movedYet || tempRook.movedYet) {
			return false;
		}
		if (!empty(tempKing, tempRook)){
			return false;
		}
		//Check if squares between king and rook are potential check position
		if (tempRook.xpos < tempKing.xpos){
			//Left side castle
			for (int i = tempRook.xpos; i <= tempKing.xpos; i++){
				if (check(i, tempKing.ypos, tempKing.color)){
					return false;
				}
			}
		}
		else if (tempRook.xpos > tempKing.xpos){
			//Right side castle
			
			for (int i = tempKing.xpos; i <= tempRook.xpos; i++){
				if (check(i, tempKing.ypos, tempKing.color)){
					return false;
				}			
			}
		}
		return true;
		
	}
	private boolean empty(Piece tempKing, Piece tempRook){
		/**@author Jeff
		 * Checks if the castling path is empty
		 * @param tempKing The king you're trying to castle
		 * @param tempRook The rook you're trying to castle with
		 * @return boolean Returns if this area is empty or not
		 */
		if (tempRook.xpos < tempKing.xpos){
			//Left side castle
			for (int i = tempRook.xpos+1; i < tempKing.xpos; i++){
				if (board[i][tempKing.ypos] != null){
					return false;
				}
			}
		}
		else if (tempRook.xpos > tempKing.xpos){
			//Right side castle
			for (int i = tempKing.xpos+1; i < tempRook.xpos; i++){
				if (board[i][tempKing.ypos] != null){
					return false;
				}
			}
		}
		return true;
	}
	private boolean check(int x, int y, char color){
		/**@author Jeff
		 * Checks for check on the spots used for castling
		 * @param x The position being checked's x
		 * @param y The position being checked's y
		 * @return boolean Returns if you can castle or not
		 */
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				if ( board[i][j] != null && !(i == x && j == y) ){
					//If board spot isn't empty, or it isn't itself
					if ( board[i][j].validMove(i, j, x, y) && board[i][j].color != color ) {
						if (checkPath(i, j, x, y) || board[i][j].type == 'N') {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean checkmate(boolean blackTurn) {
		/**@author Jeff
		 * Checks for checkmate
		 */
		Piece tempKing = findKing(blackTurn);
		int kingKillers = 0;
		//If there are more than one pieces checking the king, and the king itsefl
		//can't get out of check, checkmate
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if (board[i][j] != null && board[i][j].color != tempKing.color){
					if(board[i][j].checkingKing){
						kingKillers++;
					}
				}
			}
		}
		if (kingKillers > 1 && !freearoundKing(tempKing, blackTurn)){
			return true;
		}
		//Can the king move 
		if(freearoundKing(tempKing, blackTurn) || pieceSaveKing(tempKing, blackTurn)){
			return false;
		}
		return true;	
	}
	
	private boolean pieceSaveKing(Piece tempKing, boolean blackTurn){
		/**@author Jeff
		 * Check if any piece can save king
		 * @return boolean If there is at any time a piece that can save the king, return true
		 */
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if (board[i][j] != null && board[i][j] != tempKing && board[i][j].color == tempKing.color){
					if ( saveKing(tempKing, board[i][j], blackTurn) ){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean saveKing(Piece tempKing, Piece tempPiece, boolean blackTurn){
		/**@author Jeff
		 * Check if this piece can save king
		 * either by getting in the way or killing check piece
		 * @return boolean returns true if this piece can save it
		 */
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				Piece otherPiece = board[i][j];
				if (otherPiece != null && otherPiece != tempKing && otherPiece.color != tempKing.color){
					if(otherPiece.checkingKing){
						
						if(otherPiece.type == 'N'){
							if( tempPiece.validMove(tempPiece.xpos, tempPiece.ypos, otherPiece.xpos, otherPiece.ypos) ){
								if( checkPath(tempPiece.xpos, tempPiece.ypos, otherPiece.xpos, otherPiece.ypos) || tempPiece.type == 'N'){
									if(otherPiece.type == 'N'){//You have to kill the knight, since it's not affected by LOS
										return true;
									}
									else{//Break LOS
										if (breakLOS(tempKing, otherPiece, tempPiece, blackTurn)){
											return true;
										}
									}	
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	private boolean breakLOS(Piece tempKing, Piece kingSlayer, Piece tempPiece, boolean blackTurn){
		/**@author Jeff
		 * This is checking if you can break LOS and break check in a "phantom" move
		 */
		boolean temp = false;
		int tempX = tempPiece.xpos;
		int tempY = tempPiece.ypos;
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				Piece holder = null;
				if (board[x][y] != null){
					if (board[x][y].color != tempPiece.color){
						holder = board[x][y];
					}
					else{
						continue;
					}
				}
				if (tempPiece.validMove(tempPiece.xpos, tempPiece.ypos, x, y)){
					tempPiece.xpos = x;
					tempPiece.ypos = y;
					board[x][y] = tempPiece;
					board[tempX][tempY] = null;
					if( !check(blackTurn, false, 0, 0) ){
						temp = true;
					}
					//Put it back where it came from
					tempPiece.xpos = tempX;
					tempPiece.ypos = tempY;
					board[tempX][tempY] = tempPiece;
					board[x][y] = null;
					if (holder != null){
						board[x][y] = holder;
					}
					if (temp == true){
						return temp;
					}
				}
				else{
					continue;
				}
			}
		}
		return temp;
	}
	
	private boolean freearoundKing(Piece tempKing, boolean blackTurn){
		/**@author Jeff
		 * Checking if there's any spots that
		 * aren't in check around the king.
		 * @return boolean returns true if there are any free spots around king
		 */
		int x = tempKing.xpos;
		int y = tempKing.ypos;
		int i;
		int j;
		if (x-1 > 0){
			i = x-1;
		}
		else{
			i = x;
		}
		if (y-1 > 0){
			j = y-1;
		}
		else{
			j = y;
		}
		for (int a = j; a <= y+1; a++){
			for (int b = i; b <= x+1; b++){
				if ( !(a > 7 || b > 7 || a < 0 || b < 0 || a == y || b == x) ){
					//If check at any point returns false, return true
					if(board[b][a] != null){
						if(board[b][a].color != tempKing.color){
							if( !check(blackTurn, true, b, a) ){
								return true;
							}
						}
					}
					
				}
			}
		}
		return false;
	}
	private boolean stalemate(boolean blackTurn){
		/**@author Jeff
		 * Checks to see if any piece can move into a valid position.
		 * King cannot move into a checked position
		 */
		Piece tempKing = findKing(blackTurn);
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != null){
					if (board[i][j].color != tempKing.color){
						Piece checkedPiece = board[i][j];
						if (checkedPiece == tempKing){
							if ( freearoundKing(tempKing, blackTurn) ){
								return false;
							}
						}
						else{
							if(canMove(checkedPiece) || checkedPiece.enpassantReady){
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	private boolean canMove(Piece tempPiece){
		/**@author Jeff
		 * Checks if this piece has any valid moves
		 * A valid move is an empty spot or can eat that piece
		 */
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				if (x != tempPiece.xpos && tempPiece.ypos != y && board[x][y] != null){
					if(board[x][y].color != tempPiece.color){
						if ( tempPiece.validMove(tempPiece.xpos, tempPiece.ypos, x, y) ){
							if ( checkPath(tempPiece.xpos, tempPiece.ypos, x, y)){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean check(boolean blackTurn, boolean checkMatechecks, int x, int y) {
		/**@author Jeff
		 * Checks for general check
		 * @param blackTurn Keeps track of who's turn it is to determine the color
		 * @param checkMatechecks Used to check potential king spots. True = check a potential location for check
		 * @param x only matters if checkMatecheck is true. Potential x location.
		 * @param y only matters if checkMatecheck is true. Potential y location.
		 * @return boolean Returns if you're in check or not
		 */
		boolean temp = false;
		Piece tempKing = findKing(blackTurn);
		if (checkMatechecks){
			tempKing.xpos = x;
			tempKing.ypos = y;
		}
		//Check all of the opposite color's pieces for check
		for(int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				Piece tempPiece = board[i][j];
				if(tempPiece != null){
					if (tempPiece.color != tempKing.color &&
							(tempKing.xpos != i && tempKing.ypos != j) ){
						checkPiece(tempPiece, tempKing);
					}
					
					if (tempPiece.checkingKing == true){
						temp = true;
					}
				}
			}
		}
		return temp;
	}
	
	private void checkPiece(Piece tempPiece, Piece tempKing){
		/**@author Jeff
		 * Checks if the piece has king in check
		 */
		//If it has a valid move to the king
		if (tempPiece.validMove(tempPiece.xpos, tempPiece.ypos, tempKing.xpos, tempKing.ypos) ){
			//Checks if the path is clear to the king for any piece that isn't a knight
			if ( checkPath(tempPiece.xpos, tempPiece.ypos, tempKing.xpos, tempKing.ypos) || tempPiece.type == 'N'){
				tempPiece.checkingKing = true;
			}
			else{
				tempPiece.checkingKing = false;
			}
		}
		else{
			tempPiece.checkingKing = false;
		}
	}
	
	private Piece findKing(boolean blackTurn){
		/**@author Jeff
		 * Finds the king for the color given
		 */
		if (blackTurn) {//Find the king
			for (int i=0; i<8; i++) {
				for (int j=0; j<8; j++) {
					if (board[i][j] != null){
						if (board[i][j].type == 'K' && board[i][j].color == 'w') {
							kingInPeril = board[i][j];
							return board[i][j];
						}
					}
				}
			}
		} else {
			for (int i=0; i<8; i++) {
				for (int j=0; j<8; j++) {
					if (board[i][j] != null){
						if (board[i][j].type == 'K' && board[i][j].color == 'b') {
							kingInPeril = board[i][j];
							return board[i][j];
						}
					}
				}
			}
		}
		return null;
	}
	
	private boolean checkPath(int oldX, int oldY, int newX, int newY) {
		/**@author Brian
		 * Checks the piece you're trying to move (besides knight)
		 * has a clear path to its target
		 */
		if (oldX == newX && oldY < newY){
			for (int i = oldY+1; i < newY; i++) {
				if (board[oldX][i] != null && board[oldX][oldY].validMove(oldX, oldY, oldX, i)) {
					return false;
				}
			}
		}
		if (oldX == newX && oldY > newY){
			for (int i = oldY-1; i > newY; i--) {
				if (board[oldX][i] != null && board[oldX][oldY].validMove(oldX, oldY, oldX, i)) {
					return false;
				}
			}
		}
		if (oldX < newX && oldY==newY) {
			for (int i = oldX+1; i < newX; i++) {
				if (board[i][oldY] != null && board[oldX][oldY].validMove(oldX, oldY, i, oldY)) {
					return false;
				}
			}
		}
		if (oldX < newX && oldY < newY) {
			int j = oldY+1;
			for (int i = oldX+1; i < newX; i++) {
				if (board[i][j] != null && board[oldX][oldY].validMove(oldX, oldY, i, j)) {
					return false;
				}
				j++;
			}
		}
		if (oldX < newX && oldY > newY) {
			int j = oldY-1;
			for (int i = oldX+1; i < newX; i++) {
				if (board[i][j] != null && board[oldX][oldY].validMove(oldX, oldY, i, j)) {
					return false;
				}
				j--;
			}
		}
		if (oldX > newX && oldY == newY) {
			for (int i = oldX-1; i > newX; i--) {
				if (board[i][oldY] != null && board[oldX][oldY].validMove(oldX, oldY, i, oldY)) {
					return false;
				}
			}
		}
		if (oldX > newX && oldY < newY) {
			int j = oldY+1;
			for (int i = oldX-1; i > newX; i--) {
				if (board[i][j] != null && board[oldX][oldY].validMove(oldX, oldY, i, j)) {
					return false;
				}
				j++;
			}
		}
		if (oldX > newX && oldY > newY) {
			int j = oldY-1;
			for (int i = oldX-1; i > newX; i--) {
				if (board[i][j] != null && board[oldX][oldY].validMove(oldX, oldY, i, j)) {
					return false;
				}
				j--;
			}
		}
		
		//CHECK IF PAWN CAN MOVE FORWARD THROUGH UNITS
		
		return true;
	}
	/*public char attemptedCastle(int oldX, int oldY, int newX, int newY) {
		if ((oldX==4 && oldY==0) || (oldX==4 && oldY==7)) {
			if ((oldX-newX < 0) && (oldY==newY)) {
				return 'r';
			}
			if ((oldX-newX > 0) && (oldY==newY)) {
				return 'l';
			}
	}*/
	private void castle(Piece tempKing, Piece tempRook){
		/**@author Jeff
		 * Castling. Only fires if you can castle, so no need
		 * for any checks
		 * @param tempKing The king you're trying to castle
		 * @param tempRook The rook you're trying to castle with
		 */
		int tempX;
		if (tempRook.xpos < tempKing.xpos){
			//Left side castle
			//Move castle
			board[tempRook.xpos][tempRook.ypos] = null;
			
			//Update temp pieces on their new positions
			tempX = tempRook.xpos + 3;
			tempRook.xpos = tempX;
			board[tempX][tempRook.ypos] = tempRook; 
			
			//Move king
			board[tempKing.xpos][tempKing.ypos] = null;
			
			//Update temp pieces on their new positions
			tempX = tempKing.xpos - 2;
			tempKing.xpos = tempX;
			board[tempX][tempKing.ypos] = tempKing;
		}
		else if (tempRook.xpos > tempKing.xpos){
			//Right side castle
			//Move castle
			board[tempRook.xpos][tempRook.ypos] = null;
			
			//Update temp pieces on their new positions
			tempX = tempRook.xpos - 2;
			tempRook.xpos = tempX;
			board[tempRook.xpos][tempRook.ypos] = tempRook; 
			
			//Move king
			board[tempKing.xpos][tempKing.ypos] = null;
			
			//Update temp pieces on their new positions
			tempX = tempKing.xpos + 2;
			tempKing.xpos = tempX;
			board[tempKing.xpos][tempKing.ypos] = tempKing;
		}
	}
	private void newGame() {
		/**@author Jeff
		 * Starts the game and places all the pieces
		 */
		board[0][7] = new Rook("black", 0, 7);
		board[1][7] = new Knight("black", 1, 7);
		board[2][7] = new Bishop("black", 2, 7);
		board[3][7] = new Queen("black", 3, 7);
		board[4][7] = new King("black", 4, 7);
		board[5][7] = new Bishop("black", 5, 7);
		board[6][7] = new Knight("black", 6, 7);
		board[7][7] = new Rook("black", 7, 7);
		board[0][6] = new Pawn("black", 0, 6);
		board[1][6] = new Pawn("black", 1, 6);
		board[2][6] = new Pawn("black", 2, 6);
		board[3][6] = new Pawn("black", 3, 6);
		board[4][6] = new Pawn("black", 4, 6);
		board[5][6] = new Pawn("black", 5, 6);
		board[6][6] = new Pawn("black", 6, 6);
		board[7][6] = new Pawn("black", 7, 6);
		
		board[0][0] = new Rook("white", 0, 0);
		board[1][0] = new Knight("white", 1, 0);
		board[2][0] = new Bishop("white", 2, 0);
		board[3][0] = new Queen("white", 3, 0);
		board[4][0] = new King("white", 4, 0);
		board[5][0] = new Bishop("white", 5, 0);
		board[6][0] = new Knight("white", 6, 0);
		board[7][0] = new Rook("white", 7, 0);
		board[0][1] = new Pawn("white", 0, 1);
		board[1][1] = new Pawn("white", 1, 1);
		board[2][1] = new Pawn("white", 2, 1);
		board[3][1] = new Pawn("white", 3, 1);
		board[4][1] = new Pawn("white", 4, 1);
		board[5][1] = new Pawn("white", 5, 1);
		board[6][1] = new Pawn("white", 6, 1);
		board[7][1] = new Pawn("white", 7, 1);
	}
}
