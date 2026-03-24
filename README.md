# Nukkit-MOT 插件开发模板

这是一个完整的 Nukkit-MOT 插件开发模板，展示了各种常用功能的实现方式。

## 项目概述

- **目标服务端**: Nukkit MOT (Minecraft Bedrock Edition 服务端)
- **开发语言**: Java / Kotlin
- **构建工具**: Gradle
- **最低支持版本**: 1.0.0+

## 目录结构

```
src/main/java/cn/nukkitmot/exampleplugin/
├── camera/          # 镜头控制 API
├── command/         # 命令系统
├── config/          # 配置文件处理
├── custom/          # 自定义内容
│   ├── enchantment/ # 自定义附魔
│   ├── entity/      # 自定义实体
│   └── item/        # 自定义物品
├── form/            # 表单系统
├── loader/          # 库加载器
├── nbs/             # NoteBlockSound 音乐系统
├── scoreboard/      # 计分板系统
├── BroadcastPluginTask.java  # 定时任务示例
├── EventListener.java        # 事件监听示例
└── ExamplePlugin.java        # 插件主类
```

---

## 1. 镜头控制 API (camera)

提供玩家镜头控制的完整功能，支持固定镜头、跟随镜头、预设镜头和运镜序列。

### 1.1 核心类

| 类名 | 功能 |
|------|------|
| [CameraManager](src/main/java/cn/nukkitmot/exampleplugin/camera/CameraManager.java) | 镜头管理器，提供所有镜头控制 API |
| [CameraMode](src/main/java/cn/nukkitmot/exampleplugin/camera/CameraMode.java) | 镜头模式枚举 |
| [CameraPreset](src/main/java/cn/nukkitmot/exampleplugin/camera/CameraPreset.java) | 预设镜头配置 |
| [CameraPosition](src/main/java/cn/nukkitmot/exampleplugin/camera/CameraPosition.java) | 镜头位置数据 |
| [CameraSequence](src/main/java/cn/nukkitmot/exampleplugin/camera/CameraSequence.java) | 运镜序列（多关键帧动画） |
| [CameraInstruction](src/main/java/cn/nukkitmot/exampleplugin/camera/CameraInstruction.java) | 镜头指令封装 |

### 1.2 初始化

```java
// 在插件 onEnable() 中初始化
CameraManager.init(this);

// 在插件 onDisable() 中清理
CameraManager.getInstance().clearAllCameras();
```

### 1.3 基础用法

```java
CameraManager camera = CameraManager.getInstance();

// 1. 固定镜头 - 设置指定位置
// 参数: 玩家, X, Y, Z, 缓动时间(秒)
camera.setFixedCamera(player, 100, 50, 100, 2.0f);

// 带旋转角度的固定镜头
// 参数: 玩家, X, Y, Z, 俯仰角, 偏航角, 缓动时间
camera.setFixedCamera(player, 100, 50, 100, 30, 45, 2.0f);

// 2. 跟随镜头 - 玩家走到哪里镜头跟到哪里
// 参数: 玩家, X偏移, Y偏移, Z偏移, 缓动时间
// 示例: 在玩家后方5格、上方2格处跟随
camera.setFollowCamera(player, 0, 2, -5, 1.5f);

// 3. 关闭镜头 - 恢复正常视角
camera.clearCamera(player);

// 清除所有玩家的镜头
camera.clearAllCameras();
```

### 1.4 预设镜头

```java
// 4. 2D侧面视角 - 适合横版游戏风格
// 左侧视角，距离8格
camera.set2DCamera(player, CameraManager.CameraSide.LEFT, 8.0, 1.5f);

// 右侧视角，距离8格
camera.set2DCamera(player, CameraManager.CameraSide.RIGHT, 8.0, 1.5f);

// 其他预设镜头
// 俯视视角
camera.setTopDownCamera(player, 15.0, 1.0f);

// 等距视角
camera.setIsometricCamera(player, 10.0, 1.0f);

// 使用预设枚举
camera.setPresetCamera(player, CameraPreset.FIRST_PERSON, 0, 1.0f);
camera.setPresetCamera(player, CameraPreset.THIRD_PERSON, 5, 1.0f);
camera.setPresetCamera(player, CameraPreset.FREE, 0, 1.0f);
```

### 1.5 运镜序列（关键帧动画）

