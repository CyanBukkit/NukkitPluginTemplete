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
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetDisplayObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class NukkitBoardImpl implements IBoard {

    private final Player player;
    @Getter
    private final String objectiveName;
    private String title;
    private final List<String> currentEntries = new ArrayList<>();
    private int displaySlot = 0;

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

        removeAllScores();

        this.title = title;
        sendDisplayObjective();

        for (int i = 0; i < newRows; i++) {
            String entryName = createEntryName(i);
            String displayText = list.get(i);
            addScore(entryName, newRows - i, displayText);
            currentEntries.add(entryName);
        }
    }

    private String createEntryName(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("§");
        switch (index % 16) {
            case 0: sb.append("0"); break;
            case 1: sb.append("1"); break;
            case 2: sb.append("2"); break;
            case 3: sb.append("3"); break;
            case 4: sb.append("4"); break;
            case 5: sb.append("5"); break;
            case 6: sb.append("6"); break;
            case 7: sb.append("7"); break;
            case 8: sb.append("8"); break;
            case 9: sb.append("9"); break;
            case 10: sb.append("a"); break;
            case 11: sb.append("b"); break;
            case 12: sb.append("c"); break;
            case 13: sb.append("d"); break;
            case 14: sb.append("e"); break;
            case 15: sb.append("f"); break;
        }
        return sb.toString() + "§r§" + (index % 16);
    }

    private void addScore(String entryName, int score, String displayText) {
        try {
            SetScorePacket pk = new SetScorePacket();
            setField(pk, "action", 0);

            Object entry = createScoreEntry(entryName, score);
            if (entry == null) {
                return;
            }

            List<Object> entryList = new ArrayList<>();
            entryList.add(entry);
            setField(pk, "entries", entryList);

            player.dataPacket(pk);
        } catch (Exception e) {
            player.getServer().getLogger().warning("Failed to add score: " + e.getMessage());
        }
    }

    private void removeAllScores() {
        try {
            for (String entryName : currentEntries) {
                SetScorePacket pk = new SetScorePacket();
                setField(pk, "action", 1);

                Object entry = createScoreEntry(entryName, 0);
                if (entry == null) {
                    continue;
                }

                List<Object> entryList = new ArrayList<>();
                entryList.add(entry);
                setField(pk, "entries", entryList);

                player.dataPacket(pk);
            }
            currentEntries.clear();
        } catch (Exception e) {
            player.getServer().getLogger().warning("Failed to remove scores: " + e.getMessage());
        }
    }

    private Object createScoreEntry(String entryName, int score) {
        try {
            Class<?> entryClass = null;
            for (Class<?> innerClass : SetScorePacket.class.getDeclaredClasses()) {
                if (innerClass.getSimpleName().equals("Entry")) {
                    entryClass = innerClass;
                    break;
                }
            }

            if (entryClass == null) {
                return createScoreEntryViaReflection(entryName, score);
            }

            Object entry = entryClass.getDeclaredConstructor().newInstance();
            setField(entry, "objectiveName", objectiveName);
            setField(entry, "score", score);
            setField(entry, "name", entryName);
            return entry;
        } catch (Exception e) {
            return createScoreEntryViaReflection(entryName, score);
        }
    }

    private Object createScoreEntryViaReflection(String entryName, int score) {
        try {
            for (Method method : SetScorePacket.class.getMethods()) {
                if (method.getName().equals("setScore") && method.getParameterCount() == 3) {
                    return method.invoke(null, entryName, score, objectiveName);
                }
            }
            for (Method method : SetScorePacket.class.getMethods()) {
                if (method.getName().equals("createEntry") || method.getName().equals("createScoreEntry")) {
                    return method.invoke(null, entryName, score, objectiveName);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void delete() {
        removeAllScores();
        try {
            RemoveObjectivePacket pk = new RemoveObjectivePacket();
            setField(pk, "objectiveName", objectiveName);
            player.dataPacket(pk);
        } catch (Exception e) {
            player.getServer().getLogger().warning("Failed to remove objective: " + e.getMessage());
        }
    }

    private void sendDisplayObjective() {
        try {
            SetDisplayObjectivePacket pk = new SetDisplayObjectivePacket();
            setField(pk, "displaySlot", displaySlot);
            setField(pk, "objectiveName", objectiveName);
            setField(pk, "displayName", title);
            setField(pk, "criteriaName", "dummy");
            setField(pk, "sortOrder", 0);
            player.dataPacket(pk);
        } catch (Exception e) {
            player.getServer().getLogger().warning("Failed to send display objective: " + e.getMessage());
        }
    }

    public NukkitBoardImpl setDisplaySlot(int slot) {
        this.displaySlot = slot;
        sendDisplayObjective();
        return this;
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
}
