package sk.emefka.ws.wsserver.actions;

public interface ActionData {
    void decode(String data);

    String encode();
}
