package cn.nukkitmot.exampleplugin;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.server.ServerCommandEvent;
import cn.nukkitmot.exampleplugin.scoreboard.IBoard;
import cn.nukkitmot.exampleplugin.scoreboard.example.ImplInReal;

/**
 * 事件监听器类
 * 
 * 展示了 Nukkit 事件监听器的完整实现方式。
 * 监听器用于捕获和处理游戏中的各种事件。
 * 
 * 事件处理特性：
 * <ul>
 *   <li>实现 Listener 接口 - 标记该类为事件监听器</li>
 *   <li>@EventHandler 注解 - 标记方法为事件处理方法</li>
 *   <li>EventPriority 优先级 - 控制事件处理顺序</li>
 *   <li>ignoreCancelled - 是否处理已取消的事件</li>
 * </ul>
 * 
 * 使用方法：
 * <pre>
 * // 在插件主类中注册监听器
 * this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
 * </pre>
 * 
 * @author MagicDroidX
 * @version 1.0.0
 */
public class EventListener implements Listener {
    /** 插件主类实例引用 */
    private final ExamplePlugin plugin;

    /**
     * 构造函数
     * 
     * @param plugin 插件主类实例
     */
    public EventListener(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 服务器命令事件处理
     * 
     * 当玩家或控制台执行命令时触发此事件
     * 
     * @param event 服务器命令事件对象
     */
    // 注意：不要忘记添加 @EventHandler 注解！
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onServerCommand(ServerCommandEvent event) {
        // 输出事件触发信息到服务器日志
        this.plugin.getLogger().info("ServerCommandEvent is called!");
        
        // 可以在此处添加更多事件处理逻辑
        // 例如：拦截特定命令、记录日志、修改命令等
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        IBoard board = ExamplePlugin.boardManager.getBoard(player);
        event.setJoinMessage(player.getName() + " 欢迎来到nukkit世界");
     }

}
