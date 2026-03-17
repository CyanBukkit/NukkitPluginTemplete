package cn.nukkitmot.exampleplugin.custom.enchantment;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;
import cn.nukkit.utils.Identifier;

/**
 * 自动熔炼附魔类
 * 
 * 展示了 Nukkit 自定义附魔的完整实现方式。
 * 这是一个示例附魔，具有自动熔炼物品的能力。
 * 
 * 附魔属性：
 * <ul>
 *   <li>命名空间：nukkit</li>
 *   <li>附魔ID：auto_remelted</li>
 *   <li>最大等级：3</li>
 *   <li>稀有度：普通 (COMMON)</li>
 *   <li>适用类型：全部</li>
 * </ul>
 * 
 * 使用方法：
 * <pre>
 * // 获取附魔实例
 * Enchantment enchantment = Enchantment.getEnchantment(new Identifier("nukkit", "auto_remelted"));
 * 
 * // 设置附魔等级
 * Item item = Item.fromString("minecraft:diamond_pickaxe");
 * item.addEnchantment(enchantment.setLevel(2));
 * </pre>
 * 
 * @author NukkitMOT
 * @version 1.0.0
 */
public class AutoRemeltedEnchatment extends Enchantment {

    /**
     * 构造函数
     * 
     * 创建一个新的自动熔炼附魔实例
     */
    public AutoRemeltedEnchatment() {
        // 参数说明：
        // 1. new Identifier("nukkit", "auto_remelted") - 附魔的唯一标识符
        // 2. "autoRemelted" - 附魔的内部名称
        // 3. Rarity.COMMON - 附魔的稀有度
        // 4. EnchantmentType.ALL - 附魔适用的物品类型
        super(new Identifier("nukkit", "auto_remelted"), "autoRemelted", Rarity.COMMON, EnchantmentType.ALL);
    }

    /**
     * 获取附魔的最大等级
     * @return 最大等级为 3
     */
    @Override
    public int getMaxLevel() {
        return 3;
    }

    /**
     * 获取附魔的显示名称
     * @return 国际化后的附魔名称
     */
    @Override
    public String getName() {
        return "%enchantment.custom." + this.name;
    }
}
