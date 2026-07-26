package com.pawcare.model;

public class Volunteer extends User {
    private Integer preferredServiceId;

    public Volunteer() { super(); }

    public Volunteer(int id, String firstName, String lastName, String email, String passwordHash, String phone, Integer preferredServiceId) {
        super(id, firstName, lastName, email, passwordHash, phone, "volunteer");
        this.preferredServiceId = preferredServiceId;
    }

    public Integer getPreferredServiceId() { return preferredServiceId; }
    public void setPreferredServiceId(Integer preferredServiceId) { this.preferredServiceId = preferredServiceId; }
}