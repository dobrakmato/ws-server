package sk.emefka.ws.wsserver.actions.data;

import lombok.Data;
import org.json.JSONObject;
import sk.emefka.ws.wsserver.actions.JSONActionDataAdapter;

@Data
public class ClientDetailsReportData extends JSONActionDataAdapter {

    private String browser;
    private String os;
    private String platform;

    @Override
    public void fromJson(JSONObject json) {
        browser = json.optString("brw", null);
        os = json.optString("os", null);
        platform = json.optString("plf", null);
    }

    @Override
    public void toJson(JSONObject json) {
        json.put("brw", browser);
        json.put("os", os);
        json.put("plf", platform);
    }
}
