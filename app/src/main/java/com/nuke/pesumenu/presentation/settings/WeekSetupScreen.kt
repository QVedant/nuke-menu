package com.nuke.pesumenu.presentation.settings

import android.os.SystemClock
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import kotlinx.coroutines.launch

@Composable
fun WeekSetupScreen(
    onWeekSelected: (Int) -> Unit
) {
    val listState = rememberScalingLazyListState(
        initialCenterItemIndex = 0
    )
    val rotaryScope = rememberCoroutineScope()
    val view = LocalView.current

    var lastRotaryTime by remember {
        mutableStateOf(0L)
    }

    val rotaryCooldown = 250L

    var lastHapticPosition by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(listState.centerItemIndex) {

        val currentPosition =
            listState.centerItemIndex.coerceIn(0, 4)

        if (currentPosition != lastHapticPosition) {

            view.performHapticFeedback(
                HapticFeedbackConstants.CLOCK_TICK
            )

            lastHapticPosition = currentPosition
        }
    }

    ScreenScaffold(
        scrollState = listState
    ) {

        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .onPreRotaryScrollEvent { event ->

                    val now =
                        SystemClock.elapsedRealtime()

                    if (
                        now - lastRotaryTime <
                        rotaryCooldown
                    ) {
                        return@onPreRotaryScrollEvent true
                    }

                    val direction =
                        when {
                            event.verticalScrollPixels > 0 -> 1
                            event.verticalScrollPixels < 0 -> -1
                            else -> 0
                        }

                    if (direction != 0) {

                        val current =
                            listState.centerItemIndex

                        val target =
                            (current + direction)
                                .coerceIn(0, 4)

                        if (target != current) {

                            lastRotaryTime = now

                            rotaryScope.launch {
                                listState.animateScrollToItem(
                                    target
                                )
                            }
                        }
                    }

                    true
                },
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(10.dp),
            flingBehavior =
                ScalingLazyColumnDefaults
                    .snapFlingBehavior(listState)
        ) {

            // --------------------------------------------------
            // 0 — HEADER
            // --------------------------------------------------

            item {

                Box(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "SETUP",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelLarge,
                            textAlign = TextAlign.Center,
                            color = Color(0xFFF0EDF4),
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Text(
                            text = "Which week is\ncurrently running?",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            textAlign = TextAlign.Center,
                            color = Color(0xFFB8B4C4),
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        Text(
                            text = "↻ Rotate bezel",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF777286),
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            // --------------------------------------------------
            // 1–4 — WEEK CARDS
            // --------------------------------------------------

            items(4) { index ->

                val week = index + 1

                val isCentered =
                    listState.centerItemIndex == week

                val cardScale by
                animateFloatAsState(
                    targetValue =
                        if (isCentered) 1f else 0.94f,
                    animationSpec =
                        spring(
                            dampingRatio =
                                Spring.DampingRatioNoBouncy,
                            stiffness =
                                Spring.StiffnessMedium
                        ),
                    label = "week_card_scale"
                )

                val cardBackground by
                androidx.compose.animation.animateColorAsState(
                    targetValue =
                        if (isCentered) {
                            Color(0xFF342F52)
                        } else {
                            Color(0xFF211D35)
                        },
                    animationSpec =
                        androidx.compose.animation.core.tween(
                            durationMillis = 180
                        ),
                    label = "week_card_background"
                )

                val accentColor by
                androidx.compose.animation.animateColorAsState(
                    targetValue =
                        if (isCentered) {
                            Color(0xFFB8B0F2)
                        } else {
                            Color(0xFF625D78)
                        },
                    animationSpec =
                        androidx.compose.animation.core.tween(
                            durationMillis = 180
                        ),
                    label = "week_card_accent"
                )

                val textColor by
                androidx.compose.animation.animateColorAsState(
                    targetValue =
                        if (isCentered) {
                            Color(0xFFB8B0F2)
                        } else {
                            Color(0xFFF0EDF4)
                        },
                    animationSpec =
                        androidx.compose.animation.core.tween(
                            durationMillis = 180
                        ),
                    label = "week_card_text"
                )

                Card(
                    onClick = {

                        view.performHapticFeedback(
                            HapticFeedbackConstants.CONFIRM
                        )

                        onWeekSelected(week)
                    },
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = cardScale
                            scaleY = cardScale
                        },
                    shape =
                        RoundedCornerShape(24.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = cardBackground
                        )
                ) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 14.dp
                                ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    color = accentColor,
                                    shape =
                                        RoundedCornerShape(4.dp)
                                )
                        )

                        Text(
                            text = "WEEK $week",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            color = textColor,
                            modifier =
                                Modifier.padding(top = 6.dp)
                        )

                        if (isCentered) {

                            Text(
                                text = "TAP TO SELECT",
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelSmall,
                                color =
                                    Color(0xFFC9C4F5),
                                modifier =
                                    Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
