package cz.cvut.kbss.ear.Helpdesk.model;

public enum RequestState {

    NEW("state_new"), ASSIGNED("state_assigned"), RESOLVED("state_resolved"), CONFIRMED("state_confirmed"), CLOSED("state_closed");

    private final String name;

    RequestState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
