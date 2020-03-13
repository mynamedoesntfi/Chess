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

public class Rook extends Piece
{
    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATE = {-8, -1, 1, 8};

    public Rook(int piece_Position, Alliance piece_Alliance)
    {
        super(piece_Position, piece_Alliance, PieceType.ROOK, true);
    }

    public Rook(int piece_Position, Alliance piece_Alliance, boolean is_first_move)
    {
        super(piece_Position, piece_Alliance, PieceType.ROOK, is_first_move);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board)
    {
        final List<Move> legal_moves = new ArrayList<>();

        for(int candidate_offset : CANDIDATE_MOVE_VECTOR_COORDINATE)
        {
            int destination_coordinate = this.piece_position;
            while(BoardUtils.isValidTileCoordinate(destination_coordinate))
            {
                if(isFirstFileExclusion(this.piece_position, candidate_offset) ||
                    isEighthFileExclusion(this.piece_position, candidate_offset))
                    break;

                destination_coordinate += candidate_offset;
                if(BoardUtils.isValidTileCoordinate(destination_coordinate))
                {
                    final Tile candidate_destination_tile = board.getTile(destination_coordinate);
                    if(!candidate_destination_tile.isOccupied())
                        legal_moves.add(new NormalMove(board, this, destination_coordinate));
                    else
                    {
                        final Piece piece_on_destination = candidate_destination_tile.getPiece();
                        final Alliance alliance_of_piece_on_destination = piece_on_destination.getPieceAlliance();

                        if(alliance_of_piece_on_destination != this.piece_alliance)
                            legal_moves.add(new CaptureMove(board, this, destination_coordinate, piece_on_destination));
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legal_moves);
    }

    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getDestinationCoordinate(), move.getPiece().getPieceAlliance());
    }

    private static boolean isFirstFileExclusion(final int current_position, final int candidate_offset)
    {
        return BoardUtils.FIRST_FILE[current_position] && (candidate_offset == -1);
    }

    private static boolean isEighthFileExclusion(final int current_position, final int candidate_offset)
    {
        return BoardUtils.EIGHTH_FILE[current_position] && (candidate_offset == 1);
    }

    @Override
    public String toString()
    {
        return PieceType.ROOK.toString();
    }
}
