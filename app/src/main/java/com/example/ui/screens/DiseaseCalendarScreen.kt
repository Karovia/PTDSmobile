package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.BiomarkerTelemetry
import com.example.model.CalendarDayItem
import com.example.model.CalendarDotStatus
import com.example.model.ModularCardItem
import com.example.model.ThresholdStatus
import com.example.model.TimelineEvent
import com.example.ui.components.ConcentricBiomarkerRacetrack
import com.example.ui.components.ModularCardFeatureIcon
import com.example.ui.components.ModularCardGraphicMotif
import com.example.ui.components.PixelThresholdArrow
import com.example.ui.components.PulsingBeaconDot
import com.example.ui.theme.ClinicalHairlineBorder
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

@Composable
fun DiseaseCalendarScreen(
    uiState: TelemetryUiState,
    onSelectDay: (String) -> Unit,
    onToggleTimelineEvent: (String) -> Unit,
    onLongPressTimelineEvent: (String) -> Unit,
    onSelectAllTimelineEvents: () -> Unit,
    onClearTimelineSelection: () -> Unit,
    onCloseRemarkDialog: () -> Unit = {},
    onSaveEventRemark: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val biomarkers = uiState.biomarkers
    val isSelectionMode = uiState.selectedTimelineEventIds.isNotEmpty()

    if (isSelectionMode) {
        BackHandler(onBack = onClearTimelineSelection)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ClinicalSurface),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .testTag("disease_calendar_list"),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.bannerMessage != null) {
                item(key = "sync_toast") {
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

            // 1. Primary Biometric Telemetry Card
            item(key = "biomarker_card") {
                BiomarkerMonitorCard(
                    biomarkers = biomarkers,
                    onBlankClickInEditMode = null
                )
            }

            // 2. Perpetual & Scrollable Disease Calendar Card
            item(key = "weekly_calendar") {
                WeeklyDiseaseCalendarCard(
                    days = uiState.calendarDays,
                    selectedDayNumber = uiState.selectedDayNumber,
                    selectedDateKey = uiState.selectedDateKey,
                    onSelectDay = onSelectDay
                )
            }

            // 3. Collapsible Disease Events Timeline Header (with multi-select status)
            item(key = "timeline_header") {
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
                            text = if (isSelectionMode) {
                                "已选 ${uiState.selectedTimelineEventIds.size} 条病程"
                            } else {
                                stringResource(R.string.event_logs_header)
                            },
                            style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isSelectionMode) ClinicalOnSurface else ClinicalSecondary
                        )
                    }

                    if (isSelectionMode) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ClinicalSurfaceContainerLow,
                                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onSelectAllTimelineEvents)
                                    .testTag("btn_select_all_timeline")
                            ) {
                                Text(
                                    text = "全选",
                                    style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                    color = ClinicalOnSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = ClinicalSurfaceContainer,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onClearTimelineSelection)
                                    .testTag("btn_clear_timeline_selection")
                            ) {
                                Text(
                                    text = "取消",
                                    style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                    color = ClinicalOnSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "共 ${uiState.totalRecordCount} 条记录",
                            style = ClinicalTextStyles.labelMonoSm,
                            color = ClinicalOnSurface
                        )
                    }
                }
            }

            // 4. Timeline Cards (collapsed by default, long-press to multi-select)
            items(
                items = uiState.timelineEvents,
                key = { it.id }
            ) { event ->
                TimelineEventCard(
                    event = event,
                    isSelectionMode = isSelectionMode,
                    isSelected = event.id in uiState.selectedTimelineEventIds,
                    onToggleExpand = { onToggleTimelineEvent(event.id) },
                    onLongPress = { onLongPressTimelineEvent(event.id) }
                )
            }
        }

        // Single-selected Timeline Event Add / Edit Remark Dialog
        val singleSelectedEvent = if (uiState.selectedTimelineEventIds.size == 1) {
            val targetId = uiState.selectedTimelineEventIds.first()
            uiState.timelineEvents.firstOrNull { it.id == targetId }
        } else {
            null
        }
        if (uiState.isRemarkDialogOpen && singleSelectedEvent != null) {
            TimelineEventRemarkDialog(
                event = singleSelectedEvent,
                onDismiss = onCloseRemarkDialog,
                onSaveRemark = onSaveEventRemark
            )
        }
    }
}

