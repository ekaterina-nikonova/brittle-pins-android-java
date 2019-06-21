package com.brittlepins.brittlepins.api.model;

public class Board {
    private String id;
    private String name;
    private String description;
    private String image;
    private boolean expanded;

    public Board(String id, String name, String description, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.expanded = false;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImageURL() {
        return this.image;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void toggleExpanded() {
        this.expanded = !this.expanded;
    }
}
