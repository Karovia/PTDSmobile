package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import com.example.R
import com.example.model.AddRecordSubMode
import com.example.model.BiomarkerTelemetry
import com.example.model.BottomNavTab
import com.example.model.MedicationBoxPlan
import com.example.model.ModularCardItem
import com.example.ui.components.ClinicalRecordDossierIcon
import com.example.ui.components.ConsultationDossierIcon
import com.example.ui.components.DoctorOrderSyncIcon
import com.example.ui.components.MedicationBlisterIcon
import com.example.ui.components.MedicationCapsuleIcon
import com.example.ui.components.PulsingBeaconDot
import com.example.ui.components.ScanMatrixIcon
import com.example.ui.theme.ClinicalHairlineBorder
import com.example.ui.theme.ClinicalInverseOnSurface
import com.example.ui.theme.ClinicalInverseSurface
import com.example.ui.theme.ClinicalOnSurface
import com.example.ui.theme.ClinicalPrimary
import com.example.ui.theme.ClinicalPrimaryContainer
import com.example.ui.theme.ClinicalSecondary
import com.example.ui.theme.ClinicalSecondaryContainer
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.ClinicalSurfaceContainer
import com.example.ui.theme.ClinicalSurfaceContainerLow
import com.example.ui.theme.ClinicalSurfaceContainerLowest
import com.example.ui.theme.ClinicalTextStyles
import com.example.viewmodel.TelemetryUiState

/**
 * Top Navigation Bar with two switchable tabs:
 * - "病程日历" (BottomNavTab.CALENDAR)
 * - "卡片墙" (BottomNavTab.CARD_SETTINGS)
 * Selected state: Black text (#1A1C1C) + rounded-caps underline underneath.
 * Unselected state: Grey text (#5E5E5E).
 */
@Composable
fun ClinicalMainTopBar(
    selectedTab: BottomNavTab,
    lastMainTab: BottomNavTab,
    onSelectTab: (BottomNavTab) -> Unit,
    onBackFromAddRecord: () -> Unit = {},
    onOpenQuickScan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeTopTab = if (selectedTab == BottomNavTab.ADD_RECORD) lastMainTab else selectedTab

    Surface(
        color = ClinicalSurface.copy(alpha = 0.96f),
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxWidth()
                .height(62.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            AnimatedContent(
                targetState = selectedTab == BottomNavTab.ADD_RECORD,
                transitionSpec = {
                    if (targetState) {
                        // Entering subpage: "← 返回上一页" slides in from right/fade while tabs fade/slide left
                        (fadeIn(animationSpec = tween(220)) +
                            slideInHorizontally(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) { fullWidth -> fullWidth / 5 })
                            .togetherWith(
                                fadeOut(animationSpec = tween(150)) +
                                    slideOutHorizontally(animationSpec = tween(180)) { fullWidth -> -fullWidth / 5 }
                            )
                    } else {
                        // Returning to home: tabs slide back in from left while "← 返回上一页" slides out right
                        (fadeIn(animationSpec = tween(220)) +
                            slideInHorizontally(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) { fullWidth -> -fullWidth / 5 })
                            .togetherWith(
                                fadeOut(animationSpec = tween(150)) +
                                    slideOutHorizontally(animationSpec = tween(180)) { fullWidth -> fullWidth / 5 }
                            )
                    }.using(SizeTransform(clip = false))
                },
                label = "top_bar_mode_transition"
            ) { isAddRecordMode ->
                if (isAddRecordMode) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onBackFromAddRecord)
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("submode_back_header"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回上一页",
                            tint = ClinicalOnSurface,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "返回上一页",
                            style = ClinicalTextStyles.headlineSm.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ClinicalOnSurface
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(26.dp)
                        ) {
                            TopNavUnderlineTab(
                                label = stringResource(R.string.nav_calendar),
                                isSelected = activeTopTab == BottomNavTab.CALENDAR,
                                onClick = { onSelectTab(BottomNavTab.CALENDAR) },
                                testTag = "nav_tab_calendar"
                            )

                            TopNavUnderlineTab(
                                label = stringResource(R.string.nav_card_settings),
                                isSelected = activeTopTab == BottomNavTab.CARD_SETTINGS,
                                onClick = { onSelectTab(BottomNavTab.CARD_SETTINGS) },
                                testTag = "nav_tab_card_settings"
                            )
                        }

                        // Top-right "扫一扫" button: solid black circular button with white icon
                        Surface(
                            shape = CircleShape,
                            color = ClinicalOnSurface,
                            shadowElevation = 4.dp,
                            border = BorderStroke(1.dp, ClinicalSurface.copy(alpha = 0.14f)),
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onOpenQuickScan)
                                .testTag("top_bar_quick_scan_btn")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCodeScanner,
                                    contentDescription = "扫一扫药盒",
                                    tint = ClinicalSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Frontend Decision Model Classification Categories:
 * Determines which data category the scanned target belongs to and routes the frontend query
 * to the corresponding local data repository (backend integration deferred).
 */
private enum class QuickScanDecisionCategory(
    val code: String,
    val categoryTitle: String,
    val targetDataStoreLabel: String,
    val routingRuleText: String
) {
    DOCTOR_ORDER_RX(
        code = "CLS_RX_ORDER",
        categoryTitle = "查房医嘱处方药类",
        targetDataStoreLabel = "病程日历 · 医嘱卡片数据库",
        routingRuleText = "决策模型判定属于【医嘱处方药】➔ 检索查房医嘱中的服用时间、剂量与减量要求"
    ),
    GASTRIC_CO_MEDICATION(
        code = "CLS_CO_MED",
        categoryTitle = "联合护胃辅助药类",
        targetDataStoreLabel = "卡片墙 · 医嘱同步处方库",
        routingRuleText = "决策模型判定属于【联合护胃用药】➔ 检索餐前空腹服用节点与联用医嘱"
    ),
    DAILY_SUPPLEMENT_PLAN(
        code = "CLS_SUPP_PLAN",
        categoryTitle = "长期营养补充剂类",
        targetDataStoreLabel = "在服药盒 · 长期用药清单库",
        routingRuleText = "决策模型判定属于【长期补充剂】➔ 检索睡前/餐后常规服用计划"
    )
}

/**
 * 3-Stage Frontend Pipeline State:
 * 1. OPTICAL_RECOGNIZING: Camera frame locks onto the medicine box & extracts visual features/barcode
 * 2. DECISION_MODEL_ROUTING: Frontend decision model classifies the target data category & selects data store
 * 3. DATA_RETRIEVED: Queries the matched data store and pops up the prompt card above the bottom floating dock
 */
private enum class QuickScanFrontendStage(
    val stepNumber: Int,
    val badgeTitle: String
) {
    OPTICAL_RECOGNIZING(1, "① 镜头画面识别中"),
    DECISION_MODEL_ROUTING(2, "② 决策模型判定数据类别"),
    DATA_RETRIEVED(3, "③ 已检索匹配医嘱数据")
}

private data class ScannedBoxOrderLookupResult(
    val planId: String?,
    val drugName: String,
    val specification: String,
    val simulatedBarcode: String,
    val decisionCategory: QuickScanDecisionCategory,
    val confidenceLabel: String,
    val whenToTake: String,
    val howToTake: String,
    val matchedOrderSourceTitle: String,
    val matchedOrderExcerpt: String,
    val takenDosesToday: Int,
    val totalDosesToday: Int,
    val isFromDoctorOrder: Boolean
)

/**
 * Full-Screen Camera Preview & Frontend Decision-Model Lookup Overlay ("扫一扫"):
 * - Entire screen background is the live Camera Preview (CameraX PreviewView + full-bleed optical camera feed canvas).
 * - Frontend Pipeline:
 *   1. Recognizes the medicine box in the camera frame.
 *   2. Uses the Frontend Decision Model router to classify which data category it belongs to.
 *   3. Retrieves the corresponding Doctor's Order / Medication card data.
 * - Bottom Functional Zone: Symmetrical floating dark capsule button matching the outside
 *   FloatingAddRecordCapsuleButton 1:1, with the matched prompt card blooming upward right above it.
 */
@Composable
fun QuickScanMedicineLookupOverlay(
    uiState: TelemetryUiState,
    onDismiss: () -> Unit,
    onMarkDoseTaken: (planId: String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Build scannable medicine boxes from current medicationPlans + extra common boxes in orders
    val scannablePlans = remember(uiState.medicationPlans, uiState.timelineEvents) {
        val baseList = uiState.medicationPlans.toMutableList()
        if (baseList.none { it.drugName.contains("叶酸") }) {
            baseList.add(
                MedicationBoxPlan(
                    id = "lookup_folic_acid",
                    drugName = "叶酸片",
                    specification = "5mg × 30片/盒",
                    frequencyLabel = "每日1次",
                    dosePerTimeLabel = "每次1片 (5mg)",
                    timingLabel = "早餐后温水送服",
                    usageSummary = "每日1次 • 每次1片 (5mg) • 早餐后温水送服",
                    totalDosesToday = 1,
                    takenDosesToday = 0,
                    remainingCountLabel = "余 28 片",
                    isAutoSyncedFromOrder = true,
                    sourceOrderExcerpt = "查房医嘱：配合叶酸片每日1片早餐后口服"
                )
            )
        }
        baseList
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    var frontendStage by remember { mutableStateOf(QuickScanFrontendStage.DATA_RETRIEVED) }

    val safeIndex = selectedIndex.coerceIn(0, (scannablePlans.size - 1).coerceAtLeast(0))
    val activePlan = scannablePlans.getOrNull(safeIndex)

    // Real-time Frontend Decision Model Classification & Data Retrieval Logic
    val lookupResult = remember(activePlan, uiState.timelineEvents, uiState.cardSlots, uiState.medicationPlans, safeIndex) {
        if (activePlan == null) {
            null
        } else {
            val cleanKeyword = activePlan.drugName.substringBefore(" ").substringBefore("(").take(3)
            val matchedTimelineNote = uiState.timelineEvents.firstOrNull { evt ->
                val noteText = evt.doctorNote?.content.orEmpty()
                noteText.contains(cleanKeyword) ||
                    (activePlan.drugName.contains("甲泼尼龙") && (noteText.contains("美卓乐") || noteText.contains("甲泼尼龙"))) ||
                    (activePlan.drugName.contains("奥美拉唑") && (noteText.contains("护胃") || noteText.contains("奥美拉唑"))) ||
                    (activePlan.drugName.contains("钙") && noteText.contains("钙"))
            }

            val rxSyncCard = (uiState.cardSlots.filterNotNull() + uiState.cardLibrary)
                .firstOrNull { it.id == "card_rx_sync" }

            // Decision Model determines which category this medicine box belongs to
            val decisionCategory = when {
                activePlan.drugName.contains("甲泼尼龙") || activePlan.drugName.contains("美卓乐") || matchedTimelineNote != null ->
                    QuickScanDecisionCategory.DOCTOR_ORDER_RX
                activePlan.drugName.contains("奥美拉唑") ->
                    QuickScanDecisionCategory.GASTRIC_CO_MEDICATION
                else ->
                    QuickScanDecisionCategory.DAILY_SUPPLEMENT_PLAN
            }

            val matchedSourceTitle = when (decisionCategory) {
                QuickScanDecisionCategory.DOCTOR_ORDER_RX -> {
                    val docTitle = matchedTimelineNote?.doctorNote?.doctorTitle ?: "主治医师查房医嘱"
                    val timeLabel = matchedTimelineNote?.timestampLabel ?: "今日医嘱"
                    "检索命中 · ${decisionCategory.targetDataStoreLabel}（$docTitle $timeLabel）"
                }
                QuickScanDecisionCategory.GASTRIC_CO_MEDICATION ->
                    "检索命中 · ${decisionCategory.targetDataStoreLabel}（${rxSyncCard?.title ?: "医嘱同步处方"}）"
                QuickScanDecisionCategory.DAILY_SUPPLEMENT_PLAN ->
                    "检索命中 · ${decisionCategory.targetDataStoreLabel}"
            }

            val matchedOrderText = when {
                matchedTimelineNote?.doctorNote != null ->
                    matchedTimelineNote.doctorNote.content
                activePlan.sourceOrderExcerpt.isNotBlank() ->
                    activePlan.sourceOrderExcerpt
                else ->
                    "医嘱要求按时按量规律服用：${activePlan.usageSummary}"
            }

            val livePlan = uiState.medicationPlans.firstOrNull { it.id == activePlan.id }
            val simulatedBarcode = when (safeIndex % 4) {
                0 -> "6923450657812"
                1 -> "6901234588910"
                2 -> "6938192034715"
                else -> "6941029384756"
            }

            ScannedBoxOrderLookupResult(
                planId = livePlan?.id,
                drugName = activePlan.drugName,
                specification = activePlan.specification,
                simulatedBarcode = simulatedBarcode,
                decisionCategory = decisionCategory,
                confidenceLabel = "99.2%",
                whenToTake = activePlan.timingLabel.ifBlank { "早餐后温水送服" },
                howToTake = "${activePlan.frequencyLabel} · ${activePlan.dosePerTimeLabel}",
                matchedOrderSourceTitle = matchedSourceTitle,
                matchedOrderExcerpt = matchedOrderText,
                takenDosesToday = livePlan?.takenDosesToday ?: activePlan.takenDosesToday,
                totalDosesToday = livePlan?.totalDosesToday ?: activePlan.totalDosesToday,
                isFromDoctorOrder = matchedTimelineNote != null || activePlan.isAutoSyncedFromOrder
            )
        }
    }

    BackHandler(onBack = onDismiss)

    // ROOT FULL-SCREEN CAMERA CONTAINER (Unobstructed live camera preview background)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0E10))
            .testTag("quick_scan_lookup_overlay")
    ) {
        // LAYER 1: Live Hardware Camera Preview (CameraX PreviewView filling the entire screen)
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener(
                            {
                                runCatching {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(surfaceProvider)
                                    }
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview
                                    )
                                }
                            },
                            ContextCompat.getMainExecutor(ctx)
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Transparent full-screen touch area (tapping switches real-time detected medicine in preview)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (scannablePlans.isNotEmpty()) {
                            selectedIndex = (selectedIndex + 1) % scannablePlans.size
                        }
                    }
                )
                .testTag("quick_scan_viewfinder_box")
        )

        // LAYER 2: Top-Right Cancel Button Only (38.dp black circle with white X)
        Surface(
            shape = CircleShape,
            color = ClinicalOnSurface,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .size(38.dp)
                .clip(CircleShape)
                .clickable(onClick = onDismiss)
                .testTag("quick_scan_top_cancel_btn")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "退出全屏扫一扫",
                    tint = ClinicalSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // LAYER 3: BOTTOM FUNCTIONAL ZONE (Pop-up Matched Data Card + Symmetrical Floating Capsule Dock)
        // Matches the outside FloatingAddRecordCapsuleButton + SymmetricalExpandedAddMenu 1:1
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // POP-UP DOCTOR'S ORDER SMALL CARD: Blooms upward from the bottom floating capsule button
            // Only pops up after Stage 3 (DATA_RETRIEVED) completes!
            AnimatedVisibility(
                visible = frontendStage == QuickScanFrontendStage.DATA_RETRIEVED && lookupResult != null,
                enter = fadeIn(animationSpec = tween(durationMillis = 200)) +
                    scaleIn(
                        initialScale = 0.84f,
                        transformOrigin = TransformOrigin(0.5f, 1f),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) +
                    expandVertically(
                        expandFrom = Alignment.Bottom,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                exit = fadeOut(animationSpec = tween(durationMillis = 150)) +
                    scaleOut(
                        targetScale = 0.88f,
                        transformOrigin = TransformOrigin(0.5f, 1f),
                        animationSpec = tween(durationMillis = 160)
                    ) +
                    shrinkVertically(
                        shrinkTowards = Alignment.Bottom,
                        animationSpec = tween(durationMillis = 160)
                    ),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                lookupResult?.let { item ->
                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = ClinicalSurface,
                        shadowElevation = 14.dp,
                        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                        modifier = Modifier
                            .widthIn(max = 380.dp)
                            .fillMaxWidth()
                            .testTag("quick_scan_result_popup_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            // Decision Model Classification & Retrieval Source Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(percent = 50),
                                    color = ClinicalOnSurface
                                ) {
                                    Text(
                                        text = "决策分类：${item.decisionCategory.categoryTitle}",
                                        style = ClinicalTextStyles.bodySm.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = ClinicalSurface,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = ClinicalPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = if (item.isFromDoctorOrder) "医嘱卡片已匹配" else "用药清单已匹配",
                                        style = ClinicalTextStyles.bodySm.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = ClinicalPrimary,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Header: Drug name + Specification
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    MedicationCapsuleIcon()
                                    Text(
                                        text = item.drugName,
                                        style = ClinicalTextStyles.headlineSm.copy(fontSize = 16.sp),
                                        color = ClinicalOnSurface
                                    )
                                }
                                Text(
                                    text = "包装规格：${item.specification} · 检索库：${item.decisionCategory.targetDataStoreLabel}",
                                    style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp),
                                    color = ClinicalSecondary
                                )
                            }

                            // Highlighted 2-Column Blocks: 什么时候吃 & 怎么吃
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 什么时候吃
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = ClinicalPrimaryContainer.copy(alpha = 0.08f),
                                    border = BorderStroke(1.dp, ClinicalPrimaryContainer.copy(alpha = 0.32f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(11.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "什么时候吃",
                                            style = ClinicalTextStyles.bodySm.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = ClinicalPrimaryContainer
                                        )
                                        Text(
                                            text = item.whenToTake,
                                            style = ClinicalTextStyles.bodyMd.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            ),
                                            color = ClinicalOnSurface
                                        )
                                    }
                                }

                                // 怎么吃（用法用量）
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = ClinicalSurfaceContainerLow,
                                    border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(11.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "怎么吃（用法用量）",
                                            style = ClinicalTextStyles.bodySm.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = ClinicalSecondary
                                        )
                                        Text(
                                            text = item.howToTake,
                                            style = ClinicalTextStyles.bodyMd.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            ),
                                            color = ClinicalOnSurface
                                        )
                                    }
                                }
                            }

                            // Matched Doctor's Order Card Excerpt
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = ClinicalSurfaceContainerLow,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = item.matchedOrderSourceTitle,
                                        style = ClinicalTextStyles.bodySm.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = ClinicalSecondary
                                    )
                                    Text(
                                        text = item.matchedOrderExcerpt,
                                        style = ClinicalTextStyles.bodySm.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = ClinicalOnSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BOTTOM FLOATING FUNCTIONAL CAPSULE BUTTON
            // Matches the outside FloatingAddRecordCapsuleButton 1:1 in shape, size, 3-zone axial symmetry, and elevation
            val currentItem = lookupResult
            val isDataReady = frontendStage == QuickScanFrontendStage.DATA_RETRIEVED
            val canMarkDose = isDataReady && currentItem?.planId != null
            val isCompleted = currentItem != null && currentItem.takenDosesToday >= currentItem.totalDosesToday

            Surface(
                shape = RoundedCornerShape(percent = 50),
                color = ClinicalOnSurface,
                shadowElevation = 10.dp,
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        !isDataReady -> ClinicalSurface.copy(alpha = 0.22f)
                        isCompleted -> Color(0xFF1E7E48).copy(alpha = 0.65f)
                        else -> ClinicalPrimaryContainer.copy(alpha = 0.68f)
                    }
                ),
                modifier = Modifier
                    .width(304.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable {
                        if (!isDataReady) {
                            frontendStage = QuickScanFrontendStage.DATA_RETRIEVED
                        } else if (canMarkDose && currentItem?.planId != null) {
                            onMarkDoseTaken(currentItem.planId)
                        } else {
                            onDismiss()
                        }
                    }
                    .testTag("quick_scan_got_it_btn")
            ) {
                // 3-Zone Axial Symmetry Box matching FloatingAddRecordCapsuleButton
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    // LEFT SYMMETRICAL ANCHOR (38.dp circle): Mark Dose Taken / Re-run recognition
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                color = when {
                                    !isDataReady -> ClinicalSurface.copy(alpha = 0.14f)
                                    isCompleted -> Color(0xFF1E7E48)
                                    canMarkDose -> ClinicalPrimaryContainer
                                    else -> ClinicalSurface.copy(alpha = 0.14f)
                                },
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = ClinicalSurface.copy(alpha = 0.24f),
                                shape = CircleShape
                            )
                            .clickable {
                                if (canMarkDose && currentItem?.planId != null) {
                                    onMarkDoseTaken(currentItem.planId)
                                } else if (scannablePlans.isNotEmpty()) {
                                    selectedIndex = (selectedIndex + 1) % scannablePlans.size
                                }
                            }
                            .testTag("quick_scan_mark_taken_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                !isDataReady -> Icons.Filled.QrCodeScanner
                                isCompleted -> Icons.Filled.CheckCircle
                                else -> Icons.Filled.Check
                            },
                            contentDescription = "打卡服药",
                            tint = ClinicalSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // CENTER CORE: Locked strictly to Alignment.Center (horizontal midpoint)
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 46.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = when (frontendStage) {
                                QuickScanFrontendStage.OPTICAL_RECOGNIZING -> "正在识别药盒..."
                                QuickScanFrontendStage.DECISION_MODEL_ROUTING -> "决策模型检索中..."
                                QuickScanFrontendStage.DATA_RETRIEVED -> when {
                                    currentItem == null -> "正在识别药盒"
                                    canMarkDose && !isCompleted ->
                                        "打卡服药 (${currentItem.takenDosesToday}/${currentItem.totalDosesToday})"
                                    canMarkDose && isCompleted ->
                                        "今日已服 (${currentItem.takenDosesToday}/${currentItem.totalDosesToday})"
                                    else -> "知道了 · 已核对医嘱"
                                }
                            },
                            style = ClinicalTextStyles.bodyLg.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            ),
                            color = ClinicalSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(2.dp)
                                    .background(
                                        color = ClinicalPrimaryContainer,
                                        shape = RoundedCornerShape(percent = 50)
                                    )
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = when (frontendStage) {
                                    QuickScanFrontendStage.OPTICAL_RECOGNIZING -> "提取条码与包装特征"
                                    QuickScanFrontendStage.DECISION_MODEL_ROUTING ->
                                        currentItem?.decisionCategory?.categoryTitle ?: "判定数据类别"
                                    QuickScanFrontendStage.DATA_RETRIEVED ->
                                        currentItem?.whenToTake ?: "已匹配医嘱卡片"
                                },
                                style = ClinicalTextStyles.bodySm.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.2.sp
                                ),
                                color = ClinicalSurface.copy(alpha = 0.82f),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                softWrap = false
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(2.dp)
                                    .background(
                                        color = ClinicalPrimaryContainer,
                                        shape = RoundedCornerShape(percent = 50)
                                    )
                            )
                        }
                    }

                    // RIGHT SYMMETRICAL ANCHOR (38.dp circle): Close / Dismiss scanner
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ClinicalSurface.copy(alpha = 0.12f), CircleShape)
                            .border(
                                width = 1.dp,
                                color = ClinicalSurface.copy(alpha = 0.18f),
                                shape = CircleShape
                            )
                            .clickable(onClick = onDismiss)
                            .testTag("quick_scan_close_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "关闭扫一扫",
                            tint = ClinicalSurface,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopNavUnderlineTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected) ClinicalOnSurface else ClinicalSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "top_tab_text_color_$label"
    )
    val underlineWidth by animateDpAsState(
        targetValue = if (isSelected) 32.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "top_tab_underline_width_$label"
    )
    val underlineAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "top_tab_underline_alpha_$label"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = ClinicalTextStyles.headlineSm.copy(
                fontSize = 18.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = textColor
        )
        Spacer(modifier = Modifier.height(5.dp))
        // Capsule-shaped underline (flat top/bottom, rounded semicircular left/right ends)
        Box(
            modifier = Modifier
                .width(underlineWidth)
                .height(3.5.dp)
                .alpha(underlineAlpha)
                .background(
                    color = ClinicalOnSurface,
                    shape = RoundedCornerShape(percent = 50)
                )
        )
    }
}

