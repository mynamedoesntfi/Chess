package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.SplitPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static javax.swing.SwingUtilities.*;

public class Table
{
    private final JFrame game_frame;
    private final BoardPanel board_panel;
    private final GameHistoryPanel game_history_panel;
    private final CapturedPiecesPanel captured_pieces_panel;
    private Board chessboard;
    private Tile source_tile;
    private Tile destination_tile;
    private Piece selected_piece;
    private BoardDirection board_direction;
    private final MoveLog move_log;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    private boolean highlight_legal_moves;
    private final Color light_tile_color = Color.decode("#FFFACD");
    private final Color dark_tile_color = Color.decode("#593E1A");

    private static String DEFAULT_PIECE_IMAGES_PATH = "/home/nandan/Codes/Chess/Icons/simple/";

    public Table()
    {
        this.game_history_panel = new GameHistoryPanel();
        this.captured_pieces_panel = new CapturedPiecesPanel();
        this.chessboard = Board.createStandardBoard();
        this.game_frame = new JFrame("JChess");
        this.game_frame.setLayout(new BorderLayout());
        this.board_direction = BoardDirection.NORMAL;
        this.highlight_legal_moves = true;
        //Menu Bar
        final JMenuBar table_menu_bar = createTableMenuBar();
        this.game_frame.setJMenuBar(table_menu_bar);
        //Captured Pieces Panel
        this.game_frame.add(this.captured_pieces_panel, BorderLayout.WEST);
        //Board
        this.board_panel = new BoardPanel();
        this.move_log = new MoveLog();
        this.game_frame.add(this.board_panel, BorderLayout.CENTER);
        this.game_frame.setSize(OUTER_FRAME_DIMENSION);
        this.game_frame.setVisible(true);
        //Game History Panel
        this.game_frame.add(this.game_history_panel,BorderLayout.EAST);
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar table_menu_bar = new JMenuBar();
        table_menu_bar.add(createFileMenu());
        table_menu_bar.add(createPreferencesMenu());

        return table_menu_bar;
    }

    private JMenu createFileMenu() {
        final JMenu file_menu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Open PGN File!");
            }
        });
        file_menu.add(openPGN);
        return file_menu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferences_menu = new JMenu("Preferences");
        final JMenuItem flip_board_menu_item = new JMenuItem("Flip Board");
        flip_board_menu_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                board_direction = board_direction.opposite();
                board_panel.drawBoard(chessboard);
            }
        });
        preferences_menu.add(flip_board_menu_item);

        preferences_menu.addSeparator();

        final JCheckBoxMenuItem highlight_legal_moves_checkbox_menu_item = new JCheckBoxMenuItem("Highlight Legal Moves", true);

        highlight_legal_moves_checkbox_menu_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                highlight_legal_moves = highlight_legal_moves_checkbox_menu_item.isSelected();
            }
        });

        preferences_menu.add(highlight_legal_moves_checkbox_menu_item);
        return preferences_menu;
    }

    public enum BoardDirection
    {
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> board_tiles) {
                return board_tiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> board_tiles) {
                return Lists.reverse(board_tiles);  //Guava fn
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> board_tiles);
        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel
    {
        final List<TilePanel> board_tiles;
        BoardPanel()
        {
            super(new GridLayout(8, 8));
            this.board_tiles = new ArrayList<>();
            for(int i=0;i< BoardUtils.NUM_TILES;i++)
            {
                final TilePanel tile_panel = new TilePanel(this, i);
                this.board_tiles.add(tile_panel);
                add(tile_panel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board_to_draw) {
            removeAll();
            for(final TilePanel tile_panel : board_direction.traverse(this.board_tiles))
            {
                tile_panel.drawTile(board_to_draw);
                add(tile_panel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog
    {
        private final List<Move> moves;

        MoveLog()
        {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(Move move) {
            this.moves.add(move);
        }

        public int size()   {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(final int index)  {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }

    private class TilePanel extends JPanel
    {
        private final int tile_id;

        TilePanel(final BoardPanel board_panel, final int tileID)
        {
            super(new GridBagLayout());
            this.tile_id = tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignPieceIcon(chessboard);

            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(final MouseEvent mouseEvent) {
                    if(isRightMouseButton(mouseEvent))
                    {
                        source_tile = null;
                        destination_tile = null;
                        selected_piece = null;
                    }
                    else if(isLeftMouseButton(mouseEvent))
                    {
                        if(source_tile == null)
                        {
                            //First Click
                            source_tile = chessboard.getTile(tile_id);
                            selected_piece = source_tile.getPiece();
                            if(selected_piece == null)
                                source_tile = null;
                        }
                        else
                        {
                            //Second Click
                            destination_tile = chessboard.getTile(tile_id);
                            final Move move = Move.MoveFactory.createMove(chessboard,
                                                                            source_tile.getCoordinate(),
                                                                            destination_tile.getCoordinate());
                            final MoveTransition move_transition = chessboard.getCurrentPlayer().makeMove(move);
                            if(move_transition.getMoveStatus().isDone())
                            {
                                chessboard = move_transition.getTransitionBoard();
                                move_log.addMove(move);
                            }

                            source_tile = null;
                            destination_tile = null;
                            selected_piece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                game_history_panel.redo(chessboard, move_log);
                                captured_pieces_panel.redo(move_log);
                                board_panel.drawBoard(chessboard);
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {

                }
            });
            validate();
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignPieceIcon(board);
            highlightLegalMoves(board);
            validate();
            repaint();
        }

        private void assignPieceIcon(final Board board)
        {
            this.removeAll();
            if(board.getTile(this.tile_id).isOccupied())
            {
                try {
                    final File file = new File(DEFAULT_PIECE_IMAGES_PATH +
                            board.getTile(this.tile_id).getPiece().getPieceAlliance().toString().substring(0,1) +
                            board.getTile(this.tile_id).getPiece().toString() +
                            ".gif");
                    final BufferedImage image =
                            ImageIO.read(file);
                    this.add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void highlightLegalMoves(final Board board)
        {
            if(highlight_legal_moves)
            {
                for(final Move move : pieceLegalMoves(board))
                {
                    if(move.getDestinationCoordinate() == this.tile_id)
                    {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("/home/nandan/Codes/Chess/Icons/misc/green_dot.png")))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board)
        {
            if(selected_piece != null && selected_piece.getPieceAlliance() == board.getCurrentPlayer().getAlliance())
                return selected_piece.calculateLegalMoves(board);
            return Collections.emptyList();
        }

        private void assignTileColor() {
            if(BoardUtils.FIRST_RANK[this.tile_id] ||
                BoardUtils.THIRD_RANK[this.tile_id] ||
                BoardUtils.FIFTH_RANK[this.tile_id] ||
                BoardUtils.SEVENTH_RANK[this.tile_id])
                setBackground(this.tile_id % 2 != 0 ? light_tile_color : dark_tile_color);
            else if(BoardUtils.SECOND_RANK[this.tile_id] ||
                    BoardUtils.FOURTH_RANK[this.tile_id] ||
                    BoardUtils.SIXTH_RANK[this.tile_id] ||
                    BoardUtils.EIGHTH_RANK[this.tile_id])
                setBackground(this.tile_id % 2 == 0 ? light_tile_color : dark_tile_color);
        }
    }
}
