package ManKalah;

import ManKalah.Board;
import ManKalah.IllegalMoveException;
import ManKalah.InvalidMessageException;
import ManKalah.Kalah;
import ManKalah.Move;
import ManKalah.MsgType;
import ManKalah.Player;
import ManKalah.PrintingBoardObserver;
import ManKalah.Protocol;
import ManKalah.Side;
import ManKalah.TimeoutReader;
import ManKalah.Timer;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeoutException;

public class GameEngine {
    private static final Side startingSide = Side.SOUTH;
    private static final int holes = 7;
    private static final int seeds = 7;
    private static final long agentTimeout = 3600000;
    private static final boolean printBoardToStderr = true;
    private static final boolean allowSwapping = true;
    private Kalah kalah;
    private Player playerNorth;
    private Player playerSouth;
    public String agentMessage;

    public static void main(String[] args) {
        Player playerSouth;
        Process agentProcess1;
        Player playerNorth;
        Process agentProcess2;
        if (args.length != 2) {
            System.err.println("There have to be exactly two arguments, each being the path to an\nexecutable agent application. The two agents are started\nand play a Kalah match against each other, the first agent\nbeing the starting player.\n\nThe output to standard output will consist of exactly two lines,\none per agent where the first line will be for the first agent\nand the second for the second. Each line will have the following\nformat:\n   ( \"0\" | \"1\" )   \" \"   <RESPONSETIME>\nwhere the first number is a 1 if the agent won the game (0 for\nboth in case of a draw) and <RESPONSETIME> gives the agent's\naverage response time in milliseconds.If, for example, agent 2 won (needing 423 milliseconds per move\non the average), the output would look like this:\n0 117\n1 423\nA draw would give something like:\n0 258\n0 912");
            System.exit(-1);
        }
        try {
            agentProcess1 = Runtime.getRuntime().exec(args[0]);
        }
        catch (IOException e) {
            agentProcess1 = null;
            System.err.println("Couldn't run \"" + args[0] + "\" because of the following error: " + e.getMessage());
            System.exit(-1);
        }
        try {
            agentProcess2 = Runtime.getRuntime().exec(args[1]);
        }
        catch (IOException e) {
            agentProcess2 = null;
            System.err.println("Couldn't run \"" + args[1] + "\" because of the following error: " + e.getMessage());
            System.exit(-1);
        }
        if (startingSide == Side.NORTH) {
            playerNorth = new Player(1, args[0], agentProcess1, Side.NORTH);
            playerSouth = new Player(2, args[1], agentProcess2, Side.SOUTH);
        } else {
            playerSouth = new Player(1, args[0], agentProcess1, Side.SOUTH);
            playerNorth = new Player(2, args[1], agentProcess2, Side.NORTH);
        }
        playerNorth.startReaderThread();
        playerSouth.startReaderThread();
        Board board = new Board(7, 7);
        Kalah kalah = new Kalah(board);
        PrintingBoardObserver observer = new PrintingBoardObserver(System.err);
        board.addObserver((Observer)observer);
        observer.update((Observable)board, (Object)null);
        GameEngine game = new GameEngine(kalah, playerNorth, playerSouth);
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException var8_10) {
            // empty catch block
        }
        Player abortingPlayer = game.runMatch(startingSide);
        game.evaluate(abortingPlayer);
        playerNorth.getReaderThread().finish();
        playerSouth.getReaderThread().finish();
        try {
            playerNorth.getReaderThread().join(100);
        }
        catch (InterruptedException var9_12) {
            // empty catch block
        }
        try {
            playerSouth.getReaderThread().join(100);
        }
        catch (InterruptedException var9_13) {
            // empty catch block
        }
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException var9_14) {
            // empty catch block
        }
        try {
            agentProcess1.destroy();
        }
        catch (Exception var9_15) {
            // empty catch block
        }
        try {
            agentProcess2.destroy();
        }
        catch (Exception var9_16) {
            // empty catch block
        }
        System.exit(0);
    }

    private GameEngine(Kalah kalah, Player playerNorth, Player playerSouth) {
        this.kalah = kalah;
        this.playerNorth = playerNorth;
        this.playerSouth = playerSouth;
    }

    private Player runMatch(Side startingSide) {
        Player waitingPlayer;
        Player activePlayer;
        Timer responseTimer = new Timer();
        Player abortingPlayer = null;
        boolean skipEndMessages = false;
        if (startingSide == Side.NORTH) {
            activePlayer = this.playerNorth;
            waitingPlayer = this.playerSouth;
        } else {
            activePlayer = this.playerSouth;
            waitingPlayer = this.playerNorth;
        }
        try {
            try {
                this.playerNorth.sendMsg(Protocol.createStartMsg((Side)Side.NORTH));
            }
            catch (IOException e) {
                abortingPlayer = this.playerNorth;
                throw e;
            }
            try {
                this.playerSouth.sendMsg(Protocol.createStartMsg((Side)Side.SOUTH));
            }
            catch (IOException e) {
                abortingPlayer = this.playerSouth;
                throw e;
            }
            responseTimer.start();
            boolean gameOver = false;
            int moveCount = 1;
            while (!gameOver) {
                long timeout = 3600000 - activePlayer.getOverallResponseTime();
                agentMessage = activePlayer.getReaderThread().recvMsg(timeout);
                responseTimer.stop();
                activePlayer.incrementMoveCount();
                activePlayer.incrementOverallResponseTime(responseTimer.time());
                responseTimer.reset();
                MsgType msgType = Protocol.getMessageType((String)agentMessage);
                if (msgType == MsgType.SWAP && moveCount == 2) {
                    this.playerNorth.changeSide();
                    this.playerSouth.changeSide();
                    Player tmpPlayer = this.playerNorth;
                    this.playerNorth = this.playerSouth;
                    this.playerSouth = tmpPlayer;
                    tmpPlayer = activePlayer;
                    activePlayer = waitingPlayer;
                    waitingPlayer = tmpPlayer;
                    System.err.println("Move: Swap");
                    activePlayer.sendMsg(Protocol.createSwapInfoMsg((Board)this.kalah.getBoard()));
                    responseTimer.start();
                } else {
                    if (msgType != MsgType.MOVE) {
                        throw new InvalidMessageException("Expected a move message.");
                    }
                    int hole = Protocol.interpretMoveMsg((String)agentMessage);
                    if (hole < 1) {
                        throw new InvalidMessageException("Expected a positive hole number but got " + hole + ".");
                    }
                    Move move = new Move(activePlayer.getSide(), hole);
                    if (!this.kalah.isLegalMove(move)) {
                        throw new IllegalMoveException();
                    }
                    Side turn = this.kalah.makeMove(move);
                    if (moveCount == 1) {
                        turn = waitingPlayer.getSide();
                    }
                    gameOver = this.kalah.gameOver();
                    if (turn != activePlayer.getSide()) {
                        Player tmpPlayer = activePlayer;
                        activePlayer = waitingPlayer;
                        waitingPlayer = tmpPlayer;
                    }
                    try {
                        waitingPlayer.sendMsg(Protocol.createStateMsg((Move)move, (Board)this.kalah.getBoard(), (boolean)gameOver, (boolean)false));
                    }
                    catch (IOException e) {
                        abortingPlayer = waitingPlayer;
                        throw e;
                    }
                    activePlayer.sendMsg(Protocol.createStateMsg((Move)move, (Board)this.kalah.getBoard(), (boolean)gameOver, (boolean)true));
                    responseTimer.start();
                }
                ++moveCount;
            }
        }
        catch (InvalidMessageException e) {
            abortingPlayer = activePlayer;
            System.err.println("Error: Invalid message. " + e.getMessage() + " Agent " + abortingPlayer.getName() + " does not obey the protocol.\n" + agentMessage);
        }
        catch (IllegalMoveException e) {
            abortingPlayer = activePlayer;
            System.err.println("Error: Agent " + abortingPlayer.getName() + " tried to perform an illegal move.\n" + agentMessage);
        }
        catch (TimeoutException e) {
            abortingPlayer = activePlayer;
            System.err.println("Error: Agent " + abortingPlayer.getName() + " timed out.");
        }
        catch (IOException e) {
            if (abortingPlayer == null) {
                abortingPlayer = activePlayer;
            }
            System.err.println("Error: Connection to agent " + abortingPlayer.getName() + " broke down. " + e.getMessage());
            Player sanePlayer = abortingPlayer == this.playerNorth ? this.playerSouth : this.playerNorth;
            try {
                sanePlayer.sendMsg(Protocol.createEndMsg());
            }
            catch (IOException timeout) {
                // empty catch block
            }
            skipEndMessages = true;
        }
        if (!skipEndMessages) {
            String endMessage = Protocol.createEndMsg();
            try {
                this.playerNorth.sendMsg(endMessage);
            }
            catch (IOException sanePlayer) {
                // empty catch block
            }
            try {
                this.playerSouth.sendMsg(endMessage);
            }
            catch (IOException sanePlayer) {
                // empty catch block
            }
        }
        return abortingPlayer;
    }

    private void evaluate(Player abortingPlayer) {
        boolean northWon = false;
        boolean southWon = false;
        int score = 0;
        if (abortingPlayer == this.playerNorth) {
            southWon = true;
        } else if (abortingPlayer == this.playerSouth) {
            northWon = true;
        } else {
            int seedDifference = this.kalah.getBoard().getSeedsInStore(Side.NORTH) - this.kalah.getBoard().getSeedsInStore(Side.SOUTH);
            if (seedDifference > 0) {
                northWon = true;
            } else if (seedDifference < 0) {
                southWon = true;
            }
            score = seedDifference;
            if (score < 0) {
                score = - score;
            }
        }
        System.err.println();
        if (northWon || southWon) {
            System.err.print("WINNER: Player ");
            if (northWon) {
                System.err.println(String.valueOf(this.playerNorth.getPlayerNumber()) + " (" + this.playerNorth.getName() + ")");
            } else if (southWon) {
                System.err.println(String.valueOf(this.playerSouth.getPlayerNumber()) + " (" + this.playerSouth.getName() + ")");
            }
        } else {
            System.err.println("DRAW");
        }
        if (abortingPlayer != null) {
            System.err.println("MATCH WAS ABORTED");
        } else {
            System.err.println("SCORE: " + score);
        }
        long millisecsPerMoveSouth = this.playerSouth.getMoveCount() == 0 ? 0 : this.playerSouth.getOverallResponseTime() / (long)this.playerSouth.getMoveCount();
        long millisecsPerMoveNorth = this.playerNorth.getMoveCount() == 0 ? 0 : this.playerNorth.getOverallResponseTime() / (long)this.playerNorth.getMoveCount();
        System.err.println("\nPlayer " + this.playerSouth.getPlayerNumber() + " (" + this.playerSouth.getName() + "): " + this.playerSouth.getMoveCount() + " moves, " + millisecsPerMoveSouth + " milliseconds per move");
        System.err.println("Player " + this.playerNorth.getPlayerNumber() + " (" + this.playerNorth.getName() + "): " + this.playerNorth.getMoveCount() + " moves, " + millisecsPerMoveNorth + " milliseconds per move");
        System.err.println();
        String resultsPlayerNorth = String.valueOf(northWon ? "1" : "0") + " " + millisecsPerMoveNorth;
        String resultsPlayerSouth = String.valueOf(southWon ? "1" : "0") + " " + millisecsPerMoveSouth;
        if (this.playerNorth.getPlayerNumber() == 1) {
            System.out.println(resultsPlayerNorth);
            System.out.println(resultsPlayerSouth);
        } else {
            System.out.println(resultsPlayerSouth);
            System.out.println(resultsPlayerNorth);
        }
    }
}