package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player
{
    protected final Board board;
    protected final King player_king;
    protected final Collection<Move> legal_moves;
    private boolean is_in_check;

    public Player(final Board game_board, final Collection<Move> self_legal_moves, final Collection<Move> opponent_legal_moves)
    {
        this.board = game_board;
        this.player_king = establishKing();
        this.legal_moves = ImmutableList.copyOf(Iterables.concat(self_legal_moves,
                                                calculateKingCastleMoves(self_legal_moves, opponent_legal_moves)));
        this.is_in_check = !Player.calculateAttacksOnTile(this.player_king.getPiecePosition(), opponent_legal_moves).isEmpty();
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> opponent_legal_moves)
    {
        final List<Move> moves_that_check = new ArrayList<>();
        for(final Move move : opponent_legal_moves)
        {
            if(piecePosition == move.getDestinationCoordinate())
                moves_that_check.add(move);
        }
        return ImmutableList.copyOf(moves_that_check);
    }

    public King getPlayerKing() {
        return player_king;
    }

    public Collection<Move> getLegalMoves() {
        return legal_moves;
    }

    private King establishKing()
    {
        for(final Piece piece : getActivePieces())
        {
            if(piece.getPieceType().isKing())
                return (King)piece;
        }
        throw new RuntimeException("No King Found! Not a valid board configuration");
    }

    public boolean isMoveLegal(Move move) {
        return this.legal_moves.contains(move);
    }

    public boolean isInCheck() {
        return this.is_in_check;
    }

    //TODO CHECKS CHECKMATE STALEMATE AND CASTLE
    public boolean isInCheckmate() {
        return this.is_in_check && !hasEscapeMoves();
    }

    public boolean isInStalemate() {
        return !this.is_in_check && !hasEscapeMoves();
    }

    private boolean hasEscapeMoves() {
        for(final Move move : this.legal_moves)
        {
            final MoveTransition transition = makeMove(move);
            if(transition.getMoveStatus().isDone())
                return true;
        }
        return false;
    }

    public boolean isCastled() {
        return false;
    }

    public MoveTransition makeMove(final Move move)
    {
        //ILLEGAL MOVE
        if(!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        final Board transition_board = move.execute();
        //ILLEGAL MOVE AS IT RESULTS IN A CHECK
        final Collection<Move> king_attacks = Player.calculateAttacksOnTile(
                                            transition_board.getCurrentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                                            transition_board.getCurrentPlayer().getLegalMoves());
        if(!king_attacks.isEmpty())
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);

        //LEGAL MOVE
        return new MoveTransition(transition_board, move, MoveStatus.DONE);
    }

    protected abstract Collection<Move> calculateKingCastleMoves(Collection<Move> legal_moves, Collection<Move> opponent_legal_moves);
    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
}
