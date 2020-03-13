package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Knight extends Piece
{
    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(int piece_Position, Alliance piece_Alliance)
    {
        super(piece_Position, piece_Alliance, PieceType.KNIGHT, true);
    }

    public Knight(int piece_Position, Alliance piece_Alliance, boolean is_first_move)
    {
        super(piece_Position, piece_Alliance, PieceType.KNIGHT, is_first_move);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board)
    {
        final List<Move> legal_moves = new ArrayList<>();
        for(final int current_candidate_offset : CANDIDATE_MOVE_COORDINATES)
        {
            int candidate_destination_coordinate = this.piece_position + current_candidate_offset;

            if(BoardUtils.isValidTileCoordinate(candidate_destination_coordinate))
            {
                if(isFirstFileExclusion(this.piece_position, current_candidate_offset) ||
                    isSecondFileExclusion(this.piece_position, current_candidate_offset) ||
                    isSeventhFileExclusion(this.piece_position, current_candidate_offset) ||
                    isEighthFileExclusion(this.piece_position, current_candidate_offset))
                        continue;

                final Tile candidate_destination_tile = board.getTile(candidate_destination_coordinate);
                if(!candidate_destination_tile.isOccupied())
                    legal_moves.add(new NormalMove(board, this, candidate_destination_coordinate));
                else
                {
                    final Piece piece_at_destination = candidate_destination_tile.getPiece();
                    final Alliance alliance_of_piece_at_destination = piece_at_destination.getPieceAlliance();

                    if(this.piece_alliance != alliance_of_piece_at_destination)
                        legal_moves.add(new CaptureMove(board, this, candidate_destination_coordinate, piece_at_destination));
                }
            }
        }
        return ImmutableList.copyOf(legal_moves);
    }

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getPiece().getPieceAlliance());
    }

    private static boolean isFirstFileExclusion(final int current_position, final int current_candidate_offset)
    {
        return BoardUtils.FIRST_FILE[current_position] && (current_candidate_offset == -17 || current_candidate_offset == -10 ||
                current_candidate_offset == 6 || current_candidate_offset == 15);
    }

    private static boolean isSecondFileExclusion(final int current_position, final int current_candidate_offset)
    {
        return BoardUtils.SECOND_FILE[current_position] && (current_candidate_offset == -10 || current_candidate_offset == 6);
    }

    private static boolean isSeventhFileExclusion(final int current_position, final int current_candidate_offset)
    {
        return BoardUtils.SEVENTH_FILE[current_position] && (current_candidate_offset == -6 || current_candidate_offset == 10);
    }

    private static boolean isEighthFileExclusion(final int current_position, final int current_candidate_offset)
    {
        return BoardUtils.EIGHTH_FILE[current_position] && (current_candidate_offset == 15 || current_candidate_offset == -6 ||
                current_candidate_offset == 10 || current_candidate_offset == 17);
    }

    @Override
    public String toString()
    {
        return PieceType.KNIGHT.toString();
    }
}
