package cn.nukkitmot.exampleplugin.form;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.form.window.FormWindowDialog;

/**
 * 对话表单示例类
 * 
 * 展示了 Nukkit 对话表单（FormWindowDialog）的完整实现方式。
 * 对话表单用于与实体交互，显示带有选项的对话框。
 * 
 * 表单特性：
 * <ul>
 *   <li>可关联实体 - 表单会显示在特定实体旁</li>
 *   <li>多个选项按钮 - 玩家点击后触发对应事件</li>
 *   <li>回调处理 - 可获取玩家点击的按钮文本</li>
 * </ul>
 * 
 * 使用方法：
 * <pre>
 * // 在玩家与实体交互时调用
 * DemoDialogForm.open(player, entity);
 * </pre>
 * 
 * @author NukkitMOT
 * @version 1.0.0
 */
public class DemoDialogForm {
    /**
     * 打开对话表单
     * 
     * 向玩家显示一个与实体关联的对话表单
     * @param player 要显示表单的玩家
     * @param entity 关联的实体对象
     */
    public static void open(Player player, Entity entity) {
        // 创建对话表单窗口
        // 参数说明：
        // 1. "Title" - 对话框标题
        // 2. "Description" - 对话框描述文本
        // 3. entity - 关联的实体（玩家点击按钮时可通过实体执行操作）
        FormWindowDialog form = new FormWindowDialog("Title", "Description", entity);
        
        // 添加选项按钮
        form.addButton("Option 1");
        form.addButton("Option 2");
        
        // 设置按钮点击处理
        form.addHandler((player_, response) -> {
            // 获取玩家点击的按钮文本
            String buttonText = response.getClickedButton().getName();
            
            // 在这里处理玩家选择的选项
        });
        
        // 向玩家发送对话框
        form.send(player);
    }
}