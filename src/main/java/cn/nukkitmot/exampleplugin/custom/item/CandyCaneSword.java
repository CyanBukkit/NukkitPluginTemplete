package cn.nukkitmot.exampleplugin.custom.item;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;

/**
 * 糖果拐杖剑类
 * 
 * 展示了 Nukkit 自定义物品的完整实现方式。
 * 这是一把自定义剑类武器，具有独特的属性。
 * 
 * 物品属性：
 * <ul>
 *   <li>命名空间ID：nukkit:candy_cane_sword</li>
 *   <li>材质贴图：candy_cane_sword</li>
 *   <li>最大耐久度：500</li>
 *   <li>堆叠数量：1</li>
 *   <li>攻击伤害：4</li>
 *   <li>可装备副手：支持</li>
 *   <li>主手装备：是</li>
 * </ul>
 * 
 * @author NukkitMOT
 * @version 1.0.0
 */
public class CandyCaneSword extends ItemCustom {
    /** 命名空间ID */
    private static String spacenameId = "nukkit:candy_cane_sword";
    /** 材质贴图名称 */
    private static String textureName = "candy_cane_sword";
    /** 物品显示名称（null 使用默认名称） */
    private static String name = null;

    /**
     * 构造函数
     */
    public CandyCaneSword() {
        super(spacenameId, name, textureName);
    }

    /**
     * 获取物品在客户端显示的偏移量
     * @return 偏移量值
     */
    public int scaleOffset() {
        return 32;
    }

    /**
     * 获取物品定义
     * @return 自定义物品定义对象
     */
    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .simpleBuilder(this, null)
                // 允许装备在副手
                .allowOffHand(true)
                // 作为主手武器装备
                .handEquipped(true)
                .build();
    }

    /**
     * 获取物品最大耐久度
     * @return 最大耐久度值
     */
    @Override
    public int getMaxDurability() {
        return 500;
    }

    /**
     * 获取物品最大堆叠数量
     * @return 最大堆叠数量（剑类为1）
     */
    @Override
    public int getMaxStackSize() {
        return 1;
    }

    /**
     * 获取物品攻击伤害
     * @return 基础攻击伤害值
     */
    @Override
    public int getAttackDamage() {
        return 4;
    }

    /**
     * 判断是否为剑类物品
     * @return true 是剑类物品
     */
    @Override
    public boolean isSword() {
        return true;
    }
}