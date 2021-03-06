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
      Kalah maKalah = new Kalah(tempBoard);

      if((!maKalah.gameOver()) && (maKalah.isLegalMove(attemptedMove)))
      {
        Side childTurn = maKalah.makeMove(attemptedMove);
        if (this.getParent() == null && this.getBotSide() == Side.values()[0])
        	childTurn = childTurn.opposite();

        Node child = new Node(maKalah.getBoard(), this, childTurn, attemptedMove, maKalah.gameOver());

        if(maKalah.gameOver())
          child.setGameOver(true);

        children.add(child);

      }
    }

    return children;
  }

  public Node (Node node)
  {
    this.turn = node.getTurn();
    this.botSide = node.getBotSide();
    this.currentDepth = node.getDepth();
    this.board = node.getBoard();
    this.parentNode = null;
    this.children = node.getChildren();
    this.moveMade = null;
    this.gain = 0;
    this.isGameOver = node.getGameOver();
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
    this.children = new ArrayList<Node>();
    this.isGameOver = false;
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
    this.children = new ArrayList<Node>();
    this.isGameOver = false;
  }

  public int getDepth()
  {
      return this.currentDepth;
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

    float[][] pozNegCount = new float[4][depth + 1];
    for(int i = 1; i <= 3; i++)
      for(int j = 1; j <= depth; j++)
        pozNegCount[i][j] = 0;


    for(Node currentNode: nodes)
    {
      int benefit;
      benefit = currentNode.getBoard().getSeedsInStore(currentNode.getBotSide()) - currentNode.getBoard().getSeedsInStore(currentNode.getBotSide().opposite());
      currentNode.setGain(benefit);

      pozNegCount[3][depth]++;
      if (benefit > 0)
        pozNegCount[1][depth]++;
      else if (benefit < 0)
        pozNegCount[2][depth]++;

      currentNode.getParent().setGain(currentNode.getParent().getGain() + benefit);

      pozNegCount = currentNode.updateTreeGains(pozNegCount, depth);

    }
  }

  public void generateLevel(int depth)
  {
    
    ArrayList<Node> currentLevel = new ArrayList<Node>();
    currentLevel.add(this);

    ArrayList<Node> previousLevel = new ArrayList<Node>();
    
    float[][] pozNegCount = new float[4][depth + 1];
    for(int i = 1; i <= 3; i++)
      for(int j = 1; j <= depth; j++)
        pozNegCount[i][j] = 0;

    depth = 0;

    while(!currentLevel.isEmpty())
    {
      depth++;
      ArrayList<Node> nextLevel = new ArrayList<Node>();
      previousLevel = currentLevel;
      for(Node node : currentLevel)
      {
        if(!(node.getGameOver()))
        {
          node.setGain(0);
          nextLevel.addAll(node.getChildren());
        }
        
        if(node.getGameOver())
        {
          float benefit;
          benefit = node.getGain();

          pozNegCount[3][depth]++;
          if (benefit > 0)
            pozNegCount[1][depth]++;
          else if (benefit < 0)
            pozNegCount[2][depth]++;

           node.getParent().setGain(node.getParent().getGain() + benefit);

           pozNegCount = node.updateTreeGains(pozNegCount, depth);
        }
      }
      currentLevel = nextLevel;
    }


    for(Node node : previousLevel)
    {
      ArrayList<Node> children = node.createChildren();
      node.setChildren(children);
      currentLevel.addAll(node.getChildren());
    }


    for(Node node : currentLevel)
    {
      int benefit;
      benefit = node.getBoard().getSeedsInStore(node.getBotSide()) - node.getBoard().getSeedsInStore(node.getBotSide().opposite());
      node.setGain(benefit);

      pozNegCount[3][depth]++;
      if (benefit > 0)
        pozNegCount[1][depth]++;
      else if (benefit < 0)
        pozNegCount[2][depth]++;

      node.getParent().setGain(node.getParent().getGain() + benefit);

      pozNegCount = node.updateTreeGains(pozNegCount, depth);



    }
  }

  //Method for updating the gains on each node
  public float[][] updateTreeGains(float[][] a, int depth)
  {
    if(this.getParent() != null)
    {
      int noOfMoves = this.getParent().getChildren().size();

      if (a[3][depth] == noOfMoves)
      {

        float g;
        g = this.getParent().getGain();
	      float gg;
        if ((g > 0) && (a[2][depth] > a[1][depth]))
        {

          gg = a[1][depth]/a[2][depth];
          gg = gg * g;

          this.getParent().setGain(gg);
        }

        else if ((g < 0) && (a[1][depth] > a[2][depth]))
        {

          gg = a[2][depth]/a[1][depth];
	        gg = gg * g;

          this.getParent().setGain(gg);
        }

        a[1][depth] = 0;
        a[2][depth] = 0;
        a[3][depth] = 0;
          

        if (this.getParent().getParent() != null)
        {
          this.getParent().getParent().setGain(this.getParent().getParent().getGain() + this.getParent().getGain());

          if (this.getParent().getGain() > 0)
            a[1][depth-1]++;
          else if (this.getParent().getGain() < 0)
            a[2][depth-1]++;

          a[3][depth-1]++;

          if (a[3][depth-1] == this.getParent().getParent().getChildren().size())
            a = this.getParent().updateTreeGains(a, depth-1);
        }
      }
    }

      return a;
  }

  public boolean getGameOver()
  {
    return this.isGameOver;
  }

  public Move getMoveMade()
  {
    return this.moveMade;
  }

  public Move getMoveToMake()
  {
    float maxGain = -1000;
    Move move = null;

    for(Node currentNode : this.getChildren())
    {
      if(currentNode.getGain() >= maxGain)
      {
        maxGain = currentNode.getGain();
        move = currentNode.getMoveMade();
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

  public float getGain()
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
  //Aborted
  public boolean gameWon(Side side)
  {
    int seedsInStore = this.board.getSeedsInStore(side);
    if(seedsInStore > 49)
      return true;
    else
      return false;
  }

  public void setGameOver(boolean b)
  {
    this.isGameOver = b;
  }

  // When one of the agents can't make a move, because thats the way Kalah returns
  // isGameOver.
  public boolean isGameOver()
  {
    return this.isGameOver;
  }

  public String printNode()
  {
    StringBuilder treeString = new StringBuilder();
    treeString.append("It's " + this.turn + " to make a move.\n");
    treeString.append("Bot's side is: " + this.botSide + ".\n");
    if(this.moveMade != null)
    {
      treeString.append("Previous turn was " + this.moveMade.getSide() + ".\n");
      treeString.append("Hole selected was " + this.moveMade.getHole() + ".\n");
    }
    treeString.append("Current level is: " + this.currentDepth + " .\n");
    treeString.append("This node has " + this.children.size() + " children.\n");
    treeString.append("Current node's projected gain is "+ this.gain + '\n');
    if(this.getParent() != null)
    {
      treeString.append("Parent: \n");
      treeString.append(this.getParent().getBoard().toString() + '\n');
      treeString.append("---------------------------------------------------------\n");
    }
    treeString.append(this.getBoard().toString() + '\n');

    return treeString.toString();
  }

  @Override
  public String toString()
  {
    StringBuilder treeString = new StringBuilder();

    treeString.append(this.getBoard().toString() + '\n');

    return treeString.toString();
  }
}
