package com.example.graduation;

public class Media {
    private String title; // 영상 제목
    private String views; // 조회수
    private String post_date; // 게시일
    private String url; // storage에 저장된 위치
    private String source; // 출처


    public Media() { }

    public void setTitle(String title) { this.title = title; }

    public String getTitle() { return title; }

    public String getViews() { return views; }

    public void setViews(String views) { this.views = views; }

    public String getPost_date() { return post_date; }

    public void setPost_date(String post_date) { this.post_date = post_date; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

}
