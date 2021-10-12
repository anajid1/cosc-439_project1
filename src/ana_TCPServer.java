/*
 * Author: Dr. Tehranipour, Abdullah Najid
 * Date: 09-23-2021
 * COSC 439/522, F '21
 * Server Program: Give server program a port number with argument -p otherwise server assumes a default port number.
 * Server waits for a connection from a client program. When connection is established server notes the time and receives
 * the client's username. After that the server keeps receiving messages from the client. The server prints the messages
 * on the console and writes the messages to a file. After client sends message "DONE" the server sends the client:
 * the number of messages it received, the message log of the client's messages via the chat file, and the
 * connection time. Server then deletes the chat file and closes the connection and awaits for a new connection.
 * Only 1 connection at a time is allowed.
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ana_TCPServer {
    private static ServerSocket servSock;

    // Values will be used to calculate connection time to a client.
    private final static int MS_IN_HOUR = 3600000;
    private final static int MS_IN_MINUTES = 60000;
    private final static int MS_IN_SECONDS = 1000;

    /* Gets a port number from args array, if no port number provided use default port number. Create the server
     * object and keep running the run method which puts program in an endless state to look for a client connection.
     */
    public static void main(String[] args) {
        System.out.println("Opening port...\n");
        try {
            // Hard coded port number
            int portNumber = 20750;

            // Check if any arguments were provided for a port number.
            for(int i = 0; i < args.length; i += 2) {
                switch (args[i]) {
                    case "-p":
                        portNumber = Integer.parseInt(args[1]);
                        break;
                    default:
                        System.out.println("Invalid Arguments! \nTerminating Program...");
                        System.exit(1);
                }
            }


            // Create a server object.
            servSock = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }

        // Keep trying to connect to a client
        do {
            run();
            // Server connected to a client and now connection has ended.
            // Run the run method again and wait for a client connection to establish again.
        } while (true);
    }

    /* Method is used to make connection and end connection to a client. */
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

                // Flush the formatted message into the text file instead of waiting till file is closed.
                myWriter.flush();

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
            // MS is added to name since unit of time is in milliseconds for better readability.
            long timeMS = endTime-startTime;

            /* Currently, timeMS holds total time. Eventually timeMS will only hold remaining time that can't fit into
             * hours, minutes, and seconds.
             */

            int timeHours = 0;
            int timeMinutes = 0;
            int timeSeconds = 0;

            // Get hours.
            if(timeMS >= MS_IN_HOUR) {
                timeHours = (int) timeMS/MS_IN_HOUR;
                timeMS = timeMS % MS_IN_HOUR;
            }

            // Get minutes.
            if(timeMS >= MS_IN_MINUTES) {
                timeMinutes = (int) timeMS/MS_IN_MINUTES;
                timeMS = timeMS % MS_IN_MINUTES;
            }

            // Get seconds.
            if(timeMS >= MS_IN_SECONDS) {
                timeSeconds = (int) timeMS/MS_IN_SECONDS;
                timeMS = timeMS % MS_IN_SECONDS;
            }

            // Send time to client.
            out.println(timeHours + "::" + timeMinutes + "::" + timeSeconds + "::" + timeMS);

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
