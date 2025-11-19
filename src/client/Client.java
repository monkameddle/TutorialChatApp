package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final int PORT = 12345;
    private static final String IP = "91.99.184.4";

    public static Socket socket;

    static void main() {
        try {
            socket = new Socket(IP, PORT);
            IO.println("Verbindung mit Server hergestellt auf Port:" + PORT);

            var writer = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);

            var reader = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            // Thread zum Empfangen
            new Thread(() -> {
                try {
                    String incoming;
                    while ((incoming = reader.readLine()) != null) {
                        IO.println("Server sendet: " + incoming);
                    }
                } catch (IOException e) {
                    IO.println("Verbindung beendet");
                }
            }).start();

            var scanner = new Scanner(System.in);

            // Senden läuft weiter im Hauptthread
            while (true) {
                var message = scanner.nextLine();
                IO.println("Wir schicken an den Server: " + message);
                writer.println(message);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
