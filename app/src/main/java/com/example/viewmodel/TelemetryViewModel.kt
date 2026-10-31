package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AddRecordSubMode
import com.example.model.AppScreen
import com.example.model.BiomarkerTelemetry
import com.example.model.BottomNavTab
import com.example.model.CalendarDayItem
import com.example.model.CalendarDotStatus
import com.example.model.CardVisualType
import com.example.model.ChecklistItem
import com.example.model.DoctorNote
import com.example.model.MedicationBoxPlan
import com.example.model.ModularCardItem
import com.example.model.SbarDossier
import com.example.model.SbarRequestItem
import com.example.model.SymptomTag
import com.example.model.TimelineEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TelemetryUiState(
    val currentScreen: AppScreen = AppScreen.MAIN_TABS,
    val selectedTab: BottomNavTab = BottomNavTab.CALENDAR,
    val lastMainTab: BottomNavTab = BottomNavTab.CALENDAR,
    val addRecordSubMode: AddRecordSubMode = AddRecordSubMode.SCAN_MEDICATION_BOX,
    val biomarkers: BiomarkerTelemetry = BiomarkerTelemetry(),
    val cardSlots: List<ModularCardItem?> = defaultCardSlots(),
    val cardLibrary: List<ModularCardItem> = defaultCardLibrary(),
    val medicationPlans: List<MedicationBoxPlan> = defaultMedicationPlans(),
    val isCardEditMode: Boolean = false,
    val browsingSlotIndex: Int? = null,
    val detailModalCard: ModularCardItem? = null,
    val calendarDays: List<CalendarDayItem> = defaultCalendarDays(),
    val selectedDayNumber: String = "12",
    val timelineEvents: List<TimelineEvent> = defaultTimelineEvents(),
    val selectedTimelineEventIds: Set<String> = emptySet(),
    val totalRecordCount: Int = 14,
    val symptomTags: List<SymptomTag> = defaultSymptomTags(),
    val narrativeText: String = "",
    val sbarDossier: SbarDossier = defaultSbarDossier(),
    val hasGeneratedScript: Boolean = false,
    val isCopiedFeedback: Boolean = false,
    val isPlayingCadence: Boolean = false,
    val cadenceProgress: Float = 0f,
    val isGeneratingSbar: Boolean = false,
    val isQuickScanOpen: Boolean = false,
    val bannerMessage: String? = null
)

private const val DEFAULT_NARRATIVE =
    "近期复查化验单显示铁蛋白升至980，午后微热。想了解是否需要调整目前托珠单抗周期或做血沉复查。"

private fun defaultMedicationPlans(): List<MedicationBoxPlan> = listOf(
    MedicationBoxPlan(
        id = "med_methylpred",
        drugName = "甲泼尼龙片 (美卓乐)",
        specification = "4mg × 30片/盒",
        frequencyLabel = "每日1次",
        dosePerTimeLabel = "每次4片 (16mg)",
        timingLabel = "早餐后温水送服",
        usageSummary = "每日1次 • 每次4片 (16mg) • 早餐后温水送服",
        totalDosesToday = 1,
        takenDosesToday = 0,
        remainingCountLabel = "余 24 片",
        isAutoSyncedFromOrder = true,
        sourceOrderExcerpt = "查房医嘱：甲泼尼龙 16mg/d 口服"
    ),
    MedicationBoxPlan(
        id = "med_esomeprazole",
        drugName = "艾司奥美拉唑肠溶片",
        specification = "20mg × 7片/盒",
        frequencyLabel = "每日1次",
        dosePerTimeLabel = "每次1片 (20mg)",
        timingLabel = "早餐前30分钟吞服",
        usageSummary = "每日1次 • 每次1片 (20mg) • 早餐前30分钟吞服",
        totalDosesToday = 1,
        takenDosesToday = 0,
        remainingCountLabel = "余 5 片",
        isAutoSyncedFromOrder = true,
        sourceOrderExcerpt = "查房医嘱：配合护胃治疗"
    ),
    MedicationBoxPlan(
        id = "med_calcium_d3",
        drugName = "碳酸钙D3片 (钙尔奇)",
        specification = "600mg × 60片/瓶",
        frequencyLabel = "每日1次",
        dosePerTimeLabel = "每次1片 (600mg)",
        timingLabel = "晚餐后咀嚼服用",
        usageSummary = "每日1次 • 每次1片 (600mg) • 晚餐后咀嚼服用",
        totalDosesToday = 1,
        takenDosesToday = 0,
        remainingCountLabel = "余 42 片",
        isAutoSyncedFromOrder = false,
        sourceOrderExcerpt = "药盒扫描录入"
    )
)

