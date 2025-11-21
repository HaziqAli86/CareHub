package com.example.healthapp;

public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private String date;  // e.g., "Mon, 03 July"
    private String time;  // e.g., "09:00 AM"
    private String status; // "upcoming", "completed", or "canceled"
    private String patientName;

    // 1. Empty Constructor (Required for Firebase)
    public Appointment() {
    }

    // 2. Full Constructor
    public Appointment(String appointmentId, String patientId, String doctorId, String doctorName, String doctorSpecialty, String date, String time, String status, String patientName) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
        this.date = date;
        this.time = time;
        this.status = status;
        this.patientName = patientName;
    }

    // 3. Getters and Setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDoctorSpecialty() { return doctorSpecialty; }
    public void setDoctorSpecialty(String doctorSpecialty) { this.doctorSpecialty = doctorSpecialty; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
}