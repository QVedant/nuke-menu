package com.nuke.pesumenu.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.nuke.pesumenu.R
import com.nuke.pesumenu.presentation.data.Day
import com.nuke.pesumenu.presentation.data.MealDatabase
import com.nuke.pesumenu.presentation.data.MealEntity
import com.nuke.pesumenu.presentation.data.MealRepository
import com.nuke.pesumenu.presentation.notifications.AlarmScheduler
import com.nuke.pesumenu.presentation.notifications.NotificationHelper
import com.nuke.pesumenu.presentation.settings.AppPreferences
import com.nuke.pesumenu.presentation.settings.WeekSetupScreen
import com.nuke.pesumenu.presentation.theme.PESUMenuTheme
import kotlinx.coroutines.launch
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            requestExactAlarmPermission()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawableResource(
            android.R.color.black
        )

        setContent {

            PESUMenuTheme {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {

                    val preferences = remember {
                        AppPreferences(this@MainActivity)
                    }

                    var setupComplete by remember {
                        mutableStateOf(
                            preferences.isSetupComplete()
                        )
                    }

                    if (!setupComplete) {

                        WeekSetupScreen(
                            onWeekSelected = { week ->

                                preferences.saveWeekSetup(
                                    week = week,
                                    setupDate =
                                        java.time.LocalDate.now()
                                )

                                setupComplete = true

                                NotificationHelper
                                    .createNotificationChannel(
                                        this@MainActivity
                                    )

                                requestNotificationPermission()
                            }
                        )

                    } else {

                        LaunchedEffect(Unit) {

                            NotificationHelper
                                .createNotificationChannel(
                                    this@MainActivity
                                )

                            requestNotificationPermission()
                        }

                        var currentScreen by remember {
                            mutableStateOf("menu")
                        }

                        when (currentScreen) {

                            "menu" -> {

                                MenuScreen(
                                    preferences = preferences,
                                    onSettings = {
                                        currentScreen = "settings"
                                    }
                                )
                            }

                            "settings" -> {

                                SettingsScreen(
                                    onChangeWeek = {
                                        currentScreen = "change_week"
                                    },

                                    onBack = {
                                        currentScreen = "menu"
                                    }
                                )
                            }

                            "change_week" -> {

                                WeekSetupScreen(
                                    onWeekSelected = { week ->

                                        preferences.saveWeekSetup(
                                            week = week,
                                            setupDate =
                                                java.time.LocalDate.now()
                                        )

                                        AlarmScheduler.scheduleAll(
                                            this@MainActivity
                                        )

                                        currentScreen = "menu"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val alarmManager =
            getSystemService(ALARM_SERVICE)
                    as android.app.AlarmManager

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED &&
            alarmManager.canScheduleExactAlarms()
        ) {
            AlarmScheduler.scheduleAll(this)
        }
    }

    private fun requestNotificationPermission() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
            return
        }

        requestExactAlarmPermission()
    }

    private fun requestExactAlarmPermission() {

        val alarmManager =
            getSystemService(ALARM_SERVICE)
                    as android.app.AlarmManager

        if (alarmManager.canScheduleExactAlarms()) {

            AlarmScheduler.scheduleAll(this)

        } else {

            val intent = Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:$packageName")
            )

            startActivity(intent)
        }
    }
}

@Composable
fun SettingsScreen(
    onChangeWeek: () -> Unit,
    onBack: () -> Unit
) {
    val listState =
        rememberScalingLazyListState(
            initialCenterItemIndex = 0
        )

    ScreenScaffold(
        scrollState = listState
    ) {

        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ---------------------------------------------
            // CHANGE WEEK
            // ---------------------------------------------

            item {

                Card(
                    onClick = onChangeWeek,
                    modifier =
                        Modifier
                            .padding(horizontal = 14.dp)
                            .fillMaxWidth(),
                    shape =
                        RoundedCornerShape(24.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFF211D35)
                        )
                ) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 18.dp
                                ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier =
                                Modifier
                                    .size(14.dp)
                                    .background(
                                        color =
                                            Color(0xFFB8B0F2),
                                        shape =
                                            RoundedCornerShape(4.dp)
                                    )
                        )

                        Text(
                            text = "CHANGE WEEK",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            color =
                                Color(0xFFB8B0F2),
                            textAlign =
                                TextAlign.Center,
                            modifier =
                                Modifier.padding(top = 7.dp)
                        )

                        Text(
                            text =
                                "Choose a different\ncanteen week",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                Color(0xFFB8B4C4),
                            textAlign =
                                TextAlign.Center,
                            modifier =
                                Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }

            // ---------------------------------------------
            // BACK
            // ---------------------------------------------

            item {

                Button(
                    onClick = onBack
                ) {
                    Text("BACK")
                }
            }
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun MenuScreen(
    preferences: AppPreferences,
    onSettings: () -> Unit
) {

    val context = LocalContext.current

    val currentWeek =
        preferences.getCurrentWeek()

    val today =
        java.time.LocalDate.now()

    val currentDay =
        when (today.dayOfWeek) {
            java.time.DayOfWeek.SUNDAY ->
                Day.SUNDAY

            java.time.DayOfWeek.MONDAY ->
                Day.MONDAY

            java.time.DayOfWeek.TUESDAY ->
                Day.TUESDAY

            java.time.DayOfWeek.WEDNESDAY ->
                Day.WEDNESDAY

            java.time.DayOfWeek.THURSDAY ->
                Day.THURSDAY

            java.time.DayOfWeek.FRIDAY ->
                Day.FRIDAY

            java.time.DayOfWeek.SATURDAY ->
                Day.SATURDAY
        }

    val dayNumber =
        currentDay.ordinal + 1

    var meals by remember {
        mutableStateOf<List<MealEntity>>(
            emptyList()
        )
    }

    var selectedMeal by remember {
        mutableStateOf<MealEntity?>(null)
    }

    var refreshKey by remember {
        mutableStateOf(0)
    }

    val repository = remember {

        MealRepository(
            MealDatabase
                .getInstance(context)
                .mealDao()
        )
    }

    LaunchedEffect(
        currentWeek,
        dayNumber,
        refreshKey
    ) {

        repository.seedDatabaseIfEmpty()

        meals =
            repository.getMealsForDay(
                week = currentWeek,
                day = dayNumber
            )
    }

    if (selectedMeal != null) {

        EditMealScreen(
            meal = selectedMeal!!,
            repository = repository,

            onBack = {
                selectedMeal = null
            },

            onSaved = {
                selectedMeal = null
                refreshKey++
            }
        )

        return
    }

    /*
     * List positions:
     *
     * 0 = Header
     * 1 = Breakfast
     * 2 = Lunch
     * 3 = Snacks
     * 4 = Dinner
     */

    val listState =
        rememberScalingLazyListState(
            initialCenterItemIndex = 0
        )

    val rotaryScope =
        rememberCoroutineScope()

    val view = LocalView.current

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

    AppScaffold {

        ScreenScaffold(
            scrollState = listState
        ) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                ScalingLazyColumn(
                    state = listState,

                    modifier = Modifier
                        .fillMaxSize()
                        .onPreRotaryScrollEvent { event ->

                            /*
                             * Special case:
                             *
                             * Header → Breakfast
                             *
                             * Because the header occupies the
                             * full screen, explicitly move to
                             * item 1 on the first bezel click.
                             */
                            if (
                                listState.centerItemIndex == 0 &&
                                event.verticalScrollPixels > 0
                            ) {

                                rotaryScope.launch {

                                    listState.animateScrollToItem(
                                        1
                                    )
                                }

                                true

                            } else {

                                false
                            }
                        },

                    /*
                     * Touch scrolling / fling snapping.
                     */
                    flingBehavior =
                        androidx.wear.compose.foundation.lazy
                            .ScalingLazyColumnDefaults
                            .snapFlingBehavior(
                                listState
                            ),

                    /*
                     * Physical bezel / rotary input.
                     */
                    rotaryScrollableBehavior =
                        RotaryScrollableDefaults.snapBehavior(
                            scrollableState = listState,
                            snapSensitivity = 0.6f
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(10.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    /*
                     * HEADER
                     */
                    item {

                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                                verticalArrangement =
                                    Arrangement.Center
                            ) {

                                Text(
                                    text = "PIXEL MENU",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .displaySmall,
                                    textAlign =
                                        TextAlign.Center
                                )

                                Text(
                                    text =
                                        "WEEK $currentWeek • ${currentDay.name}",
                                    style =
                                        MaterialTheme.typography.titleSmall.copy(
                                            fontSize = 12.sp
                                        ),
                                    color =
                                        Color(0xFFB8B0F2),
                                    textAlign =
                                        TextAlign.Center,
                                    modifier =
                                        Modifier.padding(top = 8.dp)
                                )
                            }

                            CurvedRotaryHint(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            )
                        }
                    }


                    /*
                     * BREAKFAST
                     */
                    item {

                        meals.getOrNull(0)?.let { meal ->

                            MealCard(
                                meal = meal,
                                mealName = "BREAKFAST",
                                mealTime = "7:00 AM",
                                isCentered = listState.centerItemIndex == 1,
                                onClick = {
                                    selectedMeal = meal
                                }
                            )
                        }
                    }


                    /*
                     * LUNCH
                     */
                    item {

                        meals.getOrNull(1)?.let { meal ->

                            MealCard(
                                meal = meal,
                                mealName = "LUNCH",
                                mealTime = "1:15 PM",
                                isCentered = listState.centerItemIndex == 2,
                                onClick = {
                                    selectedMeal = meal
                                }
                            )
                        }
                    }


                    /*
                     * SNACKS
                     */
                    item {

                        meals.getOrNull(2)?.let { meal ->

                            MealCard(
                                meal = meal,
                                mealName = "SNACKS",
                                mealTime = "5:00 PM",
                                isCentered = listState.centerItemIndex == 3,
                                onClick = {
                                    selectedMeal = meal
                                }
                            )
                        }
                    }


                    /*
                     * DINNER
                     */
                    item {

                        meals.getOrNull(3)?.let { meal ->

                            MealCard(
                                meal = meal,
                                mealName = "DINNER",
                                mealTime = "8:00 PM",
                                isCentered = listState.centerItemIndex == 4,
                                onClick = {
                                    selectedMeal = meal
                                }
                            )
                        }
                    }

                    item {

                        IconButton(
                            onClick = onSettings,
                            modifier = Modifier
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = settings,
                                contentDescription = "Settings",
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFFB8B0F2)
                            )
                        }
                    }

                    /*
                     * NUKE branding
                     * Kept after Settings so it stays at the end
                     * of the menu without interfering with snapping.
                     */
                    item {

                        Image(
                            painter = painterResource(
                                id = R.drawable.nuke_logo4
                            ),
                            contentDescription = "NUKE",
                            modifier = Modifier
                                .size(90.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MealCard(
    meal: MealEntity,
    mealName: String,
    mealTime: String,
    isCentered: Boolean,
    onClick: () -> Unit
) {
    val displayedMenu =
        meal.customMenu ?: meal.defaultMenu

    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    val pressed by
    interactionSource.collectIsPressedAsState()

    // Small typography pop whenever this meal becomes centered.
    val titleScale =
        remember {
            Animatable(1f)
        }

    LaunchedEffect(isCentered) {
        if (isCentered) {
            titleScale.snapTo(1f)

            titleScale.animateTo(
                targetValue = 1.06f,
                animationSpec = tween(
                    durationMillis = 140
                )
            )

            titleScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 260
                )
            )
        } else {
            titleScale.snapTo(1f)
        }
    }

    val cardScale by
    animateFloatAsState(
        targetValue =
            if (pressed) {
                0.97f
            } else if (isCentered) {
                1f
            } else {
                0.96f
            },
        animationSpec =
            spring(
                dampingRatio =
                    Spring.DampingRatioNoBouncy,
                stiffness =
                    Spring.StiffnessMedium
            ),
        label = "meal_card_scale"
    )

    val cardBackground =
        if (isCentered) {
            Color(0xFF342F52)
        } else {
            Color(0xFF211D35)
        }

    val accent =
        if (isCentered) {
            Color(0xFFB8B0F2)
        } else {
            Color(0xFF8A849F)
        }

    val chipColor =
        if (isCentered) {
            Color(0xFFC9C4F5)
        } else {
            Color(0xFF8D899B)
        }

    val chipTextColor =
        Color(0xFF292443)

    val displayName =
        mealName.lowercase()
            .replaceFirstChar { it.uppercase() }

    androidx.wear.compose.material3.Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            },
        shape = RoundedCornerShape(24.dp),
        colors =
            androidx.wear.compose.material3
                .CardDefaults
                .cardColors(
                    containerColor = cardBackground
                )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // Meal title
            Row(
                modifier =
                    Modifier.graphicsLayer {
                        scaleX = titleScale.value
                        scaleY = titleScale.value
                    },
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(
                            RoundedCornerShape(4.dp)
                        )
                        .background(accent)
                )

                Text(
                    text = displayName,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    color = accent,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier.padding(start = 8.dp)
                )
            }

            // Time chip
            Box(
                modifier = Modifier
                    .padding(top = 7.dp)
                    .background(
                        color = chipColor,
                        shape =
                            RoundedCornerShape(50.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 3.dp
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = mealTime,
                    style =
                        MaterialTheme
                            .typography
                            .labelMedium,
                    color = chipTextColor,
                    textAlign = TextAlign.Center
                )
            }

            // Menu
            Text(
                text = displayedMenu,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color = Color(0xFFF0EDF4),
                textAlign = TextAlign.Center,
                maxLines = 3,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp)
            )
        }
    }
}


@Composable
fun EditMealScreen(
    meal: MealEntity,
    repository: MealRepository,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val keyboardController =
        LocalSoftwareKeyboardController.current

    val focusManager =
        LocalFocusManager.current

    val mealName =
        when (meal.mealType) {
            1 -> "Breakfast"
            2 -> "Lunch"
            3 -> "Snacks"
            4 -> "Dinner"
            else -> "Meal"
        }

    val mealTime =
        when (meal.mealType) {
            1 -> "7:00 AM"
            2 -> "1:15 PM"
            3 -> "5:00 PM"
            4 -> "8:00 PM"
            else -> ""
        }

    var menuText by remember {
        mutableStateOf(
            meal.customMenu ?: meal.defaultMenu
        )
    }

    val listState =
        rememberScalingLazyListState()

    val accent =
        Color(0xFFB8B0F2)

    val editorBackground =
        Color(0xFF211D35)

    AppScaffold {

        ScreenScaffold(
            scrollState = listState
        ) {

            ScalingLazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                /*
                 * HEADER
                 */
                item {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        modifier =
                            Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                    ) {

                        Text(
                            text = "EDIT MEAL",
                            style =
                                MaterialTheme
                                    .typography
                                    .displaySmall,
                            textAlign =
                                TextAlign.Center
                        )

                        Text(
                            text = mealName,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            color = accent,
                            textAlign =
                                TextAlign.Center,
                            modifier =
                                Modifier.padding(
                                    top = 4.dp
                                )
                        )

                        Text(
                            text = mealTime,
                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium,
                            textAlign =
                                TextAlign.Center,
                            modifier =
                                Modifier.padding(
                                    top = 2.dp
                                )
                        )
                    }
                }

                /*
                 * EDITOR CARD
                 */
                item {

                    androidx.wear.compose.material3.Card(
                        onClick = {
                            // The text field handles editing.
                        },
                        modifier =
                            Modifier
                                .padding(
                                    horizontal = 12.dp
                                )
                                .fillMaxWidth(),
                        shape =
                            RoundedCornerShape(24.dp),
                        colors =
                            androidx.wear.compose.material3
                                .CardDefaults
                                .cardColors(
                                    containerColor =
                                        editorBackground
                                )
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 14.dp
                                )
                        ) {

                            Text(
                                text = "MENU",
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelMedium,
                                color = accent,
                                modifier =
                                    Modifier.padding(
                                        bottom = 8.dp
                                    )
                            )

                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color(0xFF121018),
                                            RoundedCornerShape(
                                                16.dp
                                            )
                                        )
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 12.dp
                                        )
                            ) {

                                BasicTextField(
                                    value = menuText,

                                    onValueChange = {
                                        menuText = it
                                    },

                                    modifier =
                                        Modifier.fillMaxWidth(),

                                    textStyle =
                                        TextStyle(
                                            color =
                                                Color(0xFFF0EDF4),
                                            fontSize =
                                                14.sp,
                                            lineHeight =
                                                19.sp,
                                            textAlign =
                                                TextAlign.Start
                                        ),

                                    keyboardOptions =
                                        KeyboardOptions(
                                            imeAction =
                                                ImeAction.Done
                                        ),

                                    keyboardActions =
                                        KeyboardActions(
                                            onDone = {

                                                keyboardController
                                                    ?.hide()

                                                focusManager
                                                    .clearFocus()
                                            }
                                        )
                                )
                            }
                        }
                    }
                }

                /*
                 * SAVE
                 */
                item {

                    Button(
                        onClick = {

                            keyboardController?.hide()
                            focusManager.clearFocus()

                            kotlinx.coroutines
                                .CoroutineScope(
                                    kotlinx.coroutines
                                        .Dispatchers.IO
                                )
                                .launch {

                                    repository
                                        .updateCustomMenu(
                                            week = meal.week,
                                            day = meal.day,
                                            mealType =
                                                meal.mealType,
                                            customMenu =
                                                menuText
                                        )

                                    kotlinx.coroutines
                                        .withContext(
                                            kotlinx.coroutines
                                                .Dispatchers.Main
                                        ) {
                                            onSaved()
                                        }
                                }
                        }
                    ) {
                        Text("SAVE")
                    }
                }

                /*
                 * RESET
                 */
                item {

                    Button(
                        onClick = {

                            keyboardController?.hide()
                            focusManager.clearFocus()

                            kotlinx.coroutines
                                .CoroutineScope(
                                    kotlinx.coroutines
                                        .Dispatchers.IO
                                )
                                .launch {

                                    repository
                                        .resetToDefault(
                                            week = meal.week,
                                            day = meal.day,
                                            mealType =
                                                meal.mealType
                                        )

                                    kotlinx.coroutines
                                        .withContext(
                                            kotlinx.coroutines
                                                .Dispatchers.Main
                                        ) {
                                            onSaved()
                                        }
                                }
                        }
                    ) {
                        Text("RESET TO DEFAULT")
                    }
                }

                /*
                 * BACK
                 */
                item {

                    Button(
                        onClick = {

                            keyboardController?.hide()
                            focusManager.clearFocus()

                            onBack()
                        }
                    ) {
                        Text("BACK")
                    }
                }
            }
        }
    }
}

