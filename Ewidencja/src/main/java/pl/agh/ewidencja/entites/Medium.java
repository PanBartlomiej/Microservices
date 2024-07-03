package pl.agh.ewidencja.entites;

/**
 * Klasa typu Enum. Zawiera dozowlone typy medium przekazu sygnału.
 */
public enum Medium {
    copper_cable, twisted_pair, optical_fiber, wifi;
    // Method to get enum element by string
    public static Medium getMediumFromString(String str) {
        for (Medium medium : Medium.values()) {
            if (medium.name().equalsIgnoreCase(str)) {
                return medium;
            }
        }
        // Return null if no match is found
        return null;
    }
}