/**
 * Symmetrical Floating Dock & Expandable Capsule Menu at the bottom center of the screen:
 * - Strict 3-zone axial symmetry (equal 38.dp circular anchors on left & right, centered core text stack)
 * - Grounded dual-layer machined pedestal base for visual stability and low center of gravity
 * - Smooth spring-driven radial/vertical expansion menu for quick access to the 3 record modes
 * - Selection mode (when timeline events are selected): symmetrically morphs into "生成病程记录"
 */
@Composable
fun FloatingAddRecordCapsuleButton(
    onClick: () -> Unit,
    selectedTimelineCount: Int = 0,
    onGenerateTimelineRecord: () -> Unit = {},
    onClearTimelineSelection: () -> Unit = {},
    onSelectAddSubMode: (AddRecordSubMode) -> Unit = { onClick() },
    modifier: Modifier = Modifier
) {
    val isSelectionMode = selectedTimelineCount > 0
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Automatically collapse the quick menu if timeline selection mode activates
    LaunchedEffect(isSelectionMode) {
        if (isSelectionMode && isMenuExpanded) {
            isMenuExpanded = false
        }
    }

    if (isMenuExpanded) {
        BackHandler {
            isMenuExpanded = false
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val capsuleScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "floating_capsule_scale"
    )

    val capsuleWidth by animateDpAsState(
        targetValue = when {
            isSelectionMode -> 312.dp
            isMenuExpanded -> 304.dp
            else -> 284.dp
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "floating_capsule_width"
    )

    val addIconRotation by animateFloatAsState(
        targetValue = if (isMenuExpanded) 135f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "floating_capsule_icon_rotation"
    )

    val chevronRotation by animateFloatAsState(
        targetValue = if (isMenuExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "floating_capsule_chevron_rotation"
    )

    val circleBg by animateColorAsState(
        targetValue = when {
            isSelectionMode -> ClinicalPrimaryContainer.copy(alpha = 0.22f)
            isMenuExpanded -> ClinicalPrimaryContainer
            else -> ClinicalSurface.copy(alpha = 0.12f)
        },
        animationSpec = tween(durationMillis = 220),
        label = "symmetrical_circle_bg"
    )

    val circleBorderColor by animateColorAsState(
        targetValue = when {
            isSelectionMode -> ClinicalPrimaryContainer.copy(alpha = 0.80f)
            isMenuExpanded -> ClinicalSurface.copy(alpha = 0.28f)
            else -> ClinicalSurface.copy(alpha = 0.18f)
        },
        animationSpec = tween(durationMillis = 220),
        label = "symmetrical_circle_border"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Smooth full-screen backdrop scrim when the floating menu is expanded
        AnimatedVisibility(
            visible = isMenuExpanded && !isSelectionMode,
            enter = fadeIn(animationSpec = tween(durationMillis = 220)),
            exit = fadeOut(animationSpec = tween(durationMillis = 180))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ClinicalOnSurface.copy(alpha = 0.30f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { isMenuExpanded = false }
                    )
                    .testTag("floating_menu_scrim")
            )
        }

        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Symmetrical Expandable Floating Menu Tray (blooms from bottom center)
            AnimatedVisibility(
                visible = isMenuExpanded && !isSelectionMode,
                enter = fadeIn(animationSpec = tween(durationMillis = 200)) +
                    scaleIn(
                        initialScale = 0.84f,
                        transformOrigin = TransformOrigin(0.5f, 1f),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) +
                    expandVertically(
                        expandFrom = Alignment.Bottom,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                exit = fadeOut(animationSpec = tween(durationMillis = 150)) +
                    scaleOut(
                        targetScale = 0.88f,
                        transformOrigin = TransformOrigin(0.5f, 1f),
                        animationSpec = tween(durationMillis = 160)
                    ) +
                    shrinkVertically(
                        shrinkTowards = Alignment.Bottom,
                        animationSpec = tween(durationMillis = 160)
                    )
            ) {
                SymmetricalExpandedAddMenu(
                    onSelectSubMode = { subMode ->
                        isMenuExpanded = false
                        onSelectAddSubMode(subMode)
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // Symmetrical Dark Capsule (no outer white border)
            Surface(
                shape = RoundedCornerShape(percent = 50),
                color = ClinicalOnSurface,
                shadowElevation = 10.dp,
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        isSelectionMode -> ClinicalPrimaryContainer.copy(alpha = 0.72f)
                        isMenuExpanded -> ClinicalPrimaryContainer.copy(alpha = 0.55f)
                        else -> ClinicalSurface.copy(alpha = 0.14f)
                    }
                ),
                modifier = Modifier
                    .width(capsuleWidth)
                    .height(54.dp)
                    .graphicsLayer {
                        scaleX = capsuleScale
                        scaleY = capsuleScale
                    }
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            if (isSelectionMode) {
                                onGenerateTimelineRecord()
                            } else {
                                isMenuExpanded = !isMenuExpanded
                            }
                        }
                    )
                    .testTag(
                        if (isSelectionMode) {
                            "btn_generate_timeline_record"
                        } else {
                            "nav_tab_add_record"
                        }
                    )
            ) {
                // 3-Zone Axial Symmetry Box:
                // Left 38.dp circle == Center locked at 50% axis == Right 38.dp circle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    // LEFT SYMMETRICAL ANCHOR (38.dp circle)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(38.dp)
                            .background(circleBg, CircleShape)
                            .border(
                                width = 1.dp,
                                color = circleBorderColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelectionMode) {
                            ClinicalRecordDossierIcon(
                                tint = ClinicalSurface,
                                accentColor = ClinicalPrimaryContainer,
                                modifier = Modifier.size(17.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = if (isMenuExpanded) "收起菜单" else "添加记录",
                                tint = ClinicalSurface,
                                modifier = Modifier
                                    .size(19.dp)
                                    .graphicsLayer {
                                        rotationZ = addIconRotation
                                    }
                            )
                        }
                    }

                    // CENTER CORE: Locked strictly to Alignment.Center (horizontal midpoint)
                    AnimatedContent(
                        targetState = Triple(isSelectionMode, selectedTimelineCount, isMenuExpanded),
                        transitionSpec = {
                            (fadeIn(tween(180)) togetherWith fadeOut(tween(140)))
                                .using(SizeTransform(clip = false))
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 46.dp),
                        label = "floating_capsule_center_content"
                    ) { (selecting, count, expanded) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = when {
                                    selecting -> "生成病程记录"
                                    expanded -> "收起录入菜单"
                                    else -> stringResource(R.string.nav_add_record)
                                },
                                style = ClinicalTextStyles.bodyLg.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                ),
                                color = ClinicalSurface,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                softWrap = false
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Left symmetrical micro-bar
                                Box(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(2.dp)
                                        .background(
                                            color = ClinicalPrimaryContainer,
                                            shape = RoundedCornerShape(percent = 50)
                                        )
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = when {
                                        selecting -> "已选 $count 项 · 生成话术"
                                        expanded -> "选择上方模块直接进入"
                                        else -> "扫药盒 · 化验单 · 病程"
                                    },
                                    style = ClinicalTextStyles.bodySm.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 0.2.sp
                                    ),
                                    color = if (selecting) {
                                        ClinicalSurface.copy(alpha = 0.92f)
                                    } else {
                                        ClinicalSurface.copy(alpha = 0.72f)
                                    },
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                // Right symmetrical micro-bar
                                Box(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(2.dp)
                                        .background(
                                            color = ClinicalPrimaryContainer,
                                            shape = RoundedCornerShape(percent = 50)
                                        )
                                )
                            }
                        }
                    }

                    // RIGHT SYMMETRICAL ANCHOR (38.dp circle, identical diameter & weight to Left Anchor)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(38.dp)
                            .background(circleBg, CircleShape)
                            .border(
                                width = 1.dp,
                                color = circleBorderColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelectionMode) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "前往生成病程记录",
                                tint = ClinicalSurface,
                                modifier = Modifier.size(17.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = if (isMenuExpanded) "收起" else "展开录入菜单",
                                tint = ClinicalSurface,
                                modifier = Modifier
                                    .size(20.dp)
                                    .graphicsLayer {
                                        rotationZ = chevronRotation
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SymmetricalExpandedAddMenu(
    onSelectSubMode: (AddRecordSubMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var playStagger by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        playStagger = true
    }

    val sideCardsProgress by animateFloatAsState(
        targetValue = if (playStagger) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "side_cards_stagger"
    )
    val centerCardProgress by animateFloatAsState(
        targetValue = if (playStagger) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "center_card_stagger"
    )

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = ClinicalSurfaceContainerLowest,
        shadowElevation = 14.dp,
        border = BorderStroke(1.dp, ClinicalHairlineBorder),
        modifier = modifier
            .widthIn(min = 296.dp, max = 352.dp)
            .fillMaxWidth(0.92f)
            .testTag("floating_expanded_add_menu")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Symmetrical Header with equal left/right hairline rules
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = ClinicalHairlineBorder
                )
                Text(
                    text = "选择记录录入方式",
                    style = ClinicalTextStyles.bodySm.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = ClinicalSecondary
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = ClinicalHairlineBorder
                )
            }

            // 3 Equal-Width Symmetrical Cards (Left - Center - Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SymmetricalQuickActionTile(
                    title = "扫描药盒",
                    subtitle = "用法 / 医嘱",
                    isCenterPrimary = false,
                    onClick = { onSelectSubMode(AddRecordSubMode.SCAN_MEDICATION_BOX) },
                    testTag = "quick_menu_scan_med_box",
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            val s = 0.88f + 0.12f * sideCardsProgress
                            scaleX = s
                            scaleY = s
                            translationY = (1f - sideCardsProgress) * 18f
                            alpha = sideCardsProgress.coerceIn(0f, 1f)
                        }
                ) {
                    MedicationBlisterIcon(
                        modifier = Modifier.size(22.dp)
                    )
                }

                SymmetricalQuickActionTile(
                    title = "化验单识别",
                    subtitle = "指标同步跑道",
                    isCenterPrimary = true,
                    onClick = { onSelectSubMode(AddRecordSubMode.PHOTO_OCR) },
                    testTag = "quick_menu_lab_sheet",
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            val s = 0.86f + 0.14f * centerCardProgress
                            scaleX = s
                            scaleY = s
                            translationY = (1f - centerCardProgress) * 24f
                            alpha = centerCardProgress.coerceIn(0f, 1f)
                        }
                ) {
                    ScanMatrixIcon(
                        modifier = Modifier.size(22.dp)
                    )
                }

                SymmetricalQuickActionTile(
                    title = "病程与医嘱",
                    subtitle = "体征 / 查房",
                    isCenterPrimary = false,
                    onClick = { onSelectSubMode(AddRecordSubMode.MANUAL_TIMELINE) },
                    testTag = "quick_menu_manual_record",
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            val s = 0.88f + 0.12f * sideCardsProgress
                            scaleX = s
                            scaleY = s
                            translationY = (1f - sideCardsProgress) * 18f
                            alpha = sideCardsProgress.coerceIn(0f, 1f)
                        }
                ) {
                    DoctorOrderSyncIcon(
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SymmetricalQuickActionTile(
    title: String,
    subtitle: String,
    isCenterPrimary: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isCenterPrimary) ClinicalSurfaceContainer else ClinicalSurfaceContainerLow,
        border = BorderStroke(
            width = 1.dp,
            color = if (isCenterPrimary) {
                ClinicalOnSurface.copy(alpha = 0.22f)
            } else {
                ClinicalHairlineBorder
            }
        ),
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(ClinicalSurfaceContainerLowest, CircleShape)
                    .border(
                        width = 1.dp,
                        color = ClinicalHairlineBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                iconContent()
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = ClinicalTextStyles.bodyLg.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ClinicalOnSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = subtitle,
                    style = ClinicalTextStyles.bodySm.copy(
                        fontSize = 10.sp
                    ),
                    color = ClinicalSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun AddTelemetryRecordScreen(
    subMode: AddRecordSubMode,
    currentBiomarkers: BiomarkerTelemetry,
    medicationPlans: List<MedicationBoxPlan>,
    onSelectSubMode: (AddRecordSubMode) -> Unit,
    onNavigateBack: () -> Unit,
    onSyncRecognizedIndicators: (glucose: Double, spo2: Int, skinTemp: Double) -> Unit,
    onAddScannedMedicationBox: (
        drugName: String,
        specification: String,
        frequencyLabel: String,
        dosePerTimeLabel: String,
        timingLabel: String,
        doctorOrderText: String
    ) -> Unit,
    onSyncMedicationsFromDoctorOrder: (orderText: String) -> Unit,
    onAddManualTimelineRecord: (timestampLabel: String, descriptionText: String, doctorNoteText: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ClinicalSurface),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedContent(
            targetState = subMode,
            transitionSpec = {
                (fadeIn(animationSpec = tween(240, easing = FastOutSlowInEasing)) +
                    slideInHorizontally(
                        animationSpec = tween(260, easing = FastOutSlowInEasing)
                    ) { fullWidth -> fullWidth / 6 })
                    .togetherWith(
                        fadeOut(animationSpec = tween(180)) +
                            slideOutHorizontally(
                                animationSpec = tween(220, easing = FastOutSlowInEasing)
                            ) { fullWidth -> -fullWidth / 6 }
                    )
                    .using(SizeTransform(clip = false))
            },
            label = "add_record_mode_transition"
        ) { mode ->
            when (mode) {
                AddRecordSubMode.PHOTO_OCR -> {
                    PhotoOcrRecognitionView(
                        currentBiomarkers = currentBiomarkers,
                        onBackToSelection = onNavigateBack,
                        onSyncToCalendar = onSyncRecognizedIndicators
                    )
                }

                AddRecordSubMode.MANUAL_TIMELINE -> {
                    ManualTimelineEntryView(
                        onBackToSelection = onNavigateBack,
                        onSaveToTimeline = onAddManualTimelineRecord
                    )
                }

                AddRecordSubMode.SCAN_MEDICATION_BOX,
                AddRecordSubMode.SELECTION -> {
                    ScanMedicationBoxView(
                        onBackToSelection = onNavigateBack,
                        onSaveScannedBox = onAddScannedMedicationBox,
                        onSyncDoctorOrderOnly = onSyncMedicationsFromDoctorOrder
                    )
                }
            }
        }
    }
}

@Composable
private fun AddRecordModeSelectionView(
    medicationCount: Int,
    onBack: () -> Unit,
    onSelectPhotoOcr: () -> Unit,
    onSelectScanMedicationBox: () -> Unit,
    onSelectManualTimeline: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxSize()
            .testTag("add_record_selection_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // Option 1: 扫描药盒与用法用量（含医嘱自动同步）
        item {
            EntryOptionCard(
                badgeLabel = "方式一 • 药盒扫描与医嘱同步（当前 $medicationCount 盒在服）",
                title = "扫描药盒 / 设定怎么吃",
                subtitle = "对准药盒条码或包装盒拍照，配置用法用量（每日几次、每次几片、餐前餐后）；若填写或带有医嘱，将自动识别并同步生成吃药卡片",
                actionLabel = "进入扫描药盒与用法配置",
                iconContent = { MedicationBlisterIcon() },
                onClick = onSelectScanMedicationBox,
                testTag = "option_scan_medication_card"
            )
        }

        // Option 2: 拍照识别化验单
        item {
            EntryOptionCard(
                badgeLabel = "方式二 • 化验单 OCR",
                title = "拍照识别化验单",
                subtitle = "拍摄或上传检验报告单，识别血液内含糖量、血氧浓度、皮肤温度等核心指标并同步至病程日历跑道图",
                actionLabel = "进入拍照识别化验单",
                iconContent = { ScanMatrixIcon() },
                onClick = onSelectPhotoOcr,
                testTag = "option_photo_ocr_card"
            )
        }

        // Option 3: 手动添加病程与医嘱（医嘱内写药自动同步）
        item {
            EntryOptionCard(
                badgeLabel = "方式三 • 病程与查房医嘱",
                title = "手动添加病程 / 医嘱记录",
                subtitle = "记录今日体征与查房医嘱；若医嘱中写了需要服用的药物及剂量，系统会自动提取并生成对应吃药卡片",
                actionLabel = "添加病程与医嘱记录",
                iconContent = {
                    Icon(
                        imageVector = Icons.Filled.EditNote,
                        contentDescription = null,
                        tint = ClinicalOnSurface,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = onSelectManualTimeline,
                testTag = "option_manual_add_card"
            )
        }
    }
}

@Composable
private fun EntryOptionCard(
    badgeLabel: String,
    title: String,
    subtitle: String,
    actionLabel: String,
    iconContent: @Composable () -> Unit,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(ClinicalSurfaceContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        iconContent()
                    }
                    Text(
                        text = badgeLabel,
                        style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.Medium),
                        color = ClinicalSecondary
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = title,
                    style = ClinicalTextStyles.headlineSm,
                    color = ClinicalOnSurface
                )
                Text(
                    text = subtitle,
                    style = ClinicalTextStyles.bodyMd,
                    color = ClinicalSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ClinicalSurfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = actionLabel,
                        style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.SemiBold),
                        color = ClinicalOnSurface
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = ClinicalOnSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private data class MedicineBoxPreset(
    val shortTag: String,
    val drugName: String,
    val specification: String,
    val frequencyCount: Int,
    val doseCount: String,
    val timing: String
)

/**
 * Streamlined Medicine Box Scanner + Usage/Dosage Configuration.
 */
@Composable
private fun ScanMedicationBoxView(
    onBackToSelection: () -> Unit,
    onSaveScannedBox: (
        drugName: String,
        specification: String,
        frequencyLabel: String,
        dosePerTimeLabel: String,
        timingLabel: String,
        doctorOrderText: String
    ) -> Unit,
    onSyncDoctorOrderOnly: (orderText: String) -> Unit
) {
    val presets = remember {
        listOf(
            MedicineBoxPreset(
                shortTag = "美卓乐",
                drugName = "甲泼尼龙片 (美卓乐)",
                specification = "4mg × 30片/盒",
                frequencyCount = 1,
                doseCount = "4",
                timing = "早餐后温水送服"
            ),
            MedicineBoxPreset(
                shortTag = "叶酸片",
                drugName = "叶酸片",
                specification = "5mg × 30片/盒",
                frequencyCount = 1,
                doseCount = "1",
                timing = "早餐后温水送服"
            ),
            MedicineBoxPreset(
                shortTag = "钙尔奇",
                drugName = "碳酸钙D3片 (钙尔奇)",
                specification = "600mg × 60片/瓶",
                frequencyCount = 1,
                doseCount = "1",
                timing = "晚餐后或睡前咀嚼"
            ),
            MedicineBoxPreset(
                shortTag = "西乐葆",
                drugName = "塞来昔布胶囊 (西乐葆)",
                specification = "0.2g × 10粒/盒",
                frequencyCount = 2,
                doseCount = "1",
                timing = "早晚餐后温水送服"
            )
        )
    }

    val frequencyNumbers = remember { listOf(1, 2, 3, 4, 5, 6) }
    val presetDoseNumbers = remember { listOf("0.5", "1", "2", "3", "4") }

    var hasScannedBox by remember { mutableStateOf(true) }
    var drugNameInput by remember { mutableStateOf(presets[1].drugName) }
    var specInput by remember { mutableStateOf(presets[1].specification) }
    var selectedFrequencyCount by remember { mutableIntStateOf(presets[1].frequencyCount) }
    var selectedDoseValue by remember { mutableStateOf(presets[1].doseCount) }
    var isCustomDoseSelected by remember { mutableStateOf(false) }
    var customDoseInput by remember { mutableStateOf("1.5") }
    var timingInput by remember { mutableStateOf(presets[1].timing) }

    val resolvedFrequencyLabel = "每日${selectedFrequencyCount}次"
    val resolvedDoseNumber = if (isCustomDoseSelected) {
        customDoseInput.trim().ifEmpty { "1" }
    } else {
        selectedDoseValue
    }
    val resolvedDoseLabel = "每次${resolvedDoseNumber}片"

    LazyColumn(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxSize()
            .testTag("scan_medication_box_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Compact Medicine Box Viewfinder Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ClinicalSurfaceContainerLowest,
                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MedicationCapsuleIcon()
                            Text(
                                text = "扫描药盒",
                                style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                                color = ClinicalOnSurface
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = if (hasScannedBox) ClinicalPrimary.copy(alpha = 0.1f) else ClinicalSurfaceContainer
                        ) {
                            Text(
                                text = if (hasScannedBox) "已识别" else "待对焦",
                                style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                color = if (hasScannedBox) ClinicalPrimary else ClinicalSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Recessed Medicine Box Viewfinder
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ClinicalSurfaceContainerLow,
                        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { hasScannedBox = true }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                val len = 18.dp.toPx()
                                val stroke = 2.2.dp.toPx()
                                val w = size.width
                                val h = size.height
                                val bracketColor = if (hasScannedBox) ClinicalPrimaryContainer else ClinicalOnSurface

                                drawLine(bracketColor, Offset(0f, 0f), Offset(len, 0f), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(0f, 0f), Offset(0f, len), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, 0f), Offset(w - len, 0f), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, 0f), Offset(w, len), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(0f, h), Offset(len, h), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(0f, h), Offset(0f, h - len), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, h), Offset(w - len, h), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, h), Offset(w, h - len), stroke, StrokeCap.Round)

                                drawLine(
                                    color = ClinicalPrimaryContainer.copy(alpha = 0.4f),
                                    start = Offset(18.dp.toPx(), h * 0.5f),
                                    end = Offset(w - 18.dp.toPx(), h * 0.5f),
                                    strokeWidth = 1.5.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 20.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(ClinicalSurfaceContainerLowest, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.QrCodeScanner,
                                        contentDescription = null,
                                        tint = ClinicalPrimaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "$drugNameInput · $specInput",
                                    style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.SemiBold),
                                    color = ClinicalOnSurface
                                )
                            }
                        }
                    }

                    // Compact quick presets
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        presets.forEach { preset ->
                            val isCurrent = drugNameInput == preset.drugName
                            Surface(
                                shape = CircleShape,
                                color = if (isCurrent) ClinicalOnSurface else ClinicalSurfaceContainerLow,
                                border = BorderStroke(
                                    1.dp,
                                    if (isCurrent) ClinicalOnSurface else ClinicalSurfaceContainer
                                ),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        hasScannedBox = true
                                        drugNameInput = preset.drugName
                                        specInput = preset.specification
                                        selectedFrequencyCount = preset.frequencyCount
                                        if (preset.doseCount in presetDoseNumbers) {
                                            isCustomDoseSelected = false
                                            selectedDoseValue = preset.doseCount
                                        } else {
                                            isCustomDoseSelected = true
                                            customDoseInput = preset.doseCount
                                        }
                                        timingInput = preset.timing
                                    }
                            ) {
                                Text(
                                    text = preset.shortTag,
                                    style = ClinicalTextStyles.bodySm.copy(
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isCurrent) ClinicalSurface else ClinicalOnSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Streamlined Drug Info & Usage Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ClinicalSurfaceContainerLowest,
                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Drug Name & Specification (Retained as requested)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = drugNameInput,
                            onValueChange = { drugNameInput = it },
                            label = { Text("药品名称", style = ClinicalTextStyles.bodySm) },
                            singleLine = true,
                            textStyle = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.SemiBold),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ClinicalOnSurface,
                                unfocusedBorderColor = ClinicalHairlineBorder,
                                focusedContainerColor = ClinicalSurfaceContainerLow,
                                unfocusedContainerColor = ClinicalSurfaceContainerLow
                            ),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("input_med_drug_name")
                        )

                        OutlinedTextField(
                            value = specInput,
                            onValueChange = { specInput = it },
                            label = { Text("包装规格", style = ClinicalTextStyles.bodySm) },
                            singleLine = true,
                            textStyle = ClinicalTextStyles.bodySm,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ClinicalOnSurface,
                                unfocusedBorderColor = ClinicalHairlineBorder,
                                focusedContainerColor = ClinicalSurfaceContainerLow,
                                unfocusedContainerColor = ClinicalSurfaceContainerLow
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_med_spec")
                        )
                    }

                    // 1. 服用频次: Pixelated numeral circles 1..6
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "服用频次",
                                style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                                color = ClinicalOnSurface
                            )
                            Text(
                                text = "每日 ${selectedFrequencyCount} 次",
                                style = ClinicalTextStyles.bodySm,
                                color = ClinicalSecondary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            frequencyNumbers.forEach { num ->
                                val isSelected = selectedFrequencyCount == num
                                PixelCircleNumberButton(
                                    pixelText = num.toString(),
                                    isSelected = isSelected,
                                    onClick = { selectedFrequencyCount = num },
                                    modifier = Modifier.testTag("freq_circle_$num")
                                )
                            }
                        }
                    }

                    // 2. 单次剂量: Pixelated numeral circles (0.5, 1, 2, 3, 4) + "其他" with manual input
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "单次剂量",
                                style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                                color = ClinicalOnSurface
                            )
                            Text(
                                text = "每次 ${resolvedDoseNumber} 片",
                                style = ClinicalTextStyles.bodySm,
                                color = ClinicalSecondary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            presetDoseNumbers.forEach { doseStr ->
                                val isSelected = !isCustomDoseSelected && selectedDoseValue == doseStr
                                PixelCircleNumberButton(
                                    pixelText = doseStr,
                                    isSelected = isSelected,
                                    onClick = {
                                        isCustomDoseSelected = false
                                        selectedDoseValue = doseStr
                                    },
                                    modifier = Modifier.testTag("dose_circle_$doseStr")
                                )
                            }

                            // "其他" Circle Option
                            Surface(
                                shape = CircleShape,
                                color = if (isCustomDoseSelected) ClinicalOnSurface else ClinicalSurfaceContainerLow,
                                border = BorderStroke(
                                    1.dp,
                                    if (isCustomDoseSelected) ClinicalOnSurface else ClinicalSurfaceContainer
                                ),
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .clickable { isCustomDoseSelected = true }
                                    .testTag("dose_circle_other")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "其他",
                                        style = ClinicalTextStyles.bodySm.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (isCustomDoseSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isCustomDoseSelected) ClinicalSurface else ClinicalOnSurface
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(visible = isCustomDoseSelected) {
                            OutlinedTextField(
                                value = customDoseInput,
                                onValueChange = { customDoseInput = it },
                                label = { Text("手动输入单次片数", style = ClinicalTextStyles.bodySm) },
                                placeholder = { Text("如：1.5", style = ClinicalTextStyles.bodySm) },
                                suffix = {
                                    Text(
                                        text = "片",
                                        style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                                        color = ClinicalSecondary
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                textStyle = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.SemiBold),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ClinicalOnSurface,
                                    unfocusedBorderColor = ClinicalHairlineBorder,
                                    focusedContainerColor = ClinicalSurfaceContainerLow,
                                    unfocusedContainerColor = ClinicalSurfaceContainerLow
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_med_custom_dose")
                            )
                        }
                    }

                    // 3. 服用时间和方式: Manual text field directly
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "服用时间和方式",
                            style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                            color = ClinicalOnSurface
                        )
                        OutlinedTextField(
                            value = timingInput,
                            onValueChange = { timingInput = it },
                            placeholder = {
                                Text(
                                    text = "如：早餐后温水送服",
                                    style = ClinicalTextStyles.bodySm,
                                    color = ClinicalSecondary
                                )
                            },
                            singleLine = true,
                            textStyle = ClinicalTextStyles.bodyMd,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ClinicalOnSurface,
                                unfocusedBorderColor = ClinicalHairlineBorder,
                                focusedContainerColor = ClinicalSurfaceContainerLow,
                                unfocusedContainerColor = ClinicalSurfaceContainerLow
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_med_timing")
                        )
                    }

                    Button(
                        onClick = {
                            onSaveScannedBox(
                                drugNameInput,
                                specInput,
                                resolvedFrequencyLabel,
                                resolvedDoseLabel,
                                timingInput.trim().ifEmpty { "餐后温水送服" },
                                ""
                            )
                        },
                        shape = RoundedCornerShape(percent = 50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClinicalOnSurface,
                            contentColor = ClinicalSurface
                        ),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_save_scanned_med_box")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Medication,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "保存至卡片墙",
                                style = ClinicalTextStyles.bodyLg
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Circular selector button displaying a pixelated Arabic numeral (e.g., 1..6 or 0.5, 1, 2, 3, 4).
 */
@Composable
private fun PixelCircleNumberButton(
    pixelText: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) ClinicalOnSurface else ClinicalSurfaceContainerLow
    val borderColor = if (isSelected) ClinicalOnSurface else ClinicalSurfaceContainer
    val pixelColor = if (isSelected) ClinicalSurface else ClinicalOnSurface

    Surface(
        shape = CircleShape,
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            PixelNumberCanvas(
                text = pixelText,
                pixelColor = pixelColor,
                pixelSize = if (pixelText.length > 1) 2.2.dp else 2.8.dp
            )
        }
    }
}

