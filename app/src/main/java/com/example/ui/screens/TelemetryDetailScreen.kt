package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ChecklistItem
import com.example.model.SbarDossier
import com.example.model.SymptomTag
import com.example.ui.components.PulsingBeaconDot
import com.example.ui.theme.ClinicalInverseOnSurface
import com.example.ui.theme.ClinicalInverseSurface
import com.example.ui.theme.ClinicalOnSurface
import com.example.ui.theme.ClinicalPrimaryContainer
import com.example.ui.theme.ClinicalSecondary
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.ClinicalSurfaceContainer
import com.example.ui.theme.ClinicalSurfaceContainerHighest
import com.example.ui.theme.ClinicalSurfaceContainerLow
import com.example.ui.theme.ClinicalSurfaceContainerLowest
import com.example.ui.theme.ClinicalTextStyles
import com.example.viewmodel.TelemetryUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TelemetryDetailScreen(
    uiState: TelemetryUiState,
    onBack: () -> Unit,
    onToggleSymptomTag: (String) -> Unit,
    onUpdateNarrative: (String) -> Unit,
    onGenerateSbar: () -> Unit,
    onToggleChecklistItem: (String) -> Unit,
    onCopyAll: () -> Unit,
    onCadenceDemo: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ClinicalSurface,
        topBar = {
            Surface(
                color = ClinicalSurface.copy(alpha = 0.94f),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("telemetry_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ClinicalOnSurface
                        )
                    }
                    Text(
                        text = stringResource(R.string.title_telemetry_detail).uppercase(),
                        style = ClinicalTextStyles.headlineSm,
                        color = ClinicalOnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .testTag("telemetry_detail_list"),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // System Status Badge & Metadata Header
                item(key = "sys_status_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(ClinicalPrimaryContainer, CircleShape)
                            )
                            Text(
                                text = stringResource(R.string.sys_badge),
                                style = ClinicalTextStyles.labelMonoSm,
                                color = ClinicalSecondary
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = ClinicalSurfaceContainer
                        ) {
                            Text(
                                text = stringResource(R.string.dossier_badge),
                                style = ClinicalTextStyles.labelMonoSm,
                                color = ClinicalOnSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // SECTION 01: Input & Symptoms Picker
                item(key = "section_01_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.section_input_header),
                                style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                                color = ClinicalOnSurface
                            )
                            Text(
                                text = stringResource(R.string.section_input_sub),
                                style = ClinicalTextStyles.bodySm,
                                color = ClinicalSecondary
                            )
                        }
                        Text(
                            text = "共 ${uiState.symptomTags.size} 项",
                            style = ClinicalTextStyles.bodySm,
                            color = ClinicalSecondary
                        )
                    }
                }

                // Symptom Pills FlowRow
                item(key = "symptom_pills") {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.symptomTags.forEach { tag ->
                            SymptomPillButton(
                                tag = tag,
                                onClick = { onToggleSymptomTag(tag.id) }
                            )
                        }
                    }
                }

                // Detailed Clinical Narrative Input Field & Primary Action CTA
                item(key = "narrative_input_card") {
                    NarrativeInputCard(
                        narrativeText = uiState.narrativeText,
                        isGenerating = uiState.isGeneratingSbar,
                        onUpdateNarrative = onUpdateNarrative,
                        onGenerateSbar = onGenerateSbar
                    )
                }

                // SECTION 02: Generated Clinical Dossier (SBAR Framework)
                item(key = "section_02_header") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.section_dossier_header),
                            style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                            color = ClinicalOnSurface
                        )
                        Surface(
                            shape = CircleShape,
                            color = ClinicalSurfaceContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(ClinicalPrimaryContainer, CircleShape)
                                )
                                Text(
                                    text = "预估耗时 ${uiState.sbarDossier.estimatedTotalSeconds}秒",
                                    style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.Medium),
                                    color = ClinicalOnSurface
                                )
                            }
                        }
                    }
                }

                // SBAR 01: Situation & Baseline
                item(key = "sbar_01_card") {
                    SbarSituationCard(
                        dossier = uiState.sbarDossier,
                        isPlayingCadence = uiState.isPlayingCadence,
                        cadenceProgress = uiState.cadenceProgress
                    )
                }

                // SBAR 02: Assessment & Request Guidance
                item(key = "sbar_02_card") {
                    SbarAssessmentCard(dossier = uiState.sbarDossier)
                }

                // SBAR 03: Physical Material Checklist
                item(key = "sbar_03_card") {
                    SbarChecklistCard(
                        items = uiState.sbarDossier.checklistItems,
                        onToggleItem = onToggleChecklistItem
                    )
                }

                // Action Toolbar: Copy, Voice Cadence, Reset
                item(key = "action_toolbar") {
                    DossierActionToolbar(
                        isCopied = uiState.isCopiedFeedback,
                        isPlayingCadence = uiState.isPlayingCadence,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val fullText = buildString {
                                appendLine(uiState.sbarDossier.situationText)
                                uiState.sbarDossier.assessmentItems.forEach {
                                    appendLine("${it.index}. ${it.title}：${it.subtitle}")
                                }
                            }
                            clipboard?.setPrimaryClip(ClipData.newPlainText("SBAR Dossier", fullText))
                            onCopyAll()
                        },
                        onCadence = onCadenceDemo,
                        onReset = onReset
                    )
                }

                // Clinical Notice & Practical Tip
                item(key = "clinical_notice") {
                    ClinicalNoticeBox()
                }
            }
        }
    }
}

