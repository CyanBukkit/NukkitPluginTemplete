package cn.nukkitmot.exampleplugin.camera;

import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

/**
 * 镜头位置数据类
 * <p>
 * 封装了镜头的位置、旋转和朝向信息，用于精确控制镜头。
 *
 * @author QingTong
 * @version 1.0.0
 * @since 1.0.0
 */
public class CameraPosition {

    private final Vector3 position;
    private final Vector3 rotation;
    private final Vector3 facing;
    private final boolean facingEntity;
    private final String entityId;

    /**
     * 创建固定位置镜头
     *
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     */
    public CameraPosition(double x, double y, double z) {
        this(new Vector3(x, y, z), new Vector3(0, 0, 0), null, false, null);
    }

    /**
     * 创建带旋转的固定位置镜头
     *
     * @param x       X坐标
     * @param y       Y坐标
     * @param z       Z坐标
     * @param pitch   俯仰角
     * @param yaw     偏航角
     * @param roll    翻滚角
     */
    public CameraPosition(double x, double y, double z, double pitch, double yaw, double roll) {
        this(new Vector3(x, y, z), new Vector3(pitch, yaw, roll), null, false, null);
    }

    /**
     * 创建看向指定位置的镜头
     *
     * @param position    镜头位置
     * @param lookAt      看向的位置
     */
    public CameraPosition(Vector3 position, Vector3 lookAt) {
        this(position, null, lookAt, false, null);
    }

    /**
     * 创建看向指定实体的镜头
     *
     * @param position    镜头位置
     * @param entityId    目标实体ID
     */
    public CameraPosition(Vector3 position, String entityId) {
        this(position, null, null, true, entityId);
    }

    /**
     * 完整构造函数
     *
     * @param position      镜头位置
     * @param rotation      镜头旋转
     * @param facing        朝向位置
     * @param facingEntity  是否朝向实体
     * @param entityId      目标实体ID
     */
    private CameraPosition(Vector3 position, Vector3 rotation, Vector3 facing,
                           boolean facingEntity, String entityId) {
        this.position = position;
        this.rotation = rotation != null ? rotation : new Vector3(0, 0, 0);
        this.facing = facing;
        this.facingEntity = facingEntity;
        this.entityId = entityId;
    }

    /**
     * 从 Position 创建 CameraPosition
     *
     * @param position Nukkit Position
     * @return CameraPosition 实例
     */
    public static CameraPosition fromPosition(Position position) {
        return new CameraPosition(position.x, position.y, position.z);
    }

    /**
     * 从预设和玩家位置创建 CameraPosition
     *
     * @param playerPos 玩家位置
     * @param preset    预设配置
     * @param distance  距离
     * @return CameraPosition 实例
     */
    public static CameraPosition fromPreset(Position playerPos, CameraPreset preset, double distance) {
        Vector3 offset = preset.getOffsetWithDistance(distance);
        Vector3 pos = new Vector3(
                playerPos.x + offset.x,
                playerPos.y + offset.y,
                playerPos.z + offset.z
        );
        Vector3 rotation = preset.getRotation();
        return new CameraPosition(pos, rotation, null, false, null);
    }

    /**
     * 创建相对玩家位置的镜头
     *
     * @param playerPos 玩家位置
     * @param offsetX   X轴偏移
     * @param offsetY   Y轴偏移
     * @param offsetZ   Z轴偏移
     * @return CameraPosition 实例
     */
    public static CameraPosition relative(Position playerPos, double offsetX, double offsetY, double offsetZ) {
        return new CameraPosition(
                playerPos.x + offsetX,
                playerPos.y + offsetY,
                playerPos.z + offsetZ
        );
    }

    /**
     * 获取镜头位置
     *
     * @return 位置向量
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * 获取镜头旋转
     *
     * @return 旋转向量（pitch, yaw, roll）
     */
    public Vector3 getRotation() {
        return rotation;
    }

    /**
     * 获取朝向位置
     *
     * @return 朝向位置，如果没有则返回 null
     */
    public Vector3 getFacing() {
        return facing;
    }

    /**
     * 是否朝向实体
     *
     * @return true 如果朝向实体
     */
    public boolean isFacingEntity() {
        return facingEntity;
    }

    /**
     * 获取目标实体ID
     *
     * @return 实体ID，如果没有则返回 null
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * 计算朝向指定位置的旋转角度
     *
     * @param target 目标位置
     * @return 旋转角度向量
     */
    public Vector3 calculateRotationTo(Vector3 target) {
        double dx = target.x - position.x;
        double dy = target.y - position.y;
        double dz = target.z - position.z;

        double distance = Math.sqrt(dx * dx + dz * dz);
        double pitch = Math.toDegrees(Math.atan2(dy, distance));
        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90;

        return new Vector3(pitch, yaw, 0);
    }

    @Override
    public String toString() {
        return String.format("CameraPosition[pos=%.2f,%.2f,%.2f rot=%.2f,%.2f,%.2f]",
                position.x, position.y, position.z,
                rotation.x, rotation.y, rotation.z);
    }
}
