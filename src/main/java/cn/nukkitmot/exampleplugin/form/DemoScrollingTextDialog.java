package cn.nukkitmot.exampleplugin.form;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.form.window.FormWindowDialog;
import cn.nukkit.form.window.ScrollingTextDialog;

/**
 * 滚动文本对话框示例类
 * 
 * 展示了 Nukkit 滚动文本对话框（ScrollingTextDialog）的完整实现方式。
 * 滚动文本对话框用于显示较长的文本内容，支持滚动查看。
 * 
 * 表单特性：
 * <ul>
 *   <li>长文本显示 - 支持大量文本内容</li>
 *   <li>滚动浏览 - 玩家可以滚动查看完整内容</li>
 *   <li>可关联实体 - 可与实体交互结合使用</li>
 * </ul>
 * 
 * 使用方法：
 * <pre>
 * // 在玩家与实体交互时调用
 * DemoScrollingTextDialog.open(player, entity);
 * </pre>
 * 
 * @author NukkitMOT
 * @version 1.0.0
 */
public class DemoScrollingTextDialog {
    /**
     * 打开滚动文本对话框
     * 
     * 向玩家显示一个可滚动的长文本对话框
     * @param player 要显示对话框的玩家
     * @param entity 关联的实体对象
     */
    public static void open(Player player, Entity entity) {
        // 创建对话表单窗口
        // 参数说明：
        // 1. "Title" - 对话框标题
        // 2. "Description" - 对话框描述文本（可以是长文本）
        // 3. entity - 关联的实体
        FormWindowDialog dialog = new FormWindowDialog("Title", "Description", entity);
        
        // 添加选项按钮
        dialog.addButton("Option 1");
        dialog.addButton("Option 2");
        
        // 设置按钮点击处理
        dialog.addHandler((player_, response) -> {
            // 获取玩家点击的按钮文本
            String buttonText = response.getClickedButton().getName();
            
            // 在这里处理玩家选择的选项
        });
        
        // 创建滚动文本对话框
        // 参数说明：
        // 1. player - 目标玩家
        // 2. dialog - 基础对话框
        // 3. 1 - 滚动类型（通常是1）
        ScrollingTextDialog form = new ScrollingTextDialog(player, dialog, 1);
        
        // 向玩家发送滚动文本对话框
        form.send(player);
    }
}