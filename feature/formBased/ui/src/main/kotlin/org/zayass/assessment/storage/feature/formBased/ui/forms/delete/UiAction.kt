package org.zayass.assessment.storage.feature.formBased.ui.forms.delete

internal sealed interface UiAction {
    data class KeyChanged(val key: String): UiAction
    data object Execute: UiAction
    data object Confirm: UiAction
    data object DismissConfirmation: UiAction
}