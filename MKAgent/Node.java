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
  private float gain;

  private ArrayList<Node> createChildren()
  {
    // Aici e o nebunie
    ArrayList<Node> children = new ArrayList<Node>();
    int maxPossibleMoves = this.getBoard().getNoOfHoles();

    for(int index = 1; index <= maxPossibleMoves; index++)
    {
      Board tempBoard = new Board(this.getBoard());
      Move attemptedMove = new Move(this.turn, index);
      Kalah maKalahInGuraLor = new Kalah(tempBoard);

      if(!maKalahInGuraLor.gameOver() && maKalahInGuraLor.isLegalMove(attemptedMove))
      {
        Side childTurn = maKalahInGuraLor.makeMove(attemptedMove);

        Node child = new Node(maKalahInGuraLor.getBoard(), this, childTurn, attemptedMove, maKalahInGuraLor.gameOver());

    	  //child.setGain(child.getBoard.getSeeds(child.getBotSide(), 0) - child.getBoard.getSeeds(child.getBotSide().opposite(), 0));

        children.add(child);

      }
    }

    return children;
  }

  public Node(Board board, Side turn, Side ourSide)
  {
    this.currentDepth = 0;
    this.board = board;
    this.parentNode = null;
    this.currentDepth = 0;
    this.turn = turn;
    this.moveMade = null;
    this.gain = 0;
    this.botSide = ourSide;
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
    this.botSide = this.getParent().getBotSide();
  }

  // Lol India/China are o metoda de genu.
  public void generateChildren(int depth)
  {

    int parentDepth = this.currentDepth;
    int targetDepth = parentDepth + depth;
    ArrayList<Node> nodes = new ArrayList<Node>();
    nodes.add(this);
    ArrayList<Node> nextLevel = new ArrayList<Node>();

    for(int currDepth = parentDepth; currDepth < targetDepth; currDepth++)
    {
      nextLevel = new ArrayList<Node>();
      for(Node currentNode : nodes)
      {
        ArrayList<Node> children = currentNode.createChildren();
        currentNode.setChildren(children);
        nextLevel.addAll(children);
      }
      nodes = nextLevel;
    }

    int[][] pozNegCount = new int[3][depth];
    for(int i = 1; i <= 3; i++)
      for(int j = 1; j <= depth; j++)
        pozNegCount[i][j] = 0;


    for(Node currentNode: nodes)
    {
      int benefit;
      benefit = currentNode.getBoard().getSeedsInStore(currentNode.getBotSide()) - currentNode.getBoard().getSeedsInStore(currentNode.getBotSide().opposite());
      currentNode.setGain(benefit);

      pozNegCount = currentNode.updateTreeGains(pozNegCount, depth);
    }
  }

  public void generateLevel(int depth)
  {
    ArrayList<Node> currentLevel = new ArrayList<Node>();
    currentLevel.add(this);

    ArrayList<Node> previousLevel = new ArrayList<Node>();

    while(!currentLevel.isEmpty())
    {
      ArrayList<Node> nextLevel = new ArrayList<Node>();
      previousLevel = currentLevel;
      for(Node node : currentLevel)
      {
        node.setGain(0);
        nextLevel.addAll(node.getChildren());
      }
      currentLevel = nextLevel;
    }


    for(Node node : previousLevel)
    {
      node.createChildren();
    }


    int[][] pozNegCount = new int[3][depth];
    for(int i = 1; i <= 3; i++)
      for(int j = 1; j <= depth; j++)
        pozNegCount[i][j] = 0;


    for(Node node : node.getChildren())
    {
      int benefit;
      benefit = node.getBoard().getSeedsInStore(node.getBotSide()) - currentNode.getBoard().getSeedsInStore(node.getBotSide().opposite());
      currentNode.setGain(benefit);
      pozNegCount = node.updateTreeGains(pozNegCount, depth);

    }
  }

  //Method for updating the gains on each node
  public int[][] updateTreeGains(int[][] a, int depth)
  {
    if(this.getParent() != null)
    {
      float gain;
      gain = this.getGain();

      a[3][depth]++;
      if (gain > 0)
        a[1][depth]++;
      else if (gain < 0)
        a[2][depth]++;

      this.getParent().setGain(this.getParent().getGain() + gain;
      int noOfMoves = this.getParent().getChildren().size();

      if (a[3][depth] == noOfMoves)
      {

        float g;
        g = this.getParent().getGain();
        if (g > 0)
        {
          if (a[2][depth] == 0)
            a[2][depth]++;

          this.getParent().setGain(g * (a[1][depth]/a[2][depth]));
        }

        else if (g < 0)
        {

          if (a[1][depth] == 0)
            a[1][depth]++;

          this.getParent().setGain(g * (a[2][depth]/a[1][depth]));
        }

        this.getParent().getParent().setGain(this.getParent().getParent().getGain() + this.getParent().getGain());

        if (this.getParent().getGain() > 0)
          a[1][depth-1]++;
        else if (this.getParent().getGain() < 0)
          a[2][depth-1]++;

        a[3][depth-1]++;

        a[1][depth] = 0;
        a[2][depth] = 0;
        a[3][depth] = 0;

        if (this.getParent().getParent() != null)
        {
          if (a[3][depth-1] == this.getParent().getParent().getChildren().size())
            a = this.getParent().updateTreeGains(a, depth-1);
        }
      }
    }

      return a;
  }

  public Move getMove()
  {
    return this.moveMade;
  }

  public Move getMoveToMake()
  {
    float maxGain = -1000;
    Move move;

    for(Node currentNode : this.getChildren())
    {
      if(currentNode.getGain() >= maxGain)
      {
        maxGain = currentNode.getGain();
        move = currentNode.getMove();
      }
    }
    return move;
  }

  public void setChildren(ArrayList<Node> children)
  {
    this.children = children;
  }

  public ArrayList<Node> getChildren()
  {
    return this.children;
  }

  public void setBotSide(Side side)
  {
    this.botSide = side;
  }

  public Side getBotSide()
  {
    return this.botSide;
  }

  public void setGain(float gain)
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

  @Override
  public String toString()
  {
    StringBuilder treeString = new StringBuilder();

    treeString.append(this.getBoard().toString() + '\n');

    return treeString.toString();
  }
}
