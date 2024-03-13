package cf.arjun.dev.chess;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ChessDelegate, ServiceConnection {

    ChessView chessView;
    private TextView title;
    private ChessService game = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeServiceCall();
        setupUIViews();
        initButtons();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (game != null) {
            chessView.invalidate();
        }
    }

    private void makeServiceCall () {
        Intent intent = new Intent(this, ChessService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        startService(intent);
    }

    private void setupUIViews () {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        chessView = findViewById(R.id.chessView);
        title = findViewById(R.id.tvChess);
    }

    private void initButtons () {
        title.setOnLongClickListener( v -> {
            if (game != null) {
                game.chessModel.reset();
                chessView.invalidate();
            }
            return true;
        });
        title.setOnClickListener( v -> Toast.makeText(MainActivity.this, "Press & Hold", Toast.LENGTH_SHORT).show());
    }

    @Override
    public ChessPiece pieceAt(Square square) {
        if (game != null) {
            return game.chessModel.pieceAt(square);
        }
        return null;
    }

    @Override
    public void movePiece(Square from, Square to) {
        if (from.col == to.col && from.row == to.row) return;
        if (game != null) {
            game.chessModel.movePiece(from, to);
            chessView.invalidate();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ChessService.MyBinder binder = (ChessService.MyBinder)service;
        game = binder.getInstance();
        game.chessModel = new ChessModel();
        chessView.chessDelegate = MainActivity.this;
        chessView.invalidate();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        game = null;
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Exit !")
                .setMessage("You will lose all the progress made to the current.\nAre you sure?")
                .setPositiveButton("Exit Anyway!", (dialog1, which) -> super.onBackPressed())
                .setNegativeButton("Discard", (dialog2, which) -> {
                })
                .setCancelable(false)
                .show();
    }
}