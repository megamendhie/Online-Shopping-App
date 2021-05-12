package models;

import java.util.Date;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String uid;
    private String pic;
    private String role;
    private boolean admin;
    private long createdAt;

    public User(){}

    public User(String firstName, String lastName, String email, String uid){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uid = uid;
        this.pic = "";
        this.role = "user";
        this.phone = "";
        this.address = "";
        setAdmin(false);
        createdAt = new Date().getTime();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
