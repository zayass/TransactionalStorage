package org.zayass.assessment.storage.feature.formBased.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.zayass.assessment.storage.core.StringSuspendStorage
import javax.inject.Inject

private enum class ConfirmationStatus {
    NONE,
    COMMIT_REQUESTED,
    ROLLBACK_REQUESTED
}

@HiltViewModel
class TransactionsViewModel @Inject internal constructor(
    private val storage: StringSuspendStorage
) : ViewModel() {
    private val _isConfirmationVisible = MutableStateFlow(false)
    val isConfirmationVisible: StateFlow<Boolean>
        get() = _isConfirmationVisible.asStateFlow()

    private var confirmationStatus = ConfirmationStatus.NONE

    internal fun dispatchAction(action: UiAction) {
        when (action) {
            UiAction.Begin -> handleBegin()
            UiAction.Commit -> handleCommit()
            UiAction.Rollback -> handleRollback()
            UiAction.DismissConfirmation -> handleDismissConfirmation()
            UiAction.Confirm -> handleConfirm()
        }
    }

    private fun handleBegin() = viewModelScope.launch {
        storage.begin()
    }

    private fun handleCommit() {
        confirmationStatus = ConfirmationStatus.COMMIT_REQUESTED
        _isConfirmationVisible.value = true
    }

    private fun handleRollback() {
        confirmationStatus = ConfirmationStatus.ROLLBACK_REQUESTED
        _isConfirmationVisible.value = true
    }

    private fun handleDismissConfirmation() {
        confirmationStatus = ConfirmationStatus.NONE
        _isConfirmationVisible.value = false
    }

    private fun handleConfirm() {
        when (confirmationStatus) {
            ConfirmationStatus.COMMIT_REQUESTED -> commit()
            ConfirmationStatus.ROLLBACK_REQUESTED -> rollback()
            ConfirmationStatus.NONE -> { }
        }

        _isConfirmationVisible.value = false
    }

    private fun commit() = viewModelScope.launch {
        storage.commit()
    }

    private fun rollback() = viewModelScope.launch {
        storage.rollback()
    }
}