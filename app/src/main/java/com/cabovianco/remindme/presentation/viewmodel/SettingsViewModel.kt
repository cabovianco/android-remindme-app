package com.cabovianco.remindme.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.domain.usecase.ExportBackupUseCase
import com.cabovianco.remindme.domain.usecase.GetHistoryPreferencesUseCase
import com.cabovianco.remindme.domain.usecase.ImportBackupUseCase
import com.cabovianco.remindme.domain.usecase.SetHistoryEnabledUseCase
import com.cabovianco.remindme.domain.usecase.SetHistoryRetentionDaysUseCase
import com.cabovianco.remindme.presentation.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase,
    private val getHistoryPreferencesUseCase: GetHistoryPreferencesUseCase,
    private val setHistoryEnabledUseCase: SetHistoryEnabledUseCase,
    private val setHistoryRetentionDaysUseCase: SetHistoryRetentionDaysUseCase
) : ViewModel() {
    private val _isExporting = MutableStateFlow(false)
    private val _isImporting = MutableStateFlow(false)

    val uiState = combine(
        getHistoryPreferencesUseCase(),
        _isExporting,
        _isImporting
    ) { prefs, isExporting, isImporting ->
        SettingsUiState(
            isExporting = isExporting,
            isImporting = isImporting,
            isHistoryEnabled = prefs.isHistoryEnabled,
            historyRetentionDays = prefs.retentionDays
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsUiState()
    )

    fun setHistoryEnabled(enabled: Boolean) {
        viewModelScope.launch {
            setHistoryEnabledUseCase(enabled)
        }
    }

    fun setRetentionDays(days: Int) {
        viewModelScope.launch {
            setHistoryRetentionDaysUseCase(days)
        }
    }

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _isExporting.value = true

            try {
                exportBackupUseCase(uri)

            } finally {
                _isExporting.value = false
            }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            _isImporting.value = true

            try {
                importBackupUseCase(uri)

            } finally {
                _isImporting.value = false
            }
        }
    }
}