private fun defaultCardLibrary(): List<ModularCardItem> = listOf(
    ModularCardItem(
        id = "card_medication",
        title = "今日吃药打卡",
        subtitle = "早间激素已服 • 晚间钙片待服",
        statusBadge = "2/3 已服",
        visualType = CardVisualType.MEDICATION_CAPSULE,
        detailSummary = "今日分时服药计划：\n• 08:00 早餐前：艾司奥美拉唑 1片（已服）\n• 08:30 早餐后：甲泼尼龙片 16mg / 4片（已服）\n• 20:30 晚餐后：碳酸钙D3片 1片（待服）"
    ),
    ModularCardItem(
        id = "card_rx_sync",
        title = "医嘱同步处方",
        subtitle = "查房医嘱自动生成 2 项用药",
        statusBadge = "医嘱同步",
        visualType = CardVisualType.DOCTOR_ORDER_SYNC,
        detailSummary = "已从李主任医师查房医嘱自动解析并生成用药计划：\n• 甲泼尼龙片：16mg/d（每日1次，每次4片，早餐后）\n• 护胃治疗：艾司奥美拉唑肠溶片（每日1次，每次1片）\n当新增含处方的病程医嘱时，该卡片会自动更新。"
    ),
    ModularCardItem(
        id = "card_med_blister",
        title = "药盒用法清单",
        subtitle = "已绑 3 盒 • 余量与怎么吃",
        statusBadge = "3 盒在服",
        visualType = CardVisualType.MEDICATION_BLISTER,
        detailSummary = "已扫描药盒及用法用量汇总：\n1. 甲泼尼龙片(4mg)：每日1次 • 每次4片 • 早餐后\n2. 艾司奥美拉唑(20mg)：每日1次 • 每次1片 • 餐前\n3. 碳酸钙D3片(600mg)：每日1次 • 每次1片 • 晚餐后"
    ),
    ModularCardItem(
        id = "card_tapering",
        title = "激素递减阶梯",
        subtitle = "当前 16mg/d • 维持观察期",
        statusBadge = "4片/日",
        visualType = CardVisualType.TAPERING_LADDER,
        detailSummary = "甲泼尼龙（美卓乐）阶梯减量轨迹：\n• 09月上旬：24mg/d（6片）冲击巩固\n• 09月下旬：20mg/d（5片）平稳过渡\n• 当前阶段：16mg/d（4片）维持观察2周\n• 下一目标：指标稳定后评估减至 12mg/d（3片）"
    ),
    ModularCardItem(
        id = "card_scan",
        title = "新增化验",
        subtitle = "拍照化验单 / 自动提取",
        statusBadge = "OCR",
        visualType = CardVisualType.SCAN_MATRIX,
        detailSummary = "支持拍摄或导入血常规、生化全项、铁蛋白及血沉化验单，自动提取各项检验指标并同步至核心指标跑道。"
    ),
    ModularCardItem(
        id = "card_trend",
        title = "病程分析",
        subtitle = "多周期指标重叠比对",
        statusBadge = "3周期",
        visualType = CardVisualType.TREND_CURVE,
        detailSummary = "近3个周期铁蛋白与托珠单抗响应比对：\n• 09-02：1,420 ug/L（急性期峰值）\n• 09-18：340 ug/L（首次冲击后回落基线）\n• 10-03：980 ug/L（激素减量期出现轻度波动）"
    ),
    ModularCardItem(
        id = "card_temp",
        title = "体温监测",
        subtitle = "午后微热 • 晨起平稳",
        statusBadge = "37.8°C",
        visualType = CardVisualType.THERMOMETER_WAVE,
        detailSummary = "今日体温分时记录：\n• 08:00 晨起：36.6°C（正常基线）\n• 15:30 午后：37.8°C（轻微回升）\n• 20:00 晚间：36.9°C（已自然回落）"
    ),
    ModularCardItem(
        id = "card_peers",
        title = "转归对照",
        subtitle = "48例同方案恢复样本",
        statusBadge = "79%同轨",
        visualType = CardVisualType.PEERS_RADAR,
        detailSummary = "同类治疗方案（托珠单抗 + 甲泼尼龙递减期）48例转归参考：\n• 79% 在减量期曾出现铁蛋白轻度波动\n• 64% 采取维持当前激素剂量观察2周后平稳回落"
    ),
    ModularCardItem(
        id = "card_joint",
        title = "关节状态",
        subtitle = "双膝双腕 • 活动自如",
        statusBadge = "无压痛",
        visualType = CardVisualType.JOINT_ARC,
        detailSummary = "今日关节体征评估：\n• 晨僵时长：小于 5 分钟\n• 双腕关节：无肿胀、屈伸活动度正常\n• 双膝关节：按压无痛感，步行平稳"
    ),
    ModularCardItem(
        id = "card_rash",
        title = "皮疹状态",
        subtitle = "躯干及四肢红斑消退",
        statusBadge = "已消退",
        visualType = CardVisualType.RASH_CONSTELLATION,
        detailSummary = "今日皮肤体征观察：\n• 前胸及背部：一过性充血性红斑已完全消退\n• 双上肢内侧：无新发皮疹，无瘙痒感"
    ),
    ModularCardItem(
        id = "card_ecg",
        title = "静息心率",
        subtitle = "节律平稳 • 无心悸",
        statusBadge = "72次/分",
        visualType = CardVisualType.ECG_PULSE,
        detailSummary = "今日静息态心率与脉搏监测：\n• 晨起静息心率：72 次/分\n• 午后微热时心率：81 次/分，节律整齐"
    )
)

private fun defaultCardSlots(): List<ModularCardItem?> {
    val lib = defaultCardLibrary()
    return listOf(
        lib[0], // 今日吃药打卡
        lib[1], // 医嘱同步处方
        lib[2], // 药盒用法清单
        lib[3]  // 激素递减阶梯
    )
}

private fun defaultCalendarDays(): List<CalendarDayItem> = listOf(
    CalendarDayItem("周一", "08", CalendarDotStatus.NONE),
    CalendarDayItem("周二", "09", CalendarDotStatus.WARD_ROUND),
    CalendarDayItem("周三", "10", CalendarDotStatus.NONE),
    CalendarDayItem("周四", "11", CalendarDotStatus.ABNORMAL),
    CalendarDayItem("周五", "12", CalendarDotStatus.ABNORMAL),
    CalendarDayItem("周六", "13", CalendarDotStatus.WARD_ROUND),
    CalendarDayItem("周日", "14", CalendarDotStatus.NONE)
)

private fun defaultTimelineEvents(): List<TimelineEvent> = listOf(
    TimelineEvent(
        id = "evt_1",
        timestampLabel = "10-12 09:30",
        badgeText = "查房医嘱",
        isWarningBadge = false,
        description = "铁蛋白回落安全基线；晨起 36.8°C，关节无压痛，皮疹消退。",
        doctorNote = DoctorNote(
            doctorTitle = "李主任医师",
            statusBadge = "方案维持",
            content = "美卓乐 16mg/d 口服 + 护胃药，3日后复查血沉。"
        ),
        isExpanded = false,
        dayNumber = "12"
    ),
    TimelineEvent(
        id = "evt_2",
        timestampLabel = "10-03 16:45",
        badgeText = "化验预警",
        isWarningBadge = true,
        description = "白细胞升至 14.2×10^9/L，铁蛋白反弹，已申请在线会诊。",
        isExpanded = false,
        dayNumber = "11"
    ),
    TimelineEvent(
        id = "evt_3",
        timestampLabel = "09-28 11:15",
        badgeText = "方案调整",
        isWarningBadge = false,
        description = "托珠单抗第 2 疗程静滴顺利，无不良反应。",
        isExpanded = false,
        dayNumber = "09"
    )
)

