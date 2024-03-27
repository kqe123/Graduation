package com.example.graduation;

// 사용자 계정 정보 모델 클래스
public class UserAccount
{
    private String idToken; // firebase Uid : 고유 토큰정보
    private String emailId; // email ID
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private String gender; // 성별
    private String age; // 연령대

    public UserAccount() { }

    public String getIdToken() { return idToken; }

    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getEmailId() { return emailId; }

    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getNickname() { return nickname; }

    public void setGender(String gender) {this.gender = gender; }

    public String getGender() { return gender; }

    public String getAge() { return age; }

    public void setAge(String age) { this.age = age; }
}
