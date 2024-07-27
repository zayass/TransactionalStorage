package org.zayass.assessment.storage.feature.formBased.ui.forms.get

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import org.zayass.assessment.storage.feature.formBased.R
import org.zayass.assessment.storage.feature.formBased.ui.forms.OneFieldForm
import org.zayass.assessment.storage.feature.formBased.ui.forms.TwoFieldForm

@Composable
internal fun GetValueForm(viewModel: GetValueViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    GetValueForm(
        isValueVisible = state.isResultVisible,
        value = state.result,
        key = state.key,
        onKeyChanged = { viewModel.dispatchAction(UiAction.KeyChanged(it) )},
        isExecuteEnabled = state.isExecuteEnabled,
        onExecute = { viewModel.dispatchAction(UiAction.Execute) }
    )
}

@Composable
internal fun GetValueForm(
    isValueVisible: Boolean,
    value: String?,
    key: String,
    onKeyChanged: (String) -> Unit,
    isExecuteEnabled: Boolean,
    onExecute: () -> Unit
) {
    Column {
        OneFieldForm(
            value = key,
            onValueChange = onKeyChanged,
            label = stringResource(R.string.feature_formbased_ui_key),
            isExecuteEnabled = isExecuteEnabled,
            onExecute = onExecute,
        )

        if (isValueVisible) {
            val text = if (value != null) {
                stringResource(R.string.feature_formbased_ui_get_result, value)
            } else {
                stringResource(R.string.feature_formbased_ui_get_empty_result)
            }

            Text(text = text)
        }
    }
}





