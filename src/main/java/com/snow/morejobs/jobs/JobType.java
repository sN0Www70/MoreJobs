package com.snow.morejobs.jobs;

import java.util.HashMap;
import java.util.Map;

public enum JobType {

    HUNTER("hunter", "Chasseur", false, "chasseur"),
    FARMER("farmer", "Fermier", false, "fermier"),
    INTERIOR_DESIGNER("interiordesigner", "Décorateur", false, "decorateur"),
    GARDENER("gardener", "Jardinier", false, "jardinier"),
    MINER("miner", "Mineur", false, "mineur"),
    THERAPIST("therapist", "Thérapeute", false, "therapeute"),
    JUDGE("judge", "Juge", false, "juge"),
    ARCHITECT("architect", "Architecte", false, "architecte"),
    BARTENDER("bartender", "Barman", false, "barman"),
    STYLIST("stylist", "Styliste", false, "styliste"),
    PUBLICIST("publicist", "Publiciste", false, "publiciste"),
    URBANIST("urbanist", "Urbaniste", false, "urbaniste"),

    MAYOR("mayor", "Maire", false, "maire"),
    POLICE_OFFICER("policeofficer", "Policier", false, "policier"),

    // Jobs secrets (aucun lien LP)
    ALIEN("alien", "Alien", true, null),
    FATETELLER("fateteller", "Voyant", true, null),
    MAD_SCIENTIST("madscientist", "Savant fou", true, null),
    THIEF("thief", "Voleur", true, null),

    NONE("none", "Aucun", false, "none");

    private static final Map<String, JobType> BY_NAME = new HashMap<>();

    static {
        for (JobType job : values()) {
            BY_NAME.put(job.name.toLowerCase(), job);
        }
    }

    private final String name;
    private final String displayName;
    private final boolean secret;
    private final String luckPermsGroup;

    JobType(String name, String displayName, boolean secret, String luckPermsGroup) {
        this.name = name;
        this.displayName = displayName;
        this.secret = secret;
        this.luckPermsGroup = luckPermsGroup;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSecret() {
        return secret;
    }

    public String getLuckPermsGroup() {
        return luckPermsGroup;
    }

    public boolean hasLuckPermsGroup() {
        return luckPermsGroup != null;
    }

    public int getSalary() {
        switch (this) {
            case FARMER: return 5;
            case HUNTER: return 6;
            case MINER: return 7;
            case ARCHITECT: return 8;
            case BARTENDER: return 4;
            case MAYOR: return 10;
            case POLICE_OFFICER: return 7;
            case INTERIOR_DESIGNER: return 5;
            case GARDENER: return 4;
            case JUDGE: return 8;
            case THERAPIST: return 5;
            case STYLIST: return 5;
            case PUBLICIST: return 5;
            case URBANIST: return 6;
            case ALIEN:
            case FATETELLER:
            case MAD_SCIENTIST:
            case THIEF:
            default: return 0;
        }
    }

    public static JobType fromName(String name) {
        return BY_NAME.getOrDefault(name.toLowerCase(), NONE);
    }
}
