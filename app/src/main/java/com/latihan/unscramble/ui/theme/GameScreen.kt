package com.latihan.unscramble.ui.theme

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.latihan.unscramble.R

@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val mediumPadding = dimensionResource(id = R.dimen.padding_medium)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Unscramble",
                fontSize = 28.sp,
                color = colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            ) 
            GameStatus(score = gameUiState.score)
        }
        GameLayout(
            onUserGuessChanged = {gameViewModel.updateUserGuess(it)},
            onKeyboardDone = {gameViewModel.checkUserGuess()},
            userGuess = gameViewModel.userGuess,
            isGuessWrong = gameUiState.isGuessedWordWrong,
            currentScrambleWord = gameUiState.currentScrambleWord,
            wordCount = gameUiState.currentWordCount,
            modifier = Modifier.padding(20.dp)
        )
        Button(
            onClick = {gameViewModel.checkUserGuess()},
            modifier = Modifier
                .fillMaxWidth()
                .padding(mediumPadding)
        ) {
            Text(
                text = stringResource(id = R.string.submit),
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        OutlinedButton(
            onClick = {gameViewModel.skipWord()},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = mediumPadding)
        ) {
            Text(
                text = stringResource(id = R.string.skip),
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
    if (gameUiState.isGameOver) {
        FinalDialog(
            score = gameUiState.score,
            onPlayAgain = { gameViewModel.resetGame()}
        )
    }
}

@Composable
fun GameLayout(
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    userGuess: String,
    isGuessWrong: Boolean,
    wordCount: Int,
    currentScrambleWord: String,
    modifier: Modifier = Modifier
) {
    val mediumPadding = dimensionResource(id = R.dimen.padding_medium)
    
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(mediumPadding)
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(Alignment.End),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.primary
                )
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = stringResource(id = R.string.word_count, wordCount),
                    style = typography.titleMedium,
                    color = colorScheme.onPrimary
                )
            }
            Text(
                text = currentScrambleWord,
                style = typography.displayMedium
            )
            Text(
                text = stringResource(R.string.instructions),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
            )
            OutlinedTextField(
                value = userGuess,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface
                ),
                onValueChange = onUserGuessChanged,
                label = {
                    if (isGuessWrong) {
                        Text(text = stringResource(id = R.string.wrong_guess))
                    } else {
                        Text(text = stringResource(id = R.string.enter_your_word))
                    }
                },
                isError = isGuessWrong,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {onKeyboardDone()}
                )
            )
        }
    }
}

@Composable
fun GameStatus(score: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primary
        )
    ) {
        Text(
            text = stringResource(id = R.string.score, score),
            style = typography.headlineSmall,
            color = colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun FinalDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {

        },
        title = { Text(text = stringResource(id = R.string.congratulations)) },
        text = { Text(text = stringResource(id = R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = {activity.finish()}) {
                Text(text = stringResource(id = R.string.exit))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onPlayAgain()
                }
            ) {
                Text(text = stringResource(id = R.string.play_again))
            }
        }
    )
}

@Preview
@Composable
fun ScreenPreview() {
    GameScreen()
}