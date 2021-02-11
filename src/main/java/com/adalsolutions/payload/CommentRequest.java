package com.adalsolutions.payload;

import javax.validation.constraints.NotEmpty;

public class CommentRequest {
    private int postId;

    @NotEmpty
    private String username;

    @NotEmpty
    private String comment;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
