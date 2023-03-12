package com.nani.utility.network;

import com.nani.gui.lobby.Player;

public interface Command {
    enum Type {
        CREATE_LOBBY, // to-server part
        ADD_PLAYER, REMOVE_PLAYER, // to-player part
        READY_PLAYER, CREATE_SUDOKU_GAME, // in the lobby
        PLAYER_WIN // during the game
    }
    Type getCommandType();
    /*-----------*/
    String getLobbyCode(); // CREATE_LOBBY
    /**/
    Player getPlayer();
    /**/
}
