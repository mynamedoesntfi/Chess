package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece
{
    public static final int[] CANDIDATE_MOVE_COORDINATES = {7, 8, 9, 16};

    public Pawn(int piece_Position, Alliance piece_Alliance)
    {
        super(piece_Position, piece_Alliance, PieceType.PAWN, true);
    }

    public Pawn(int piece_Position, Alliance piece_Alliance, boolean is_first_move)
    {
        super(piece_Position, piece_Alliance, PieceType.PAWN, is_first_move);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board)
    {
        final List<Move> legal_moves = new ArrayList<>();
        for(int candidate_offset : CANDIDATE_MOVE_COORDINATES)
        {
            int candidate_destination_coordinate = this.piece_position + this.piece_alliance.getDirection()*candidate_offset;

            if(!BoardUtils.isValidTileCoordinate(candidate_destination_coordinate))  continue;;

            //1 Square pawn move
            if(candidate_offset == 8 && !board.getTile(candidate_destination_coordinate).isOccupied())
                //TODO move work to do here (pawn promotion) !!
                legal_moves.add(new NormalMove(board, this, candidate_destination_coordinate));
            //2 Square pawn move
            else if(candidate_offset == 16 && this.isFirstMove() &&
                    ((BoardUtils.SECOND_RANK[this.piece_position] && this.getPieceAlliance().isWhite()) ||
                        BoardUtils.SEVENTH_RANK[this.piece_position] && this.getPieceAlliance().isBlack()))
            {
                //pawn jump;
                final int middle_square = this.piece_position + this.getPieceAlliance().getDirection()*8;
                if(!board.getTile(middle_square).isOccupied() && !board.getTile(candidate_destination_coordinate).isOccupied())
                    legal_moves.add(new PawnJumpMove(board, this, candidate_destination_coordinate));
            }
            else if(candidate_offset == 7   &&
                    !((BoardUtils.FIRST_FILE[this.piece_position] && this.piece_alliance.isBlack())   ||
                     (BoardUtils.EIGHTH_FILE[this.piece_position] && this.piece_alliance.isWhite()) )   )
            {
                //CaptureMove
                if(board.getTile(candidate_destination_coordinate).isOccupied())
                {
                    final Piece piece_on_destination = board.getTile(candidate_destination_coordinate).getPiece();
                    if(piece_on_destination.getPieceAlliance() != this.piece_alliance)
                        //TODO replace logic to attack into pawn promotion
                        legal_moves.add(new CaptureMove(board, this, candidate_destination_coordinate, piece_on_destination));
                }
            }
            else if(candidate_offset == 9   &&
                    !((BoardUtils.FIRST_FILE[this.piece_position] && this.piece_alliance.isWhite())   ||
                            (BoardUtils.EIGHTH_FILE[this.piece_position] && this.piece_alliance.isBlack()) )   )
            {
                //CaptureMove
                if(board.getTile(candidate_destination_coordinate).isOccupied())
                {
                    final Piece piece_on_destination = board.getTile(candidate_destination_coordinate).getPiece();
                    if(piece_on_destination.getPieceAlliance() != this.piece_alliance)
                        //TODO replace logic to attack into pawn promotion
                        legal_moves.add(new CaptureMove(board, this, candidate_destination_coordinate, piece_on_destination));
                }
            }
        }
        return ImmutableList.copyOf(legal_moves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getPiece().getPieceAlliance());
    }

    @Override
    public String toString()
    {
        return Piece.PieceType.PAWN.toString();
    }
}
