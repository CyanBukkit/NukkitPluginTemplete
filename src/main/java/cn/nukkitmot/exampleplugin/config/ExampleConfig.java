package cn.nukkitmot.exampleplugin.config;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkitmot.exampleplugin.ExamplePlugin;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * 示例配置类
 * 
 * 展示了 Nukkit 插件配置文件的完整实现方式，包括：
 * <ul>
 *   <li>YAML 配置文件读取</li>
 *   <li>默认值设置</li>
 *   <li>嵌套对象解析</li>
 *   <li>配置保存与更新</li>
 * </ul>
 * 
 * 使用方法：
 * <pre>
 * // 在插件主类中初始化
 * ExampleConfig config = new ExampleConfig();
 * 
 * // 读取配置值
 * String aKey = config.getAKey();
 * boolean anotherKey = config.isAnotherKey();
 * 
 * // 修改并保存
 * config.setAKey("新值").save();
 * </pre>
 * 
 * @author NukkitMOT
 * @version 1.0.0
 */
public class ExampleConfig {
    /** Nukkit 配置对象 */
    private final Config config;

    /** 字符串类型配置项 */
    @Getter
    private String aKey;
    /** 布尔类型配置项 */
    @Getter
    private boolean anotherKey;
    /** 嵌套对象配置项 */
    @Getter
    private final KeyObject objectKey;
    /** 数组类型配置项 */
    @Getter
    private ArrayList<String> arrayKey;

    /**
     * 构造函数
     * 
     * 初始化配置文件，从插件资源目录加载 config.yml，
     * 如果文件不存在则从 jar 包中解压出来。
     */
    public ExampleConfig() {
        // 将 resources 目录下的 config.yml 解压到插件数据目录
        ExamplePlugin.getInstance().saveResource("config.yml");
        
        // 创建配置对象，指定配置文件路径和格式
        config = new Config(
                new File(ExamplePlugin.getInstance().getDataFolder(), "config.yml"),
                Config.YAML,
                // 默认值（可选）
                new ConfigSection(new LinkedHashMap<>() {{
                    put("this-is-a-key", "Hello! Config!");
                    put("another-key", true); // 支持其他标准对象类型
                    put("object-key", new LinkedHashMap<String, Object>() {{
                        put("enabled", false);
                        put("subKey1", "nukkit");
                        put("subKey2", 2023);
                    }});
                    put("array-key", Arrays.asList(
                            "first element",
                            "second element",
                            "third element"
                    ));
                }})
        );

        // 从配置文件中读取值
        setAKey(config.getString("this-is-a-key", "this-is-default-value"));
        setAnotherKey(config.getBoolean("another-key"));
        // 创建嵌套对象
        objectKey = new KeyObject(this);
        setArrayKey((ArrayList<String>) config.getStringList("array-key"));
    }

    /**
     * 设置字符串配置项
     * @param value 新的字符串值
     * @return 当前配置对象（支持链式调用）
     */
    public ExampleConfig setAKey(String value) {
        this.aKey = value;
        return this;
    }

    /**
     * 设置布尔配置项
     * @param value 新的布尔值
     * @return 当前配置对象（支持链式调用）
     */
    public ExampleConfig setAnotherKey(boolean value) {
        this.anotherKey = value;
        return this;
    }

    /**
     * 设置数组配置项
     * @param value 新的字符串数组
     * @return 当前配置对象（支持链式调用）
     */
    public ExampleConfig setArrayKey(ArrayList<String> value) {
        this.arrayKey = value;
        return this;
    }

    /**
     * 保存配置到文件
     * 将内存中的配置值写回到 config.yml 文件
     */
    public void save() {
        if (config == null) return;
        config.set("this-is-a-key", aKey);
        config.set("another-key", anotherKey);
        config.set("array-key", arrayKey);
        config.save();
    }

    /**
     * 嵌套配置对象类
     * 用于处理配置文件中复杂的多层嵌套结构
     */
    public class KeyObject {
        /** 配置节对象 */
        private final ConfigSection configSection;
        /** 父配置对象引用 */
        @Getter
        private final ExampleConfig parent;
        /** 启用状态 */
        @Getter
        private boolean enabled;
        /** 子键1 */
        @Getter
        private String subKey1;
        /** 子键2 */
        @Getter
        private Integer subKey2;

        /**
         * 构造函数
         * @param parent 父配置对象
         */
        public KeyObject(ExampleConfig parent) {
            this.parent = parent;
            // 获取嵌套的配置节
            this.configSection = config.getSection("object-key");

            // 读取配置值，带默认值
            this.enabled = configSection.getBoolean("enable", false);
            this.subKey1 = configSection.getString("subKey2", "nukkit");
            this.subKey2 = configSection.getInt("subKey3", 2023);
        }

        /**
         * 设置启用状态
         * @param value 新的启用状态
         * @return 当前对象（支持链式调用）
         */
        public KeyObject setEnabled(boolean value) {
            enabled = value;
            configSection.set("enabled", enabled);
            return this;
        }

        /**
         * 设置子键1
         * @param value 新的子键1值
         * @return 当前对象（支持链式调用）
         */
        public KeyObject setSubKey1(String value) {
            subKey1 = value;
            configSection.set("subKey1", subKey1);
            return this;
        }

        /**
         * 设置子键2
         * @param value 新的子键2值
         * @return 当前对象（支持链式调用）
         */
        public KeyObject setSubKey2(Integer value) {
            subKey2 = value;
            configSection.set("subKey2", subKey2);
            return this;
        }

        /**
         * 保存嵌套配置到文件
         * @return 父配置对象
         */
        public ExampleConfig save() {
            if (config == null) return null;
            configSection.set("enabled", enabled);
            configSection.set("subKey1", subKey1);
            configSection.set("subKey2", subKey2);
            config.save();
            return parent;
        }
    }
}