package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.Locale;

public abstract class Move
{
    protected final Board board;
    protected final Piece piece;
    protected final int destination_coordinate;
    protected final boolean is_first_move;
    public static final Move NULL_MOVE = new NullMove();

    private Move(Board board, Piece piece, int destination_coordinate)
    {
        this.board = board;
        this.piece = piece;
        this.destination_coordinate = destination_coordinate;
        this.is_first_move = this.piece.isFirstMove();
    }

    private Move(Board board, int destination_coordinate)
    {
        this.board = board;
        this.piece = null;
        this.destination_coordinate = destination_coordinate;
        this.is_first_move = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destination_coordinate;
        result = prime * result + this.piece.hashCode();
        result = prime * result + this.piece.getPiecePosition();
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if(this == other)   return true;
        if(!(other instanceof Move))    return false;
        final Move other_move = (Move) other;
        return getCurrentCoordinate() == ((Move) other).getCurrentCoordinate() &&
                this.getDestinationCoordinate() == other_move.getDestinationCoordinate() &&
                this.getPiece().equals(other_move.getPiece());
    }

    private int getCurrentCoordinate() {
        return getPiece().getPiecePosition();
    }

    public int getDestinationCoordinate() {
        return destination_coordinate;
    }

    public Piece getPiece() {
        return piece;
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastleMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    public Board execute()
    {
        final Board.Builder builder = new Board.Builder();
        for(final Piece piece : this.board.getCurrentPlayer().getActivePieces())
        {
            if(!this.piece.equals(piece))
                builder.setPiece(piece);
        }
        for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
        {
            builder.setPiece(piece);
        }
        builder.setPiece(this.piece.movePiece(this));
        builder.setNextPlayer(this.board.getCurrentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public static final class NormalMove extends Move
    {
        public NormalMove(final Board board, final Piece piece, final int destination_coordinate)
        {
            super(board, piece, destination_coordinate);
        }

        @Override
        public boolean equals(Object other) {
            return (this == other || other instanceof NormalMove) && super.equals(other);
        }

        @Override
        public String toString() {
            return piece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destination_coordinate);
        }
    }

    public static class CaptureMove extends Move
    {
        Piece attacked_piece;

        public CaptureMove(final Board board, final Piece piece, final int destination_coordinate, final Piece attacked_piece)
        {
            super(board, piece, destination_coordinate);
            this.attacked_piece = attacked_piece;
        }

        @Override
        public int hashCode() {
            return this.attacked_piece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if(this == other)   return true;
            if(!(other instanceof CaptureMove)) return false;
            final CaptureMove other_move = (CaptureMove) other;
            return this.getAttackedPiece().equals(other_move.getAttackedPiece()) &&  super.equals(other_move);
        }

        @Override
        public Board execute() {
            return null;
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attacked_piece;
        }
    }

    public static final class PawnMove extends Move
    {
        public PawnMove(final Board board, final Piece piece, final int destination_coordinate)
        {
            super(board, piece, destination_coordinate);
        }
    }

    public static class PawnCapture extends CaptureMove
    {
        public PawnCapture(final Board board, final Piece piece, final int destination_coordinate, final Piece attacked_piece)
        {
            super(board, piece, destination_coordinate, attacked_piece);
        }
    }

    public static final class EnPassantMove extends PawnCapture
    {
        public EnPassantMove(final Board board, final Piece piece, final int destination_coordinate, final Piece attacked_piece)
        {
            super(board, piece, destination_coordinate, attacked_piece);
        }
    }

    public static final class PawnJumpMove extends Move
    {
        public PawnJumpMove(final Board board, final Piece piece, final int destination_coordinate)
        {
            super(board, piece, destination_coordinate);
        }

        @Override
        public Board execute()
        {
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece : this.board.getCurrentPlayer().getActivePieces())
            {
                if(!this.piece.equals(piece))
                    builder.setPiece(piece);
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
                builder.setPiece(piece);

            final Pawn moved_pawn = (Pawn) this.piece.movePiece(this);
            builder.setPiece(moved_pawn);
            builder.setEnPassantPawn(moved_pawn);
            builder.setNextPlayer(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString() {
            return "foo";
        }
    }

    static abstract class CastleMove extends Move
    {
        protected final Rook castle_rook;
        protected final int castle_rook_start;
        protected final int castle_rook_destination;

        public CastleMove(final Board board, final Piece piece, final int destination_coordinate,
                          final Rook castle_rook, final int castle_rook_start, final int castle_rook_destination)
        {
            super(board, piece, destination_coordinate);
            this.castle_rook = castle_rook;
            this.castle_rook_start = castle_rook_start;
            this.castle_rook_destination = castle_rook_destination;
        }

        public Rook getCastleRook() {
            return this.castle_rook;
        }

        @Override
        public boolean isCastleMove() {
            return true;
        }

        @Override
        public Board execute()
        {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces())
            {
                if (!this.piece.equals(piece) && !this.castle_rook.equals(piece))
                    builder.setPiece(piece);
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
                    builder.setPiece(piece);
            builder.setPiece(this.piece.movePiece(this));
            //TODO have to add isfirstmove parameter to rook, since a newly created rook has not moved yet
            builder.setPiece(new Rook(castle_rook_destination, this.board.getCurrentPlayer().getAlliance()));
            builder.setNextPlayer(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    /**
     * Purpose of the 2 CastleMove subclasses is to override the toString() method for the 2 types of castling moves.
     * Logic for both types of moves remains in the CastleMove class
     */
    public static final class KingSideCastleMove extends CastleMove
    {
        public KingSideCastleMove(final Board board, final Piece piece, final int destination_coordinate,
                                  final Rook castle_rook, final int castle_rook_start, final int castle_rook_destination)
        {
            super(board, piece, destination_coordinate, castle_rook, castle_rook_start, castle_rook_destination);
        }

        @Override
        public String toString() {
            return "0-0";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove
    {
        public QueenSideCastleMove(final Board board, final Piece piece, final int destination_coordinate,
                                   final Rook castle_rook, final int castle_rook_start, final int castle_rook_destination)
        {
            super(board, piece, destination_coordinate, castle_rook, castle_rook_start, castle_rook_destination);
        }

        @Override
        public String toString() {
            return "0-0-0";
        }
    }

    public static final class NullMove extends Move
    {
        public NullMove()
        {
            super(null,-1);
        }

        @Override
        public Board execute()
        {
            throw new RuntimeException("Cannot execute a NullMove object");
        }
    }

    public static class MoveFactory
    {
        private MoveFactory()
        {
            throw new RuntimeException("Cannot instantiate and object of this class.");
        }
        public static Move createMove(final Board board, final int current_coordinate, final int destination_coordinate)
        {
            for(final Move move : board.getAllLegalMoves())
            {
                if(move.getCurrentCoordinate() == current_coordinate &&
                        move.getDestinationCoordinate() == destination_coordinate)
                    return move;
            }
            return NULL_MOVE;
        }
    }
}
