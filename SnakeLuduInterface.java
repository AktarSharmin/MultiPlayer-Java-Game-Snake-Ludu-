
import java.io.*;
import java.util.*;
import java.util.Random;

public class SnakeLuduInterface {

    public HashSet<PrintWriter> writers = new HashSet<>();//set for broadcast msg to all user

    public SnakeLuduInterface() {
        this.currentBoard = new String[100];
        this.playerMove = 1;
    }

    public volatile int playerMove; //1 for 1st player, -1 for 2nd player 
    public volatile boolean bool=false;
    private String[] currentBoard;
    private static Random rand = new Random();

    

    

    public String[] updateTable(int pos, String name) {

        for (int i = 0; i < pos - 2; i++) {
            if (this.currentBoard[i] == name) {
                this.currentBoard[i] = Integer.toString(i + 1);
            }
        }
        for (int i = pos - 2; i < 100; i++) {
            if (this.currentBoard[i] == name) {
                this.currentBoard[i] = Integer.toString(i + 1);
            }
        }

        this.currentBoard[pos - 1] = name;
        this.playerMove = -this.playerMove;

        return this.currentBoard;
    }

    public String printTable() {
        String t2 = "";
        for (int i = 0; i < 100; i++) {
            t2 = t2.concat(this.currentBoard[i] + " |");
            if ((i + 1) % 10 == 0) {

                t2 = t2.concat("\n");
            }

        }
        return t2;
    }

    public void initTable() {

        for (int i = 0; i < 100; i++) {

            this.currentBoard[i] = Integer.toString(i + 1);
        }

    }

   

    private static int nextSquare(int square) {
        switch (square) {
            case 4:
                return 14;
            case 9:
                return 31;
            case 17:
                return 7;
            case 20:
                return 38;
            case 28:
                return 84;
            case 40:
                return 59;
            case 51:
                return 67;
            case 54:
                return 34;
            case 62:
                return 19;
            case 63:
                return 81;
            case 64:
                return 60;
            case 71:
                return 91;
            case 87:
                return 24;
            case 93:
                return 73;
            case 95:
                return 75;
            case 99:
                return 78;
            default:
                return square;
        }
    }

 

    int turn(String player, int square) {

        int square2 = square;
        while (true) {
            int roll = rand.nextInt(6) + 1;
            for (PrintWriter writer : writers) {
                writer.print("Player " + player + " ,on square " + square2 + ", rolls a " + roll);
            }
            System.out.printf("Player %s, on square %d, rolls a %d", player, square2, roll);
            if (square2 + roll > 100) {
                System.out.println(" but cannot move.");
                for (PrintWriter writer : writers) {
                    writer.println(" but cannot move.");
                }
            } else {
                square2 += roll;
                System.out.printf(" and moves to square %d\n", square2);
                for (PrintWriter writer : writers) {
                    writer.println(" and moves to square " + square2);
                }
                if (square2 == 100) {
                    return 100;
                }
                
                int next = nextSquare(square2);
                if (square2 < next) {
                    System.out.printf("Yay! Landed on a ladder. Climb up to %d.\n", next);
                    for (PrintWriter writer : writers) {
                        writer.println("Yay! Landed on a ladder. Climb up to " + next);
                    }
                    if (next > 100) {
                        return 100;
                    }
                    square2 = next;
                } else if (square2 > next) {
                    System.out.printf("Oops! Landed on a snake. Slither down to %d.\n", next);
                    for (PrintWriter writer : writers) {
                        writer.println("Oops! Landed on a snake. Slither down to " + next);
                    }
                    square2 = next;
                }
            }
            if (roll < 6 ) {
                return square2;
            }
            System.out.println("Rolled a 6 so roll again.");
            for (PrintWriter writer : writers) {
                writer.println("Rolled a 6 so roll again.");
            }
        }
    }

}
