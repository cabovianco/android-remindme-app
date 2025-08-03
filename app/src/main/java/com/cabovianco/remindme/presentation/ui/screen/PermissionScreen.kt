package com.cabovianco.remindme.presentation.ui.screen

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cabovianco.remindme.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PermissionScreen(
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isNotificationsPermissionGranted by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.75f)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            PostNotificationsPermission(
                isGranted = isNotificationsPermissionGranted,
                onPermissionResult = { isNotificationsPermissionGranted = it }
            )
        }

        AcceptButton(
            onClick = onAccept,
            enabled = isNotificationsPermissionGranted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PostNotificationsPermission(
    isGranted: Boolean,
    onPermissionResult: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val postNotificationPermission = rememberPermissionState(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        onPermissionResult = { onPermissionResult(it) }
    )

    val unEnablePermissionToast = Toast.makeText(
        LocalContext.current,
        R.string.post_notifications_permission_toast,
        Toast.LENGTH_SHORT
    )

    PermissionCard(
        titleResId = R.string.post_notifications_permission_title,
        descriptionResId = R.string.post_notifications_permission_description,
        isChecked = isGranted,
        onCheckedChange = {
            if (!isGranted) {
                postNotificationPermission.launchPermissionRequest()
            } else {
                unEnablePermissionToast.show()
            }
        },
        modifier = modifier
    )
}

@Composable
private fun PermissionCard(
    titleResId: Int,
    descriptionResId: Int,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PermissionCardInfo(
                titleResId = titleResId,
                descriptionResId = descriptionResId,
                modifier = Modifier.fillMaxWidth(0.80f),
            )

            Switch(
                checked = isChecked,
                onCheckedChange = { onCheckedChange(it) }
            )
        }
    }
}

@Composable
private fun PermissionCardInfo(
    titleResId: Int,
    descriptionResId: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(titleResId),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(descriptionResId),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun AcceptButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        enabled = enabled
    ) {
        Text(text = stringResource(R.string.permissions_screen_accept_button))
    }
}
