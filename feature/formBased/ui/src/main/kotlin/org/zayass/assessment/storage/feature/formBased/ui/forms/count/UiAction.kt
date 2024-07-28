package org.zayass.assessment.storage.feature.formBased.ui.forms.count

internal sealed interface UiAction {
    data class ValueChanged(val value: String) : UiAction
    data object Execute : UiAction
}
