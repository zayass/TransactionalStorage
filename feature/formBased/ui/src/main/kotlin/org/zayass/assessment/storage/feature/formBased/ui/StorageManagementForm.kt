package org.zayass.assessment.storage.feature.formBased.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.zayass.assessment.storage.feature.formBased.R
import org.zayass.assessment.storage.feature.formBased.ui.forms.count.CountValuesForm
import org.zayass.assessment.storage.feature.formBased.ui.forms.delete.DeleteKeyForm
import org.zayass.assessment.storage.feature.formBased.ui.forms.get.GetValueForm
import org.zayass.assessment.storage.feature.formBased.ui.forms.set.SetValueForm

internal enum class Pages {
    GET,
    COUNT,
    SET,
    DELETE
}

@Composable
private fun Pages.title() = stringResource(id = when (this) {
    Pages.GET -> R.string.feature_formbased_ui_get
    Pages.COUNT -> R.string.feature_formbased_ui_count
    Pages.SET -> R.string.feature_formbased_ui_set
    Pages.DELETE -> R.string.feature_formbased_ui_delete
})

@Composable
fun StorageManagementForm(viewModel: TransactionsViewModel = hiltViewModel()) {
    val isConfirmationVisible by viewModel.isConfirmationVisible.collectAsState()
    StorageManagementForm(
        onBeginTransaction = { viewModel.dispatchAction(UiAction.Begin) },
        onCommitTransaction = { viewModel.dispatchAction(UiAction.Commit) },
        onRollbackTransaction = { viewModel.dispatchAction(UiAction.Rollback) },
        isConfirmationVisible = isConfirmationVisible,
        onDismissConfirmation = { viewModel.dispatchAction(UiAction.DismissConfirmation) },
        onConfirm = { viewModel.dispatchAction(UiAction.Confirm) }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StorageManagementForm(
    onBeginTransaction: () -> Unit,
    onCommitTransaction: () -> Unit,
    onRollbackTransaction: () -> Unit,

    isConfirmationVisible: Boolean,
    onDismissConfirmation: () -> Unit,
    onConfirm: () -> Unit
) {
    val pages = Pages.entries

    val pagerState = rememberPagerState(
        pageCount = { pages.size },
        initialPage = 0,
    )

    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.imePadding()) {
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = tabIndex == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(text = page.title())
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top,
        ) { index ->
            Box(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                val page = pages[index]
                when (page) {
                    Pages.GET -> GetValueForm()
                    Pages.COUNT -> CountValuesForm()
                    Pages.SET -> SetValueForm()
                    Pages.DELETE -> DeleteKeyForm()
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))
        TransactionButtons(
            onBeginTransaction = onBeginTransaction,
            onCommitTransaction = onCommitTransaction,
            onRollbackTransaction = onRollbackTransaction
        )
        Spacer(modifier = Modifier.size(16.dp))
    }

    if (isConfirmationVisible) {
        AlertDialog(
            title = {
                Text(text = stringResource(R.string.feature_formbased_ui_are_you_sure))
            },
            onDismissRequest = onDismissConfirmation,
            dismissButton = {
                Button(onClick = onDismissConfirmation) {
                    Text(text = stringResource(R.string.feature_formbased_ui_discard))
                }
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(text = stringResource(R.string.feature_formbased_ui_confirm))
                }
            },
        )
    }
}

@Composable
fun TransactionButtons(
    onBeginTransaction: () -> Unit,
    onCommitTransaction: () -> Unit,
    onRollbackTransaction: () -> Unit
) {
    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        Button(onClick = onBeginTransaction) {
            Text(text = stringResource(R.string.feature_formbased_ui_begin))
        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = onCommitTransaction) {
            Text(text = stringResource(R.string.feature_formbased_ui_commit))
        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = onRollbackTransaction) {
            Text(text = stringResource(R.string.feature_formbased_ui_rollback))
        }
    }
}
