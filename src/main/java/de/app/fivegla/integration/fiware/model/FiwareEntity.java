package de.app.fivegla.integration.fiware.model;

import de.app.fivegla.integration.fiware.api.FiwareType;

/**
 * The FiwareEntity interface represents an entity in the Fiware system.
 * Implementing classes should provide a JSON representation of the entity.
 */
public interface FiwareEntity {

    /**
     * Converts the FiwareEntity object to JSON format.
     *
     * @return The FiwareEntity object in JSON format.
     */
    String asJson();

    default String locationAsJson(double latitude, double longitude) {
        if (latitude == 0.0 && longitude == 0.0) {
            return "{}";
        } else {
            return "{" +
                    "  \"type\":\"" + FiwareType.GEO_JSON.getKey() + "\"," +
                    "  \"value\": {" +
                    "    \"type\":\"Point\"," +
                    "    \"coordinates\": [" + longitude + "," + latitude + "]" +
                    "  }" +
                    "}";
        }
    }

    /**
     * Returns the ID of the FiwareEntity object.
     *
     * @return The ID of the FiwareEntity object.
     */
    String getId();
}