@Composable
private fun BiomarkerMonitorCard(
    biomarkers: BiomarkerTelemetry,
    onBlankClickInEditMode: (() -> Unit)?
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onBlankClickInEditMode != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onBlankClickInEditMode
                    )
                } else {
                    Modifier
                }
            )
            .testTag("biomarker_monitor_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ConcentricBiomarkerRacetrack(
                biomarkers = biomarkers,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            RacetrackThresholdLegendRow()

            HorizontalDivider(
                thickness = 1.dp,
                color = ClinicalSurfaceContainer
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (biomarkers.showGlucoseTrack) {
                    val glucoseStatus = biomarkers.glucoseStatus
                    BiomarkerDataRow(
                        dotColor = ClinicalPrimaryContainer,
                        label = stringResource(R.string.ferritin_label),
                        valueText = biomarkers.glucoseValue.toString(),
                        valueColor = if (glucoseStatus != ThresholdStatus.NORMAL) ClinicalPrimaryContainer else ClinicalOnSurface,
                        referenceText = "/${biomarkers.glucoseLimit}",
                        unitText = "mmol/L",
                        thresholdStatus = glucoseStatus
                    )
                }
                if (biomarkers.showSpo2Track) {
                    val spo2Status = biomarkers.spo2Status
                    BiomarkerDataRow(
                        dotColor = ClinicalOnSurface,
                        label = stringResource(R.string.plt_label),
                        valueText = "${biomarkers.spo2Value}%",
                        valueColor = if (spo2Status != ThresholdStatus.NORMAL) ClinicalPrimaryContainer else ClinicalOnSurface,
                        referenceText = "/${biomarkers.spo2MinLimit}%",
                        unitText = "%",
                        thresholdStatus = spo2Status
                    )
                }
                if (biomarkers.showSkinTempTrack) {
                    val skinTempStatus = biomarkers.skinTempStatus
                    BiomarkerDataRow(
                        dotColor = ClinicalSecondary,
                        label = stringResource(R.string.crp_label),
                        valueText = biomarkers.skinTempValue.toString(),
                        valueColor = if (skinTempStatus != ThresholdStatus.NORMAL) ClinicalPrimaryContainer else ClinicalOnSurface,
                        referenceText = "/${biomarkers.skinTempLowLimit}-${biomarkers.skinTempHighLimit}",
                        unitText = "°C",
                        thresholdStatus = skinTempStatus
                    )
                }
            }
        }
    }
}

@Composable
private fun RacetrackThresholdLegendRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Solid Safe Rail
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 6.dp)
                    .background(ClinicalSecondaryContainer, CircleShape)
            )
            Text(
                text = "实心轨: 安全区间",
                style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp),
                color = ClinicalSecondary
            )
        }

        // 2. Threshold Caliper Gate
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 2.5.dp, height = 11.dp)
                    .background(ClinicalOnSurface, CircleShape)
            )
            Text(
                text = "卡尺线: 正常阈值",
                style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp),
                color = ClinicalSecondary
            )
        }

        // 3. Dotted Hazard Zone
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(3.5.dp)
                            .background(ClinicalPrimaryContainer.copy(alpha = 0.65f), CircleShape)
                    )
                }
            }
            Text(
                text = "点阵轨: 超限禁区",
                style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp),
                color = ClinicalSecondary
            )
        }
    }
}

