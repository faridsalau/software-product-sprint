package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

public class Comments {
    private List<String> comments = new ArrayList<>();

    public void addComment(String newComment) {
        comments.add(newComment);
    }
}