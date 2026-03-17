package cn.nukkitmot.exampleplugin.form;

import cn.nukkit.Player;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindowModal;

/**
 * 模态表单示例类
 * 
 * 展示了 Nukkit 模态表单（FormWindowModal）的完整实现方式。
 * 模态表单显示一个带有两个按钮的确认对话框，玩家必须选择其一。
 * 
 * 表单特性：
 * <ul>
 *   <li>两个按钮选项 - 典型用法是"确认"和"取消"</li>
 *   <li>阻塞性 - 玩家必须做出选择才能继续操作</li>
 *   <li>简单直观 - 适用于需要玩家明确确认的场景</li>
 * </ul>
 * 
 * 使用方法：
 * <pre>
 * // 在命令或事件中调用
 * DemoModalForm.open(player);
 * </pre>
 * 
 * @author NukkitMOT
 * @version 1.0.0
 */
public class DemoModalForm {
    /**
     * 打开模态表单
     * 
     * 向玩家显示一个带有两个按钮的模态对话框
     * @param player 要显示表单的玩家
     */
    public static void open(Player player) {
        // 创建模态表单窗口
        // 参数说明：
        // 1. "Title" - 表单标题
        // 2. "Description" - 表单描述文本
        // 3. "true" - 第一个按钮文本（通常为确认/是）
        // 4. "false" - 第二个按钮文本（通常为取消/否）
        FormWindowModal form = new FormWindowModal("Title", "Description", "true", "false");
        
        // 设置按钮点击处理
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            // 检查表单是否被关闭
            if (form.wasClosed()) return;
            
            // 获取玩家点击的按钮ID
            // 0 = 第一个按钮 ("true")
            // 1 = 第二个按钮 ("false")
            if (form.getResponse().getClickedButtonId() == 0) {
                // 玩家点击了确认按钮
            } else {
                // 玩家点击了取消按钮
            }
        }));
        
        // 向玩家显示表单
        player.showFormWindow(form);
    }
}