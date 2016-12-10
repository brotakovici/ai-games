package MKAgent;
import java.util.List;

public class Node
{
  private Board board;
  private Node parentNode;
  private List<Node> children;

  private List<Node> createChildren(Board board)
  {
    // Aici e o nebunie
    return null;
  }

  public Node(Board board)
  {
    this.board = board;
    this.parentNode = null;
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