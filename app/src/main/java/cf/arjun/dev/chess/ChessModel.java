package cf.arjun.dev.chess;

import java.util.ArrayList;
import java.util.List;

public class ChessModel {
    private final List<ChessPiece> piecesBox;
    private int ActivePlayer = 0;

    public ChessModel () {
        piecesBox = new ArrayList<>();
        reset();
    }

    private void isGameOver () {
        int count = 0;
        ChessPiece piece = null;
        for (ChessPiece p : piecesBox) {
            if (p.rank == ChessRank.KING) {
                count++;
                piece = p;
            }
        }
        if (count == 1) {
            ChessPiece finalPiece = piece;
            piecesBox.removeIf(p -> p.player != finalPiece.player);
        }

    }

    public void movePiece (Square from, Square to) {

        if ((from.col < to.col && to.col > 7) || (from.col > to.col && to.col < 0)) return;
        if ((from.row < to.row && to.row > 7) || (from.row > to.row && to.row < 0)) return;

        ChessPiece piece = pieceAt(from);
        if (piece != null) {

            if (ActivePlayer == 1 && piece.player == ChessPlayer.BLACK) return;
            if (ActivePlayer == 2 && piece.player == ChessPlayer.WHITE) return;

            if (canMove(from, to)) {

                ChessPiece secondPiece = pieceAt(to);
                if (secondPiece != null) {
                    if (secondPiece.player.equals(piece.player)) return;
                    piecesBox.remove(secondPiece);
                }

                piece.col = to.col;
                piece.row = to.row;

                ActivePlayer = (piece.player == ChessPlayer.WHITE) ? 2 : 1;

                isGameOver();

            }
        }
    }

    private boolean canMove (Square from, Square to) {
        if (from.col == to.col && from.row == to.row) return false;
        ChessPiece movingPiece = pieceAt(from);
        if (movingPiece == null) return false;
        switch (movingPiece.rank) {
            case KNIGHT: {
                return canKnightMove(from, to);
            }
            case ROCK: {
                return canRookMove(from, to);
            }
            case BISHOP: {
                return canBishopMove(from, to);
            }
            case QUEEN: {
                return canQueenMove(from, to);
            }
            case KING: {
                return canKingMove(from, to);
            }
            case PAWN: {
                return canPawnMove(from, to);
            }
        }
        return true;
    }

    private boolean canRookMove (Square from, Square to) {
        return (from.col == to.col && isClearVerticallyBetween(from, to)) || (from.row == to.row && isClearHorizontallyBetween(from, to));
    }

    private boolean canKnightMove (Square from, Square to) {
        return (Math.abs(from.col - to.col) == 2 && Math.abs(from.row - to.row) == 1) ||
                (Math.abs(from.col - to.col) == 1 && Math.abs(from.row - to.row) == 2);
    }

    private boolean canBishopMove (Square from, Square to) {
        return ((Math.abs(from.col - to.col) == Math.abs(from.row - to.row)) && isClearDiagonally(from, to));
    }

    private boolean canQueenMove (Square from, Square to) {
        return (canRookMove(from, to) || canBishopMove(from, to));
    }

    private boolean canKingMove (Square from, Square to) {
        int dx = Math.abs(from.col - to.col);
        int dy = Math.abs(from.row - to.row);
        return (canQueenMove(from, to) && ((dx == 1 && dy == 1) || (dx + dy == 1)));
    }

    private boolean canPawnMove (Square from, Square to) {
        ChessPiece pawnFrom = pieceAt(from);
        if (from.col == to.col) {
            if (from.row == 1 && pawnFrom.player == ChessPlayer.WHITE) {
                return ((to.row == 2 || to.row == 3) && pieceAt(to) == null);
            } else if (from.row == 6 && pawnFrom.player == ChessPlayer.BLACK) {
                return ((to.row == 4 || to.row == 5) && pieceAt(to) == null);
            } else {
                if (Math.abs(from.row - to.row) == 1 && pieceAt(to) == null) {
                    if (pawnFrom != null) {
                        return (pawnFrom.player == ChessPlayer.WHITE && from.row + 1 == to.row) || (pawnFrom.player == ChessPlayer.BLACK && from.row - 1 == to.row);
                    }
                }
            }
        } else {
            int dx = Math.abs(from.col - to.col);
            int dy = Math.abs(from.row - to.row);
            ChessPiece pawn = pieceAt(to);
            if (pawn != null) {
                return (dx == 1 && dy == 1) && (pawn.player != ChessPlayer.WHITE && from.row + 1 == to.row) ||
                        (pawn.player != ChessPlayer.BLACK && from.row - 1 == to.row);
            } if (from.row == to.row) return false;
        }
        return false;
    }

