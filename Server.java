/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tanni
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private static final int DefaultPort = 5000;//if no port is provided then it will be used as server port
    private Socket s = null;
    private ServerSocket ss = null;
    private Scanner in = null;//used for receiving client input via server socket
    private PrintWriter out = null;//output stream of the client for broadcasting purpose 

    public Server(int port) throws IOException {

        ss = new ServerSocket(port);

        System.out.println("The server is running...");

        while (true) {

            try {

                while (true) {
                    SnakeLuduInterface game = new SnakeLuduInterface();//createing the game object for each 2 player
                    int playerID = 1;//player id for the 1st 
                    for (int i = 0; i < 2; i++) {
                        s = ss.accept();

                        in = new Scanner(s.getInputStream());
                        out = new PrintWriter(s.getOutputStream(), true);

                        game.writers.add(out);

                        System.out.println("Player " + Integer.toString(i + 1) + " connected successfully.");
                        new ProcesClient(s, in, out, game, playerID).start();
                        playerID = -1;//player id for the 2nd
                    }

                }

            } catch (Exception e) {
                s.close();
                System.out.println(e.getMessage());
            }
        }

    }

    public static void main(String args[]) throws IOException {

        int port = args.length > 0 ? Integer.parseInt(args[0]) : DefaultPort;
        Server server = new Server(port);

    }

    public class ProcesClient extends Thread {

        private final Scanner in;
        private final PrintWriter out;
        private final Socket s;

        private String username;

        private int dice = 1;
        public SnakeLuduInterface game;//game object
        public int playerID;

        public ProcesClient(Socket s, Scanner in, PrintWriter out, SnakeLuduInterface game, int playerID) {
            this.s = s;
            this.in = in;
            this.out = out;
            this.game = game;
            this.playerID = playerID;

            if (playerID == 1) {//iniate table when 1st player joins
                this.game.initTable();
            }

        }

        @Override
        public void run() {
            try {
                //player wise asking username
                switch (this.playerID) {
                    case 1:
                        //first player connected
                        out.println("You are the first player. Please enter username:");
                        username = in.nextLine();
                        out.println("Hello " + username + ". Welcome to the snake ludu game. After joining the 2nd player your game will start.");

                        break;
                    case -1:
                        //2nd player connected 
                        out.println("You are the 2nd player. Please enter username to start playing:");
                        username = in.nextLine();
                        this.game.bool = true;//make it true when 2nd player joins so that 1st player can start

                        //initialising the current board table to both player
                        for (PrintWriter writer : game.writers) {
                            writer.println(this.game.printTable());

                        }
                        break;

                    default:
                        break;
                }

                while (dice != 100) {//while any dice results in 100 indicating game ending

                    if (this.game.playerMove == this.playerID) {//check if current player's move 

                        while (this.game.bool != true) {//1st player can't start until 2nd player joins,so sleep executes

                            Thread.sleep(500);
                        }
                        //2nd player joins so loop breaks and 1st player starts play  

                        out.println("please type any key to roll dice");
                        in.nextLine().trim();

                        dice = this.game.turn(username, dice);//invoke the func to return dice value
                        this.game.updateTable(dice, username);//update main table with user's dice value 
                        //show updated table to both players
                        for (PrintWriter writer : game.writers) {
                            writer.println(this.game.printTable());

                        }

                        if (dice == 100) {//if results in 100 then the user is  the winner
                            for (PrintWriter writer : game.writers) {
                                if (writer.equals(out)) {//if the winner user then prints winner msg
                                    writer.println("you win");
                                    this.s.close();
                                    this.in.close();
                                    this.out.close();
                                    writer.close();

                                } else {//opponent who loses the game 
                                    writer.println("you lose");
                                    this.s.close();
                                    this.in.close();
                                    this.out.close();
                                    writer.close();
                                }

                            }

                            break;
                        }

                    } else {
                        // other player's turn

                        out.println("Please wait for opponent's move.");
                        while (this.game.playerMove != this.playerID) {
                            //until the player in move does not take place this player can not hit 
                            Thread.sleep(500);

                        }

                    }

                }

            } catch (Exception e) {

            }

        }
    }
}
