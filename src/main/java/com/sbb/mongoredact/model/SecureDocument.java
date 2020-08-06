package com.sbb.mongoredact.model;

import java.util.List;

public class SecureDocument {

    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
