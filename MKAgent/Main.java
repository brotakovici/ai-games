package MKAgent;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

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
			boolean first = true, swap = false;

			Side north = Side.values()[0];
			Side south = Side.values()[1];
			Side mySide = north; 

			Board b = new Board(7,7);
                                    Kalah kal = new Kalah(b);
                                    Move move = null;

                                    Node rootNode = null;

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

							// SuperMove
							if (first){
								mySide = south;
								msj = Protocol.createMoveMsg(b.getNoOfHoles());
								sendMsg(msj);
                                                                                                move = new Move(south, b.getNoOfHoles());
                                                                                                kal.makeMove(b, move);
                                                                                                b = kal.getBoard();
							} // if

							System.err.println("Starting player? " + first);
							break; // Start
						case STATE: 
							Protocol.MoveTurn r = Protocol.interpretStateMsg(s, b);

							// If opponent swaps
							if (r.move == -1){
                                                                                                swap = true;
								mySide = north;
							}

							// SwapMove
							if (!first && r.move == b.getNoOfHoles() && !swap) {
								swap = true;
								mySide = south;
								msj = Protocol.createSwapMsg();
								sendMsg(msj);
							} // if

                                                                                    // Opponent's move
                                                                                    if (!first && r.move != b.getNoOfHoles() && !swap){
                                                                                        mySide = north;
                                                                                        move = new Move(south, r.move);
                                                                                        kal.makeMove(b, move);
                                                                                        b = kal.getBoard();
                                                                                    } // if

                                                                                    if (rootNode == null && first) 
                                                                                        rootNode = new Node(b, north);
                                                                                    else if (rootNode == null && !first && !swap)
                                                                                        rootNode = new Node(b, south);
                                                                                    else if (rootNode == null && !first && swap)
                                                                                        rootNode = new Node(b, north);

                                                                                    if (rootNode.getChildren.isEmpty())
                                                                                        rootNode.generateChildren(4);

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
