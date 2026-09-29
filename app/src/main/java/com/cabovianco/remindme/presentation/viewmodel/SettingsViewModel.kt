package com.cabovianco.remindme.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.domain.usecase.ExportBackupUseCase
import com.cabovianco.remindme.domain.usecase.ImportBackupUseCase
import com.cabovianco.remindme.presentation.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val uiState get() = _uiState.asStateFlow()

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }

            try {
                exportBackupUseCase(uri)
            } finally {
                _uiState.update { it.copy(isExporting = false) }
            }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true) }

            try {
                importBackupUseCase(uri)

            } finally {
                _uiState.update { it.copy(isImporting = false) }
            }
        }
    }
}
