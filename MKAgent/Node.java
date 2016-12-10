package MKAgent;

public class Node
{
  private Board board;
  private Node parentNode;
  private List<Node> children;

  private List<Board> createChildren(Board board)
  {
    return new List<Board>();
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