package cz.cvut.kbss.ear.Helpdesk.model;

public enum EmployeeRole {
    COMMON("ROLE_COMMON"), MANAGER("ROLE_MANAGER");

    private final String name;

    EmployeeRole(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
