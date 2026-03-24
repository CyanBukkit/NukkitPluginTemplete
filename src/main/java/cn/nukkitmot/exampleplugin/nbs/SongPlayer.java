package cn.nukkitmot.exampleplugin.nbs;

import cn.nukkit.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SongPlayer {

    protected Song song;
    protected volatile boolean playing = false;
    protected volatile short tick = -1;
    protected final ArrayList<String> playerList;
    protected volatile boolean autoDestroy = false;
    protected volatile boolean destroyed = false;
    protected boolean autoCycle = true;
    protected byte fadeTarget = 100;
    protected byte volume = 100;
    protected byte fadeStart = volume;
    protected int fadeDuration = 60;
    protected int fadeDone = 0;
    protected long lastPlayed = 0;

    public SongPlayer(Song song) {
        this.song = song;
        this.playerList = new ArrayList<>();
    }

    public boolean getAutoCycle() {
        return autoCycle;
    }

    public void setAutoCycle(boolean autoCycle) {
        this.autoCycle = autoCycle;
    }

    public byte getFadeTarget() {
        return fadeTarget;
    }

    public void setFadeTarget(byte fadeTarget) {
        this.fadeTarget = fadeTarget;
    }

    public byte getFadeStart() {
        return fadeStart;
    }

    public void setFadeStart(byte fadeStart) {
        this.fadeStart = fadeStart;
    }

    public int getFadeDuration() {
        return fadeDuration;
    }

    public void setFadeDuration(int fadeDuration) {
        this.fadeDuration = fadeDuration;
    }

    public int getFadeDone() {
        return fadeDone;
    }

    public void setFadeDone(int fadeDone) {
        this.fadeDone = fadeDone;
    }

    protected void calculateFade() {
        if (fadeDone == fadeDuration) return;
        double targetVolume = fadeStart + ((fadeTarget - fadeStart) * ((double) fadeDone / fadeDuration));
        setVolume((byte) targetVolume);
        fadeDone++;
    }

    public List<String> getPlayerList() {
        synchronized (playerList) {
            return Collections.unmodifiableList(new ArrayList<>(playerList));
        }
    }

    public void addPlayer(Player player) {
        synchronized (playerList) {
            if (!playerList.contains(player.getName())) {
                playerList.add(player.getName());
            }
        }
    }

    public boolean getAutoDestroy() {
        return autoDestroy;
    }

    public void setAutoDestroy(boolean autoDestroy) {
        this.autoDestroy = autoDestroy;
    }

    public abstract void playTick(ArrayList<String> players, int tick);

    public void destroy() {
        destroyed = true;
        playing = false;
        tick = -1;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public short getTick() {
        return tick;
    }

    public void setTick(short tick) {
        this.tick = tick;
    }

    public void removePlayer(Player player) {
        synchronized (playerList) {
            playerList.remove(player.getName());
            if (playerList.isEmpty() && autoDestroy) {
                destroy();
            }
        }
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        this.volume = volume;
    }

    public Song getSong() {
        return song;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean hasPlayer(Player player) {
        synchronized (playerList) {
            return playerList.contains(player.getName());
        }
    }

    public void clearPlayers() {
        synchronized (playerList) {
            playerList.clear();
        }
    }
}