@Composable
private fun SymptomPillButton(
    tag: SymptomTag,
    onClick: () -> Unit
) {
    val containerColor = if (tag.isActive) ClinicalInverseSurface else ClinicalSurfaceContainer
    val textColor = if (tag.isActive) ClinicalInverseOnSurface else ClinicalOnSurface
    val dotColor = if (tag.isActive) ClinicalPrimaryContainer else ClinicalSecondary
    val fontWeight = if (tag.isActive) FontWeight.Bold else FontWeight.Normal

    Surface(
        shape = CircleShape,
        color = containerColor,
        shadowElevation = if (tag.isActive) 1.dp else 0.dp,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .testTag("symptom_tag_${tag.id}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(dotColor, CircleShape)
            )
            Text(
                text = tag.label,
                style = ClinicalTextStyles.labelMonoMd.copy(fontWeight = fontWeight),
                color = textColor
            )
        }
    }
}

@Composable
private fun NarrativeInputCard(
    narrativeText: String,
    isGenerating: Boolean,
    onUpdateNarrative: (String) -> Unit,
    onGenerateSbar: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.narrative_header),
                    style = ClinicalTextStyles.labelMonoSm,
                    color = ClinicalSecondary
                )
                Text(
                    text = "${narrativeText.length}/300",
                    style = ClinicalTextStyles.labelMonoSm,
                    color = ClinicalSecondary
                )
            }

            // Recessed Textarea Well
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ClinicalSurfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .padding(10.dp)
                ) {
                    if (narrativeText.isEmpty()) {
                        Text(
                            text = stringResource(R.string.narrative_placeholder),
                            style = ClinicalTextStyles.bodyMd,
                            color = ClinicalSecondary.copy(alpha = 0.7f)
                        )
                    }
                    BasicTextField(
                        value = narrativeText,
                        onValueChange = onUpdateNarrative,
                        textStyle = ClinicalTextStyles.bodyMd.copy(color = ClinicalOnSurface),
                        cursorBrush = SolidColor(ClinicalPrimaryContainer),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("symptom_textarea")
                    )
                }
            }

            // Primary Action CTA
            Button(
                onClick = onGenerateSbar,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClinicalInverseSurface,
                    contentColor = ClinicalInverseOnSurface
                ),
                contentPadding = PaddingValues(vertical = 13.dp, horizontal = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (isGenerating) 0.75f else 1f)
                    .testTag("generate_sbar_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PulsingBeaconDot(color = ClinicalPrimaryContainer, size = 8.dp)
                    Text(
                        text = stringResource(R.string.btn_generate_sbar),
                        style = ClinicalTextStyles.bodyLg
                    )
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = ClinicalInverseOnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SbarSituationCard(
    dossier: SbarDossier,
    isPlayingCadence: Boolean,
    cadenceProgress: Float
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Left 4dp red accent bar matching `w-1 bg-primary-container`
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(ClinicalPrimaryContainer)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.sbar_01_title),
                        style = ClinicalTextStyles.labelMonoSm,
                        color = ClinicalOnSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ClinicalSurfaceContainer
                    ) {
                        Text(
                            text = dossier.situationDurationLabel,
                            style = ClinicalTextStyles.labelMonoSm,
                            color = ClinicalSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = isPlayingCadence) {
                    LinearProgressIndicator(
                        progress = { cadenceProgress },
                        color = ClinicalPrimaryContainer,
                        trackColor = ClinicalSurfaceContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(CircleShape)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ClinicalSurfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dossier.situationText,
                        style = ClinicalTextStyles.bodyMd,
                        color = ClinicalOnSurface,
                        modifier = Modifier
                            .padding(10.dp)
                            .testTag("sbar_situation_text")
                    )
                }
            }
        }
    }
}

