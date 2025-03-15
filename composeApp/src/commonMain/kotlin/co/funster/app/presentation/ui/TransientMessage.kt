package co.funster.app.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import funster.composeapp.generated.resources.Res
import funster.composeapp.generated.resources.dismiss
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

/**
 * Composable function that displays a transient error message with a dismiss button and automatic timeout.
 * The message appears with a fade-in animation and disappears with a fade-out animation when dismissed
 * or after a specified duration. Uses [AnimatedVisibility] for animation and [Surface] for styling.
 *
 * @param message The error message to display. If null, the composable is not rendered.
 * @param onDismiss Callback invoked when the message is dismissed, either by the user clicking "Dismiss"
 *                  or after the timeout expires.
 * @param modifier Optional [Modifier] to apply to the [AnimatedVisibility] container. Defaults to an empty [Modifier].
 * @param durationMillis Duration in milliseconds after which the message automatically dismisses.
 *                       Defaults to 3000L (3 seconds).
 */
@Composable
fun TransientMessage(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = 3000L // 3 seconds
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        message?.let { msg ->
            Surface(
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(AppDimens.MediumPadding)
            ) {
                Row(
                    modifier = Modifier.padding(AppDimens.SmallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(Res.string.dismiss), color = MaterialTheme.colorScheme.onError)
                    }
                }
            }
            LaunchedEffect(Unit) {
                delay(durationMillis)
                onDismiss()
            }
        }
    }
}
