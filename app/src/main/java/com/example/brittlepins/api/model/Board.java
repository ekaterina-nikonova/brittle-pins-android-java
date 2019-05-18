package com.example.brittlepins.api.model;

public class Board {
    private String id;
    private String name;
    private String description;
    private String image;

    public Board(String id, String name, String description, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getName() {
        return this.name;
    }
}
