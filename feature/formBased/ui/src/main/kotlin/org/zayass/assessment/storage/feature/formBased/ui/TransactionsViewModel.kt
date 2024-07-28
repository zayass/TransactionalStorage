package org.zayass.assessment.storage.feature.formBased.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.zayass.assessment.storage.core.StringSuspendStorage
import org.zayass.assessment.storage.core.TransactionResult
import org.zayass.assessment.storage.feature.formBased.R
import javax.inject.Inject

private enum class ConfirmationStatus {
    NONE,
    COMMIT_REQUESTED,
    ROLLBACK_REQUESTED
}

data class UiState(
    val isConfirmationVisible: Boolean = false,
    @StringRes
    val message: Int? = null
)

@HiltViewModel
class TransactionsViewModel @Inject internal constructor(
    private val storage: StringSuspendStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
        get() = _uiState.asStateFlow()

    private var confirmationStatus = ConfirmationStatus.NONE

    internal fun dispatchAction(action: UiAction) {
        when (action) {
            UiAction.Begin -> handleBegin()
            UiAction.Commit -> handleCommit()
            UiAction.Rollback -> handleRollback()
            UiAction.DismissConfirmation -> handleDismissConfirmation()
            UiAction.Confirm -> handleConfirm()
            UiAction.DismissMessage -> handleDismissMessage()
        }
    }

    private fun handleBegin() = viewModelScope.launch {
        storage.begin()
    }

    private fun handleCommit() {
        confirmationStatus = ConfirmationStatus.COMMIT_REQUESTED
        _uiState.update {
            it.copy(isConfirmationVisible = true)
        }
    }

    private fun handleRollback() {
        confirmationStatus = ConfirmationStatus.ROLLBACK_REQUESTED
        _uiState.update {
            it.copy(isConfirmationVisible = true)
        }
    }

    private fun handleDismissConfirmation() {
        confirmationStatus = ConfirmationStatus.NONE
        _uiState.update {
            it.copy(isConfirmationVisible = false)
        }
    }

    private fun handleConfirm() {
        when (confirmationStatus) {
            ConfirmationStatus.COMMIT_REQUESTED -> commit()
            ConfirmationStatus.ROLLBACK_REQUESTED -> rollback()
            ConfirmationStatus.NONE -> { }
        }
    }

    private fun commit() = viewModelScope.launch {
        val result = storage.commit()
        val message = when (result) {
            TransactionResult.SUCCESS -> R.string.feature_formbased_ui_commited
            TransactionResult.NOT_IN_TRANSACTION -> R.string.feature_formbased_ui_not_in_transaction
        }

        _uiState.update {
            UiState(message = message)
        }
    }

    private fun rollback() = viewModelScope.launch {
        val result = storage.rollback()
        val message = when (result) {
            TransactionResult.SUCCESS -> R.string.feature_formbased_ui_rolled_back
            TransactionResult.NOT_IN_TRANSACTION -> R.string.feature_formbased_ui_not_in_transaction
        }

        _uiState.update {
            UiState(message = message)
        }
    }

    private fun handleDismissMessage() {
        _uiState.update {
            it.copy(message = null)
        }
    }
}
