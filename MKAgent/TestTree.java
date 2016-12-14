package MKAgent;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class TestTree {
	public static void main(String[] args) {
		Board b = new Board(7,7);

		Side north = Side.values()[0];
		Side south = Side.values()[1];
		Side mySide = south;

		Node tree = new Node(b, mySide, south);

		tree.generateChildren(4);
	}
}
