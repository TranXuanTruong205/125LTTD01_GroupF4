package com.dinerestaurant.app.model;

public class User {
    private String phone;
    private String email;
    private String fullName;
    private String dob;
    private String gender;
    private String location;

    public User() {}

    public User(String phone, String email, String fullName) {
        this.phone = phone;
        this.email = email;
        this.fullName = fullName;
    }

    // GET – SET
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}