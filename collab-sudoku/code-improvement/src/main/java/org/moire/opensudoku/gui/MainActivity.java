package org.moire.opensudoku.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.moire.opensudoku.R;
import org.moire.opensudoku.utils.Const;


public class MainActivity extends AppCompatActivity {

    private EditText boardNameTextEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardNameTextEdit = (EditText) findViewById(R.id.game_name);

        Button createGameButton = (Button) findViewById(R.id.create_game_button);
        createGameButton.setOnClickListener(new CreateGameClickListener());

        Button joinGameButton = (Button) findViewById(R.id.join_game_button);
        joinGameButton.setOnClickListener(new JoinGameClickListener());
    }

    private class CreateGameClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String boardName = boardNameTextEdit.getText().toString();

            Log.d(Const.TAG, "Creating game with board name: " + boardName);

            Intent i = new Intent(MainActivity.this, SudokuPlayActivity.class);
            i.putExtra(SudokuPlayActivity.SUDOKU_BOARD_NAME, boardName);
            i.putExtra(SudokuPlayActivity.SUDOKU_JOIN_GAME, false);
            startActivity(i);
        }
    }

    private class JoinGameClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String boardName = boardNameTextEdit.getText().toString();

            Log.d(Const.TAG, "Joining game with board name: " + boardName);

            Intent i = new Intent(MainActivity.this, SudokuPlayActivity.class);
            i.putExtra(SudokuPlayActivity.SUDOKU_BOARD_NAME, boardName);
            i.putExtra(SudokuPlayActivity.SUDOKU_JOIN_GAME, true);
            startActivity(i);
        }
    }
}
