package org.zayass.assessment.storage.feature.formBased.ui.forms.count

internal data class UiState(
    val value: String = "",
    val result: Int? = null,
    val isExecuteEnabled: Boolean = false,
)