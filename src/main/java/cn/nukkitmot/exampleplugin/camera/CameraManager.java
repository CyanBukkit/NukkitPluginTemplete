package cn.nukkitmot.exampleplugin.camera;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.CameraInstructionPacket;
import cn.nukkit.network.protocol.types.camera.CameraEase;
import cn.nukkit.network.protocol.types.camera.CameraSetInstruction;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.CameraPresetManager;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 镜头管理器
 * <p>
 * 核心 API 类，提供玩家镜头控制的所有功能。
 * 支持固定镜头、跟随镜头、预设镜头和运镜序列。
 *
 * <h3>使用示例：</h3>
 * <pre>
 * // 初始化
 * CameraManager.init(plugin);
 *
 * // 设置固定镜头
 * CameraManager.getInstance().setFixedCamera(player, 100, 50, 100, 2.0f);
 *
 * // 设置跟随镜头
 * CameraManager.getInstance().setFollowCamera(player, 0, 2, -5, 1.5f);
 *
 * // 设置2D侧面视角
 * CameraManager.getInstance().set2DCamera(player, CameraPreset.SIDE_LEFT, 8.0);
 *
 * // 清除镜头
 * CameraManager.getInstance().clearCamera(player);
 * </pre>
 *
 * @author QingTong
 * @version 1.0.0
 * @since 1.0.0
 */
public class CameraManager {

    private static CameraManager instance;
    private static Plugin plugin;

    private final Map<UUID, CameraState> playerCameras;
    private final Map<UUID, CameraSequence> activeSequences;
    private final PluginLogger logger;

    private CameraManager(Plugin plugin) {
        CameraManager.plugin = plugin;
        this.playerCameras = new ConcurrentHashMap<>();
        this.activeSequences = new ConcurrentHashMap<>();
        this.logger = plugin.getLogger();
    }

    /**
     * 初始化镜头管理器
     * <p>
     * 必须在插件启用时调用一次。
     *
     * @param plugin 插件实例
     */
    public static void init(Plugin plugin) {
        if (instance == null) {
            instance = new CameraManager(plugin);
        }
    }

    /**
     * 获取镜头管理器实例
     * <p>
     * 必须先调用 {@link #init(Plugin)} 初始化。
     *
     * @return CameraManager 实例
     */
    public static CameraManager getInstance() {
        return instance;
    }

    // ==================== 基础镜头控制 ====================

    /**
     * 设置固定镜头
     * <p>
     * 镜头固定在指定位置，不跟随玩家移动。
     *
     * @param player   目标玩家
     * @param x        X坐标
     * @param y        Y坐标
     * @param z        Z坐标
     * @param easeTime 缓动时间（秒）
     */
    public void setFixedCamera(Player player, double x, double y, double z, float easeTime) {
        setFixedCamera(player, x, y, z, 0, 0, easeTime);
    }

    /**
     * 设置固定镜头（带旋转）
     * <p>
     * 镜头固定在指定位置和角度，不跟随玩家移动。
     *
     * @param player   目标玩家
     * @param x        X坐标
     * @param y        Y坐标
     * @param z        Z坐标
     * @param pitch    俯仰角
     * @param yaw      偏航角
     * @param easeTime 缓动时间（秒）
     */
    public void setFixedCamera(Player player, double x, double y, double z, double pitch, double yaw, float easeTime) {
        CameraPosition position = new CameraPosition(x, y, z, pitch, yaw, 0);
        setCameraPosition(player, position, easeTime, CameraInstruction.EaseType.LINEAR);
        updatePlayerCameraState(player, CameraMode.FIXED, position, false);
    }

    /**
     * 设置跟随镜头
     * <p>
     * 镜头跟随玩家移动，保持相对位置。
     *
     * @param player   目标玩家
     * @param offsetX  X轴偏移
     * @param offsetY  Y轴偏移
     * @param offsetZ  Z轴偏移
     * @param easeTime 缓动时间（秒）
     */
    public void setFollowCamera(Player player, double offsetX, double offsetY, double offsetZ, float easeTime) {
        Position playerPos = player.getPosition();
        CameraPosition position = CameraPosition.relative(playerPos, offsetX, offsetY, offsetZ);
        setCameraPosition(player, position, easeTime, CameraInstruction.EaseType.LINEAR);
        updatePlayerCameraState(player, CameraMode.FOLLOW, position, true);
    }

