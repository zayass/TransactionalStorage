package org.zayass.assessment.storage.feature.formBased.ui.forms.delete

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
internal class DeleteKeyViewModel @Inject constructor(
    private val storage: StringSuspendStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
        get() = _uiState.asStateFlow()

    fun dispatchAction(action: UiAction) {
        when (action) {
            is UiAction.KeyChanged -> handleKeyChanged(action.key)
            UiAction.Execute -> handleExecute()
            UiAction.Confirm -> handleConfirmation()
            UiAction.DismissConfirmation -> handleDismissConfirmation()
        }
    }

    private fun handleKeyChanged(key: String) {
        _uiState.update {
            UiState(
                key = key,
                isExecuteEnabled = key.isNotEmpty(),
            )
        }
    }

    private fun handleExecute() {
        _uiState.update {
            it.copy(
                isConfirmationVisible = true,
            )
        }
    }

    private fun handleDismissConfirmation() {
        _uiState.update {
            it.copy(
                isConfirmationVisible = false,
            )
        }
    }

    private fun handleConfirmation() = viewModelScope.launch {
        val key = _uiState.value.key
        storage.delete(key)

        _uiState.update {
            UiState(result = true)
        }
    }
}
