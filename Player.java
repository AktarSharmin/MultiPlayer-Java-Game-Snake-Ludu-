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

public class Player {

    private static final int DefaultPort = 5000;
    private static final String DefaultServer = "localhost";

    Player(String ip, int port) throws IOException {
        Socket s = new Socket(ip, port);
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        Scanner input = new Scanner(System.in);
        //for getting server broadcast msg in thread pool without waiting for user input 
        clientReceiver cr = new clientReceiver(s);
        cr.start();
        //loop for getting user input 
        while (true) {

            String line = input.nextLine();
            out.println(line);
            if (line.startsWith("quit")) {
                break;
            }

        }
        try {
            s.close();
            input.close();
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void main(String args[]) throws IOException {
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DefaultPort;
        String ip = args.length > 1 ? args[0] : DefaultServer;
        Player player = new Player(ip, port);

    }

    //thread for getting server broadcast msg 
    public class clientReceiver extends Thread {

        Socket s;
        BufferedReader in;

        public clientReceiver(Socket s) throws IOException {
            this.s = s;
            this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        }

        @Override
        public void run() {
            try {
                while (true) {

                    String msg = in.readLine();
                    if (msg == null) {
                        break;
                    }
                    System.out.println(msg);
                    if (msg.startsWith("you")) {
                        break;
                        

                    }

                }
                s.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

    }

}
