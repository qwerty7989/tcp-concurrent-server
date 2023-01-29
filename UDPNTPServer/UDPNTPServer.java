package UDPNTPServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.Instant;

public class UDPNTPServer {
    public static void main(String[] args) throws Exception {
        /*
         * Problem Encouter:
         *  BufferOverflow when Client made new NTP request.
         * 
         * What's happened:
         *  The "ByteBuffer sendData" are pre-allocated and may cause overflow 
         * when the Server try to write Client's input into it.
         * 
         * Solution:
         *  Allocate the ByteBuffer every loop fixed the issue.
         * 
         * Status: Fixed
         */
        long currentTimestamp = 0; // ? Store Epoch time.

        // ? Create Main Server Socket & Instantiate the Server.
        DatagramSocket serverSocket = new DatagramSocket(9876);
        
        while(true) {
            // ? Input/Output data.
            byte[] receiveData = new byte[1024];
            ByteBuffer sendData = ByteBuffer.allocate(Long.BYTES); // ? For store epoch time.
            
            System.out.println("Waiting for connection");

            // ? Allocated Space for received Datagram & 
            // ? Received the Input Datagram from user.
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            // ? Break down the Input Datagram; Data, IP address, Port.
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            
            // ? Get Server's current time.
            currentTimestamp = Instant.now().toEpochMilli();

            sendData.putLong(currentTimestamp);
            System.out.println("Sent: " + currentTimestamp);

            // ? Create Datagram to send the modified data &
            // ? Send it to the Client.
            DatagramPacket sendPacket = new DatagramPacket(sendData.array(), sendData.limit(), IPAddress, port);
            serverSocket.send(sendPacket);
        }
    }    
}
