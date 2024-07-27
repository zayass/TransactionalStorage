package org.zayass.assessment.storage.feature.formBased.ui.forms.count

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.zayass.assessment.storage.core.StringSuspendStorage
import javax.inject.Inject

@HiltViewModel
internal class CountValuesViewModel @Inject constructor(
    private val storage: StringSuspendStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
        get() = _uiState.asStateFlow()

    fun dispatchAction(action: UiAction) {
        when (action) {
            is UiAction.ValueChanged -> handleValueChanged(action.value)
            UiAction.Execute -> handleExecute()
        }
    }

    private fun handleValueChanged(value: String) {
        _uiState.update {
            UiState(
                value = value,
                isExecuteEnabled = value.isNotEmpty()
            )
        }
    }

    private fun handleExecute() = viewModelScope.launch {
        val value = _uiState.value.value
        val count = storage.count(value)
        _uiState.update {
            it.copy(
                result = count
            )
        }
    }
}