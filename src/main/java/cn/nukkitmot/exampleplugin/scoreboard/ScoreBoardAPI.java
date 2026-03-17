package cn.nukkitmot.exampleplugin.text;

import cn.nukkit.Player;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreBoardAPI {

    private static final Map<UUID, IBoard> BOARDS = new ConcurrentHashMap<>();
    @Getter
    private static BoardManager manager;

    public static void init() {
    }

    public static void setManager(BoardManager manager) {
        ScoreBoardAPI.manager = manager;
    }

    public static IBoard createBoard(Player player) {
        IBoard board = new NukkitBoardImpl(player);
        BOARDS.put(player.getUniqueId(), board);
        return board;
    }

    public static IBoard getBoard(Player player) {
        return BOARDS.get(player.getUniqueId());
    }

    public static void removeBoard(Player player) {
        IBoard board = BOARDS.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    public static boolean hasBoard(Player player) {
        return BOARDS.containsKey(player.getUniqueId());
    }

    public static void removeAllBoards() {
        for (IBoard board : BOARDS.values()) {
            board.delete();
        }
        BOARDS.clear();
    }


}
