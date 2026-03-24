package cn.nukkitmot.exampleplugin.camera;

/**
 * 镜头模式枚举
 * <p>
 * 定义了不同类型的镜头控制模式，用于控制玩家视角的行为方式。
 *
 * @author QingTong
 * @version 1.0.0
 * @since 1.0.0
 */
public enum CameraMode {

    /**
     * 第一人称视角（默认）
     * <p>
     * 玩家正常游戏视角，无特殊镜头控制
     */
    FIRST_PERSON("minecraft:first_person"),

    /**
     * 第三人称背面视角
     * <p>
     * 从玩家背后观察的视角
     */
    THIRD_PERSON("minecraft:third_person"),

    /**
     * 第三人称正面视角
     * <p>
     * 从玩家正面观察的视角
     */
    THIRD_PERSON_FRONT("minecraft:third_person_front"),

    /**
     * 自由视角
     * <p>
     * 镜头可以自由移动，不受玩家位置限制
     */
    FREE("minecraft:free"),

    /**
     * 固定视角
     * <p>
     * 镜头固定在指定位置，不跟随玩家移动
     */
    FIXED("minecraft:fixed"),

    /**
     * 跟随视角
     * <p>
     * 镜头跟随玩家移动，保持相对位置
     */
    FOLLOW("minecraft:follow"),

    /**
     * 轨道视角
     * <p>
     * 镜头围绕目标点旋转
     */
    ORBIT("minecraft:orbit"),

    /**
     * 侧面视角（2D风格）
     * <p>
     * 从玩家侧面观察的固定视角
     */
    SIDE("minecraft:side");

    private final String identifier;

    CameraMode(String identifier) {
        this.identifier = identifier;
    }

    /**
     * 获取镜头模式的标识符
     *
     * @return Minecraft 标识符字符串
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * 根据标识符获取镜头模式
     *
     * @param identifier 标识符字符串
     * @return 对应的 CameraMode，如果未找到则返回 FIRST_PERSON
     */
    public static CameraMode fromIdentifier(String identifier) {
        for (CameraMode mode : values()) {
            if (mode.identifier.equals(identifier)) {
                return mode;
            }
        }
        return FIRST_PERSON;
    }
}