@Composable
private fun BiomarkerDataRow(
    dotColor: Color,
    label: String,
    valueText: String,
    valueColor: Color,
    referenceText: String,
    unitText: String,
    thresholdStatus: ThresholdStatus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(dotColor, CircleShape)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.Medium),
                    color = ClinicalOnSurface
                )
                Text(
                    text = "($unitText)",
                    style = ClinicalTextStyles.bodySm,
                    color = ClinicalSecondary
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = valueText,
                    style = ClinicalTextStyles.dotMatrixValue,
                    color = valueColor
                )
                Text(
                    text = referenceText,
                    style = ClinicalTextStyles.dotMatrixReference,
                    color = ClinicalSecondary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            if (thresholdStatus != ThresholdStatus.NORMAL) {
                PixelThresholdArrow(
                    status = thresholdStatus,
                    color = ClinicalPrimaryContainer,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}

@Composable
fun FourSlotCardsGrid(
    cardSlots: List<ModularCardItem?>,
    isEditMode: Boolean,
    onCardClick: (Int, ModularCardItem) -> Unit,
    onLongPressCard: () -> Unit,
    onDeleteSlot: (Int) -> Unit,
    onPlusSlotClick: (Int) -> Unit,
    onBlankSlotTapInEditMode: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: Slot 0 and Slot 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (slotIndex in 0..1) {
                val card = cardSlots.getOrNull(slotIndex)
                CardSlotCell(
                    slotIndex = slotIndex,
                    card = card,
                    isEditMode = isEditMode,
                    onCardClick = { if (card != null) onCardClick(slotIndex, card) },
                    onLongPress = onLongPressCard,
                    onDeleteClick = { onDeleteSlot(slotIndex) },
                    onPlusClick = { onPlusSlotClick(slotIndex) },
                    onBlankTap = onBlankSlotTapInEditMode,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Row 2: Slot 2 and Slot 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (slotIndex in 2..3) {
                val card = cardSlots.getOrNull(slotIndex)
                CardSlotCell(
                    slotIndex = slotIndex,
                    card = card,
                    isEditMode = isEditMode,
                    onCardClick = { if (card != null) onCardClick(slotIndex, card) },
                    onLongPress = onLongPressCard,
                    onDeleteClick = { onDeleteSlot(slotIndex) },
                    onPlusClick = { onPlusSlotClick(slotIndex) },
                    onBlankTap = onBlankSlotTapInEditMode,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CardSlotCell(
    slotIndex: Int,
    card: ModularCardItem?,
    isEditMode: Boolean,
    onCardClick: () -> Unit,
    onLongPress: () -> Unit,
    onDeleteClick: () -> Unit,
    onPlusClick: () -> Unit,
    onBlankTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = card,
        contentKey = { it?.id ?: "empty_slot" },
        transitionSpec = {
            if (targetState == null) {
                // Deleting a card: outgoing card scales down and fades out smoothly
                (fadeIn(animationSpec = tween(220, delayMillis = 40)) +
                    scaleIn(
                        initialScale = 0.86f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )).togetherWith(
                    fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)) +
                        scaleOut(
                            targetScale = 0.80f,
                            animationSpec = tween(240, easing = FastOutSlowInEasing)
                        )
                )
            } else {
                // Switching or adding a card: outgoing content scales/fades out, new card springs in
                (fadeIn(animationSpec = tween(240, easing = FastOutSlowInEasing)) +
                    scaleIn(
                        initialScale = 0.85f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )).togetherWith(
                    fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                        scaleOut(
                            targetScale = 0.84f,
                            animationSpec = tween(210, easing = FastOutSlowInEasing)
                        )
                )
            }.using(SizeTransform(clip = false))
        },
        modifier = modifier.height(152.dp),
        label = "card_slot_transition_$slotIndex"
    ) { animatedCard ->
        if (animatedCard != null) {
            val cardInteractionSource = remember { MutableInteractionSource() }
            val isCardPressed by cardInteractionSource.collectIsPressedAsState()

            val cardScale by animateFloatAsState(
                targetValue = when {
                    isCardPressed -> 0.95f
                    isEditMode -> 0.975f
                    else -> 1f
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "card_edit_scale_$slotIndex"
            )

            val borderColor by animateColorAsState(
                targetValue = if (isEditMode) ClinicalOnSurface else ClinicalSurfaceContainer,
                animationSpec = tween(durationMillis = 220),
                label = "card_border_color_$slotIndex"
            )

            // Active card in this slot
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ClinicalSurfaceContainerLowest,
                border = BorderStroke(
                    width = 1.dp,
                    color = borderColor
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = cardScale
                        scaleY = cardScale
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .combinedClickable(
                        interactionSource = cardInteractionSource,
                        indication = null,
                        onClick = onCardClick,
                        onLongClick = onLongPress
                    )
                    .testTag("card_slot_$slotIndex")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Row: Characteristic Icon on left; animated Delete button in Edit Mode or short badge in normal mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(ClinicalSurfaceContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            ModularCardFeatureIcon(visualType = animatedCard.visualType)
                        }

                        AnimatedContent(
                            targetState = isEditMode,
                            transitionSpec = {
                                (fadeIn(tween(180)) + scaleIn(
                                    initialScale = 0.6f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )).togetherWith(
                                    fadeOut(tween(150)) + scaleOut(
                                        targetScale = 0.6f,
                                        animationSpec = tween(150)
                                    )
                                ).using(SizeTransform(clip = false))
                            },
                            label = "delete_badge_switch_$slotIndex"
                        ) { editActive ->
                            if (editActive) {
                                val deleteInteraction = remember { MutableInteractionSource() }
                                val isDeletePressed by deleteInteraction.collectIsPressedAsState()
                                val deleteScale by animateFloatAsState(
                                    targetValue = if (isDeletePressed) 0.84f else 1f,
                                    animationSpec = spring(stiffness = Spring.StiffnessHigh),
                                    label = "delete_btn_scale_$slotIndex"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .graphicsLayer {
                                            scaleX = deleteScale
                                            scaleY = deleteScale
                                        }
                                        .clip(CircleShape)
                                        .background(ClinicalPrimaryContainer, CircleShape)
                                        .clickable(
                                            interactionSource = deleteInteraction,
                                            indication = null,
                                            onClick = onDeleteClick
                                        )
                                        .testTag("delete_card_slot_$slotIndex"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "删除卡片",
                                        tint = ClinicalSurface,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            } else if (animatedCard.statusBadge.isNotBlank()) {
                                Surface(
                                    shape = CircleShape,
                                    color = ClinicalSurfaceContainer
                                ) {
                                    Text(
                                        text = animatedCard.statusBadge,
                                        style = ClinicalTextStyles.labelMonoSm,
                                        color = ClinicalOnSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(26.dp))
                            }
                        }
                    }

                    // Middle Row: Characteristic Graphic Motif
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ModularCardGraphicMotif(visualType = animatedCard.visualType)
                    }

                    // Bottom Row: Clean Title & 1-line Subtitle
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = animatedCard.title,
                            style = ClinicalTextStyles.cardTitleSm,
                            color = ClinicalOnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = animatedCard.subtitle,
                            style = ClinicalTextStyles.bodySm,
                            color = ClinicalSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        } else {
            // Empty slot: smoothly scale and fade between '+' slot in Edit Mode and blank space outside Edit Mode
            AnimatedContent(
                targetState = isEditMode,
                transitionSpec = {
                    (fadeIn(tween(200)) + scaleIn(
                        initialScale = 0.84f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )).togetherWith(
                        fadeOut(tween(180)) + scaleOut(
                            targetScale = 0.84f,
                            animationSpec = tween(180, easing = FastOutSlowInEasing)
                        )
                    ).using(SizeTransform(clip = false))
                },
                modifier = Modifier.fillMaxSize(),
                label = "empty_slot_mode_$slotIndex"
            ) { editActive ->
                if (editActive) {
                    val plusInteraction = remember { MutableInteractionSource() }
                    val isPlusPressed by plusInteraction.collectIsPressedAsState()
                    val plusScale by animateFloatAsState(
                        targetValue = if (isPlusPressed) 0.94f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "plus_slot_scale_$slotIndex"
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ClinicalSurfaceContainerLow,
                        border = BorderStroke(1.dp, ClinicalHairlineBorder),
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = plusScale
                                scaleY = plusScale
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = plusInteraction,
                                indication = null,
                                onClick = onPlusClick
                            )
                            .testTag("empty_plus_slot_$slotIndex")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(ClinicalSurfaceContainerLowest, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "添加卡片",
                                    tint = ClinicalOnSurface,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                } else {
                    val blankInteraction = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .combinedClickable(
                                interactionSource = blankInteraction,
                                indication = null,
                                onClick = onBlankTap,
                                onLongClick = onLongPress
                            )
                            .testTag("blank_empty_slot_$slotIndex")
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyDiseaseCalendarCard(
    days: List<CalendarDayItem>,
    selectedDayNumber: String,
    selectedDateKey: String = "2026-10-$selectedDayNumber",
    onSelectDay: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val initialIndex = remember(days) {
        days.indexOfFirst { it.dateKey == "2026-10-08" }
            .takeIf { it >= 0 }
            ?: days.indexOfFirst { it.dateKey == selectedDateKey }.coerceAtLeast(0)
    }
    val stripListState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    var isPerpetualExpanded by remember { mutableStateOf(false) }

    // Real-time visible Year & Month updated dynamically as the user scrolls left/right
    val visibleYearMonthPair by remember(days) {
        derivedStateOf {
            val visibleItems = stripListState.layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val centerInfo = visibleItems[visibleItems.size / 2]
                val centerDay = days.getOrNull(centerInfo.index)
                if (centerDay != null) {
                    Pair(centerDay.year, centerDay.month)
                } else {
                    Pair(2026, 10)
                }
            } else {
                val fallback = days.firstOrNull { it.dateKey == selectedDateKey }
                if (fallback != null) Pair(fallback.year, fallback.month) else Pair(2026, 10)
            }
        }
    }

    val activeYear = visibleYearMonthPair.first
    val activeMonth = visibleYearMonthPair.second
    val dynamicHeaderText = "${activeYear}年${activeMonth}月"

    fun scrollToYearMonth(targetYear: Int, targetMonth: Int) {
        val targetIndex = days.indexOfFirst { it.year == targetYear && it.month == targetMonth && it.dayOfMonth == 1 }
            .takeIf { it >= 0 } ?: return
        coroutineScope.launch {
            stripListState.animateScrollToItem(targetIndex)
        }
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("weekly_calendar_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Header: Clickable Live Year-Month (updates on scroll; tap to toggle secondary perpetual calendar menu)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isPerpetualExpanded = !isPerpetualExpanded }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .testTag("calendar_dynamic_month_header")
            ) {
                Icon(
                    imageVector = Icons.Filled.EventRepeat,
                    contentDescription = null,
                    tint = ClinicalOnSurface,
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = dynamicHeaderText,
                    style = ClinicalTextStyles.cardTitleSm,
                    color = ClinicalOnSurface
                )
            }

            // Widened Horizontally Scrollable Perpetual Date Strip (swipe from Oct back to Jan or earlier)
            LazyRow(
                state = stripListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("perpetual_date_strip"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(
                    items = days,
                    key = { it.dateKey }
                ) { dayItem ->
                    val isSelected = if (days.size > 14) {
                        dayItem.dateKey == selectedDateKey
                    } else {
                        dayItem.dayNumber == selectedDayNumber
                    }
                    val bgColor = if (isSelected) ClinicalOnSurface else ClinicalSurfaceContainerLow
                    val dayLabelColor = if (isSelected) ClinicalSecondaryContainer else ClinicalSecondary
                    val numColor = if (isSelected) ClinicalSurface else ClinicalOnSurface
                    // Red dot ONLY indicates a record was added on this day (no dot if no record added)
                    val hasRecordOnDay = dayItem.hasRecordAdded || dayItem.dotStatus == CalendarDotStatus.ABNORMAL
                    val dotColor = if (hasRecordOnDay) ClinicalPrimaryContainer else Color.Transparent

                    val itemTag = if (dayItem.year == 2026 && dayItem.month == 10) {
                        "calendar_day_${dayItem.dayNumber}"
                    } else {
                        "calendar_day_${dayItem.dateKey}"
                    }

                    Column(
                        modifier = Modifier
                            .width(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(bgColor, RoundedCornerShape(24.dp))
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        width = 2.dp,
                                        color = ClinicalOnSurface.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                } else if (dayItem.isToday) {
                                    Modifier.border(
                                        width = 1.dp,
                                        color = ClinicalOnSurface.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { onSelectDay(dayItem.dateKey) }
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                            .testTag(itemTag),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (dayItem.isToday) "今天" else dayItem.dayOfWeek,
                            style = ClinicalTextStyles.labelMonoSm.copy(
                                fontSize = 11.sp,
                                letterSpacing = 0.2.sp,
                                fontWeight = if (dayItem.isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = dayLabelColor
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = dayItem.dayNumber,
                            style = ClinicalTextStyles.labelMonoLg,
                            color = numColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(dotColor, CircleShape)
                                .testTag("calendar_dot_${dayItem.dateKey}")
                        )
                    }
                }
            }

            // Expandable Full Perpetual Calendar (万年历) with Year/Month Bar & Month Grid
            AnimatedVisibility(
                visible = isPerpetualExpanded,
                enter = expandVertically(tween(220)) + fadeIn(tween(180)),
                exit = shrinkVertically(tween(180)) + fadeOut(tween(150))
            ) {
                val monthDays = remember(days, activeYear, activeMonth) {
                    days.filter { it.year == activeYear && it.month == activeMonth }
                }
                val weekHeaders = remember { listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日") }
                val leadingEmptyCount = remember(monthDays) {
                    val firstDow = monthDays.firstOrNull()?.dayOfWeek ?: "周一"
                    weekHeaders.indexOf(firstDow).coerceAtLeast(0)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .testTag("perpetual_calendar_expanded_panel"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = ClinicalSurfaceContainer)

                    // Year switcher + "回到今天" button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(2025, 2026).forEach { yr ->
                                val isYrSelected = activeYear == yr
                                Surface(
                                    shape = CircleShape,
                                    color = if (isYrSelected) ClinicalOnSurface else ClinicalSurfaceContainerLow,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { scrollToYearMonth(yr, activeMonth) }
                                        .testTag("perpetual_year_$yr")
                                ) {
                                    Text(
                                        text = "${yr}年",
                                        style = ClinicalTextStyles.bodySm.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (isYrSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isYrSelected) ClinicalSurface else ClinicalOnSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = ClinicalSurfaceContainerLow,
                            border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onSelectDay("2026-10-12")
                                    val todayIdx = days.indexOfFirst { it.dateKey == "2026-10-08" }
                                        .takeIf { it >= 0 } ?: 0
                                    coroutineScope.launch {
                                        stripListState.animateScrollToItem(todayIdx)
                                    }
                                }
                                .testTag("btn_jump_to_today")
                        ) {
                            Text(
                                text = "回到今天 (10-12)",
                                style = ClinicalTextStyles.bodySm.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ClinicalOnSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // 1月 .. 12月 Horizontal Quick Month Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (1..12).forEach { m ->
                            val isMonthSelected = activeMonth == m
                            Surface(
                                shape = CircleShape,
                                color = if (isMonthSelected) ClinicalOnSurface else ClinicalSurfaceContainerLow,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { scrollToYearMonth(activeYear, m) }
                                    .testTag("perpetual_month_$m")
                            ) {
                                Text(
                                    text = "${m}月",
                                    style = ClinicalTextStyles.bodySm.copy(
                                        fontSize = 11.sp,
                                        fontWeight = if (isMonthSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isMonthSelected) ClinicalSurface else ClinicalOnSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // 7-Column Weekday Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekHeaders.forEach { wd ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = wd,
                                    style = ClinicalTextStyles.bodySm.copy(fontSize = 10.sp),
                                    color = ClinicalSecondary
                                )
                            }
                        }
                    }

                    // Full Month Days Grid
                    val totalCells = leadingEmptyCount + monthDays.size
                    val rowCount = (totalCells + 6) / 7
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (r in 0 until rowCount) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (c in 0 until 7) {
                                    val cellIdx = r * 7 + c
                                    val dayIdx = cellIdx - leadingEmptyCount
                                    val dayCell = monthDays.getOrNull(dayIdx)
                                    if (dayCell != null) {
                                        val isCellSelected = dayCell.dateKey == selectedDateKey
                                        val hasCellRecord = dayCell.hasRecordAdded || dayCell.dotStatus == CalendarDotStatus.ABNORMAL
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (isCellSelected) ClinicalOnSurface else ClinicalSurfaceContainerLow,
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable {
                                                    onSelectDay(dayCell.dateKey)
                                                    val targetStripIdx = days.indexOfFirst { it.dateKey == dayCell.dateKey }
                                                        .coerceAtLeast(0)
                                                    coroutineScope.launch {
                                                        stripListState.animateScrollToItem(
                                                            (targetStripIdx - 2).coerceAtLeast(0)
                                                        )
                                                    }
                                                }
                                                .padding(vertical = 6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = dayCell.dayNumber,
                                                style = ClinicalTextStyles.labelMonoSm.copy(
                                                    fontWeight = if (isCellSelected || dayCell.isToday) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isCellSelected) ClinicalSurface else ClinicalOnSurface
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .background(
                                                        if (hasCellRecord) ClinicalPrimaryContainer else Color.Transparent,
                                                        CircleShape
                                                    )
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimelineEventCard(
    event: TimelineEvent,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onToggleExpand: () -> Unit,
    onLongPress: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) ClinicalOnSurface else ClinicalSurfaceContainer,
        animationSpec = tween(durationMillis = 180),
        label = "timeline_border_${event.id}"
    )
    val cardScale by animateFloatAsState(
        targetValue = if (isSelected) 0.985f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "timeline_scale_${event.id}"
    )

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .clip(RoundedCornerShape(24.dp))
            .combinedClickable(
                onClick = onToggleExpand,
                onLongClick = onLongPress
            )
            .testTag("timeline_event_${event.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
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
                    AnimatedVisibility(visible = isSelectionMode) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = if (isSelected) ClinicalOnSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (isSelected) ClinicalOnSurface else ClinicalHairlineBorder,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "已选中",
                                    tint = ClinicalSurface,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = event.timestampLabel,
                        style = ClinicalTextStyles.labelMonoSm,
                        color = if (event.isExpanded || isSelected) ClinicalOnSurface else ClinicalSecondary
                    )

                    if (event.badgeText.isNotBlank()) {
                        if (event.isWarningBadge) {
                            Surface(
                                shape = CircleShape,
                                color = ClinicalPrimaryContainer
                            ) {
                                Text(
                                    text = event.badgeText,
                                    style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                    color = ClinicalSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = CircleShape,
                                color = ClinicalSurfaceContainer
                            ) {
                                Text(
                                    text = event.badgeText,
                                    style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                    color = ClinicalOnSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    if (event.remark.isNotBlank()) {
                        Surface(
                            shape = CircleShape,
                            color = ClinicalOnSurface.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, ClinicalOnSurface.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "已备注",
                                style = ClinicalTextStyles.bodySm.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                                color = ClinicalOnSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(ClinicalSurfaceContainerLow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (event.isExpanded) {
                            Icons.Filled.KeyboardArrowDown
                        } else {
                            Icons.AutoMirrored.Filled.KeyboardArrowRight
                        },
                        contentDescription = null,
                        tint = ClinicalSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (!event.isExpanded && event.remark.isNotBlank()) {
                Text(
                    text = "备注：${event.remark}",
                    style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp),
                    color = ClinicalSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            AnimatedVisibility(
                visible = event.isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (event.sourceTag.isNotBlank() || event.attachmentName.isNotBlank()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (event.sourceTag.isNotBlank()) {
                                Surface(
                                    shape = CircleShape,
                                    color = ClinicalSurfaceContainerLow,
                                    border = BorderStroke(1.dp, ClinicalSurfaceContainer)
                                ) {
                                    Text(
                                        text = "来源：${event.sourceTag}",
                                        style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                        color = ClinicalSecondary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            if (event.attachmentName.isNotBlank()) {
                                Surface(
                                    shape = CircleShape,
                                    color = ClinicalSurfaceContainerLow,
                                    border = BorderStroke(1.dp, ClinicalSurfaceContainer)
                                ) {
                                    Text(
                                        text = "文件：${event.attachmentName}",
                                        style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                        color = ClinicalOnSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = event.description,
                        style = ClinicalTextStyles.bodyMd,
                        color = ClinicalOnSurface
                    )

                    if (event.doctorNote != null) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ClinicalSurfaceContainerLow,
                            border = BorderStroke(1.dp, ClinicalSurfaceContainer)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = event.doctorNote.doctorTitle,
                                        style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                                        color = ClinicalSecondary
                                    )
                                    if (event.doctorNote.statusBadge.isNotBlank()) {
                                        Surface(
                                            shape = CircleShape,
                                            color = ClinicalPrimary.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = event.doctorNote.statusBadge,
                                                style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                                color = ClinicalPrimary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = event.doctorNote.content,
                                    style = ClinicalTextStyles.bodySm,
                                    color = ClinicalOnSurface
                                )
                            }
                        }
                    }

                    if (event.remark.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ClinicalSurfaceContainerLow,
                            border = BorderStroke(1.dp, ClinicalOnSurface.copy(alpha = 0.18f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "病程备注",
                                    style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                    color = ClinicalOnSurface
                                )
                                Text(
                                    text = event.remark,
                                    style = ClinicalTextStyles.bodySm,
                                    color = ClinicalOnSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineEventRemarkDialog(
    event: TimelineEvent,
    onDismiss: () -> Unit,
    onSaveRemark: (String) -> Unit
) {
    val isEditingExisting = event.remark.isNotBlank()
    var remarkInput by remember(event.id, event.remark) { mutableStateOf(event.remark) }
    val quickRemarkSuggestions = remember {
        listOf(
            "今日状态平稳，按时服药",
            "服药后轻微胃部不适",
            "待下次门诊复诊时向主任确认",
            "家属已核对医嘱与剂量"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClinicalSurfaceContainerLowest,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = if (isEditingExisting) "修改病程备注" else "添加病程备注",
                        style = ClinicalTextStyles.cardTitleSm,
                        color = ClinicalOnSurface
                    )
                    Text(
                        text = "${event.timestampLabel} ${event.badgeText}".trim(),
                        style = ClinicalTextStyles.bodySm,
                        color = ClinicalSecondary
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "关闭",
                        tint = ClinicalOnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ClinicalSurfaceContainerLow,
                    border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = event.description,
                        style = ClinicalTextStyles.bodySm,
                        color = ClinicalSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                OutlinedTextField(
                    value = remarkInput,
                    onValueChange = { remarkInput = it },
                    label = {
                        Text(
                            text = if (isEditingExisting) "编辑备注内容" else "输入备注内容",
                            style = ClinicalTextStyles.bodySm
                        )
                    },
                    placeholder = {
                        Text(
                            text = "记录当时体征感受、服药反应或复诊待问事项...",
                            style = ClinicalTextStyles.bodySm,
                            color = ClinicalSecondary
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
                        .testTag("timeline_remark_input")
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "快捷备注标签：",
                        style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                        color = ClinicalSecondary
                    )
                    quickRemarkSuggestions.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { suggestion ->
                                Surface(
                                    shape = CircleShape,
                                    color = ClinicalSurfaceContainerLow,
                                    border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(CircleShape)
                                        .clickable {
                                            remarkInput = if (remarkInput.isBlank()) {
                                                suggestion
                                            } else {
                                                "${remarkInput.trimEnd('；', '。')}；$suggestion"
                                            }
                                        }
                                ) {
                                    Text(
                                        text = suggestion,
                                        style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp),
                                        color = ClinicalOnSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        dismissButton = {
            if (isEditingExisting) {
                TextButton(
                    onClick = { onSaveRemark("") },
                    modifier = Modifier.testTag("btn_clear_timeline_remark")
                ) {
                    Text(
                        text = "清空备注",
                        style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.Medium),
                        color = ClinicalPrimaryContainer
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveRemark(remarkInput) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClinicalOnSurface,
                    contentColor = ClinicalSurface
                ),
                modifier = Modifier.testTag("btn_save_timeline_remark")
            ) {
                Text(
                    text = if (isEditingExisting) "保存修改" else "保存备注",
                    style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    )
}

@Composable
private fun DiagnosticHardwareFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(ClinicalOnSurface, CircleShape)
            )
            Text(
                text = stringResource(R.string.footer_engine),
                style = ClinicalTextStyles.bodySm,
                color = ClinicalSecondary
            )
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(ClinicalOnSurface, CircleShape)
            )
        }
        Text(
            text = stringResource(R.string.footer_enclave),
            style = ClinicalTextStyles.bodySm.copy(fontSize = 11.sp),
            color = ClinicalSecondary.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ModularCardDetailDialog(
    card: ModularCardItem,
    onDismiss: () -> Unit,
    onNavigateToAddRecord: () -> Unit,
    onNavigateToDossier: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClinicalSurfaceContainerLowest,
        shape = RoundedCornerShape(24.dp),
        title = {
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
                            .size(36.dp)
                            .background(ClinicalSurfaceContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        ModularCardFeatureIcon(visualType = card.visualType)
                    }
                    Column {
                        Text(
                            text = card.title,
                            style = ClinicalTextStyles.cardTitleSm,
                            color = ClinicalOnSurface
                        )
                        if (card.statusBadge.isNotBlank()) {
                            Text(
                                text = card.statusBadge,
                                style = ClinicalTextStyles.bodySm,
                                color = ClinicalSecondary
                            )
                        }
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "关闭",
                        tint = ClinicalOnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ClinicalSurfaceContainerLow,
                border = BorderStroke(1.dp, ClinicalSurfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = card.detailSummary,
                    style = ClinicalTextStyles.bodyMd,
                    color = ClinicalOnSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }
        },
        confirmButton = {
            val isDossierTarget = card.id == "card_trend" || card.id == "card_peers"
            Button(
                onClick = if (isDossierTarget) onNavigateToDossier else onNavigateToAddRecord,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClinicalOnSurface,
                    contentColor = ClinicalSurface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(ClinicalPrimaryContainer, CircleShape)
                    )
                    Text(
                        text = if (isDossierTarget) "加入门诊问询话术" else "前往添加新记录",
                        style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    )
}

@Composable
fun CardBrowserDialog(
    slotIndex: Int,
    currentSlots: List<ModularCardItem?>,
    cardLibrary: List<ModularCardItem>,
    onDismiss: () -> Unit,
    onSelectCard: (ModularCardItem) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClinicalSurfaceContainerLowest,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "选择卡片",
                    style = ClinicalTextStyles.cardTitleSm,
                    color = ClinicalOnSurface
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "关闭",
                        tint = ClinicalOnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                cardLibrary.forEach { item ->
                    val isUsedInCurrentSlot = currentSlots.getOrNull(slotIndex)?.id == item.id
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isUsedInCurrentSlot) ClinicalSurfaceContainer else ClinicalSurfaceContainerLow,
                        border = BorderStroke(
                            1.dp,
                            if (isUsedInCurrentSlot) ClinicalOnSurface else ClinicalSurfaceContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectCard(item) }
                            .testTag("browser_card_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(ClinicalSurfaceContainerLowest, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ModularCardFeatureIcon(visualType = item.visualType)
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = item.title,
                                        style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.SemiBold),
                                        color = ClinicalOnSurface
                                    )
                                    Text(
                                        text = item.subtitle,
                                        style = ClinicalTextStyles.bodySm,
                                        color = ClinicalSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (item.statusBadge.isNotBlank()) {
                                Surface(
                                    shape = CircleShape,
                                    color = ClinicalSurfaceContainerLowest
                                ) {
                                    Text(
                                        text = item.statusBadge,
                                        style = ClinicalTextStyles.labelMonoSm,
                                        color = ClinicalOnSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
