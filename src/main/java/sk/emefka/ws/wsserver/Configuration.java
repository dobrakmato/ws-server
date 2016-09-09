package sk.emefka.ws.wsserver;

import lombok.Getter;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public final class Configuration {

    private JSONObject confRoot;
    private int listenPort;
    private String listenAddress;
    private String websocketPath;
    private String serverStatusPath;
    private String clientListPath;
    private String backendAddress;
    private int backendPort;

    public Configuration(Path path) {
        this.readJson(path);
    }

    private void readJson(Path path) {
        try {
            String json = new String(Files.readAllBytes(path), Charset.forName("UTF-8"));
            JSONObject obj = new JSONObject(json);

            configureFromJson(obj);
            confRoot = obj;
        } catch (IOException e) {
            throw new RuntimeException("Cannot load configuration file!", e);
        }
    }

    private void configureFromJson(JSONObject obj) {
        JSONObject listen = obj.has("listen") ? obj.getJSONObject("listen") : new JSONObject();
        JSONObject backend = obj.has("backend") ? obj.getJSONObject("backend") : new JSONObject();
        JSONObject paths = obj.has("paths") ? obj.getJSONObject("paths") : new JSONObject();

        this.listenPort = listen.optInt("port", 8080);
        this.listenAddress = listen.optString("address", "0.0.0.0");

        this.backendPort = backend.optInt("port", 8081);
        this.backendAddress = backend.optString("address", "127.0.0.1");

        this.websocketPath = paths.optString("webSocket", "/live");
        this.serverStatusPath = paths.optString("serverStatus", "/server-status");
        this.clientListPath = paths.optString("clientList", "/clients");
    }
}
