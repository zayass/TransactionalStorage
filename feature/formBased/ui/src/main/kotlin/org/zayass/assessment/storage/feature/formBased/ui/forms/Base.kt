package org.zayass.assessment.storage.feature.formBased.ui.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.zayass.assessment.storage.feature.formBased.R

@Composable
internal fun OneFieldForm(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isExecuteEnabled: Boolean,
    onExecute: () -> Unit,
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(text = label)
            },
        )
        FormSpacer()

        ExecuteButton(
            enabled = isExecuteEnabled,
            onExecute = onExecute,
        )
    }
}

@Composable
internal fun TwoFieldForm(
    value1: String,
    value2: String,
    onValue1Change: (String) -> Unit,
    onValue2Change: (String) -> Unit,
    label1: String,
    label2: String,
    isExecuteEnabled: Boolean,
    onExecute: () -> Unit,
) {
    Column {
        OutlinedTextField(
            value = value1,
            onValueChange = onValue1Change,
            label = {
                Text(text = label1)
            },
        )
        FormSpacer()

        OutlinedTextField(
            value = value2,
            onValueChange = onValue2Change,
            label = {
                Text(text = label2)
            },
        )
        FormSpacer()

        ExecuteButton(
            enabled = isExecuteEnabled,
            onExecute = onExecute,
        )
    }
}

@Composable
private fun FormSpacer() {
    Spacer(modifier = Modifier.size(16.dp))
}

@Composable
private fun ColumnScope.ExecuteButton(
    enabled: Boolean,
    onExecute: () -> Unit,
) {
    Button(
        modifier = Modifier.align(alignment = Alignment.End),
        enabled = enabled,
        onClick = onExecute,
    ) {
        Text(text = stringResource(R.string.feature_formbased_ui_execute))
    }
}
