package cn.nukkitmot.exampleplugin.camera;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.TaskHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 运镜序列类
 * <p>
 * 用于创建复杂的镜头运动序列，支持多个关键帧的平滑过渡。
 *
 * @author QingTong
 * @version 1.0.0
 * @since 1.0.0
 */
public class CameraSequence {

    private final List<SequenceFrame> frames;
    private final Player targetPlayer;
    private int currentFrame;
    private boolean playing;
    private boolean paused;
    private TaskHandler taskHandler;
    private boolean loop;
    private Runnable onComplete;
    private final Plugin plugin;

    /**
     * 创建运镜序列
     *
     * @param plugin 插件实例
     * @param player 目标玩家
     */
    public CameraSequence(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.targetPlayer = player;
        this.frames = new ArrayList<>();
        this.currentFrame = 0;
        this.playing = false;
        this.paused = false;
        this.loop = false;
    }

    /**
     * 添加关键帧
     *
     * @param position   镜头位置
     * @param duration   持续时间（tick）
     * @param easeType   缓动类型
     * @return CameraSequence 实例（链式调用）
     */
    public CameraSequence addFrame(CameraPosition position, int duration, CameraInstruction.EaseType easeType) {
        frames.add(new SequenceFrame(position, duration, easeType));
        return this;
    }

    /**
     * 添加关键帧（使用默认线性缓动）
     *
     * @param position 镜头位置
     * @param duration 持续时间（tick）
     * @return CameraSequence 实例（链式调用）
     */
    public CameraSequence addFrame(CameraPosition position, int duration) {
        return addFrame(position, duration, CameraInstruction.EaseType.LINEAR);
    }

    /**
     * 添加相对玩家的关键帧
     *
     * @param offsetX    X轴偏移
     * @param offsetY    Y轴偏移
     * @param offsetZ    Z轴偏移
     * @param pitch      俯仰角
     * @param yaw        偏航角
     * @param duration   持续时间（tick）
     * @param easeType   缓动类型
     * @return CameraSequence 实例（链式调用）
     */
    public CameraSequence addRelativeFrame(double offsetX, double offsetY, double offsetZ,
                                            double pitch, double yaw, int duration,
                                            CameraInstruction.EaseType easeType) {
        Position playerPos = targetPlayer.getPosition();
        CameraPosition pos = new CameraPosition(
                playerPos.x + offsetX,
                playerPos.y + offsetY,
                playerPos.z + offsetZ,
                pitch, yaw, 0
        );
        return addFrame(pos, duration, easeType);
    }

    /**
     * 添加环绕玩家的圆形运动帧
     *
     * @param radius      环绕半径
     * @param height      高度
     * @param startAngle  起始角度（度）
     * @param endAngle    结束角度（度）
     * @param duration    持续时间（tick）
     * @param frameCount  帧数
     * @return CameraSequence 实例（链式调用）
     */
    public CameraSequence addOrbitFrames(double radius, double height,
                                          double startAngle, double endAngle,
                                          int duration, int frameCount) {
        double angleStep = (endAngle - startAngle) / frameCount;
        int frameDuration = duration / frameCount;

        Position playerPos = targetPlayer.getPosition();
        for (int i = 0; i <= frameCount; i++) {
            double angle = Math.toRadians(startAngle + angleStep * i);
            double x = playerPos.x + radius * Math.cos(angle);
            double z = playerPos.z + radius * Math.sin(angle);
            double y = playerPos.y + height;

            CameraPosition pos = new CameraPosition(x, y, z);
            addFrame(pos, frameDuration, CameraInstruction.EaseType.IN_OUT_SINE);
        }
        return this;
    }

    /**
     * 添加预设镜头帧
     *
     * @param preset   预设配置
     * @param distance 距离
     * @param duration 持续时间（tick）
     * @param easeType 缓动类型
     * @return CameraSequence 实例（链式调用）
     */
    public CameraSequence addPresetFrame(CameraPreset preset, double distance,
                                          int duration, CameraInstruction.EaseType easeType) {
        CameraPosition pos = CameraPosition.fromPreset(targetPlayer.getPosition(), preset, distance);
        return addFrame(pos, duration, easeType);
    }

    /**
     * 播放运镜序列
     */
    public void play() {
        if (frames.isEmpty() || playing) {
            return;
        }

        playing = true;
        paused = false;
        currentFrame = 0;

        executeFrame(0);
    }

    /**
     * 暂停运镜序列
     */
    public void pause() {
        paused = true;
        if (taskHandler != null) {
            taskHandler.cancel();
            taskHandler = null;
        }
    }

    /**
     * 继续播放运镜序列
     */
    public void resume() {
        if (paused && playing) {
            paused = false;
            executeFrame(currentFrame);
        }
    }

    /**
     * 停止运镜序列
     */
    public void stop() {
        playing = false;
        paused = false;
        currentFrame = 0;

        if (taskHandler != null) {
            taskHandler.cancel();
            taskHandler = null;
        }

        CameraManager.getInstance().clearCamera(targetPlayer);
    }

    /**
     * 执行指定帧
     *
     * @param frameIndex 帧索引
     */
    private void executeFrame(int frameIndex) {
        if (frameIndex >= frames.size()) {
            if (loop) {
                currentFrame = 0;
                executeFrame(0);
            } else {
                playing = false;
                if (onComplete != null) {
                    onComplete.run();
                }
            }
            return;
        }

        currentFrame = frameIndex;
        SequenceFrame frame = frames.get(frameIndex);

        CameraManager.getInstance().setCameraPosition(
                targetPlayer,
                frame.position,
                frame.duration / 20f,
                frame.easeType
        );

        taskHandler = Server.getInstance().getScheduler().scheduleDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (playing && !paused) {
                    executeFrame(frameIndex + 1);
                }
            }
        }, frame.duration);
    }

    /**
     * 设置是否循环播放
     *
     * @param loop true 表示循环播放
     * @return CameraSequence 实例（链式调用）
     */
    public CameraSequence setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    /**
     * 设置完成回调
     *
     * @param onComplete 完成时执行的回调
     * @return CameraSequence 实例（链式调用）
     */
    public CameraSequence onComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    /**
     * 是否正在播放
     *
     * @return true 如果正在播放
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * 是否已暂停
     *
     * @return true 如果已暂停
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * 获取当前帧索引
     *
     * @return 当前帧索引
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * 获取总帧数
     *
     * @return 总帧数
     */
    public int getFrameCount() {
        return frames.size();
    }

    /**
     * 获取目标玩家
     *
     * @return 目标玩家
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    /**
     * 清除所有帧
     */
    public void clearFrames() {
        frames.clear();
        currentFrame = 0;
    }

    /**
     * 关键帧数据类
     */
    private static class SequenceFrame {
        final CameraPosition position;
        final int duration;
        final CameraInstruction.EaseType easeType;

        SequenceFrame(CameraPosition position, int duration, CameraInstruction.EaseType easeType) {
            this.position = position;
            this.duration = duration;
            this.easeType = easeType;
        }
    }

    /**
     * 创建简单的运镜序列构建器
     *
     * @param plugin 插件实例
     * @param player 目标玩家
     * @return CameraSequence 实例
     */
    public static CameraSequence builder(Plugin plugin, Player player) {
        return new CameraSequence(plugin, player);
    }
}
