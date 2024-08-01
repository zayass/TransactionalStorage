package org.zayass.assessment.storage.feature.formBased.ui.forms.delete

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.zayass.assessment.storage.core.designsystem.components.MediumSpacer
import org.zayass.assessment.storage.feature.formBased.R
import org.zayass.assessment.storage.feature.formBased.ui.forms.OneFieldForm

@Composable
internal fun DeleteKeyForm(viewModel: DeleteKeyViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DeleteKeyForm(
        result = state.result,
        key = state.key,
        onKeyChanged = { viewModel.dispatchAction(UiAction.KeyChanged(it)) },
        isExecuteEnabled = state.isExecuteEnabled,
        onExecute = { viewModel.dispatchAction(UiAction.Execute) },
        isConfirmationVisible = state.isConfirmationVisible,
        onConfirm = { viewModel.dispatchAction(UiAction.Confirm) },
        onDismissConfirmation = { viewModel.dispatchAction(UiAction.DismissConfirmation) },
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
    onDismissConfirmation: () -> Unit,
) {
    Column {
        OneFieldForm(
            value = key,
            onValueChange = onKeyChanged,
            label = stringResource(R.string.feature_formbased_ui_key),
            isExecuteEnabled = isExecuteEnabled,
            onExecute = onExecute,
        )

        if (result) {
            MediumSpacer()
            Text(text = stringResource(R.string.feature_formbased_ui_deleted))
        }
    }

    if (isConfirmationVisible) {
        AlertDialog(
            title = {
                Text(text = stringResource(id = R.string.feature_formbased_ui_are_you_sure))
            },
            onDismissRequest = onDismissConfirmation,
            dismissButton = {
                Button(onClick = onDismissConfirmation) {
                    Text(text = stringResource(id = R.string.feature_formbased_ui_discard))
                }
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(text = stringResource(id = R.string.feature_formbased_ui_confirm))
                }
            },
        )
    }
}
