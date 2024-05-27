package com.example.graduation;

public class Review {
    private String content;
    private String nickname;
    private Float score;
    private String alcohol_name;
    private String age;
    private String gender;


    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Float getScore() { return score; }

    public void setScore(Float score) { this.score = score; }

    public String getAlcohol_name() { return alcohol_name; }

    public void setAlcohol_name(String alcohol_name) { this.alcohol_name = alcohol_name; }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
