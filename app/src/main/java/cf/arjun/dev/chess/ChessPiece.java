package cf.arjun.dev.chess;

public class ChessPiece {

    public int col, row;
    public ChessPlayer player;
    public ChessRank rank;
    public int resId;

    public ChessPiece (int col, int row, ChessPlayer player, ChessRank rank, int resId) {
        this.col = col;
        this.row = row;
        this.player = player;
        this.rank = rank;
        this.resId = resId;
    }

}
