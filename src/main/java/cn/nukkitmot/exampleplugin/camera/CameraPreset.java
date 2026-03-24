package cn.nukkitmot.exampleplugin.camera;

import cn.nukkit.math.Vector3;

/**
 * 预设镜头配置枚举
 * <p>
 * 提供了常用的预设镜头位置，方便快速设置2D视角等常见镜头模式。
 *
 * @author QingTong
 * @version 1.0.0
 * @since 1.0.0
 */
public enum CameraPreset {

    /**
     * 默认视角
     * <p>
     * 使用游戏默认视角，无特殊设置
     */
    DEFAULT("default", new Vector3(0, 0, 0), new Vector3(0, 0, 0)),

    /**
     * 左侧2D视角
     * <p>
     * 从玩家左侧观察，适合横版2D风格游戏
     */
    SIDE_LEFT("side_left", new Vector3(-5, 2, 0), new Vector3(0, 0, 0)),

    /**
     * 右侧2D视角
     * <p>
     * 从玩家右侧观察，适合横版2D风格游戏
     */
    SIDE_RIGHT("side_right", new Vector3(5, 2, 0), new Vector3(0, 0, 0)),

    /**
     * 顶部俯视视角
     * <p>
     * 从正上方俯视玩家
     */
    TOP_DOWN("top_down", new Vector3(0, 10, 0), new Vector3(90, 0, 0)),

    /**
     * 等距视角
     * <p>
     * 经典的45度等距视角
     */
    ISOMETRIC("isometric", new Vector3(-8, 8, -8), new Vector3(45, 45, 0)),

    /**
     * 过肩视角
     * <p>
     * 从玩家肩膀后方观察
     */
    OVER_SHOULDER("over_shoulder", new Vector3(0, 1.5, -3), new Vector3(10, 0, 0)),

    /**
     * 正面特写
     * <p>
     * 从正面近距离观察玩家
     */
    FRONT_CLOSE("front_close", new Vector3(0, 1.5, 3), new Vector3(10, 180, 0)),

    /**
     * 全景视角
     * <p>
     * 从远处观察玩家和周围环境
     */
    PANORAMIC("panoramic", new Vector3(0, 5, -10), new Vector3(20, 0, 0)),

    /**
     * 低角度仰视
     * <p>
     * 从低处向上观察，营造宏伟感
     */
    LOW_ANGLE("low_angle", new Vector3(0, 0.5, -4), new Vector3(-15, 0, 0)),

    /**
     * 高角度俯视
     * <p>
     * 从高处向下观察
     */
    HIGH_ANGLE("high_angle", new Vector3(0, 8, -6), new Vector3(35, 0, 0));

    private final String name;
    private final Vector3 offset;
    private final Vector3 rotation;

    CameraPreset(String name, Vector3 offset, Vector3 rotation) {
        this.name = name;
        this.offset = offset;
        this.rotation = rotation;
    }

    /**
     * 获取预设名称
     *
     * @return 预设名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取相对偏移量
     * <p>
     * 相对于玩家位置的偏移向量
     *
     * @return 偏移向量
     */
    public Vector3 getOffset() {
        return offset;
    }

    /**
     * 获取旋转角度
     * <p>
     * 镜头的旋转角度（pitch, yaw, roll）
     *
     * @return 旋转角度向量
     */
    public Vector3 getRotation() {
        return rotation;
    }

    /**
     * 根据名称获取预设
     *
     * @param name 预设名称
     * @return 对应的 CameraPreset，如果未找到则返回 DEFAULT
     */
    public static CameraPreset fromName(String name) {
        for (CameraPreset preset : values()) {
            if (preset.name.equalsIgnoreCase(name)) {
                return preset;
            }
        }
        return DEFAULT;
    }

    /**
     * 创建带距离的预设偏移
     * <p>
     * 根据预设方向创建指定距离的偏移量
     *
     * @param distance 距离玩家的距离
     * @return 新的偏移向量
     */
    public Vector3 getOffsetWithDistance(double distance) {
        Vector3 normalized = offset.normalize();
        if (normalized.length() == 0) {
            return new Vector3(0, distance, -distance);
        }
        return normalized.multiply(distance);
    }
}
