package MKAgent;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class TestTree {

	public static final int DEPTH = 4;

	public static void main(String[] args) {
		Board b = new Board(7,7);

		Side north = Side.values()[0];
		Side south = Side.values()[1];
		Side mySide = south;

		Node tree = new Node(b, mySide, south);

		tree.generateChildren(DEPTH);


		ArrayList<Node> level = new ArrayList<Node>();

		Node randChild = tree.getChildren().get(3);

		randChild.generateLevel(DEPTH);
		
		level.add(randChild);

		//int i = 0;
		while(!level.isEmpty()) 
              //&&		i<3)
		{
			//i++;
			ArrayList<Node> nextLevel = new ArrayList<Node>();
			for(Node node : level)
			{
				System.out.println(node.printNode());
				nextLevel.addAll(node.getChildren());
			}
			level = nextLevel;
		}

	}
}
