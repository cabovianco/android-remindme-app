package com.cabovianco.remindme.presentation.ui.screen

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cabovianco.remindme.R
import com.cabovianco.remindme.presentation.ui.screen.shared.BottomActionButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@Composable
fun PermissionScreen(
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var currentStep by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !hasNotificationPermission(context)
            ) PermissionStepType.Notifications else PermissionStepType.ExactAlarm
        )
    }

    when (currentStep) {
        PermissionStepType.Notifications -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationStep(
                    modifier = modifier,
                    onNext = {
                        if (!checkAlarmPermission(context)) {
                            currentStep = PermissionStepType.ExactAlarm
                        } else {
                            onAccept()
                        }
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !checkAlarmPermission(
                            context
                        )
                    ) {
                        currentStep = PermissionStepType.ExactAlarm
                    } else {
                        onAccept()
                    }
                }
            }
        }

        PermissionStepType.ExactAlarm -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmStep(
                    modifier = modifier,
                    onNext = { onAccept() }
                )
            } else {
                LaunchedEffect(Unit) { onAccept() }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationStep(
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    var notificationRequested by rememberSaveable { mutableStateOf(false) }

    if (permissionState.status.isGranted) {
        LaunchedEffect(Unit) { onNext() }
    }

    PermissionStepLayout(
        type = PermissionStepType.Notifications,
        modifier = modifier,
        onButtonClick = {
            val status = permissionState.status
            if (!status.isGranted && !status.shouldShowRationale && notificationRequested) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }

                context.startActivity(intent)

            } else {
                permissionState.launchPermissionRequest()
                notificationRequested = true
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun AlarmStep(
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(checkAlarmPermission(context)) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        hasPermission = checkAlarmPermission(context)
    }

    if (hasPermission) {
        LaunchedEffect(Unit) { onNext() }
    }

    PermissionStepLayout(
        type = PermissionStepType.ExactAlarm,
        modifier = modifier,
        onButtonClick = {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }

            context.startActivity(intent)
        }
    )
}

@Composable
private fun PermissionStepLayout(
    type: PermissionStepType,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val step = getPermissionStep(type)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomActionButton(
                text = stringResource(R.string.permissions_btn_continue),
                onClick = onButtonClick,
                paddingValues = PaddingValues(24.dp)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = step.icon,
                contentDescription = null,
                modifier = Modifier.size(128.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = step.title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun getPermissionStep(type: PermissionStepType): PermissionStep {
    return when (type) {
        PermissionStepType.Notifications -> PermissionStep(
            title = stringResource(R.string.permissions_notif_title),
            description = stringResource(R.string.permissions_notif_desc),
            icon = painterResource(R.drawable.notification_permission)
        )

        PermissionStepType.ExactAlarm -> PermissionStep(
            title = stringResource(R.string.permissions_alarm_title),
            description = stringResource(R.string.permissions_alarm_desc),
            icon = painterResource(R.drawable.exact_alarm_permission)
        )
    }
}

private enum class PermissionStepType {
    Notifications,
    ExactAlarm
}

private data class PermissionStep(
    val title: String,
    val description: String,
    val icon: Painter
)

private fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

private fun checkAlarmPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}
