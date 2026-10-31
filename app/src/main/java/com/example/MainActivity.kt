package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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

    Crossfade(
        targetState = uiState.currentScreen,
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = ClinicalSurface,
                    topBar = {
                        ClinicalMainTopBar(
                            selectedTab = uiState.selectedTab,
                            lastMainTab = uiState.lastMainTab,
                            onSelectTab = { tab -> viewModel.selectBottomTab(tab) },
                            onBackFromAddRecord = viewModel::navigateBackFromAddRecord
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (uiState.selectedTab) {
                            BottomNavTab.CALENDAR -> {
                                DiseaseCalendarScreen(
                                    uiState = uiState,
                                    onSelectDay = viewModel::selectCalendarDay,
                                    onToggleTimelineEvent = viewModel::toggleTimelineEvent,
                                    onLongPressTimelineEvent = viewModel::toggleTimelineEventSelection,
                                    onSelectAllTimelineEvents = viewModel::selectAllTimelineEvents,
                                    onClearTimelineSelection = viewModel::clearTimelineSelection
                                )
                            }

                            BottomNavTab.ADD_RECORD -> {
                                AddTelemetryRecordScreen(
                                    subMode = uiState.addRecordSubMode,
                                    currentBiomarkers = uiState.biomarkers,
                                    medicationPlans = uiState.medicationPlans,
                                    onSelectSubMode = viewModel::setAddRecordSubMode,
                                    onNavigateBack = viewModel::navigateBackFromAddRecord,
                                    onSyncRecognizedIndicators = viewModel::syncRecognizedIndicators,
                                    onAddScannedMedicationBox = viewModel::addScannedMedicationBox,
                                    onSyncMedicationsFromDoctorOrder = viewModel::syncMedicationsFromDoctorOrder,
                                    onAddManualTimelineRecord = viewModel::addManualTimelineRecord
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
                                    onSwitchToAddTab = { viewModel.selectBottomTab(BottomNavTab.ADD_RECORD) },
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

                        // Floating Capsule Button at the bottom: "添加记录" or "生成病程记录" when timeline events are selected
                        AnimatedVisibility(
                            visible = uiState.selectedTab != BottomNavTab.ADD_RECORD,
                            enter = fadeIn() + slideInVertically { it },
                            exit = fadeOut() + slideOutVertically { it },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            FloatingAddRecordCapsuleButton(
                                selectedTimelineCount = if (uiState.selectedTab == BottomNavTab.CALENDAR) {
                                    uiState.selectedTimelineEventIds.size
                                } else {
                                    0
                                },
                                onGenerateTimelineRecord = viewModel::openTelemetryDetail,
                                onClearTimelineSelection = viewModel::clearTimelineSelection,
                                onSelectAddSubMode = { subMode ->
                                    viewModel.selectBottomTab(BottomNavTab.ADD_RECORD, subMode)
                                },
                                onClick = {
                                    viewModel.selectBottomTab(
                                        BottomNavTab.ADD_RECORD,
                                        AddRecordSubMode.SELECTION
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
