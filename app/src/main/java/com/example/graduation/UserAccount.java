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
    private String degree; // 사용자 취향(도수)
    private String price; // 사용자 취향(가격)
    private int sweetness; // 사용자 취향(단맛)
    private int bitterness; // 사용자 취향(쓴맛)
    private Boolean carbonated; // 사용자 취향(탄산)
    private String recom1st;
    private String recom2nd;
    private String recom3rd;





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

    public String getDegree() { return degree; }

    public void setDegree(String degree) { this.degree = degree; }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price; }

    public int getSweetness() { return sweetness; }

    public void setSweetness(int sweetness) { this.sweetness = sweetness; }

    public int getBitterness() { return bitterness; }

    public void setBitterness(int bitterness) { this.bitterness = bitterness; }

    public Boolean getCarbonated() { return carbonated; }

    public void setCarbonated(Boolean carbonated) { this.carbonated = carbonated; }

    public String getRecom1st() { return recom1st;}

    public void setRecom1st(String recom1st) { this.recom1st = recom1st; }

    public String getRecom2nd() { return recom2nd; }

    public void setRecom2nd(String recom2nd) { this.recom2nd = recom2nd; }

    public String getRecom3rd() { return recom3rd; }

    public void setRecom3rd(String recom3rd) { this.recom3rd = recom3rd; }
}
