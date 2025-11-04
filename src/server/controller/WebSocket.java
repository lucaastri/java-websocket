package server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.config.Config;

public class WebSocket {

    private static int port = Config.port;
    private static int SERVER_SLEEP = 250;

    ServerSocket server;
    private boolean shutDown = false;

    List<ClientHandler> clients;

    public WebSocket() {
        clients = new ArrayList<>();
    }

    public void run() {

        try {
        	server = new ServerSocket(port);
        	System.out.println("Server has started on 127.0.0.1:" + port  + ".\r\n" + "Waiting for a connection");
        	while (!shutDown) {
        		try {
        			handle(server.accept());
        			Thread.sleep(SERVER_SLEEP);
        		} catch (IOException ioe) {
        			System.out.println("NetException?: " + ioe.getMessage());
        		} catch (InterruptedException intexc) {
        			System.out.println("Interrupted Exception: " + intexc.getMessage());
        			continue;
        		}
        	}
        } catch (IOException ioServer){
            System.out.println("Errore grave: " + ioServer.getMessage());
        }
    }

    private void handle(Socket socket) throws IOException {
        
        ClientHandler client = new ClientHandler(socket, clients);
        System.out.println("Un client si è sconnesso: " + 
            client.getClientInfo().getIp());
        client.start();

        clients.add(client);

    }
}
