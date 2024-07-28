package org.zayass.assessment.storage.feature.formBased.ui.forms.get

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
internal class GetValueViewModel @Inject constructor(
    private val storage: StringSuspendStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
        get() = _uiState.asStateFlow()

    fun dispatchAction(action: UiAction) {
        when (action) {
            is UiAction.KeyChanged -> handleKeyChanged(action.key)
            UiAction.Execute -> handleExecute()
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

    private fun handleExecute() = viewModelScope.launch {
        val key = _uiState.value.key
        val value = storage.get(key)

        _uiState.update {
            it.copy(
                isResultVisible = true,
                result = value,
            )
        }
    }
}