    private boolean isClearDiagonally (Square from, Square to) {
        int gap = Math.abs(from.col - to.col) - 1;
        if (gap == 0) return true;
        for (int i=1; i<=gap; i++) {
            int nextCol = (to.col > from.col) ? from.col + i : from.col - i;
            int nextRow = (to.row > from.row) ? from.row + i : from.row - i;
            if (pieceAt(new Square(nextCol, nextRow)) != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isClearVerticallyBetween (Square from, Square to) {
        if (from.col != to.col) return false;
        int gap = Math.abs(from.row - to.row) - 1;
        if (gap == 0) return true;
        for (int i=1; i<=gap; i++) {
            int nextRow = (to.row > from.row) ? from.row + i : from.row - i;
            if (pieceAt(new Square(to.col, nextRow)) != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isClearHorizontallyBetween (Square from, Square to) {
        if (from.row != to.row) return false;
        int gap = Math.abs(from.col - to.col) - 1;
        if (gap == 0) return true;
        for (int i=1; i<=gap; i++) {
            int nextCol = (to.col > from.col) ? from.col + i : from.col - i;
            if (pieceAt(new Square(nextCol, to.row)) != null) {
                return false;
            }
        }
        return true;
    }

    public void reset () {

        ActivePlayer = 0;
        piecesBox.clear();

        // Adding Rock, Knight and Bishop

        for (int i=0; i<2; i++) {
            piecesBox.add(new ChessPiece(i*7, 0, ChessPlayer.WHITE, ChessRank.ROCK, R.drawable.rook_white));
            piecesBox.add(new ChessPiece(i*7, 7, ChessPlayer.BLACK, ChessRank.ROCK, R.drawable.rook_black));
            piecesBox.add(new ChessPiece(i*5+1, 0, ChessPlayer.WHITE, ChessRank.KNIGHT, R.drawable.knight_white));
            piecesBox.add(new ChessPiece(i*5+1, 7, ChessPlayer.BLACK, ChessRank.KNIGHT, R.drawable.knight_black));
            piecesBox.add(new ChessPiece(i*3+2, 0, ChessPlayer.WHITE, ChessRank.BISHOP, R.drawable.bishop_white));
            piecesBox.add(new ChessPiece(i*3+2, 7, ChessPlayer.BLACK, ChessRank.BISHOP, R.drawable.bishop_black));
        }

        // Adding King and Queen

        piecesBox.add(new ChessPiece(3, 0, ChessPlayer.WHITE, ChessRank.KING, R.drawable.king_white));
        piecesBox.add(new ChessPiece(4, 7, ChessPlayer.BLACK, ChessRank.KING, R.drawable.king_black));
        piecesBox.add(new ChessPiece(4, 0, ChessPlayer.WHITE, ChessRank.QUEEN, R.drawable.queen_white));
        piecesBox.add(new ChessPiece(3, 7, ChessPlayer.BLACK, ChessRank.QUEEN, R.drawable.queen_black));

        // Adding all Pawns

        for (int i=1; i<7; i+=5) {
            for (int j=0; j<8; j++) {
                if (i == 1) piecesBox.add(new ChessPiece(j, i, ChessPlayer.WHITE, ChessRank.PAWN, R.drawable.pawn_white));
                else piecesBox.add(new ChessPiece(j, i, ChessPlayer.BLACK, ChessRank.PAWN, R.drawable.pawn_black));
            }
        }

    }

    public ChessPiece pieceAt (Square square) {
        for (ChessPiece piece : piecesBox) {
            if (square.col == piece.col && square.row == piece.row) {
                return piece;
            }
        }
        return null;
    }

}
