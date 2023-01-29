package TCPConcurrentSumServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

class TCPClient {
    public static void main(String[] args) {
        String sentence;
        String responseFromServer;
        int index = 1;
        int size = 2;

        // ! Client Socket
        Socket clientSocket = null;

        // ! Input/Output Stream
        Scanner inFromUser = null;
        Scanner inFromServer = null;
        DataOutputStream outToServer = null;

        try {
            // ? Create Input Stream to get input from user.
            inFromUser = new Scanner(System.in);

            // ? Create Client Socket & Try connect to server.
            clientSocket = new Socket("localhost", 56789);

            // ? Create Output Stream & Attached to Socket
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            // ? Create Input Stream & Attached to Socket (From Server, not User)
            inFromServer = new Scanner(clientSocket.getInputStream());

            while (true) {
                System.out.print("Please enter number " + index + " (to end, press enter): ");

                // ? Get the Input from User into sentence
                sentence = inFromUser.nextLine();

                // ? Write the Input from User to the Server through Output Stream.
                outToServer.writeBytes(sentence + "\n");
                
                // ? User hits enter to quit.
                if (sentence.equals("")) {
                    System.exit(1);
                }
                
                // ? User already entered 2 numbers.
                if (index == size) {
                    // ? Read the Input from Server.
                    responseFromServer = inFromServer.nextLine();
                    System.out.println(responseFromServer);
                    index = 1;
                } else {
                    index += 1;
                }
            }
        } catch (IOException e) {
            System.out.println("Error occurred: Closing the connection.");
        } finally {
            try {
                if (inFromServer != null)
                    inFromServer.close();
                if (outToServer != null)
                    outToServer.close();
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
