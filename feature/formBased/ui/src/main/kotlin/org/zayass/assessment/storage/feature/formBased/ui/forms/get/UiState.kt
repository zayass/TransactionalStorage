package org.zayass.assessment.storage.feature.formBased.ui.forms.get

internal data class UiState(
    val key: String = "",
    val isResultVisible: Boolean = false,
    val result: String? = null,
    val isExecuteEnabled: Boolean = false,
)