    /**
     * 设置看向玩家的固定镜头
     * <p>
     * 镜头固定在指定位置，始终朝向玩家。
     *
     * @param player   目标玩家
     * @param x        镜头X坐标
     * @param y        镜头Y坐标
     * @param z        镜头Z坐标
     * @param easeTime 缓动时间（秒）
     */
    public void setFacingCamera(Player player, double x, double y, double z, float easeTime) {
        CameraPosition position = new CameraPosition(new Vector3(x, y, z), player.getPosition());
        setCameraPosition(player, position, easeTime, CameraInstruction.EaseType.LINEAR);
        updatePlayerCameraState(player, CameraMode.FIXED, position, false);
    }

    // ==================== 预设镜头 ====================

    /**
     * 设置2D侧面视角
     * <p>
     * 适合横版2D风格游戏的侧面视角。
     *
     * @param player   目标玩家
     * @param side     侧面方向（LEFT 或 RIGHT）
     * @param distance 距离玩家的距离
     * @param easeTime 缓动时间（秒）
     */
    public void set2DCamera(Player player, CameraSide side, double distance, float easeTime) {
        CameraPreset preset = (side == CameraSide.LEFT) ? CameraPreset.SIDE_LEFT : CameraPreset.SIDE_RIGHT;
        setPresetCamera(player, preset, distance, easeTime);
    }

    /**
     * 设置俯视视角
     * <p>
     * 从正上方俯视玩家的视角。
     *
     * @param player   目标玩家
     * @param height   俯视高度
     * @param easeTime 缓动时间（秒）
     */
    public void setTopDownCamera(Player player, double height, float easeTime) {
        setPresetCamera(player, CameraPreset.TOP_DOWN, height, easeTime);
    }

    /**
     * 设置等距视角
     * <p>
     * 经典的45度等距视角。
     *
     * @param player   目标玩家
     * @param distance 距离
     * @param easeTime 缓动时间（秒）
     */
    public void setIsometricCamera(Player player, double distance, float easeTime) {
        setPresetCamera(player, CameraPreset.ISOMETRIC, distance, easeTime);
    }

    /**
     * 设置预设镜头
     * <p>
     * 使用预设配置设置镜头。
     *
     * @param player   目标玩家
     * @param preset   预设配置
     * @param distance 距离
     * @param easeTime 缓动时间（秒）
     */
    public void setPresetCamera(Player player, CameraPreset preset, double distance, float easeTime) {
        CameraPosition position = CameraPosition.fromPreset(player.getPosition(), preset, distance);
        setCameraPosition(player, position, easeTime, CameraInstruction.EaseType.IN_OUT_SINE);
        updatePlayerCameraState(player, CameraMode.FIXED, position, false);
    }

    // ==================== 高级镜头控制 ====================

    /**
     * 设置镜头位置
     * <p>
     * 发送相机指令到客户端设置镜头。
     *
     * @param player   目标玩家
     * @param position 镜头位置
     * @param easeTime 缓动时间（秒）
     * @param easeType 缓动类型
     */
    public void setCameraPosition(Player player, CameraPosition position, float easeTime, CameraInstruction.EaseType easeType) {
        if (!player.isOnline()) {
            return;
        }

        CameraInstructionPacket pk = new CameraInstructionPacket();
        CameraSetInstruction cameraSetInstruction = new CameraSetInstruction();
        cameraSetInstruction.setPreset(CameraPresetManager.FREE);

        Vector3 pos = position.getPosition();
        cameraSetInstruction.setPos(new Vector3f((float) pos.x, (float) pos.y, (float) pos.z));

        Vector3 rot = position.getRotation();
        cameraSetInstruction.setRot(new Vector2f((float) rot.x, (float) rot.y));

        if (easeTime > 0) {
            cameraSetInstruction.setEase(new CameraSetInstruction.EaseData(convertEaseType(easeType), easeTime));
        }

        pk.setSetInstruction(cameraSetInstruction);
        player.dataPacket(pk);
    }

