package chess;

import java.util.Scanner;
/**@author Jeff
 * @author Brian
 * Where chess is played. Moves are "translated" from raw input into numbers*/
public class Chess {
	public static Board board;
	public static void main (String [] args) {
		newGame();
		boolean blackTurn = false;
		boolean drawAttempt=false;
		Scanner scanner = new Scanner(System.in);
		while(true) {
			if (blackTurn) {
				System.out.print("Black's move: ");
			} else {
				System.out.print("White's move: ");
			}
			String input = scanner.nextLine();
			if (drawAttempt) {
				if (input.equals("draw")){
					break;
				}
			}
			if (input.equals("resign")) {
				if (blackTurn) {
					System.out.println("White wins");
					break;
				} else {
					System.out.println("Black wins");
					break;
				}
			}
			if (board.move(input, blackTurn)){
				board.printBoard();
				blackTurn = !blackTurn;
				String inArr[] = input.split(" ");
				if (inArr.length == 3){
					if (inArr[2].equals("draw?")) {
						drawAttempt = true;
					}
				}
			} else {
				invalidMove();
				continue;
			}
			
		}
		scanner.close();
	}
	public static void newGame(){
		board = new Board();
		board.printBoard();
	}
	public static void Check(){
		System.out.println("Check");
		System.out.println("");
	}
	public static void Checkmate(){
		System.out.println("Checkmate");
		System.out.println("");
	}
	public static void Stalemate(){
		System.out.println("Stalemate");
		System.out.println("");
	}
	public static void invalidMove(){
		System.out.println("Illegal move, try again");
	}
}
