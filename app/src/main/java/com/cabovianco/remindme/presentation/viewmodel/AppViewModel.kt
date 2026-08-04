package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.domain.usecase.DismissWelcomeScreenUseCase
import com.cabovianco.remindme.domain.usecase.GetInitialDestinationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getInitialDestinationUseCase: GetInitialDestinationUseCase,
    private val dismissWelcomeScreenUseCase: DismissWelcomeScreenUseCase
) : ViewModel() {
    val initialDestination = getInitialDestinationUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun dismissWelcomeScreen() {
        viewModelScope.launch {
            dismissWelcomeScreenUseCase()
        }
    }
}
