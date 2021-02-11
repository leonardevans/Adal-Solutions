package com.adalsolutions.models;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    String username;
    private String comment;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp dateCommented;

    @ManyToOne()
    @JoinColumn(referencedColumnName = "id")
    private Post post;

    public Comment() {
    }

    public Comment(String username, String comment) {
        this.username = username;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Timestamp getDateCommented() {
        return dateCommented;
    }

    public void setDateCommented(Timestamp dateCommented) {
        this.dateCommented = dateCommented;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
