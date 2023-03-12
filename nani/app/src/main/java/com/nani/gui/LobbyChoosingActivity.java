package com.nani.gui;

import static com.nani.gui.sudoku.SudokuBoardSingleLobbyActivity.lobbyNumberId;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.nani.R;
import com.nani.utility.Utils;
import com.nani.utility.network.Client;
import com.nani.utility.network.Command;
import com.nani.utility.network.CommandInterpreter;

public class LobbyChoosingActivity extends MainActivity {
    public final static String lobbyNew = "LOBBY_CREATED";
    public final static Client client = new Client();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.err.println("LOBBY ACTIVITY");
        createClient();
        setContentView(R.layout.lobby_choosing);
        findViewById(R.id.home_button_choosing_lobby).setOnClickListener(
                view -> startActivity(new Intent(this, StartActivity.class)));
        EditText text_lobby_number = findViewById(R.id.text_lobby_number);
        findViewById(R.id.enter_lobby_number).setOnClickListener(view -> loadLobby(text_lobby_number.getText().toString()));
        findViewById(R.id.create_lobby_button).setOnClickListener(view -> createLobby());
    }

    private void createClient() {
        boolean runRes = false;
        while(!runRes) {
            runRes = client.run();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // toast
        }

    }

    private void createLobby() {
        String request = CommandInterpreter.createLobbyRequest();
        client.sendRequest(request);
        client.setReadingCallback((String line) -> {
            Command command = CommandInterpreter.parseLine(line);
            if (command != null && command.getCommandType().equals(Command.Type.CREATE_LOBBY)) {
                runOnUiThread(() -> {
                    String lobbyCode = command.getLobbyCode();
                    loadLobby(lobbyCode);
                });
            }
        });

    }

    private void loadLobby(String lobbyCode) {
        client.resetReadingCallback();
        String request = CommandInterpreter.createAddPlayerMessage(lobbyCode, Utils.loadOwnerPlayer()); // check
        client.sendRequest(request);

        Intent intent = new Intent(LobbyChoosingActivity.this, LobbyActivity.class);
        intent.putExtra(lobbyNumberId, lobbyCode);
        startActivity(intent);
    }




}