```java
// 5. 运镜序列 - 创建复杂的镜头动画
CameraSequence sequence = camera.createSequence(player)
    // 添加第一帧: 位置 + 持续时间(tick) + 缓动类型
    .addFrame(new CameraPosition(100, 50, 100), 40, CameraInstruction.EaseType.IN_OUT_SINE)
    // 添加第二帧
    .addFrame(new CameraPosition(120, 60, 120), 40)
    // 添加第三帧
    .addFrame(new CameraPosition(140, 50, 100), 40);

// 播放序列
sequence.play();

// 暂停
sequence.pause();

// 停止
sequence.stop();

// 设置循环播放
sequence.setLoop(true);

// 设置完成回调
sequence.onComplete(() -> {
    player.sendMessage("运镜完成!");
});
```

### 1.6 环绕运镜

```java
// 创建环绕玩家的圆形运镜
CameraSequence orbitSequence = camera.createSequence(player)
    .addOrbitFrames(
        10.0,    // 环绕半径
        5.0,     // 高度
        0,       // 起始角度(度)
        360,     // 结束角度(度)
        100,     // 总持续时间(tick)
        20       // 帧数
    )
    .setLoop(false);

orbitSequence.play();
```

### 1.7 缓动类型

支持的缓动类型：
- `LINEAR` - 线性匀速
- `IN_OUT_SINE` - 正弦缓入缓出（推荐）
- `IN_OUT_QUAD` - 二次缓入缓出
- `IN_OUT_CUBIC` - 三次缓入缓出
- `IN_OUT_QUART` - 四次缓入缓出
- `IN_OUT_QUINT` - 五次缓入缓出
- `IN_OUT_EXPO` - 指数缓入缓出
- `IN_OUT_CIRC` - 圆形缓入缓出
- `IN_OUT_BACK` - 回弹缓入缓出
- `IN_OUT_ELASTIC` - 弹性缓入缓出
- `IN_OUT_BOUNCE` - 弹跳缓入缓出
- `SPRING` - 弹簧效果

### 1.8 状态查询

```java
// 检查玩家是否有活动镜头
boolean hasCamera = camera.hasActiveCamera(player);

// 获取玩家当前镜头状态
CameraManager.CameraState state = camera.getPlayerCameraState(player);

// 获取当前镜头模式
CameraMode mode = state.getMode();

// 获取当前镜头位置
CameraPosition position = state.getPosition();

// 检查是否是相对位置（跟随模式）
boolean isRelative = state.isRelative();
```

---

## 2. 命令系统 (command)

展示 Nukkit 插件命令的完整实现方式。

### 2.1 核心类

| 类名 | 功能 |
|------|------|
| [ExampleCommand](src/main/java/cn/nukkitmot/exampleplugin/command/ExampleCommand.java) | 示例命令类 |

### 2.2 命令注册

```java
// 在 plugin.yml 中注册命令
commands:
  examplecommand:
    description: "示例命令"
    usage: "/examplecommand <参数>"
    aliases: ["test"]
    permission: exampleplugin.command.example

// 在主类中初始化
@Override
public void onEnable() {
    this.getServer().getCommandMap().register("exampleplugin", new ExampleCommand());
}
```

### 2.3 命令参数定义

```java
public class ExampleCommand extends PluginCommand<ExamplePlugin> {
    
    public ExampleCommand() {
        super("examplecommand", ExamplePlugin.getInstance());
        
        // 设置多语言描述
        this.setDescription("%exampleplugin.examplecommand.description");
        this.setAliases(new String[]{"test"});
        
        // 清空默认参数
        this.getCommandParameters().clear();
        
        // 模式1: 枚举参数
        this.getCommandParameters().put("pattern1", new CommandParameter[]{
            CommandParameter.newEnum("enum1", false, 
                new CommandEnum("method", "say1", "sayone")),
        });
        
        // 模式2: 多参数组合
        this.getCommandParameters().put("pattern2", new CommandParameter[]{
            CommandParameter.newEnum("enum2", false, new String[]{"say2"}),
            CommandParameter.newType("player", false, CommandParamType.TARGET),
            CommandParameter.newType("message", true, CommandParamType.STRING)
        });
    }
    
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // 命令执行逻辑
        return true;
    }
}
```

### 2.4 参数类型

| 类型 | 说明 |
|------|------|
| `CommandParamType.INT` | 整数 |
| `CommandParamType.FLOAT` | 浮点数 |
| `CommandParamType.STRING` | 字符串 |
| `CommandParamType.TARGET` | 目标玩家 |
| `CommandParamType.BLOCK_POSITION` | 方块位置 |
| `CommandParamType.POSITION` | 位置 |
| `CommandParamType.MESSAGE` | 消息文本 |

