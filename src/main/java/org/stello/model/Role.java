package org.stello.model;

public enum Role {

    EMPLOYEE(100),
    LEAD(200),
    MANAGER(300),
    SR_MANAGER(400),
    DIRECTOR(500),
    SR_DIRECTOR(600),
    VP(700),
    SR_VP(800),
    CTO(1000),
    CEO(1001),
    COMPANY(1000000);

    int seniority;

    Role(int seniority) {
        this.seniority = seniority;
    }

    public int getSeniority() {
        return seniority;
    }

    public void setSeniority(int seniority) {
        this.seniority = seniority;
    }
}
