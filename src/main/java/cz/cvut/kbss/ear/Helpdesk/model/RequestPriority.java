package cz.cvut.kbss.ear.Helpdesk.model;

public enum RequestPriority {
    CRITICAL("PRIO_CRITICAL"), HIGH("PRIO_HIGH"), MODERATE("PRIO_MODERATE"), LOW("PRIO_LOW");

    private final String name;

    RequestPriority(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
