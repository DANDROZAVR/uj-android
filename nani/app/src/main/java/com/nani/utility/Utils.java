package com.nani.utility;

import com.nani.gui.lobby.Player;

import java.util.Random;

public class Utils {
    static public String name = "dandrozavr";
    public static Player loadOwnerPlayer() {
        Random random = new Random();
        int val = random.nextInt();
        return new Player(val, name);
    }
}
