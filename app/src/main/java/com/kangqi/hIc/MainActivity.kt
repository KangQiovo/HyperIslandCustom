package com.kangqi.hIc

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kangqi.hIc.model.IslandConfig
import com.kangqi.hIc.model.ModuleStatus
import com.kangqi.hIc.service.IslandManager
import com.kangqi.hIc.ui.components.LiquidGlassNavBar
import com.kangqi.hIc.ui.components.NavItem
import com.kangqi.hIc.ui.screens.AboutScreen
import com.kangqi.hIc.ui.screens.CreditsScreen
import com.kangqi.hIc.ui.screens.HomeScreen
import com.kangqi.hIc.ui.screens.IslandControlScreen
import com.kangqi.hIc.ui.screens.LogViewerScreen
import com.kangqi.hIc.ui.screens.SettingsScreen
import com.kangqi.hIc.ui.screens.TemplatesScreen
import com.kangqi.hIc.utils.ConfigBackupHelper
import com.kangqi.hIc.ui.theme.DockStyle
import com.kangqi.hIc.log.HicLog
import com.kangqi.hIc.log.LogCollector
import com.kangqi.hIc.ui.theme.HyperIslandTheme

import com.kangqi.hIc.ui.theme.LocalIsDark
import com.kangqi.hIc.ui.theme.LocalThemeManager
import com.kangqi.hIc.ui.theme.ThemeManager
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.basic.TopAppBar as MiuixTopAppBar
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.ThemeController as MiuixThemeController
import com.kangqi.hIc.ui.theme.AppTheme
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.foundation.layout.PaddingValues
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var islandManager: IslandManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        HicLog.init(filesDir)
        LogCollector.init(this)

        islandManager = IslandManager(this)
        val moduleActive = isModuleActive()

        HicLog.i("MainActivity", "App started, module active=$moduleActive")

        showWelcomeIsland()

        setContent {
            val scope = rememberCoroutineScope()
            val themeManager = remember { ThemeManager(this@MainActivity, scope) }

            LaunchedEffect(Unit) { themeManager.load() }

            CompositionLocalProvider(LocalThemeManager provides themeManager) {
                val backdrop = rememberLayerBackdrop()
                HyperIslandTheme(themeManager = themeManager, backdrop = backdrop) {
                    HyperIslandApp(islandManager, moduleActive, themeManager, backdrop)
                }
            }
        }
    }

    private fun showWelcomeIsland() {
        Handler(Looper.getMainLooper()).postDelayed({
            val welcomeConfig = IslandConfig(
                title = "欢迎使用",
                content = "HyperIsland",
                frontTitle = "欢迎使用",
                ticker = "欢迎使用 · HyperIsland",
                aodTitle = "欢迎使用",
                duration = 5000,
                islandTimeout = 10,
                islandFirstFloat = true,
                enableFloat = false,
                highlightColor = "#3482FF",
                borderColor = "#FFFFFF",
                templateType = 1,
                business = "custom",
                islandProperty = 1,
                smallIslandText = "欢迎使用",
                notifTitle = "欢迎使用",
                notifContent = "HyperIsland Custom",
                updatable = false,
            )
            islandManager.showIsland(welcomeConfig)
        }, 500)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogCollector.destroy()
    }

    /**
     * Check if the Xposed module is active.
     * This method is hooked by Xposed to return true.
     */
    fun isModuleActive(): Boolean = false
}

