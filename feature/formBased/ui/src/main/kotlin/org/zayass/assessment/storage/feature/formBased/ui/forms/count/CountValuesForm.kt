package org.zayass.assessment.storage.feature.formBased.ui.forms.count

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.zayass.assessment.storage.feature.formBased.R
import org.zayass.assessment.storage.feature.formBased.ui.forms.OneFieldForm

@Composable
internal fun CountValuesForm(viewModel: CountValuesViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    CountValuesForm(
        count = state.result,
        value = state.value,
        onValueChanged = { viewModel.dispatchAction(UiAction.ValueChanged(it) )},
        isExecuteEnabled = state.isExecuteEnabled,
        onExecute = { viewModel.dispatchAction(UiAction.Execute) }
    )
}

@Composable
internal fun CountValuesForm(
    count: Int?,
    value: String,
    onValueChanged: (String) -> Unit,
    isExecuteEnabled: Boolean,
    onExecute: () -> Unit
) {
    Column {
        OneFieldForm(
            value = value,
            onValueChange = onValueChanged,
            label = stringResource(R.string.feature_formbased_ui_value),
            isExecuteEnabled = isExecuteEnabled,
            onExecute = onExecute
        )
        if (count != null) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "Count: $count")
        }
    }
}