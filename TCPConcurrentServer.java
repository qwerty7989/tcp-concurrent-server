import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class EchoThread extends Thread {
    private Socket connectionSocket;

    public EchoThread(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    public void run() {
        /*
         * ? Problem Encounter:
         * The index is not saved. It keeps return 0 everytime User enter number and 
         * Client sent the request. 
         * 
         * ? What's really happen:
         * The index is 0 because the "socket initiate" code block in reside in "while" loop.
         * Which cause the "socket" to "initiate" new socket everytime User enter number.
         * 
         * The first clue is that on the Server-side, it produce "Waiting for connection"
         * everytime User enter number. Another is the "index" value is always 0.
         * 
         * ? Solution:
         * Just re-oriented the code block, the while loop should not initiate these "fixed" 
         * variable. Like, Input/Output stream variable, and socket variable.
         * 
         * Status: Fixed
         */

        // ! Variables
        int size = 2;
        int index = 0;
        int[] number = new int[size];
        int sum = 0;

        // ! Input/Output Stream
        BufferedReader inFromClient = null;
        DataOutputStream outToClient = null;

        try {
            // ? Create Input Stream that receive from Client.
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            // ? Create Output Stream that'll be send to Client
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            while(true) {
                // ? Read the Input from Client Stream.
                String clientSentence = inFromClient.readLine();
                
                // ? Read if client's input is hit "enter" or "number".
                if (clientSentence.equals("")) {
                    // ? Disconnect the socket.
                    System.out.println("Client disconnected.");
                    outToClient.writeBytes("Disconnecting...");
                    return;
                } else {
                    // ? Add the number into the array.
                    System.out.println("Number " + (index + 1) + ": " + clientSentence);
                    number[index] = Integer.parseInt(clientSentence);
                    index += 1;
                }

                if (index == 2) {
                    // ? Calculated the sum value.
                    for (int val : number) {
                        sum += val;
                    }

                    // ? Send the response to Client.
                    String responseToClient = "The result is " + sum + '\n';
                    System.out.println("Response: " + sum);
                    outToClient.writeBytes(responseToClient);
                    index = 0;
                }
            }
        } catch (IOException e) {
            System.err.println("Closing Socket connection.");
        } finally {
            try {
                if (inFromClient != null)
                    inFromClient.close();
                if (outToClient != null)
                    outToClient.close();
                if (connectionSocket != null)
                    connectionSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class TCPConcurrentServer {
    public static void main(String[] args) {

        // ! Main server and connection socket
        ServerSocket welcomeSocket = null;

        // ? Create Main Server Socket & Instantiate the Server.
        try {
            welcomeSocket = new ServerSocket(56789);
        } catch (IOException e) {
            System.out.println("Cannot create a welcome socket.");
            System.exit(1);
        }

        while (true) {
            try {
                System.out.println("Waiting for connection");

                // ? Wait, for the Client to contact to the Main Socket.
                // ? Then create a dedicated socket for it.
                Socket connectionSocket = welcomeSocket.accept();

                // ? Create new thread and start running it.
                EchoThread echoThread = new EchoThread(connectionSocket);
                echoThread.start();
            } catch (IOException e) {
                System.out.println("Cannot create this connection");
            }
        }
    }
}
