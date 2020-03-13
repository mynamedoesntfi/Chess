package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.concurrent.Future;

public class MoveTransition
{
    private final Board transition_board;
    private final Move move;
    private final MoveStatus move_status;

    public MoveTransition(final Board transition_board, final Move move, final MoveStatus move_status)
    {
        this.transition_board = transition_board;
        this.move = move;
        this.move_status = move_status;
    }

    public MoveStatus getMoveStatus() {
        return this.move_status;
    }

    public Board getTransitionBoard() {
        return transition_board;
    }
}
