package org.zayass.assessment.storage.feature.formBased.ui.forms.set

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.zayass.assessment.storage.core.designsystem.components.MediumSpacer
import org.zayass.assessment.storage.feature.formBased.R
import org.zayass.assessment.storage.feature.formBased.ui.forms.TwoFieldForm

@Composable
internal fun SetValueForm(viewModel: SetValueViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SetValueForm(
        valueUpdated = state.result,
        key = state.key,
        onKeyChanged = { viewModel.dispatchAction(UiAction.KeyChanged(it)) },
        value = state.value,
        onValueChanged = { viewModel.dispatchAction(UiAction.ValueChanged(it)) },
        isExecuteEnabled = state.isExecuteEnabled,
        onExecute = { viewModel.dispatchAction(UiAction.Execute) },
    )
}

@Composable
internal fun SetValueForm(
    valueUpdated: Boolean,
    key: String,
    onKeyChanged: (String) -> Unit,
    value: String,
    onValueChanged: (String) -> Unit,
    isExecuteEnabled: Boolean,
    onExecute: () -> Unit,
) {
    Column {
        TwoFieldForm(
            value1 = key,
            onValue1Change = onKeyChanged,
            label1 = stringResource(R.string.feature_formbased_ui_key),
            value2 = value,
            onValue2Change = onValueChanged,
            label2 = stringResource(R.string.feature_formbased_ui_value),
            isExecuteEnabled = isExecuteEnabled,
            onExecute = onExecute,
        )

        if (valueUpdated) {
            MediumSpacer()
            Text(text = stringResource(R.string.feature_formbased_ui_value_updated))
        }
    }
}
