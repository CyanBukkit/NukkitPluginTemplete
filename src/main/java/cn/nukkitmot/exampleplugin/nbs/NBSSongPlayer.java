package cn.nukkitmot.exampleplugin.nbs;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.scheduler.TaskHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NBSSongPlayer extends SongPlayer {

    private static final float[] PITCH_VALUES = {
            0.5f, 0.529732f, 0.561231f, 0.594604f, 0.629961f, 0.667420f, 0.707107f, 0.749154f,
            0.793701f, 0.840896f, 0.890899f, 0.943874f, 1.0f, 1.059463f, 1.122462f, 1.189207f,
            1.259921f, 1.334840f, 1.414214f, 1.498307f, 1.587401f, 1.681793f, 1.781797f, 1.887749f, 2.0f
    };

    private static final Map<Integer, String> INSTRUMENT_SOUNDS = new HashMap<>();

    static {
        INSTRUMENT_SOUNDS.put(0, "note.harp");
        INSTRUMENT_SOUNDS.put(1, "note.bass");
        INSTRUMENT_SOUNDS.put(2, "note.bd");
        INSTRUMENT_SOUNDS.put(3, "note.snare");
        INSTRUMENT_SOUNDS.put(4, "note.hat");
        INSTRUMENT_SOUNDS.put(5, "note.guitar");
        INSTRUMENT_SOUNDS.put(6, "note.flute");
        INSTRUMENT_SOUNDS.put(7, "note.bell");
        INSTRUMENT_SOUNDS.put(8, "note.chime");
        INSTRUMENT_SOUNDS.put(9, "note.xylophone");
    }

    private final Position playPosition;
    private final float globalVolume;
    private final boolean useDistanceAttenuation;
    private final double maxDistance;
    private TaskHandler taskFuture;
    private int tickCount = 0;

    public NBSSongPlayer(Song song) {
        this(song, new Position(0, 0, 0, null), 100f, true, 50.0);
    }

    public NBSSongPlayer(Song song, Position position) {
        this(song, position, 100f, true, 50.0);
    }

    public NBSSongPlayer(Song song, Position position, float volume) {
        this(song, position, volume, true, 50.0);
    }

    public NBSSongPlayer(Song song, Position position, float volume, boolean useDistanceAttenuation, double maxDistance) {
        super(song);
        this.playPosition = position;
        this.globalVolume = volume;
        this.useDistanceAttenuation = useDistanceAttenuation;
        this.maxDistance = maxDistance;
    }

    @Override
    public void playTick(ArrayList<String> players, int tick) {
        if (song == null || song.getLayerHashMap() == null) {
            return;
        }

        float vol = (getVolume() / 100f) * (globalVolume / 100f);
        if (vol <= 0f) {
            return;
        }

        ArrayList<Player> targetPlayers = getTargetPlayers();
        if (targetPlayers.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, Layer> entry : song.getLayerHashMap().entrySet()) {
            Layer layer = entry.getValue();
            if (layer == null) {
                continue;
            }

            Note note = layer.getNote(tick);
            if (note == null) {
                continue;
            }

            String soundName = INSTRUMENT_SOUNDS.get((int) note.getInstrument());
            if (soundName == null) {
                continue;
            }

            int keyIndex = note.getKey() - 33;
            if (keyIndex < 0 || keyIndex >= PITCH_VALUES.length) {
                continue;
            }

            float pitch = PITCH_VALUES[keyIndex];
            float layerVol = (layer.getVolume() / 100f);

            for (Player player : targetPlayers) {
                sendSound(player, soundName, vol * layerVol, pitch);
            }
        }
    }

    private ArrayList<Player> getTargetPlayers() {
        ArrayList<Player> result = new ArrayList<>();
        List<String> playerNames;

        synchronized (playerList) {
            playerNames = new ArrayList<>(playerList);
        }

        if (playerNames.isEmpty()) {
            return result;
        }

        for (String name : playerNames) {
            Player player = Server.getInstance().getPlayerExact(name);
            if (player != null && player.isOnline()) {
                if (playPosition.getLevel() == null || player.getLevel() == playPosition.getLevel()) {
                    result.add(player);
                }
            }
        }

        return result;
    }

    private void sendSound(Player player, String soundName, float volume, float pitch) {
        if (useDistanceAttenuation && playPosition.getLevel() != null) {
            double distance = player.distance(playPosition);
            if (distance > maxDistance) {
                return;
            }

            float distanceFactor = 1.0f - (float) (distance / maxDistance);
            distanceFactor = Math.max(0.1f, distanceFactor * distanceFactor);
            volume *= distanceFactor;
        }

        if (volume < 0.01f) {
            return;
        }

        PlaySoundPacket pk = new PlaySoundPacket();
        pk.name = soundName;
        pk.volume = Math.min(volume * 100f, 100f);
        pk.pitch = pitch;
        pk.x = (int) playPosition.x;
        pk.y = (int) playPosition.y;
        pk.z = (int) playPosition.z;
        player.dataPacket(pk);
    }

    public void start() {
        if (playing) {
            return;
        }

        playing = true;
        tick = -1;
        tickCount = 0;

        int delay = (int) song.getDelay();
        taskFuture = Server.getInstance().getScheduler().scheduleRepeatingTask(
                NBSSoundManager.getInstance().getPlugin(),
                new Runnable() {
                    @Override
                    public void run() {
                        if (!playing || destroyed) {
                            return;
                        }

                        tick++;
                        if (tick >= song.getLength()) {
                            if (autoCycle) {
                                tick = 0;
                            } else {
                                stop();
                                return;
                            }
                        }

                        ArrayList<String> players;
                        synchronized (playerList) {
                            players = new ArrayList<>(playerList);
                        }

                        if (!players.isEmpty()) {
                            playTick(players, tick);
                        }
                    }
                },
                delay,
                true
        );
    }

    public void stop() {
        playing = false;
        if (taskFuture != null) {
            taskFuture.cancel();
            taskFuture = null;
        }
    }

    public void pause() {
        playing = false;
    }

    public void resume() {
        if (!playing && tick >= 0) {
            playing = true;
        }
    }

    public Position getPlayPosition() {
        return playPosition;
    }

    public float getGlobalVolume() {
        return globalVolume;
    }

    public boolean isUseDistanceAttenuation() {
        return useDistanceAttenuation;
    }

    public double getMaxDistance() {
        return maxDistance;
    }
}
