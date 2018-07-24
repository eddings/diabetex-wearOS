package com.muhendis.diabetwatch.model;

/**
 * Created by muhendis on 28.03.2018.
 */

public class UserFirebaseDb {
    String email;
    String password;
    String name;
    String surname;
    String city;
    String assignedDoctorId;
    int isActiveUser;
    int isWatchUser;
    String joinDate;
    String lastSeen;
    String notes;
    String phone;
    String gender;
    String age;
    String token;
    String email_password;
    int pid;


    public UserFirebaseDb() {
    }

    public UserFirebaseDb(String email, String password, String name, String surname, String city, int isActiveUser, String joinDate, String lastSeen, String notes, String phone, String gender, String age, String token, String assignedDoctorId, int isWatchUser, int pid) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.isActiveUser = isActiveUser;
        this.joinDate = joinDate;
        this.lastSeen = lastSeen;
        this.notes = notes;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
        this.token = token;
        this.assignedDoctorId = assignedDoctorId;
        this.pid = pid;
        this.isWatchUser = isWatchUser;
        this.email_password = email+"_"+password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int isActiveUser() {
        return isActiveUser;
    }

    public void setActiveUser(int activeUser) {
        isActiveUser = activeUser;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail_password() {
        return email_password;
    }

    public void setEmail_password(String email_password) {
        this.email_password = email_password;
    }

    public String getAssignedDoctorId() {
        return assignedDoctorId;
    }

    public void setAssignedDoctorId(String assignedDoctorId) {
        this.assignedDoctorId = assignedDoctorId;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
