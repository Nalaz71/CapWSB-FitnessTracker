package pl.wsb.fitnesstracker.training.internal;

/**
 * Enum representing different types of activities for training.
 * Each activity type has a display name associated with it.
 */
public enum ActivityType {

    // Enum constants representing different activity types
    RUNNING("Running"),
    CYCLING("Cycling"),
    WALKING("Walking"),
    SWIMMING("Swimming"),
    TENNIS("Tenis");


    // Display name for the activity type
    private final String displayName;

    /**
     * Constructor for ActivityType enum.
     *
     * @param displayName the display name of the activity type
     */
    ActivityType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the activity type.
     *
     * @return the display name of the activity type
     */
    public String getDisplayName() {
        return displayName;
    }

}
