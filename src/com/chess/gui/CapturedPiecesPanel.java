package com.chess.gui;

import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CapturedPiecesPanel extends JPanel
{
    private final JPanel black_panel;
    private final JPanel white_panel;

    private static final Dimension TAKEN_PIECE_DIMENSION = new Dimension(40, 80);
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    public CapturedPiecesPanel()
    {
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.black_panel = new JPanel(new GridLayout(8, 2));
        this.white_panel = new JPanel(new GridLayout(8, 2));
        this.black_panel.setBackground(PANEL_COLOR);
        this.white_panel.setBackground(PANEL_COLOR);
        add(this.black_panel, BorderLayout.NORTH);
        add(this.white_panel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECE_DIMENSION);
    }

    public void redo(final Table.MoveLog move_log)
    {
        this.black_panel.removeAll();
        this.white_panel.removeAll();

        final List<Piece> white_taken_pieces = new ArrayList<>();
        final List<Piece> black_taken_pieces = new ArrayList<>();

        for(final Move move : move_log.getMoves())
        {
            if(move.isAttack())
            {
                // Adding the captured piece to the panel
                final Piece taken_piece = move.getAttackedPiece();
                if(taken_piece.getPieceAlliance().isWhite())
                    white_taken_pieces.add(taken_piece);
                else
                    black_taken_pieces.add(taken_piece);
            }
        }

        Collections.sort(white_taken_pieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece piece, Piece t1) {
                return Ints.compare(piece.getPieceValue(), t1.getPieceValue());
            }
        });
        Collections.sort(black_taken_pieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece piece, Piece t1) {
                return Ints.compare(piece.getPieceValue(), t1.getPieceValue());
            }
        });

        for(final Piece piece : white_taken_pieces)
        {
            try
            {
                final BufferedImage image = ImageIO.read(new File("/home/nandan/Codes/Chess/Icons/simple/"
                        + piece.getPieceAlliance().toString().substring(0,1) + piece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel image_label = new JLabel();
                this.white_panel.add(image_label);
            }
            catch (IOException e)   {}
        }
        for(final Piece piece : black_taken_pieces)
        {
            try
            {
                final BufferedImage image = ImageIO.read(new File("/home/nandan/Codes/Chess/Icons/simple/"
                        + piece.getPieceAlliance().toString().substring(0,1) + piece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel image_label = new JLabel();
                this.black_panel.add(image_label);
            }
            catch (IOException e)   {}
        }

        validate();
    }
}