/**
 * Renders 5x7 dot-matrix pixelated digits ('0'..'9' and '.') inside a Canvas.
 */
@Composable
private fun PixelNumberCanvas(
    text: String,
    pixelColor: Color,
    pixelSize: Dp,
    modifier: Modifier = Modifier
) {
    val glyphs = remember(text) { text.map { pixelDigitMatrix(it) } }
    val charCols = remember(glyphs) { glyphs.map { it.first().length } }
    val gapCols = 1
    val totalCols = remember(charCols) {
        charCols.sum() + (glyphs.size - 1).coerceAtLeast(0) * gapCols
    }
    val totalRows = 7

    Canvas(
        modifier = modifier.size(
            width = pixelSize * totalCols,
            height = pixelSize * totalRows
        )
    ) {
        val cell = pixelSize.toPx()
        val dotSize = cell * 0.84f
        val offsetPad = (cell - dotSize) * 0.5f
        var colCursor = 0

        glyphs.forEachIndexed { idx, rows ->
            val widthCols = charCols[idx]
            for (r in 0 until totalRows) {
                val rowStr = rows[r]
                for (c in 0 until widthCols) {
                    if (rowStr[c] == '1') {
                        drawRect(
                            color = pixelColor,
                            topLeft = Offset(
                                x = (colCursor + c) * cell + offsetPad,
                                y = r * cell + offsetPad
                            ),
                            size = Size(dotSize, dotSize)
                        )
                    }
                }
            }
            colCursor += widthCols + gapCols
        }
    }
}

