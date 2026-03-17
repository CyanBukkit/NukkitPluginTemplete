package cn.nukkitmot.exampleplugin;

import cn.nukkit.scheduler.PluginTask;

/**
 * 广播插件任务类
 * 
 * 展示了 Nukkit 定时任务（PluginTask）的完整实现方式。
 * 这是一个示例定时任务，每隔一定tick数执行一次。
 * 
 * 任务特性：
 * <ul>
 *   <li>继承 PluginTask - 标准的插件定时任务</li>
 *   <li>泛型指定 - 绑定到 ExamplePlugin 插件实例</li>
 *   <li>定时执行 - 每个游戏tick都会检查是否执行</li>
 * </ul>
 * 
 * 使用方法：
 * <pre>
 * // 在插件主类中注册任务
 * this.getServer().getScheduler().scheduleRepeatingTask(new BroadcastPluginTask(this), 20);
 * // 第二个参数是间隔tick数，20tick ≈ 1秒
 * </pre>
 * 
 * @author MagicDroidX
 * @version 1.0.0
 */
public class BroadcastPluginTask extends PluginTask<ExamplePlugin> {

    /**
     * 构造函数
     * 
     * @param owner 插件主类实例
     */
    public BroadcastPluginTask(ExamplePlugin owner) {
        super(owner);
    }

    /**
     * 任务执行回调
     * 
     * 每当任务到达执行时间时调用此方法
     * @param currentTick 当前的游戏tick数
     */
    @Override
    public void onRun(int currentTick) {
        // 输出任务执行信息到服务器日志
        this.getOwner().getLogger().info("I've run on tick " + currentTick);
        
        // 可以在这里添加更多定时执行的逻辑
    }
}
