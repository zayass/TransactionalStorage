package org.zayass.assessment.storage.core.designsystem.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.zayass.assessment.storage.core.designsystem.theme.dimens

@Composable
fun SmallSpacer() = Spacer(modifier = Modifier.size(size = MaterialTheme.dimens.small))

@Composable
fun MediumSpacer() = Spacer(modifier = Modifier.size(size = MaterialTheme.dimens.medium))
