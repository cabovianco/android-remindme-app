package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.model.TagColor
import com.cabovianco.remindme.domain.model.TagIcon
import com.cabovianco.remindme.domain.usecase.InsertTagUseCase
import com.cabovianco.remindme.presentation.state.CreateTagUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTagViewModel @Inject constructor(
    private val insertTagUseCase: InsertTagUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<CreateTagUiState> = MutableStateFlow(CreateTagUiState())
    val uiState: StateFlow<CreateTagUiState> get() = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onColorChange(color: TagColor) {
        _uiState.value = _uiState.value.copy(color = color)
    }

    fun onIconChange(icon: TagIcon?) {
        _uiState.value = _uiState.value.copy(icon = icon)
    }

    fun onCreateTag() {
        viewModelScope.launch {
            with (_uiState.value) {
                val tag = Tag(name = name, color = color, icon = icon)
                insertTagUseCase(tag)
            }
        }
    }
}
