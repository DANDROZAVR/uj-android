package com.nani.utility.network;

import com.nani.gui.lobby.Player;

import java.util.Random;

public class CommandInterpreter {
    /*
        first word  --- command
        second word --- lobby number (def 0)
        and then description
    */
    public static Random random = new Random();
    public static int extractLobbyNumber(String line) {
        String[] words = line.split(" ");
        if (words.length <= 1) {
            return -1;
        }
        return Integer.parseInt(words[1]);
    }

    public static Command parseLine(String line) {
        String[] words = line.split(" ");
        if (words.length == 0) {
            return null;
        }
        String lobbyCode;
        Player player;
        try {
            switch (words[0]) {
                case "ready_player":
                    lobbyCode = words[1];
                    player = new Player(Integer.parseInt(words[2]), words[3]);
                    return new ReadyPlayer(lobbyCode, player);
                case "create_lobby":
                    if (words.length == 1)
                        lobbyCode = generateLobbyCode(5);
                    else
                        lobbyCode = words[1];
                    return new CreateLobbyCommand(lobbyCode);
                case "disconnect":
                    break;
                case "player_win":
                    lobbyCode = words[1];
                    player = new Player(Integer.parseInt(words[2]), words[3]);
                    return new PlayerWinCommand(lobbyCode, player);
                case "add_player":
                    lobbyCode = words[1];
                    player = new Player(Integer.parseInt(words[2]), words[3]);
                    return new AddPlayerCommand(lobbyCode, player);
                case "create_sudoku_game":
                    lobbyCode = words[1];
                    return new CreateNewSudokuGame(lobbyCode);
                default:
            }
        } catch (RuntimeException ignored) {}
        return null;
    }

    public static String createAddPlayerMessage(String lobbyCode, Player player) {
        return "add_player " + lobbyCode + " " + player.getId() + " " + player.getNickname();
    }
    public static String createWinningRequest(String lobbycode, Player player) {
        return "player_win " + lobbycode + " " + player.getId() + " " + player.getNickname();
    }
    public static String createReadyRequest(String lobbycode, Player player) {
        return "ready_player " + lobbycode + " " + player.getId() + " " + player.getNickname();
    }
    public static String createSudokuGameRequest(String lobbycode) {
        return "create_sudoku_game " + lobbycode;
    }
    public static String createLobbyRespond(String lobbyCode) {
        return "create_lobby " + lobbyCode;
    }
    public static String createLobbyRequest() {
        return "create_lobby ";
    }
    public static String generateLobbyCode(int codeLength) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < codeLength; ++i)
            res.append((char)(random.nextInt(26) + 'a'));
        return res.toString();
    }


    private static class EmptyCommand implements Command {
        @Override
        public Type getCommandType() {
            throw new UnsupportedOperationException("getCommandType");
        }

        @Override
        public String getLobbyCode() {
            throw new UnsupportedOperationException("getLobbyCode");
        }

        @Override
        public Player getPlayer() {
            throw new UnsupportedOperationException("player");
        }


    }
    private static class CreateLobbyCommand extends EmptyCommand {
        String lobbyCode;
        public CreateLobbyCommand(String lobbyCode) {
            this.lobbyCode = lobbyCode;
        }
        @Override
        public String getLobbyCode() {
            return lobbyCode;
        }
        @Override
        public Type getCommandType() {
            return Type.CREATE_LOBBY;
        }
    }
    private static class AddPlayerCommand extends EmptyCommand {
        String lobbyCode;
        Player player;
        public AddPlayerCommand(String lobbyCode, Player player) {
            this.lobbyCode = lobbyCode;
            this.player = player;
        }
        @Override
        public String getLobbyCode() {
            return lobbyCode;
        }
        @Override
        public Player getPlayer() {
            return player;
        }
        @Override
        public Type getCommandType() {
            return Type.ADD_PLAYER;
        }
    }
    private static class PlayerWinCommand extends EmptyCommand {
        String lobbyCode;
        Player player;
        public PlayerWinCommand(String lobbyCode, Player player) {
            this.lobbyCode = lobbyCode;
            this.player = player;
        }
        @Override
        public String getLobbyCode() {
            return lobbyCode;
        }
        @Override
        public Player getPlayer() {
            return player;
        }
        @Override
        public Type getCommandType() {
            return Type.PLAYER_WIN;
        }
    }
    private static class ReadyPlayer extends EmptyCommand {
        String lobbyCode;
        Player player;
        public ReadyPlayer(String lobbyCode, Player player) {
            this.lobbyCode = lobbyCode;
            this.player = player;
        }
        @Override
        public String getLobbyCode() {
            return lobbyCode;
        }
        @Override
        public Player getPlayer() {
            return player;
        }
        @Override
        public Type getCommandType() {
            return Type.READY_PLAYER;
        }
    }
    private static class CreateNewSudokuGame extends EmptyCommand {
        String lobbyCode;
        public CreateNewSudokuGame(String lobbyCode) {
            this.lobbyCode = lobbyCode;
        }
        @Override
        public String getLobbyCode() {
            return lobbyCode;
        }
        @Override
        public Type getCommandType() {
            return Type.CREATE_SUDOKU_GAME;
        }
    }
}
