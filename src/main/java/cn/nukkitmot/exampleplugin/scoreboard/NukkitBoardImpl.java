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

package cn.nukkitmot.exampleplugin.text;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetDisplayObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class NukkitBoardImpl implements IBoard {

    private final Player player;
    @Getter
    private final String objectiveName;
    private String title;
    private final List<ScoreData> entries = new ArrayList<>();
    private int displaySlot = 0;
    private String customIconName;
    private int currentRows = 0;

    public NukkitBoardImpl(Player player) {
        this(player, "");
    }

    public NukkitBoardImpl(Player player, String title) {
        this.player = player;
        this.objectiveName = "sb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        this.title = title;
        sendDisplayObjective();
    }

    @Override
    public void update(String title, Collection<String> lines) {
        List<String> list = new ArrayList<>(lines);
        int newRows = list.size();

        if (newRows != currentRows) {
            rebuild(newRows);
        }

        this.title = title;
        sendDisplayObjective();

        entries.clear();
        for (int i = 0; i < newRows; i++) {
            ScoreData entry = new ScoreData();
            entry.score = newRows - i;
            entry.name = colorEntry(i) + list.get(i);
            entries.add(entry);
        }
        sendScore();
    }

    private void rebuild(int rows) {
        entries.clear();
        currentRows = rows;
        sendDisplayObjective();
    }

    @Override
    public void delete() {
        try {
            RemoveObjectivePacket pk = new RemoveObjectivePacket();
            setField(pk, "objectiveName", objectiveName);
            player.dataPacket(pk);
        } catch (Exception e) {
            player.getServer().getLogger().warning("Failed to remove scoreboard: " + e.getMessage());
        }
        entries.clear();
        currentRows = 0;
    }

    private String colorEntry(int index) {
        String[] colors = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f"};
        return colors[index % colors.length];
    }

    private void sendDisplayObjective() {
        SetDisplayObjectivePacket pk = new SetDisplayObjectivePacket();
        setField(pk, "displaySlot", displaySlot);
        setField(pk, "objectiveName", objectiveName);
        setField(pk, "displayName", title);
        player.dataPacket(pk);
    }

    public NukkitBoardImpl setDisplaySlot(int slot) {
        this.displaySlot = slot;
        sendDisplayObjective();
        return this;
    }

    public NukkitBoardImpl setCustomIcon(String iconName) {
        this.customIconName = iconName;
        sendDisplayObjective();
        return this;
    }

    private void sendScore() {
        try {
            SetScorePacket pk = new SetScorePacket();
            setField(pk, "action", 1);

            List<Object> entryList = new ArrayList<>();
            for (ScoreData data : entries) {
                Object entry = createScoreEntry(data);
                if (entry != null) {
                    entryList.add(entry);
                }
            }

            setField(pk, "entries", entryList);
            player.dataPacket(pk);
        } catch (Exception e) {
            player.getServer().getLogger().warning("Failed to send score: " + e.getMessage());
        }
    }

    private Object createScoreEntry(ScoreData data) {
        try {
            Class<?> entryClass = null;
            for (Class<?> innerClass : SetScorePacket.class.getDeclaredClasses()) {
                if (innerClass.getSimpleName().equals("Entry") || innerClass.getSimpleName().equals("ScoreEntry")) {
                    entryClass = innerClass;
                    break;
                }
            }

            if (entryClass == null) {
                return null;
            }

            Object entry = entryClass.getDeclaredConstructor().newInstance();
            setField(entry, "objectiveName", objectiveName);
            setField(entry, "score", data.score);
            setField(entry, "name", data.name);
            setField(entry, "type", 0);
            return entry;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            Field field = findField(obj.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(obj, value);
            }
        } catch (IllegalAccessException e) {
            for (Field field : obj.getClass().getFields()) {
                if (field.getName().equalsIgnoreCase(fieldName)) {
                    try {
                        field.setAccessible(true);
                        field.set(obj, value);
                    } catch (IllegalAccessException ignored) {
                    }
                    return;
                }
            }
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static class ScoreData {
        String name;
        int score;
    }
}