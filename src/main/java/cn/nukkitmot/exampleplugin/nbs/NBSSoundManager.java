package cn.nukkitmot.exampleplugin.nbs;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.scheduler.ServerScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NBSSoundManager {

    private static NBSSoundManager instance;
    private static Plugin plugin;

    private final Map<Integer, NBSSongPlayer> activePlayers;
    private final AtomicInteger playerIdGenerator;
    private final Map<String, Song> songCache;
    private final PluginLogger logger;

    private NBSSoundManager(Plugin plugin) {
        this.plugin = plugin;
        this.activePlayers = new ConcurrentHashMap<>();
        this.playerIdGenerator = new AtomicInteger(0);
        this.songCache = new ConcurrentHashMap<>();
        this.logger = plugin.getLogger();

        NBSDecoder.setLogger(logger);
    }

    public static void init(Plugin plugin) {
        if (instance == null) {
            instance = new NBSSoundManager(plugin);
        }
    }

    public static NBSSoundManager getInstance() {
        return instance;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public NBSSongPlayer playNBS(Player player, File nbsFile) {
        return playNBS(player, nbsFile, 100f);
    }

    public NBSSongPlayer playNBS(Player player, File nbsFile, float volume) {
        Song song = loadSong(nbsFile);
        if (song == null) {
            return null;
        }

        NBSSongPlayer player2 = new NBSSongPlayer(song, player.getPosition(), volume);
        int playerId = playerIdGenerator.incrementAndGet();
        activePlayers.put(playerId, player2);

        player2.addPlayer(player);
        player2.setAutoDestroy(true);
        player2.start();

        return player2;
    }

    public NBSSongPlayer playNBS(File nbsFile, Position position) {
        return playNBS(nbsFile, position, 100f);
    }

    public NBSSongPlayer playNBS(File nbsFile, Position position, float volume) {
        Song song = loadSong(nbsFile);
        if (song == null) {
            return null;
        }

        NBSSongPlayer nbsPlayer = new NBSSongPlayer(song, position, volume);
        int playerId = playerIdGenerator.incrementAndGet();
        activePlayers.put(playerId, nbsPlayer);

        nbsPlayer.start();
        return nbsPlayer;
    }

    public NBSSongPlayer playNBSForAll(File nbsFile) {
        return playNBSForAll(nbsFile, new Position(0, 0, 0, null), 100f);
    }

    public NBSSongPlayer playNBSForAll(File nbsFile, float volume) {
        return playNBSForAll(nbsFile, new Position(0, 0, 0, null), volume);
    }

    public NBSSongPlayer playNBSForAll(File nbsFile, Position position) {
        return playNBSForAll(nbsFile, position, 100f);
    }

    public NBSSongPlayer playNBSForAll(File nbsFile, Position position, float volume) {
        Song song = loadSong(nbsFile);
        if (song == null) {
            return null;
        }

        NBSSongPlayer nbsPlayer = new NBSSongPlayer(song, position, volume, false, Double.MAX_VALUE);
        int playerId = playerIdGenerator.incrementAndGet();
        activePlayers.put(playerId, nbsPlayer);

        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            nbsPlayer.addPlayer(player);
        }

        nbsPlayer.setAutoDestroy(true);
        nbsPlayer.start();
        return nbsPlayer;
    }

    public Song loadSong(File nbsFile) {
        if (!nbsFile.exists()) {
            logger.error("NBS file not found: " + nbsFile.getAbsolutePath());
            return null;
        }

        String cacheKey = nbsFile.getAbsolutePath();
        Song cached = songCache.get(cacheKey);
        if (cached != null) {
            return new Song(cached);
        }

        Song song = NBSDecoder.parse(nbsFile);
        if (song != null) {
            songCache.put(cacheKey, song);
        }
        return song;
    }

    public void stopNBS(NBSSongPlayer nbsPlayer) {
        if (nbsPlayer == null) {
            return;
        }

        nbsPlayer.stop();
        nbsPlayer.destroy();

        for (Map.Entry<Integer, NBSSongPlayer> entry : activePlayers.entrySet()) {
            if (entry.getValue() == nbsPlayer) {
                activePlayers.remove(entry.getKey());
                break;
            }
        }
    }

    public void stopNBSForPlayer(Player player) {
        for (NBSSongPlayer nbsPlayer : activePlayers.values()) {
            if (nbsPlayer.hasPlayer(player)) {
                nbsPlayer.removePlayer(player);
            }
        }
    }

    public void stopAll() {
        for (NBSSongPlayer nbsPlayer : activePlayers.values()) {
            nbsPlayer.stop();
            nbsPlayer.destroy();
        }
        activePlayers.clear();
    }

    public void pauseNBS(NBSSongPlayer nbsPlayer) {
        if (nbsPlayer != null) {
            nbsPlayer.pause();
        }
    }

    public void resumeNBS(NBSSongPlayer nbsPlayer) {
        if (nbsPlayer != null) {
            nbsPlayer.resume();
        }
    }

    public List<NBSSongPlayer> getActivePlayers() {
        return Collections.unmodifiableList(new ArrayList<>(activePlayers.values()));
    }

    public List<NBSSongPlayer> getPlayersForPlayer(Player player) {
        List<NBSSongPlayer> result = new ArrayList<>();
        for (NBSSongPlayer nbsPlayer : activePlayers.values()) {
            if (nbsPlayer.hasPlayer(player)) {
                result.add(nbsPlayer);
            }
        }
        return result;
    }

    public void addPlayerToNBS(NBSSongPlayer nbsPlayer, Player player) {
        if (nbsPlayer != null && !nbsPlayer.hasPlayer(player)) {
            nbsPlayer.addPlayer(player);
        }
    }

    public void removePlayerFromNBS(NBSSongPlayer nbsPlayer, Player player) {
        if (nbsPlayer != null) {
            nbsPlayer.removePlayer(player);
        }
    }

    public void setNBSVolume(NBSSongPlayer nbsPlayer, byte volume) {
        if (nbsPlayer != null) {
            nbsPlayer.setVolume(volume);
        }
    }

    public void clearCache() {
        songCache.clear();
    }

    public void removeFromCache(File nbsFile) {
        if (nbsFile != null) {
            songCache.remove(nbsFile.getAbsolutePath());
        }
    }

    public boolean isPlaying(NBSSongPlayer nbsPlayer) {
        return nbsPlayer != null && nbsPlayer.isPlaying();
    }
}
