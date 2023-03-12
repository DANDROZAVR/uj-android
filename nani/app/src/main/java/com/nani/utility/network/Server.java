package com.nani.utility.network;

import android.util.Pair;

import com.nani.gui.lobby.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    ServerSocket serverSocket;
    Map<String, List<Pair<Socket, Player>>> map;

    public Server() {
        map = new HashMap<>();
        Thread temp = new Thread(() -> {
            try {
                System.out.println("connecting server");
                serverSocket = new ServerSocket(10130);
                while(!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    Thread t = new Thread(() -> {
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                String newLine = reader.readLine();
                                if (newLine != null) {
                                    processLine(newLine, clientSocket);
                                    System.out.println("server got: " + newLine);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    t.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        temp.start();
    }

    private void processLine(String newLine, Socket clientSocket) {
        Command command = CommandInterpreter.parseLine(newLine);
        if (command == null) {
            System.out.println("unrecognized command: " + newLine);
            return;
        }
        Player player;
        String lobbyCode;
        switch (command.getCommandType()) {
            case CREATE_LOBBY:
                lobbyCode = command.getLobbyCode();
                System.out.println("server adding new lobby: " + lobbyCode);
                if (!map.containsKey(lobbyCode)) {
                    map.put(lobbyCode, new ArrayList<>());
                }
                String message = CommandInterpreter.createLobbyRespond(lobbyCode);
                sendMessage(clientSocket, message);
                break;
            case ADD_PLAYER:
                player = command.getPlayer();
                lobbyCode = command.getLobbyCode();
                if (map.containsKey(lobbyCode)) {
                    for (Pair<Socket, Player> pair : map.get(lobbyCode)) {
                        Socket s = pair.first;
                        message = CommandInterpreter.createAddPlayerMessage(lobbyCode, player);
                        String message2 = CommandInterpreter.createAddPlayerMessage(lobbyCode, pair.second);
                        sendMessage(s, message);
                        sendMessage(clientSocket, message2);
                    }
                    map.get(lobbyCode).add(new Pair<>(clientSocket, player));
                } else {
                    System.err.println("error. Can't add player to the lobby " + lobbyCode +  ". The lobby doesn't exist");
                }
                break;
            case PLAYER_WIN:
                player = command.getPlayer();
                lobbyCode = command.getLobbyCode();
                if (map.containsKey(lobbyCode)) {
                    for (Pair<Socket, Player> pair : map.get(lobbyCode)) {
                        Socket s = pair.first;
                        message = CommandInterpreter.createWinningRequest(lobbyCode, player);
                        sendMessage(s, message);
                    }
                } else {
                    System.err.println("error. Can't win player in the game " + lobbyCode +  ". The lobby doesn't exist");
                }
                break;
            case READY_PLAYER:
                player = command.getPlayer();
                lobbyCode = command.getLobbyCode();
                if (map.containsKey(lobbyCode)) {
                    for (Pair<Socket, Player> pair : map.get(lobbyCode)) {
                        Socket s = pair.first;
                        message = CommandInterpreter.createReadyRequest(lobbyCode, player);
                        sendMessage(s, message);
                    }
                } else {
                    System.err.println("error. Can't win player in the game " + lobbyCode +  ". The lobby doesn't exist");
                }
                break;
            case CREATE_SUDOKU_GAME:
                lobbyCode = command.getLobbyCode();
                if (map.containsKey(lobbyCode)) {
                    for (Pair<Socket, Player> pair : map.get(lobbyCode)) {
                        Socket s = pair.first;
                        message = CommandInterpreter.createSudokuGameRequest(lobbyCode);
                        sendMessage(s, message);
                    }
                } else {
                    System.err.println("error. Can't start the game in " + lobbyCode +  ". The lobby doesn't exist");
                }
                break;
        }
    }
    private void sendMessage(Socket s, String message) {
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
            writer.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}