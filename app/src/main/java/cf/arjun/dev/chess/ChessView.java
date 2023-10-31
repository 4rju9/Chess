package cf.arjun.dev.chess;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class ChessView extends View {

    private float cellSide = 130f;
    private float originX = 20f;
    private float originY = 450f;
    private final Paint paint = new Paint();
    public ChessDelegate chessDelegate = null;
    private Bitmap movingBitMap = null;
    private ChessPiece movingPiece = null;
    private int fromCol = -1;
    private int fromRow = -1;
    private float movingX = -1;
    private float movingY = -1;
    private final Map<Integer, Bitmap> bitmaps = new HashMap<>();
    private final int[] imgResIDs = {
            R.drawable.king_white,
            R.drawable.queen_white,
            R.drawable.rook_white,
            R.drawable.knight_white,
            R.drawable.bishop_white,
            R.drawable.pawn_white,
            R.drawable.king_black,
            R.drawable.queen_black,
            R.drawable.rook_black,
            R.drawable.knight_black,
            R.drawable.bishop_black,
            R.drawable.pawn_black
    };

    public ChessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        loadBitMaps();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int smaller = Integer.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(smaller, smaller);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (canvas != null) {
            int width = getWidth();
            int height = getHeight();
            float scaleFactor = 0.94f;
            float chessBoardSide = Integer.min(width, height) * scaleFactor;
            cellSide = chessBoardSide / 8f;
            originX = (width - chessBoardSide) / 2f;
            originY = (height - chessBoardSide) / 2f;
        }

        drawChessBoard(canvas);
        drawPiece(canvas);

    }

    private void loadBitMaps () {
        for (int resId : imgResIDs) {
            bitmaps.put(resId, BitmapFactory.decodeResource(getResources(), resId));
        }
    }

    private void drawPiece (Canvas canvas) {

        if (chessDelegate != null) {
            for (int row=0; row<8; row++) {
                for (int col=0; col<8; col++) {
                    ChessPiece piece = chessDelegate.pieceAt(new Square(col, row));
                    if (piece != null) {
                        if (movingPiece != piece) {
                            drawPieceAt(canvas, col, row, piece.resId);
                        }
                    }
                }
            }

            if (movingBitMap != null) {
                canvas.drawBitmap(movingBitMap, null, new RectF(movingX - cellSide/2f, movingY - cellSide/2f, movingX +  cellSide/2f, movingY + cellSide/2f), paint);
            }

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                fromCol = (int)((event.getX() - originX) / cellSide);
                fromRow = 7 - (int)((event.getY() - originY) / cellSide);
                if (chessDelegate != null) {
                    movingPiece = chessDelegate.pieceAt(new Square(fromCol, fromRow));
                    movingBitMap = bitmaps.get(movingPiece.resId);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                movingX = event.getX();
                movingY = event.getY();
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                int col = (int)((event.getX() - originX) / cellSide);
                int row = 7 - (int)((event.getY() - originY) / cellSide);
                movingPiece = null;
                movingBitMap = null;
                if (fromCol != col || fromRow != row) {
                    if (chessDelegate != null) {
                        chessDelegate.movePiece(new Square(fromCol, fromRow), new Square(col, row));
                    }
                } else {
                    invalidate();
                }
                fromCol = -1;
                fromRow = -1;
                break;
            }
        }
        return true;
    }

    private void drawPieceAt (Canvas canvas, int col, int row, int resId) {
        Bitmap bitmap = bitmaps.get(resId);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, null, new RectF(originX + col * cellSide, originY + (7 - row) * cellSide, originX + (col + 1) * cellSide, originY + ((7 - row) + 1) * cellSide), paint);
        }
    }
    
    private void drawChessBoard (Canvas canvas) {

        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {

                if ((row + col) % 2 == 1) paint.setColor(getResources().getColor(R.color.chessGreen));
                else paint.setColor(getResources().getColor(R.color.chessWhite));
                canvas.drawRect(originX + col * cellSide, originY + row * cellSide, originX + (col + 1) * cellSide, originY + (row + 1) * cellSide, paint);

            }
        }

    }

}
