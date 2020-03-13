package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class Board
{
    private final List<Tile> game_board;
    private final Collection<Piece> white_pieces;
    private final Collection<Piece> black_pieces;

    //A Board Has 2 Players
    private final WhitePlayer white_player;
    private final BlackPlayer black_player;
    private final Player current_player;

    @Override
    public String toString()
    {
        final StringBuilder s_builder = new StringBuilder();
        for(int i=0; i<BoardUtils.NUM_TILES; i++)
        {
            final String tile_text = this.game_board.get(i).toString();
            s_builder.append(String.format("%3s", tile_text));
            if((i+1) % BoardUtils.NUM_FILES == 0)    s_builder.append("\n");
        }
        return s_builder.toString();
    }

    public Tile getTile(int tile_coordinate)
    {
        return game_board.get(tile_coordinate);
    }

    public Collection<Piece> getBlackPieces() {
        return black_pieces;
    }

    public Collection<Piece> getWhitePieces() {
        return white_pieces;
    }

    public Player getWhitePlayer() {    return white_player;    }

    public Player getBlackPlayer() {    return black_player;    }

    public Player getCurrentPlayer() {  return current_player;  }

    private Board(final Builder builder)  //CONSTRUCTOR
    {
        this.game_board = createGameBoard(builder);
        this.white_pieces = calculateActivePieces(game_board, Alliance.WHITE);
        this.black_pieces = calculateActivePieces(game_board, Alliance.BLACK);

        final Collection<Move> white_standard_legal_moves = calculateLegalMoves(this.white_pieces);
        final Collection<Move> black_standard_legal_moves = calculateLegalMoves(this.black_pieces);

        this.white_player = new WhitePlayer(this, white_standard_legal_moves, black_standard_legal_moves);
        this.black_player = new BlackPlayer(this, white_standard_legal_moves, black_standard_legal_moves);
        this.current_player = builder.next_player.choosePlayer(this.white_player,this.black_player);
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces)
    {
        final List<Move> legal_moves = new ArrayList<>();
        for(final Piece piece : pieces)
            legal_moves.addAll(piece.calculateLegalMoves(this));

        return ImmutableList.copyOf(legal_moves);
    }

    private static Collection<Piece> calculateActivePieces(final List<Tile> game_board, final Alliance alliance)
    {
        final List<Piece> active_pieces = new ArrayList<>();
        for(final Tile tile : game_board)
        {
            if(tile.isOccupied())
            {
                final Piece piece = tile.getPiece();
                if(piece.getPieceAlliance() == alliance)
                    active_pieces.add(piece);
            }
        }
        return ImmutableList.copyOf(active_pieces);
    }

    private static List<Tile> createGameBoard(Builder builder)  //Returns A Board Given Builder Object(using board_config)
    {
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for(int i=0;i<BoardUtils.NUM_TILES;i++)
            tiles[i] = Tile.createTile(i, builder.board_config.get(i));

        return ImmutableList.copyOf(tiles);
    }

    public static Board createStandardBoard()   //Returns a Builder Object(with standard board_config)
    {
        final Builder builder = new Builder();

        //Black Layout
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new King(4, Alliance.BLACK));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        builder.setPiece(new Pawn(8, Alliance.BLACK));
        builder.setPiece(new Pawn(9, Alliance.BLACK));
        builder.setPiece(new Pawn(10, Alliance.BLACK));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        builder.setPiece(new Pawn(13, Alliance.BLACK));
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(15, Alliance.BLACK));

        //White Layout
        builder.setPiece(new Pawn(48, Alliance.WHITE));
        builder.setPiece(new Pawn(49, Alliance.WHITE));
        builder.setPiece(new Pawn(50, Alliance.WHITE));
        builder.setPiece(new Pawn(51, Alliance.WHITE));
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new Pawn(53, Alliance.WHITE));
        builder.setPiece(new Pawn(54, Alliance.WHITE));
        builder.setPiece(new Pawn(55, Alliance.WHITE));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Queen(59, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Rook(63, Alliance.WHITE));

        builder.setNextPlayer(Alliance.WHITE);

        return new Board(builder);
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.white_player.getLegalMoves(), this.black_player.getLegalMoves()));
    }


    public static class Builder
    {
        Map<Integer, Piece> board_config;
        Alliance next_player;
        Pawn en_passant_pawn;

        public Builder()
        {
            this.board_config = new HashMap<>();
        }

        public Builder setPiece(final Piece piece)
        {
            this.board_config.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setNextPlayer(final Alliance next_player)
        {
            this.next_player = next_player;
            return this;
        }

        public Board build()
        {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn en_passant_pawn) {
            this.en_passant_pawn = en_passant_pawn;
        }

        public Pawn getEnPassantPawn() {
            return en_passant_pawn;
        }
    }
}
