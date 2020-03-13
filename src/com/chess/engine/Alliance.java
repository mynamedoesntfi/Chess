package com.chess.engine;

import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

public enum Alliance
{
    WHITE
            {
                @Override
                public int getDirection()
                {
                    return -1;
                }

                @Override
                public boolean isBlack() {  return false;   }

                @Override
                public boolean isWhite() {  return true;    }

                @Override
                public Player choosePlayer(WhitePlayer white_player, BlackPlayer black_player) {
                    return white_player;
                }
            },
    BLACK
            {
                @Override
                public int getDirection()
                {
                    return 1;
                }

                @Override
                public boolean isBlack() {  return true;    }

                @Override
                public boolean isWhite() {  return false;   }

                @Override
                public Player choosePlayer(WhitePlayer white_player, BlackPlayer black_player) {
                    return black_player;
                }
            };

    public abstract int getDirection();
    public abstract boolean isBlack();
    public abstract boolean isWhite();

    public abstract Player choosePlayer(WhitePlayer white_player, BlackPlayer black_player);
}
