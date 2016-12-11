package MKAgent;
import java.util.List;

public class Node
{
  private Side turn;
  private int depth;
  private Board board;
  private Node parentNode;
  private List<Node> children;

  private List<Node> createChildren(Board board)
  {
    // Aici e o nebunie
    return null;
  }

  public Node(Board board, int depth, Side turn)
  {
    this.board = board;
    this.parentNode = null;
    this.depth = depth;
    this.turn = turn;
    this.children = createChildren(this.board);
  }

  public Node(Board board, Node parentNode)
  {
    this.board = board;
    this.parentNode = parentNode;
    this.children = createChildren(this.board);
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