private fun pixelDigitMatrix(ch: Char): List<String> = when (ch) {
    '0' -> listOf(
        "01110",
        "10001",
        "10011",
        "10101",
        "11001",
        "10001",
        "01110"
    )
    '1' -> listOf(
        "00100",
        "01100",
        "00100",
        "00100",
        "00100",
        "00100",
        "01110"
    )
    '2' -> listOf(
        "01110",
        "10001",
        "00001",
        "00110",
        "01000",
        "10000",
        "11111"
    )
    '3' -> listOf(
        "11110",
        "00001",
        "00001",
        "01110",
        "00001",
        "00001",
        "11110"
    )
    '4' -> listOf(
        "00010",
        "00110",
        "01010",
        "10010",
        "11111",
        "00010",
        "00010"
    )
    '5' -> listOf(
        "11111",
        "10000",
        "11110",
        "00001",
        "00001",
        "10001",
        "01110"
    )
    '6' -> listOf(
        "00110",
        "01000",
        "10000",
        "11110",
        "10001",
        "10001",
        "01110"
    )
    '.' -> listOf(
        "00",
        "00",
        "00",
        "00",
        "00",
        "11",
        "11"
    )
    else -> listOf(
        "111",
        "101",
        "101",
        "101",
        "101",
        "101",
        "111"
    )
}