private fun defaultSymptomTags(): List<SymptomTag> = listOf(
    SymptomTag("tag_fever", "反复发热 (>38.5℃)", false),
    SymptomTag("tag_joint", "关节晨僵 / 肿痛", false),
    SymptomTag("tag_rash", "皮疹 / 充血性红斑", false),
    SymptomTag("tag_lab", "近期化验指标异常", true),
    SymptomTag("tag_med_side", "用药后乏力 / 胃部不适", false),
    SymptomTag("tag_tapering", "激素减量方案咨询", true)
)

private fun defaultSbarDossier(): SbarDossier = SbarDossier(
    estimatedTotalSeconds = 40,
    situationDurationLabel = "约 20 秒",
    situationText = "“主任您好，我患有成人斯蒂尔病。近两周激素按计划递减至每日2片，近3天午后体温偶有回升至37.6℃。10月3日生化检查显示铁蛋白升至980 ng/mL，对比上次基线有轻微反弹。”",
    assessmentDurationLabel = "约 15 秒",
    assessmentItems = listOf(
        SbarRequestItem(
            index = 1,
            title = "调整生物制剂周期间隔",
            subtitle = "目前托珠单抗每4周一次，是否建议短期内缩短为3周或提前巩固一次？"
        ),
        SbarRequestItem(
            index = 2,
            title = "激素阶梯减量暂停研判",
            subtitle = "当前每日2片（美卓乐）是否需维持观察两周，暂缓进一步减至1.5片？"
        )
    ),
    checklistItems = listOf(
        ChecklistItem("chk_1", "14天体温与午后热型折线记录表", "手写或电子表", false),
        ChecklistItem("chk_2", "10月3日血常规/铁蛋白/血沉原件", "纸质单", false),
        ChecklistItem("chk_3", "既往托珠单抗输注批次及签名记录卡", "手册夹页", false)
    )
)

class TelemetryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TelemetryUiState())
    val uiState: StateFlow<TelemetryUiState> = _uiState.asStateFlow()

    private var cadenceJob: Job? = null

    fun openTelemetryDetail() {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.TELEMETRY_DETAIL,
                isCardEditMode = false,
                browsingSlotIndex = null,
                detailModalCard = null
            )
        }
    }

    fun navigateBackToMain() {
        _uiState.update { it.copy(currentScreen = AppScreen.MAIN_TABS) }
    }

    fun selectBottomTab(tab: BottomNavTab, initialSubMode: AddRecordSubMode = AddRecordSubMode.SCAN_MEDICATION_BOX) {
        _uiState.update { state ->
            val updatedLastMain = if (tab != BottomNavTab.ADD_RECORD) tab else state.lastMainTab
            val targetSubMode = if (tab == BottomNavTab.ADD_RECORD) {
                if (initialSubMode == AddRecordSubMode.SELECTION) {
                    AddRecordSubMode.SCAN_MEDICATION_BOX
                } else {
                    initialSubMode
                }
            } else {
                // Preserve current subMode while exiting so AnimatedContent transitions smoothly from the active sub-view directly to Home
                state.addRecordSubMode
            }
            state.copy(
                selectedTab = tab,
                lastMainTab = updatedLastMain,
                addRecordSubMode = targetSubMode,
                isCardEditMode = false,
                browsingSlotIndex = null,
                currentScreen = AppScreen.MAIN_TABS
            )
        }
    }

    fun navigateBackFromAddRecord() {
        _uiState.update { state ->
            state.copy(
                selectedTab = state.lastMainTab,
                isCardEditMode = false,
                browsingSlotIndex = null
            )
        }
    }

    fun setAddRecordSubMode(subMode: AddRecordSubMode) {
        _uiState.update { it.copy(addRecordSubMode = subMode) }
    }

    fun enterCardEditMode() {
        _uiState.update { it.copy(isCardEditMode = true) }
    }

    fun exitCardEditMode() {
        _uiState.update { it.copy(isCardEditMode = false, browsingSlotIndex = null) }
    }

    fun deleteCardAtSlot(slotIndex: Int) {
        if (slotIndex !in 0..3) return
        _uiState.update { state ->
            val updatedSlots = state.cardSlots.toMutableList()
            updatedSlots[slotIndex] = null
            state.copy(cardSlots = updatedSlots)
        }
    }

    fun openCardBrowserForSlot(slotIndex: Int) {
        if (slotIndex !in 0..3) return
        _uiState.update {
            it.copy(
                browsingSlotIndex = slotIndex,
                detailModalCard = null
            )
        }
    }

    fun closeCardBrowser() {
        _uiState.update { it.copy(browsingSlotIndex = null) }
    }

    fun selectCardForSlot(slotIndex: Int, card: ModularCardItem) {
        if (slotIndex !in 0..3) return
        _uiState.update { state ->
            val updatedSlots = state.cardSlots.toMutableList()
            updatedSlots[slotIndex] = card
            state.copy(
                cardSlots = updatedSlots,
                browsingSlotIndex = null
            )
        }
    }

    fun onCardClicked(card: ModularCardItem) {
        when (card.id) {
            "card_scan" -> selectBottomTab(BottomNavTab.ADD_RECORD, AddRecordSubMode.PHOTO_OCR)
            else -> _uiState.update { it.copy(detailModalCard = card) }
        }
    }

    fun closeDetailModal() {
        _uiState.update { it.copy(detailModalCard = null) }
    }

    fun toggleMedicationDoseTaken(planId: String) {
        _uiState.update { state ->
            val updatedPlans = state.medicationPlans.map { plan ->
                if (plan.id == planId) {
                    val nextTaken = if (plan.takenDosesToday >= plan.totalDosesToday) 0 else plan.takenDosesToday + 1
                    plan.copy(takenDosesToday = nextTaken)
                } else {
                    plan
                }
            }
            refreshModularMedicationCards(state.copy(medicationPlans = updatedPlans))
        }
    }

    fun resetAllMedicationDoses() {
        _uiState.update { state ->
            val resetPlans = state.medicationPlans.map { it.copy(takenDosesToday = 0) }
            refreshModularMedicationCards(state.copy(medicationPlans = resetPlans))
        }
    }

    fun addScannedMedicationBox(
        drugName: String,
        specification: String,
        frequencyLabel: String,
        dosePerTimeLabel: String,
        timingLabel: String,
        doctorOrderText: String
    ) {
        val cleanName = drugName.trim()
        if (cleanName.isEmpty()) return

        _uiState.update { state ->
            val cleanSpec = specification.trim().ifEmpty { "标准盒装" }
            val cleanFreq = frequencyLabel.trim().ifEmpty { "每日1次" }
            val cleanDose = dosePerTimeLabel.trim().ifEmpty { "每次1片" }
            val cleanTiming = timingLabel.trim().ifEmpty { "餐后温水送服" }
            val totalDoses = when {
                cleanFreq.contains("6") || cleanFreq.contains("六") -> 6
                cleanFreq.contains("5") || cleanFreq.contains("五") -> 5
                cleanFreq.contains("4") || cleanFreq.contains("四") -> 4
                cleanFreq.contains("3") || cleanFreq.contains("三") -> 3
                cleanFreq.contains("2") || cleanFreq.contains("两") || cleanFreq.contains("二") -> 2
                else -> 1
            }
            val usageSummary = "$cleanFreq • $cleanDose • $cleanTiming"
            val hasOrder = doctorOrderText.isNotBlank()

            val scannedPlan = MedicationBoxPlan(
                id = "med_box_${System.currentTimeMillis()}",
                drugName = cleanName,
                specification = cleanSpec,
                frequencyLabel = cleanFreq,
                dosePerTimeLabel = cleanDose,
                timingLabel = cleanTiming,
                usageSummary = usageSummary,
                totalDosesToday = totalDoses,
                takenDosesToday = 0,
                remainingCountLabel = "新开盒 • 满量",
                isAutoSyncedFromOrder = hasOrder,
                sourceOrderExcerpt = if (hasOrder) "医嘱关联：${doctorOrderText.trim()}" else "药盒扫描录入 • 已设定用法用量"
            )

            // Also parse any extra medications mentioned in doctorOrderText
            val orderExtractedPlans = if (hasOrder) {
                parseMedicationsFromDoctorOrder(doctorOrderText.trim())
                    .filterNot { extracted ->
                        cleanName.contains(extracted.drugName.take(3)) ||
                            extracted.drugName.contains(cleanName.take(3))
                    }
            } else {
                emptyList()
            }

            val mergedPlans = listOf(scannedPlan) + orderExtractedPlans + state.medicationPlans.filterNot {
                it.drugName == cleanName
            }

            val newTimelineEvent = TimelineEvent(
                id = "evt_med_${System.currentTimeMillis()}",
                timestampLabel = "10-12 刚刚",
                badgeText = if (hasOrder) "药盒+医嘱同步" else "扫描药盒",
                isWarningBadge = false,
                description = "录入用药【$cleanName ($cleanSpec)】：$usageSummary。" +
                    if (orderExtractedPlans.isNotEmpty()) {
                        " 同时从医嘱自动同步生成：${orderExtractedPlans.joinToString("、") { it.drugName }}。"
                    } else {
                        ""
                    },
                doctorNote = if (hasOrder) {
                    DoctorNote(
                        doctorTitle = "关联用药医嘱",
                        statusBadge = "自动同步",
                        content = doctorOrderText.trim()
                    )
                } else {
                    null
                },
                isExpanded = false,
                dayNumber = state.selectedDayNumber
            )

            val updatedState = state.copy(
                medicationPlans = mergedPlans,
                timelineEvents = listOf(newTimelineEvent) + state.timelineEvents,
                totalRecordCount = state.totalRecordCount + 1,
                selectedTab = BottomNavTab.CARD_SETTINGS,
                lastMainTab = BottomNavTab.CARD_SETTINGS,
                bannerMessage = if (orderExtractedPlans.isNotEmpty()) {
                    "药盒已录入，并从医嘱自动同步生成 ${orderExtractedPlans.size + 1} 项吃药卡片"
                } else {
                    "药盒【$cleanName】及用法用量已同步至卡片墙"
                }
            )
            refreshModularMedicationCards(updatedState)
        }
        clearBannerAfterDelay()
    }

    fun syncMedicationsFromDoctorOrder(orderText: String) {
        val cleanOrder = orderText.trim()
        if (cleanOrder.isEmpty()) return

        val extracted = parseMedicationsFromDoctorOrder(cleanOrder)
        if (extracted.isEmpty()) return

        _uiState.update { state ->
            val existingFiltered = state.medicationPlans.filterNot { existing ->
                extracted.any { ext ->
                    existing.drugName.contains(ext.drugName.take(3)) ||
                        ext.drugName.contains(existing.drugName.take(3))
                }
            }
            val mergedPlans = extracted + existingFiltered

            val newEvent = TimelineEvent(
                id = "evt_rx_sync_${System.currentTimeMillis()}",
                timestampLabel = "10-12 刚刚",
                badgeText = "医嘱自动同步",
                isWarningBadge = false,
                description = "从医嘱自动提取并同步 ${extracted.size} 项用药方案：${extracted.joinToString("；") { "${it.drugName}（${it.usageSummary}）" }}。",
                doctorNote = DoctorNote(
                    doctorTitle = "处方医嘱原文",
                    statusBadge = "已生成吃药卡",
                    content = cleanOrder
                ),
                isExpanded = false,
                dayNumber = state.selectedDayNumber
            )

            val updatedState = state.copy(
                medicationPlans = mergedPlans,
                timelineEvents = listOf(newEvent) + state.timelineEvents,
                totalRecordCount = state.totalRecordCount + 1,
                selectedTab = BottomNavTab.CARD_SETTINGS,
                lastMainTab = BottomNavTab.CARD_SETTINGS,
                bannerMessage = "已根据医嘱自动生成 ${extracted.size} 项用药用法并同步至卡片墙"
            )
            refreshModularMedicationCards(updatedState)
        }
        clearBannerAfterDelay()
    }

    private fun refreshModularMedicationCards(state: TelemetryUiState): TelemetryUiState {
        val plans = state.medicationPlans
        val totalCount = plans.sumOf { it.totalDosesToday }.coerceAtLeast(1)
        val takenCount = plans.sumOf { it.takenDosesToday }
        val autoSyncedCount = plans.count { it.isAutoSyncedFromOrder }

        val topNames = plans.take(2).joinToString(" • ") { it.drugName.substringBefore(" ") }
        val detailLines = plans.joinToString("\n") { plan ->
            val statusStr = if (plan.takenDosesToday >= plan.totalDosesToday) "已服" else "待服"
            "• ${plan.drugName}：${plan.usageSummary}（$statusStr）"
        }

        fun updateCard(item: ModularCardItem): ModularCardItem = when (item.id) {
            "card_medication" -> item.copy(
                subtitle = if (topNames.isNotEmpty()) topNames else item.subtitle,
                statusBadge = "$takenCount/$totalCount 已服",
                detailSummary = "今日分时服药计划：\n$detailLines"
            )
            "card_rx_sync" -> item.copy(
                subtitle = "医嘱自动同步 $autoSyncedCount 项在服处方",
                statusBadge = "${autoSyncedCount}项同步",
                detailSummary = "医嘱自动解析生成的用药方案：\n" +
                    plans.filter { it.isAutoSyncedFromOrder }.joinToString("\n") {
                        "• ${it.drugName}：${it.usageSummary}"
                    }.ifEmpty { "暂无医嘱自动同步项" }
            )
            "card_med_blister" -> item.copy(
                subtitle = "已绑 ${plans.size} 盒 • 含用法用量",
                statusBadge = "${plans.size} 盒在服",
                detailSummary = "药盒扫描与用法用量清单：\n$detailLines"
            )
            else -> item
        }

        return state.copy(
            cardLibrary = state.cardLibrary.map(::updateCard),
            cardSlots = state.cardSlots.map { it?.let(::updateCard) }
        )
    }

    private fun parseMedicationsFromDoctorOrder(orderText: String): List<MedicationBoxPlan> {
        val clauses = orderText.split("，", ",", "；", ";", "。", "\n", "、")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val results = mutableListOf<MedicationBoxPlan>()
        val now = System.currentTimeMillis()

        clauses.forEachIndexed { idx, clause ->
            val freq = when {
                clause.contains("每日3次") || clause.contains("一天三次") || clause.contains("一日3次") || clause.contains("三餐") -> "每日3次"
                clause.contains("每日2次") || clause.contains("一天两次") || clause.contains("一日2次") || clause.contains("早晚") -> "每日2次"
                clause.contains("每周1次") || clause.contains("每周一次") -> "每周1次"
                clause.contains("隔日") -> "隔日1次"
                else -> "每日1次"
            }
            val totalDoses = when (freq) {
                "每日3次" -> 3
                "每日2次" -> 2
                else -> 1
            }
            val timing = when {
                clause.contains("餐前") || clause.contains("空腹") -> "早餐前30分钟服用"
                clause.contains("睡前") || clause.contains("每晚") || clause.contains("晚间") -> "晚餐后或睡前服用"
                clause.contains("早晚") -> "早晚餐后温水送服"
                else -> "早餐后温水送服"
            }

            // Extract explicit dose like "16mg", "1.5片", "2片", "1片", "1粒"
            val doseRegex = Regex("""(\d+(\.\d+)?\s*(片|粒|mg|毫克|袋|支))""")
            val matchedDoses = doseRegex.findAll(clause).map { it.value }.toList()
            val doseLabel = if (matchedDoses.isNotEmpty()) {
                "每次 " + matchedDoses.joinToString(" / ")
            } else {
                "每次1片 (按医嘱剂量)"
            }

            when {
                clause.contains("甲泼尼龙") || clause.contains("美卓乐") || clause.contains("激素") -> {
                    results.add(
                        MedicationBoxPlan(
                            id = "med_rx_steroid_${now}_$idx",
                            drugName = "甲泼尼龙片 (美卓乐)",
                            specification = "4mg × 30片/盒",
                            frequencyLabel = freq,
                            dosePerTimeLabel = if (clause.contains("16mg")) "每次4片 (16mg)" else if (clause.contains("12mg")) "每次3片 (12mg)" else if (clause.contains("6mg") || clause.contains("1.5片")) "每次1.5片 (6mg)" else doseLabel,
                            timingLabel = timing,
                            usageSummary = "$freq • ${if (clause.contains("16mg")) "每次4片 (16mg)" else doseLabel} • $timing",
                            totalDosesToday = totalDoses,
                            takenDosesToday = 0,
                            remainingCountLabel = "医嘱同步",
                            isAutoSyncedFromOrder = true,
                            sourceOrderExcerpt = "来自医嘱：$clause"
                        )
                    )
                }
                clause.contains("护胃") || clause.contains("奥美拉唑") || clause.contains("雷贝拉唑") || clause.contains("泮托拉唑") -> {
                    results.add(
                        MedicationBoxPlan(
                            id = "med_rx_ppi_${now}_$idx",
                            drugName = "艾司奥美拉唑镁肠溶片",
                            specification = "20mg × 7片/盒",
                            frequencyLabel = freq,
                            dosePerTimeLabel = if (matchedDoses.isNotEmpty()) doseLabel else "每次1片 (20mg)",
                            timingLabel = "早餐前30分钟整片吞服",
                            usageSummary = "$freq • 每次1片 (20mg) • 早餐前30分钟整片吞服",
                            totalDosesToday = totalDoses,
                            takenDosesToday = 0,
                            remainingCountLabel = "医嘱同步",
                            isAutoSyncedFromOrder = true,
                            sourceOrderExcerpt = "来自医嘱：$clause"
                        )
                    )
                }
                clause.contains("钙") -> {
                    results.add(
                        MedicationBoxPlan(
                            id = "med_rx_ca_${now}_$idx",
                            drugName = "碳酸钙D3片 (钙尔奇)",
                            specification = "600mg × 60片/瓶",
                            frequencyLabel = freq,
                            dosePerTimeLabel = if (matchedDoses.isNotEmpty()) doseLabel else "每次1片 (600mg)",
                            timingLabel = "晚餐后或睡前咀嚼服用",
                            usageSummary = "$freq • 每次1片 (600mg) • 晚餐后或睡前咀嚼服用",
                            totalDosesToday = totalDoses,
                            takenDosesToday = 0,
                            remainingCountLabel = "医嘱同步",
                            isAutoSyncedFromOrder = true,
                            sourceOrderExcerpt = "来自医嘱：$clause"
                        )
                    )
                }
                clause.contains("叶酸") -> {
                    results.add(
                        MedicationBoxPlan(
                            id = "med_rx_folic_${now}_$idx",
                            drugName = "叶酸片",
                            specification = "5mg × 30片/盒",
                            frequencyLabel = freq,
                            dosePerTimeLabel = if (matchedDoses.isNotEmpty()) doseLabel else "每次1片 (5mg)",
                            timingLabel = timing,
                            usageSummary = "$freq • ${if (matchedDoses.isNotEmpty()) doseLabel else "每次1片 (5mg)"} • $timing",
                            totalDosesToday = totalDoses,
                            takenDosesToday = 0,
                            remainingCountLabel = "医嘱同步",
                            isAutoSyncedFromOrder = true,
                            sourceOrderExcerpt = "来自医嘱：$clause"
                        )
                    )
                }
                clause.contains("甲氨蝶呤") -> {
                    results.add(
                        MedicationBoxPlan(
                            id = "med_rx_mtx_${now}_$idx",
                            drugName = "甲氨蝶呤片",
                            specification = "2.5mg × 16片/瓶",
                            frequencyLabel = "每周1次",
                            dosePerTimeLabel = if (matchedDoses.isNotEmpty()) doseLabel else "每次4片 (10mg)",
                            timingLabel = "固定每周同一日餐后服用",
                            usageSummary = "每周1次 • ${if (matchedDoses.isNotEmpty()) doseLabel else "每次4片 (10mg)"} • 餐后服用",
                            totalDosesToday = 1,
                            takenDosesToday = 0,
                            remainingCountLabel = "医嘱同步",
                            isAutoSyncedFromOrder = true,
                            sourceOrderExcerpt = "来自医嘱：$clause"
                        )
                    )
                }
                clause.contains("布洛芬") || clause.contains("塞来昔布") || clause.contains("洛索洛芬") -> {
                    val drugTitle = when {
                        clause.contains("塞来昔布") -> "塞来昔布胶囊 (西乐葆)"
                        clause.contains("洛索洛芬") -> "洛索洛芬钠片"
                        else -> "布洛芬缓释胶囊"
                    }
                    results.add(
                        MedicationBoxPlan(
                            id = "med_rx_nsaid_${now}_$idx",
                            drugName = drugTitle,
                            specification = "0.2g × 20粒/盒",
                            frequencyLabel = freq,
                            dosePerTimeLabel = if (matchedDoses.isNotEmpty()) doseLabel else "每次1粒",
                            timingLabel = "餐后温水送服",
                            usageSummary = "$freq • ${if (matchedDoses.isNotEmpty()) doseLabel else "每次1粒"} • 餐后温水送服",
                            totalDosesToday = totalDoses,
                            takenDosesToday = 0,
                            remainingCountLabel = "医嘱同步",
                            isAutoSyncedFromOrder = true,
                            sourceOrderExcerpt = "来自医嘱：$clause"
                        )
                    )
                }
                matchedDoses.isNotEmpty() || clause.contains("口服") || clause.contains("加服") || clause.contains("服用") -> {
                    val cleanDrugTitle = clause
                        .replace("继续", "")
                        .replace("加服", "")
                        .replace("配合", "")
                        .replace("口服", "")
                        .replace(doseRegex, "")
                        .trim()
                        .take(12)
                        .ifEmpty { "医嘱处方用药" }
                    results.add(
                        MedicationBoxPlan(
                            id = "med_rx_custom_${now}_$idx",
                            drugName = cleanDrugTitle,
                            specification = "医嘱处方规格",
                            frequencyLabel = freq,
                            dosePerTimeLabel = doseLabel,
                            timingLabel = timing,
                            usageSummary = "$freq • $doseLabel • $timing",
                            totalDosesToday = totalDoses,
                            takenDosesToday = 0,
                            remainingCountLabel = "医嘱同步",
                            isAutoSyncedFromOrder = true,
                            sourceOrderExcerpt = "来自医嘱：$clause"
                        )
                    )
                }
            }
        }
        return results.distinctBy { it.drugName }
    }

    fun selectCalendarDay(dayNumber: String) {
        _uiState.update { state ->
            state.copy(
                selectedDayNumber = dayNumber,
                timelineEvents = state.timelineEvents.map { it.copy(isExpanded = false) }
            )
        }
    }

    fun toggleTimelineEvent(eventId: String) {
        _uiState.update { state ->
            if (state.selectedTimelineEventIds.isNotEmpty()) {
                val updatedSelection = if (eventId in state.selectedTimelineEventIds) {
                    state.selectedTimelineEventIds - eventId
                } else {
                    state.selectedTimelineEventIds + eventId
                }
                state.copy(selectedTimelineEventIds = updatedSelection)
            } else {
                state.copy(
                    timelineEvents = state.timelineEvents.map { event ->
                        if (event.id == eventId) event.copy(isExpanded = !event.isExpanded) else event
                    }
                )
            }
        }
    }

    fun toggleTimelineEventSelection(eventId: String) {
        _uiState.update { state ->
            val updatedSelection = if (eventId in state.selectedTimelineEventIds) {
                state.selectedTimelineEventIds - eventId
            } else {
                state.selectedTimelineEventIds + eventId
            }
            state.copy(selectedTimelineEventIds = updatedSelection)
        }
    }

    fun selectAllTimelineEvents() {
        _uiState.update { state ->
            state.copy(selectedTimelineEventIds = state.timelineEvents.map { it.id }.toSet())
        }
    }

    fun clearTimelineSelection() {
        _uiState.update { it.copy(selectedTimelineEventIds = emptySet()) }
    }

    fun removeSelectedTimelineEventInGenerator(eventId: String) {
        _uiState.update { state ->
            if (state.selectedTimelineEventIds.size <= 1) return@update state
            state.copy(
                selectedTimelineEventIds = state.selectedTimelineEventIds - eventId,
                hasGeneratedScript = false
            )
        }
    }

    fun toggleSymptomTag(tagId: String) {
        _uiState.update { state ->
            state.copy(
                symptomTags = state.symptomTags.map { tag ->
                    if (tag.id == tagId) tag.copy(isActive = !tag.isActive) else tag
                }
            )
        }
    }

    fun updateNarrativeText(newText: String) {
        val clipped = if (newText.length <= 300) newText else newText.take(300)
        _uiState.update { it.copy(narrativeText = clipped) }
    }

    private fun desensitizeClinicalText(raw: String): String {
        return raw
            .replace(Regex("""[张王李赵陈刘杨黄周吴徐孙胡朱高林何郭马罗][A-Za-z\u4e00-\u9fa5]{0,2}(主任|医师|医生|教授|大夫)"""), "[主治医师]")
            .replace(Regex("""(主任医师|副主任医师|主治医师)\s*[A-Za-z\u4e00-\u9fa5]{2,3}"""), "[主治医师]")
            .replace(Regex("""[\u4e00-\u9fa5]{2,8}(医院|院区|门诊部)"""), "[就诊医院]")
            .replace(Regex("""\b\d{6,18}\b"""), "[病历号已脱敏]")
    }

    fun generateStructuredSbar() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingSbar = true) }
            delay(420)
            _uiState.update { state ->
                val selectedEvents = state.timelineEvents.filter { it.id in state.selectedTimelineEventIds }
                    .ifEmpty { state.timelineEvents.take(1) }
                val activeTags = state.symptomTags.filter { it.isActive }
                val glucose = state.biomarkers.glucoseValue
                val spo2 = state.biomarkers.spo2Value
                val temp = state.biomarkers.skinTempValue
                val customNarrative = desensitizeClinicalText(state.narrativeText.trim())

                val timelineLines = selectedEvents.mapIndexed { index, evt ->
                    val tagPart = if (evt.badgeText.isNotBlank()) "[${evt.badgeText}] " else ""
                    val cleanDesc = desensitizeClinicalText(evt.description.trimEnd('。'))
                    val docPart = evt.doctorNote?.let {
                        " | 关联医嘱：${desensitizeClinicalText(it.content)}"
                    } ?: ""
                    "${index + 1}. ${evt.timestampLabel} $tagPart$cleanDesc$docPart"
                }.joinToString("\n")

                val symptomSummary = buildList {
                    if (activeTags.isNotEmpty()) {
                        add(activeTags.joinToString("、") { it.label })
                    }
                    if (customNarrative.isNotEmpty()) {
                        add(customNarrative)
                    }
                }.joinToString("；").ifEmpty { "近期无明显新增不适" }

                val desensitizedPrompt = buildString {
                    appendLine("【角色与任务】请作为临床沟通助手，基于以下已脱敏的病程记录与近期症状，为[患者本人]生成一份简明、重点突出的门诊复诊问诊话术。")
                    appendLine()
                    appendLine("【已脱敏病程记录（共 ${selectedEvents.size} 项）】")
                    appendLine(timelineLines)
                    appendLine()
                    appendLine("【近期症状与实时体征】")
                    appendLine("• 近期症状：$symptomSummary")
                    appendLine("• 当前体征：血糖 $glucose mmol/L | 血氧 $spo2% | 皮肤温度 ${temp}℃")
                    appendLine()
                    append("【输出要求】用第一人称整理成 30 秒内可口述完的病程汇报，并列出需要向[主治医师]确认的用药与复查问题。")
                }

                val requests = mutableListOf<SbarRequestItem>()
                var idx = 1
                if (activeTags.any { it.id == "tag_lab" } || selectedEvents.any { it.isWarningBadge }) {
                    requests.add(
                        SbarRequestItem(
                            index = idx++,
                            title = "指标波动与复查节点确认",
                            subtitle = "结合已脱敏病程中的化验变化，引导强模型追问是否需调整复查频率或加查炎症指标。"
                        )
                    )
                }
                if (activeTags.any { it.id == "tag_tapering" } || selectedEvents.any { it.doctorNote != null } || requests.isEmpty()) {
                    requests.add(
                        SbarRequestItem(
                            index = idx++,
                            title = "当前用药与激素阶梯评估",
                            subtitle = "结合近期病程恢复情况，生成关于口服药及激素剂量维持或减量的核心问询项。"
                        )
                    )
                }
                if (activeTags.any { it.id == "tag_fever" || it.id == "tag_joint" || it.id == "tag_rash" } || customNarrative.isNotEmpty()) {
                    requests.add(
                        SbarRequestItem(
                            index = idx++,
                            title = "近期症状应对与预警信号",
                            subtitle = "针对近期出现的症状（$symptomSummary），要求话术中包含居家观察预警标准。"
                        )
                    )
                }
                if (activeTags.any { it.id == "tag_med_side" }) {
                    requests.add(
                        SbarRequestItem(
                            index = idx,
                            title = "服药耐受与护胃方案调整",
                            subtitle = "针对服药后不适，加入是否需调整服药时间或护胃方案的问诊要点。"
                        )
                    )
                }

                state.copy(
                    isGeneratingSbar = false,
                    hasGeneratedScript = true,
                    sbarDossier = state.sbarDossier.copy(
                        estimatedTotalSeconds = 20 + selectedEvents.size * 6 + requests.size * 6,
                        situationText = desensitizedPrompt,
                        assessmentItems = requests
                    )
                )
            }
        }
    }

    fun toggleChecklistItem(itemId: String) {
        _uiState.update { state ->
            state.copy(
                sbarDossier = state.sbarDossier.copy(
                    checklistItems = state.sbarDossier.checklistItems.map { item ->
                        if (item.id == itemId) item.copy(isChecked = !item.isChecked) else item
                    }
                )
            )
        }
    }

    fun triggerCopyFeedback() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCopiedFeedback = true) }
            delay(1800)
            _uiState.update { it.copy(isCopiedFeedback = false) }
        }
    }

    fun triggerCadenceDemo() {
        if (_uiState.value.isPlayingCadence) return
        cadenceJob?.cancel()
        cadenceJob = viewModelScope.launch {
            _uiState.update { it.copy(isPlayingCadence = true, cadenceProgress = 0f) }
            val steps = 35
            for (i in 1..steps) {
                delay(100)
                _uiState.update { it.copy(cadenceProgress = i / steps.toFloat()) }
            }
            _uiState.update { it.copy(isPlayingCadence = false, cadenceProgress = 0f) }
        }
    }

    fun resetConsultationInput() {
        _uiState.update { state ->
            state.copy(
                narrativeText = "",
                hasGeneratedScript = false,
                symptomTags = state.symptomTags.map { it.copy(isActive = false) }
            )
        }
    }

    fun syncRecognizedIndicators(
        glucose: Double,
        spo2: Int,
        skinTemp: Double
    ) {
        _uiState.update { state ->
            val newEvent = TimelineEvent(
                id = "evt_ocr_${System.currentTimeMillis()}",
                timestampLabel = "10-12 刚刚",
                badgeText = "化验单OCR",
                isWarningBadge = false,
                description = "监测指标同步：血液内含糖量 $glucose mmol/L，血氧浓度 $spo2%，皮肤温度 $skinTemp °C。",
                isExpanded = false,
                dayNumber = state.selectedDayNumber
            )
            state.copy(
                biomarkers = state.biomarkers.copy(
                    glucoseValue = glucose,
                    spo2Value = spo2,
                    skinTempValue = skinTemp
                ),
                timelineEvents = listOf(newEvent) + state.timelineEvents,
                totalRecordCount = state.totalRecordCount + 1,
                selectedTab = BottomNavTab.CALENDAR,
                lastMainTab = BottomNavTab.CALENDAR,
                bannerMessage = "识别指标已同步至病程日历跑道图"
            )
        }
        clearBannerAfterDelay()
    }

    fun addManualTimelineRecord(
        timestampLabel: String,
        descriptionText: String,
        doctorNoteText: String
    ) {
        val cleanDesc = descriptionText.trim()
        if (cleanDesc.isEmpty()) return

        _uiState.update { state ->
            val cleanDoc = doctorNoteText.trim()
            val autoExtractedMeds = if (cleanDoc.isNotEmpty()) {
                parseMedicationsFromDoctorOrder(cleanDoc)
            } else {
                emptyList()
            }

            val mergedMedicationPlans = if (autoExtractedMeds.isNotEmpty()) {
                val filteredExisting = state.medicationPlans.filterNot { existing ->
                    autoExtractedMeds.any { ext ->
                        existing.drugName.contains(ext.drugName.take(3)) ||
                            ext.drugName.contains(existing.drugName.take(3))
                    }
                }
                autoExtractedMeds + filteredExisting
            } else {
                state.medicationPlans
            }

            val newEvent = TimelineEvent(
                id = "evt_manual_${System.currentTimeMillis()}",
                timestampLabel = timestampLabel.ifBlank { "10-12 刚刚" },
                badgeText = if (autoExtractedMeds.isNotEmpty()) "医嘱用药同步" else "",
                isWarningBadge = false,
                description = cleanDesc,
                doctorNote = if (cleanDoc.isNotEmpty()) {
                    DoctorNote(
                        doctorTitle = "诊疗医嘱",
                        statusBadge = if (autoExtractedMeds.isNotEmpty()) "已同步生成用药" else "",
                        content = cleanDoc
                    )
                } else {
                    null
                },
                isExpanded = false,
                dayNumber = state.selectedDayNumber
            )
            val updatedState = state.copy(
                medicationPlans = mergedMedicationPlans,
                timelineEvents = listOf(newEvent) + state.timelineEvents,
                totalRecordCount = state.totalRecordCount + 1,
                selectedTab = BottomNavTab.CALENDAR,
                lastMainTab = BottomNavTab.CALENDAR,
                bannerMessage = if (autoExtractedMeds.isNotEmpty()) {
                    "病程记录已保存 • 已根据医嘱自动同步生成 ${autoExtractedMeds.size} 项吃药卡片"
                } else {
                    "新病程记录已添加至病程日历下方"
                }
            )
            refreshModularMedicationCards(updatedState)
        }
        clearBannerAfterDelay()
    }

    fun updateCardSettings(
        showGlucose: Boolean? = null,
        showSpo2: Boolean? = null,
        showSkinTemp: Boolean? = null,
        glucoseLimit: Double? = null,
        spo2MinLimit: Int? = null,
        skinTempLowLimit: Double? = null,
        skinTempHighLimit: Double? = null,
        highContrastAlerts: Boolean? = null,
        autoSbarSync: Boolean? = null
    ) {
        _uiState.update { state ->
            state.copy(
                biomarkers = state.biomarkers.copy(
                    showGlucoseTrack = showGlucose ?: state.biomarkers.showGlucoseTrack,
                    showSpo2Track = showSpo2 ?: state.biomarkers.showSpo2Track,
                    showSkinTempTrack = showSkinTemp ?: state.biomarkers.showSkinTempTrack,
                    glucoseLimit = glucoseLimit ?: state.biomarkers.glucoseLimit,
                    spo2MinLimit = spo2MinLimit ?: state.biomarkers.spo2MinLimit,
                    skinTempLowLimit = skinTempLowLimit ?: state.biomarkers.skinTempLowLimit,
                    skinTempHighLimit = skinTempHighLimit ?: state.biomarkers.skinTempHighLimit,
                    highContrastAlerts = highContrastAlerts ?: state.biomarkers.highContrastAlerts,
                    autoSbarSync = autoSbarSync ?: state.biomarkers.autoSbarSync
                )
            )
        }
    }

    fun openQuickScan() {
        _uiState.update { it.copy(isQuickScanOpen = true) }
    }

    fun closeQuickScan() {
        _uiState.update { it.copy(isQuickScanOpen = false) }
    }

    private fun clearBannerAfterDelay() {
        viewModelScope.launch {
            delay(2500)
            _uiState.update { it.copy(bannerMessage = null) }
        }
    }
}
