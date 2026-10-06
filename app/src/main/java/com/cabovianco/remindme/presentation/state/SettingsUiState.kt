package com.cabovianco.remindme.presentation.state

data class SettingsUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val isHistoryEnabled: Boolean? = null,
    val historyRetentionDays: Int = 3
)
