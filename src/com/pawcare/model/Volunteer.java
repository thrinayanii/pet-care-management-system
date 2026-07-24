package com.pawcare.model;

public class Volunteer extends User {
    private String preferredTask;

    public Volunteer() { super(); }

    public Volunteer(int id, String firstName, String lastName, String email, String passwordHash, String phone, String preferredTask) {
        super(id, firstName, lastName, email, passwordHash, phone, "volunteer");
        this.preferredTask = preferredTask;
    }

    public String getPreferredTask() { return preferredTask; }
    public void setPreferredTask(String preferredTask) { this.preferredTask = preferredTask; }
}