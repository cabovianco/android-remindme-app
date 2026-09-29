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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cabovianco.remindme.R
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
            isExporting = uiState.isExporting,
            isImporting = uiState.isImporting,
            onExportClick = {
                val fileName = "remindme_${LocalDate.now()}.json"
                exportLauncher.launch(fileName)
            },
            onImportClick = {
                importLauncher.launch("application/json")
            }
        )
    }
}

@Composable
private fun SettingsContent(
    isExporting: Boolean,
    isImporting: Boolean,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsGroup(
            title = stringResource(R.string.settings_section_data_backup)
        ) {
            SettingsItem(
                title = stringResource(R.string.settings_export_title),
                description = stringResource(R.string.settings_export_description),
                icon = painterResource(R.drawable.ic_export),
                isLoading = isExporting,
                onClick = onExportClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            SettingsItem(
                title = stringResource(R.string.settings_import_title),
                description = stringResource(R.string.settings_import_description),
                icon = painterResource(R.drawable.ic_import),
                isLoading = isImporting,
                onClick = onImportClick
            )
        }
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
                color = MaterialTheme.colorScheme.primary,
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
    icon: Painter? = null,
    isLoading: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null && !isLoading) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            icon?.let {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )

            description?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
