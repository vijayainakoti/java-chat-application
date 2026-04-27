import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        try {

            Socket socket = new Socket("localhost", 1234);
            System.out.println("Connected to Server");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            Scanner sc = new Scanner(System.in);

            // 🔥 username
            System.out.print("Enter your name: ");
            String name = sc.nextLine();

            out.println(name);

            // receive messages thread
            Thread readThread = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println("[CHAT] " + msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            readThread.start();

            // send messages
            while (true) {
                String msg = sc.nextLine();
                out.println(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}