package com.example.financeapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Suppress("FunctionNaming")
internal fun TransactionScreen(
    viewModel: TransactionViewModel = viewModel(),
) {
    val state = viewModel.uiState

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.transaction_title))

            Text(
                stringResource(
                    R.string.balance_summary,
                    state.balance,
                    state.transactionCount,
                ),
            )

            DescriptionField(
                description = state.description,
                onDescriptionChange = {
                    viewModel.onEvent(MainUiEvent.DescriptionChanged(it))
                },
            )

            AmountField(
                amount = state.amount,
                onAmountChange = {
                    viewModel.onEvent(MainUiEvent.AmountChanged(it))
                },
            )

            TypeSelector(
                selectedType = state.type,
                onTypeChange = {
                    viewModel.onEvent(MainUiEvent.TypeChanged(it))
                },
            )

            ErrorText(error = state.error)

            AddButton(onClick = viewModel::addTransaction)

            if (state.transactions.isNotEmpty()) {
                TransactionHistory(transactions = state.transactions)
            }
        }
    }
}

@Composable
private fun DescriptionField(
    description: String,
    onDescriptionChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = {
            Text(stringResource(R.string.transaction_description))
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun AmountField(
    amount: String,
    onAmountChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = amount,
        onValueChange = onAmountChange,
        label = {
            Text(stringResource(R.string.transaction_amount))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun TypeSelector(
    selectedType: TransactionType,
    onTypeChange: (TransactionType) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selectedType == TransactionType.EXPENSE,
            onClick = {
                onTypeChange(TransactionType.EXPENSE)
            },
            label = {
                Text(stringResource(R.string.transaction_expense))
            },
        )
        FilterChip(
            selected = selectedType == TransactionType.INCOME,
            onClick = {
                onTypeChange(TransactionType.INCOME)
            },
            label = {
                Text(stringResource(R.string.transaction_income))
            },
        )
    }
}

@Composable
private fun ErrorText(error: TransactionError?) {
    error?.let { currentError ->
        val textId = when (currentError) {
            TransactionError.INVALID_INPUT -> R.string.transaction_invalid_input
            TransactionError.AMOUNT_TOO_LARGE -> R.string.transaction_amount_too_large
        }
        Text(
            text = stringResource(textId),
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun AddButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.transaction_add))
    }
}

@Composable
private fun TransactionHistory(transactions: List<Transaction>) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.transaction_history),
        style = MaterialTheme.typography.titleMedium,
    )

    transactions.asReversed().forEach { tx ->
        val row = if (tx.type == TransactionType.INCOME) {
            R.string.transaction_history_income
        } else {
            R.string.transaction_history_expense
        }
        Text(stringResource(row, tx.description, tx.amount.toString()))
    }
}
