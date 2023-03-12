package com.nani.gui.lobby;

public class Player {
    final private int id;
    final private String nickname;

    public Player(int id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
    public int getId() { return id; }
    public String getNickname() { return nickname; }
}
