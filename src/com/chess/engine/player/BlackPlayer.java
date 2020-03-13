package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player
{
    public BlackPlayer(Board board, Collection<Move> white_standard_legal_moves, Collection<Move> black_standard_legal_moves)
    {
        super(board, black_standard_legal_moves, white_standard_legal_moves);
    }

    @Override
    protected Collection<Move> calculateKingCastleMoves(final Collection<Move> legal_moves, final Collection<Move> opponent_legal_moves) {

        final  List<Move> king_castle_moves = new ArrayList<>();
        if(this.player_king.isFirstMove() && !this.isInCheck())
        {
            //BLACK KING'S KING-SIDE-CASTLE MOVE
            if(!this.board.getTile(5).isOccupied() &&
                    !this.board.getTile(6).isOccupied())   //Check for squares being occupied
            {
                if(Player.calculateAttacksOnTile(5, opponent_legal_moves).isEmpty() &&
                        Player.calculateAttacksOnTile(6, opponent_legal_moves).isEmpty())
                //Check for checks on the transition squares
                {
                    final Tile rook_tile = this.board.getTile(7);
                    if(rook_tile.isOccupied() && rook_tile.getPiece().isFirstMove() && rook_tile.getPiece().getPieceType().isRook())
                        king_castle_moves.add(new Move.KingSideCastleMove(this.board,
                                                                            this.player_king,
                                                                            6,
                                                                            (Rook) rook_tile.getPiece(),
                                                                            rook_tile.getCoordinate(),
                                                                            5));
                }
            }
            //BLACK KINGS'S QUEEN-SIDE-CASTLE MOVE
            if(!this.board.getTile(1).isOccupied() &&
                    !this.board.getTile(2).isOccupied() &&
                    !this.board.getTile(3).isOccupied())   //Check for squares being occupied
            {
                if(Player.calculateAttacksOnTile(1, opponent_legal_moves).isEmpty() &&
                        Player.calculateAttacksOnTile(2, opponent_legal_moves).isEmpty() &&
                        Player.calculateAttacksOnTile(3, opponent_legal_moves).isEmpty())
                //Check for checks on the transition squares
                {
                    final Tile rook_tile = this.board.getTile(0);
                    if(rook_tile.isOccupied() && rook_tile.getPiece().isFirstMove() && rook_tile.getPiece().getPieceType().isRook())
                        king_castle_moves.add(new Move.QueenSideCastleMove(this.board,
                                                                            this.player_king,
                                                                            2,
                                                                            (Rook) rook_tile.getPiece(),
                                                                            rook_tile.getCoordinate(),
                                                                            3));
                }
            }
        }
        return ImmutableList.copyOf(king_castle_moves);
    }

    @Override
    public Collection<Piece> getActivePieces()
    {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.getWhitePlayer();
    }


}
