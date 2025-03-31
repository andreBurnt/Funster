package co.funster.app.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.funster.app.core.model.Event
import coil3.compose.AsyncImage
import funster.composeapp.generated.resources.Res
import funster.composeapp.generated.resources.no_image
import funster.composeapp.generated.resources.no_date_message
import funster.composeapp.generated.resources.unknown_location_message
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Composable function that displays a single event item in a card layout.
 * Shows the event's image, name, start date, and location in a visually appealing format.
 *
 * @param event The [Event] object containing the data to display, including name, image URL,
 *              start date, and location.
 * @param modifier Optional [Modifier] to apply to the [Card]. Defaults to an empty [Modifier].
 */
@Composable
fun EventItem(
    event: Event,
    modifier: Modifier = Modifier,
) {
    val placeholderPainter = painterResource(Res.drawable.no_image)

    Card(
        modifier = modifier.padding(AppDimens.SmallPadding),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.SmallPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image on the left
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.name,
                modifier = Modifier
                    .size(80.dp) // Fixed size for square image
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                placeholder = placeholderPainter,
                error = placeholderPainter,
            )

            Column(
                modifier = Modifier
                    .padding(start = AppDimens.SmallPadding)
                    .weight(1f)
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = AppDimens.ExtraSmallPadding),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = event.formattedStartDate ?: stringResource(Res.string.no_date_message),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF26A69A), // Teal
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = event.location ?: stringResource(Res.string.unknown_location_message),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun EventItemPreview() {
    val event = Event(
        id = "123",
        name = "Madonna",
        imageUrl = "https://en.wikipedia.org/wiki/Madonna#/media/File:HungUpSticky3.jpg",
        startDate = "01-01-2025",
        endDate = "02-01-2050",
        city = "Seattle",
        location = "The Spheres"
    )
    EventItem(event = event)
}

