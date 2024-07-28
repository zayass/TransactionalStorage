package org.zayass.assessment.storage.feature.formBased.ui.forms.delete

internal data class UiState(
    val key: String = "",
    val result: Boolean = false,
    val isConfirmationVisible: Boolean = false,
    val isExecuteEnabled: Boolean = false,
)
