package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SbarDossier
import com.example.model.SymptomTag
import com.example.model.TimelineEvent
import com.example.ui.components.ClinicalRecordDossierIcon
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
    onRemoveSelectedEvent: (String) -> Unit,
    onToggleSymptomTag: (String) -> Unit,
    onUpdateNarrative: (String) -> Unit,
    onGenerateSbar: () -> Unit,
    onCopyAll: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    val selectedEvents = uiState.timelineEvents
        .filter { it.id in uiState.selectedTimelineEventIds }
        .ifEmpty { uiState.timelineEvents.take(1) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ClinicalSurface,
        topBar = {
            Surface(
                color = ClinicalSurface.copy(alpha = 0.96f),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .fillMaxWidth()
                        .height(62.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onBack)
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("telemetry_detail_back_button"),
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
                    top = 12.dp,
                    bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Combined Timeline Events Card
                item(key = "selected_events_card") {
                    SelectedTimelineEventsCard(
                        selectedEvents = selectedEvents,
                        canRemove = selectedEvents.size > 1,
                        onRemoveEvent = onRemoveSelectedEvent
                    )
                }

                // 2. Recent Symptoms & Request Card
                item(key = "recent_symptoms_card") {
                    RecentSymptomsRequestCard(
                        symptomTags = uiState.symptomTags,
                        narrativeText = uiState.narrativeText,
                        isGenerating = uiState.isGeneratingSbar,
                        hasGenerated = uiState.hasGeneratedScript,
                        onToggleSymptomTag = onToggleSymptomTag,
                        onUpdateNarrative = onUpdateNarrative,
                        onGenerateSbar = onGenerateSbar
                    )
                }

                // 3. Generated Consultation Script Output
                item(key = "generated_script_result") {
                    AnimatedVisibility(
                        visible = uiState.hasGeneratedScript,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        GeneratedConsultationScriptSection(
                            dossier = uiState.sbarDossier,
                            isCopied = uiState.isCopiedFeedback,
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val fullText = buildString {
                                    appendLine(uiState.sbarDossier.situationText)
                                    uiState.sbarDossier.assessmentItems.forEach {
                                        appendLine("${it.index}. ${it.title}：${it.subtitle}")
                                    }
                                }
                                clipboard?.setPrimaryClip(ClipData.newPlainText("问诊话术", fullText))
                                onCopyAll()
                            },
                            onReset = onReset
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedTimelineEventsCard(
    selectedEvents: List<TimelineEvent>,
    canRemove: Boolean,
    onRemoveEvent: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = ClinicalSurfaceContainerLowest,
        border = BorderStroke(1.dp, ClinicalSurfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("selected_timeline_events_card")
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
                    text = "已选病程组合（自动脱敏）",
                    style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                    color = ClinicalSecondary
                )
                Text(
                    text = "共 ${selectedEvents.size} 项",
                    style = ClinicalTextStyles.labelMonoSm,
                    color = ClinicalOnSurface
                )
            }

            selectedEvents.forEachIndexed { index, event ->
                if (index > 0) {
                    HorizontalDivider(thickness = 1.dp, color = ClinicalSurfaceContainer)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = event.timestampLabel,
                                style = ClinicalTextStyles.labelMonoSm,
                                color = ClinicalSecondary
                            )
                            if (event.badgeText.isNotBlank()) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (event.isWarningBadge) ClinicalPrimaryContainer else ClinicalSurfaceContainer
                                ) {
                                    Text(
                                        text = event.badgeText,
                                        style = ClinicalTextStyles.bodySm.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = if (event.isWarningBadge) ClinicalSurface else ClinicalOnSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = event.description,
                            style = ClinicalTextStyles.bodyMd,
                            color = ClinicalOnSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (canRemove) {
                        IconButton(
                            onClick = { onRemoveEvent(event.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "移除该病程",
                                tint = ClinicalSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecentSymptomsRequestCard(
    symptomTags: List<SymptomTag>,
    narrativeText: String,
    isGenerating: Boolean,
    hasGenerated: Boolean,
    onToggleSymptomTag: (String) -> Unit,
    onUpdateNarrative: (String) -> Unit,
    onGenerateSbar: () -> Unit
) {
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
                    text = "是否有近期症状",
                    style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                    color = ClinicalSecondary
                )
                Text(
                    text = "可多选",
                    style = ClinicalTextStyles.labelMonoSm,
                    color = ClinicalSecondary
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                symptomTags.forEach { tag ->
                    SymptomPillButton(
                        tag = tag,
                        onClick = { onToggleSymptomTag(tag.id) }
                    )
                }
            }

            // Optional supplementary symptom text box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ClinicalSurfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp)
                        .padding(12.dp)
                ) {
                    if (narrativeText.isEmpty()) {
                        Text(
                            text = "补充近期症状或想问医生的问题（选填）...",
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

            AnimatedVisibility(visible = isGenerating) {
                LinearProgressIndicator(
                    color = ClinicalPrimaryContainer,
                    trackColor = ClinicalSurfaceContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(CircleShape)
                )
            }

            Button(
                onClick = onGenerateSbar,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClinicalInverseSurface,
                    contentColor = ClinicalInverseOnSurface
                ),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (isGenerating) 0.75f else 1f)
                    .testTag("generate_sbar_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClinicalRecordDossierIcon(
                        tint = ClinicalInverseOnSurface,
                        accentColor = ClinicalPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = when {
                            isGenerating -> "正在脱敏病程并生成提示词..."
                            hasGenerated -> "重新发送请求 · 生成脱敏问诊话术"
                            else -> "发送请求 · 生成脱敏问诊话术"
                        },
                        style = ClinicalTextStyles.bodyLg.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
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
    val containerColor = if (tag.isActive) ClinicalInverseSurface else ClinicalSurfaceContainerLow
    val textColor = if (tag.isActive) ClinicalInverseOnSurface else ClinicalOnSurface
    val fontWeight = if (tag.isActive) FontWeight.Bold else FontWeight.Normal

    Surface(
        shape = CircleShape,
        color = containerColor,
        border = if (tag.isActive) null else BorderStroke(1.dp, ClinicalSurfaceContainer),
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .testTag("symptom_tag_${tag.id}")
    ) {
        Text(
            text = tag.label,
            style = ClinicalTextStyles.labelMonoMd.copy(fontWeight = fontWeight),
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun GeneratedConsultationScriptSection(
    dossier: SbarDossier,
    isCopied: Boolean,
    onCopy: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ClinicalSurfaceContainerLowest,
            border = BorderStroke(1.dp, ClinicalSurfaceContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "脱敏问诊话术提示词（Prompt）",
                            style = ClinicalTextStyles.bodySm.copy(fontWeight = FontWeight.SemiBold),
                            color = ClinicalOnSurface
                        )
                        Surface(
                            shape = CircleShape,
                            color = ClinicalSurfaceContainer
                        ) {
                            Text(
                                text = "已脱敏 · 可喂给强模型",
                                style = ClinicalTextStyles.labelMonoSm,
                                color = ClinicalSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = ClinicalSurfaceContainerLow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = dossier.situationText,
                            style = ClinicalTextStyles.bodyMd,
                            color = ClinicalOnSurface,
                            modifier = Modifier
                                .padding(12.dp)
                                .testTag("sbar_situation_text")
                        )
                    }

                    if (dossier.assessmentItems.isNotEmpty()) {
                        Text(
                            text = "提示词包含的追问维度",
                            style = ClinicalTextStyles.labelMonoSm,
                            color = ClinicalSecondary
                        )

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
        }

        // Clean 2-button toolbar: Copy & Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = ClinicalOnSurface,
                modifier = Modifier
                    .weight(1.6f)
                    .clip(CircleShape)
                    .clickable(onClick = onCopy)
                    .testTag("copy_dossier_btn")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = null,
                        tint = ClinicalSurface,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCopied) "已复制脱敏提示词" else "复制脱敏提示词",
                        style = ClinicalTextStyles.labelMonoMd.copy(fontWeight = FontWeight.Bold),
                        color = ClinicalSurface,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

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
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
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
                        text = "清空重写",
                        style = ClinicalTextStyles.labelMonoMd,
                        color = ClinicalSecondary
                    )
                }
            }
        }
    }
}
