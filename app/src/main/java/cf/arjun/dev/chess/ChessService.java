package cf.arjun.dev.chess;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class ChessService extends Service {

    private final MyBinder binder = new MyBinder();
    public ChessModel chessModel;
    public MediaPlayer player;

    public ChessService () {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    class MyBinder extends Binder {

        public ChessService getInstance () {
            player = MediaPlayer.create(getApplicationContext(), R.raw.sound_move);
            return ChessService.this;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        player.release();
        player = null;

    }
}