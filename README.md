# More Rock n Roll

一个面向 Minecraft 1.21.1 的 Fabric 音乐唱片收藏模组。目前加入了 8 张自定义唱片，并提供简体中文和英文文本。

## 开发环境

- Minecraft 1.21.1
- Java 21
- Fabric Loader 0.19.3 或更高版本
- Fabric API 0.116.15+1.21.1
- Yarn mappings 1.21.1+build.3

## 开发命令

在 Windows PowerShell 中运行：

```powershell
.\gradlew.bat runDatagen
.\gradlew.bat runClient
.\gradlew.bat build
```

`runDatagen` 会把唱片注册表数据和物品模型生成到 `src/main/generated`。该目录中的 JSON 应提交到版本库，只有其中的 `.cache` 目录会被忽略。

## 项目结构

- `src/main/java`：注册代码、唱片定义和 datagen provider
- `src/main/resources`：语言、贴图、声音与模组元数据等手写资源
- `src/main/generated`：由 datagen 生成的 JSON 资源
- `run`：本地开发运行目录，不进入版本库

## 添加曲目

1. 准备单声道 OGG Vorbis 音频和唱片贴图。
2. 注册 `SoundEvent`、唱片物品与 `JukeboxSong` 定义。
3. 更新 `sounds.json` 以及中英文语言文件。
4. 运行 `runDatagen`，检查生成的注册表和模型 JSON。
5. 运行 `runClient` 测试听感、名称、材质和比较器输出。

## 许可证与素材

项目代码当前使用 [CC0 1.0](LICENSE)。音乐录音、封面和其他第三方素材可能拥有各自的版权，不因代码许可证而自动获得授权；公开发布或分发前，请确认你拥有相应权利，并在这里补充素材来源和许可说明。  

此模组未获任何音乐授权，仅用于个人娱乐目的，故不上传任一音乐唱片的音频文件。

当前曲目的 `music_disc.*.ogg` 录音只保留在本地开发环境中，不包含在公开代码仓库内。其他拥有分发授权的 OGG 音效仍可正常提交，并由 Git LFS 管理。

## 状态

项目仍处于早期开发阶段，接口、内容和资源结构可能继续调整。
