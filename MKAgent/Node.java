package MKAgent;
import java.util.ArrayList;

public class Node
{
  private Side turn;
  private Side botSide;
  private int currentDepth;
  private Board board;
  private Node parentNode;
  private ArrayList<Node> children;
  private Move moveMade;
  private boolean isGameOver;
  private boolean isWon;
  private in gain;

  private ArrayList<Node> createChildren(Node node)
  {
    // Aici e o nebunie
    ArrayList<Node> children = new ArrayList<Node>();
    int maxPossibleMoves = node.getBoard().getNoOfHoles();

    for(int index = 1; index <= maxPossibleMoves; index++)
    {
      Board tempBoard = new Board(node.getBoard());
      Move attemptedMove = new Move(this.turn, index);
      Kalah maKalahInGuraLor = new Kalah(tempBoard);

      if(maKalahInGuraLor.isLegalMove(attemptedMove))
      {
        Side childTurn = maKalahInGuraLor.makeMove(attemptedMove);

        Node child = new Node(maKalahInGuraLor.getBoard(), node, childTurn, attemptedMove, maKalahInGuraLor.gameOver());

    	  //child.setGain(child.getBoard.getSeeds(child.getBotSide(), 0) - child.getBoard.getSeeds(child.getBotSide().opposite(), 0));

        children.add(child);

      }
    }

    return children;
  }

  public Node(Board board, Side turn)
  {
    this.currentDepth = 0;
    this.board = board;
    this.parentNode = null;
    this.currentDepth = 0;
    this.turn = turn;
    this.moveMade = null;
    this.gain = 0;
    this.botSide = turn;
    //this.children = createChildren(this.board, depth);
  }

  public Node(Board board, Node parentNode, Side turn, Move moveMade, boolean isGameOver)
  {
    this.board = board;
    this.parentNode = parentNode;
    this.turn = turn;
    this.currentDepth = parentNode.currentDepth + 1;
    this.isGameOver = isGameOver;
    this.moveMade = moveMade;
    this.gain = 0;
    this.botSide = this.getParent().getBotSide;
  }

  // Lol India/China are o metoda de genu.
  public void generateChildren(int depth)
  {
    while(depth)
    {
      this.createChildren(this);
      
      depth --;

      if (depth)
      {
	int worstGain = 1000;
	Move worstParentMoveMade = new Move();

        //for each child in children
          if (worstGain > child.getGain())
          {
	    worstGain = child.getGain();
            worstParentMoveMade = chil.getParent().getMoveMade();
          }

        //Nu prea stiu cum sa o definesc deci nu e definita lista asta inca
        worstMoves.add(worstParentMoveMade);
	worstGains.add(worstGain);
      }
    }
    // Force to set children to childrenless nodes up to a depth
  }

  public Move getMoveToMake()
  {
    Move move = new Move();
    //for moves in worstMoves
    //CREIERUL S-A OPRIT
    {
      //gaseste maximul de la worst gains si baga  worst moveul care ii corespunde in PIZDA MA-SII... nu. in move.
    }
    return move;
  }

  public Move getMoveMade()
  {
    return this.moveMade;
  }

  public ArrayList<Node> getChildren()
  {
    return this.children;
  }

  public void setBotSide(Side side)
  {
    this.botSide = side;
  }

  public Side getBotSide(Side side)
  {
    return this.botSide;
  }

  public void setGain(int gain)
  {
    this.gain = gain;
  }

  public int getGain()
  {
    return this.gain;
  }

  public Board getBoard()
  {
    return this.board;
  }

  public Side getTurn()
  {
    return this.turn;
  }

  public Node getParent()
  {
    return this.parentNode;
  }

  // Checks if side has half + 1 seeds in well
  // NEEDS IMPLEMENTATION
  public boolean gameWon(Side side)
  {
    return false;
  }

  // When one of the agents can't make a move, because thats the way Kalah returns
  // isGameOver.
  public boolean isGameOver()
  {
    return this.isGameOver;
  }
}
