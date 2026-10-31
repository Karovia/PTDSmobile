package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
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
    modifier: Modifier = Modifier
) {
    val activeTopTab = if (selectedTab == BottomNavTab.ADD_RECORD) lastMainTab else selectedTab

    Surface(
        color = ClinicalSurface.copy(alpha = 0.96f),
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxWidth()
                .height(62.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedTab == BottomNavTab.ADD_RECORD) {
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
            label = "add_record_mode_transition"
        ) { mode ->
            when (mode) {
                AddRecordSubMode.SELECTION -> {
                    AddRecordModeSelectionView(
                        medicationCount = medicationPlans.size,
                        onBack = onNavigateBack,
                        onSelectPhotoOcr = { onSelectSubMode(AddRecordSubMode.PHOTO_OCR) },
                        onSelectScanMedicationBox = { onSelectSubMode(AddRecordSubMode.SCAN_MEDICATION_BOX) },
                        onSelectManualTimeline = { onSelectSubMode(AddRecordSubMode.MANUAL_TIMELINE) }
                    )
                }

                AddRecordSubMode.PHOTO_OCR -> {
                    PhotoOcrRecognitionView(
                        currentBiomarkers = currentBiomarkers,
                        onBackToSelection = onNavigateBack,
                        onSyncToCalendar = onSyncRecognizedIndicators
                    )
                }

                AddRecordSubMode.SCAN_MEDICATION_BOX -> {
                    ScanMedicationBoxView(
                        onBackToSelection = onNavigateBack,
                        onSaveScannedBox = onAddScannedMedicationBox,
                        onSyncDoctorOrderOnly = onSyncMedicationsFromDoctorOrder
                    )
                }

                AddRecordSubMode.MANUAL_TIMELINE -> {
                    ManualTimelineEntryView(
                        onBackToSelection = onNavigateBack,
                        onSaveToTimeline = onAddManualTimelineRecord
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
    val frequency: String,
    val dose: String,
    val timing: String
)

/**
 * Medicine Box Scanner + Usage/Dosage ("怎么吃") Configuration + Auto-Sync from Doctor's Order.
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
                shortTag = "美卓乐 (甲泼尼龙)",
                drugName = "甲泼尼龙片 (美卓乐)",
                specification = "4mg × 30片/盒",
                frequency = "每日1次",
                dose = "每次4片 (16mg)",
                timing = "早餐后温水送服"
            ),
            MedicineBoxPreset(
                shortTag = "叶酸片 (5mg)",
                drugName = "叶酸片",
                specification = "5mg × 30片/盒",
                frequency = "每日1次",
                dose = "每次1片 (5mg)",
                timing = "早餐后温水送服"
            ),
            MedicineBoxPreset(
                shortTag = "钙尔奇 (碳酸钙D3)",
                drugName = "碳酸钙D3片 (钙尔奇)",
                specification = "600mg × 60片/瓶",
                frequency = "每日1次",
                dose = "每次1片 (600mg)",
                timing = "晚餐后或睡前咀嚼服用"
            ),
            MedicineBoxPreset(
                shortTag = "西乐葆 (塞来昔布)",
                drugName = "塞来昔布胶囊 (西乐葆)",
                specification = "0.2g × 10粒/盒",
                frequency = "每日2次",
                dose = "每次1粒 (0.2g)",
                timing = "早晚餐后温水送服"
            )
        )
    }

    var hasScannedBox by remember { mutableStateOf(true) }
    var drugNameInput by remember { mutableStateOf(presets[1].drugName) }
    var specInput by remember { mutableStateOf(presets[1].specification) }
    var frequencyInput by remember { mutableStateOf(presets[1].frequency) }
    var doseInput by remember { mutableStateOf(presets[1].dose) }
    var timingInput by remember { mutableStateOf(presets[1].timing) }

    var doctorOrderInput by remember {
        mutableStateOf("查房医嘱：继续甲泼尼龙片 16mg/d 早餐后口服，配合护胃治疗，加服叶酸片每日1片，每晚钙片1片")
    }

    val frequencyOptions = listOf("每日1次", "每日2次", "每日3次", "隔日1次", "每周1次")
    val doseOptions = listOf("每次1片", "每次2片", "每次4片 (16mg)", "每次1.5片 (6mg)", "每次1粒")
    val timingOptions = listOf("早餐后温水送服", "早餐前30分钟吞服", "晚餐后或睡前服用", "早晚餐后温水送服")

    LazyColumn(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxSize()
            .testTag("scan_medication_box_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1. Medicine Box Barcode & Package Viewfinder Card
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MedicationCapsuleIcon()
                            Text(
                                text = "药盒拍照 / 国药准字条码扫描",
                                style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                                color = ClinicalOnSurface
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = if (hasScannedBox) ClinicalPrimary.copy(alpha = 0.1f) else ClinicalSurfaceContainer
                        ) {
                            Text(
                                text = if (hasScannedBox) "药盒已识别" else "等待对焦",
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
                            .height(156.dp)
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
                                val len = 20.dp.toPx()
                                val stroke = 2.4.dp.toPx()
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

                                // Horizontal laser line across the medicine box
                                drawLine(
                                    color = ClinicalPrimaryContainer.copy(alpha = 0.45f),
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
                                        .size(42.dp)
                                        .background(ClinicalSurfaceContainerLowest, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.QrCodeScanner,
                                        contentDescription = null,
                                        tint = ClinicalPrimaryContainer,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Text(
                                    text = "已定位药盒：${drugNameInput}（$specInput）",
                                    style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.SemiBold),
                                    color = ClinicalOnSurface
                                )
                                Text(
                                    text = "点击下方预设药盒可快速模拟扫描不同药品",
                                    style = ClinicalTextStyles.bodySm,
                                    color = ClinicalSecondary
                                )
                            }
                        }
                    }

                    // Quick demo scan presets
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "快速模拟扫描药盒：",
                            style = ClinicalTextStyles.bodySm,
                            color = ClinicalSecondary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                            frequencyInput = preset.frequency
                                            doseInput = preset.dose
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
        }

        // 2. Usage & Dosage Configuration Card ("药盒可以添加用法用量（比如怎么吃）")
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
                            text = "药盒信息与用法用量（怎么吃）",
                            style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                            color = ClinicalOnSurface
                        )
                        Text(
                            text = "支持点选或手动输入",
                            style = ClinicalTextStyles.bodySm,
                            color = ClinicalSecondary
                        )
                    }

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

                    // Frequency Selector ("每日几次")
                    UsageSelectorGroup(
                        label = "1. 服用频次（每日几次）",
                        options = frequencyOptions,
                        selectedOption = frequencyInput,
                        onSelectOption = { frequencyInput = it }
                    )

                    // Dose per time Selector ("每次吃多少")
                    UsageSelectorGroup(
                        label = "2. 单次剂量（每次怎么吃）",
                        options = doseOptions,
                        selectedOption = doseInput,
                        onSelectOption = { doseInput = it }
                    )

                    // Timing Selector ("饭前/饭后/睡前")
                    UsageSelectorGroup(
                        label = "3. 服用时间与方式",
                        options = timingOptions,
                        selectedOption = timingInput,
                        onSelectOption = { timingInput = it }
                    )

                    // Live Usage Summary Preview Pill
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = ClinicalSurfaceContainerLow,
                        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "生成用法用量标签",
                                    style = ClinicalTextStyles.bodySm,
                                    color = ClinicalSecondary
                                )
                                Text(
                                    text = "$frequencyInput • $doseInput • $timingInput",
                                    style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.Bold),
                                    color = ClinicalOnSurface
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            onSaveScannedBox(
                                drugNameInput,
                                specInput,
                                frequencyInput,
                                doseInput,
                                timingInput,
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
                                text = "保存该药盒及用法至卡片墙",
                                style = ClinicalTextStyles.bodyLg
                            )
                        }
                    }
                }
            }
        }

        // 3. Doctor's Order Auto-Sync Card ("如果有医嘱的话，医嘱上写了要吃什么药，就自动同步生成")
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
                            DoctorOrderSyncIcon()
                            Text(
                                text = "医嘱用药自动识别与同步生成",
                                style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                                color = ClinicalOnSurface
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = ClinicalPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "智能解析医嘱",
                                style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = ClinicalPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "如果查房或门诊医嘱中写了要吃什么药、怎么吃，粘贴或输入在下方，系统会自动提取全部药品并同步生成吃药卡片：",
                        style = ClinicalTextStyles.bodySm,
                        color = ClinicalSecondary
                    )

                    // Quick doctor order templates
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val orderTemplates = listOf(
                            "查房医嘱：甲泼尼龙16mg+护胃+叶酸+钙片" to "查房医嘱：继续甲泼尼龙片 16mg/d 早餐后口服，配合护胃治疗，加服叶酸片每日1片，每晚钙片1片",
                            "减量医嘱：美卓乐减至1.5片+钙片+布洛芬" to "门诊医嘱：美卓乐减至每日1次每次1.5片早餐后服，碳酸钙每晚1片，若发热关节痛加服布洛芬每次1粒",
                            "巩固医嘱：甲氨蝶呤每周1次+叶酸" to "复诊医嘱：甲氨蝶呤片每周1次每次4片餐后口服，叶酸片每日1次每次1片早餐后服"
                        )
                        orderTemplates.forEach { (label, fullOrder) ->
                            Surface(
                                shape = CircleShape,
                                color = ClinicalSurfaceContainerLow,
                                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { doctorOrderInput = fullOrder }
                            ) {
                                Text(
                                    text = label,
                                    style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp),
                                    color = ClinicalOnSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = doctorOrderInput,
                        onValueChange = { doctorOrderInput = it },
                        label = { Text("医嘱原文（含药品名称与用法用量）", style = ClinicalTextStyles.bodySm) },
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
                            .testTag("input_doctor_order_rx")
                    )

                    Button(
                        onClick = { onSyncDoctorOrderOnly(doctorOrderInput) },
                        shape = RoundedCornerShape(percent = 50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClinicalPrimaryContainer,
                            contentColor = ClinicalSurface
                        ),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_sync_doctor_order_meds")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ClinicalRecordDossierIcon(
                                tint = ClinicalSurface,
                                accentColor = ClinicalSurface,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = "按医嘱自动生成全部吃药卡片",
                                style = ClinicalTextStyles.bodyLg
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UsageSelectorGroup(
    label: String,
    options: List<String>,
    selectedOption: String,
    onSelectOption: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.Medium),
            color = ClinicalSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedOption == option
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) ClinicalOnSurface else ClinicalSurfaceContainerLow,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) ClinicalOnSurface else ClinicalSurfaceContainer
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onSelectOption(option) }
                ) {
                    Text(
                        text = option,
                        style = ClinicalTextStyles.bodySm.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) ClinicalSurface else ClinicalOnSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
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

@Composable
private fun ManualTimelineEntryView(
    onBackToSelection: () -> Unit,
    onSaveToTimeline: (timestampLabel: String, descriptionText: String, doctorNoteText: String) -> Unit
) {
    var timestampInput by remember { mutableStateOf("10-12 今天 14:30") }
    var descriptionInput by remember {
        mutableStateOf("午后复测体温37.2°C，双膝关节无明显压痛，按计划完成今日口服药。")
    }
    var doctorNoteInput by remember {
        mutableStateOf("继续甲泼尼龙片 16mg/d 口服，加服叶酸片每日1片（早餐后），每晚碳酸钙D3片1片")
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
                            text = "支持医嘱自动转吃药卡",
                            style = ClinicalTextStyles.bodySm,
                            color = ClinicalPrimary
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
                                text = "诊疗医嘱（若写有要吃什么药，将自动同步生成吃药卡片）",
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
                                text = "已检测到医嘱内含处方用药：保存后将自动同步生成用法用量与吃药卡片",
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
