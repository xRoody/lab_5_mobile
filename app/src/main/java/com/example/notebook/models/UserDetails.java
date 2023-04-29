package com.example.notebook.models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserDetails {
    public String id;
    public String user;
    public String firstName;
    public String lastName;
    public String birthday;
    public String avatar;

    public String login;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public LocalDate getBirthday() {
        return LocalDate.parse(birthday);
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday.toString();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("user", user);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("birthday", birthday);
        map.put("avatar", avatar);
        map.put("login", login);
        return map;
    }


}