@Composable
private fun SbarAssessmentCard(dossier: SbarDossier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sbar_02_title),
                    style = ClinicalTextStyles.labelMonoSm,
                    color = ClinicalOnSurface
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ClinicalSurfaceContainer
                ) {
                    Text(
                        text = dossier.assessmentDurationLabel,
                        style = ClinicalTextStyles.labelMonoSm,
                        color = ClinicalSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            dossier.assessmentItems.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ClinicalSurfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(20.dp)
                                .background(ClinicalSurfaceContainerHighest, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.index.toString(),
                                style = ClinicalTextStyles.labelMonoSm,
                                color = ClinicalOnSurface
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = item.title,
                                style = ClinicalTextStyles.bodyMd.copy(fontWeight = FontWeight.Medium),
                                color = ClinicalOnSurface
                            )
                            Text(
                                text = item.subtitle,
                                style = ClinicalTextStyles.bodySm,
                                color = ClinicalSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SbarChecklistCard(
    items: List<ChecklistItem>,
    onToggleItem: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sbar_03_title),
                    style = ClinicalTextStyles.labelMonoSm,
                    color = ClinicalOnSurface
                )
                Text(
                    text = "共 ${items.size} 项",
                    style = ClinicalTextStyles.bodySm,
                    color = ClinicalSecondary
                )
            }

            items.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ClinicalSurfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onToggleItem(item.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        if (item.isChecked) ClinicalOnSurface else ClinicalPrimaryContainer,
                                        CircleShape
                                    )
                            )
                            Text(
                                text = item.title,
                                style = ClinicalTextStyles.bodyMd.copy(
                                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                color = if (item.isChecked) ClinicalSecondary else ClinicalOnSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (item.isChecked) "已备齐" else item.formatTag,
                            style = ClinicalTextStyles.labelMonoSm,
                            color = ClinicalSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DossierActionToolbar(
    isCopied: Boolean,
    isPlayingCadence: Boolean,
    onCopy: () -> Unit,
    onCadence: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Button 1: Copy Full Text
        Surface(
            shape = CircleShape,
            color = ClinicalSurfaceContainerLowest,
            border = BorderStroke(1.dp, ClinicalSurfaceContainer),
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .clickable(onClick = onCopy)
                .testTag("copy_dossier_btn")
        ) {
            Row(
                modifier = Modifier.padding(vertical = 11.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = null,
                    tint = ClinicalOnSurface,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isCopied) stringResource(R.string.btn_copied) else stringResource(R.string.btn_copy_all),
                    style = ClinicalTextStyles.labelMonoMd.copy(fontWeight = FontWeight.Bold),
                    color = ClinicalOnSurface
                )
            }
        }

        // Button 2: Speech Cadence Demo
        Surface(
            shape = CircleShape,
            color = if (isPlayingCadence) ClinicalSurfaceContainer else ClinicalSurfaceContainerLowest,
            border = BorderStroke(1.dp, ClinicalSurfaceContainer),
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .clickable(onClick = onCadence)
                .testTag("cadence_demo_btn")
        ) {
            Row(
                modifier = Modifier.padding(vertical = 11.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.GraphicEq,
                    contentDescription = null,
                    tint = ClinicalPrimaryContainer,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isPlayingCadence) stringResource(R.string.btn_cadence_playing) else stringResource(R.string.btn_cadence_demo),
                    style = ClinicalTextStyles.labelMonoMd.copy(fontWeight = FontWeight.Bold),
                    color = ClinicalOnSurface
                )
            }
        }

        // Button 3: Reset
        Surface(
            shape = CircleShape,
            color = ClinicalSurfaceContainerLowest,
            border = BorderStroke(1.dp, ClinicalSurfaceContainer),
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .clickable(onClick = onReset)
                .testTag("reset_dossier_btn")
        ) {
            Row(
                modifier = Modifier.padding(vertical = 11.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = ClinicalSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.btn_reset),
                    style = ClinicalTextStyles.labelMonoMd,
                    color = ClinicalSecondary
                )
            }
        }
    }
}

@Composable
private fun ClinicalNoticeBox() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ClinicalSurfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(18.dp)
                    .background(ClinicalSurfaceContainerHighest, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "i",
                    style = ClinicalTextStyles.labelMonoSm,
                    color = ClinicalOnSurface
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = stringResource(R.string.clinical_notice_title),
                    style = ClinicalTextStyles.labelMonoSm,
                    color = ClinicalOnSurface
                )
                Text(
                    text = stringResource(R.string.clinical_notice_body),
                    style = ClinicalTextStyles.bodySm,
                    color = ClinicalSecondary
                )
            }
        }
    }
}
