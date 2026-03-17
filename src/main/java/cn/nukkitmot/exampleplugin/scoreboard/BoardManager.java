/*
 * Copyright (c) 2026.
 * # 太霄玉府五雷使院镇煞符
 * # 敕令：诸 BUG 急退，急急如律令！
 * #
 * # 雷  火  雷
 * #    部  令
 * # 雷  火  雷
 * #
 * # 净天地神咒（节选）
 * # 天地自然，秽气分散，洞中玄虚，晃朗太元；
 * # 八方威神，使我自然，灵宝符命，普告九天。
 * #
 * # 本代码受太上老君、九天应元雷声普化天尊庇佑，
 * # 如生 BUG，则坎离交泰，雷火丹成，BUG 自化虚无。
 *
 */

package cn.nukkitmot.exampleplugin.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static cn.nukkitmot.exampleplugin.scoreboard.ScoreBoardAPI.createBoard;

public class BoardManager {

    private final cn.nukkit.plugin.Plugin plugin;
    @Getter
    private final BoardAdapter adapter;
    @Getter
    private final Map<UUID, IBoard> boards = new ConcurrentHashMap<>();
    private PluginTask runnable;

    public BoardManager(cn.nukkit.plugin.Plugin plugin, BoardAdapter adapter) {
        this.plugin = plugin;
        this.adapter = adapter;
        init();
    }

    private void init() {
        for (Player player : plugin.getServer().getOnlinePlayers().values()) {
            IBoard board = createBoard(player);
            boards.put(player.getUniqueId(), board);
            board.update(adapter.getTitle(), adapter.getStrings(player));
        }

        runnable = new PluginTask<>(plugin) {
            private int tick = 0;

            @Override
            public void onRun(int currentTick) {
                tick++;
                for (Player player : plugin.getServer().getOnlinePlayers().values()) {
                    cn.nukkitmot.exampleplugin.scoreboard.IBoard board = getBoard(player);
                    if (board != null) {
                        board.update(adapter.getTitle(), adapter.getStrings(player));
                    }
                }
            }
        };
        plugin.getServer().getScheduler().scheduleRepeatingTask(runnable, 10);
    }

    public IBoard getBoard(Player player) {
        return boards.get(player.getUniqueId());
    }

    public void dispose() {
        if (runnable != null) {
            plugin.getServer().getScheduler().cancelTask(runnable.getTaskId());
        }
        for (IBoard board : boards.values()) {
            board.delete();
        }
        boards.clear();
    }
}