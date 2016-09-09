package sk.emefka.ws.wsserver.actions;

/*
 * {
 *   "act": "cdr",
 *   "data": {
 *      "dat1": 76.33,
 *      "dat2": "string"
 *   }
 * }
 *
 */

import lombok.Data;

@Data
public class Action {
    private final ActionType type;
    private final ActionData data;
}
