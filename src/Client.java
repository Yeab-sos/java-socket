import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1234);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            // Get the client's name
            System.out.print("write your username: ");
            String name = scanner.nextLine();
            output.println(name); // Send name to the server

            // Read the welcome message from the server
            String serverMessage = input.readLine();
            System.out.println("Server: " + serverMessage);

            String userMessage;
            while (true) {
                System.out.print("client: ");
                userMessage = scanner.nextLine();

                // Send message to server
                output.println(userMessage);

                // Receive the response from server (either broadcast or private message)
                String serverResponse = input.readLine();
                System.out.println(serverResponse);

                // Exit if the user types "bye"
                if (userMessage.equalsIgnoreCase("see you soon")) {
                    System.out.println("Exiting...");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

