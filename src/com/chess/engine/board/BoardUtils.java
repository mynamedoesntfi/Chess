package com.chess.engine.board;

import java.util.*;

public class BoardUtils
{
    public static final int NUM_TILES = 64;
    public static final int NUM_RANKS = 8;
    public static final int NUM_FILES = 8;

    public static final boolean[] FIRST_FILE = initFile(0);
    public static final boolean[] SECOND_FILE = initFile(1);
    public static final boolean[] SEVENTH_FILE = initFile(6);
    public static final boolean[] EIGHTH_FILE = initFile(7);

    public static final boolean[] FIRST_RANK = initRank(1);
    public static final boolean[] SECOND_RANK = initRank(2);
    public static final boolean[] THIRD_RANK = initRank(3);
    public static final boolean[] FOURTH_RANK = initRank(4);
    public static final boolean[] FIFTH_RANK = initRank(5);
    public static final boolean[] SIXTH_RANK = initRank(6);
    public static final boolean[] SEVENTH_RANK = initRank(7);
    public static final boolean[] EIGHTH_RANK = initRank(8);

    public static final int START_TILE_INDEX = 0;

    public static final List<String> ALGEBRAIC_NOTATION = init_ALGEBRAIC_NOTATION();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = init_POSITION_TO_COORDINATE();

    private static boolean[] initRank(int rank_num)
    {
        final boolean[] rank = new boolean[NUM_TILES];
        int index = 0;
        final int initial_tile_coordinate = (BoardUtils.NUM_RANKS - rank_num) * BoardUtils.NUM_FILES;
        do
        {
            rank[initial_tile_coordinate + index++] = true;
        }while(index<BoardUtils.NUM_FILES);

        return rank;
    }

    private static boolean[] initFile(int tile_number)
    {
        final boolean[] file = new boolean[NUM_TILES];
        do
        {
            file[tile_number] = true;
            tile_number += NUM_FILES; //adding 8 to get to next rank
        }while(tile_number<NUM_TILES);

        return file;
    }

    private static Map<String, Integer> init_POSITION_TO_COORDINATE() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = START_TILE_INDEX; i < NUM_TILES; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return Collections.unmodifiableMap(positionToCoordinate);
    }

    private static List<String> init_ALGEBRAIC_NOTATION() {
        return Collections.unmodifiableList(Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
    }

    private BoardUtils()
    {
        throw new RuntimeException("BoardUtils class cannot be instantiated.");
    }

    public static boolean isValidTileCoordinate(int coordinate)
    {
        return (coordinate>=0 && coordinate<64);
    }

    public static String getPositionAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }

    public static int getCoordinateAtPosition(final String position) {
        return POSITION_TO_COORDINATE.get(position);
    }
}
