// Programmer: COSC 439/522, F '21
// Client program
// File name: TCPClient.java
// When you run this program, you must give both the host name and
// the service port number as command line arguments. For example,
// java TCPClient localhost 22222

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ana_TCPClient {
    private static InetAddress host;

    // Declare and initialize with hard coded values. May change from arguments.
    private static String hostAddress = "localhost";
    private static String portNumber = "20750";

    // Declare and initialize username to an empty string. Will prompt user for username if not provided.
    private static String username = "";

    public static void main(String[] args) {
        /* Get 3 command line arguments: username (-u), server host address (-h), server port number (-p). */
        // Go through each argument and change values for each respective variable.
        for(int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-u":
                    username = args[i + 1];
                    break;
                case "-h":
                    hostAddress = args[i + 1];
                    break;
                case "-p":
                    portNumber = args[i + 1];
                    break;
                default:
                    System.out.println("Invalid Arguments! \n Terminating Program...");
                    System.exit(1);
            }
        }

        // Determine if username was provided.
        if(username.isEmpty()) {
            // Get a username from user.
            Scanner keyboard = new Scanner(System.in);
            System.out.print("Please enter a username: ");
            username = keyboard.nextLine();
        }

        try {
            // Get server IP-address
            host = InetAddress.getByName(hostAddress);
        } catch (UnknownHostException e) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        run(Integer.parseInt(portNumber));
    }

    private static void run(int port) {
        Socket link = null;
        try {
            // Establish a connection to the server
            link = new Socket(host, port);

            // Set up input and output streams for the connection
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);

            // Set up stream for keyboard entry
            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));
            String message, response;

            // Send server username.
            out.println(username);

            // Get data from the user and send it to the server
            do {
                System.out.print("Enter message: ");
                message = userEntry.readLine();
                out.println(message);
            } while (!message.equals("DONE"));

            // Receive the final report and close the connection
            response = in.readLine();
            while(!response.equals("DONE")) {
                System.out.println(response);
                response = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            // Close connection.
            try {
                System.out.println("\n!!!!! Closing connection... !!!!!");
                link.close();
            } catch (IOException e) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }

        }

    }

}