package com.kangqi.hIc

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.core.content.FileProvider
import com.kangqi.hIc.log.HicLog
import com.kangqi.hIc.log.LogCollector
import com.kangqi.hIc.model.IslandConfig
import com.kangqi.hIc.model.ModuleStatus
import com.kangqi.hIc.service.IslandManager
import com.kangqi.hIc.ui.components.LiquidGlassNavBar
import com.kangqi.hIc.ui.components.NavItem
import com.kangqi.hIc.ui.screens.CreditsScreen
import com.kangqi.hIc.ui.screens.HomeScreen
import com.kangqi.hIc.ui.screens.IslandControlScreen
import com.kangqi.hIc.ui.screens.LogViewerScreen
import com.kangqi.hIc.ui.screens.SettingsScreen
import com.kangqi.hIc.ui.screens.TemplatesScreen
import com.kangqi.hIc.ui.theme.AppTheme
import com.kangqi.hIc.ui.theme.DockStyle
import com.kangqi.hIc.ui.theme.HyperIslandTheme
import com.kangqi.hIc.ui.theme.LocalThemeManager
import com.kangqi.hIc.ui.theme.ThemeManager
import com.kangqi.hIc.utils.ConfigBackupHelper
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.basic.TopAppBar as MiuixTopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState

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
        managerVersion = "v${com.kangqi.hic.preview.BuildConfig.VERSION_NAME} (${com.kangqi.hic.preview.BuildConfig.VERSION_CODE})"
    )

    val scrollBehavior = if (themeManager.appTheme == AppTheme.MIUIX) {
        MiuixScrollBehavior(rememberTopAppBarState())
    } else null

    val isSecondaryPage = selectedRoute == "logs" || selectedRoute == "credits"
    val isGlassDock = themeManager.dockStyle == DockStyle.LIQUID_GLASS

    // ── HorizontalPager state (SukiSU-Ultra pattern) ──
    val pagerState = rememberPagerState(pageCount = { 4 })

    // Sync pager swipe → route
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            if (!isSecondaryPage) {
                selectedRoute = navItems[page].route
            }
        }
    }

    // Sync nav tap → pager
    LaunchedEffect(selectedRoute) {
        if (!isSecondaryPage) {
            val targetPage = navItems.indexOfFirst { it.route == selectedRoute }
            if (targetPage >= 0 && targetPage != pagerState.currentPage) {
                pagerState.animateScrollToPage(targetPage)
            }
        }
    }

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
                            else -> "HyperIsland"
                        },
                        scrollBehavior = scrollBehavior
                    )
                }
            },
            bottomBar = {
                if (!isGlassDock && !isSecondaryPage) {
                    when (themeManager.dockStyle) {
                        DockStyle.MIUIX -> {
                            MiuixNavigationBar {
                                navItems.forEachIndexed { index, item ->
                                    MiuixNavigationBarItem(
                                        selected = pagerState.currentPage == index,
                                        onClick = {
                                            selectedRoute = item.route
                                            scope.launch { pagerState.animateScrollToPage(index) }
                                        },
                                        icon = item.icon,
                                        label = item.label
                                    )
                                }
                            }
                        }
                        DockStyle.MD3 -> {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            ) {
                                navItems.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = pagerState.currentPage == index,
                                        onClick = {
                                            selectedRoute = item.route
                                            scope.launch { pagerState.animateScrollToPage(index) }
                                        },
                                        icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label) }
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        ) { paddingValues: PaddingValues ->
            val effectivePadding = if (isGlassDock) {
                PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = 0.dp
                )
            } else {
                paddingValues
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier)
                    .padding(effectivePadding)
            ) {
                // ── Main content: HorizontalPager for swipe navigation ──
                if (!isSecondaryPage) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        beyondViewportPageCount = 1
                    ) { page ->
                        when (page) {
                            0 -> HomeScreen(
                                moduleStatus = moduleStatus,
                                whitelistCount = whitelist.size,
                                templateCount = templates.size
                            )
                            1 -> IslandControlScreen(
                                onShowIsland = { config -> islandManager.showIsland(config) },
                                onDismissIsland = { islandManager.dismissIsland() },
                                onPickImage = { imagePickerLauncher.launch("image/*") },
                                customImageUri = customImageUri,
                            )
                            2 -> TemplatesScreen(
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
                            3 -> SettingsScreen(
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
                                onNavigateToCredits = { selectedRoute = "credits" }
                            )
                        }
                    }
                }

                // ── Secondary pages overlay with animated transition ──
                AnimatedContent(
                    targetState = selectedRoute,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "secondary_page"
                ) { route ->
                    when (route) {
                        "logs" -> LogViewerScreen(
                            onBack = { selectedRoute = navItems[pagerState.currentPage].route }
                        )
                        "credits" -> CreditsScreen(
                            onBack = { selectedRoute = navItems[pagerState.currentPage].route }
                        )
                    }
                }
            }
        }

        // Floating glass navbar overlay
        if (isGlassDock && !isSecondaryPage) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                LiquidGlassNavBar(
                    items = navItems,
                    selectedRoute = selectedRoute,
                    onItemSelected = { route ->
                        selectedRoute = route
                        val targetPage = navItems.indexOfFirst { it.route == route }
                        if (targetPage >= 0) {
                            scope.launch { pagerState.animateScrollToPage(targetPage) }
                        }
                    },
                    backdrop = backdrop,
                    glassBarAlpha = themeManager.glassBarAlpha,
                    glassSliderAlpha = themeManager.glassSliderAlpha,
                )
            }
        }
    }
}
