package com.nuke.pesumenu.presentation.settings

import android.os.SystemClock
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimePicker
import androidx.wear.compose.material3.TimePickerType
import com.nuke.pesumenu.presentation.notifications.AlarmScheduler
import com.nuke.pesumenu.presentation.notifications.ReminderConfig
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun ReminderSetupScreen(
    preferences: AppPreferences,
    isInitialSetup: Boolean = false,
    onComplete: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current
    val rotaryScope = rememberCoroutineScope()

    var editingConfig by remember {
        mutableStateOf<ReminderConfig?>(null)
    }

    var refreshTrigger by remember {
        mutableIntStateOf(0)
    }

    // Default Wear OS UI Time Selector
    if (editingConfig != null) {
        val config = editingConfig!!
        val (currentHour, currentMin) = preferences.getReminderTime(config)

        BackHandler {
            editingConfig = null
        }

        TimePicker(
            initialTime = LocalTime.of(
                currentHour.coerceIn(0, 23),
                currentMin.coerceIn(0, 59)
            ),
            timePickerType = TimePickerType.HoursMinutes24H,
            onTimePicked = { pickedTime ->
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                preferences.saveReminderTime(config, pickedTime.hour, pickedTime.minute)
                if (preferences.isSetupComplete()) {
                    AlarmScheduler.scheduleAll(context)
                }
                editingConfig = null
                refreshTrigger++
            }
        )
        return
    }

    val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)
    var lastRotaryTime by remember { mutableLongStateOf(0L) }
    val rotaryCooldown = 250L

    // Index 0: Header, 1..5: Reminder Cards, 6: Combined Actions Card
    val maxIndex = 6

    var lastHapticPosition by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState.centerItemIndex) {
        val currentPosition = listState.centerItemIndex.coerceIn(0, maxIndex)
        if (currentPosition != lastHapticPosition) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            lastHapticPosition = currentPosition
        }
    }

    AppScaffold {
        ScreenScaffold(scrollState = listState) {
            Box(modifier = Modifier.fillMaxSize()) {
                ScalingLazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .onPreRotaryScrollEvent { event ->
                            val now = SystemClock.elapsedRealtime()
                            if (now - lastRotaryTime < rotaryCooldown) {
                                return@onPreRotaryScrollEvent true
                            }

                            val direction = when {
                                event.verticalScrollPixels > 0 -> 1
                                event.verticalScrollPixels < 0 -> -1
                                else -> 0
                            }

                            if (direction != 0) {
                                val current = listState.centerItemIndex
                                val target = (current + direction).coerceIn(0, maxIndex)

                                if (target != current) {
                                    lastRotaryTime = now
                                    rotaryScope.launch {
                                        listState.animateScrollToItem(target)
                                    }
                                }
                            }
                            true
                        },
                    flingBehavior = ScalingLazyColumnDefaults.snapFlingBehavior(listState),
                    rotaryScrollableBehavior = RotaryScrollableDefaults.snapBehavior(
                        scrollableState = listState,
                        snapSensitivity = 0.6f
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ---------------- 0: FULL-SCREEN HEADER ----------------
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "REMINDERS",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color(0xFFF0EDF4),
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Tap any card to edit time",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFB8B4C4),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                Text(
                                    text = "↻ Rotate bezel",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF777286),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                        }
                    }

                    // ---------------- 1..5: REMINDER CARDS (24H FORMAT) ----------------
                    items(ReminderConfig.ALL.size) { index ->
                        val config = ReminderConfig.ALL[index]
                        val (hour, minute) = remember(config, refreshTrigger) {
                            preferences.getReminderTime(config)
                        }

                        val isCentered = listState.centerItemIndex == index + 1

                        val cardScale by animateFloatAsState(
                            targetValue = if (isCentered) 1f else 0.94f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "reminder_card_scale"
                        )

                        val cardBackground = if (isCentered) {
                            Color(0xFF342F52)
                        } else {
                            Color(0xFF211D35)
                        }

                        val accentColor = if (isCentered) {
                            Color(0xFFB8B0F2)
                        } else {
                            Color(0xFF8A849F)
                        }

                        val chipColor = if (isCentered) {
                            Color(0xFFC9C4F5)
                        } else {
                            Color(0xFF8D899B)
                        }

                        val chipTextColor = Color(0xFF292443)

                        Card(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                editingConfig = config
                            },
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = cardScale
                                    scaleY = cardScale
                                },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardBackground
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Title with accent indicator
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(accentColor)
                                    )
                                    Text(
                                        text = config.title.uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = accentColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(start = 7.dp)
                                    )
                                }

                                // 24-Hour Time Pill
                                Box(
                                    modifier = Modifier
                                        .padding(top = 7.dp)
                                        .background(
                                            color = chipColor,
                                            shape = RoundedCornerShape(50.dp)
                                        )
                                        .padding(horizontal = 18.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ReminderConfig.formatTime24Hour(hour, minute),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = chipTextColor,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Touch to edit hint
                                Text(
                                    text = "Touch to edit",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp
                                    ),
                                    color = if (isCentered) Color(0xFFC9C4F5) else Color(0xFF777286),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 5.dp)
                                )
                            }
                        }
                    }

                    // ---------------- 6: COMBINED ACTIONS (FLOATING PILL BUTTONS) ----------------
                    item {
                        val isCentered = listState.centerItemIndex == 6

                        val cardScale by animateFloatAsState(
                            targetValue = if (isCentered) 1f else 0.94f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "actions_card_scale"
                        )

                        Card(
                            onClick = { /* pills handle their own clicks */ },
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = cardScale
                                    scaleY = cardScale
                                },
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 1. CONTINUE Pill
                                Button(
                                    onClick = {
                                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                        onComplete()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFB8B0F2)
                                    ),
                                    shape = RoundedCornerShape(50.dp)
                                ) {
                                    Text(
                                        text = "CONTINUE",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFF211D35)
                                    )
                                }

                                // 2. RESET TO DEFAULTS Pill
                                Button(
                                    onClick = {
                                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                        preferences.resetAllReminderTimes()
                                        if (preferences.isSetupComplete()) {
                                            AlarmScheduler.scheduleAll(context)
                                        }
                                        refreshTrigger++
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF211D35)
                                    ),
                                    shape = RoundedCornerShape(50.dp)
                                ) {
                                    Text(
                                        text = "RESET TO DEFAULTS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = Color(0xFFB8B4C4)
                                    )
                                }

                                // 3. BACK Pill (if onBack provided)
                                if (onBack != null) {
                                    Button(
                                        onClick = {
                                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                            onBack()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF1B182B)
                                        ),
                                        shape = RoundedCornerShape(50.dp)
                                    ) {
                                        Text(
                                            text = "BACK",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp
                                            ),
                                            color = Color(0xFF8A849F)
                                        )
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
