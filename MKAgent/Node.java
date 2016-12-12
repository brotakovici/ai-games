package MKAgent;
import java.util.ArrayList;

public class Node
{
  private Side turn;
  private int currentDepth;
  private Board board;
  private Node parentNode;
  private ArrayList<Node> children;
  private int moveMade;

  private ArrayList<Node> createChildren(Board board, int depth)
  {
    // Aici e o nebunie
    ArrayList<Node> children = new ArrayList<Node>();
    int maxPossibleMoves = board.getNoOfHoles();

    for(int index = 1; index <= maxPossibleMoves; index++)
    {
      Board tempBoard = new Board(board);
      Move attemptedMove = new Move(index, this.turn);
      // Make move, set currentDepth + 1, set turn, set parentNode, move made to get there
      /*
      (simulatedBoard, getsTurn) = this.simulateMove(board, i)
      create node
      */
    }

    return children;
  }

  public Node(Board board, int depth, Side turn, int moveMade)
  {
    this.currentDepth = 0;
    this.board = board;
    this.parentNode = null;
    this.currentDepth = depth;
    this.turn = turn;
    this.moveMade = moveMade;
    //this.children = createChildren(this.board, depth);
  }

  public Node(Board board, Node parentNode, Side turn)
  {
    this.board = board;
    this.parentNode = parentNode;
    this.currentDepth = parentNode.currentDepth + 1;
  }

  public void setChildren(Board board, int depth)
  {
    // Force to set children to childrenless nodes
  }

  public ArrayList<Node> getChildren()
  {
    return this.children;
  }

  public Board getBoard()
  {
    return this.board;
  }

  public Node getParent()
  {
    return this.parentNode;
  }
}