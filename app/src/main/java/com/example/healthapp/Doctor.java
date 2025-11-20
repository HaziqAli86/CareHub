package com.example.healthapp; // Make sure this matches your actual package name

import java.io.Serializable;

// Implementing Serializable allows us to pass this entire object
// from the Home Screen to the Detail Screen via Intent
public class Doctor implements Serializable {

    private String id;
    private String name;
    private String specialty;
    private String hospital;
    private String imageUrl; // URL from Firebase Storage or a web link
    private double rating;
    private int patients;
    private int experience;

    // 1. Empty Constructor (REQUIRED for Firebase Firestore)
    // Firebase needs this to create the object from the database
    public Doctor() {
    }

    // 2. Full Constructor (Useful for creating dummy data)
    public Doctor(String id, String name, String specialty, String hospital, String imageUrl, double rating, int patients, int experience) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.hospital = hospital;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.patients = patients;
        this.experience = experience;
    }

    // 3. Getters and Setters (Right-click inside class -> Generate -> Getter and Setter)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getPatients() { return patients; }
    public void setPatients(int patients) { this.patients = patients; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
}