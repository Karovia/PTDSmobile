package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AddRecordSubMode
import com.example.model.AppScreen
import com.example.model.BottomNavTab
import com.example.ui.screens.AddTelemetryRecordScreen
import com.example.ui.screens.CardSettingsScreen
import com.example.ui.screens.ClinicalMainTopBar
import com.example.ui.screens.DiseaseCalendarScreen
import com.example.ui.screens.FloatingAddRecordCapsuleButton
import com.example.ui.screens.QuickScanMedicineLookupOverlay
import com.example.ui.screens.TelemetryDetailScreen
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TelemetryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ClinicalTelemetryApp()
            }
        }
    }
}

@Composable
fun ClinicalTelemetryApp(
    viewModel: TelemetryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState.currentScreen,
        transitionSpec = {
            if (targetState == AppScreen.TELEMETRY_DETAIL) {
                (fadeIn(animationSpec = tween(260, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) { fullHeight -> fullHeight / 7 } +
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(260, easing = FastOutSlowInEasing)
                    ))
                    .togetherWith(
                        fadeOut(animationSpec = tween(200)) +
                            scaleOut(
                                targetScale = 0.95f,
                                animationSpec = tween(240, easing = FastOutSlowInEasing)
                            )
                    )
                    .apply { targetContentZIndex = 1f }
            } else {
                (fadeIn(animationSpec = tween(240, easing = LinearOutSlowInEasing)) +
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ))
                    .togetherWith(
                        fadeOut(animationSpec = tween(220, easing = FastOutLinearInEasing)) +
                            slideOutVertically(
                                animationSpec = tween(260, easing = FastOutSlowInEasing)
                            ) { fullHeight -> fullHeight / 6 } +
                            scaleOut(
                                targetScale = 0.95f,
                                animationSpec = tween(250, easing = FastOutSlowInEasing)
                            )
                    )
                    .apply { targetContentZIndex = -1f }
            }.using(SizeTransform(clip = false))
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            AppScreen.TELEMETRY_DETAIL -> {
                TelemetryDetailScreen(
                    uiState = uiState,
                    onBack = viewModel::navigateBackToMain,
                    onRemoveSelectedEvent = viewModel::removeSelectedTimelineEventInGenerator,
                    onToggleSymptomTag = viewModel::toggleSymptomTag,
                    onUpdateNarrative = viewModel::updateNarrativeText,
                    onGenerateSbar = viewModel::generateStructuredSbar,
                    onCopyAll = viewModel::triggerCopyFeedback,
                    onReset = viewModel::resetConsultationInput
                )
            }

            AppScreen.MAIN_TABS -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = ClinicalSurface,
                        topBar = {
                            ClinicalMainTopBar(
                                selectedTab = uiState.selectedTab,
                                lastMainTab = uiState.lastMainTab,
                                onSelectTab = { tab -> viewModel.selectBottomTab(tab) },
                                onBackFromAddRecord = viewModel::navigateBackFromAddRecord,
                                onOpenQuickScan = viewModel::openQuickScan
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = Pair(uiState.selectedTab, uiState.addRecordSubMode),
                                transitionSpec = {
                                    val fromTab = initialState.first
                                    val toTab = targetState.first
                                    when {
                                        // Entering ADD_RECORD sub-screen (e.g. Scan Medication Box) from Home
                                        toTab == BottomNavTab.ADD_RECORD && fromTab != BottomNavTab.ADD_RECORD -> {
                                            (fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
                                                slideInHorizontally(
                                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                                ) { fullWidth -> fullWidth / 3 } +
                                                scaleIn(
                                                    initialScale = 0.96f,
                                                    animationSpec = tween(280, easing = FastOutSlowInEasing)
                                                ))
                                                .togetherWith(
                                                    fadeOut(animationSpec = tween(220, easing = FastOutLinearInEasing)) +
                                                        slideOutHorizontally(
                                                            animationSpec = tween(280, easing = FastOutSlowInEasing)
                                                        ) { fullWidth -> -fullWidth / 6 } +
                                                        scaleOut(
                                                            targetScale = 0.96f,
                                                            animationSpec = tween(260, easing = FastOutSlowInEasing)
                                                        )
                                                )
                                                .apply { targetContentZIndex = 1f }
                                        }

                                        // Returning from ADD_RECORD sub-screen directly back to Home (Calendar or Card Wall)
                                        fromTab == BottomNavTab.ADD_RECORD && toTab != BottomNavTab.ADD_RECORD -> {
                                            (fadeIn(animationSpec = tween(260, easing = LinearOutSlowInEasing)) +
                                                slideInHorizontally(
                                                    animationSpec = tween(280, easing = FastOutSlowInEasing)
                                                ) { fullWidth -> -fullWidth / 5 } +
                                                scaleIn(
                                                    initialScale = 0.96f,
                                                    animationSpec = tween(280, easing = FastOutSlowInEasing)
                                                ))
                                                .togetherWith(
                                                    fadeOut(animationSpec = tween(240, easing = FastOutLinearInEasing)) +
                                                        slideOutHorizontally(
                                                            animationSpec = tween(280, easing = FastOutSlowInEasing)
                                                        ) { fullWidth -> fullWidth / 3 } +
                                                        scaleOut(
                                                            targetScale = 0.95f,
                                                            animationSpec = tween(260, easing = FastOutSlowInEasing)
                                                        )
                                                )
                                                .apply { targetContentZIndex = -1f }
                                        }

                                        // Switching sub-modes within ADD_RECORD
                                        fromTab == BottomNavTab.ADD_RECORD && toTab == BottomNavTab.ADD_RECORD -> {
                                            (fadeIn(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                                                slideInHorizontally(
                                                    animationSpec = tween(240, easing = FastOutSlowInEasing)
                                                ) { fullWidth -> fullWidth / 8 })
                                                .togetherWith(
                                                    fadeOut(animationSpec = tween(180)) +
                                                        slideOutHorizontally(
                                                            animationSpec = tween(220, easing = FastOutSlowInEasing)
                                                        ) { fullWidth -> -fullWidth / 8 }
                                                )
                                        }

                                        // Switching horizontally between Calendar and Card Wall
                                        toTab == BottomNavTab.CARD_SETTINGS -> {
                                            (fadeIn(animationSpec = tween(220)) +
                                                slideInHorizontally(
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                                        stiffness = Spring.StiffnessMediumLow
                                                    )
                                                ) { fullWidth -> fullWidth / 8 })
                                                .togetherWith(
                                                    fadeOut(animationSpec = tween(180)) +
                                                        slideOutHorizontally(
                                                            animationSpec = tween(200, easing = FastOutSlowInEasing)
                                                        ) { fullWidth -> -fullWidth / 8 }
                                                )
                                        }

                                        else -> {
                                            (fadeIn(animationSpec = tween(220)) +
                                                slideInHorizontally(
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                                        stiffness = Spring.StiffnessMediumLow
                                                    )
                                                ) { fullWidth -> -fullWidth / 8 })
                                                .togetherWith(
                                                    fadeOut(animationSpec = tween(180)) +
                                                        slideOutHorizontally(
                                                            animationSpec = tween(200, easing = FastOutSlowInEasing)
                                                        ) { fullWidth -> fullWidth / 8 }
                                                )
                                        }
                                    }.using(SizeTransform(clip = false))
                                },
                                modifier = Modifier.fillMaxSize(),
                                label = "main_tab_animated_content"
                            ) { (currentTab, currentSubMode) ->
                                when (currentTab) {
                                    BottomNavTab.CALENDAR -> {
                                        DiseaseCalendarScreen(
                                            uiState = uiState,
                                            onSelectDay = viewModel::selectCalendarDay,
                                            onToggleTimelineEvent = viewModel::toggleTimelineEvent,
                                            onLongPressTimelineEvent = viewModel::toggleTimelineEventSelection,
                                            onSelectAllTimelineEvents = viewModel::selectAllTimelineEvents,
                                            onClearTimelineSelection = viewModel::clearTimelineSelection,
                                            onCloseRemarkDialog = viewModel::closeTimelineRemarkDialog,
                                            onSaveEventRemark = viewModel::saveSelectedTimelineEventRemark
                                        )
                                    }

                                    BottomNavTab.ADD_RECORD -> {
                                        AddTelemetryRecordScreen(
                                            subMode = currentSubMode,
                                            currentBiomarkers = uiState.biomarkers,
                                            medicationPlans = uiState.medicationPlans,
                                            pendingMedicationPrefill = uiState.pendingMedicationPrefill,
                                            detectedOrderPrefills = uiState.detectedOrderPrefills,
                                            onSelectDetectedOrderPrefill = viewModel::selectDetectedOrderPrefill,
                                            onSelectSubMode = viewModel::setAddRecordSubMode,
                                            onNavigateBack = viewModel::navigateBackFromAddRecord,
                                            onSyncRecognizedIndicators = viewModel::syncRecognizedIndicators,
                                            onAddScannedMedicationBox = viewModel::addScannedMedicationBox,
                                            onSyncMedicationsFromDoctorOrder = viewModel::syncMedicationsFromDoctorOrder,
                                            onAddManualTimelineRecord = viewModel::addManualTimelineRecord,
                                            onSyncUploadedFileToTimeline = { fileName, sourceTag, timestampLabel, summaryDescription, doctorOrderText, remarkText ->
                                                val descWithRemark = if (remarkText.isNotBlank()) {
                                                    "$summaryDescription（备注：${remarkText.trim()}）"
                                                } else {
                                                    summaryDescription
                                                }
                                                viewModel.syncUploadedClinicalFileToTimeline(
                                                    timestampLabel = timestampLabel,
                                                    sourceTag = sourceTag,
                                                    attachmentName = fileName,
                                                    descriptionText = descWithRemark,
                                                    doctorNoteText = doctorOrderText,
                                                    navigateToCalendar = true
                                                )
                                            },
                                            onConfirmMedicationOrderAndJumpToScanBox = { selectedPrefill, allDetectedPrefills, alsoSyncToTimeline, fileName, sourceTag, summaryDescription ->
                                                viewModel.confirmMedicationOrderAndJumpToScanBox(
                                                    primaryPrefill = selectedPrefill,
                                                    allDetectedPrefills = allDetectedPrefills,
                                                    timestampLabel = "10-12 今天 15:20",
                                                    sourceTag = sourceTag,
                                                    attachmentName = fileName,
                                                    descriptionText = summaryDescription,
                                                    doctorNoteText = selectedPrefill.sourceOrderExcerpt,
                                                    alsoSyncTimeline = alsoSyncToTimeline
                                                )
                                            }
                                        )
                                    }

                                    BottomNavTab.CARD_SETTINGS -> {
                                        CardSettingsScreen(
                                            uiState = uiState,
                                            onNavigateBackToCalendar = { viewModel.selectBottomTab(BottomNavTab.CALENDAR) },
                                            onCardClick = viewModel::onCardClicked,
                                            onEnterCardEditMode = viewModel::enterCardEditMode,
                                            onExitCardEditMode = viewModel::exitCardEditMode,
                                            onDeleteCardAtSlot = viewModel::deleteCardAtSlot,
                                            onOpenCardBrowserForSlot = viewModel::openCardBrowserForSlot,
                                            onCloseCardBrowser = viewModel::closeCardBrowser,
                                            onSelectCardForSlot = viewModel::selectCardForSlot,
                                            onCloseDetailModal = viewModel::closeDetailModal,
                                            onSwitchToAddTab = {
                                                viewModel.selectBottomTab(
                                                    BottomNavTab.ADD_RECORD,
                                                    AddRecordSubMode.SCAN_MEDICATION_BOX
                                                )
                                            },
                                            onOpenScanMedicationBox = {
                                                viewModel.selectBottomTab(
                                                    BottomNavTab.ADD_RECORD,
                                                    AddRecordSubMode.SCAN_MEDICATION_BOX
                                                )
                                            },
                                            onToggleMedicationDoseTaken = viewModel::toggleMedicationDoseTaken,
                                            onResetAllMedicationDoses = viewModel::resetAllMedicationDoses,
                                            onOpenTelemetryDetail = viewModel::openTelemetryDetail
                                        )
                                    }
                                }
                            }

                            // Floating Capsule Button at the bottom: "添加记录" or "生成病程记录" when timeline events are selected
                            AnimatedVisibility(
                                visible = uiState.selectedTab != BottomNavTab.ADD_RECORD && !uiState.isQuickScanOpen,
                                enter = fadeIn(animationSpec = tween(220, delayMillis = 50)) +
                                    slideInVertically(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    ) { it / 2 } +
                                    scaleIn(
                                        initialScale = 0.90f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    ),
                                exit = fadeOut(animationSpec = tween(160)) +
                                    slideOutVertically(animationSpec = tween(200)) { it / 2 } +
                                    scaleOut(targetScale = 0.92f, animationSpec = tween(180)),
                                modifier = Modifier.align(Alignment.BottomCenter)
                            ) {
                                val singleSelectedId = if (
                                    uiState.selectedTab == BottomNavTab.CALENDAR &&
                                    uiState.selectedTimelineEventIds.size == 1
                                ) {
                                    uiState.selectedTimelineEventIds.firstOrNull()
                                } else {
                                    null
                                }
                                val singleEventHasRemark = singleSelectedId?.let { id ->
                                    uiState.timelineEvents.find { it.id == id }?.remark?.isNotBlank() == true
                                } ?: false

                                FloatingAddRecordCapsuleButton(
                                    selectedTimelineCount = if (uiState.selectedTab == BottomNavTab.CALENDAR) {
                                        uiState.selectedTimelineEventIds.size
                                    } else {
                                        0
                                    },
                                    selectedSingleEventHasRemark = singleEventHasRemark,
                                    onEditTimelineRemark = viewModel::openTimelineRemarkDialog,
                                    onGenerateTimelineRecord = viewModel::openTelemetryDetail,
                                    onClearTimelineSelection = viewModel::clearTimelineSelection,
                                    onSelectAddSubMode = { subMode ->
                                        viewModel.selectBottomTab(BottomNavTab.ADD_RECORD, subMode)
                                    },
                                    onClick = {
                                        viewModel.selectBottomTab(
                                            BottomNavTab.ADD_RECORD,
                                            AddRecordSubMode.SCAN_MEDICATION_BOX
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Full-Screen Edge-to-Edge Camera Preview & Decision Model Lookup Overlay
                    AnimatedVisibility(
                        visible = uiState.isQuickScanOpen,
                        enter = fadeIn(animationSpec = tween(220)) +
                            scaleIn(
                                initialScale = 0.96f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ),
                        exit = fadeOut(animationSpec = tween(180)) +
                            scaleOut(targetScale = 0.97f, animationSpec = tween(180)),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        QuickScanMedicineLookupOverlay(
                            uiState = uiState,
                            onDismiss = viewModel::closeQuickScan,
                            onMarkDoseTaken = viewModel::toggleMedicationDoseTaken
                        )
                    }
                }
            }
        }
    }
}
