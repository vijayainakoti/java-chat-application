import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    static List<ClientHandler> clients = new ArrayList<>();
    static List<String> onlineUsers = new ArrayList<>();

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server started... waiting for clients");

            while (true) {

                Socket socket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(socket);

                synchronized (clients) {
                    clients.add(handler);
                }

                handler.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // send message to all clients
    public static void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                c.send(message);
            }
        }
    }

    // show online users
    public static void sendUserList() {

        StringBuilder users = new StringBuilder("🟢 Online Users: ");

        synchronized (onlineUsers) {
            for (String u : onlineUsers) {
                users.append(u).append(" ");
            }
        }

        String finalList = users.toString();

        System.out.println(finalList);   // server display
        broadcast(finalList);            // send to clients
    }
}

//

class ClientHandler extends Thread {

    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String name;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 🔥 get username
            name = in.readLine();

            synchronized (Server.onlineUsers) {
                Server.onlineUsers.add(name);
            }

            Server.broadcast("🔵 " + name + " joined the chat");
            Server.sendUserList();

            String msg;

            while ((msg = in.readLine()) != null) {

                String fullMsg = name + ": " + msg;

                System.out.println(fullMsg);

                Server.broadcast(fullMsg);
            }

        } catch (Exception e) {

            System.out.println(name + " left the chat");

        } finally {

            synchronized (Server.onlineUsers) {
                Server.onlineUsers.remove(name);
            }

            Server.broadcast("🔴 " + name + " left the chat");
            Server.sendUserList();
        }
    }

    public void send(String msg) {
        out.println(msg);
    }
}