package cf.arjun.dev.chess;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ChessService extends Service {

    private final MyBinder binder = new MyBinder();
    public ChessModel chessModel;

    public ChessService () {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    class MyBinder extends Binder {

        public ChessService getInstance () {
            return ChessService.this;
        }

    }

}