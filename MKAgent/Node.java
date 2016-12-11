package MKAgent;
import java.util.List;

public class Node
{
  private Side turn;
  private int currentDepth;
  private Board board;
  private Node parentNode;
  private List<Node> children;

  private List<Node> createChildren(Board board, int depth)
  {
    // Aici e o nebunie
    List<Node> children = new List<Node>();
    int maxPossibleMoves = board.getNoOfHoles();

    for(int index = 1; index <= maxPossibleMoves; index++)
    {
      Board tempBoard = new Board(board);
      // Make move, set currentDepth + 1, set turn, set parentNode
    }

    return children;
  }

  public Node(Board board, int depth, Side turn)
  {
    this.currentDepth = 0;
    this.board = board;
    this.parentNode = null;
    this.depth = depth;
    this.turn = turn;
    this.children = createChildren(this.board, depth);
  }

  public Node(Board board, Node parentNode, Side turn)
  {
    this.board = board;
    this.parentNode = parentNode;
    this.currentDepth = parentNode.currentDepth + 1;
  }

  public void setChildren(Board board, Depth depth)
  {
    // Force to set children to childrenless nodes
  }

  public List<Node> getChildren()
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