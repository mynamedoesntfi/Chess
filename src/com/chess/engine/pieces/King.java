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

public class King extends Piece
{
    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(int piece_Position, Alliance piece_Alliance)
    {
        super(piece_Position, piece_Alliance, PieceType.KING, true);
    }

    public King(int piece_Position, Alliance piece_Alliance, boolean is_first_move)
    {
        super(piece_Position, piece_Alliance, PieceType.KING, is_first_move);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board)
    {
        final List<Move> legal_moves = new ArrayList<>();

        for(final int candidate_offset : CANDIDATE_MOVE_COORDINATES)
        {
            final int candidate_destination_coordinate = this.piece_position + candidate_offset;

            if(BoardUtils.isValidTileCoordinate(candidate_destination_coordinate))
            {
                if(isFirstFileExclusion(this.piece_position, candidate_destination_coordinate) ||
                    isEighthFileExclusion(this.piece_position, candidate_destination_coordinate))
                    continue;

                final Tile destination_tile = board.getTile(candidate_destination_coordinate);

                if(!destination_tile.isOccupied())
                    legal_moves.add(new NormalMove(board, this, candidate_destination_coordinate));
                else
                {
                    final Piece piece_at_destination = destination_tile.getPiece();
                    final Alliance alliance_of_piece_at_destination = piece_at_destination.getPieceAlliance();

                    if(this.piece_alliance != alliance_of_piece_at_destination)
                        legal_moves.add(new CaptureMove(board, this, candidate_destination_coordinate, piece_at_destination));
                }
            }
        }
        return ImmutableList.copyOf(legal_moves);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getPiece().getPieceAlliance());
    }

    private static boolean isFirstFileExclusion(final int current_position, final int candidate_offset)
    {
        return BoardUtils.FIRST_FILE[current_position] && (candidate_offset == -9 || candidate_offset == -1 || candidate_offset == 7);
    }

    private static boolean isEighthFileExclusion(final int current_position, final int candidate_offset)
    {
        return BoardUtils.EIGHTH_FILE[current_position] && (candidate_offset == -7 || candidate_offset == 1 || candidate_offset == 9);
    }

    @Override
    public String toString()
    {
        return PieceType.KING.toString();
    }
}
