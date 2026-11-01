# DT-OS 病程日历（PTDSmobile）

一款面向慢病 / 长期随访患者的 **Android 病程管理应用**：用"工业级临床遥测"的视觉语言，把病程日历、用药打卡、生命体征监测和复诊问诊准备装进一部手机。

> 本项目由 Google AI Studio（Stitch 设计稿）生成 Android 原生工程，UI 为深色临床监护风格，全部数据保存在本地，不上传任何服务器。

---

## ✨ 功能特性

| 模块 | 说明 |
| --- | --- |
| 📅 **病程日历** | 月历视图 + 单日病程时间线，查房 / 异常节点圆点标记，事件可展开查看关联医嘱与备注 |
| ➕ **添加记录** | 四种录入方式：选择录入、照片 OCR、药盒扫描（相机）、手动时间线 |
| 💊 **用药管理** | 药板打卡矩阵、每日剂量进度、余量提示；可粘贴医嘱处方文本自动解析成用药计划 |
| 🗂 **模块化卡片** | 11 种可视化卡片（取景扫描、趋势曲线、体温计标尺、心电脉冲波等），支持编辑、替换、删除槽位 |
| 🩺 **生命体征遥测** | 血糖 / 血氧 / 皮温同心圆轨道，阈值越限自动高亮告警，可按轨道开关显示 |
| 📋 **SBAR 复诊档案** | 勾选病程事件 + 症状标签后，一键生成 30 秒口述问诊话术与待确认问题清单，内置临床文本脱敏，支持复制导出 |

> ⚠️ 当前版本的 SBAR 生成与体征数据为**本地模板 / 演示数据**（未发起真实网络请求），`.env` 中的 `GEMINI_API_KEY` 为 AI Studio 预留位，供后续接入真实 AI 能力使用。

## 🛠 技术栈

- **语言 / UI**：Kotlin 2.2.10 · Jetpack Compose（BOM 2024.09.00）· Material 3
- **架构**：MVVM（单 Activity + `TelemetryViewModel` + Kotlin Flow 状态流）
- **构建**：AGP 9.1.1 · Gradle 9.3.1 · Java 11 · KSP
- **其他**：CameraX（药盒扫描）、Secrets Gradle Plugin（`.env` 注入）、Robolectric 单元测试
- **设备要求**：minSdk 24（Android 7.0）及以上，targetSdk 36（Android 16）

## 📦 环境要求

1. **Android Studio** Otter（2025.2.x）或更高稳定版（需支持 AGP 9.1.1）
   - Android Studio 自带 JDK，无需单独安装 Java
2. 一台 **Android 7.0+** 真机（开启 USB 调试）或 API 24+ 的模拟器
3. （可选）本机已安装 Android SDK Platform 36

> 仓库未包含 `gradlew` Wrapper 脚本，推荐直接用 Android Studio 打开（会按 `gradle-wrapper.properties` 自动拉取 Gradle 9.3.1）；纯命令行构建请自备 Gradle 9.3.1。

## 🚀 快速开始

### 方式一：Android Studio（推荐）

```bash
git clone https://github.com/Karovia/PTDSmobile.git
```

1. 打开 Android Studio → **File ▸ Open** → 选择 `PTDSmobile` 根目录
2. 等待 Gradle Sync 完成（首次会自动下载 Gradle 9.3.1 与依赖，需要网络）
3. 选择设备 → 点击 ▶️ **Run 'app'**

### 方式二：命令行

```bash
# 使用本机安装的 Gradle（9.3.1+）
gradle assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk
```

### 构建 Release 包

Release 签名从环境变量读取（沿用 AI Studio 约定）：

```bash
export KEYSTORE_PATH=/path/to/my-upload-key.jks   # 默认取项目根目录 my-upload-key.jks
export STORE_PASSWORD=你的keystore密码
export KEY_PASSWORD=你的key密码
gradle assembleRelease
```

> Debug 构建使用仓库约定的 `debug.keystore`（密码 `android`），无需额外配置。

## 🔑 配置说明

| 文件 | 是否必需 | 说明 |
| --- | --- | --- |
| `.env` | 否 | 从 `.env.example` 复制。当前代码未实际调用 Gemini API，`GEMINI_API_KEY` 可不填 |
| `google-services.json` | 否 | 未配置时构建仅打印 WARN，不影响编译运行 |

项目不含 `INTERNET` 权限（仅 `CAMERA`，用于药盒扫描），是纯本地应用；后续接入 AI 时需在 `AndroidManifest.xml` 补充网络权限。

## 📁 项目结构

```
PTDSmobile/
├── app/src/main/java/com/example/
│   ├── MainActivity.kt              # 单 Activity 入口 + 屏幕切换动画
│   ├── model/TelemetryModels.kt     # 数据模型（卡片/时间线/用药/SBAR 等）
│   ├── viewmodel/TelemetryViewModel.kt  # 全局状态与业务逻辑
│   └── ui/
│       ├── screens/                 # 日历、详情、添加记录、设置四大屏幕
│       ├── components/ClinicalComponents.kt  # 临床风格通用组件
│       └── theme/                   # 配色、字体（Space Grotesk / Space Mono / Silkscreen）
├── .env.example                     # 环境变量模板（GEMINI_API_KEY 预留）
├── app/build.gradle.kts             # 模块构建配置（签名 / 密钥 / 依赖）
└── gradle/libs.versions.toml        # 依赖版本清单
```

## ❓ 常见问题

- **Sync 卡在下载 Gradle**：Gradle 9.3.1 体积较大，请耐心等待或配置代理；确保 Android Studio 为较新版本。
- **相机黑屏**：需在系统设置中授予相机权限；模拟器请使用扩展控件里的虚拟场景摄像头。
- **日历日期固定为 2026-10**：演示数据写死于 `TelemetryViewModel` 的 `defaultCalendarDays()`，可在其中调整。
- **SBAR 生成的内容是假的吗**：是本地规则模板生成的结构化话术（含脱敏处理），AI 接入后可替换 `generateStructuredSbar()`。

## ⚠️ 免责声明

本项目为**原型演示应用**，内置体征与病程数据均为虚构演示数据，不构成任何医疗建议，不可用于真实诊疗决策。

## 📄 License

仅供学习交流使用。
