package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece
{
    protected final PieceType piece_type;
    protected final int piece_position;
    protected final Alliance piece_alliance;
    protected final boolean is_first_move;
    private final int cached_hashcode;

    Piece(final int piece_Position, final Alliance piece_Alliance, final PieceType piece_type, final boolean is_first_move)
    {
        this.piece_position = piece_Position;
        this.piece_alliance = piece_Alliance;
        this.piece_type = piece_type;
        this.is_first_move = is_first_move;
        this.cached_hashcode = computeHashCode();
    }

    private int computeHashCode() {
        int result = piece_type.hashCode();
        result = 31 * result + piece_alliance.hashCode();
        result = 31 * result + piece_position;
        result = 31 * result + (is_first_move ? 1 : 0);
        return result;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    @Override
    public boolean equals(final Object other)
    {
        if(this == other)   return true;
        if(!(other instanceof Piece))   return false;
        final Piece other_piece = (Piece) other;
        return piece_position == other_piece.piece_position && piece_type == other_piece.getPieceType()
                && piece_alliance == ((Piece) other).getPieceAlliance();
    }

    @Override
    public int hashCode() {
        return this.cached_hashcode;
    }

    public final boolean isFirstMove()
    {
        return this.is_first_move;
    }

    public Alliance getPieceAlliance()
    {
        return piece_alliance;
    }

    public int getPiecePosition()  {  return piece_position;  }

    public PieceType getPieceType() {   return this.piece_type; }

    public int getPieceValue() {
        return this.getPieceType().getPieceValue();
    }

    public enum PieceType
    {
        PAWN("P", 100),
        KNIGHT("N", 300),
        BISHOP("B", 300),
        ROOK("R", 500),
        QUEEN("Q", 900),
        KING("K", 10000);

        private String piece_name;
        private int piece_value;
        PieceType(final String piece_name, final int piece_value)
        {
            this.piece_name = piece_name;
            this.piece_value = piece_value;
        }

        @Override
        public String toString() {   return this.piece_name;    }

        public boolean isKing() {   return piece_name.equals("K");  }

        public boolean isRook() {   return piece_name.equals("R");   }

        public int getPieceValue() {
            return this.piece_value;
        }
    }
}