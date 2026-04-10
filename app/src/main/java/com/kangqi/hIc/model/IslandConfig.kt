package com.kangqi.hIc.model

/**
 * Complete Super Island configuration matching the official miui.focus.param API.
 * Supports HyperOS 3 templates 1-22, all component types, and full visual customization.
 */
data class IslandConfig(
    // ── Content (大岛文字) ──
    val title: String = "HyperIsland",
    val content: String = "",
    val frontTitle: String = "",
    val ticker: String = "",
    val aodTitle: String = "",

    // ── Timing ──
    val duration: Int = 5000,
    val islandTimeout: Int = 3600,
    val islandFirstFloat: Boolean = true,
    val enableFloat: Boolean = false,

    // ── Appearance (颜色与样式) ──
    val highlightColor: String = "#3482FF",
    val borderColor: String = "#FFFFFF",
    val progressColor: String = "#3482FF",
    val progressBgColor: String = "#333333",
    val emphasisColor: String = "#3482FF",
    val islandBackgroundColor: String = "",
    val textColor: String = "",
    val contentColor: String = "",

    // ── Progress (进度条, 支持类型1-3) ──
    val showProgress: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 100,
    val progressType: Int = 1,
    val progressGradientStart: String = "",
    val progressGradientEnd: String = "",
    val progressNodeCount: Int = 0,

    // ── Timer (倒计时) ──
    val showTimer: Boolean = false,
    val timerDurationMs: Long = 0,

    // ── Template (模板 1-22) ──
    val templateType: Int = 1,
    val business: String = "custom",
    val islandProperty: Int = 1,

    // ── Big Island Layout (大岛区域) ──
    val leftComponentType: Int = 1,
    val rightComponentType: Int = 2,
    val rightText: String = "",
    val showCoverImage: Boolean = false,

    // ── Button (按钮组件, 类型1-5) ──
    val showButton: Boolean = false,
    val buttonType: Int = 1,
    val buttonCount: Int = 1,
    val buttonText1: String = "",
    val buttonText2: String = "",
    val buttonText3: String = "",
    val buttonColor: String = "#3482FF",
    val buttonTextColor: String = "#FFFFFF",
    val showButtonProgress: Boolean = false,
    val buttonProgressValue: Int = 0,

    // ── Notification ──
    val notifTitle: String = "",
    val notifContent: String = "",
    val notifImportance: Int = 3,
    val isShowNotification: Boolean = true,
    val substName: String = "HyperIsland Custom",

    // ── Small Island (小岛) ──
    val smallIslandText: String = "",
    val showSmallIslandProgress: Boolean = false,
    val smallIslandType: Int = 0,

    // ── Rich Text (富文本) ──
    val useHighLight: Boolean = true,
    val emphasisStart: Int = 0,
    val emphasisEnd: Int = 0,
    val delimiterVisible: Boolean = false,

    // ── Update ──
    val updatable: Boolean = false,

    // ── Package Spoofing (伪装应用) ──
    val spoofPackage: String = "",

    // ── Custom Icon (自定义图标) ──
    val customImageUri: String = "",

    // ── App Icon (从已安装应用选择图标) ──
    val iconPackage: String = "",
)

data class IslandTemplate(
    val id: String,
    val name: String,
    val title: String,
    val content: String,
    val duration: Int = 5000,
    val highlightColor: String = "#3482FF",
    val borderColor: String = "#FFFFFF",
    val showProgress: Boolean = false,
    val showTimer: Boolean = false,
    val timerDurationMs: Long = 0,
    val templateType: Int = 1,
    val business: String = "custom",
    val icon: String? = null,
    val showButton: Boolean = false,
    val buttonText1: String = "",
    val buttonColor: String = "#3482FF",
    val progressType: Int = 1,
    val rightComponentType: Int = 2,
    val rightText: String = "",
    val smallIslandType: Int = 0,
    val emphasisColor: String = "#3482FF",
)

data class ModuleStatus(
    val isActive: Boolean = false,
    val hookedApps: Int = 0,
    val templatesCount: Int = 0,
    val xposedApiVersion: Int = 0,
    val managerVersion: String = "Unknown"
)

/**
 * Well-known package names for spoofing — HyperOS island validates these
 * to determine which built-in template to render.
 */
object SpoofPackages {
    const val GAODE_MAP = "com.autonavi.minimap"
    const val BAIDU_MAP = "com.baidu.BaiduMap"
    const val DIDI = "com.sdu.didi.psnger"
    const val MEITUAN = "com.sankuai.meituan"
    const val ELEME = "me.ele"
    const val PHONE = "com.android.incallui"
    const val CLOCK = "com.android.deskclock"
    const val XIAOMI_CLOCK = "com.xiaomi.clock"
    const val MUSIC = "com.tencent.qqmusic"
    const val NETEASE_MUSIC = "com.netease.cloudmusic"
    const val WECHAT = "com.tencent.mm"
    const val ALIPAY = "com.eg.android.AlipayGphone"
    const val BROWSER = "com.android.browser"
    const val MIUI_WEATHER = "com.miui.weather2"
    const val SHAREPLAY = "com.miui.misound"
    const val FILES = "com.android.fileexplorer"

    val ALL = listOf(
        "" to "不伪装",
        GAODE_MAP to "高德地图",
        BAIDU_MAP to "百度地图",
        DIDI to "滴滴出行",
        MEITUAN to "美团",
        ELEME to "饿了么",
        PHONE to "电话",
        CLOCK to "时钟",
        XIAOMI_CLOCK to "小米时钟",
        MUSIC to "QQ音乐",
        NETEASE_MUSIC to "网易云音乐",
        WECHAT to "微信",
        ALIPAY to "支付宝",
        MIUI_WEATHER to "天气",
        FILES to "文件管理",
    )
}

enum class IslandScenario(
    val label: String,
    val business: String,
    val templateType: Int,
    val recommendedPackage: String = ""
) {
    CUSTOM("自定义", "custom", 1),
    NAVIGATION("导航", "navigation", 14, SpoofPackages.GAODE_MAP),
    RIDE_HAILING("打车", "taxi", 4, SpoofPackages.DIDI),
    FOOD_DELIVERY("外卖", "food", 4, SpoofPackages.MEITUAN),
    CALL("通话", "call", 3, SpoofPackages.PHONE),
    TIMER("计时器", "timer", 5, SpoofPackages.XIAOMI_CLOCK),
    DOWNLOAD("下载", "download", 5),
    WEATHER("天气", "weather", 1, SpoofPackages.MIUI_WEATHER),
    VERIFICATION("验证码", "verification", 16),
    MUSIC("音乐", "music", 1, SpoofPackages.NETEASE_MUSIC),
    PAYMENT("支付", "payment", 2, SpoofPackages.ALIPAY),
    FILE_TRANSFER("传输", "transfer", 6, SpoofPackages.FILES),
    LIVE("直播", "live", 7),
    RECORDING("录音", "recording", 8),
    GAMING("游戏", "gaming", 9),
    SPORTS("运动", "sports", 10),
    CHARGING("充电", "charging", 11),
    BUTTON_DEMO("按钮", "button_demo", 12),
    COVER("封面", "cover", 13),
    COUNTDOWN("倒数", "countdown", 15),
    STAGED("分阶段", "staged", 17),
    MULTI_BUTTON("多按钮", "multi_button", 18),
}
