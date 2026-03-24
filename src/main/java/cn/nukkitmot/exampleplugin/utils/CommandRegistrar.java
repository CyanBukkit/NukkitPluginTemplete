package cn.nukkitmot.exampleplugin.utils;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 命令注册工具类
 * <p>
 * 提供无需在 plugin.yml 中声明即可注册命令的功能。
 * 通过反射直接操作 SimpleCommandMap 实现命令注册。
 *
 * <h3>使用示例：</h3>
 * <pre>
 * // 方式1：直接注册命令实例
 * CommandRegistrar.registerCommand(this, new MyCommand());
 *
 * // 方式2：批量注册多个命令
 * CommandRegistrar.registerCommands(this,
 *     new Command1(),
 *     new Command2(),
 *     new Command3()
 * );
 *
 * // 方式3：使用构建器创建并注册命令
 * CommandRegistrar.CommandBuilder.create("mycommand")
 *     .description("我的命令")
 *     .usage("/mycommand [参数]")
 *     .aliases("mc", "mycmd")
 *     .permission("myplugin.command.mycommand")
 *     .executor((sender, commandLabel, args) -> {
 *         sender.sendMessage("命令执行成功!");
 *         return true;
 *     })
 *     .register(this);
 * </pre>
 *
 * @author QingTong
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommandRegistrar {

    private static final String FALLBACK_PREFIX = "dynamic";

    /**
     * 私有构造函数，防止实例化
     */
    private CommandRegistrar() {
        throw new UnsupportedOperationException("工具类不能实例化");
    }

    /**
     * 注册单个命令
     * <p>
     * 将命令注册到服务器的命令映射中，无需在 plugin.yml 中声明。
     *
     * @param plugin  插件实例
     * @param command 要注册的命令
     * @return 是否注册成功
     */
    public static boolean registerCommand(Plugin plugin, Command command) {
        return registerCommand(plugin, command, FALLBACK_PREFIX);
    }

    /**
     * 注册单个命令（指定前缀）
     * <p>
     * 将命令注册到服务器的命令映射中，可以自定义前缀。
     *
     * @param plugin         插件实例
     * @param command        要注册的命令
     * @param fallbackPrefix 回退前缀（用于命令冲突时）
     * @return 是否注册成功
     */
    public static boolean registerCommand(Plugin plugin, Command command, String fallbackPrefix) {
        try {
            CommandMap commandMap = getCommandMap();
            if (commandMap == null) {
                plugin.getLogger().error("无法获取 CommandMap，命令注册失败: " + command.getName());
                return false;
            }

            // 检查命令是否已存在
            if (commandMap.getCommand(command.getName()) != null) {
                plugin.getLogger().warning("命令 '" + command.getName() + "' 已存在，将被覆盖");
            }

            // 注册命令
            boolean success = commandMap.register(fallbackPrefix, command);

            if (success) {
                plugin.getLogger().info("成功注册命令: /" + command.getName());

                // 注册别名
                for (String alias : command.getAliases()) {
                    plugin.getLogger().debug("  注册别名: /" + alias);
                }
            } else {
                plugin.getLogger().warning("命令 '" + command.getName() + "' 注册可能未完全成功");
            }

            return success;
        } catch (Exception e) {
            plugin.getLogger().error("注册命令 '" + command.getName() + "' 时发生错误: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 批量注册多个命令
     * <p>
     * 一次性注册多个命令，提高注册效率。
     *
     * @param plugin   插件实例
     * @param commands 要注册的命令数组
     * @return 成功注册的命令数量
     */
    public static int registerCommands(Plugin plugin, Command... commands) {
        int successCount = 0;
        for (Command command : commands) {
            if (registerCommand(plugin, command)) {
                successCount++;
            }
        }
        plugin.getLogger().info("批量注册完成: " + successCount + "/" + commands.length + " 个命令注册成功");
        return successCount;
    }

    /**
     * 注销命令
     * <p>
     * 从服务器的命令映射中移除指定命令。
     *
     * @param plugin      插件实例
     * @param commandName 要注销的命令名称
     * @return 是否注销成功
     */
    public static boolean unregisterCommand(Plugin plugin, String commandName) {
        try {
            CommandMap commandMap = getCommandMap();
            if (commandMap == null) {
                return false;
            }

            Command command = commandMap.getCommand(commandName);
            if (command == null) {
                plugin.getLogger().warning("命令 '" + commandName + "' 不存在，无法注销");
                return false;
            }

            // 使用反射从 knownCommands 中移除
            if (unregisterFromKnownCommands(command)) {
                plugin.getLogger().info("成功注销命令: /" + commandName);
                return true;
            }

            return false;
        } catch (Exception e) {
            plugin.getLogger().error("注销命令 '" + commandName + "' 时发生错误: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取服务器的 CommandMap
     * <p>
     * 通过反射从 Server 实例中获取 SimpleCommandMap。
     *
     * @return CommandMap 实例，如果获取失败返回 null
     */
    public static CommandMap getCommandMap() {
        try {
            Server server = Server.getInstance();

            // 尝试直接调用 getCommandMap 方法（Nukkit-MOT 可能直接提供）
            try {
                Method method = server.getClass().getMethod("getCommandMap");
                return (CommandMap) method.invoke(server);
            } catch (NoSuchMethodException ignored) {
                // 方法不存在，继续尝试反射获取字段
            }

            // 通过反射获取 commandMap 字段
            Field field = getCommandMapField(server.getClass());
            if (field == null) {
                return null;
            }

            field.setAccessible(true);
            return (CommandMap) field.get(server);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 递归查找 commandMap 字段
     *
     * @param clazz 要查找的类
     * @return 字段对象，如果未找到返回 null
     */
    private static Field getCommandMapField(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return null;
        }

        try {
            return clazz.getDeclaredField("commandMap");
        } catch (NoSuchFieldException e) {
            // 在父类中查找
            return getCommandMapField(clazz.getSuperclass());
        }
    }

    /**
     * 从 knownCommands 中移除命令
     *
     * @param command 要移除的命令
     * @return 是否移除成功
     */
    @SuppressWarnings("unchecked")
    private static boolean unregisterFromKnownCommands(Command command) {
        try {
            CommandMap commandMap = getCommandMap();
            if (commandMap == null) {
                return false;
            }

            // 获取 knownCommands 字段
            Field knownCommandsField = null;
            Class<?> clazz = commandMap.getClass();

            while (clazz != null && clazz != Object.class) {
                try {
                    knownCommandsField = clazz.getDeclaredField("knownCommands");
                    break;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }

            if (knownCommandsField == null) {
                return false;
            }

            knownCommandsField.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            // 移除主命令
            knownCommands.remove(command.getName().toLowerCase());

            // 移除别名
            for (String alias : command.getAliases()) {
                knownCommands.remove(alias.toLowerCase());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查命令是否已注册
     *
     * @param commandName 命令名称
     * @return 是否已注册
     */
    public static boolean isCommandRegistered(String commandName) {
        CommandMap commandMap = getCommandMap();
        if (commandMap == null) {
            return false;
        }
        return commandMap.getCommand(commandName) != null;
    }

    /**
     * 获取已注册命令的数量
     *
     * @return 命令数量，如果获取失败返回 -1
     */
    @SuppressWarnings("unchecked")
    public static int getRegisteredCommandCount() {
        try {
            CommandMap commandMap = getCommandMap();
            if (commandMap == null) {
                return -1;
            }

            Field knownCommandsField = null;
            Class<?> clazz = commandMap.getClass();

            while (clazz != null && clazz != Object.class) {
                try {
                    knownCommandsField = clazz.getDeclaredField("knownCommands");
                    break;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }

            if (knownCommandsField == null) {
                return -1;
            }

            knownCommandsField.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
            return knownCommands.size();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // ==================== 命令构建器 ====================

    /**
     * 命令构建器
     * <p>
     * 提供流式 API 用于快速创建和注册简单命令。
     *
     * <h3>使用示例：</h3>
     * <pre>
     * CommandRegistrar.CommandBuilder.create("mycommand")
     *     .description("我的命令")
     *     .usage("/mycommand [参数]")
     *     .aliases("mc", "mycmd")
     *     .executor((sender, label, args) -> {
     *         sender.sendMessage("Hello!");
     *         return true;
     *     })
     *     .register(plugin);
     * </pre>
     */
    public static class CommandBuilder {
        private String name;
        private String description = "";
        private String usageMessage = "";
        private String[] aliases = new String[0];
        private String permission = null;
        private String permissionMessage = null;
        private CommandExecutor executor;

        /**
         * 私有构造函数
         *
         * @param name 命令名称
         */
        private CommandBuilder(String name) {
            this.name = name.toLowerCase();
        }

        /**
         * 创建新的命令构建器
         *
         * @param name 命令名称
         * @return CommandBuilder 实例
         */
        public static CommandBuilder create(String name) {
            return new CommandBuilder(name);
        }

        /**
         * 设置命令描述
         *
         * @param description 描述文本
         * @return 当前构建器（链式调用）
         */
        public CommandBuilder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * 设置命令用法
         *
         * @param usage 用法文本，如 "/command [参数]"
         * @return 当前构建器（链式调用）
         */
        public CommandBuilder usage(String usage) {
            this.usageMessage = usage;
            return this;
        }

        /**
         * 设置命令别名
         *
         * @param aliases 别名数组
         * @return 当前构建器（链式调用）
         */
        public CommandBuilder aliases(String... aliases) {
            this.aliases = aliases;
            return this;
        }

        /**
         * 设置权限节点
         *
         * @param permission 权限节点
         * @return 当前构建器（链式调用）
         */
        public CommandBuilder permission(String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * 设置权限不足时的提示消息
         *
         * @param message 提示消息
         * @return 当前构建器（链式调用）
         */
        public CommandBuilder permissionMessage(String message) {
            this.permissionMessage = message;
            return this;
        }

        /**
         * 设置命令执行器
         *
         * @param executor 执行器接口
         * @return 当前构建器（链式调用）
         */
        public CommandBuilder executor(CommandExecutor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * 构建命令对象
         *
         * @return 构建好的 Command 对象
         */
        public Command build() {
            DynamicCommand command = new DynamicCommand(
                    name, description, usageMessage, aliases, executor
            );

            if (permission != null) {
                command.setPermission(permission);
            }
            if (permissionMessage != null) {
                command.setPermissionMessage(permissionMessage);
            }

            return command;
        }

        /**
         * 构建并注册命令
         *
         * @param plugin 插件实例
         * @return 是否注册成功
         */
        public boolean register(Plugin plugin) {
            return CommandRegistrar.registerCommand(plugin, build());
        }
    }

    /**
     * 命令执行器接口
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * 执行命令
         *
         * @param sender       命令发送者
         * @param commandLabel 命令标签
         * @param args         命令参数
         * @return 是否执行成功
         */
        boolean execute(cn.nukkit.command.CommandSender sender, String commandLabel, String[] args);
    }

    /**
     * 动态命令类
     * <p>
     * 内部使用的命令实现，支持通过 lambda 设置执行逻辑。
     */
    private static class DynamicCommand extends Command {
        private final CommandExecutor executor;

        /**
         * 构造函数
         *
         * @param name         命令名称
         * @param description  命令描述
         * @param usageMessage 用法消息
         * @param aliases      别名数组
         * @param executor     执行器
         */
        public DynamicCommand(String name, String description, String usageMessage,
                              String[] aliases, CommandExecutor executor) {
            super(name, description, usageMessage, aliases);
            this.executor = executor;
        }

        @Override
        public boolean execute(cn.nukkit.command.CommandSender sender, String commandLabel, String[] args) {
            if (executor != null) {
                return executor.execute(sender, commandLabel, args);
            }
            return false;
        }
    }
}
