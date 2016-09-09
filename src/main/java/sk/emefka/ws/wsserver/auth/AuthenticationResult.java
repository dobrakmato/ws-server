package sk.emefka.ws.wsserver.auth;

import lombok.Data;

/**
 * Represents result (failure or success) of authentication attempt
 * with required fields.
 */
@Data
public class AuthenticationResult {
    private final int databaseId;
    private final String username;
}
