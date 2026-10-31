package com.example.model

enum class AppScreen {
    MAIN_TABS,
    TELEMETRY_DETAIL
}

enum class BottomNavTab {
    CALENDAR,
    ADD_RECORD,
    CARD_SETTINGS
}

enum class AddRecordSubMode {
    SELECTION,
    PHOTO_OCR,
    SCAN_MEDICATION_BOX,
    MANUAL_TIMELINE
}

enum class CardVisualType(val label: String) {
    SCAN_MATRIX("取景扫描"),
    TREND_CURVE("趋势曲线"),
    THERMOMETER_WAVE("体温计标尺"),
    JOINT_ARC("关节活动弧"),
    RASH_CONSTELLATION("皮疹分布点阵"),
    PEERS_RADAR("样本对照环"),
    MEDICATION_CAPSULE("用药胶囊"),
    MEDICATION_BLISTER("药板打卡矩阵"),
    DOCTOR_ORDER_SYNC("医嘱处方同步"),
    TAPERING_LADDER("激素递减阶梯"),
    ECG_PULSE("心电脉冲波")
}

data class ModularCardItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val statusBadge: String,
    val visualType: CardVisualType,
    val detailSummary: String
)

data class MedicationBoxPlan(
    val id: String,
    val drugName: String,
    val specification: String,
    val frequencyLabel: String,
    val dosePerTimeLabel: String,
    val timingLabel: String,
    val usageSummary: String,
    val totalDosesToday: Int = 1,
    val takenDosesToday: Int = 0,
    val remainingCountLabel: String = "余 24 片",
    val isAutoSyncedFromOrder: Boolean = false,
    val sourceOrderExcerpt: String = ""
)

enum class CalendarDotStatus {
    NONE,
    WARD_ROUND,
    ABNORMAL
}

data class CalendarDayItem(
    val dayOfWeek: String,
    val dayNumber: String,
    val dotStatus: CalendarDotStatus
)

data class DoctorNote(
    val doctorTitle: String,
    val statusBadge: String = "",
    val content: String
)

data class TimelineEvent(
    val id: String,
    val timestampLabel: String,
    val badgeText: String = "",
    val isWarningBadge: Boolean = false,
    val description: String,
    val doctorNote: DoctorNote? = null,
    val isExpanded: Boolean = false,
    val dayNumber: String = "12"
)

data class SymptomTag(
    val id: String,
    val label: String,
    val isActive: Boolean
)

data class SbarRequestItem(
    val index: Int,
    val title: String,
    val subtitle: String
)

data class ChecklistItem(
    val id: String,
    val title: String,
    val formatTag: String,
    val isChecked: Boolean = false
)

data class SbarDossier(
    val estimatedTotalSeconds: Int,
    val situationDurationLabel: String,
    val situationText: String,
    val assessmentDurationLabel: String,
    val assessmentItems: List<SbarRequestItem>,
    val checklistItems: List<ChecklistItem>
)

enum class ThresholdStatus {
    NORMAL,
    HIGH,
    LOW
}

data class BiomarkerTelemetry(
    val glucoseValue: Double = 8.6,
    val glucoseLimit: Double = 7.0,
    val spo2Value: Int = 93,
    val spo2MinLimit: Int = 95,
    val skinTempValue: Double = 37.8,
    val skinTempLowLimit: Double = 35.8,
    val skinTempHighLimit: Double = 37.3,
    val showGlucoseTrack: Boolean = true,
    val showSpo2Track: Boolean = true,
    val showSkinTempTrack: Boolean = true,
    val highContrastAlerts: Boolean = true,
    val autoSbarSync: Boolean = true
) {
    val glucoseStatus: ThresholdStatus
        get() = if (glucoseValue > glucoseLimit) ThresholdStatus.HIGH else ThresholdStatus.NORMAL

    val spo2Status: ThresholdStatus
        get() = if (spo2Value < spo2MinLimit) ThresholdStatus.LOW else ThresholdStatus.NORMAL

    val skinTempStatus: ThresholdStatus
        get() = when {
            skinTempValue > skinTempHighLimit -> ThresholdStatus.HIGH
            skinTempValue < skinTempLowLimit -> ThresholdStatus.LOW
            else -> ThresholdStatus.NORMAL
        }
}
