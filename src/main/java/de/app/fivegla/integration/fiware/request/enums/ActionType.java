package de.app.fivegla.integration.fiware.request.enums;

import lombok.Getter;

/**
 * Action type.
 */
@Getter
public enum ActionType {

    APPEND("append");

    private final String key;

    ActionType(String key) {
        this.key = key;
    }
}
