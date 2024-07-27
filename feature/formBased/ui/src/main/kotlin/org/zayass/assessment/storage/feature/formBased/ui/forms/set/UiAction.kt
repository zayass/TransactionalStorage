package org.zayass.assessment.storage.feature.formBased.ui.forms.set

internal sealed interface UiAction {
    data class KeyChanged(val key: String): UiAction
    data class ValueChanged(val value: String): UiAction
    data object Execute: UiAction
}