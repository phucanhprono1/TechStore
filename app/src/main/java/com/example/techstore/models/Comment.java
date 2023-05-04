package com.example.techstore.models;

public class Comment {
    private Integer comment_id;
    private String comment;
    private Integer rate;
    private String resp_comment;

    public Integer getComment_id(int comment) {
        return comment_id;
    }

    public void setComment_id(Integer comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment(String productId) {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRate(int productId) {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getResp_comment(String productId) {
        return resp_comment;
    }

    public void setResp_comment(String resp_comment) {
        this.resp_comment = resp_comment;
    }

    public Comment(Integer comment_id, String comment, Integer rate, String resp_comment) {
        this.comment_id = comment_id;
        this.comment = comment;
        this.rate = rate;
        this.resp_comment = resp_comment;
    }

    public Comment() {
    }
}
