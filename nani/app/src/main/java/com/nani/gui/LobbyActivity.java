package com.nani.gui;

import static com.nani.gui.LobbyChoosingActivity.client;
import static com.nani.gui.sudoku.SudokuBoardActivity.sudokuId;
import static com.nani.gui.sudoku.SudokuBoardSingleLobbyActivity.lobbyNumberId;
import static com.nani.gui.sudoku.SudokuBoardSingleLobbyActivity.playersNumberId;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.example.nani.R;
import com.nani.gui.lobby.Player;
import com.nani.gui.sudoku.SudokuBoardSingleLobbyActivity;
import com.nani.utility.Utils;
import com.nani.utility.network.Command;
import com.nani.utility.network.CommandInterpreter;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends MainActivity {
    private List<Player> players;
    private Player ownerPlayer;
    private List<TextView> nicknames_status;
    private List<Player> ready;
    private String lobbyCode;
    private TextView text_sudoku_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);
        ownerPlayer = Utils.loadOwnerPlayer();
        players = new ArrayList<>();
        ready = new ArrayList<>();
        nicknames_status = new ArrayList<>();

        players.add(ownerPlayer);
        lobbyCode = getIntent().getStringExtra(lobbyNumberId);

        nicknames_status.add(findViewById(R.id.nickname_lobby_1));
        nicknames_status.add(findViewById(R.id.nickname_lobby_2));
        nicknames_status.add(findViewById(R.id.nickname_lobby_3));
        updateGui();

        text_sudoku_number = findViewById(R.id.text_sudoku_number);
        TextView lobby_code_text = findViewById(R.id.lobby_code_number);
        lobby_code_text.setText(lobbyCode);
        findViewById(R.id.ready_lobby).setOnClickListener(view -> {
            String request = CommandInterpreter.createReadyRequest(lobbyCode, ownerPlayer);
            client.sendRequest(request);
        });
        findViewById(R.id.start_game_sudoku_lobby).setOnClickListener(view -> {
            String request = CommandInterpreter.createSudokuGameRequest(lobbyCode);
            client.sendRequest(request);
        });
        configClient();
    }

    private void configClient() {
        client.setReadingCallback((String line) -> {
            Command command = CommandInterpreter.parseLine(line);
            if (command != null) {
                switch (command.getCommandType()) {
                    case ADD_PLAYER:
                        players.add(command.getPlayer());
                        runOnUiThread(this::updateGui);
                        break;
                    case REMOVE_PLAYER:
                        players.remove(command.getPlayer());
                        runOnUiThread(this::updateGui);
                        break;
                    case CREATE_SUDOKU_GAME:
                        runOnUiThread(this::startGameSudoku);
                        break;
                    case READY_PLAYER:
                        ready.add(command.getPlayer());
                        runOnUiThread(this::updateGui);
                        break;
                }
            }
        });
    }

    private void updateGui() {
        for (int i = 0; i < players.size(); ++i) {
            nicknames_status.get(i).setText(players.get(i).getNickname());
            nicknames_status.get(i).setTextColor(Color.RED);
        }
        for (int i = 0; i < players.size(); ++i)
            for (Player p : ready)
                if (p.getId() == players.get(i).getId()) {
                    nicknames_status.get(i).setTextColor(Color.GREEN);
                }
    }

    public void startGameSudoku() {
        int value = 1;
        String proposedSudoku = text_sudoku_number.getText().toString();
        System.out.println(proposedSudoku);
        try {
            value = Integer.parseInt(proposedSudoku);
            if (value < 0 || value > 30) {
                value = 1;
            }
        } catch (Exception ignored) {}

        Intent intent = new Intent(LobbyActivity.this, SudokuBoardSingleLobbyActivity.class);
        intent.putExtra(sudokuId, value);
        intent.putExtra(playersNumberId, Math.min(players.size(), nicknames_status.size()));
        intent.putExtra(lobbyNumberId, lobbyCode);
        startActivity(intent);
    }
}
