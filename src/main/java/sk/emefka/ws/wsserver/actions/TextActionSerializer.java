package sk.emefka.ws.wsserver.actions;

import lombok.experimental.UtilityClass;
import sk.emefka.ws.wsserver.actions.data.ClientDetailsReportData;

@UtilityClass
public class TextActionSerializer {

    // token-data

    public String serialize(Action action) {
        return action.getType().getToken() + "-" + action.getData().encode();
    }

    public Action deserialize(String string) {
        int index = string.indexOf('-');
        String typeString = string.substring(0, index);
        String dataString = string.substring(index + 1);
        ActionType type = ActionType.fromToken(typeString);
        ActionData data = deserializeData(type, dataString);
        return new Action(type, data);
    }

    private static ActionData deserializeData(ActionType type, String part) {
        switch (type) {
            case CLIENT_DETAILS_REPORT:
                ClientDetailsReportData data = new ClientDetailsReportData();
                data.decode(part);
                return data;
            default:
                return null;
        }
    }
}