    private CameraEase convertEaseType(CameraInstruction.EaseType easeType) {
        if (easeType == null) {
            return CameraEase.LINEAR;
        }
        switch (easeType) {
            case LINEAR:
                return CameraEase.LINEAR;
            case SPRING:
                return CameraEase.SPRING;
            case IN_QUAD:
                return CameraEase.EASE_IN_QUAD;
            case OUT_QUAD:
                return CameraEase.EASE_OUT_QUAD;
            case IN_OUT_QUAD:
                return CameraEase.EASE_IN_OUT_QUAD;
            case IN_CUBIC:
                return CameraEase.EASE_IN_CUBIC;
            case OUT_CUBIC:
                return CameraEase.EASE_OUT_CUBIC;
            case IN_OUT_CUBIC:
                return CameraEase.EASE_IN_OUT_CUBIC;
            case IN_QUART:
                return CameraEase.EASE_IN_QUART;
            case OUT_QUART:
                return CameraEase.EASE_OUT_QUART;
            case IN_OUT_QUART:
                return CameraEase.EASE_IN_OUT_QUART;
            case IN_SINE:
                return CameraEase.EASE_IN_SINE;
            case OUT_SINE:
                return CameraEase.EASE_OUT_SINE;
            case IN_OUT_SINE:
                return CameraEase.EASE_IN_OUT_SINE;
            case IN_EXPO:
                return CameraEase.EASE_IN_EXPO;
            case OUT_EXPO:
                return CameraEase.EASE_OUT_EXPO;
            case IN_OUT_EXPO:
                return CameraEase.EASE_IN_OUT_EXPO;
            case IN_CIRC:
                return CameraEase.EASE_IN_CIRC;
            case OUT_CIRC:
                return CameraEase.EASE_OUT_CIRC;
            case IN_OUT_CIRC:
                return CameraEase.EASE_IN_OUT_CIRC;
            case IN_BACK:
                return CameraEase.EASE_IN_BACK;
            case OUT_BACK:
                return CameraEase.EASE_OUT_BACK;
            case IN_OUT_BACK:
                return CameraEase.EASE_IN_OUT_BACK;
            case IN_ELASTIC:
                return CameraEase.EASE_IN_ELASTIC;
            case OUT_ELASTIC:
                return CameraEase.EASE_OUT_ELASTIC;
            case IN_OUT_ELASTIC:
                return CameraEase.EASE_IN_OUT_ELASTIC;
            case IN_BOUNCE:
                return CameraEase.EASE_IN_BOUNCE;
            case OUT_BOUNCE:
                return CameraEase.EASE_OUT_BOUNCE;
            case IN_OUT_BOUNCE:
                return CameraEase.EASE_IN_OUT_BOUNCE;
            default:
                return CameraEase.LINEAR;
        }
    }

