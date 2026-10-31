package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.PathMeasure
import com.example.model.BiomarkerTelemetry
import com.example.model.CardVisualType
import com.example.model.ThresholdStatus
import com.example.ui.theme.ClinicalOnSurface
import com.example.ui.theme.ClinicalPrimaryContainer
import com.example.ui.theme.ClinicalSecondary
import com.example.ui.theme.ClinicalSecondaryContainer
import com.example.ui.theme.ClinicalSurfaceContainerHighest
import com.example.ui.theme.ClinicalSurfaceContainerLowest

@Composable
fun PulsingBeaconDot(
    color: Color = ClinicalPrimaryContainer,
    size: Dp = 6.dp,
    pulseEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (!pulseEnabled) {
        Box(
            modifier = modifier
                .size(size)
                .background(color, CircleShape)
        )
        return
    }
    val infiniteTransition = rememberInfiniteTransition(label = "beacon_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_alpha"
    )
    Box(
        modifier = modifier
            .size(size)
            .alpha(alpha)
            .background(color, CircleShape)
    )
}

/**
 * 8-bit Pixel-Matrix Arrow (↑ for HIGH, ↓ for LOW) matching the Silkscreen dot-matrix readout.
 */
@Composable
fun PixelThresholdArrow(
    status: ThresholdStatus,
    color: Color = ClinicalPrimaryContainer,
    modifier: Modifier = Modifier
) {
    if (status == ThresholdStatus.NORMAL) return

    val isUp = status == ThresholdStatus.HIGH
    Canvas(
        modifier = modifier
            .width(12.dp)
            .height(14.dp)
    ) {
        val cols = 7
        val rows = 8
        val cellW = size.width / cols
        val cellH = size.height / rows
        val px = minOf(cellW, cellH)
        val offsetX = (size.width - px * cols) / 2f
        val offsetY = (size.height - px * rows) / 2f

        // 7x8 pixel matrix for crisp 8-bit arrow
        val upPattern = arrayOf(
            intArrayOf(0, 0, 0, 1, 0, 0, 0),
            intArrayOf(0, 0, 1, 1, 1, 0, 0),
            intArrayOf(0, 1, 1, 1, 1, 1, 0),
            intArrayOf(1, 1, 0, 1, 0, 1, 1),
            intArrayOf(0, 0, 0, 1, 0, 0, 0),
            intArrayOf(0, 0, 0, 1, 0, 0, 0),
            intArrayOf(0, 0, 0, 1, 0, 0, 0),
            intArrayOf(0, 0, 0, 1, 0, 0, 0)
        )

        val downPattern = arrayOf(
            intArrayOf(0, 0, 0, 1, 0, 0, 0),
            intArrayOf(0, 0, 0, 1, 0, 0, 0),
            intArrayOf(0, 0, 0, 1, 0, 0, 0),
            intArrayOf(0, 0, 0, 1, 0, 0, 0),
            intArrayOf(1, 1, 0, 1, 0, 1, 1),
            intArrayOf(0, 1, 1, 1, 1, 1, 0),
            intArrayOf(0, 0, 1, 1, 1, 0, 0),
            intArrayOf(0, 0, 0, 1, 0, 0, 0)
        )

        val pattern = if (isUp) upPattern else downPattern
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (pattern[r][c] == 1) {
                    drawRect(
                        color = color,
                        topLeft = Offset(offsetX + c * px, offsetY + r * px),
                        size = Size(px * 1.02f, px * 1.02f)
                    )
                }
            }
        }
    }
}

/**
 * Hardware Racetrack Concentric Indicator with Threshold Caliper Gates & Dotted Hazard Zones:
 * - Solid recessed track (#E2E2E2) marks the healthy/normal range ONLY.
 * - Dotted rail (•••) marks the over-threshold hazard zone (so users never want to "fill" into the dotted zone).
 * - Crisp perpendicular caliper ticks (┃) mark exact normal threshold boundaries on each ring,
 *   visually splitting any excess value that spills past the threshold gate.
 */
