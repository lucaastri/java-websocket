# Chat Web Application through Websocket on a Java Server
This is another school project I coded for a TPSIT (*Tecnologie e Progettazione di Sistemi Informatici e di Telecomunicazioni*, in english *Information and Telecommunications Systems Technologies and Design*) assignment. This web application allows clients on their browser to chat privately, chat with broadcast, know who is connected, and kick out sockets (a sort of admin implementation).

## Architecture and Files
The project has 7 classes (ignoring module-info) in 6 packages:
src/  
│  
├── server/  
│ ├── config/  
│ │ └── Config.java  
│ │  
│ ├── comms/  
│ │ ├── Request.java  
│ │ └── Response.java  
│ │  
│ ├── controller/  
│ │ ├── ClientHandler.java 
│ │ └── WebSocket.java 
│ │  
│ └── libs/  
│ └── gson-2.10.1.jar
│ │  
│ └── model/  
│ └── ClientInfo.java
│  
├── WebSocketServer.java 
└── module-info.java
The class **Config** holds the parameters for the whole software, to make the code more scalable, such as:

    public  enum  requests  {  SEND,  LIST,  AUTH,  END  };
    public  final  static  int  port  =  8086;

The classes **Request** and **Response** are objects that hold the package information, serialised in JSON through Google's GSON.
The class **WebSocketServers** works as the Main class, starting the class **WebSocket**. **WebSocket** is the actual socket, as it has the instructions to accept and drop connections. But, the actual communication is handled by **ClientHandler**: **ClientHandler** is responsible for encoding and decoding the packages and process the requests to respond appropriately.

It also features a Client side, that is simple and straightforward:
client/ 
│  
├── css/  
│ └── style.css
├── javascript/  
│ └── main.js
└── index.php
*Note: I was requested to code in php, although it is not necessary. It was not my choice!*

## Protocol
*Note: this Protocol is the exact same protocol as my last project, as I tried my best to code this new "web version" without touching the protocol. Link here for the aforementioned last project: [here](https://github.com/lucaastri/java-chat-client)*
The protocol, though simple and straightforward, was entirely designed by me. It has few simple commands: **SEND**, **LIST**, **AUTH**, **END**. The usage of a Config class makes the adding of new commands theorethically easier. It could be classified as stateful, JSON-based, synchronous per client handler.
I have provided some error feedbacks: **UNAVAILABLE_SERVER**, **UNREACHABLE_CLIENT**, **FAILED_COMMAND**, **OK**; with each their own code: 401, 402, 403, 200. Those codes are indeed "HTTP-like" because I have completed this project *while* I was studying the HTTP protocol in Systems & Network (a subject).
Examples of Commands:

    SEND "Alice" "Hi, how are you?"
    LIST
    AUTH "Marcus"
    END

### JSON
Every command is serialised through JSON, by GSON library. An example of a command could be:

    {
      "request": "SEND",
      "to": "Alice",
      "message": "Hi, how are you?"
    }

### Used Technologies
- Java 17+ (OOP)
- WebSocket
- HTTP communication with the aforementioned custom protocol
- Basic PHP with JS Frontend
- Multithreading
- GSON library ([github rep of GSON](https://github.com/google/gson))

## Quick Start
1. Clone the repository
2. Run `WebSocketServer.java` to start the server
3. Run the Client `index.php` (tested with XAMPP)
4. Run multiple istances to try the chatting service

## End Notes
I am proud of this project, despite its many flaws. It was my first *considerable* project that required knowledge on two (technically four) programming languages with many technologies involved, mixing the old-fashioned centralised developmet with the more realistic distributed developing. 
### Possible updates & Honesty remarks
- I kept most of my comments, to document my trial-and-error (also debug) process.
- Group chats were implemented, but the time for the assignment was up before I could finish, so the functionality is commented and should *theoretically* work, but it is **untested**.
- The whole program is more manual than it could be, due to didactic reasons.
- Some code was provided to me by my teacher.
- There could be a race condition in the multithreading, it has not been managed optimally.


Link for the rep: [here](https://github.com/lucaastri/java-websocket)
