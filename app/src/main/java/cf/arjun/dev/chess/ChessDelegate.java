package cf.arjun.dev.chess;

public interface ChessDelegate {
    ChessPiece pieceAt (Square square);
    void movePiece(Square from, Square to);
}