@Composable
private fun HyperIslandApp(
    islandManager: IslandManager,
    isModuleActive: Boolean,
    themeManager: ThemeManager,
    backdrop: Backdrop
) {
    var selectedRoute by remember { mutableStateOf("home") }
    var templates by remember { mutableStateOf(islandManager.getTemplates()) }
    var whitelist by remember { mutableStateOf(islandManager.whitelist) }
    var isEnabled by remember { mutableStateOf(islandManager.isEnabled) }

    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    var customImageUri by remember { mutableStateOf("") }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            customImageUri = uri.toString()
        }
    }

    // Config import file picker
    val importConfigLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val result = ConfigBackupHelper.importConfig(context, uri)
                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                // Reload state from prefs
                themeManager.load()
                whitelist = islandManager.whitelist
                isEnabled = islandManager.isEnabled
                templates = islandManager.getTemplates()
            }
        }
    }

    val navItems = listOf(
        NavItem("主页", Icons.Filled.Home, "home"),
        NavItem("超级岛", Icons.Filled.Dashboard, "island"),
        NavItem("模板", Icons.Filled.Widgets, "templates"),
        NavItem("设置", Icons.Filled.Settings, "settings"),
    )

    val moduleStatus = ModuleStatus(
        isActive = isModuleActive,
        hookedApps = whitelist.size,
        templatesCount = templates.size,
        xposedApiVersion = 0,
        managerVersion = "v${com.kangqi.hic.BuildConfig.VERSION_NAME.trimStart('v', 'V')} (${com.kangqi.hic.BuildConfig.VERSION_CODE})"
    )

    val scrollBehavior = if (themeManager.appTheme == AppTheme.MIUIX) {
        MiuixScrollBehavior(rememberTopAppBarState())
    } else null

    val isSecondaryPage = selectedRoute == "logs" || selectedRoute == "credits" || selectedRoute == "about"
    val isGlassDock = themeManager.dockStyle == DockStyle.LIQUID_GLASS

    // Restart system UI dialog state — hoisted to MainActivity so the button can live in
    // MiuixTopAppBar actions (Miuix) OR inside IslandControlScreen fixed header (MD3/Glass)
    var showRestartDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        MiuixScaffold(
            topBar = {
                if (themeManager.appTheme == AppTheme.MIUIX && !isSecondaryPage) {
                    MiuixTopAppBar(
                        title = when (selectedRoute) {
                            "home" -> "HyperIsland"
                            "island" -> "超级岛"
                            "templates" -> "模板"
                            "settings" -> "设置"
                            "logs" -> "运行日志"
                            "credits" -> "引用项目"
                            "about" -> "关于"
                            else -> "HyperIsland"
                        },
                        scrollBehavior = scrollBehavior
                    )
                }
            },
            bottomBar = {
                // Hide dock bar on secondary pages (e.g. log viewer)
                // Only render solid dock bars for non-glass styles.
                // Glass navbar is rendered as a floating overlay outside the scaffold.
                if (!isGlassDock && !isSecondaryPage) {
                    when (themeManager.dockStyle) {
                        DockStyle.MIUIX -> {
                            if (themeManager.appTheme == AppTheme.MD3 &&
                                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
                            ) {
                                // Force Monet palette for Miuix dock under MD3 theme
                                val monetMode = if (LocalIsDark.current)
                                    ColorSchemeMode.MonetDark else ColorSchemeMode.MonetLight
                                val monetController = remember(monetMode) { MiuixThemeController(monetMode) }
                                MiuixTheme(controller = monetController) {
                                    MiuixNavigationBar {
                                        navItems.forEach { item ->
                                            MiuixNavigationBarItem(
                                                selected = selectedRoute == item.route,
                                                onClick = { selectedRoute = item.route },
                                                icon = item.icon,
                                                label = item.label
                                            )
                                        }
                                    }
                                }
                            } else {
                                MiuixNavigationBar {
                                    navItems.forEach { item ->
                                        MiuixNavigationBarItem(
                                            selected = selectedRoute == item.route,
                                            onClick = { selectedRoute = item.route },
                                            icon = item.icon,
                                            label = item.label
                                        )
                                    }
                                }
                            }
                        }
                        DockStyle.MD3 -> {
                            val md3Dark = LocalIsDark.current
                            val md3Ctx = androidx.compose.ui.platform.LocalContext.current
                            val monet = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                if (md3Dark) dynamicDarkColorScheme(md3Ctx) else dynamicLightColorScheme(md3Ctx)
                            } else null
                            val navItemColors = NavigationBarItemDefaults.colors(
                                selectedIconColor = monet?.onSecondaryContainer
                                    ?: MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedTextColor = monet?.onSurface
                                    ?: MaterialTheme.colorScheme.onSurface,
                                indicatorColor = monet?.secondaryContainer
                                    ?: MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = monet?.onSurfaceVariant
                                    ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = monet?.onSurfaceVariant
                                    ?: MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            NavigationBar(
                                containerColor = monet?.surfaceContainer
                                    ?: MaterialTheme.colorScheme.surfaceContainer,
                            ) {
                                navItems.forEach { item ->
                                    NavigationBarItem(
                                        selected = selectedRoute == item.route,
                                        onClick = { selectedRoute = item.route },
                                        icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label) },
                                        colors = navItemColors
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        ) { paddingValues: PaddingValues ->
            // Content area — secondary pages manage their own edge-to-edge insets
            // (bg painted behind status bar, content padded by windowInsetsPadding internally)
            val effectivePadding = when {
                isSecondaryPage -> PaddingValues(
                    top = 0.dp,      // secondary pages handle status bar themselves
                    bottom = 0.dp    // no dock bar on secondary pages
                )
                isGlassDock -> PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = 0.dp    // no bottom padding — content extends behind glass navbar
                )
                else -> paddingValues
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier)
                    .padding(effectivePadding)
            ) {
                when (selectedRoute) {
                    "home" -> HomeScreen(
                        moduleStatus = moduleStatus,
                        whitelistCount = whitelist.size,
                        templateCount = templates.size
                    )
                    "island" -> IslandControlScreen(
                        onShowIsland = { config -> islandManager.showIsland(config) },
                        onDismissIsland = { islandManager.dismissIsland() },
                        onPickImage = { imagePickerLauncher.launch("image/*") },
                        customImageUri = customImageUri,
                        onRestartClick = { showRestartDialog = true },
                    )
                    "templates" -> TemplatesScreen(
                        templates = templates,
                        onApplyTemplate = { config -> islandManager.showIsland(config) },
                        onSaveTemplate = { template ->
                            islandManager.saveTemplate(template)
                            templates = islandManager.getTemplates()
                        },
                        onDeleteTemplate = { id ->
                            islandManager.deleteTemplate(id)
                            templates = islandManager.getTemplates()
                        }
                    )
                    "settings" -> SettingsScreen(
                        isEnabled = isEnabled,
                        onEnabledChanged = { isEnabled = it; islandManager.isEnabled = it },
                        whitelist = whitelist,
                        onAddToWhitelist = { pkg ->
                            islandManager.addToWhitelist(pkg)
                            whitelist = islandManager.whitelist
                        },
                        onRemoveFromWhitelist = { pkg ->
                            islandManager.removeFromWhitelist(pkg)
                            whitelist = islandManager.whitelist
                        },
                        onResetSettings = {
                            scope.launch {
                                ConfigBackupHelper.resetAllConfig(context)
                                themeManager.load()
                                isEnabled = true
                                whitelist = emptySet()
                                templates = emptyList()
                                Toast.makeText(context, "Settings reset. Please restart.", Toast.LENGTH_LONG).show()
                            }
                        },
                        onNavigateToLogs = { selectedRoute = "logs" },
                        onExportConfig = {
                            scope.launch {
                                val file = ConfigBackupHelper.exportConfig(context)
                                val uri = FileProvider.getUriForFile(
                                    context, "${context.packageName}.fileprovider", file
                                )
                                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                    putExtra(android.content.Intent.EXTRA_SUBJECT, "HyperIsland Config Backup")
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Export config"))
                            }
                        },
                        onImportConfig = {
                            importConfigLauncher.launch("application/json")
                        },
                        onNavigateToCredits = { selectedRoute = "credits" },
                        onNavigateToAbout = { selectedRoute = "about" }
                    )
                    "logs" -> LogViewerScreen(
                        onBack = { selectedRoute = "settings" }
                    )
                    "credits" -> CreditsScreen(
                        onBack = { selectedRoute = "settings" }
                    )
                    "about" -> AboutScreen(
                        onBack = { selectedRoute = "settings" }
                    )
                }
            }
        }

        // Floating glass navbar overlay — renders above scaffold content (hidden on secondary pages)
        if (isGlassDock && !isSecondaryPage) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                LiquidGlassNavBar(
                    items = navItems,
                    selectedRoute = selectedRoute,
                    onItemSelected = { selectedRoute = it },
                    backdrop = backdrop,
                    glassBarAlpha = themeManager.glassBarAlpha,
                    glassSliderAlpha = themeManager.glassSliderAlpha,
                    accentOverride = if (themeManager.appTheme == AppTheme.MD3) {
                        MaterialTheme.colorScheme.primary
                    } else null,
                )
            }
        }
    }

    // ═══════════════ Restart System UI Confirmation Dialog ═══════════════
    // Hoisted to MainActivity so the restart button can live in both MiuixTopAppBar
    // (Miuix theme) and IslandControlScreen's fixed header (MD3/Glass themes).
    com.kangqi.hIc.ui.components.HyperAlertDialog(
        show = showRestartDialog,
        onDismissRequest = { showRestartDialog = false },
        title = {
            Text(
                "重启系统界面",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        },
        text = {
            Text(
                "需要 Root 权限。重启系统界面后超级岛的修改才会生效。是否继续？",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            com.kangqi.hIc.ui.components.HyperTextButton(
                text = "确认重启",
                onClick = {
                    showRestartDialog = false
                    try {
                        Runtime.getRuntime().exec(arrayOf("su", "-c", "killall com.android.systemui"))
                    } catch (_: Exception) { }
                }
            )
        },
        dismissButton = {
            com.kangqi.hIc.ui.components.HyperTextButton(
                text = "取消",
                onClick = { showRestartDialog = false },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
