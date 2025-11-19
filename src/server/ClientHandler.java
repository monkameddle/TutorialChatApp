package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket client;
    private PrintWriter writer;
    private String username;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    public void sendMessage(String msg) {
        if (writer != null) {
            writer.println(msg);
            writer.flush();
        }
    }

    @Override
    public void run() {
        try {
            writer = new PrintWriter(client.getOutputStream());
            var reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String first = reader.readLine();
            if (first == null || !first.startsWith("reg:")) {
                sendMessage("falsches registrationsformat");
                client.close();
                return;
            }

            String requestedName = first.substring(4).trim();
            IO.println("ccc");

            if (Server.CLIENTS.containsKey(requestedName)) {
                sendMessage("benutzername vergeben");
                client.close();
                return;
            }

            username = requestedName;
            Server.CLIENTS.put(username, this);
            sendMessage("hallo " + username);

            String input;
            while ((input = reader.readLine()) != null) {

                if (input.equalsIgnoreCase("command:list")) {
                    String list = String.join(", ", Server.CLIENTS.keySet());
                    sendMessage(list);
                    continue;
                }

                if (input.startsWith("msg:")) {
                    String[] parts = input.split(":", 3);
                    if (parts.length < 3) {
                        sendMessage("ungültiges msg format");
                        continue;
                    }

                    String targetName = parts[1];
                    String msg = parts[2];

                    // hier vlt noch abfangen, dass man sich selbt keine nachrichten schrieben darf
                    ClientHandler target = Server.CLIENTS.get(targetName);

                    if (target == null) {
                        sendMessage("unbekannter nutzer");
                    } else {
                        target.sendMessage(username + ": " + msg);
                    }
                }
            }

        } catch (IOException ignored) {
        } finally {
            if (username != null) {
                Server.CLIENTS.remove(username);
            }
        }
    }
}
