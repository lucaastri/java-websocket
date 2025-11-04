package server.model;

import java.time.LocalDateTime;

public class ClientInfo {
    
    private String ip;
    private String hostName;
    private LocalDateTime lastSeen;

    public ClientInfo(String ip, String hostName) {
        this.ip = ip;
        this.hostName = hostName;
        lastSeen = LocalDateTime.now();
    }

    public String getIp() { return this.ip; }
    public String getHostName() { return this.hostName; }
    public LocalDateTime getLastSeen() { return this.lastSeen; }

}
