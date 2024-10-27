package cz.cvut.kbss.ear.Helpdesk.model;

public enum DepartmentType {
    IT("TYPE_IT"), ACCOUNT("TYPE_ACCOUNT"), BILLING("TYPE_BILLING");

    private final String name;

    DepartmentType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
