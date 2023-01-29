package UDPNTPServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

class UDPClient {
    public static void main(String[] args) throws Exception {
        // ? Create Client Socket.
        DatagramSocket clientSocket = new DatagramSocket();

        // ? Translate hostname to IP address, using DNS.
        InetAddress IPAddress = InetAddress.getByName("localhost");

        // ? Allocate space.
        byte[] sendData = new byte[1024];
        ByteBuffer receiveData = ByteBuffer.allocate(Long.BYTES); // ? For receive epoch time.

        // ? Create Datagram with Data-to-Send; length, IP address, port.
        // ? Then Send this Packet with Client Socket.
        // ! Empty packet.
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);

        // ? Read the Datagram from Server.
        DatagramPacket receivePacket = new DatagramPacket(receiveData.array(), receiveData.limit());
        clientSocket.receive(receivePacket);

        // ? Convert from byte[] to long using ByteBuffer.
        ByteBuffer responseString = ByteBuffer.allocate(Long.BYTES);
        responseString.put(receivePacket.getData());
        responseString.flip();

        // ? Convert into Date-Time format.
        String ntpDatetime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS")
                .format(new java.util.Date(responseString.getLong()));
        System.out.println("Server time: " + ntpDatetime);

        // ? Close the socket
        clientSocket.close();
    }
}