    /**
     * 使用指令设置镜头
     * <p>
     * 通过 CameraInstruction 对象设置镜头。
     *
     * @param player      目标玩家
     * @param instruction 镜头指令
     */
    public void setCameraInstruction(Player player, CameraInstruction instruction) {
        if (!player.isOnline()) {
            return;
        }

        String cmd = instruction.toCommandString(player);
        Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), cmd);
    }

    // ==================== 运镜序列 ====================

    /**
     * 创建运镜序列
     * <p>
     * 为指定玩家创建一个新的运镜序列。
     *
     * @param player 目标玩家
     * @return CameraSequence 实例
     */
    public CameraSequence createSequence(Player player) {
        stopSequence(player);
        CameraSequence sequence = new CameraSequence(plugin, player);
        activeSequences.put(player.getUniqueId(), sequence);
        return sequence;
    }

    /**
     * 播放运镜序列
     * <p>
     * 开始播放指定玩家的运镜序列。
     *
     * @param player 目标玩家
     */
    public void playSequence(Player player) {
        CameraSequence sequence = activeSequences.get(player.getUniqueId());
        if (sequence != null) {
            sequence.play();
        }
    }

    /**
     * 暂停运镜序列
     * <p>
     * 暂停指定玩家的运镜序列。
     *
     * @param player 目标玩家
     */
    public void pauseSequence(Player player) {
        CameraSequence sequence = activeSequences.get(player.getUniqueId());
        if (sequence != null) {
            sequence.pause();
        }
    }

    /**
     * 停止运镜序列
     * <p>
     * 停止并清除指定玩家的运镜序列。
     *
     * @param player 目标玩家
     */
    public void stopSequence(Player player) {
        CameraSequence sequence = activeSequences.remove(player.getUniqueId());
        if (sequence != null) {
            sequence.stop();
        }
    }

    /**
     * 获取运镜序列
     * <p>
     * 获取指定玩家的当前运镜序列。
     *
     * @param player 目标玩家
     * @return CameraSequence 实例，如果没有则返回 null
     */
    public CameraSequence getSequence(Player player) {
        return activeSequences.get(player.getUniqueId());
    }

    // ==================== 镜头清除 ====================

    /**
     * 清除玩家镜头
     * <p>
     * 恢复玩家正常视角。
     *
     * @param player 目标玩家
     */
    public void clearCamera(Player player) {
        if (!player.isOnline()) {
            return;
        }

        CameraInstructionPacket pk = new CameraInstructionPacket();
        pk.setClear(OptionalBoolean.of(true));
        player.dataPacket(pk);

        playerCameras.remove(player.getUniqueId());
        stopSequence(player);
    }

    /**
     * 清除所有玩家镜头
     * <p>
     * 恢复所有玩家的正常视角。
     */
    public void clearAllCameras() {
        for (CameraSequence sequence : activeSequences.values()) {
            sequence.stop();
        }
        activeSequences.clear();

        for (UUID uuid : playerCameras.keySet()) {
            Player player = Server.getInstance().getOnlinePlayers().get(uuid);
            if (player != null && player.isOnline()) {
                CameraInstructionPacket pk = new CameraInstructionPacket();
                pk.setClear(OptionalBoolean.of(true));
                player.dataPacket(pk);
            }
        }
        playerCameras.clear();
    }

    // ==================== 状态查询 ====================

    /**
     * 检查玩家是否有活动镜头
     * <p>
     * 判断指定玩家当前是否被镜头控制。
     *
     * @param player 目标玩家
     * @return true 如果有活动镜头
     */
    public boolean hasActiveCamera(Player player) {
        return playerCameras.containsKey(player.getUniqueId());
    }

    /**
     * 获取玩家镜头状态
     * <p>
     * 获取指定玩家的当前镜头状态信息。
     *
     * @param player 目标玩家
     * @return CameraState 实例，如果没有则返回 null
     */
    public CameraState getCameraState(Player player) {
        return playerCameras.get(player.getUniqueId());
    }

    /**
     * 更新玩家镜头状态
     *
     * @param player    目标玩家
     * @param mode      镜头模式
     * @param position  镜头位置
     * @param following 是否跟随
     */
    private void updatePlayerCameraState(Player player, CameraMode mode, CameraPosition position, boolean following) {
        playerCameras.put(player.getUniqueId(), new CameraState(mode, position, following));
    }

    // ==================== 侧面方向枚举 ====================

    /**
     * 2D侧面方向枚举
     */
    public enum CameraSide {
        /**
         * 左侧
         */
        LEFT,

        /**
         * 右侧
         */
        RIGHT
    }

    /**
     * 镜头状态类
     * <p>
     * 记录玩家的当前镜头状态信息。
     */
    public static class CameraState {
        private final CameraMode mode;
        private final CameraPosition position;
        private final boolean following;

        CameraState(CameraMode mode, CameraPosition position, boolean following) {
            this.mode = mode;
            this.position = position;
            this.following = following;
        }

        /**
         * 获取镜头模式
         *
         * @return 镜头模式
         */
        public CameraMode getMode() {
            return mode;
        }

        /**
         * 获取镜头位置
         *
         * @return 镜头位置
         */
        public CameraPosition getPosition() {
            return position;
        }

        /**
         * 是否跟随玩家
         *
         * @return true 如果跟随玩家
         */
        public boolean isFollowing() {
            return following;
        }
    }
}
