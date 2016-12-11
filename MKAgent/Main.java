package MKAgent;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

private Side mySide;

/**
 * Method that handles the seed distribution once a move is simulated.
 */
public void distSeeds (int seedNum, Side side, int holeNum, int noOfHoles, Board board, Side playerSide, Node currentNode)
{
        int index = holeNum + 1;
	board.setSeeds(side, holeNum, 0);
        while (seedNum != 0) && (index <= noOfHoles)
        {
                seedNum--;
                board.setSeeds(side, index, board.getSeeds(side, index) + 1);
                index++;
        }//while

        if (seeds != 0)
        {
                if (side == playerSide)
		{
			if (playerSide == mySide)
			{
				currentNode.gain++;
				seedNum--;
			}//if
			
			else
			{
				currentNode.gain--;
				seedNum--;
			}//else
		}
			
                distSeeds(seedNum, side.opposite(), 0, noOfHoles, board, playerSide, currentNode);
        }//if

        else
	{
                if (side == playerSide) && (board.getSeeds(side, index) == 1)
                {
			if (playerSide == mySide)
				currentNode.gain += board.getSeedsOp(side, index);
			else
				currentNode.gain -= board.getSeedsOp(side, index);
				
                        board.setSeedsOp(side, index, 0);
                }//if
		
		boolean noEnd = false;

		for(int i = 1; i<= noOfHoles; i++)
		{
			if (board.getSeeds(playerSide, i) != 0)
			{
				noEnd = true;
				i = noOfHoles + 1;
			}//if
		}//for

		if (!noEnd)
			for(int i = 1; i<= noOfHoles; i++)
			{
				if (playerSide == mySide)
					currentNode.gain -= board.getSeeds(playerSide.opposite, i);
				else
					currentNode.gain += board.getSeeds(playerSide.opposite, i);
				
				board.setSeeds(playerSide.opposite, i, 0);
			}//for
	}//else	

}//distSeeds

/**
 * The main application class. It also provides methods for communication
 * with the game engine.
 */
public class Main
{
    /**
     * Input from the game engine.
     */
    private static Reader input = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Sends a message to the game engine.
     * @param msg The message.
     */
    public static void sendMsg (String msg)
    {
    	System.out.print(msg);
    	System.out.flush();
    }

    /**
     * Receives a message from the game engine. Messages are terminated by
     * a '\n' character.
     * @return The message.
     * @throws IOException if there has been an I/O error.
     */
    public static String recvMsg() throws IOException
    {
    	StringBuilder message = new StringBuilder();
    	int newCharacter;

    	do
    	{
    		newCharacter = input.read();
    		if (newCharacter == -1)
    			throw new EOFException("Input ended unexpectedly.");
    		message.append((char)newCharacter);
    	} while((char)newCharacter != '\n');

		return message.toString();
    }

	/**
	 * The main method, invoked when the program is started.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args)
	{
    	try {
			String s, msj;
			boolean first = true;
			mySide = NORTH;
			Board b = new Board(7,7);
			while (true){
				System.err.println();
				s = recvMsg();
				System.err.print("Received: " + s);
				try {
					MsgType mt = Protocol.getMessageType(s);
					switch (mt) {
						case START:
							System.err.println("A start.");
							first = Protocol.interpretStartMsg(s);

							// @SuperMove
							if (first){
								mySide = SOUTH;
								msj = Protocol.createMoveMsg(b.getNoOfHoles());
								sendMsg(msj);
							} // if

							System.err.println("Starting player? " + first);
							break; // Start
						case STATE: 
							Protocol.MoveTurn r = Protocol.interpretStateMsg(s, b);

							// If opponent swaps
							if (r.move == -1){
								mySide = NORTH;
							}

							// @SwapMove
							if (!first && r.move == b.getNoOfHoles()) {
								first = true;
								mySide = SOUTH;
								msj = Protocol.createSwapMsg();
								sendMsg(msj);
							} // if
							

							System.err.println("A state.");
							System.err.println("This was the move: " + r.move);
							System.err.println("Is the game over? " + r.end);
							if (!r.end) System.err.println("Is it our turn again? " + r.again);
							System.err.print("The board:\n" + b);
							break; // State
						case END: 
							System.err.println("An end. Bye bye!"); 
							return; // END
					} // switch
				} /* try */ catch (InvalidMessageException e) {
					System.err.println(e.getMessage());
				} // catch
			} // while
		} /* try */ catch (IOException e) {
			System.err.println("This shouldn't happen: " + e.getMessage());
		} // catch
  } // main
} // Main
