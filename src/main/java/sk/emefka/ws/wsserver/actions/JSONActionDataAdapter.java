package sk.emefka.ws.wsserver.actions;

import org.json.JSONObject;

public abstract class JSONActionDataAdapter implements ActionData {

    @Override
    public void decode(String data) {
        fromJson(new JSONObject(data));
    }

    @Override
    public String encode() {
        JSONObject json = new JSONObject();
        toJson(json);
        return json.toString();
    }

    public abstract void fromJson(JSONObject json);

    public abstract void toJson(JSONObject json);
}
