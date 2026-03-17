package cn.nukkitmot.exampleplugin.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindowSimple;

/**
 * 简单表单示例类
 * 
 * 展示了 Nukkit 简单表单（FormWindowSimple）的完整实现方式。
 * 简单表单用于显示带有多个按钮的列表界面。
 * 
 * 表单特性：
 * <ul>
 *   <li>多个按钮 - 可以添加任意数量的按钮选项</li>
 *   <li>简单直观 - 适合做菜单选择</li>
 *   <li>按钮点击处理 - 可获取点击的按钮索引和文本</li>
 * </ul>
 * 
 * 使用方法：
 * <pre>
 * // 在命令或事件中调用
 * DemoSimpleForm.open(player);
 * </pre>
 * 
 * @author NukkitMOT
 * @version 1.0.0
 */
public class DemoSimpleForm {
    /**
     * 打开简单表单
     * 
     * 向玩家显示一个带有多个按钮的简单表单
     * @param player 要显示表单的玩家
     */
    public static void open(Player player) {
        // 创建简单表单窗口
        // 参数说明：
        // 1. "Title" - 表单标题
        // 2. "Description" - 表单描述文本
        FormWindowSimple form = new FormWindowSimple("Title", "Description");
        
        // 添加按钮
        form.addButton(new ElementButton("Button 1"));
        form.addButton(new ElementButton("Button 2"));
        form.addButton(new ElementButton("Button 3"));
        
        // 设置按钮点击处理
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            // 检查表单是否被关闭
            if (form.wasClosed()) return;
            
            // 获取玩家点击的按钮索引
            int buttonIndex = form.getResponse().getClickedButtonId();
            
            // 获取玩家点击的按钮文本
            String buttonText = form.getResponse().getClickedButton().getText();
            
            // 根据按钮索引执行不同操作
            switch (buttonIndex) {
                case 0:
                    // 处理按钮1的点击事件
                    break;
                case 1:
                    // 处理按钮2的点击事件
                    break;
                case 2:
                    // 处理按钮3的点击事件
                    break;
                default:
                    // 默认处理
                    break;
            }
        }));
        
        // 向玩家显示表单
        player.showFormWindow(form);
    }
}