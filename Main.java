// Compile: $ javac Main.java
// Usage: $ java Main SRC_PORT DST_DST

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Main extends Thread {
    public static Scanner console = new Scanner(System.in);

    private final int PACKET_SIZE = 16 * 20;

    private DatagramSocket udpSock;

    public Main(int src, int dst) throws SocketException {
        udpSock = new DatagramSocket(src);
        System.out.printf("Sending messages to %d\n", dst);
        System.out.printf("Listening on %d\n", src);
    }

    public void run() {
        while (true) {
            try {
                byte[] udpBuffer = new byte[PACKET_SIZE];
                DatagramPacket udpPacket = new DatagramPacket(udpBuffer, udpBuffer.length);
                udpSock.receive(udpPacket);

                System.out.printf("[%d] ", udpPacket.getPort());
                for (byte b : udpPacket.getData()) {
                    if (b == 0) break;
                    System.out.printf("%c", b);
                }
                System.out.println();
            } catch (Exception e) {
                System.exit(1);
            }
        }
    }

    public void sendMessages(int dst) throws IOException {
        while (true) {
            byte[] msg = console.nextLine().getBytes();
            byte[] udpBuffer = new byte[PACKET_SIZE];
            System.arraycopy(msg, 0, udpBuffer, 0, msg.length < PACKET_SIZE ? msg.length : PACKET_SIZE);
            DatagramPacket udpPacket =
                new DatagramPacket(udpBuffer, udpBuffer.length, InetAddress.getLocalHost(), dst);
            udpSock.send(udpPacket);
        }
    }

    public static void main(String[] args) {
        int src = 0, dst = 0;
        try {
            if (args.length == 2) {
                src = Integer.parseInt(args[0]);
                dst = Integer.parseInt(args[1]);
            } else {
                System.out.print("Enter (src), (dst): ");
                src = console.nextInt();
                dst = console.nextInt();
                console.nextLine();
            }
            Main m = new Main(src, dst);
            m.start(); // Receive messages
            m.sendMessages(dst); // Send messages
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