---

## 3. 配置文件 (config)

展示 YAML 配置文件的读取和写入。

### 3.1 核心类

| 类名 | 功能 |
|------|------|
| [ExampleConfig](src/main/java/cn/nukkitmot/exampleplugin/config/ExampleConfig.java) | 配置管理类 |

### 3.2 基本用法

```java
public class ExampleConfig {
    private final Config config;
    
    public ExampleConfig() {
        // 从 resources 解压默认配置
        ExamplePlugin.getInstance().saveResource("config.yml");
        
        // 加载配置
        config = new Config(
            new File(ExamplePlugin.getInstance().getDataFolder(), "config.yml"),
            Config.YAML,
            // 默认值
            new ConfigSection(new LinkedHashMap<>() {{
                put("this-is-a-key", "Hello! Config!");
                put("another-key", true);
                put("object-key", new LinkedHashMap<String, Object>() {{
                    put("enabled", false);
                    put("subKey1", "nukkit");
                }});
            }})
        );
    }
    
    // 读取配置
    public String getString(String key) {
        return config.getString(key);
    }
    
    public int getInt(String key) {
        return config.getInt(key);
    }
    
    public boolean getBoolean(String key) {
        return config.getBoolean(key);
    }
    
    // 保存配置
    public void save() {
        config.save();
    }
}
```

### 3.3 使用示例

```java
// 初始化
ExampleConfig config = new ExampleConfig();

// 读取
String value = config.getString("this-is-a-key");
int number = config.getInt("number-key");

// 修改并保存
config.set("this-is-a-key", "新值");
config.save();
```

---

## 4. 自定义内容 (custom)

### 4.1 自定义物品

| 类名 | 功能 |
|------|------|
| [CandyCaneSword](src/main/java/cn/nukkitmot/exampleplugin/custom/item/CandyCaneSword.java) | 自定义剑类物品 |

```java
public class CandyCaneSword extends ItemCustom {
    
    public CandyCaneSword() {
        super("nukkit:candy_cane_sword", null, "candy_cane_sword");
    }
    
    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
            .simpleBuilder(this, null)
            .allowOffHand(true)      // 允许副手
            .handEquipped(true)      // 作为主手装备
            .build();
    }
    
    @Override
    public int getMaxDurability() {
        return 500;  // 耐久度
    }
    
    @Override
    public int getMaxStackSize() {
        return 1;    // 最大堆叠
    }
}
```

### 4.2 注册自定义物品

```java
// 在插件主类中注册
private void registerItems() {
    Item.registerCustomItem(CandyCaneSword.class);
}
```

### 4.3 自定义实体

| 类名 | 功能 |
|------|------|
| [MarkerEntity](src/main/java/cn/nukkitmot/exampleplugin/custom/entity/MarkerEntity.java) | 自定义标记实体 |

```java
// 注册自定义实体
EntityManager.get().registerDefinition(
    CustomEntityDefinition.builder()
        .identifier("nukkit:marker")
        .summonable(true)
        .spawnEgg(true)
        .implementation(MarkerEntity.class)
        .build()
);
```

### 4.4 自定义附魔

| 类名 | 功能 |
|------|------|
| [AutoRemeltedEnchatment](src/main/java/cn/nukkitmot/exampleplugin/custom/enchantment/AutoRemeltedEnchatment.java) | 自定义附魔 |

---

## 5. 表单系统 (form)

展示 Nukkit 表单的完整实现。

### 5.1 核心类

| 类名 | 功能 |
|------|------|
| [DemoSimpleForm](src/main/java/cn/nukkitmot/exampleplugin/form/DemoSimpleForm.java) | 简单表单（按钮列表） |
| [DemoCustomForm](src/main/java/cn/nukkitmot/exampleplugin/form/DemoCustomForm.java) | 自定义表单（输入框、下拉等） |
| [DemoModalForm](src/main/java/cn/nukkitmot/exampleplugin/form/DemoModalForm.java) | 模式表单（是/否） |
| [DemoDialogForm](src/main/java/cn/nukkitmot/exampleplugin/form/DemoDialogForm.java) | 对话框表单 |
| [DemoScrollingTextDialog](src/main/java/cn/nukkitmot/exampleplugin/form/DemoScrollingTextDialog.java) | 滚动文本对话框 |

### 5.2 简单表单

