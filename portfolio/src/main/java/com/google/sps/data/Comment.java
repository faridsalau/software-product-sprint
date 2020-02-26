package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

public class Comment{
    private List<String> comments = new ArrayList<>();

    public void addComment(String newComment){
        comments.add(newComment);
    }
}