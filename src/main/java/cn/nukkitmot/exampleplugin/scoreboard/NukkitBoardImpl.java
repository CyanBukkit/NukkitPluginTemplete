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
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.manager.IScoreboardManager;
import cn.nukkitmot.exampleplugin.ExamplePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NukkitBoardImpl implements IBoard {

    private final Player player;
    private final String objectiveName;
    private String title;
    private Scoreboard scoreboard;
    private IScoreboardManager scoreboardManager;
    private Scoreboard.DisplaySlot displaySlot = Scoreboard.DisplaySlot.SIDEBAR;

    public NukkitBoardImpl(Player player) {
        this(player, "");
    }

    public NukkitBoardImpl(Player player, String title) {
        this.player = player;
        this.objectiveName = "sb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        this.title = title;
        this.scoreboardManager = ExamplePlugin.instance.getServer().getScoreboardManager();
        createScoreboard();
    }

    private void createScoreboard() {
        if (scoreboard != null) {
            scoreboard.hideFor(player);
        }
        scoreboard = new Scoreboard(title, Scoreboard.SortOrder.ASCENDING, displaySlot);
        if (scoreboard != null) {
            scoreboard.showTo(player);
        }
    }

    @Override
    public void update(String title, List<String> lines) {
        boolean titleChanged = title == null ? this.title != null : !title.equals(this.title);
        
        if (scoreboard == null || titleChanged) {
            this.title = title != null ? title : "";
            createScoreboard();
            if (scoreboard == null) {
                return;
            }
        }

        scoreboard.holdUpdates();

        boolean needsUpdate = false;
        List<String> translatedLines = new ArrayList<>();

        for (String line : lines) {
            translatedLines.add(line);
            if (!scoreboard.getScores().containsKey(line)) {
                needsUpdate = true;
            }
        }

        if (needsUpdate) {
            scoreboard.clear();
            int line = 0;
            for (String text : translatedLines) {
                scoreboard.setScore(text, line++);
            }
        }

        scoreboard.unholdUpdates();
    }

    @Override
    public void delete() {
        if (scoreboard != null) {
            scoreboard.hideFor(player);
            scoreboard = null;
        }
    }

    public NukkitBoardImpl setDisplaySlot(Scoreboard.DisplaySlot slot) {
        this.displaySlot = slot;
        if (scoreboard != null) {
            scoreboard.hideFor(player);
            scoreboard = new Scoreboard(title, Scoreboard.SortOrder.ASCENDING, displaySlot);
            if (scoreboard != null) {
                scoreboard.showTo(player);
            }
        }
        return this;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public String getObjectiveName() {
        return objectiveName;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
