package MKAgent;
import java.util.ArrayList;

public class Node
{
  private Side turn;
  private int currentDepth;
  private Board board;
  private Node parentNode;
  private ArrayList<Node> children;
  private Move moveMade;
  private boolean isGameOver;
  private boolean isWon;

  private ArrayList<Node> createChildren(Board board)
  {
    // Aici e o nebunie
    ArrayList<Node> children = new ArrayList<Node>();
    int maxPossibleMoves = board.getNoOfHoles();

    for(int index = 1; index <= maxPossibleMoves; index++)
    {
      Board tempBoard = new Board(board);
      Move attemptedMove = new Move(this.turn, index);
      Kalah maKalahInGuraLor = new Kalah(tempBoard);

      if(maKalahInGuraLor.isLegalMove(attemptedMove))
      {
        Side childTurn = maKalahInGuraLor.makeMove(attemptedMove);

        Node child = new Node(maKalahInGuraLor.getBoard(), this, childTurn, attemptedMove, maKalahInGuraLor.gameOver());
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
  }

  // Lol India/China are o metoda de genu.
  public void generateChildren(Board board, int depth)
  {
    // Force to set children to childrenless nodes up to a depth
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


  // When one of the agents can't make a move, because thats the way Kalah returns
  // isGameOver.
  public boolean isGameOver()
  {
    return this.isGameOver;
  }
}