package com.example.graduation;

public class Comment {
    private String userName;
    private String content;
    private String title;
    private String age;
    private String gender;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Comment() {}

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getAge() { return age; }

    public void setAge(String age) { this.age = age; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }
}
