import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clientHandlers;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clientHandlers = new ArrayList<>();
    }

    // Starts the server and listens for client connections
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandlers.add(clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Error accepting client connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Broadcasts messages to all clients
    public synchronized void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler clientHandler : clientHandlers) {
            // Don't send the message back to the sender
            if (clientHandler != sender) {
                clientHandler.sendMessage(message);
            }
        }
    }

    // Send a private message to a specific client
    public synchronized void sendPrivateMessage(String recipientName, String message, ClientHandler sender) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getName().equals(recipientName)) {
                clientHandler.sendMessage("Private from " + sender.getName() + ": " + message);
                return;
            }
        }
        sender.sendMessage("Client " + recipientName + " not found.");
    }

    // Gracefully closes the server socket
    public void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server socket closed successfully.");
            }
        } catch (IOException e) {
            System.err.println("Error closing the server socket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            // Create server socket on port 1234
            serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure server socket is closed when exiting
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing the server socket in finally block: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
