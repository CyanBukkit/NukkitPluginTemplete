package cn.nukkitmot.exampleplugin.custom.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.custom.EntityDefinition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 标记实体类
 * 
 * 展示了 Nukkit 自定义实体的完整实现方式。
 * 这是一个简单的标记实体，可以用于显示数字索引。
 * 
 * 实体属性：
 * <ul>
 *   <li>标识符：example:marker</li>
 *   <li>尺寸：0.5 x 0.5 x 0.5</li>
 *   <li>生成方式：可使用刷怪蛋生成</li>
 * </ul>
 * 
 * 功能特性：
 * <ul>
 *   <li>可设置 MarkerIndex 用于存储索引值</li>
 *   <li>头顶显示索引数字</li>
 * </ul>
 * 
 * @author NukkitMOT
 * @version 1.0.0
 */
public class MarkerEntity extends Entity implements CustomEntity {
    /** 实体定义对象 */
    public static final EntityDefinition DEF =
            EntityDefinition
                    .builder()
                    // 实体唯一标识符，格式：命名空间:实体名
                    .identifier("example:marker")
                    // 是否可通过命令生成（暂不启用）
                    //.summonable(true)
                    // 是否生成刷怪蛋
                    .spawnEgg(true)
                    // 实体实现类
                    .implementation(MarkerEntity.class)
                    .build();
    
    /** 标记索引的 NBT 数据键名 */
    private static final String MARKER_INDEX_KEY = "MarkerIndex";

    /**
     * 构造函数
     * @param chunk 实体所在的区块
     * @param nbt 实体 NBT 数据
     */
    public MarkerEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    /**
     * 获取网络ID
     * @return 实体定义对应的运行时ID
     */
    @Override
    public int getNetworkId() {
        return this.getEntityDefinition().getRuntimeId();
    }

    /**
     * 获取实体定义
     * @return 当前实体的定义信息
     */
    @Override
    public EntityDefinition getEntityDefinition() {
        return DEF;
    }

    /**
     * 获取实体高度
     * @return 实体高度（0.5格）
     */
    @Override
    public float getHeight() {
        return 0.5F;
    }

    /**
     * 获取实体宽度
     * @return 实体宽度（0.5格）
     */
    @Override
    public float getWidth() {
        return 0.5F;
    }

    /**
     * 获取实体长度
     * @return 实体长度（0.5格）
     */
    @Override
    public float getLength() {
        return 0.5F;
    }

    /**
     * 获取实体原始名称
     * @return 实体显示名称
     */
    //@Override
    public String getOriginalName() {
        return "Marker";
    }

    /**
     * 获取标记索引
     * @return 从 NBT 中读取的索引值
     */
    public int getMarkerIndex() {
        return namedTag.getInt(MARKER_INDEX_KEY);
    }

    /**
     * 设置标记索引
     * 同时更新 NBT 数据和头顶显示的名称
     * @param index 要设置的索引值
     */
    public void setMarkerIndex(int index) {
        namedTag.putInt(MARKER_INDEX_KEY, index);
        // 设置头顶名称，使用绿色格式化代码
        setNameTag("§a" + index);
    }
}