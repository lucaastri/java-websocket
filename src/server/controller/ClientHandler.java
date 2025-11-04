package server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.model.ClientInfo;
import server.config.Config;
import server.comms.Request;
import server.comms.Response;

public class ClientHandler extends Thread {

    private static List<ClientHandler> clients;
    private static List<List<ClientHandler>> groups;
    private static List<String> names = new ArrayList<>();
    private Socket socket;
    private ClientInfo clientInfo;
    private OutputStream out;
    private BufferedReader in;
    private static Map<ClientHandler, String> ids = new HashMap<>(); 
    private static Gson json = new Gson();
    private String jsonString = "";
	private String jsonStringBack = "";
	private String username = "";
    
    public OutputStream getOutputStream() throws IOException { return this.socket.getOutputStream(); }

    public ClientHandler(Socket socket, List<ClientHandler> allClients) throws IOException {
        this.socket = socket;
        this.clients = allClients;
        // this.socket.setKeepAlive(true);
        clientInfo = new ClientInfo(this.socket.getInetAddress().getHostAddress(), "" + socket.getPort());

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //in = new Scanner(socket.getInputStream());
        out = socket.getOutputStream();
    }

    @Override
    public void run() {
        
        try {

            // Leggi la richiesta di handshake dal client
            String data;
            String key = null;
            while (!(data = in.readLine()).isEmpty()) {
                if (data.startsWith("Sec-WebSocket-Key: ")) {
                    key = data.substring(19);
                }
            }

            // Genera la risposta di handshake
            if (key != null) {
                String acceptKey = generateWebSocketAcceptKey(key);
                String handshakeResponse = 
                        "HTTP/1.1 101 Switching Protocols\r\n" +
                        "Upgrade: websocket\r\n" +
                        "Connection: Upgrade\r\n" +
                        "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
                out.write(handshakeResponse.getBytes(StandardCharsets.UTF_8));
                out.flush();

                // Dopo l'handshake, gestisci i messaggi WebSocket
                while (!socket.isClosed()) {

                    try {
                    byte[] decodedMessage = decodeWebSocketMessage(socket.getInputStream());
                    if (decodedMessage == null) {
                        break; // Se il messaggio e' nullo, chiudi la connessione
                    }
                    String message = new String(decodedMessage, StandardCharsets.UTF_8);
                    //System.out.println("Messaggio ricevuto: " + message);
                    
                    /*
                     * Se un pacchetto JSON tipo è:
                     * {
                     *      "request":"send";
                     *      ...
                     * }
                     * Le tre istruzioni sotto pescano solo il campo di request.
                     */
                    JsonElement elem = JsonParser.parseString(message);
                    System.out.println(elem.toString());
                    JsonObject obj = elem.getAsJsonObject();
                    String requestType = obj.get("request").getAsString().toUpperCase();

                    Response response;
                    Response responseBack;
                    boolean isFound = false;
                    
                    switch (requestType) {
                    /*
                     * {
                     * 		"request":"SEND",
                     * 		"to":"Mario",
                     * 		"message":"ciao!"
                     * }
                     */
                    /*
                     * {
                     * 		"request":"SEND",
                     * 		"to":"BROAD",
                     * 		"message":"amici, ciao!"
                     * }
                     */
                    case "SEND":
                        Request send = json.fromJson(message, Request.class); 
                        if (send.to.equals("BROAD")) {
                        	for (ClientHandler client : clients) {
                        		response = new Response(Config.feedbacks.OK, ids.get(client), Config.codes[3], send.message); //prepara la risposta
                                //responseBack = new Response(Config.feedbacks.OK, this.username, Config.codes[3], null);
                                jsonString = json.toJson(response); //scrive in stringa la risposta
                                //jsonStringBack  = json.toJson(responseBack);
                                sendWebSocketMessage(client.getOutputStream(), jsonString);
                                //sendWebSocketMessage(getOutputStream(), jsonStringBack);
                        	}
                        } else {
                        	for (ClientHandler client : clients) { //cerca per ogni client nella lista dei clients
                                if (ids.get(client).compareToIgnoreCase(send.getTo()) == 0) { //se la porta del SEND corrisponde ad una delle porte salvate nella lista
                                    isFound = true;
                                    response = new Response(Config.feedbacks.OK, ids.get(client), Config.codes[3], send.message); //prepara la risposta
                                    responseBack = new Response(Config.feedbacks.OK, this.username, Config.codes[3], null);
                                    jsonString = json.toJson(response); //scrive in stringa la risposta
                                    jsonStringBack  = json.toJson(responseBack);
                                    sendWebSocketMessage(client.getOutputStream(), jsonString);
                                    sendWebSocketMessage(getOutputStream(), jsonStringBack);
                                }
                            }
                            if (!isFound) { //se non ha mai trovato nessuno con  il numero di  porta voluto
                                response = new Response(Config.feedbacks.UNREACHABLE_CLIENT, null, Config.codes[1], null); //come prima, prepara la risposta
                                jsonStringBack = json.toJson(response); //la fa come stringa
                                sendWebSocketMessage(getOutputStream(), jsonStringBack); //la manda a chi ha mandato il messaggio iniziale
                            }
                        }
                    break;
                    
                    /*
                     * {
                     * 		"request":"LIST",
                     * }
                     */
                    case "LIST":
                    	String list = "";
                    	for (Map.Entry<ClientHandler, String> entry : ids.entrySet()) {
                    	    list += entry.getValue() + ", ";
                    	}
                        response = new Response(Config.feedbacks.OK,null, Config.codes[3], list);
                        jsonStringBack = json.toJson(response);
                        sendWebSocketMessage(getOutputStream(), jsonStringBack);
                    break;
                    
                    /*
                     * {
                     * 		"request":"GROUP",
                     * 		"to":"amici",
                     * 		"message":"paolo, ciro"
                     * }
                     */
                    /*case "GROUP":
                    	Request group = json.fromJson(message, Request.class); 
                    	group.message.replace(" ", "");
                    	String name = group.to;
                    	String[] members = group.message.split(",");
                    	List<ClientHandler> newGroup = new ArrayList<>();
                    	names.add(name);
                    	for (Map.Entry<ClientHandler, String> entry : ids.entrySet()) {
                    		for (int i = 0; i < members.length; i++) {
                    			if (entry.getValue().equals(members[i])) {
                    				newGroup.add(entry.getKey());
                    			}
                    		}
                    	}
                        response = new Response(Config.feedbacks.OK,null, Config.codes[3], "gruppo creato!");
                        jsonStringBack = json.toJson(response);
                        sendWebSocketMessage(getOutputStream(), jsonStringBack);
                    break;*/
                    
                    /*
                     * {
                     * 		"request":"AUTH",
                     * 		"to":"Luca"
                     * }
                     */
                    case "AUTH":
                    	Request auth = json.fromJson(message, Request.class); 
                    	if (auth.to.equals("null")) {
                    		response = new Response(Config.feedbacks.UNREACHABLE_CLIENT, null, Config.codes[1], "username invalido!");
                    	} else {
                    		ids.put(this, auth.to);
                    		this.username = auth.to;
                    		clients.add(this);
                        	response = new Response(Config.feedbacks.OK, null, Config.codes[3], "user confermato: " + auth.to);
                    	}
                    	jsonStringBack = json.toJson(response);
                    	sendWebSocketMessage(getOutputStream(), jsonStringBack);
                    break;
                    
                    /*
                     * {
                     * 		"request":"END",
                     * 		"to":"Luca"
                     * }
                     */
                    case "END":
                        Request end = json.fromJson(message, Request.class);
                        String dead = end.to;
                        ClientHandler toRemove = null;
                        for (Map.Entry<ClientHandler, String> entry : ids.entrySet()) {
                            if (entry.getValue().equals(dead)) {
                                toRemove = entry.getKey();
                                break;
                            }
                        }
                        if (toRemove != null) {
                            ids.remove(toRemove);
							clients.remove(toRemove);
							response = new Response(Config.feedbacks.OK, null, Config.codes[3], "Client disconnesso con successo: " + dead);
	                        toRemove.socket.close();
                        } else {
                            response = new Response(Config.feedbacks.UNREACHABLE_CLIENT, null, Config.codes[1], "Client non trovato: " + dead);
                        }
                        //jsonString = json.toJson(response);
                        //sendWebSocketMessage(getOutputStream(), jsonString);
                        //Thread.currentThread().interrupt();
                    break;
                    
                    }
				
                    } catch (IOException ioe2) {
                        System.out.println("Errore nella codifica/decodifica del messaggio dallo stream: " + ioe2.getMessage());
                    }
                }
            }
			
                    
        } catch (IOException e) {
            System.out.println("Errore nella impostazione degli stream I/O: " + e.getMessage());
            e.printStackTrace();
        }

        this.clients.remove(this);
        System.out.println("End connection for: " + this.socket.getInetAddress().getHostAddress());

    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    private String generateWebSocketAcceptKey(String key) {
        try {
            String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            String combined = key + magicString;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashed = md.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] decodeWebSocketMessage(InputStream inputStream) throws IOException {
        int b1 = inputStream.read();
        if (b1 == -1) return null;
        int b2 = inputStream.read();
        if (b2 == -1) return null;

        boolean fin = (b1 & 0x80) != 0;
        int opcode = b1 & 0x0F;
        boolean masked = (b2 & 0x80) != 0;
        int payloadLength = b2 & 0x7F;

        if (payloadLength == 126) {
            payloadLength = (inputStream.read() << 8) + inputStream.read();
        } else if (payloadLength == 127) {
            throw new IOException("Payload troppo grande");
        }

        byte[] mask = null;
        if (masked) {
            mask = new byte[4];
            for (int i = 0; i < 4; i++) {
                int read = inputStream.read();
                if (read == -1) throw new IOException("Fine stream prematura");
                mask[i] = (byte) read;
            }
        }

        byte[] payload = new byte[payloadLength];
        int totalRead = 0;
        while (totalRead < payloadLength) {
            int read = inputStream.read(payload, totalRead, payloadLength - totalRead);
            if (read == -1) throw new IOException("Fine stream prematura");
            totalRead += read;
        }

        if (masked) {
            for (int i = 0; i < payloadLength; i++) {
                payload[i] = (byte) (payload[i] ^ mask[i % 4]);
            }
        }

        // Controllo opcode testo
        if (opcode == 0x8) {
            throw new IOException("Close frame ricevuto");
        }

        return payload;
    }


    public void sendWebSocketMessage(OutputStream outputStream, String message) throws IOException {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        // Frame del messaggio
        outputStream.write(0x81);  // 0x81 = testo in WebSocket
        if (messageBytes.length <= 125) {
            outputStream.write(messageBytes.length);
        } else if (messageBytes.length <= 65535) {
            outputStream.write(126);
            outputStream.write(messageBytes.length >> 8);
            outputStream.write(messageBytes.length & 0xFF);
        } else {
            throw new IOException("Messaggio troppo lungo da inviare");
        }
        outputStream.write(messageBytes);
        outputStream.flush();
    }

}
