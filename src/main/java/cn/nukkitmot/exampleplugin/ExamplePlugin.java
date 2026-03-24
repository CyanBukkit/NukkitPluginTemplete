package cn.nukkitmot.exampleplugin;

import cn.cyanbukkit.example.MainKt;
import cn.nukkit.Server;
import cn.nukkit.entity.custom.EntityManager;
import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.LangCode;
import cn.nukkit.lang.PluginI18n;
import cn.nukkit.lang.PluginI18nManager;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cn.nukkitmot.exampleplugin.command.ExampleCommand;
import cn.nukkitmot.exampleplugin.config.ExampleConfig;
import cn.nukkitmot.exampleplugin.custom.enchantment.AutoRemeltedEnchatment;
import cn.nukkitmot.exampleplugin.custom.entity.MarkerEntity;
import cn.nukkitmot.exampleplugin.custom.item.CandyCaneSword;
import cn.nukkitmot.exampleplugin.loader.Library;
import cn.nukkitmot.exampleplugin.loader.NukkitLibraryManager;
import cn.nukkitmot.exampleplugin.loader.Repositories;
import cn.nukkitmot.exampleplugin.scoreboard.BoardManager;
import cn.nukkitmot.exampleplugin.scoreboard.example.ImplInReal;
import cn.nukkitmot.exampleplugin.nbs.NBSSoundManager;
import cn.nukkitmot.exampleplugin.camera.CameraManager;
import lombok.Getter;

import java.util.List;

/**
 * author: MagicDroidX
 * NukkitExamplePlugin Project
 */
public class ExamplePlugin extends PluginBase {
    @Getter
    public static ExamplePlugin instance;
    @Getter
    public static PluginI18n i18n;
    @Getter
    public static LangCode serverLangCode;
    @Getter
    public static ExampleConfig EXAMPLE_CONFIG;
    private NukkitLibraryManager libraryManager;

    @Getter
    public static BoardManager boardManager;

    public ExamplePlugin() {
    }


    @Override
    public void onLoad() {
        libraryManager = new NukkitLibraryManager(this);

        libraryManager.addRepository(Repositories.MAVEN_CENTRAL);
        libraryManager.addRepository(Repositories.JITPACK);

        Library kotlinStdLib = Library.builder()
                .groupId("org.jetbrains.kotlin")
                .artifactId("kotlin-stdlib")
                .version("2.3.20")
                .build();

        libraryManager.loadLibrary(kotlinStdLib);
        System.out.println("依赖库加载完成");

        instance = this;
        //register the plugin i18n
        i18n = PluginI18nManager.register(this);
        initServerLangCode();
        //register the command of plugin
        this.getServer().getCommandMap().register("exampleplugin", new ExampleCommand());

        //register the custom item of server
//        registerItems();
        //register the custom entity of server
//        registerEntities();
//        Enchantment.register(new AutoRemeltedEnchatment(), true);

        this.getLogger().info(TextFormat.WHITE + "I've been loaded!");
    }

    @Override
    public void onEnable() { 
        this.getLogger().info(TextFormat.DARK_GREEN + "I've been enabled!");
        this.getLogger().info(String.valueOf(this.getDataFolder().mkdirs()));

        NBSSoundManager.init(this);
        CameraManager.init(this);

        //Use the plugin's i18n output
        this.getLogger().info(i18n.tr(getServerLangCode(), "exampleplugin.helloworld", "世界"));

        //Register the EventListener
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        //PluginTask
        this.getServer().getScheduler().scheduleDelayedRepeatingTask(new BroadcastPluginTask(this), 500, 200);

        //Save resources
        this.saveResource("string.txt");

        //Config reading and writing
        EXAMPLE_CONFIG = new ExampleConfig();
        getLogger().info(EXAMPLE_CONFIG.getAKey());

        EXAMPLE_CONFIG.getObjectKey().setSubKey1("this a new random number "+Math.random()).save();
        getLogger().info(EXAMPLE_CONFIG.getObjectKey().getSubKey1());
        boardManager = new BoardManager(this, new ImplInReal());
        MainKt.INSTANCE.run();
    }

    @Override
    public void onDisable() {
        NBSSoundManager.getInstance().stopAll();
        CameraManager.getInstance().clearAllCameras();
        this.getLogger().info(TextFormat.DARK_RED + "I've been disabled!");
    }

    private void registerItems() {
        List<Class<? extends ItemCustom>> list = List.of(
                CandyCaneSword.class
        );
        for (Class<? extends ItemCustom> item : list) {
            Item.registerCustomItem(item);
        }
    }

    private void registerEntities() {
        EntityManager.get().registerDefinition(MarkerEntity.DEF);
    }

    public void initServerLangCode() {
        switch (Server.getInstance().getLanguage().getLang()) {
            case "eng" -> {
                serverLangCode = LangCode.en_US;
            }
            case "chs" -> {
                serverLangCode = LangCode.zh_CN;
            }
            case "deu" -> {
                serverLangCode = LangCode.de_DE;
            }
            case "rus" -> {
                serverLangCode = LangCode.ru_RU;
            }
            default -> {
                try {
                    serverLangCode = LangCode.valueOf(Server.getInstance().getLanguage().getLang());
                } catch (IllegalArgumentException e) {
                    serverLangCode = LangCode.en_US;
                }
            }
        }
    }
}
