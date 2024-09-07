package com.acme.pointbarbackend.platform.iam.application.internal.outboundservices.tokens;

/**
 * Token service
 * <p>
 *     This interface represents the token service.
 *     It is used to generate and validate a token.
 * </p>
 */
public interface TokenService {

    /**
     * Generate a token for a given username
     * @param username the username
     * @return the generated token
     */
    String generateToken(String username);

    /**
     * Get the username from a token
     * @param token the token
     * @return the username
     */
    String getUsernameFromToken(String token);

    /** Validate a token
     * @param token the token
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
}