@Composable
fun ConcentricBiomarkerRacetrack(
    biomarkers: BiomarkerTelemetry,
    modifier: Modifier = Modifier
) {
    val bgOuterPath = remember {
        PathParser().parsePathString(
            "M 160 12 L 250 12 A 56 56 0 0 1 250 124 L 70 124 A 56 56 0 0 1 70 12 L 160 12 Z"
        ).toPath()
    }
    val bgMiddlePath = remember {
        PathParser().parsePathString(
            "M 160 26 L 250 26 A 42 42 0 0 1 250 110 L 70 110 A 42 42 0 0 1 70 26 L 160 26 Z"
        ).toPath()
    }
    val bgInnerPath = remember {
        PathParser().parsePathString(
            "M 160 40 L 250 40 A 28 28 0 0 1 250 96 L 70 96 A 28 28 0 0 1 70 40 L 160 40 Z"
        ).toPath()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "racetrack_pip_pulse")
    val pipAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pip_alpha"
    )

    val isGlucoseAlarm = biomarkers.glucoseStatus != ThresholdStatus.NORMAL
    val isSpo2Alarm = biomarkers.spo2Status != ThresholdStatus.NORMAL
    val isSkinTempAlarm = biomarkers.skinTempStatus != ThresholdStatus.NORMAL

    // 1. Outer Track (Blood Glucose on 0..12 mmol/L scale):
    // Normal threshold gate is at glucoseLimit / 12.0 (e.g., 7.0 / 12.0 = 58.3%). Beyond 58.3% is the dotted hazard zone!
    val outerLimitFraction = (biomarkers.glucoseLimit / 12.0).toFloat().coerceIn(0.25f, 0.85f)
    val outerProgress = (biomarkers.glucoseValue / 12.0).toFloat().coerceIn(0.05f, 0.96f)

    // 2. Middle Track (SpO2 % on 0..100% scale):
    // Safe zone is spo2MinLimit..100% (e.g., 95%..100%). Below 95% triggers low alarm.
    val middleLimitFraction = (biomarkers.spo2MinLimit / 100f).coerceIn(0.50f, 0.98f)
    val middleProgress = (biomarkers.spo2Value / 100f).coerceIn(0.05f, 1.0f)

    // 3. Inner Track (Skin Temperature on 34.0..40.0 °C scale):
    // Safe homeostasis window is skinTempLowLimit..skinTempHighLimit (e.g. 35.8..37.3°C -> 30%..55%).
    // Beyond 37.3°C (55%) is the dotted hyperthermia hazard zone!
    val innerLowFraction = ((biomarkers.skinTempLowLimit - 34.0) / 6.0).toFloat().coerceIn(0.12f, 0.50f)
    val innerHighFraction = ((biomarkers.skinTempHighLimit - 34.0) / 6.0).toFloat().coerceIn(0.35f, 0.85f)
    val innerProgress = ((biomarkers.skinTempValue - 34.0) / 6.0).toFloat().coerceIn(0.05f, 0.96f)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth()
                .aspectRatio(2.35f)
        ) {
            val scaleX = size.width / 320f
            val scaleY = size.height / 136f
            val uniformScale = minOf(scaleX, scaleY)

            val trackStroke = Stroke(
                width = 13f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
            val hazardDottedStroke = Stroke(
                width = 4.5f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.5f, 7.5f), 0f)
            )

            val outerMeasure = PathMeasure().apply { setPath(bgOuterPath, false) }
            val middleMeasure = PathMeasure().apply { setPath(bgMiddlePath, false) }
            val innerMeasure = PathMeasure().apply { setPath(bgInnerPath, false) }

            // Outer background: Solid safe container (0 .. limit) + Dotted hazard zone (limit .. end)
            val outerSafeBgPath = Path().also {
                outerMeasure.getSegment(0f, outerMeasure.length * outerLimitFraction, it, true)
            }
            val outerHazardBgPath = Path().also {
                outerMeasure.getSegment(
                    outerMeasure.length * (outerLimitFraction + 0.02f),
                    outerMeasure.length * 0.97f,
                    it,
                    true
                )
            }

            // Inner background: Solid safe container (0 .. highLimit) + Dotted hazard zone (highLimit .. end)
            val innerSafeBgPath = Path().also {
                innerMeasure.getSegment(0f, innerMeasure.length * innerHighFraction, it, true)
            }
            val innerHazardBgPath = Path().also {
                innerMeasure.getSegment(
                    innerMeasure.length * (innerHighFraction + 0.025f),
                    innerMeasure.length * 0.96f,
                    it,
                    true
                )
            }

            val activeOuterPath = Path().also {
                outerMeasure.getSegment(0f, outerMeasure.length * outerProgress, it, true)
            }
            val activeMiddlePath = Path().also {
                middleMeasure.getSegment(0f, middleMeasure.length * middleProgress, it, true)
            }
            val activeInnerPath = Path().also {
                innerMeasure.getSegment(0f, innerMeasure.length * innerProgress, it, true)
            }

            fun drawThresholdGate(
                measure: PathMeasure,
                fraction: Float,
                tickColor: Color
            ) {
                val dist = measure.length * fraction
                val pos = measure.getPosition(dist)
                val tan = measure.getTangent(dist)
                val normal = Offset(-tan.y, tan.x)
                val halfSpan = 6.8f

                // Crisp white knockout separator across the 13px track
                drawLine(
                    color = ClinicalSurfaceContainerLowest,
                    start = pos - normal * (halfSpan + 1.2f),
                    end = pos + normal * (halfSpan + 1.2f),
                    strokeWidth = 5.2f,
                    cap = StrokeCap.Butt
                )
                // Precision caliper gate line inside the knockout
                drawLine(
                    color = tickColor,
                    start = pos - normal * halfSpan,
                    end = pos + normal * halfSpan,
                    strokeWidth = 2.0f,
                    cap = StrokeCap.Round
                )
            }

            scale(scaleX = uniformScale, scaleY = uniformScale, pivot = Offset.Zero) {
                // 1. Base Tracks: Solid for safe zones, Dotted (•••) for over-limit hazard zones
                drawPath(path = outerSafeBgPath, color = ClinicalSecondaryContainer, style = trackStroke)
                drawPath(
                    path = outerHazardBgPath,
                    color = ClinicalPrimaryContainer.copy(alpha = 0.35f),
                    style = hazardDottedStroke
                )

                drawPath(path = bgMiddlePath, color = ClinicalSecondaryContainer, style = trackStroke)

                drawPath(path = innerSafeBgPath, color = ClinicalSecondaryContainer, style = trackStroke)
                drawPath(
                    path = innerHazardBgPath,
                    color = ClinicalSecondary.copy(alpha = 0.42f),
                    style = hazardDottedStroke
                )

                // 2. Outer Track — Blood Glucose (Red #D71920)
                if (biomarkers.showGlucoseTrack) {
                    val outerColor = if (isGlucoseAlarm) {
                        ClinicalPrimaryContainer
                    } else {
                        ClinicalPrimaryContainer.copy(alpha = 0.55f)
                    }
                    drawPath(path = activeOuterPath, color = outerColor, style = trackStroke)

                    // Caliper gate at glucoseLimit (e.g., 7.0 mmol/L) — cuts cleanly across the bar if exceeded
                    drawThresholdGate(
                        measure = outerMeasure,
                        fraction = outerLimitFraction,
                        tickColor = ClinicalOnSurface
                    )

                    if (isGlucoseAlarm) {
                        val tip = outerMeasure.getPosition(outerMeasure.length * outerProgress)
                        drawCircle(
                            color = ClinicalSurfaceContainerLowest.copy(alpha = pipAlpha),
                            radius = 2.6f,
                            center = tip
                        )
                    }
                }

                // 3. Middle Track — SpO2 % (Black #1A1C1C)
                if (biomarkers.showSpo2Track) {
                    val middleColor = if (isSpo2Alarm) {
                        ClinicalOnSurface
                    } else {
                        ClinicalOnSurface.copy(alpha = 0.55f)
                    }
                    drawPath(path = activeMiddlePath, color = middleColor, style = trackStroke)

                    // Minimum safe threshold gate at spo2MinLimit (e.g., 95%)
                    drawThresholdGate(
                        measure = middleMeasure,
                        fraction = middleLimitFraction,
                        tickColor = ClinicalPrimaryContainer
                    )

                    if (isSpo2Alarm) {
                        val tip = middleMeasure.getPosition(middleMeasure.length * middleProgress)
                        drawCircle(
                            color = ClinicalSurfaceContainerLowest.copy(alpha = pipAlpha),
                            radius = 2.6f,
                            center = tip
                        )
                    }
                }

                // 4. Inner Track — Skin Temperature (Grey #5E5E5E)
                if (biomarkers.showSkinTempTrack) {
                    val isInnerHighlighted = isSkinTempAlarm || isGlucoseAlarm
                    val innerColor = if (isInnerHighlighted) {
                        ClinicalSecondary
                    } else {
                        ClinicalSecondary.copy(alpha = 0.48f)
                    }
                    drawPath(path = activeInnerPath, color = innerColor, style = trackStroke)

                    // Lower and Upper homeostasis threshold gates (35.8°C and 37.3°C)
                    drawThresholdGate(
                        measure = innerMeasure,
                        fraction = innerLowFraction,
                        tickColor = ClinicalOnSurface
                    )
                    drawThresholdGate(
                        measure = innerMeasure,
                        fraction = innerHighFraction,
                        tickColor = ClinicalOnSurface
                    )

                    if (isInnerHighlighted) {
                        val tip = innerMeasure.getPosition(innerMeasure.length * innerProgress)
                        drawCircle(
                            color = ClinicalSurfaceContainerLowest.copy(alpha = pipAlpha),
                            radius = 2.6f,
                            center = tip
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dispatcher for the distinctive top-left icon of each modular card type.
 */
@Composable
fun ModularCardFeatureIcon(
    visualType: CardVisualType,
    modifier: Modifier = Modifier
) {
    when (visualType) {
        CardVisualType.SCAN_MATRIX -> ScanMatrixIcon(modifier)
        CardVisualType.TREND_CURVE -> TrendDotsIcon(modifier)
        CardVisualType.THERMOMETER_WAVE -> ThermometerVitalsIcon(modifier)
        CardVisualType.JOINT_ARC -> JointArticulationIcon(modifier)
        CardVisualType.RASH_CONSTELLATION -> RashDermisIcon(modifier)
        CardVisualType.PEERS_RADAR -> PeersRadarIcon(modifier)
        CardVisualType.MEDICATION_CAPSULE -> MedicationCapsuleIcon(modifier)
        CardVisualType.MEDICATION_BLISTER -> MedicationBlisterIcon(modifier)
        CardVisualType.DOCTOR_ORDER_SYNC -> DoctorOrderSyncIcon(modifier)
        CardVisualType.TAPERING_LADDER -> TaperingLadderIcon(modifier)
        CardVisualType.ECG_PULSE -> EcgPulseIcon(modifier)
    }
}

/**
 * Distinctive graphical motif displayed in the middle/right of each modular card so cards rely on
 * characteristic visual telemetry graphics rather than plain numbers.
 */
@Composable
fun ModularCardGraphicMotif(
    visualType: CardVisualType,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .width(68.dp)
            .height(28.dp)
    ) {
        val w = size.width
        val h = size.height

        when (visualType) {
            CardVisualType.SCAN_MATRIX -> {
                // Viewfinder bracket & scan laser line
                val bracketLen = 7.dp.toPx()
                val stroke = 1.6.dp.toPx()
                drawLine(ClinicalOnSurface, Offset(4f, 2f), Offset(4f + bracketLen, 2f), stroke, StrokeCap.Round)
                drawLine(ClinicalOnSurface, Offset(4f, 2f), Offset(4f, 2f + bracketLen), stroke, StrokeCap.Round)
                drawLine(ClinicalOnSurface, Offset(w - 4f, 2f), Offset(w - 4f - bracketLen, 2f), stroke, StrokeCap.Round)
                drawLine(ClinicalOnSurface, Offset(w - 4f, 2f), Offset(w - 4f, 2f + bracketLen), stroke, StrokeCap.Round)
                drawLine(ClinicalOnSurface, Offset(4f, h - 2f), Offset(4f + bracketLen, h - 2f), stroke, StrokeCap.Round)
                drawLine(ClinicalOnSurface, Offset(4f, h - 2f), Offset(4f, h - 2f - bracketLen), stroke, StrokeCap.Round)
                drawLine(ClinicalOnSurface, Offset(w - 4f, h - 2f), Offset(w - 4f - bracketLen, h - 2f), stroke, StrokeCap.Round)
                drawLine(ClinicalOnSurface, Offset(w - 4f, h - 2f), Offset(w - 4f, h - 2f - bracketLen), stroke, StrokeCap.Round)
                // Red scan beam
                drawLine(
                    color = ClinicalPrimaryContainer,
                    start = Offset(10f, h * 0.5f),
                    end = Offset(w - 10f, h * 0.5f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            CardVisualType.TREND_CURVE -> {
                // Multi-cycle overlapping wave curve
                val path = Path().apply {
                    moveTo(2f, h * 0.8f)
                    cubicTo(w * 0.3f, h * 0.75f, w * 0.45f, h * 0.2f, w * 0.7f, h * 0.35f)
                    lineTo(w - 4f, h * 0.15f)
                }
                drawPath(
                    path = path,
                    color = ClinicalOnSurface,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
                drawCircle(
                    color = ClinicalPrimaryContainer,
                    radius = 3.5.dp.toPx(),
                    center = Offset(w - 4f, h * 0.15f)
                )
            }

            CardVisualType.THERMOMETER_WAVE -> {
                // Horizontal mercury gauge track with thermal ticks
                val barY = h * 0.55f
                drawRoundRect(
                    color = ClinicalSurfaceContainerHighest,
                    topLeft = Offset(2f, barY - 3.dp.toPx()),
                    size = Size(w - 4f, 6.dp.toPx()),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                drawRoundRect(
                    color = ClinicalPrimaryContainer,
                    topLeft = Offset(2f, barY - 3.dp.toPx()),
                    size = Size((w - 4f) * 0.68f, 6.dp.toPx()),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                for (i in 0..4) {
                    val x = 6f + i * ((w - 12f) / 4f)
                    drawLine(
                        color = ClinicalSecondary,
                        start = Offset(x, barY - 8.dp.toPx()),
                        end = Offset(x, barY - 5.dp.toPx()),
                        strokeWidth = 1.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            CardVisualType.JOINT_ARC -> {
                // Joint flexion range arc + pivot node
                drawArc(
                    color = ClinicalSurfaceContainerHighest,
                    startAngle = 190f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(w * 0.18f, 3f),
                    size = Size(w * 0.64f, h * 1.4f),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = ClinicalOnSurface,
                    startAngle = 205f,
                    sweepAngle = 115f,
                    useCenter = false,
                    topLeft = Offset(w * 0.18f, 3f),
                    size = Size(w * 0.64f, h * 1.4f),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                drawCircle(
                    color = ClinicalPrimaryContainer,
                    radius = 3.dp.toPx(),
                    center = Offset(w * 0.5f, h * 0.72f)
                )
            }

            CardVisualType.RASH_CONSTELLATION -> {
                // Skin dermis dot-matrix dissipation pattern
                val cols = 6
                val rows = 2
                val spacingX = (w - 12f) / (cols - 1)
                val spacingY = 10.dp.toPx()
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val isHighlighted = (r == 0 && c == 2)
                        val dotColor = when {
                            isHighlighted -> ClinicalPrimaryContainer
                            c < 3 -> ClinicalOnSurface.copy(alpha = 0.65f)
                            else -> ClinicalSurfaceContainerHighest
                        }
                        drawCircle(
                            color = dotColor,
                            radius = if (isHighlighted) 2.8.dp.toPx() else 2.dp.toPx(),
                            center = Offset(6f + c * spacingX, 7f + r * spacingY)
                        )
                    }
                }
            }

            CardVisualType.PEERS_RADAR -> {
                // Concentric cohort distribution rings
                val cx = w * 0.65f
                val cy = h * 0.5f
                drawCircle(
                    color = ClinicalSurfaceContainerHighest,
                    radius = 12.dp.toPx(),
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = ClinicalOnSurface,
                    radius = 7.dp.toPx(),
                    center = Offset(cx, cy),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                    )
                )
                drawCircle(
                    color = ClinicalPrimaryContainer,
                    radius = 2.8.dp.toPx(),
                    center = Offset(cx + 3.dp.toPx(), cy - 2.dp.toPx())
                )
            }

            CardVisualType.MEDICATION_CAPSULE -> {
                // Dose schedule pill blocks
                val blockW = (w - 16f) / 3f
                for (i in 0..2) {
                    val x = 2f + i * (blockW + 6f)
                    val filled = i < 2
                    drawRoundRect(
                        color = if (filled) ClinicalOnSurface else ClinicalSurfaceContainerHighest,
                        topLeft = Offset(x, h * 0.3f),
                        size = Size(blockW, 8.dp.toPx()),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
                drawCircle(
                    color = ClinicalPrimaryContainer,
                    radius = 2.5.dp.toPx(),
                    center = Offset(2f + blockW * 0.5f, h * 0.3f + 4.dp.toPx())
                )
            }

            CardVisualType.MEDICATION_BLISTER -> {
                // 4x2 Blister pack pill matrix
                val cols = 4
                val rows = 2
                val cellW = (w - 8f) / cols
                val cellH = (h - 4f) / rows
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val cx = 4f + c * cellW + cellW * 0.5f
                        val cy = 2f + r * cellH + cellH * 0.5f
                        val isTaken = (r == 0 && c < 3)
                        val isNextDose = (r == 0 && c == 3)
                        drawRoundRect(
                            color = when {
                                isNextDose -> ClinicalPrimaryContainer
                                isTaken -> ClinicalOnSurface
                                else -> ClinicalSurfaceContainerHighest
                            },
                            topLeft = Offset(cx - 5.dp.toPx(), cy - 3.dp.toPx()),
                            size = Size(10.dp.toPx(), 6.dp.toPx()),
                            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                        )
                    }
                }
            }

            CardVisualType.DOCTOR_ORDER_SYNC -> {
                // Prescription Rx wave + sync pulse nodes
                val midY = h * 0.5f
                drawLine(
                    color = ClinicalSurfaceContainerHighest,
                    start = Offset(4f, midY),
                    end = Offset(w - 4f, midY),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = ClinicalOnSurface,
                    start = Offset(4f, midY),
                    end = Offset(w * 0.68f, midY),
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(color = ClinicalOnSurface, radius = 3.5.dp.toPx(), center = Offset(w * 0.22f, midY))
                drawCircle(color = ClinicalOnSurface, radius = 3.5.dp.toPx(), center = Offset(w * 0.48f, midY))
                drawCircle(color = ClinicalPrimaryContainer, radius = 4.2.dp.toPx(), center = Offset(w * 0.74f, midY))
            }

            CardVisualType.TAPERING_LADDER -> {
                // Step-down corticosteroid tapering bars
                val barCount = 4
                val gap = 4.dp.toPx()
                val barW = (w - 8f - gap * (barCount - 1)) / barCount
                val heights = floatArrayOf(0.88f, 0.68f, 0.48f, 0.32f)
                for (i in 0 until barCount) {
                    val barH = h * heights[i]
                    val x = 4f + i * (barW + gap)
                    val y = h - barH - 2f
                    val color = when (i) {
                        2 -> ClinicalPrimaryContainer // Current active step
                        0, 1 -> ClinicalOnSurface
                        else -> ClinicalSurfaceContainerHighest
                    }
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, y),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx())
                    )
                }
            }

            CardVisualType.ECG_PULSE -> {
                // Crisp ECG QRS waveform
                val path = Path().apply {
                    val midY = h * 0.55f
                    moveTo(2f, midY)
                    lineTo(w * 0.28f, midY)
                    lineTo(w * 0.36f, midY - 5.dp.toPx())
                    lineTo(w * 0.44f, midY + 6.dp.toPx())
                    lineTo(w * 0.54f, 3f)
                    lineTo(w * 0.64f, h - 3f)
                    lineTo(w * 0.72f, midY)
                    lineTo(w - 2f, midY)
                }
                drawPath(
                    path = path,
                    color = ClinicalPrimaryContainer,
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

@Composable
fun ScanMatrixIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        val blocks = listOf(
            Offset(3f, 3f), Offset(7f, 3f), Offset(3f, 7f),
            Offset(18f, 3f), Offset(14f, 3f), Offset(18f, 7f),
            Offset(3f, 18f), Offset(3f, 14f), Offset(7f, 18f),
            Offset(18f, 18f), Offset(14f, 18f), Offset(18f, 14f)
        )
        blocks.forEach { topLeft ->
            drawRoundRect(
                color = ClinicalOnSurface,
                topLeft = Offset(topLeft.x * s, topLeft.y * s),
                size = Size(3f * s, 3f * s),
                cornerRadius = CornerRadius(1f * s, 1f * s)
            )
        }
        drawRoundRect(
            color = ClinicalPrimaryContainer,
            topLeft = Offset(10f * s, 11f * s),
            size = Size(4f * s, 2f * s),
            cornerRadius = CornerRadius(0.5f * s, 0.5f * s)
        )
    }
}

@Composable
fun TrendDotsIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        drawCircle(color = ClinicalOnSurface, radius = 2f * s, center = Offset(4f * s, 18f * s))
        drawCircle(color = ClinicalOnSurface, radius = 2f * s, center = Offset(9f * s, 14f * s))
        drawCircle(color = ClinicalOnSurface, radius = 2f * s, center = Offset(14f * s, 8f * s))
        drawCircle(color = ClinicalPrimaryContainer, radius = 2.5f * s, center = Offset(20f * s, 5f * s))
        drawRect(
            color = ClinicalOnSurface.copy(alpha = 0.3f),
            topLeft = Offset(8f * s, 13f * s),
            size = Size(2f * s, 2f * s)
        )
        drawRect(
            color = ClinicalOnSurface.copy(alpha = 0.3f),
            topLeft = Offset(13f * s, 7f * s),
            size = Size(2f * s, 2f * s)
        )
    }
}

@Composable
fun ThermometerVitalsIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        drawRoundRect(
            color = ClinicalOnSurface,
            topLeft = Offset(10f * s, 3f * s),
            size = Size(4f * s, 11f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
        drawCircle(
            color = ClinicalPrimaryContainer,
            radius = 4f * s,
            center = Offset(12f * s, 17f * s)
        )
        drawCircle(
            color = ClinicalSurfaceContainerLowest,
            radius = 1.8f * s,
            center = Offset(12f * s, 17f * s)
        )
    }
}

@Composable
fun JointArticulationIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        // Upper bone segment
        drawLine(
            color = ClinicalOnSurface,
            start = Offset(6f * s, 4f * s),
            end = Offset(11f * s, 10f * s),
            strokeWidth = 3f * s,
            cap = StrokeCap.Round
        )
        // Lower bone segment
        drawLine(
            color = ClinicalOnSurface,
            start = Offset(11f * s, 14f * s),
            end = Offset(18f * s, 20f * s),
            strokeWidth = 3f * s,
            cap = StrokeCap.Round
        )
        // Joint capsule node
        drawCircle(
            color = ClinicalPrimaryContainer,
            radius = 2.6f * s,
            center = Offset(12f * s, 12f * s)
        )
        // Motion wave arc
        drawArc(
            color = ClinicalOnSurface,
            startAngle = -60f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(11f * s, 6f * s),
            size = Size(9f * s, 12f * s),
            style = Stroke(width = 1.6f * s, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun RashDermisIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        val positions = listOf(
            Offset(6f, 6f), Offset(12f, 5f), Offset(18f, 7f),
            Offset(5f, 13f), Offset(17f, 14f),
            Offset(8f, 19f), Offset(14f, 19f)
        )
        positions.forEach { pt ->
            drawCircle(
                color = ClinicalOnSurface,
                radius = 1.8f * s,
                center = Offset(pt.x * s, pt.y * s)
            )
        }
        drawCircle(
            color = ClinicalPrimaryContainer,
            radius = 3f * s,
            center = Offset(11.5f * s, 12f * s)
        )
    }
}

@Composable
fun PeersRadarIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        drawCircle(
            color = ClinicalOnSurface,
            radius = 9f * s,
            center = Offset(12f * s, 12f * s),
            style = Stroke(width = 2f * s)
        )
        drawCircle(
            color = ClinicalOnSurface,
            radius = 5f * s,
            center = Offset(12f * s, 12f * s),
            style = Stroke(
                width = 1.5f * s,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * s, 2f * s), 0f)
            )
        )
        drawCircle(
            color = ClinicalPrimaryContainer,
            radius = 2f * s,
            center = Offset(14f * s, 10f * s)
        )
        drawCircle(
            color = ClinicalOnSurface,
            radius = 1.5f * s,
            center = Offset(9f * s, 14f * s)
        )
    }
}

@Composable
fun MedicationCapsuleIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        drawRoundRect(
            color = ClinicalOnSurface,
            topLeft = Offset(4f * s, 7f * s),
            size = Size(16f * s, 10f * s),
            cornerRadius = CornerRadius(5f * s, 5f * s),
            style = Stroke(width = 2f * s)
        )
        drawRoundRect(
            color = ClinicalPrimaryContainer,
            topLeft = Offset(5f * s, 8f * s),
            size = Size(7f * s, 8f * s),
            cornerRadius = CornerRadius(4f * s, 4f * s)
        )
    }
}

@Composable
fun MedicationBlisterIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        drawRoundRect(
            color = ClinicalOnSurface,
            topLeft = Offset(3f * s, 4f * s),
            size = Size(18f * s, 16f * s),
            cornerRadius = CornerRadius(3f * s, 3f * s),
            style = Stroke(width = 1.8f * s)
        )
        val pills = listOf(
            Offset(8f, 9f) to ClinicalPrimaryContainer,
            Offset(16f, 9f) to ClinicalOnSurface,
            Offset(8f, 15f) to ClinicalOnSurface,
            Offset(16f, 15f) to ClinicalSecondary
        )
        pills.forEach { (pt, color) ->
            drawCircle(
                color = color,
                radius = 2f * s,
                center = Offset(pt.x * s, pt.y * s)
            )
        }
    }
}

@Composable
fun DoctorOrderSyncIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        // Clipboard / prescription sheet outline
        drawRoundRect(
            color = ClinicalOnSurface,
            topLeft = Offset(4f * s, 3f * s),
            size = Size(16f * s, 18f * s),
            cornerRadius = CornerRadius(3f * s, 3f * s),
            style = Stroke(width = 1.8f * s)
        )
        // Cross / Rx indicator in Signal Red
        drawLine(
            color = ClinicalPrimaryContainer,
            start = Offset(9f * s, 9f * s),
            end = Offset(15f * s, 9f * s),
            strokeWidth = 2f * s,
            cap = StrokeCap.Round
        )
        drawLine(
            color = ClinicalPrimaryContainer,
            start = Offset(12f * s, 6f * s),
            end = Offset(12f * s, 12f * s),
            strokeWidth = 2f * s,
            cap = StrokeCap.Round
        )
        // Bottom order line
        drawLine(
            color = ClinicalOnSurface,
            start = Offset(8f * s, 16f * s),
            end = Offset(16f * s, 16f * s),
            strokeWidth = 1.8f * s,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun TaperingLadderIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        drawRoundRect(
            color = ClinicalOnSurface,
            topLeft = Offset(4f * s, 5f * s),
            size = Size(4f * s, 15f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
        drawRoundRect(
            color = ClinicalPrimaryContainer,
            topLeft = Offset(10f * s, 10f * s),
            size = Size(4f * s, 10f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
        drawRoundRect(
            color = ClinicalSecondary,
            topLeft = Offset(16f * s, 14f * s),
            size = Size(4f * s, 6f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
    }
}

@Composable
fun EcgPulseIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        val path = Path().apply {
            moveTo(2f * s, 12f * s)
            lineTo(7f * s, 12f * s)
            lineTo(9.5f * s, 5f * s)
            lineTo(13.5f * s, 19f * s)
            lineTo(16.5f * s, 10f * s)
            lineTo(18.5f * s, 12f * s)
            lineTo(22f * s, 12f * s)
        }
        drawPath(
            path = path,
            color = ClinicalPrimaryContainer,
            style = Stroke(width = 2.2f * s, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Custom Consultation Script / Dossier Icon matching the Nothing X / Clinical Transparency
 * monoline + dot-matrix + Signal Red accent aesthetic.
 */
@Composable
fun ConsultationDossierIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val s = size.width / 24f
        // Structured consultation bubble / dossier frame
        val bubblePath = Path().apply {
            moveTo(6f * s, 4f * s)
            lineTo(18f * s, 4f * s)
            quadraticTo(21f * s, 4f * s, 21f * s, 7f * s)
            lineTo(21f * s, 15f * s)
            quadraticTo(21f * s, 18f * s, 18f * s, 18f * s)
            lineTo(10f * s, 18f * s)
            lineTo(5.5f * s, 21f * s)
            lineTo(5.5f * s, 18f * s)
            quadraticTo(3f * s, 18f * s, 3f * s, 15f * s)
            lineTo(3f * s, 7f * s)
            quadraticTo(3f * s, 4f * s, 6f * s, 4f * s)
            close()
        }
        drawPath(
            path = bubblePath,
            color = ClinicalOnSurface,
            style = Stroke(width = 2f * s, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Signal Red diagnostic dot-matrix pip on first SBAR line
        drawCircle(
            color = ClinicalPrimaryContainer,
            radius = 1.7f * s,
            center = Offset(8f * s, 9f * s)
        )
        // First structured script line
        drawLine(
            color = ClinicalOnSurface,
            start = Offset(12f * s, 9f * s),
            end = Offset(16.8f * s, 9f * s),
            strokeWidth = 2f * s,
            cap = StrokeCap.Round
        )
        // Second structured script line + dot
        drawCircle(
            color = ClinicalOnSurface,
            radius = 1.3f * s,
            center = Offset(8f * s, 13.5f * s)
        )
        drawLine(
            color = ClinicalOnSurface,
            start = Offset(12f * s, 13.5f * s),
            end = Offset(15.2f * s, 13.5f * s),
            strokeWidth = 2f * s,
            cap = StrokeCap.Round
        )
    }
}

