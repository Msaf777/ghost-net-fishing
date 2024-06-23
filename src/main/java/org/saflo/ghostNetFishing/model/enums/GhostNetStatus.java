package org.saflo.ghostNetFishing.model.enums;

public enum GhostNetStatus {
    REPORTED("Gemeldet"),
    RECOVERY_PENDING("Bergung ausstehend"),
    RECOVERED("Geborgen"),
    LOST("Verschollen"),
    UNKNOWN("Unbekannt");

    private final String germanStatus;

    GhostNetStatus(String germanStatus) {
        this.germanStatus = germanStatus;
    }

    public String getGermanStatus() {
        return germanStatus;
    }

    public static GhostNetStatus fromStatus(GhostNetStatus status) {
        return status != null ? status : UNKNOWN;
    }
}