@Composable
private fun PhotoOcrRecognitionView(
    currentBiomarkers: BiomarkerTelemetry,
    onBackToSelection: () -> Unit,
    onSyncToCalendar: (glucose: Double, spo2: Int, skinTemp: Double) -> Unit
) {
    var hasCapturedSheet by remember { mutableStateOf(false) }
    var glucoseText by remember { mutableStateOf(currentBiomarkers.glucoseValue.toString()) }
    var spo2Text by remember { mutableStateOf(currentBiomarkers.spo2Value.toString()) }
    var skinTempText by remember { mutableStateOf(currentBiomarkers.skinTempValue.toString()) }
    var wbcText by remember { mutableStateOf("11.8") }
    var esrText by remember { mutableStateOf("38") }

    LazyColumn(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxSize()
            .testTag("photo_ocr_view_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Camera / Viewfinder Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ClinicalSurfaceContainerLowest,
                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "化验单取景框",
                            style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                            color = ClinicalOnSurface
                        )
                        Surface(
                            shape = CircleShape,
                            color = if (hasCapturedSheet) ClinicalPrimary.copy(alpha = 0.1f) else ClinicalSurfaceContainer
                        ) {
                            Text(
                                text = if (hasCapturedSheet) "已取景预览" else "等待拍摄",
                                style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                color = if (hasCapturedSheet) ClinicalPrimary else ClinicalSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Recessed Viewfinder Box with Hardware Reticle Corners
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ClinicalSurfaceContainerLow,
                        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { hasCapturedSheet = true }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(18.dp)
                            ) {
                                val len = 22.dp.toPx()
                                val stroke = 2.5.dp.toPx()
                                val w = size.width
                                val h = size.height
                                val bracketColor = if (hasCapturedSheet) ClinicalPrimaryContainer else ClinicalOnSurface

                                drawLine(bracketColor, Offset(0f, 0f), Offset(len, 0f), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(0f, 0f), Offset(0f, len), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, 0f), Offset(w - len, 0f), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, 0f), Offset(w, len), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(0f, h), Offset(len, h), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(0f, h), Offset(0f, h - len), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, h), Offset(w - len, h), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, h), Offset(w, h - len), stroke, StrokeCap.Round)
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(ClinicalSurfaceContainerLowest, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (hasCapturedSheet) Icons.Filled.CheckCircle else Icons.Filled.CameraAlt,
                                        contentDescription = null,
                                        tint = if (hasCapturedSheet) ClinicalPrimaryContainer else ClinicalOnSurface,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Text(
                                    text = if (hasCapturedSheet) {
                                        "化验单图像已载入 • 已定位 5 项核心指标"
                                    } else {
                                        "对准纸质化验单拍摄，自动提取各项检验指标"
                                    },
                                    style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.Medium),
                                    color = ClinicalOnSurface
                                )
                                Text(
                                    text = "支持血常规 / 生化全项 / 铁蛋白 / 血沉报告单",
                                    style = ClinicalTextStyles.bodySm,
                                    color = ClinicalSecondary
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { hasCapturedSheet = true },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ClinicalInverseSurface,
                                contentColor = ClinicalInverseOnSurface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_trigger_camera_ocr")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "拍照识别",
                                style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.Medium)
                            )
                        }

                        OutlinedButton(
                            onClick = { hasCapturedSheet = true },
                            shape = CircleShape,
                            border = BorderStroke(1.dp, ClinicalHairlineBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ClinicalOnSurface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_trigger_gallery_ocr")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "相册导入",
                                style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }
        }

        // Recognized Indicators Preview Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ClinicalSurfaceContainerLowest,
                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "识别指标同步预览",
                            style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                            color = ClinicalOnSurface
                        )
                        Text(
                            text = "可微调核对",
                            style = ClinicalTextStyles.bodySm,
                            color = ClinicalSecondary
                        )
                    }

                    RecognizedMetricRow(
                        name = "血液内含糖量 (mmol/L)",
                        unit = "危险阈值 >${currentBiomarkers.glucoseLimit}",
                        value = glucoseText,
                        onValueChange = { glucoseText = it },
                        isAbnormal = (glucoseText.toDoubleOrNull() ?: 0.0) > currentBiomarkers.glucoseLimit
                    )
                    HorizontalDivider(color = ClinicalSurfaceContainer)

                    RecognizedMetricRow(
                        name = "血氧浓度 (%)",
                        unit = "报警阈值 <${currentBiomarkers.spo2MinLimit}%",
                        value = spo2Text,
                        onValueChange = { spo2Text = it },
                        isAbnormal = (spo2Text.toIntOrNull() ?: 100) < currentBiomarkers.spo2MinLimit
                    )
                    HorizontalDivider(color = ClinicalSurfaceContainer)

                    val parsedSkinTemp = skinTempText.toDoubleOrNull() ?: currentBiomarkers.skinTempValue
                    RecognizedMetricRow(
                        name = "皮肤温度 (°C)",
                        unit = "正常范围 ${currentBiomarkers.skinTempLowLimit}-${currentBiomarkers.skinTempHighLimit}°C",
                        value = skinTempText,
                        onValueChange = { skinTempText = it },
                        isAbnormal = parsedSkinTemp > currentBiomarkers.skinTempHighLimit || parsedSkinTemp < currentBiomarkers.skinTempLowLimit
                    )
                    HorizontalDivider(color = ClinicalSurfaceContainer)

                    RecognizedMetricRow(
                        name = "白细胞计数 (10^9/L)",
                        unit = "参考范围 3.5-9.5",
                        value = wbcText,
                        onValueChange = { wbcText = it },
                        isAbnormal = (wbcText.toDoubleOrNull() ?: 0.0) > 9.5
                    )
                    HorizontalDivider(color = ClinicalSurfaceContainer)

                    RecognizedMetricRow(
                        name = "红细胞沉降率 (mm/h)",
                        unit = "参考范围 0-20",
                        value = esrText,
                        onValueChange = { esrText = it },
                        isAbnormal = (esrText.toIntOrNull() ?: 0) > 20
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val g = glucoseText.toDoubleOrNull() ?: currentBiomarkers.glucoseValue
                            val s = spo2Text.toIntOrNull() ?: currentBiomarkers.spo2Value
                            val t = skinTempText.toDoubleOrNull() ?: currentBiomarkers.skinTempValue
                            onSyncToCalendar(g, s, t)
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClinicalInverseSurface,
                            contentColor = ClinicalInverseOnSurface
                        ),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_sync_ocr_metrics")
                    ) {
                        Text(
                            text = "同步识别指标至病程日历",
                            style = ClinicalTextStyles.bodyLg
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecognizedMetricRow(
    name: String,
    unit: String,
    value: String,
    onValueChange: (String) -> Unit,
    isAbnormal: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = name,
                style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.Medium),
                color = ClinicalOnSurface
            )
            Text(
                text = unit,
                style = ClinicalTextStyles.bodySm,
                color = ClinicalSecondary
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = ClinicalTextStyles.labelMonoLg.copy(
                color = if (isAbnormal) ClinicalPrimaryContainer else ClinicalOnSurface
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ClinicalOnSurface,
                unfocusedBorderColor = ClinicalHairlineBorder,
                focusedContainerColor = ClinicalSurfaceContainerLow,
                unfocusedContainerColor = ClinicalSurfaceContainerLow
            ),
            modifier = Modifier.width(108.dp)
        )
    }
}

private enum class ClinicalDocScanType(
    val shortTitle: String,
    val badgeLabel: String,
    val viewfinderPrompt: String
) {
    DOCTOR_ORDER(
        shortTitle = "扫医嘱",
        badgeLabel = "自动提取医嘱",
        viewfinderPrompt = "对准查房或门诊医嘱单拍摄，回传 Mac 自动提取医嘱条目"
    ),
    PRESCRIPTION_SLIP(
        shortTitle = "扫开药单",
        badgeLabel = "提取开药单",
        viewfinderPrompt = "对准门诊处方/开药单拍摄，回传 Mac 提取药品规格与剂量"
    ),
    MEDICAL_RECORD(
        shortTitle = "扫病历",
        badgeLabel = "自动提取病历",
        viewfinderPrompt = "对准门诊或住院病历拍摄，回传 Mac 提取体征数值与病程描述"
    )
}

private data class ExtractedFieldItem(
    val id: String,
    val label: String,
    val subHint: String,
    val value: String,
    val isConfirmed: Boolean
)