```java
public class DemoSimpleForm {
    public static void open(Player player) {
        FormWindowSimple form = new FormWindowSimple("标题", "描述文本");
        
        // 添加按钮
        form.addButton(new ElementButton("按钮1"));
        form.addButton(new ElementButton("按钮2"));
        
        // 处理点击
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) return;
            
            int buttonId = form.getResponse().getClickedButtonId();
            String buttonText = form.getResponse().getClickedButton().getText();
            
            // 处理不同按钮
            switch (buttonId) {
                case 0: // 按钮1
                    break;
                case 1: // 按钮2
                    break;
            }
        }));
        
        player.showFormWindow(form);
    }
}
```

### 5.3 自定义表单

```java
FormWindowCustom form = new FormWindowCustom("自定义表单");

// 添加输入框
form.addElement(new ElementInput("输入框标签", "占位符文本", "默认值"));

// 添加下拉菜单
form.addElement(new ElementDropdown("下拉菜单", Arrays.asList("选项1", "选项2", "选项3")));

// 添加滑块
form.addElement(new ElementSlider("滑块", 0, 100, 1, 50));

// 添加开关
form.addElement(new ElementToggle("开关", true));

// 处理提交
form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
    if (form.wasClosed()) return;
    
    String input = form.getResponse().getInputResponse(0);
    int dropdownIndex = form.getResponse().getDropdownResponse(1).getElementID();
    float sliderValue = form.getResponse().getSliderResponse(2);
    boolean toggleValue = form.getResponse().getToggleResponse(3);
}));

player.showFormWindow(form);
```

### 5.4 模式表单

```java
FormWindowModal form = new FormWindowModal(
    "确认", 
    "确定要执行此操作吗？",
    "是",  // 确认按钮文本
    "否"   // 取消按钮文本
);

form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
    if (form.wasClosed()) return;
    
    boolean confirmed = form.getResponse().getClickedButtonId() == 0;
    if (confirmed) {
        // 确认操作
    } else {
        // 取消操作
    }
}));

player.showFormWindow(form);
```

---

## 6. NoteBlockSound 音乐系统 (nbs)

提供 NBS 文件的播放功能，支持为单个或所有玩家播放音乐。

### 6.1 核心类

| 类名 | 功能 |
|------|------|
| [NBSSoundManager](src/main/java/cn/nukkitmot/exampleplugin/nbs/NBSSoundManager.java) | NBS 音乐管理器 |
| [NBSSongPlayer](src/main/java/cn/nukkitmot/exampleplugin/nbs/NBSSongPlayer.java) | NBS 播放器 |
| [NBSDecoder](src/main/java/cn/nukkitmot/exampleplugin/nbs/NBSDecoder.java) | NBS 文件解码器 |
| [Song](src/main/java/cn/nukkitmot/exampleplugin/nbs/Song.java) | 歌曲数据 |
| [Layer](src/main/java/cn/nukkitmot/exampleplugin/nbs/Layer.java) | 音轨层 |
| [Note](src/main/java/cn/nukkitmot/exampleplugin/nbs/Note.java) | 音符数据 |

### 6.2 初始化

```java
// 在插件 onEnable() 中初始化
NBSSoundManager.init(this);

// 在插件 onDisable() 中停止所有音乐
NBSSoundManager.getInstance().stopAll();
```

### 6.3 播放音乐

```java
NBSSoundManager nbs = NBSSoundManager.getInstance();

// 为单个玩家播放
File nbsFile = new File(getDataFolder(), "music/song.nbs");
NBSSongPlayer player = nbs.playNBS(player, nbsFile);
NBSSongPlayer player = nbs.playNBS(player, nbsFile, 100f); // 指定音量

// 在指定位置播放（范围内玩家都能听到）
NBSSongPlayer player = nbs.playNBS(nbsFile, position);
NBSSongPlayer player = nbs.playNBS(nbsFile, position, 100f);

// 为所有玩家播放
NBSSongPlayer player = nbs.playNBSForAll(nbsFile);
NBSSongPlayer player = nbs.playNBSForAll(nbsFile, 100f);
```

### 6.4 控制播放

```java
// 停止指定播放器
nbs.stopPlayer(playerId);

// 停止玩家的所有音乐
nbs.stopPlayerSongs(player);

// 停止所有音乐
nbs.stopAll();

// 获取播放器
NBSSongPlayer player = nbs.getPlayer(playerId);
```

### 6.5 播放器控制

```java
NBSSongPlayer player = nbs.playNBS(...);

// 添加/移除听众
player.addPlayer(anotherPlayer);
player.removePlayer(player);

// 控制播放
player.setPlaying(false);  // 暂停
player.setPlaying(true);   // 继续
player.setFadeTime(10);    // 设置淡入淡出时间(tick)
player.setAutoDestroy(true); // 播放完自动销毁
```

