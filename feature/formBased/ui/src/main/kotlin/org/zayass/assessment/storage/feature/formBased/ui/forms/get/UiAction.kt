package org.zayass.assessment.storage.feature.formBased.ui.forms.get

internal sealed interface UiAction {
    data class KeyChanged(val key: String): UiAction
    data object Execute: UiAction
}