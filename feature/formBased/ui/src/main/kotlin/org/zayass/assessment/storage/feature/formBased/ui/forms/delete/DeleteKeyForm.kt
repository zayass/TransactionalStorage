package org.zayass.assessment.storage.feature.formBased.ui.forms.delete

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import org.zayass.assessment.storage.feature.formBased.R
import org.zayass.assessment.storage.feature.formBased.ui.forms.OneFieldForm


@Composable
internal fun DeleteKeyForm(viewModel: DeleteKeyViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    DeleteKeyForm(
        result = state.result,
        key = state.key,
        onKeyChanged = { viewModel.dispatchAction(UiAction.KeyChanged(it)) },
        isExecuteEnabled =  state.isExecuteEnabled,
        onExecute = { viewModel.dispatchAction(UiAction.Execute) },
        isConfirmationVisible = state.isConfirmationVisible,
        onConfirm = { viewModel.dispatchAction(UiAction.Confirm) },
        onDismissConfirmation = { viewModel.dispatchAction(UiAction.DismissConfirmation) }
    )
}

@Composable
internal fun DeleteKeyForm(
    result: Boolean,
    key: String,
    onKeyChanged: (String) -> Unit,
    isExecuteEnabled: Boolean,
    onExecute: () -> Unit,
    isConfirmationVisible: Boolean,
    onConfirm: () -> Unit,
    onDismissConfirmation: () -> Unit
) {
    Column {
        OneFieldForm(
            value = key,
            onValueChange = onKeyChanged,
            label = stringResource(R.string.feature_formbased_ui_key),
            isExecuteEnabled = isExecuteEnabled,
            onExecute = onExecute
        )

        if (result) {
            Text(text = "Deleted")
        }
    }

    if (isConfirmationVisible) {
        AlertDialog(
            title = {
                Text(text = "Are you sure?")
            },
            onDismissRequest = onDismissConfirmation,
            dismissButton = {
                Button(onClick = onDismissConfirmation) {
                    Text(text = "Discard")
                }
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(text = "Confirm")
                }
            },
        )
    }
}