---

## 7. 计分板系统 (scoreboard)

展示计分板的完整实现。

### 7.1 核心类

| 类名 | 功能 |
|------|------|
| [ScoreBoardAPI](src/main/java/cn/nukkitmot/exampleplugin/scoreboard/ScoreBoardAPI.java) | 计分板 API |
| [BoardManager](src/main/java/cn/nukkitmot/exampleplugin/scoreboard/BoardManager.java) | 计分板管理器 |
| [IBoard](src/main/java/cn/nukkitmot/exampleplugin/scoreboard/IBoard.java) | 计分板接口 |
| [NukkitBoardImpl](src/main/java/cn/nukkitmot/exampleplugin/scoreboard/NukkitBoardImpl.java) | Nukkit 计分板实现 |

### 7.2 基本用法

```java
// 初始化
BoardManager boardManager = new BoardManager(this, new ImplInReal());
ScoreBoardAPI.setManager(boardManager);

// 创建计分板
IBoard board = ScoreBoardAPI.createBoard(player);

// 设置标题
board.setDisplayName("§l§e我的服务器");

// 设置行内容
board.setLines(Arrays.asList(
    "§7----------------",
    "§f玩家: §a" + player.getName(),
    "§f等级: §e10",
    "§f金币: §61000",
    "§7----------------"
));

// 更新特定行
board.updateLine(2, "§f等级: §e" + newLevel);

// 删除计分板
ScoreBoardAPI.removeBoard(player);
```

---

## 8. 定时任务 (BroadcastPluginTask)

展示 Nukkit 定时任务的实现。

### 8.1 核心类

| 类名 | 功能 |
|------|------|
| [BroadcastPluginTask](src/main/java/cn/nukkitmot/exampleplugin/BroadcastPluginTask.java) | 定时广播任务 |

### 8.2 创建任务

```java
public class BroadcastPluginTask extends PluginTask<ExamplePlugin> {
    
    public BroadcastPluginTask(ExamplePlugin owner) {
        super(owner);
    }
    
    @Override
    public void onRun(int currentTick) {
        // 定时执行的逻辑
        getOwner().getLogger().info("任务执行于 tick: " + currentTick);
    }
}
```

### 8.3 注册任务

```java
// 延迟任务 - 延迟100tick执行
this.getServer().getScheduler().scheduleDelayedTask(
    new BroadcastPluginTask(this), 
    100
);

// 重复任务 - 每20tick执行一次
this.getServer().getScheduler().scheduleRepeatingTask(
    new BroadcastPluginTask(this), 
    20
);

// 延迟重复任务 - 延迟500tick后，每200tick执行
this.getServer().getScheduler().scheduleDelayedRepeatingTask(
    new BroadcastPluginTask(this), 
    500,   // 延迟
    200    // 间隔
);

// 异步任务
this.getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {
    @Override
    public void onRun() {
        // 异步执行的代码
    }
});
```

---

## 9. 事件监听 (EventListener)

展示 Nukkit 事件监听器的实现。

### 9.1 核心类

| 类名 | 功能 |
|------|------|
| [EventListener](src/main/java/cn/nukkitmot/exampleplugin/EventListener.java) | 事件监听器 |

### 9.2 创建监听器

```java
public class EventListener implements Listener {
    private final ExamplePlugin plugin;
    
    public EventListener(ExamplePlugin plugin) {
        this.plugin = plugin;
    }
    
    // 玩家加入事件
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("欢迎 " + player.getName() + " 加入服务器!");
    }
    
    // 玩家离开事件
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getLogger().info(player.getName() + " 离开了服务器");
    }
    
    // 玩家聊天事件
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // 拦截特定消息
        if (message.contains("脏话")) {
            event.setCancelled(true);
            player.sendMessage("§c请勿使用不当语言!");
        }
    }
}
```

### 9.3 注册监听器

```java
// 在插件主类中注册
@Override
public void onEnable() {
    this.getServer().getPluginManager().registerEvents(
        new EventListener(this), 
        this
    );
}
```

### 9.4 常用事件

| 事件类 | 说明 |
|--------|------|
| `PlayerJoinEvent` | 玩家加入 |
| `PlayerQuitEvent` | 玩家离开 |
| `PlayerChatEvent` | 玩家聊天 |
| `PlayerInteractEvent` | 玩家交互 |
| `BlockBreakEvent` | 方块破坏 |
| `BlockPlaceEvent` | 方块放置 |
| `EntityDamageEvent` | 实体受伤 |
| `PlayerDeathEvent` | 玩家死亡 |
| `InventoryClickEvent` | 背包点击 |
| `PlayerMoveEvent` | 玩家移动 |

