package cn.nukkitmot.exampleplugin.utils;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;
import cn.nukkitmot.exampleplugin.ExamplePlugin;

/**
 * 动态命令注册示例类
 * <p>
 * 展示如何使用 CommandRegistrar 工具类在无需 plugin.yml 声明的情况下注册命令。
 *
 * <h3>使用方法：</h3>
 * <p>在插件主类的 onEnable() 方法中调用：</p>
 * <pre>
 * DynamicCommandExample.registerAll(this);
 * </pre>
 *
 * @author QingTong
 * @version 1.0.0
 * @since 1.0.0
 */
public class DynamicCommandExample {

    /**
     * 注册所有示例命令
     * <p>
     * 在插件启用时调用此方法注册所有动态命令。
     *
     * @param plugin 插件实例
     */
    public static void registerAll(Plugin plugin) {
        // 方式1: 使用命令构建器快速创建简单命令
        registerWithBuilder(plugin);

        // 方式2: 注册自定义 Command 类
        registerCustomCommand(plugin);

        // 方式3: 批量注册多个命令
        registerBatchCommands(plugin);

        plugin.getLogger().info("动态命令注册完成！");
    }

    /**
     * 方式1: 使用命令构建器
     * <p>
     * 适合快速创建简单的命令，无需创建单独的类。
     */
    private static void registerWithBuilder(Plugin plugin) {
        // 注册 /hello 命令
        boolean success1 = CommandRegistrar.CommandBuilder.create("hello")
                .description("打招呼命令")
                .usage("/hello [玩家名]")
                .aliases("hi", "hey")
                .permission("exampleplugin.command.hello")
                .permissionMessage("§c你没有权限使用此命令！")
                .executor((sender, label, args) -> {
                    if (args.length == 0) {
                        // 给自己打招呼
                        sender.sendMessage("§a你好, " + sender.getName() + "!");
                    } else {
                        // 给指定玩家打招呼
                        Player target = plugin.getServer().getPlayerExact(args[0]);
                        if (target != null && target.isOnline()) {
                            target.sendMessage("§e" + sender.getName() + " 向你打招呼！");
                            sender.sendMessage("§a你向 " + target.getName() + " 打招呼了！");
                        } else {
                            sender.sendMessage("§c玩家 " + args[0] + " 不在线！");
                        }
                    }
                    return true;
                })
                .register(plugin);

        // 注册 /heal 命令 - 治疗命令
        boolean success2 = CommandRegistrar.CommandBuilder.create("heal")
                .description("治疗自己或指定玩家")
                .usage("/heal [玩家名]")
                .aliases("h", "恢复")
                .permission("exampleplugin.command.heal")
                .executor((sender, label, args) -> {
                    Player target;
                    if (args.length == 0) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage("§c控制台必须指定玩家名！");
                            return false;
                        }
                        target = (Player) sender;
                    } else {
                        target = plugin.getServer().getPlayerExact(args[0]);
                        if (target == null || !target.isOnline()) {
                            sender.sendMessage("§c玩家 " + args[0] + " 不在线！");
                            return false;
                        }
                    }

                    target.setHealth(target.getMaxHealth());
                    target.sendMessage("§a你被治愈了！生命值已恢复满！");
                    if (target != sender) {
                        sender.sendMessage("§a你治愈了 " + target.getName());
                    }
                    return true;
                })
                .register(plugin);

        // 注册 /day 命令 - 设置时间为白天
        CommandRegistrar.CommandBuilder.create("day")
                .description("将时间设置为白天")
                .usage("/day")
                .permission("exampleplugin.command.day")
                .executor((sender, label, args) -> {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        player.getLevel().setTime(0);
                        player.sendMessage("§e时间已设置为白天！");
                    } else {
                        // 控制台执行，设置所有世界
                        plugin.getServer().getLevels().values().forEach(level -> level.setTime(0));
                        sender.sendMessage("§e所有世界的时间已设置为白天！");
                    }
                    return true;
                })
                .register(plugin);

        plugin.getLogger().info("已注册 " + (success1 && success2 ? 3 : 0) + " 个构建器命令");
    }

    /**
     * 方式2: 注册自定义 Command 类
     * <p>
     * 适合复杂的命令逻辑，需要单独创建命令类。
     */
    private static void registerCustomCommand(Plugin plugin) {
        // 注册 /fly 命令
        Command flyCommand = new Command("fly", "切换飞行模式", "/fly [玩家名]", new String[]{"飞行"}) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                if (!sender.hasPermission("exampleplugin.command.fly")) {
                    sender.sendMessage("§c你没有权限使用此命令！");
                    return false;
                }

                Player target;
                if (args.length == 0) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§c控制台必须指定玩家名！");
                        return false;
                    }
                    target = (Player) sender;
                } else {
                    target = plugin.getServer().getPlayerExact(args[0]);
                    if (target == null || !target.isOnline()) {
                        sender.sendMessage("§c玩家 " + args[0] + " 不在线！");
                        return false;
                    }
                }

                boolean canFly = !target.getAllowFlight();
                target.setAllowFlight(canFly);

                String status = canFly ? "§a开启" : "§c关闭";
                target.sendMessage("§e飞行模式已" + status + "！");
                if (target != sender) {
                    sender.sendMessage("§e你" + (canFly ? "开启" : "关闭") + "了 " + target.getName() + " 的飞行模式");
                }
                return true;
            }
        };
        flyCommand.setPermission("exampleplugin.command.fly");

        boolean success = CommandRegistrar.registerCommand(plugin, flyCommand);
        plugin.getLogger().info("自定义命令 /fly 注册" + (success ? "成功" : "失败"));
    }

    /**
     * 方式3: 批量注册多个命令
     * <p>
     * 适合一次性注册多个相关命令。
     */
    private static void registerBatchCommands(Plugin plugin) {
        // 创建多个命令
        Command gm0Command = new Command("gm0", "切换到生存模式", "/gm0 [玩家名]", new String[]{"gms", "survival"}) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return changeGameMode(plugin, sender, args, 0, "生存模式");
            }
        };
        gm0Command.setPermission("exampleplugin.command.gamemode");

        Command gm1Command = new Command("gm1", "切换到创造模式", "/gm1 [玩家名]", new String[]{"gmc", "creative"}) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return changeGameMode(plugin, sender, args, 1, "创造模式");
            }
        };
        gm1Command.setPermission("exampleplugin.command.gamemode");

        Command gm2Command = new Command("gm2", "切换到冒险模式", "/gm2 [玩家名]", new String[]{"gma", "adventure"}) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return changeGameMode(plugin, sender, args, 2, "冒险模式");
            }
        };
        gm2Command.setPermission("exampleplugin.command.gamemode");

        // 批量注册
        int count = CommandRegistrar.registerCommands(plugin, gm0Command, gm1Command, gm2Command);
        plugin.getLogger().info("批量注册了 " + count + " 个游戏模式命令");
    }

    /**
     * 切换游戏模式的辅助方法
     */
    private static boolean changeGameMode(Plugin plugin, CommandSender sender, String[] args, 
                                          int gameMode, String modeName) {
        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c控制台必须指定玩家名！");
                return false;
            }
            target = (Player) sender;
        } else {
            target = plugin.getServer().getPlayerExact(args[0]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage("§c玩家 " + args[0] + " 不在线！");
                return false;
            }
        }

        target.setGamemode(gameMode);
        target.sendMessage("§a你的游戏模式已切换为 " + modeName);
        if (target != sender) {
            sender.sendMessage("§a你将 " + target.getName() + " 的游戏模式切换为 " + modeName);
        }
        return true;
    }

    /**
     * 注销所有动态注册的命令
     * <p>
     * 在插件禁用时调用，清理注册的命令。
     *
     * @param plugin 插件实例
     */
    public static void unregisterAll(Plugin plugin) {
        String[] commands = {"hello", "heal", "day", "fly", "gm0", "gm1", "gm2"};
        int count = 0;
        for (String cmd : commands) {
            if (CommandRegistrar.unregisterCommand(plugin, cmd)) {
                count++;
            }
        }
        plugin.getLogger().info("已注销 " + count + " 个动态命令");
    }
}
