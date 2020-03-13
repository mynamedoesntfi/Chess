package com.chess.engine.board;

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

import static com.chess.engine.board.BoardUtils.NUM_TILES;

public abstract class Tile
{
    protected final int tile_Coordinate;
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles()
    {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for(int i=0;i<NUM_TILES;i++)
            emptyTileMap.put(i, new EmptyTile(i));

        return ImmutableMap.copyOf(emptyTileMap);
    }

    private Tile(final int coordinate)
    {
        tile_Coordinate = coordinate;
    }

    public static Tile createTile(final int tile_Coordiante, final Piece piece)
    {
        return piece != null ? new OccupiedTile(tile_Coordiante, piece) : EMPTY_TILES_CACHE.get(tile_Coordiante);
    }

    public int getCoordinate()  {
        return tile_Coordinate;
    }

    public abstract boolean isOccupied();
    public abstract Piece getPiece();

    /** 2 Types of tiles - Occupied and Empty **/
    public static class EmptyTile extends Tile
    {
        private EmptyTile(int coordinate)
        {
            super(coordinate);
        }
        @Override
        public boolean isOccupied()
        {   return false;   }
        @Override
        public Piece getPiece()
        {   return null;    }
        @Override
        public String toString()
        {
            return "-";
        }
    }

    public static class OccupiedTile extends Tile
    {
        private final Piece piece;
        private OccupiedTile(final int coordinate, final Piece piece)
        {
            super(coordinate);
            this.piece = piece;
        }
        @Override
        public boolean isOccupied()
        {   return true;   }
        @Override
        public Piece getPiece()
        {   return piece;    }
        @Override
        public String toString()
        {
            return getPiece().getPieceAlliance().isBlack() ?    getPiece().toString().toLowerCase() : getPiece().toString().toUpperCase();
        }
    }
}