---

## 10. 库加载器 (loader)

展示外部库的加载和管理。

### 10.1 核心类

| 类名 | 功能 |
|------|------|
| [NukkitLibraryManager](src/main/java/cn/nukkitmot/exampleplugin/loader/NukkitLibraryManager.java) | 库管理器 |
| [Library](src/main/java/cn/nukkitmot/exampleplugin/loader/Library.java) | 库定义 |
| [Repositories](src/main/java/cn/nukkitmot/exampleplugin/loader/Repositories.java) | 仓库配置 |

### 10.2 加载库

```java
// 在插件主类中加载库
@Override
public void onLoad() {
    NukkitLibraryManager manager = new NukkitLibraryManager(this);
    
    // 添加 Maven 仓库
    manager.addRepository(Repositories.MAVEN_CENTRAL);
    manager.addRepository(Repositories.JITPACK);
    
    // 添加依赖库
    manager.loadLibrary(Library.builder()
        .groupId("com.google.code.gson")
        .artifactId("gson")
        .version("2.10.1")
        .build());
    
    // 加载所有库
    manager.loadAll();
}
```

---

## 11. 插件主类 (ExamplePlugin)

展示插件主类的完整结构。

```java
public class ExamplePlugin extends PluginBase {
    
    private static ExamplePlugin instance;
    private static PluginI18n i18n;
    
    @Override
    public void onLoad() {
        instance = this;
        // 加载库等预处理
    }
    
    @Override
    public void onEnable() {
        // 保存默认资源
        saveResource("config.yml");
        
        // 初始化 i18n
        i18n = PluginI18nManager.register(this);
        
        // 初始化各系统
        CameraManager.init(this);
        NBSSoundManager.init(this);
        
        // 注册命令
        getServer().getCommandMap().register("exampleplugin", new ExampleCommand());
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        
        // 注册定时任务
        getServer().getScheduler().scheduleRepeatingTask(
            new BroadcastPluginTask(this), 20);
        
        // 注册自定义物品
        registerItems();
        
        // 注册自定义实体
        registerEntities();
        
        getLogger().info("插件已启用!");
    }
    
    @Override
    public void onDisable() {
        // 清理资源
        NBSSoundManager.getInstance().stopAll();
        CameraManager.getInstance().clearAllCameras();
        
        getLogger().info("插件已禁用!");
    }
    
    public static ExamplePlugin getInstance() {
        return instance;
    }
    
    public static PluginI18n getI18n() {
        return i18n;
    }
}
```

---

## 12. 编译和部署

### 12.1 编译插件

```bash
# 使用 Gradle 编译
./gradlew build

# 或者在 Windows 上
gradlew.bat build
```

### 12.2 部署插件

编译后的插件位于 `build/libs/` 目录下：

```
build/libs/
├── ExamplePlugin-1.0.0.jar          # 主插件
└── ExamplePlugin-1.0.0-all.jar      # 包含依赖的完整包
```

将 JAR 文件复制到服务器的 `plugins/` 目录即可。

---

## 13. 多语言支持

### 13.1 语言文件

在 `src/main/resources/lang/` 目录下创建语言文件：

**zh_CN.lang**:
```properties
exampleplugin.helloworld=你好，世界！
exampleplugin.examplecommand.description=示例命令
```

**en_US.lang**:
```properties
exampleplugin.helloworld=Hello, World!
exampleplugin.examplecommand.description=Example command
```

### 13.2 使用多语言

```java
// 发送给玩家
player.sendMessage(i18n.tr(player.getLangCode(), "exampleplugin.helloworld"));

// 使用占位符
player.sendMessage(i18n.tr(player.getLangCode(), 
    "exampleplugin.welcome", player.getName()));

// 获取服务器默认语言
LangCode serverLang = getServer().getLanguage().getLang();
```

---

## 许可证

本项目采用 MIT 许可证。

## 作者

- NukkitMOT Team
- QingTong Development Team

## 相关链接

- [Nukkit GitHub](https://github.com/CloudburstMC/Nukkit)
- [Nukkit-MOT GitHub](https://github.com/MemoriesOfTime/Nukkit-MOT)
- [Nukkit 文档](https://cloudburstmc.org/wiki/nukkit/)
