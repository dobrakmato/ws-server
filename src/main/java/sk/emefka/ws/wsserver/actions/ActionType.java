package sk.emefka.ws.wsserver.actions;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum ActionType {
    CLIENT_DETAILS_REPORT("cdr"),;

    @Getter
    private final String token;

    ActionType(String token) {
        this.token = token;
    }

    private static Map<String, ActionType> ACTIONS;

    static {
        ActionType[] types = values();
        ACTIONS = new HashMap<>(types.length);
        for (ActionType type : types) {
            if (ACTIONS.containsKey(type.getToken())) {
                throw new IllegalStateException("Two types with same token " + type.getToken() + " " + type);
            }
            ACTIONS.put(type.getToken(), type);
        }
    }

    public static ActionType fromToken(String token) {
        return ACTIONS.get(token);
    }
}
