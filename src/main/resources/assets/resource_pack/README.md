# 渐变计分板背景资源包

## 效果预览

左侧完全透明渐变到右侧黑色半透明，提供优雅的计分板视觉效果。

```
透明度渐变示意:
左侧                                    右侧
 0%    25%    50%    75%    100%
├──────┼──────┼──────┼──────┤
█                              (完全透明)
██                             (轻微透明)
████                           (半透明)
██████                         (较不透明)
████████                       (黑色半透明)
```

## 文件结构

```
resource_pack/
├── manifest.json                    # 资源包清单
├── pack_icon.png                    # 资源包图标（可选）
├── create_scoreboard_texture.py     # 纹理生成脚本
├── README.md                        # 说明文档
└── subpacks/
    └── gradient/                    # 子包文件夹
        ├── textures/
        │   └── ui/
        │       ├── scoreboard.png   # 渐变纹理图片
        │       └── scoreboard.json  # 纹理配置
        └── ui/
            ├── _global_variables.json   # 全局变量
            └── scoreboards.json         # 计分板UI定义
```

## 技术规格

| 属性 | 值 |
|------|-----|
| 纹理尺寸 | 256x64 像素 |
| 颜色模式 | RGBA (带透明通道) |
| 九宫格切片 | 8 像素 |
| 透明度范围 | 左侧 0 → 右侧 180 |
| 背景颜色 | 纯黑色 (#000000) |

## 使用方法

### 1. 作为独立资源包

1. 将整个 `resource_pack` 文件夹复制到 Minecraft 的 `resource_packs` 目录
2. 重命名为 `GradientScoreboard`
3. 在游戏中启用此资源包

### 2. 集成到插件

将资源包文件放入插件的资源目录：

```
YourPlugin.jar/
├── plugin.yml
├── ...
└── assets/
    └── resource_pack/
        ├── manifest.json
        └── subpacks/
            └── gradient/
                ├── textures/
                │   └── ui/
                │       ├── scoreboard.png
                │       └── scoreboard.json
                └── ui/
                    ├── _global_variables.json
                    └── scoreboards.json
```

### 3. 自定义纹理

运行 Python 脚本重新生成纹理：

```bash
cd src/main/resources/assets/resource_pack
python create_scoreboard_texture.py
```

修改脚本参数可调整：
- `width`: 纹理宽度
- `height`: 纹理高度
- 渐变透明度范围

## 配置说明

### scoreboard.json

```json
{
  "nineslice_size": 8,      // 九宫格切片大小
  "base_size": [256, 64]    // 基础尺寸
}
```

### scoreboards.json

关键配置项：
- `alpha`: 0.95 - 整体透明度
- `texture`: "textures/ui/scoreboard" - 纹理路径
- `size`: ["100%c", "100%c + 6px"] - 自适应大小

### _global_variables.json

颜色配置：
- `$objective_title_color`: 标题颜色 [R, G, B]
- `$player_name_color`: 玩家名颜色 [R, G, B]
- `$player_score_color`: 分数颜色 [R, G, B]

## 兼容性

- **游戏版本**: 1.18.0+
- **服务端**: Nukkit-MOT, PocketMine-MP, BDS
- **客户端**: Windows 10/11, Android, iOS, Switch, Xbox

## 注意事项

1. 确保 `manifest.json` 中的 `min_engine_version` ≥ [1, 18, 0]
2. 纹理必须使用 PNG 格式以支持透明通道
3. 九宫格切片大小必须与 `scoreboard.json` 中的配置一致
4. 资源包 UUID 必须唯一，避免与其他资源包冲突

## 故障排除

### 计分板背景不显示

1. 检查资源包是否已启用
2. 检查纹理路径是否正确
3. 检查 manifest.json 格式是否正确

### 渐变效果不明显

1. 调整 `create_scoreboard_texture.py` 中的透明度范围
2. 检查 `scoreboards.json` 中的 `alpha` 值

### 与其他资源包冲突

1. 修改 `manifest.json` 中的 UUID
2. 调整资源包加载顺序

## 许可证

MIT License

## 作者

QingTong Development Team
