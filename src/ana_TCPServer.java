// Programmer: COSC 439/522, F '21
// Server program
// File name: "TCPServer.java"
// When you run this program, you must give the service port
// number as a command line argument. For example,
// java TCPServer 22222

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ana_TCPServer {
    private static ServerSocket servSock;

    public static void main(String[] args) {
        System.out.println("Opening port...\n");
        try {
            // Hard coded port number
            int portNumber = 20750;

            // Check if any arguments were provided for a port number.
            if (args.length == 2) {
                if (args[0].equals("-p")) {
                    portNumber = Integer.parseInt(args[1]);
                }
            }
            // Create a server object
            servSock = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }

        do {
            run();
        } while (true);
    }

    private static void run() {
        Socket link = null;
        try {
            // Put the server into a waiting state
            link = servSock.accept();

            // Connection to a client is now established.

            // https://stackoverflow.com/a/20211695
            /* Get time java program has been running for. We will use this value later to calculate
                client connection time. */
            long startTime = System.currentTimeMillis();

            // Set up input and output streams for socket
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);

            // print local host name
            String host = InetAddress.getLocalHost().getHostName();
            System.out.println("Client has established a connection to " + host);

            // First message from client is client's username.
            String clientUsername = in.readLine();

            // Create a file to save client's messages.
            // https://www.w3schools.com/java/java_files_create.asp
            FileWriter myWriter = new FileWriter("ana_chat.txt");

            // Receive and process the incoming messages.
            int numMessages = 0;
            String message = in.readLine();
            while (!message.equals("DONE")) {
                // Append client's username to their message and print it.
                String formatMessage = clientUsername + ": " + message;
                System.out.println(formatMessage);

                // Put client's message in text file.
                myWriter.write(formatMessage + "\n");

                numMessages++;
                message = in.readLine();
            }

            // Close file.
            myWriter.close();

            // Send a report back to client.
            out.println("Server received " + numMessages + " messages");

            // Read from file and send back client's messages.
            // https://www.w3schools.com/java/java_files_read.asp
            File file = new File("ana_chat.txt");
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine())
                out.println(myReader.nextLine());

            // Get connection time and send it to client.
            long endTime = System.currentTimeMillis();
            long totalTimeMS = endTime-startTime;
            long totalTimeSeconds = totalTimeMS/1000;
            long totalTimeMinutes = totalTimeSeconds/60;
            long totalTimeHours = totalTimeMinutes/60;
            out.println(totalTimeHours + "::" + totalTimeMinutes + "::" + totalTimeSeconds + "::" + totalTimeMS);

            // Let client know there are no more messages from the server.
            out.println("DONE");
            myReader.close();

            // https://www.w3schools.com/java/java_files_delete.asp
            file.delete();

        } catch (IOException e) {e.printStackTrace();}
        // Close the connection
        finally {
            try {
                System.out
                        .println("!!!!! Closing connection... !!!!!\n" + "!!! Waiting for the next connection... !!!");
                link.close();
            }

            catch (IOException e) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }

    }

}
