package cn.nukkitmot.exampleplugin.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindowCustom;

import java.util.Arrays;

/**
 * 自定义表单示例类
 * 
 * 展示了 Nukkit 自定义表单（FormWindowCustom）的完整实现方式。
 * 自定义表单允许玩家输入文本和选择下拉选项。
 * 
 * 表单元素：
 * <ul>
 *   <li>文本输入框 - 用于获取玩家输入的文本</li>
 *   <li>下拉菜单 - 用于让玩家从多个选项中选择</li>
 * </ul>
 * 
 * 使用方法：
 * <pre>
 * // 在命令或事件中调用
 * DemoCustomForm.open(player);
 * </pre>
 * 
 * @author NukkitMOT
 * @version 1.0.0
 */
public class DemoCustomForm {
    /**
     * 打开自定义表单
     * 
     * 向玩家显示一个包含文本输入和下拉菜单的表单
     * @param player 要显示表单的玩家
     */
    public static void open(Player player) {
        // 创建自定义表单窗口
        FormWindowCustom form = new FormWindowCustom("Title");
        
        // 添加表单元素 - 文本输入框
        // 参数说明：
        // 1. "Text Input" - 输入框标题
        // 2. "Default Value" - 默认值
        form.addElement(new ElementInput("Text Input", "Default Value"));
        
        // 添加表单元素 - 下拉菜单
        // 参数说明：
        // 1. "Dropdown" - 下拉菜单标题
        // 2. 选项列表
        form.addElement(new ElementDropdown("Dropdown", Arrays.asList("Option 1", "Option 2", "Option 3")));
        
        // 设置表单提交处理
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            // 检查表单是否被关闭
            if (form.wasClosed()) return;
            
            // 获取文本输入框的值（索引0）
            String inputText = form.getResponse().getInputResponse(0);
            
            // 获取下拉菜单的选择（索引1）
            int selectedIndex = form.getResponse().getDropdownResponse(1).getElementID();  // 获取选中项索引
            String selectedText = form.getResponse().getDropdownResponse(1).getElementContent();  // 获取选中项文本
            
            // 在这里处理玩家提交的数据
        }));
        
        // 向玩家显示表单
        player.showFormWindow(form);
    }
}