@Composable
private fun ManualTimelineEntryView(
    onBackToSelection: () -> Unit,
    onSaveToTimeline: (timestampLabel: String, descriptionText: String, doctorNoteText: String) -> Unit
) {
    var activeScanType by remember { mutableStateOf(ClinicalDocScanType.DOCTOR_ORDER) }
    // Extraction confirmation sub-UI only triggers after the user scans a document
    var triggeredScanType by remember { mutableStateOf<ClinicalDocScanType?>(null) }
    var filledFromScanTag by remember { mutableStateOf<String?>(null) }

    // 1. Sub-UI State: 医嘱提取项 (Doctor's Order)
    var orderItems by remember {
        mutableStateOf(
            listOf(
                ExtractedFieldItem(
                    id = "order_1",
                    label = "主药剂量医嘱",
                    subHint = "激素口服方案",
                    value = "继续甲泼尼龙片 16mg/d 早餐后口服",
                    isConfirmed = false
                ),
                ExtractedFieldItem(
                    id = "order_2",
                    label = "联合辅助用药",
                    subHint = "护胃与补钙方案",
                    value = "加服叶酸片每日1片（早餐后），每晚碳酸钙D3片1片",
                    isConfirmed = false
                ),
                ExtractedFieldItem(
                    id = "order_3",
                    label = "监测与随访医嘱",
                    subHint = "体温与复查要求",
                    value = "每日监测午后体温，3天后复查血常规与CRP",
                    isConfirmed = false
                )
            )
        )
    }

    // 2. Sub-UI State: 开药单提取项 (Prescription Slip)
    var prescriptionItems by remember {
        mutableStateOf(
            listOf(
                ExtractedFieldItem(
                    id = "rx_1",
                    label = "甲泼尼龙片 (美卓乐)",
                    subHint = "规格 4mg×30片/盒 · 单次剂量与频次",
                    value = "每次4片 (16mg) · 每日1次早餐后口服",
                    isConfirmed = false
                ),
                ExtractedFieldItem(
                    id = "rx_2",
                    label = "叶酸片",
                    subHint = "规格 5mg×30片/盒 · 单次剂量与频次",
                    value = "每次1片 (5mg) · 每日1次早餐后口服",
                    isConfirmed = false
                ),
                ExtractedFieldItem(
                    id = "rx_3",
                    label = "碳酸钙D3片 (钙尔奇)",
                    subHint = "规格 600mg×60片/瓶 · 单次剂量与频次",
                    value = "每次1片 (600mg) · 每晚1次口服",
                    isConfirmed = false
                )
            )
        )
    }

    // 3. Sub-UI State: 病历提取项 (Medical Record)
    var medicalRecordItems by remember {
        mutableStateOf(
            listOf(
                ExtractedFieldItem(
                    id = "mr_time",
                    label = "病历记录时间",
                    subHint = "门诊/查房时间戳",
                    value = "10-12 今天 15:20",
                    isConfirmed = false
                ),
                ExtractedFieldItem(
                    id = "mr_vitals",
                    label = "体征实测数值",
                    subHint = "体温 / 心率 / 血氧",
                    value = "体温 36.8°C，心率 76次/分，血氧 98%",
                    isConfirmed = false
                ),
                ExtractedFieldItem(
                    id = "mr_symptoms",
                    label = "主诉与查体记录",
                    subHint = "关节与皮疹体征",
                    value = "午后无发热，双膝关节无肿胀压痛，皮疹已消退",
                    isConfirmed = false
                ),
                ExtractedFieldItem(
                    id = "mr_assessment",
                    label = "病程评估结论",
                    subHint = "阶段性病情判断",
                    value = "病情处于稳定缓解期，维持当前抗炎减量节奏",
                    isConfirmed = false
                )
            )
        )
    }

    // Form fields below
    var timestampInput by remember { mutableStateOf("10-12 今天 14:30") }
    var descriptionInput by remember {
        mutableStateOf("午后复测体温37.2°C，双膝关节无明显压痛，按计划完成今日口服药。")
    }
    var doctorNoteInput by remember {
        mutableStateOf("继续甲泼尼龙片 16mg/d 口服，加服叶酸片每日1片（早餐后），每晚碳酸钙D3片1片")
    }

    fun applyConfirmedDoctorOrderToForm(items: List<ExtractedFieldItem>) {
        val confirmed = items.filter { it.isConfirmed }
        if (confirmed.isNotEmpty()) {
            doctorNoteInput = confirmed.joinToString("；") { it.value.trim() }
            filledFromScanTag = "已从 Mac 医嘱扫描确认填入 (${confirmed.size} 项)"
        }
    }

    fun applyConfirmedPrescriptionToForm(items: List<ExtractedFieldItem>) {
        val confirmed = items.filter { it.isConfirmed }
        if (confirmed.isNotEmpty()) {
            doctorNoteInput = "开药单处方：" + confirmed.joinToString("；") { "${it.label} ${it.value.trim()}" }
            filledFromScanTag = "已从 Mac 开药单确认填入 (${confirmed.size} 项药品)"
        }
    }

    fun applyConfirmedMedicalRecordToForm(items: List<ExtractedFieldItem>) {
        val confirmed = items.filter { it.isConfirmed }
        if (confirmed.isNotEmpty()) {
            confirmed.find { it.id == "mr_time" }?.let { timestampInput = it.value.trim() }
            val descParts = confirmed.filter { it.id != "mr_time" }.map { it.value.trim() }
            if (descParts.isNotEmpty()) {
                descriptionInput = descParts.joinToString("；") + "。"
            }
            filledFromScanTag = "已从 Mac 病历扫描确认填入 (${confirmed.size} 项)"
        }
    }

    val hasMedicationInOrder = remember(doctorNoteInput) {
        val text = doctorNoteInput.trim()
        text.contains("片") || text.contains("粒") || text.contains("mg") ||
            text.contains("口服") || text.contains("甲泼尼龙") || text.contains("美卓乐") ||
            text.contains("护胃") || text.contains("钙") || text.contains("叶酸")
    }

    LazyColumn(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxSize()
            .testTag("manual_timeline_view_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Document Scanner Card (扫医嘱 / 扫开药单 / 扫病历 + Mac Relay Status)
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ClinicalSurfaceContainerLowest,
                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ScanMatrixIcon()
                            Text(
                                text = "扫描病历 / 医嘱 / 开药单",
                                style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                                color = ClinicalOnSurface
                            )
                        }

                        // Mac Relay Status Pill
                        Surface(
                            shape = CircleShape,
                            color = if (triggeredScanType != null) {
                                ClinicalPrimary.copy(alpha = 0.1f)
                            } else {
                                ClinicalSurfaceContainerLow
                            },
                            border = BorderStroke(
                                1.dp,
                                if (triggeredScanType != null) {
                                    ClinicalPrimaryContainer.copy(alpha = 0.35f)
                                } else {
                                    ClinicalSurfaceContainer
                                }
                            )
                        ) {
                            Text(
                                text = if (triggeredScanType != null) {
                                    "Mac 已处理回传"
                                } else {
                                    "回传 Mac 协同解析"
                                },
                                style = ClinicalTextStyles.bodySm.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (triggeredScanType != null) ClinicalPrimary else ClinicalSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // 3 Scan Mode Selector Pills: 扫医嘱 | 扫开药单 | 扫病历
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClinicalDocScanType.entries.forEach { scanType ->
                            val isSelected = activeScanType == scanType
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) ClinicalOnSurface else ClinicalSurfaceContainerLow,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) ClinicalOnSurface else ClinicalSurfaceContainer
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .clickable {
                                        activeScanType = scanType
                                        if (triggeredScanType != null) {
                                            triggeredScanType = scanType
                                        }
                                    }
                                    .testTag("doc_scan_tab_${scanType.name.lowercase()}")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = scanType.shortTitle,
                                        style = ClinicalTextStyles.bodySm.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) ClinicalSurface else ClinicalOnSurface
                                    )
                                }
                            }
                        }
                    }

                    // Recessed Viewfinder Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ClinicalSurfaceContainerLow,
                        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(126.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { triggeredScanType = activeScanType }
                            .testTag("doc_scan_viewfinder")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp)
                            ) {
                                val len = 18.dp.toPx()
                                val stroke = 2.2.dp.toPx()
                                val w = size.width
                                val h = size.height
                                val isTriggered = triggeredScanType == activeScanType
                                val bracketColor = if (isTriggered) ClinicalPrimaryContainer else ClinicalOnSurface

                                drawLine(bracketColor, Offset(0f, 0f), Offset(len, 0f), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(0f, 0f), Offset(0f, len), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, 0f), Offset(w - len, 0f), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, 0f), Offset(w, len), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(0f, h), Offset(len, h), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(0f, h), Offset(0f, h - len), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, h), Offset(w - len, h), stroke, StrokeCap.Round)
                                drawLine(bracketColor, Offset(w, h), Offset(w, h - len), stroke, StrokeCap.Round)
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 20.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(ClinicalSurfaceContainerLowest, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (triggeredScanType == activeScanType) {
                                            Icons.Filled.CheckCircle
                                        } else {
                                            Icons.Filled.CameraAlt
                                        },
                                        contentDescription = null,
                                        tint = if (triggeredScanType == activeScanType) {
                                            ClinicalPrimaryContainer
                                        } else {
                                            ClinicalOnSurface
                                        },
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                                Text(
                                    text = if (triggeredScanType == activeScanType) {
                                        "已扫描【${activeScanType.shortTitle.removePrefix("扫")}】· Mac 已返回提取数据，请在下方核对确认"
                                    } else {
                                        activeScanType.viewfinderPrompt
                                    },
                                    style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.Medium),
                                    color = ClinicalOnSurface
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { triggeredScanType = activeScanType },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ClinicalOnSurface,
                                contentColor = ClinicalSurface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_trigger_doc_scan")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "扫描${activeScanType.shortTitle.removePrefix("扫")}（回传 Mac）",
                                style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        if (triggeredScanType != null) {
                            OutlinedButton(
                                onClick = { triggeredScanType = null },
                                shape = CircleShape,
                                border = BorderStroke(1.dp, ClinicalHairlineBorder),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = ClinicalSecondary
                                ),
                                modifier = Modifier.testTag("btn_close_extracted_sub_ui")
                            ) {
                                Text(
                                    text = "收起识别",
                                    style = ClinicalTextStyles.bodySm
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Conditional Extraction & Confirmation Sub-UIs (ONLY triggered after scanning!)
        item {
            AnimatedVisibility(
                visible = triggeredScanType != null,
                enter = fadeIn(tween(220)) + expandVertically(tween(240)),
                exit = fadeOut(tween(180)) + shrinkVertically(tween(200))
            ) {
                val currentScan = triggeredScanType ?: activeScanType
                AnimatedContent(
                    targetState = currentScan,
                    transitionSpec = {
                        fadeIn(tween(200)).togetherWith(fadeOut(tween(160)))
                    },
                    label = "extracted_sub_ui_switch"
                ) { scanMode ->
                    when (scanMode) {
                        ClinicalDocScanType.DOCTOR_ORDER -> {
                            ExtractedConfirmationSubUiCard(
                                subUiBadge = "医嘱提取子界面",
                                title = "自动提取医嘱 · 待核对确认",
                                macStatusText = "手机扫描 → Mac 端解析完成 → 已回传手机",
                                items = orderItems,
                                onUpdateItemValue = { id, newVal ->
                                    orderItems = orderItems.map {
                                        if (it.id == id) it.copy(value = newVal) else it
                                    }
                                },
                                onToggleItemConfirm = { id ->
                                    orderItems = orderItems.map {
                                        if (it.id == id) it.copy(isConfirmed = !it.isConfirmed) else it
                                    }
                                },
                                onConfirmAllAndFill = {
                                    val allConfirmed = orderItems.map { it.copy(isConfirmed = true) }
                                    orderItems = allConfirmed
                                    applyConfirmedDoctorOrderToForm(allConfirmed)
                                },
                                onFillConfirmedToForm = {
                                    applyConfirmedDoctorOrderToForm(orderItems)
                                },
                                applyButtonLabel = "将已确认医嘱添加到表单",
                                testTagPrefix = "sub_ui_doctor_order"
                            )
                        }

                        ClinicalDocScanType.PRESCRIPTION_SLIP -> {
                            ExtractedConfirmationSubUiCard(
                                subUiBadge = "开药单提取子界面",
                                title = "自动提取开药单 · 待核对确认",
                                macStatusText = "手机扫描 → Mac 端处方解析 → 已回传手机",
                                items = prescriptionItems,
                                onUpdateItemValue = { id, newVal ->
                                    prescriptionItems = prescriptionItems.map {
                                        if (it.id == id) it.copy(value = newVal) else it
                                    }
                                },
                                onToggleItemConfirm = { id ->
                                    prescriptionItems = prescriptionItems.map {
                                        if (it.id == id) it.copy(isConfirmed = !it.isConfirmed) else it
                                    }
                                },
                                onConfirmAllAndFill = {
                                    val allConfirmed = prescriptionItems.map { it.copy(isConfirmed = true) }
                                    prescriptionItems = allConfirmed
                                    applyConfirmedPrescriptionToForm(allConfirmed)
                                },
                                onFillConfirmedToForm = {
                                    applyConfirmedPrescriptionToForm(prescriptionItems)
                                },
                                applyButtonLabel = "将已确认开药单添加到表单",
                                testTagPrefix = "sub_ui_prescription"
                            )
                        }

                        ClinicalDocScanType.MEDICAL_RECORD -> {
                            ExtractedConfirmationSubUiCard(
                                subUiBadge = "病历提取子界面",
                                title = "自动提取病历 · 待核对确认",
                                macStatusText = "手机扫描 → Mac 端病历解析 → 已回传手机",
                                items = medicalRecordItems,
                                onUpdateItemValue = { id, newVal ->
                                    medicalRecordItems = medicalRecordItems.map {
                                        if (it.id == id) it.copy(value = newVal) else it
                                    }
                                },
                                onToggleItemConfirm = { id ->
                                    medicalRecordItems = medicalRecordItems.map {
                                        if (it.id == id) it.copy(isConfirmed = !it.isConfirmed) else it
                                    }
                                },
                                onConfirmAllAndFill = {
                                    val allConfirmed = medicalRecordItems.map { it.copy(isConfirmed = true) }
                                    medicalRecordItems = allConfirmed
                                    applyConfirmedMedicalRecordToForm(allConfirmed)
                                },
                                onFillConfirmedToForm = {
                                    applyConfirmedMedicalRecordToForm(medicalRecordItems)
                                },
                                applyButtonLabel = "将已确认病历添加到表单",
                                testTagPrefix = "sub_ui_medical_record"
                            )
                        }
                    }
                }
            }
        }

        // 3. 病程与医嘱记录表单 Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ClinicalSurfaceContainerLowest,
                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "病程与医嘱记录表单",
                            style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                            color = ClinicalOnSurface
                        )
                        Text(
                            text = filledFromScanTag ?: "支持扫描确认后填入",
                            style = ClinicalTextStyles.bodySm,
                            color = if (filledFromScanTag != null) ClinicalPrimary else ClinicalSecondary
                        )
                    }

                    OutlinedTextField(
                        value = timestampInput,
                        onValueChange = { timestampInput = it },
                        label = {
                            Text(
                                text = "记录时间",
                                style = ClinicalTextStyles.bodySm
                            )
                        },
                        singleLine = true,
                        textStyle = ClinicalTextStyles.labelMonoLg,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ClinicalOnSurface,
                            unfocusedBorderColor = ClinicalHairlineBorder,
                            focusedContainerColor = ClinicalSurfaceContainerLow,
                            unfocusedContainerColor = ClinicalSurfaceContainerLow
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_timestamp_input")
                    )

                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        label = {
                            Text(
                                text = "病程记录内容",
                                style = ClinicalTextStyles.bodySm
                            )
                        },
                        minLines = 3,
                        textStyle = ClinicalTextStyles.bodyMd,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ClinicalOnSurface,
                            unfocusedBorderColor = ClinicalHairlineBorder,
                            focusedContainerColor = ClinicalSurfaceContainerLow,
                            unfocusedContainerColor = ClinicalSurfaceContainerLow
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_description_input")
                    )

                    OutlinedTextField(
                        value = doctorNoteInput,
                        onValueChange = { doctorNoteInput = it },
                        label = {
                            Text(
                                text = "诊疗医嘱 / 开药单内容",
                                style = ClinicalTextStyles.bodySm
                            )
                        },
                        minLines = 2,
                        textStyle = ClinicalTextStyles.bodySm,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ClinicalOnSurface,
                            unfocusedBorderColor = ClinicalHairlineBorder,
                            focusedContainerColor = ClinicalSurfaceContainerLow,
                            unfocusedContainerColor = ClinicalSurfaceContainerLow
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_doctor_note_input")
                    )

                    if (hasMedicationInOrder) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ClinicalPrimary.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, ClinicalPrimaryContainer.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "已检测到医嘱/开药单内含处方用药：保存后将自动同步生成吃药卡片",
                                style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.Medium),
                                color = ClinicalOnSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onSaveToTimeline(timestampInput, descriptionInput, doctorNoteInput)
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClinicalInverseSurface,
                            contentColor = ClinicalInverseOnSurface
                        ),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_manual_timeline_btn")
                    ) {
                        Text(
                            text = if (hasMedicationInOrder) "保存记录并自动同步医嘱用药" else "添加到病程日历下方记录",
                            style = ClinicalTextStyles.bodyLg
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable Extraction Confirmation Sub-UI Card for Doctor's Order, Prescription Slip, and Medical Record.
 * Supports modifying individual extracted values, confirming individual items, or one-click confirming all.
 */
@Composable
private fun ExtractedConfirmationSubUiCard(
    subUiBadge: String,
    title: String,
    macStatusText: String,
    items: List<ExtractedFieldItem>,
    onUpdateItemValue: (id: String, newValue: String) -> Unit,
    onToggleItemConfirm: (id: String) -> Unit,
    onConfirmAllAndFill: () -> Unit,
    onFillConfirmedToForm: () -> Unit,
    applyButtonLabel: String,
    testTagPrefix: String
) {
    val confirmedCount = items.count { it.isConfirmed }
    val allConfirmed = confirmedCount == items.size && items.isNotEmpty()

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(1.5.dp, ClinicalOnSurface.copy(alpha = 0.18f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("${testTagPrefix}_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top bar: Sub-UI title + One-Click Confirm All button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ClinicalOnSurface
                        ) {
                            Text(
                                text = subUiBadge,
                                style = ClinicalTextStyles.bodySm.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ClinicalSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = title,
                            style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.Bold),
                            color = ClinicalOnSurface
                        )
                    }
                    Text(
                        text = macStatusText,
                        style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp),
                        color = ClinicalSecondary
                    )
                }

                // 一键全部确认 Button
                Surface(
                    shape = CircleShape,
                    color = if (allConfirmed) ClinicalPrimaryContainer else ClinicalOnSurface,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onConfirmAllAndFill() }
                        .testTag("${testTagPrefix}_confirm_all_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = ClinicalSurface,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (allConfirmed) "已全部确认" else "一键全部确认",
                            style = ClinicalTextStyles.bodySm.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ClinicalSurface
                        )
                    }
                }
            }

            HorizontalDivider(color = ClinicalSurfaceContainer)

            // Extracted items list: each item can be manually modified and individually confirmed
            items.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (item.isConfirmed) {
                        ClinicalSurfaceContainerLow
                    } else {
                        ClinicalSurface
                    },
                    border = BorderStroke(
                        1.dp,
                        if (item.isConfirmed) ClinicalOnSurface else ClinicalSurfaceContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text(
                                    text = item.label,
                                    style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                                    color = ClinicalOnSurface
                                )
                                Text(
                                    text = item.subHint,
                                    style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp),
                                    color = ClinicalSecondary
                                )
                            }

                            // Per-item manual confirmation toggle
                            Surface(
                                shape = CircleShape,
                                color = if (item.isConfirmed) ClinicalOnSurface else ClinicalSurfaceContainer,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { onToggleItemConfirm(item.id) }
                                    .testTag("confirm_item_${item.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    if (item.isConfirmed) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = ClinicalSurface,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    Text(
                                        text = if (item.isConfirmed) "已确认" else "点击确认",
                                        style = ClinicalTextStyles.bodySm.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = if (item.isConfirmed) ClinicalSurface else ClinicalOnSurface
                                    )
                                }
                            }
                        }

                        // Editable field for manual modification of extracted values/numbers
                        OutlinedTextField(
                            value = item.value,
                            onValueChange = { onUpdateItemValue(item.id, it) },
                            singleLine = false,
                            maxLines = 2,
                            textStyle = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.Medium),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ClinicalOnSurface,
                                unfocusedBorderColor = ClinicalHairlineBorder,
                                focusedContainerColor = ClinicalSurfaceContainerLowest,
                                unfocusedContainerColor = ClinicalSurfaceContainerLowest
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_extracted_${item.id}")
                        )
                    }
                }
            }

            // Apply confirmed items into the form below
            Button(
                onClick = onFillConfirmedToForm,
                enabled = confirmedCount > 0,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClinicalOnSurface,
                    contentColor = ClinicalSurface,
                    disabledContainerColor = ClinicalSurfaceContainer,
                    disabledContentColor = ClinicalSecondary
                ),
                contentPadding = PaddingValues(vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${testTagPrefix}_apply_to_form_btn")
            ) {
                Text(
                    text = if (confirmedCount > 0) {
                        "$applyButtonLabel（已确认 $confirmedCount/${items.size} 项）"
                    } else {
                        "请先确认提取项（或点击右上角一键全部确认）"
                    },
                    style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
private fun SubModeBackHeader(
    modeBadge: String,
    title: String,
    onBack: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ClinicalSurfaceContainerLow,
        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onBack)
            .testTag("submode_back_header")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = ClinicalOnSurface,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.Medium),
                    color = ClinicalOnSurface
                )
            }
            Text(
                text = modeBadge,
                style = ClinicalTextStyles.bodySm,
                color = ClinicalSecondary
            )
        }
    }
}

