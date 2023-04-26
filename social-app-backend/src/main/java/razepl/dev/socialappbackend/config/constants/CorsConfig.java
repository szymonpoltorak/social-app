package razepl.dev.socialappbackend.config.constants;

import java.util.List;

/**
 * Configuration for Cross-Origin Resource Sharing (CORS) settings.
 */
public final class CorsConfig {

    /**
     * List of allowed HTTP request methods.
     */
    public static final List<String> ALLOWED_REQUESTS = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

    /**
     * List of allowed frontend server addresses.
     */
    public static final List<String> FRONTEND_ADDRESS = List.of("http://localhost:4200");

    /**
     * HTTP header for specifying the content type of request or response.
     */
    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    /**
     * API pattern for CORS configuration.
     */
    public static final String API_PATTERN = "/api/**";

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private CorsConfig() {
    }
}