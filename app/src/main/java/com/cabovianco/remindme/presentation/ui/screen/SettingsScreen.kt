package com.cabovianco.remindme.presentation.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cabovianco.remindme.R
import com.cabovianco.remindme.presentation.ui.screen.shared.DashedDivider
import com.cabovianco.remindme.presentation.ui.screen.shared.NavigationTopBar
import com.cabovianco.remindme.presentation.viewmodel.SettingsViewModel
import java.time.LocalDate

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName ?: "1.3.0"

        } catch (_: Exception) {
            "1.3.0"
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportBackup(it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.importBackup(it) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            NavigationTopBar(
                title = stringResource(R.string.settings_title),
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        SettingsContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            isHistoryEnabled = uiState.isHistoryEnabled,
            onHistoryEnabledChange = { viewModel.setHistoryEnabled(it) },
            historyRetentionDays = uiState.historyRetentionDays,
            onRetentionDaysChange = { viewModel.setRetentionDays(it) },
            isExporting = uiState.isExporting,
            onExportClick = {
                val fileName = "remindme_${LocalDate.now()}.json"
                exportLauncher.launch(fileName)
            },
            isImporting = uiState.isImporting,
            onImportClick = {
                importLauncher.launch("application/json")
            },
            onPrivacyPolicyClick = {
                uriHandler.openUri("https://github.com/cabovianco/android-remindme-app/blob/main/POLICY.md")
            },
            onGitHubClick = {
                uriHandler.openUri("https://github.com/cabovianco/android-remindme-app")
            },
            onKoFiClick = {
                uriHandler.openUri("https://ko-fi.com/cabovianco")
            },
            versionName = versionName
        )
    }
}

@Composable
private fun SettingsContent(
    isHistoryEnabled: Boolean?,
    onHistoryEnabledChange: (Boolean) -> Unit,
    historyRetentionDays: Int,
    onRetentionDaysChange: (Int) -> Unit,
    isExporting: Boolean,
    onExportClick: () -> Unit,
    isImporting: Boolean,
    onImportClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onGitHubClick: () -> Unit,
    onKoFiClick: () -> Unit,
    versionName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            HistorySettingsGroup(
                isHistoryEnabled = isHistoryEnabled ?: false,
                onHistoryEnabledChange = onHistoryEnabledChange,
                historyRetentionDays = historyRetentionDays,
                onRetentionDaysChange = onRetentionDaysChange,
                isLoading = isHistoryEnabled == null
            )

            DataAndExportSettingsGroup(
                isExporting = isExporting,
                onExportClick = onExportClick,
                isImporting = isImporting,
                onImportClick = onImportClick
            )

            AboutSettingsGroup(
                onPrivacyPolicyClick = onPrivacyPolicyClick,
                onGitHubClick = onGitHubClick,
                onKoFiClick = onKoFiClick
            )
        }

        SettingsFooter(versionName = versionName)
    }
}

@Composable
private fun HistorySettingsGroup(
    isHistoryEnabled: Boolean,
    onHistoryEnabledChange: (Boolean) -> Unit,
    historyRetentionDays: Int,
    onRetentionDaysChange: (Int) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    SettingsGroup(
        modifier = modifier,
        title = stringResource(R.string.settings_section_history)
    ) {
        SettingsSwitchItem(
            title = stringResource(R.string.settings_history_title),
            description = stringResource(R.string.settings_history_description),
            checked = isHistoryEnabled,
            onCheckedChange = onHistoryEnabledChange,
            isLoading = isLoading
        )

        DashedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        SettingsMenuItem(
            title = stringResource(R.string.settings_history_retention_title),
            options = listOf(3, 7, 30),
            selected = historyRetentionDays,
            onSelected = onRetentionDaysChange,
            enabled = isHistoryEnabled,
            labelMapper = { days ->
                when (days) {
                    3 -> stringResource(R.string.settings_history_retention_3_days)
                    7 -> stringResource(R.string.settings_history_retention_7_days)
                    else -> stringResource(R.string.settings_history_retention_30_days)
                }
            }
        )
    }
}

@Composable
private fun DataAndExportSettingsGroup(
    isExporting: Boolean,
    onExportClick: () -> Unit,
    isImporting: Boolean,
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsGroup(
        modifier = modifier,
        title = stringResource(R.string.settings_section_data_backup)
    ) {
        SettingsItem(
            title = stringResource(R.string.settings_export_title),
            description = stringResource(R.string.settings_export_description),
            onClick = onExportClick,
            leadingContent = {
                SettingsLeadingIcon(
                    painter = painterResource(R.drawable.ic_export),
                    isLoading = isExporting
                )
            }
        )

        DashedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        SettingsItem(
            title = stringResource(R.string.settings_import_title),
            description = stringResource(R.string.settings_import_description),
            onClick = onImportClick,
            leadingContent = {
                SettingsLeadingIcon(
                    painter = painterResource(R.drawable.ic_import),
                    isLoading = isImporting
                )
            }
        )
    }
}

@Composable
private fun AboutSettingsGroup(
    onPrivacyPolicyClick: () -> Unit,
    onGitHubClick: () -> Unit,
    onKoFiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsGroup(
        modifier = modifier,
        title = stringResource(R.string.settings_section_about)
    ) {
        SettingsItem(
            title = stringResource(R.string.settings_privacy_policy_title),
            onClick = onPrivacyPolicyClick,
            trailingContent = { SettingsForwardIcon() }
        )

        DashedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        SettingsItem(
            title = stringResource(R.string.settings_github_title),
            onClick = onGitHubClick,
            trailingContent = { SettingsForwardIcon() }
        )

        DashedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        SettingsItem(
            title = stringResource(R.string.settings_kofi_title),
            onClick = onKoFiClick,
            trailingContent = { SettingsForwardIcon() }
        )
    }
}

@Composable
private fun SettingsGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        title?.let {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = it,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null && enabled) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent?.invoke()

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                color = if (enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                style = MaterialTheme.typography.titleMedium
            )

            description?.let {
                Text(
                    text = it,
                    color = if (enabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        trailingContent?.invoke()
    }
}


@Composable
private fun SettingsSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true
) {
    SettingsItem(
        title = title,
        modifier = modifier,
        description = description,
        enabled = enabled,
        onClick = if (!isLoading && enabled) {
            { onCheckedChange(!checked) }
        } else null,
        trailingContent = {
            if (isLoading) {
                Box(
                    modifier = Modifier.size(width = 52.dp, height = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Switch(
                    checked = checked,
                    onCheckedChange = null,
                    enabled = enabled
                )
            }
        }
    )
}

@Composable
private fun <T> SettingsMenuItem(
    title: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    labelMapper: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    SettingsItem(
        title = title,
        modifier = modifier,
        description = description,
        enabled = enabled,
        onClick = if (enabled) {
            { expanded = true }
        } else null,
        trailingContent = {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = labelMapper(selected),
                    color = if (enabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.bodyMedium
                )

                DropdownMenu(
                    expanded = expanded && enabled,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = labelMapper(option),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                onSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun SettingsLeadingIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    if (isLoading) {
        Box(
            modifier = modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Icon(
            modifier = modifier.size(24.dp),
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SettingsForwardIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        modifier = modifier.size(24.dp),
        painter = painterResource(R.drawable.ic_arrow_forward),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
}

@Composable
private fun SettingsFooter(
    versionName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.settings_footer_made_by),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "v$versionName",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
