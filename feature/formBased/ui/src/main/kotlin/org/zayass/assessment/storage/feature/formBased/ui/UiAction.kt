package org.zayass.assessment.storage.feature.formBased.ui

internal sealed interface UiAction {
    data object Begin : UiAction
    data object Commit : UiAction
    data object Rollback : UiAction
    data object DismissConfirmation : UiAction
    data object Confirm : UiAction
    data object DismissMessage : UiAction
}
