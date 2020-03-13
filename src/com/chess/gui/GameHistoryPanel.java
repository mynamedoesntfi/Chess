package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class GameHistoryPanel extends JPanel
{
    private final DataModel model;
    private final JScrollPane scroll_pane;
    private final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

    GameHistoryPanel()
    {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scroll_pane = new JScrollPane();
        this.scroll_pane.setColumnHeaderView(table.getTableHeader());
        this.scroll_pane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scroll_pane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final Table.MoveLog move_log)
    {
        int current_row = 0;
        this.model.clear();
        for(final Move move : move_log.getMoves())
        {
            final String move_text = move.toString();
            if(move.getPiece().getPieceAlliance().isWhite())    {
                this.model.setValueAt(move_text, current_row, 0);
            }
            else    {
                this.model.setValueAt(move_text, current_row++, 1);
            }
        }

        if(move_log.getMoves().size() > 0)
        {
            final Move last_move = move_log.getMoves().get(move_log.size()-1);
            final String move_text = last_move.toString();

            if(last_move.getPiece().getPieceAlliance().isWhite())   {
                this.model.setValueAt(move_text + calculateCheckAndCheckmateHash(board), current_row, 0);
            }
            else if(last_move.getPiece().getPieceAlliance().isBlack())  {
                this.model.setValueAt(move_text + calculateCheckAndCheckmateHash(board), current_row - 1, 1);
            }
        }

        final JScrollBar vertical = scroll_pane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private String calculateCheckAndCheckmateHash(Board board) {
        if(board.getCurrentPlayer().isInCheckmate())    return "#";
        else if(board.getCurrentPlayer().isInCheck())   return "+";
        return "";
    }

    private static class DataModel extends DefaultTableModel
    {
        private final List<Row> values;
        private static final String[] NAMES = {"white", "black"};

        DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount()    {
            if(this.values == null) return 0;
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            final Row current_row = this.values.get(row);
            if(column == 0) return current_row.getWhiteMove();
            else if(column == 1)    return current_row.getBlackMove();
            else    return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            final Row current_row;
            if(this.values.size() <= row)
            {
                //if row(ie number of rows) exceeds the row count in the table add a new row(happens when white moves)
                current_row = new Row();
                this.values.add(current_row);
            }
            else
                //retrieve the row to which black move is to be added(ie row has not exceeded row count)
                current_row = this.values.get(row);
            if(column == 0) {
                current_row.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            }
            else if (column == 1) {
                current_row.setBlackMove((String) aValue);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Move.class;
        }

        @Override
        public String getColumnName(int column) {
            return NAMES[column];
        }
    }

    private static class Row
    {
        private String white_move;
        private String black_move;

        Row()
        {
        }

        private String getWhiteMove()   {
            return this.white_move;
        }

        private void setWhiteMove(final String white_move)  {
            this.white_move = white_move;
        }

        private String getBlackMove()   {
            return this.black_move;
        }

        private void setBlackMove(final String black_move)  {
            this.black_move = black_move;
        }
    }
}
