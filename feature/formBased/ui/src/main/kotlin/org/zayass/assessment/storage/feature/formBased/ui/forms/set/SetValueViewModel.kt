package org.zayass.assessment.storage.feature.formBased.ui.forms.set

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
internal class SetValueViewModel @Inject constructor(
    private val storage: StringSuspendStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
        get() = _uiState.asStateFlow()

    fun dispatchAction(action: UiAction) {
        when (action) {
            is UiAction.KeyChanged -> handleKeyChanged(action.key)
            is UiAction.ValueChanged -> handleValueChanged(action.value)
            UiAction.Execute -> handleExecute()
        }
    }

    private fun handleKeyChanged(key: String) {
        val value = _uiState.value.value
        _uiState.update {
            it.copy(
                key = key,
                result = false,
                isExecuteEnabled = isExecuteEnabled(key, value),
            )
        }
    }

    private fun handleValueChanged(value: String) {
        val key = _uiState.value.key
        _uiState.update {
            it.copy(
                value = value,
                result = false,
                isExecuteEnabled = isExecuteEnabled(key, value),
            )
        }
    }

    private fun isExecuteEnabled(key: String, value: String): Boolean {
        return key.isNotEmpty() && value.isNotEmpty()
    }

    private fun handleExecute() = viewModelScope.launch {
        val uiState = uiState.value
        storage.set(uiState.key, uiState.value)

        _uiState.update {
            it.copy(
                result = true,
            )
        }
    }
}