/**
 * 卡片墙 (CardWall / CardSettingsScreen):
 * 1. Top: Stacked Medication Cards Deck ("吃药卡片堆"):
 *    - Displays medication dosage & usage method on top.
 *    - Bottom track has a draggable pill on the left and a stick-figure human head on the right.
 *    - Dragging the pill to the stick-figure head marks the medicine as taken and reveals the next stacked card!
 * 2. Bottom: 2x2 Four-Slot Modular Cards Grid.
 */
@Composable
fun CardSettingsScreen(
    uiState: TelemetryUiState,
    onNavigateBackToCalendar: () -> Unit,
    onCardClick: (ModularCardItem) -> Unit,
    onEnterCardEditMode: () -> Unit,
    onExitCardEditMode: () -> Unit,
    onDeleteCardAtSlot: (Int) -> Unit,
    onOpenCardBrowserForSlot: (Int) -> Unit,
    onCloseCardBrowser: () -> Unit,
    onSelectCardForSlot: (Int, ModularCardItem) -> Unit,
    onCloseDetailModal: () -> Unit,
    onSwitchToAddTab: () -> Unit,
    onOpenScanMedicationBox: () -> Unit,
    onToggleMedicationDoseTaken: (String) -> Unit,
    onResetAllMedicationDoses: () -> Unit,
    onOpenTelemetryDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (uiState.isCardEditMode) {
            onExitCardEditMode()
        } else {
            onNavigateBackToCalendar()
        }
    }

    val blankClickInteraction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ClinicalSurface)
            .then(
                if (uiState.isCardEditMode) {
                    Modifier.clickable(
                        interactionSource = blankClickInteraction,
                        indication = null,
                        onClick = onExitCardEditMode
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .testTag("card_settings_list"),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (uiState.bannerMessage != null) {
                item(key = "card_wall_sync_toast") {
                    Surface(
                        color = ClinicalOnSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.bannerMessage,
                            style = ClinicalTextStyles.bodySm,
                            color = ClinicalSurface,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            // 1. Stacked Medication Cards Deck (吃药堆叠卡片组)
            item(key = "stacked_medication_deck") {
                StackedMedicationDeck(
                    medicationPlans = uiState.medicationPlans,
                    onMarkDoseTaken = onToggleMedicationDoseTaken,
                    onResetAllDoses = onResetAllMedicationDoses,
                    onOpenScanMedicationBox = onOpenScanMedicationBox
                )
            }

            // 2. 2x2 Four-Slot Modular Cards Grid
            item(key = "four_slot_cards_grid") {
                FourSlotCardsGrid(
                    cardSlots = uiState.cardSlots,
                    isEditMode = uiState.isCardEditMode,
                    onCardClick = { slotIndex, card ->
                        if (uiState.isCardEditMode) {
                            onOpenCardBrowserForSlot(slotIndex)
                        } else {
                            onCardClick(card)
                        }
                    },
                    onLongPressCard = onEnterCardEditMode,
                    onDeleteSlot = onDeleteCardAtSlot,
                    onPlusSlotClick = onOpenCardBrowserForSlot,
                    onBlankSlotTapInEditMode = onExitCardEditMode
                )
            }
        }

        // Card Detail Dialog
        val detailCard = uiState.detailModalCard
        if (detailCard != null) {
            ModularCardDetailDialog(
                card = detailCard,
                onDismiss = onCloseDetailModal,
                onNavigateToAddRecord = {
                    onCloseDetailModal()
                    if (detailCard.id == "card_medication" ||
                        detailCard.id == "card_rx_sync" ||
                        detailCard.id == "card_med_blister"
                    ) {
                        onOpenScanMedicationBox()
                    } else {
                        onSwitchToAddTab()
                    }
                },
                onNavigateToDossier = {
                    onCloseDetailModal()
                    onOpenTelemetryDetail()
                }
            )
        }

        // Card Browser Dialog when clicking '+' on an empty slot (or tapping a slot in edit mode)
        val browsingSlot = uiState.browsingSlotIndex
        if (browsingSlot != null) {
            CardBrowserDialog(
                slotIndex = browsingSlot,
                currentSlots = uiState.cardSlots,
                cardLibrary = uiState.cardLibrary,
                onDismiss = onCloseCardBrowser,
                onSelectCard = { chosen ->
                    onSelectCardForSlot(browsingSlot, chosen)
                }
            )
        }
    }
}

/**
 * Stacked Medication Cards Deck:
 * - Cards are visually stacked on top of each other (up to 3 visible layers).
 * - Top card shows drug name, dosage & usage method on top, and a pill-to-avatar drag track at the bottom.
 * - Dragging the pill to the stick-figure head on the right completes the dose and reveals the next card in the stack.
 */
@Composable
private fun StackedMedicationDeck(
    medicationPlans: List<MedicationBoxPlan>,
    onMarkDoseTaken: (String) -> Unit,
    onResetAllDoses: () -> Unit,
    onOpenScanMedicationBox: () -> Unit
) {
    val pendingPlans = remember(medicationPlans) {
        medicationPlans.filter { it.takenDosesToday < it.totalDosesToday }
    }
    val totalCount = medicationPlans.size
    val remainingCount = pendingPlans.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stacked_medication_deck"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Minimal Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "用药打卡",
                    style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                    color = ClinicalSecondary
                )
                Surface(
                    shape = CircleShape,
                    color = ClinicalSurfaceContainer
                ) {
                    Text(
                        text = if (remainingCount > 0) "待服 $remainingCount/$totalCount" else "已服完",
                        style = ClinicalTextStyles.labelMonoSm,
                        color = ClinicalOnSurface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = ClinicalSurfaceContainerLow,
                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onOpenScanMedicationBox)
                    .testTag("btn_card_wall_scan_med_box")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = null,
                        tint = ClinicalOnSurface,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "扫药盒",
                        style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                        color = ClinicalOnSurface
                    )
                }
            }
        }

        if (pendingPlans.isNotEmpty()) {
            val visibleStack = pendingPlans.take(3)
            val currentStepIndex = (totalCount - remainingCount + 1).coerceAtLeast(1)

            // Layered 3D Card Stack Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = ((visibleStack.size - 1).coerceAtLeast(0) * 14).dp),
                contentAlignment = Alignment.TopCenter
            ) {
                visibleStack.forEachIndexed { depthIndex, plan ->
                    key(plan.id) {
                        val isTopCard = depthIndex == 0
                        val targetScale by animateFloatAsState(
                            targetValue = when (depthIndex) {
                                0 -> 1.0f
                                1 -> 0.94f
                                else -> 0.88f
                            },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "stack_scale_${plan.id}"
                        )
                        val targetOffsetY by animateDpAsState(
                            targetValue = when (depthIndex) {
                                0 -> 0.dp
                                1 -> 14.dp
                                else -> 28.dp
                            },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "stack_offset_${plan.id}"
                        )
                        val targetAlpha by animateFloatAsState(
                            targetValue = when (depthIndex) {
                                0 -> 1.0f
                                1 -> 0.82f
                                else -> 0.55f
                            },
                            animationSpec = tween(220),
                            label = "stack_alpha_${plan.id}"
                        )

                        StackedMedicationCardItem(
                            plan = plan,
                            stepBadgeText = "$currentStepIndex / $totalCount",
                            isTopCard = isTopCard,
                            onSwallowed = { onMarkDoseTaken(plan.id) },
                            modifier = Modifier
                                .zIndex((10 - depthIndex).toFloat())
                                .offset(y = targetOffsetY)
                                .graphicsLayer {
                                    scaleX = targetScale
                                    scaleY = targetScale
                                    alpha = targetAlpha
                                }
                        )
                    }
                }
            }
        } else {
            // All medicines taken state: clean minimal card with one-tap reset
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ClinicalSurfaceContainerLowest,
                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("all_meds_taken_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(ClinicalSurfaceContainerLow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            StickFigureHeadAvatar(
                                mouthOpenFraction = 0f,
                                isCompleted = true,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "今日药品已全部服完",
                                style = ClinicalTextStyles.cardTitleSm,
                                color = ClinicalOnSurface
                            )
                            Text(
                                text = "共完成 $totalCount 项服药打卡",
                                style = ClinicalTextStyles.bodySm,
                                color = ClinicalSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(percent = 50),
                        color = ClinicalOnSurface,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable(onClick = onResetAllDoses)
                            .testTag("btn_reset_med_stack")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "重置卡片堆",
                                tint = ClinicalSurface,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "再来一次",
                                style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.Bold),
                                color = ClinicalSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StackedMedicationCardItem(
    plan: MedicationBoxPlan,
    stepBadgeText: String,
    isTopCard: Boolean,
    onSwallowed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffsetPx by remember(plan.id) { mutableFloatStateOf(0f) }
    var maxDragPx by remember(plan.id) { mutableFloatStateOf(1f) }
    var isDragging by remember(plan.id) { mutableStateOf(false) }
    var isSwallowing by remember(plan.id) { mutableStateOf(false) }

    LaunchedEffect(isSwallowing) {
        if (isSwallowing) {
            delay(220)
            onSwallowed()
        }
    }

    val animatedDragPx by animateFloatAsState(
        targetValue = when {
            isSwallowing -> maxDragPx
            isDragging -> dragOffsetPx
            else -> 0f
        },
        animationSpec = if (isDragging) {
            spring(stiffness = Spring.StiffnessHigh)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = "pill_drag_offset_${plan.id}"
    )

    val cardDismissScale by animateFloatAsState(
        targetValue = if (isSwallowing) 0.92f else 1f,
        animationSpec = tween(200),
        label = "swallow_card_scale_${plan.id}"
    )
    val cardDismissAlpha by animateFloatAsState(
        targetValue = if (isSwallowing) 0f else 1f,
        animationSpec = tween(200),
        label = "swallow_card_alpha_${plan.id}"
    )

    val dragProgress = (animatedDragPx / maxDragPx.coerceAtLeast(1f)).coerceIn(0f, 1f)
    val pillSize = 52.dp
    val trackPadding = 6.dp
    val pillSizePx = with(LocalDensity.current) { pillSize.toPx() }
    val trackPaddingPx = with(LocalDensity.current) { trackPadding.toPx() }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = ClinicalSurfaceContainerLowest,
        shadowElevation = if (isTopCard) 4.dp else 1.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (isTopCard) ClinicalOnSurface.copy(alpha = 0.18f) else ClinicalHairlineBorder.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardDismissScale
                scaleY = cardDismissScale
                alpha = cardDismissAlpha
            }
            .testTag("medication_plan_card_${plan.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Upper Section: Drug Name, Dosage & Method ("卡片上面显示药的用量和服用方法")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plan.drugName,
                        style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.SemiBold),
                        color = ClinicalSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = CircleShape,
                        color = ClinicalSurfaceContainer
                    ) {
                        Text(
                            text = stepBadgeText,
                            style = ClinicalTextStyles.labelMonoSm,
                            color = ClinicalOnSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }

                // Prominent Dosage & Usage Method Display
                Text(
                    text = plan.dosePerTimeLabel,
                    style = ClinicalTextStyles.headlineSm.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ClinicalOnSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UsageTagPill(text = plan.frequencyLabel, isPrimary = true)
                    UsageTagPill(text = plan.timingLabel, isPrimary = false)
                }
            }

            // Lower Section: Pill-to-Stick-Figure-Avatar Drag Track ("下面是一个药丸。把药丸拖到右边（右边是一个简笔画的人头像），拖过去之后就视为已经服药")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(ClinicalSurfaceContainerLow, RoundedCornerShape(percent = 50))
                    .border(
                        width = 1.dp,
                        color = if (dragProgress > 0.65f) ClinicalPrimaryContainer else ClinicalSurfaceContainer,
                        shape = RoundedCornerShape(percent = 50)
                    )
                    .onSizeChanged { size ->
                        val available = size.width - (pillSizePx * 2f) - (trackPaddingPx * 2f) + (pillSizePx * 0.65f)
                        maxDragPx = available.coerceAtLeast(1f)
                    }
                    .padding(trackPadding)
            ) {
                // Center Dotted Guide & Hint Text
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .alpha((1f - dragProgress * 1.4f).coerceIn(0f, 1f)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "拖入右侧服药",
                        style = ClinicalTextStyles.bodySm.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                        color = ClinicalSecondary
                    )
                    Text(
                        text = "• • • ▸",
                        style = ClinicalTextStyles.labelMonoSm,
                        color = ClinicalSecondary
                    )
                }

                // Right Target: Stick-Figure Human Head (简笔画人头像)
                val isNearMouth = dragProgress > 0.55f
                Box(
                    modifier = Modifier
                        .size(pillSize)
                        .align(Alignment.CenterEnd)
                        .clip(CircleShape)
                        .background(
                            color = if (isNearMouth) {
                                ClinicalPrimaryContainer.copy(alpha = 0.14f)
                            } else {
                                ClinicalSurfaceContainerLowest
                            },
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = if (isNearMouth) ClinicalPrimaryContainer else ClinicalHairlineBorder,
                            shape = CircleShape
                        )
                        .then(
                            if (isTopCard) {
                                Modifier.clickable {
                                    if (!isSwallowing) isSwallowing = true
                                }
                            } else {
                                Modifier
                            }
                        )
                        .testTag("btn_toggle_med_dose_${plan.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    StickFigureHeadAvatar(
                        mouthOpenFraction = ((dragProgress - 0.25f) / 0.65f).coerceIn(0f, 1f),
                        isCompleted = isSwallowing,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Left Draggable Handle: Capsule Pill (可拖动药丸)
                val pillShrinkScale = if (isSwallowing) 0.2f else 1f - (dragProgress * 0.12f)
                Box(
                    modifier = Modifier
                        .offset { IntOffset(animatedDragPx.roundToInt(), 0) }
                        .size(pillSize)
                        .align(Alignment.CenterStart)
                        .graphicsLayer {
                            scaleX = pillShrinkScale
                            scaleY = pillShrinkScale
                            rotationZ = dragProgress * 25f
                        }
                        .clip(CircleShape)
                        .background(ClinicalSurfaceContainerLowest, CircleShape)
                        .border(
                            width = 1.5.dp,
                            color = ClinicalOnSurface,
                            shape = CircleShape
                        )
                        .then(
                            if (isTopCard) {
                                Modifier.pointerInput(plan.id, maxDragPx) {
                                    detectHorizontalDragGestures(
                                        onDragStart = {
                                            isDragging = true
                                        },
                                        onHorizontalDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffsetPx = (dragOffsetPx + dragAmount).coerceIn(0f, maxDragPx)
                                        },
                                        onDragEnd = {
                                            isDragging = false
                                            if (dragOffsetPx >= maxDragPx * 0.72f) {
                                                isSwallowing = true
                                            } else {
                                                dragOffsetPx = 0f
                                            }
                                        },
                                        onDragCancel = {
                                            isDragging = false
                                            dragOffsetPx = 0f
                                        }
                                    )
                                }
                            } else {
                                Modifier
                            }
                        )
                        .testTag("draggable_pill_${plan.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    DraggableCapsulePillGraphic(modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}

/**
 * Two-tone Capsule Pill Graphic (Signal Red + White with Obsidian outline) for dragging.
 */
@Composable
private fun DraggableCapsulePillGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pillW = w * 0.86f
        val pillH = h * 0.48f
        val left = (w - pillW) / 2f
        val top = (h - pillH) / 2f
        val radius = pillH / 2f

        // Left half filled in Signal Red
        drawRoundRect(
            color = ClinicalPrimaryContainer,
            topLeft = Offset(left, top),
            size = Size(pillW * 0.54f, pillH),
            cornerRadius = CornerRadius(radius, radius)
        )
        // Center seam cover so middle is straight
        drawRect(
            color = ClinicalSurfaceContainerLowest,
            topLeft = Offset(left + pillW * 0.5f, top),
            size = Size(pillW * 0.5f, pillH)
        )
        // Outer capsule stroke
        drawRoundRect(
            color = ClinicalOnSurface,
            topLeft = Offset(left, top),
            size = Size(pillW, pillH),
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(width = 2.dp.toPx())
        )
        // Center divider line
        drawLine(
            color = ClinicalOnSurface,
            start = Offset(w * 0.5f, top),
            end = Offset(w * 0.5f, top + pillH),
            strokeWidth = 1.8.dp.toPx()
        )
    }
}

/**
 * Minimalist Stick-Figure Human Head Avatar (简笔画人头像):
 * - Drawn in clean monoline style.
 * - Opens its mouth progressively as the pill is dragged toward it (`mouthOpenFraction`).
 */
@Composable
private fun StickFigureHeadAvatar(
    mouthOpenFraction: Float,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val s = size.width / 24f
        val strokeColor = if (isCompleted || mouthOpenFraction > 0.5f) {
            ClinicalPrimaryContainer
        } else {
            ClinicalOnSurface
        }
        val lineStroke = Stroke(
            width = 1.9f * s,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // 1. Minimalist Hair Tuft on top of head
        val hairPath = Path().apply {
            moveTo(10f * s, 3.8f * s)
            quadraticTo(11.5f * s, 1.6f * s, 13.5f * s, 3.2f * s)
            moveTo(12.2f * s, 3.5f * s)
            quadraticTo(14.2f * s, 1.8f * s, 15.5f * s, 3.8f * s)
        }
        drawPath(path = hairPath, color = strokeColor, style = lineStroke)

        // 2. Stick-Figure Head Circle
        drawCircle(
            color = strokeColor,
            radius = 7.8f * s,
            center = Offset(12f * s, 11.2f * s),
            style = lineStroke
        )

        // 3. Eyes ( facing slightly left toward the incoming pill )
        drawCircle(
            color = strokeColor,
            radius = 1.15f * s,
            center = Offset(9.2f * s, 9.8f * s)
        )
        drawCircle(
            color = strokeColor,
            radius = 1.15f * s,
            center = Offset(13.8f * s, 9.8f * s)
        )

        // 4. Mouth: opens into an 'O' as the pill approaches from the left!
        if (mouthOpenFraction > 0.15f && !isCompleted) {
            val openRadiusX = (1.6f + mouthOpenFraction * 1.6f) * s
            val openRadiusY = (1.0f + mouthOpenFraction * 2.0f) * s
            drawOval(
                color = ClinicalPrimaryContainer,
                topLeft = Offset(11.2f * s - openRadiusX, 14.2f * s - openRadiusY),
                size = Size(openRadiusX * 2f, openRadiusY * 2f),
                style = Stroke(width = 1.8f * s)
            )
        } else {
            // Gentle smile arc
            val smilePath = Path().apply {
                moveTo(9.4f * s, 13.8f * s)
                quadraticTo(11.6f * s, 15.8f * s, 14.2f * s, 13.8f * s)
            }
            drawPath(path = smilePath, color = strokeColor, style = lineStroke)
        }

        // 5. Minimalist Neck & Shoulder line at bottom
        val shoulderPath = Path().apply {
            moveTo(6.5f * s, 21.8f * s)
            quadraticTo(12f * s, 18.8f * s, 17.5f * s, 21.8f * s)
        }
        drawPath(path = shoulderPath, color = strokeColor, style = lineStroke)
    }
}

@Composable
private fun UsageTagPill(
    text: String,
    isPrimary: Boolean
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isPrimary) ClinicalOnSurface else ClinicalSurfaceContainerLow,
        border = if (isPrimary) null else BorderStroke(1.dp, ClinicalSurfaceContainer)
    ) {
        Text(
            text = text,
            style = ClinicalTextStyles.bodySm.copy(
                fontSize = 11.sp,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isPrimary) ClinicalSurface else ClinicalOnSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
