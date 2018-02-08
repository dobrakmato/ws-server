package sk.emefka.ws.wsserver.auth;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import sk.emefka.ws.wsserver.Configuration;
import sk.emefka.ws.wsserver.Disposable;
import sk.emefka.ws.wsserver.RobinServer;
import sk.emefka.ws.wsserver.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class EmefkaAuthenticator implements Authenticator, Disposable {

    private ThreadLocal<MessageDigest> sha256;
    private final ServerCookieDecoder cookieDecoder = ServerCookieDecoder.LAX;
    private final JSONObject mysqlConf;

    // todo: replace with connection pool
    private ThreadLocal<MysqlLogin> mysqlConnection = ThreadLocal.withInitial(this::createConnection);
    private List<MysqlLogin> openConnections = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void dispose() {
        // Close all mysql resources before allowing GC to collect Java
        // objects.
        log.info("Closing all open mysql connections.");
        for (MysqlLogin mysql : openConnections) {
            try {
                mysql.close();
            } catch (SQLException e) {
                log.error("Can't c!", e);
            }
        }

        // Set ThreadLocals to null, so they can be garbage collected.
        sha256 = null;
        mysqlConnection = null;
    }

    @Data
    private final static class MysqlLogin {
        private final Connection connection;
        private final PreparedStatement authStmt;

        private void close() throws SQLException {
            this.connection.close();
            this.authStmt.close();
        }
    }

    //private final BasicDataSource db = new BasicDataSource();

    public EmefkaAuthenticator() {
        sha256 = ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);// should not happen...
            }
        });

        Configuration config = RobinServer.getInstance().getConfig();
        mysqlConf = config.getConfRoot().getJSONObject("emefka").getJSONObject("mysql");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            log.error("error", e);
        }

        //db.setUrl("jdbc:mysql://" + conf.getString("address") + ":" + conf.getInt("port") + "/" + conf.getString("database"));
        //db.setUsername(conf.getString("username"));
        //db.setPassword(conf.getString("password"));
    }

    private MysqlLogin createConnection() {
        log.info(" Creating connection for this thread.");
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://" + mysqlConf.getString("address") + ":" + mysqlConf.getInt("port") + "/" + mysqlConf.getString("database"),
                    mysqlConf.getString("username"),
                    mysqlConf.getString("password")
            );

            PreparedStatement authStmt = connection.prepareStatement("SELECT password, sha, id FROM users WHERE `name` = ?");
            MysqlLogin l = new MysqlLogin(connection, authStmt);
            openConnections.add(l);
            return l;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Future<AuthenticationResult> authenticate(ChannelHandlerContext ctx, FullHttpRequest req) {
        return RobinServer.getInstance().getExecutorGroup().submit(new Callable<AuthenticationResult>() {
            @Override
            public AuthenticationResult call() throws Exception {
                MysqlLogin mysql = mysqlConnection.get();
                return doAuthenticate(mysql);
            }

            private AuthenticationResult doAuthenticate(MysqlLogin mysql) throws Exception {
                Map<String, Cookie> cookies = Utils.cookiesSetToMap(cookieDecoder.decode(req.headers().get("Cookie", "")));

                String username = cookies.containsKey("username") ? cookies.get("username").value() : null;
                String password = cookies.containsKey("psw2") ? cookies.get("psw2").value() : null;

                if (username == null || password == null) {
                    // Authentication failed: No User or Password.
                    log.debug(" Authentication failed: No User or Password.");
                    return null;
                }

                // This is based on our legacy PHP login / authentication. Be scared.
                PreparedStatement authStmt = mysql.authStmt;
                authStmt.clearParameters();
                authStmt.setString(1, username);

                ResultSet result = authStmt.executeQuery();
                if (result.next()) {
                    String dbPassword = result.getString("password");
                    String hexPsw2 = psw2(dbPassword);
                    if (password.equalsIgnoreCase(hexPsw2)) {
                        // Authentication succeed.
                        return new AuthenticationResult(result.getInt("id"), username);
                    } else {
                        // Authentication failed: Wrong password.
                        log.info(" Authentication failed: Wrong password ({}).", username);
                        return null;
                    }
                } else {
                    // Authentication failed: User not found.
                    log.info(" Authentication failed: User not found ({}).", username);
                    return null;
                }
            }

            private String psw2(String password) {
                MessageDigest sha = sha256.get();
                sha.update(password.getBytes(CharsetUtil.UTF_8));
                return Utils.bytesToHex(sha.digest());
            }
        });
    }
}
