package MKAgent;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Test {

	public static Side north = Side.values()[0];
	public static Side south = Side.values()[1];
	public static Side mySide = south;
	public static int gain = 0;

	/**
 * Method that handles the seed distribution once a move is simulated.
 */
public static Board distSeeds (int seedNum, Side side, int holeNum, int noOfHoles, Board board, Side playerSide){
        int index = holeNum + 1;
        if (holeNum != 0)
			board.setSeeds(side, holeNum, 0);
		
        while ((seedNum != 0) && (index <= noOfHoles)) {
            seedNum--;
            board.setSeeds(side, index, board.getSeeds(side, index) + 1);
            index++;
        }//while

        if (seedNum != 0) {
        	if (side == playerSide) {
				if (playerSide == mySide) {
					gain++;
					seedNum--;
				} else {
					gain--;
					seedNum--;
				}//else
			} // if
			
            board = distSeeds(seedNum, side.opposite(), 0, noOfHoles, board, playerSide);
        } else {
        	if ((side == playerSide) && (board.getSeeds(side, index) == 1)) {
				if (playerSide == mySide)
					gain += board.getSeedsOp(side, index);
				else
					gain -= board.getSeedsOp(side, index);

                board.setSeedsOp(side, index, 0);
            }//if
		
			boolean noEnd = false;

			for(int i = 1; i<= noOfHoles; i++) {
				if (board.getSeeds(playerSide, i) != 0) {
					noEnd = true;
					i = noOfHoles + 1;
				}//if
			}//for

			if (!noEnd) {
				for(int i = 1; i<= noOfHoles; i++) {
					if (playerSide == mySide)
						gain -= board.getSeeds(playerSide.opposite(), i);
					else
						gain += board.getSeeds(playerSide.opposite(), i);
				
					board.setSeeds(playerSide.opposite(), i, 0);
				}//for
			} // if
		}//else	
		
		return board;
}//distSeeds
   
	public static void main(String[] args) {
    	Board testBoard = new Board(7,7);
    	testBoard = distSeeds(7, mySide, 3, 7, testBoard, mySide); 
    	for(int i = 7; i > 0; i--)
    		System.out.print(testBoard.getSeeds(mySide.opposite(), i) + " ");
    	System.out.println();

		for(int i = 1; i <= 7; i++)
    		System.out.print(testBoard.getSeeds(mySide, i) + " ");
    	System.out.println();

    	System.out.println(gain);
	}//main
  
} // Main