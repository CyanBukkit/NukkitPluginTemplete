package cn.nukkitmot.exampleplugin.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.LangCode;
import cn.nukkit.lang.PluginI18n;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.TextFormat;
import cn.nukkitmot.exampleplugin.ExamplePlugin;
import cn.nukkitmot.exampleplugin.scoreboard.ScoreBoardAPI;

/**
 * 示例命令类
 * 
 * 展示了 Nukkit 插件命令的完整实现方式，包括：
 * <ul>
 *   <li>命令参数定义</li>
 *   <li>多语言支持</li>
 *   <li>子命令模式</li>
 *   <li>玩家交互处理</li>
 * </ul>
 * 
 * 使用方法：
 * <ul>
 *   <li>/examplecommand say1 或 /examplecommand sayone - 给发送者一把附魔镐</li>
 *   <li>/examplecommand say2 &lt;玩家名&gt; [消息] - 向指定玩家发送消息</li>
 * </ul>
 * 
 * @author NukkitMOT
 * @version 1.0.0
 */
public class ExampleCommand extends PluginCommand<ExamplePlugin> {

    /** 插件主类实例引用 */
    protected ExamplePlugin api = ExamplePlugin.getInstance();
    /** 国际化文本处理对象 */
    protected PluginI18n i18n = ExamplePlugin.getI18n();

    /**
     * 构造函数
     * 
     * 初始化命令的基本配置，包括：
     * <ul>
     *   <li>命令名称（小写）</li>
     *   <li>命令描述（通过语言文件键值实现多语言）</li>
     *   <li>命令别名</li>
     *   <li>命令参数定义</li>
     * </ul>
     * 
     * 注意：
     * <ul>
     *   <li>命令名称必须为小写</li>
     *   <li>描述使用语言文件键值，可实现不同语言玩家的差异化描述</li>
     *   <li>必须继承 PluginCommand 才能使用多语言描述功能</li>
     * </ul>
     */
    public ExampleCommand() {
        /*
        1.the name of the command must be lowercase
        2.Here the description is set in with the key in the language file,Look at en_US.lang or zh_CN.lang.
        This can send different command description to players of different language.
        You must extends PluginCommand to have this feature.
        */
        super("examplecommand", ExamplePlugin.getInstance());

        this.setDescription("%exampleplugin.examplecommand.description");
        //设置命令别名
        this.setAliases(new String[]{"test"});

        /*
         * 开始设置命令参数，首先需要清空默认参数
         * 因为 Nukkit 会自动填充一些默认参数，我们不需要这些
         * */
        this.getCommandParameters().clear();

        /*
         * 命令参数配置说明：
         * 1. getCommandParameters() 返回 Map<String, CommandParameter[]>
         *    每个条目代表一个子命令或命令模式
         * 2. 每个子命令名称不能重复
         * 3. 可选参数必须放在子命令的最后位置，或连续使用
         * */
         
        // 模式1：定义一个枚举类型参数
        this.getCommandParameters().put("pattern1", new CommandParameter[]{
                // 创建枚举参数 "enum1"，第三个参数为可选(false)
                // 枚举值：say1 和 sayone
                CommandParameter.newEnum("enum1", false, new CommandEnum("method", "say1", "sayone")),
        });
        
        // 模式2：定义多个参数，包含枚举、目标玩家和消息
        this.getCommandParameters().put("pattern2", new CommandParameter[]{
                CommandParameter.newEnum("enum2", false, new String[]{"say2"}),  // 枚举参数
                CommandParameter.newType("player", false, CommandParamType.TARGET),  // 玩家目标参数，必填
                CommandParameter.newType("message", true, CommandParamType.STRING)  // 字符串消息，可选
        });
    }

    /**
     * 命令执行入口
     * 
     * 当命令语法正确时才会执行此方法，因此无需自行验证参数格式。
     * 在执行命令前，系统会自动检查执行者是否拥有对应权限。
     * 如果权限不足或参数格式错误，系统会自动显示错误信息。
     *
     * @param sender       命令发送者（可能是玩家或控制台）
     * @param commandLabel 命令标签，例如使用 /test 123 时，值为 "test"
     * @param args         命令参数数组
     * @return 命令执行是否成功
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // 获取发送者的语言代码，非玩家则使用服务器默认语言
        LangCode langCode = sender.isPlayer() ? ((Player) sender).getLanguageCode() : ExamplePlugin.getServerLangCode();
        
        // 参数数量少于1时，显示用法提示
        if (args.length < 1) {
            sender.sendMessage(i18n.tr(langCode, "exampleplugin.args.missing"));
            return false;
        }
        
        // 根据第一个参数分发到不同的子命令处理
        switch (args[0]) {
            // 子命令1：say1 或 sayone - 给玩家一把附魔镐
            case "say1", "sayone" -> {
                api.getLogger().info("execute say1");
                // 检查发送者是否为玩家
                if (sender instanceof Player player) {
                    // 创建石镐
                    Item pickaxe = Item.fromString("minecraft:stone_pickaxe");
                    // 获取附魔属性（自动熔炼附魔，等级2）
                    Enchantment enchantment = Enchantment.getEnchantment(new Identifier("nukkit", "auto_remelted")).setLevel(2);
                    // 为物品添加附魔
                    pickaxe.addEnchantment(enchantment);
                    // 将物品添加到玩家背包
                    player.getInventory().addItem(pickaxe);

                }
            }
            // 子命令2：say2 - 向指定玩家发送消息
            case "say2" -> {
                api.getLogger().info("execute say2");
                // 检查是否提供了玩家参数
                if (args.length < 2) {
                    sender.sendMessage(i18n.tr(langCode, "exampleplugin.args.missing"));
                    return false;
                }
                // 获取目标玩家对象
                Player player = Server.getInstance().getPlayer(args[1]);
                // 获取目标玩家的语言代码
                LangCode playerLangCode = player.getLanguageCode();
                
                // 检查是否提供了消息参数
                if (args.length > 2) {
                    // 有消息参数，发送带消息的内容
                    String message = args[2];
                    player.sendMessage(TextFormat.WHITE + i18n.tr(playerLangCode, "exampleplugin.helloworld", player.getName()) + " this is message:" + message);
                } else {
                    // 无消息参数，只发送欢迎内容
                    player.sendMessage(TextFormat.WHITE + i18n.tr(playerLangCode, "exampleplugin.helloworld", player.getName()));
                }
                return true;
            }
            // 未匹配的子命令
            default -> {
                return false;
            }
        }
        return true;
    }
}