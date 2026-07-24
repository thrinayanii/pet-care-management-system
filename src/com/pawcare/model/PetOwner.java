package com.pawcare.model;

public class PetOwner extends User {
    public PetOwner() { super(); }

    public PetOwner(int id, String firstName, String lastName, String email, String passwordHash, String phone) {
        super(id, firstName, lastName, email, passwordHash, phone, "user");
    }
}