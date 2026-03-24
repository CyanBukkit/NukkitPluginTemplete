package cn.nukkitmot.exampleplugin.camera;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.math.Vector3;

import java.util.HashMap;
import java.util.Map;

/**
 * 镜头指令封装类
 * <p>
 * 封装了发送到客户端的相机指令数据，支持多种镜头控制方式。
 *
 * @author QingTong
 * @version 1.0.0
 * @since 1.0.0
 */
public class CameraInstruction {

    private final InstructionType type;
    private final CameraMode mode;
    private final CameraPosition position;
    private final CameraPreset preset;
    private final String presetName;
    private final float easeTime;
    private final EaseType easeType;
    private final boolean defaultPreset;

    private CameraInstruction(Builder builder) {
        this.type = builder.type;
        this.mode = builder.mode;
        this.position = builder.position;
        this.preset = builder.preset;
        this.presetName = builder.presetName;
        this.easeTime = builder.easeTime;
        this.easeType = builder.easeType;
        this.defaultPreset = builder.defaultPreset;
    }

    /**
     * 获取指令类型
     *
     * @return 指令类型
     */
    public InstructionType getType() {
        return type;
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
     * 获取预设配置
     *
     * @return 预设配置
     */
    public CameraPreset getPreset() {
        return preset;
    }

    /**
     * 获取预设名称
     *
     * @return 预设名称
     */
    public String getPresetName() {
        return presetName;
    }

    /**
     * 获取缓动时间
     *
     * @return 缓动时间（秒）
     */
    public float getEaseTime() {
        return easeTime;
    }

    /**
     * 获取缓动类型
     *
     * @return 缓动类型
     */
    public EaseType getEaseType() {
        return easeType;
    }

    /**
     * 是否为默认预设
     *
     * @return true 如果是默认预设
     */
    public boolean isDefaultPreset() {
        return defaultPreset;
    }

    /**
     * 将指令转换为命令字符串
     *
     * @param player 目标玩家
     * @return 命令字符串
     */
    public String toCommandString(Player player) {
        StringBuilder cmd = new StringBuilder("/camera ");
        cmd.append(player.getName()).append(" ");

        switch (type) {
            case CLEAR:
                cmd.append("clear");
                break;
            case SET:
                cmd.append("set ");
                if (presetName != null) {
                    cmd.append(presetName);
                    if (defaultPreset) {
                        cmd.append(" default");
                    }
                } else if (preset != null) {
                    cmd.append(preset.getName());
                }

                if (easeTime > 0) {
                    cmd.append(" ease ").append(easeTime).append(" ").append(easeType.getName());
                }

                if (position != null) {
                    Vector3 pos = position.getPosition();
                    cmd.append(" pos ").append(pos.x).append(" ").append(pos.y).append(" ").append(pos.z);

                    Vector3 rot = position.getRotation();
                    if (rot.x != 0 || rot.y != 0) {
                        cmd.append(" rot ").append(rot.x).append(" ").append(rot.y);
                    }
                }
                break;
            case FADE:
                cmd.append("fade");
                break;
        }

        return cmd.toString();
    }

    /**
     * 指令类型枚举
     */
    public enum InstructionType {
        /**
         * 清除镜头设置，恢复正常视角
         */
        CLEAR,

        /**
         * 设置镜头
         */
        SET,

        /**
         * 镜头淡入淡出效果
         */
        FADE
    }

    /**
     * 缓动类型枚举
     */
    public enum EaseType {
        LINEAR("linear"),
        SPRING("spring"),
        IN_QUAD("in_quad"),
        OUT_QUAD("out_quad"),
        IN_OUT_QUAD("in_out_quad"),
        IN_CUBIC("in_cubic"),
        OUT_CUBIC("out_cubic"),
        IN_OUT_CUBIC("in_out_cubic"),
        IN_QUART("in_quart"),
        OUT_QUART("out_quart"),
        IN_OUT_QUART("in_out_quart"),
        IN_SINE("in_sine"),
        OUT_SINE("out_sine"),
        IN_OUT_SINE("in_out_sine"),
        IN_EXPO("in_expo"),
        OUT_EXPO("out_expo"),
        IN_OUT_EXPO("in_out_expo"),
        IN_CIRC("in_circ"),
        OUT_CIRC("out_circ"),
        IN_OUT_CIRC("in_out_circ"),
        IN_BOUNCE("in_bounce"),
        OUT_BOUNCE("out_bounce"),
        IN_OUT_BOUNCE("in_out_bounce"),
        IN_BACK("in_back"),
        OUT_BACK("out_back"),
        IN_OUT_BACK("in_out_back"),
        IN_ELASTIC("in_elastic"),
        OUT_ELASTIC("out_elastic"),
        IN_OUT_ELASTIC("in_out_elastic");

        private final String name;

        EaseType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 构建器类
     */
    public static class Builder {
        private InstructionType type = InstructionType.SET;
        private CameraMode mode;
        private CameraPosition position;
        private CameraPreset preset;
        private String presetName;
        private float easeTime = 0;
        private EaseType easeType = EaseType.LINEAR;
        private boolean defaultPreset = false;

        /**
         * 设置指令类型
         *
         * @param type 指令类型
         * @return Builder 实例
         */
        public Builder type(InstructionType type) {
            this.type = type;
            return this;
        }

        /**
         * 设置镜头模式
         *
         * @param mode 镜头模式
         * @return Builder 实例
         */
        public Builder mode(CameraMode mode) {
            this.mode = mode;
            return this;
        }

        /**
         * 设置镜头位置
         *
         * @param position 镜头位置
         * @return Builder 实例
         */
        public Builder position(CameraPosition position) {
            this.position = position;
            return this;
        }

        /**
         * 设置预设配置
         *
         * @param preset 预设配置
         * @return Builder 实例
         */
        public Builder preset(CameraPreset preset) {
            this.preset = preset;
            return this;
        }

        /**
         * 设置预设名称
         *
         * @param presetName 预设名称
         * @return Builder 实例
         */
        public Builder presetName(String presetName) {
            this.presetName = presetName;
            return this;
        }

        /**
         * 设置缓动时间
         *
         * @param easeTime 缓动时间（秒）
         * @return Builder 实例
         */
        public Builder easeTime(float easeTime) {
            this.easeTime = easeTime;
            return this;
        }

        /**
         * 设置缓动类型
         *
         * @param easeType 缓动类型
         * @return Builder 实例
         */
        public Builder easeType(EaseType easeType) {
            this.easeType = easeType;
            return this;
        }

        /**
         * 设置为默认预设
         *
         * @param defaultPreset 是否为默认
         * @return Builder 实例
         */
        public Builder defaultPreset(boolean defaultPreset) {
            this.defaultPreset = defaultPreset;
            return this;
        }

        /**
         * 构建 CameraInstruction 实例
         *
         * @return CameraInstruction 实例
         */
        public CameraInstruction build() {
            return new CameraInstruction(this);
        }
    }

    /**
     * 创建清除镜头指令
     *
     * @return CameraInstruction 实例
     */
    public static CameraInstruction clear() {
        return new Builder().type(InstructionType.CLEAR).build();
    }

    /**
     * 创建设置预设镜头指令
     *
     * @param preset 预设配置
     * @return CameraInstruction 实例
     */
    public static CameraInstruction setPreset(CameraPreset preset) {
        return new Builder().preset(preset).build();
    }

    /**
     * 创建设置位置镜头指令
     *
     * @param position 镜头位置
     * @return CameraInstruction 实例
     */
    public static CameraInstruction setPosition(CameraPosition position) {
        return new Builder().position(position).build();
    }
}
