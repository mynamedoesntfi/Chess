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

public class WhitePlayer extends Player
{
    public WhitePlayer(Board board, Collection<Move> white_standard_legal_moves, Collection<Move> black_standard_legal_moves)
    {
        super(board, white_standard_legal_moves, black_standard_legal_moves);
    }

    @Override
    protected Collection<Move> calculateKingCastleMoves(final Collection<Move> legal_moves, final Collection<Move> opponent_legal_moves)
    {
        final  List<Move> king_castle_moves = new ArrayList<>();
        if(this.player_king.isFirstMove() && !this.isInCheck())
        {
            //WHITE KING'S KING-SIDE-CASTLE MOVE
            if(!this.board.getTile(61).isOccupied() &&
                    !this.board.getTile(62).isOccupied())   //Check for squares being occupied
            {
                if(Player.calculateAttacksOnTile(61, opponent_legal_moves).isEmpty() &&
                    Player.calculateAttacksOnTile(62, opponent_legal_moves).isEmpty())
                    //Check for checks on the transition squares
                {
                    final Tile rook_tile = this.board.getTile(63);
                    if(rook_tile.isOccupied() && rook_tile.getPiece().isFirstMove() && rook_tile.getPiece().getPieceType().isRook())
                        king_castle_moves.add(new Move.KingSideCastleMove(this.board,
                                                                            this.player_king,
                                                                            62,
                                                                            (Rook) rook_tile.getPiece(),
                                                                            rook_tile.getCoordinate(),
                                                                            61));
                }
            }
            //WHITE KINGS'S QUEEN-SIDE-CASTLE MOVE
            if(!this.board.getTile(57).isOccupied() &&
                    !this.board.getTile(58).isOccupied() &&
                    !this.board.getTile(59).isOccupied())   //Check for squares being occupied
            {
                if(Player.calculateAttacksOnTile(57, opponent_legal_moves).isEmpty() &&
                        Player.calculateAttacksOnTile(58, opponent_legal_moves).isEmpty() &&
                        Player.calculateAttacksOnTile(59, opponent_legal_moves).isEmpty())
                    //Check for checks on the transition squares
                {
                    final Tile rook_tile = this.board.getTile(56);
                    if(rook_tile.isOccupied() && rook_tile.getPiece().isFirstMove() && rook_tile.getPiece().getPieceType().isRook())
                        king_castle_moves.add(new Move.QueenSideCastleMove(this.board,
                                                                            this.player_king,
                                                                            58,
                                                                            (Rook) rook_tile.getPiece(),
                                                                            rook_tile.getCoordinate(),
                                                                            59));
                }
            }
        }
        return ImmutableList.copyOf(king_castle_moves);
    }

    @Override
    public Collection<Piece> getActivePieces()
    {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.getBlackPlayer();
    }
}
