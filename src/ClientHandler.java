import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Server server;
    private String name;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // Set up input and output streams
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // Get the client's name
            name = input.readLine(); // The first message is the client's name
            System.out.println(name + " has joined the chat!");

            // Send a welcome message to the client
            output.println("Welcome " + name + "! You can now start chatting.");

            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                if (clientMessage.startsWith("/private")) {
                    // Private message format: /private <username> <message>
                    String[] parts = clientMessage.split(" ", 3);
                    if (parts.length >= 3) {
                        String recipientName = parts[1];
                        String privateMessage = parts[2];
                        server.sendPrivateMessage(recipientName, privateMessage, this);
                    }
                } else {
                    // Broadcast the message to all clients
                    System.out.println(name + ": " + clientMessage);
                    server.broadcastMessage(name + ": " + clientMessage, this);
                }

                // Close connection if client sends "bye"
                if (clientMessage.equalsIgnoreCase("bye")) {
                    System.out.println(name + " disconnected.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Clean up when done
                if (input != null) input.close();
                if (output != null) output.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Send a message to the client
    public void sendMessage(String message) {
        if (output != null) {
            output.println(message);
        }
    }

    // Get the name of the client
    public String getName() {
        return name;
    }
}
