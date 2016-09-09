package sk.emefka.ws.wsserver;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import sk.emefka.ws.wsserver.auth.AuthenticationResult;

import java.time.Duration;
import java.time.Instant;

public class Client {

    /**
     * Static constant used database ID to represent client is not (yet) authenticated.
     */
    public static final int NOT_AUTHENTICATED = -1;

    /**
     * Database ID of authenticated user. This is determined by database engine and it's
     * persistent across all devices and all sessions. If client is not authenticated
     * this variable has value of minus one - representing that this client has NO database
     * id.
     */
    @Getter
    private int databaseId;

    /**
     * Nickname of authenticated client. May be null if client is not authenticated.
     */
    @Getter
    private String nickname;

    /**
     * Flag representing whether this client is authenticated or not.
     */
    @Getter
    private boolean isAuthenticated; // This should be true when databaseId == analyticsId.

    /**
     * Timestamp of when the client joined this WebSocket server.
     */
    @Getter
    private final Instant joinedAt = Instant.now();
    @Getter
    private final long joinedAtNano = System.nanoTime();


    /**
     * The technology (browser, os) this client used to connect to this server. This
     * value is determined from user agent string client sent, when upgrading
     * HTTP request to WebSocket.
     */
    @Getter
    @Setter
    private UserAgent userAgent;

    /**
     * Channel associated with this client.
     */
    @Getter
    private final Channel channel;


    public Client(Channel channel) {
        this.channel = channel;
        this.userAgent = UserAgent.UNKNOWN;
        this.isAuthenticated = false;
        this.databaseId = NOT_AUTHENTICATED;
    }

    /**
     * Sets this client's authentication status to authenticated and also sets his
     * databaseId.
     *
     * @param result authentication result
     */
    public void setAuthenticated(AuthenticationResult result) {
        this.isAuthenticated = true;
        this.databaseId = result.getDatabaseId();
        this.nickname = result.getUsername();
    }

    /**
     * Returns the duration between this client's time of join and current time.
     *
     * @return length of this session
     */
    public Duration getSessionDuration() {
        return Duration.between(joinedAt, Instant.now());
    }

    public void sendMessage(String msg) {
        //channel.writeAndFlush(msg);
    }
}
