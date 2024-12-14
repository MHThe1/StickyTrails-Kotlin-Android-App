package com.work.stickytrails.ui.screens

import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.ui.tooling.preview.Preview
import com.work.stickytrails.models.NoteModel
import com.work.stickytrails.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

val colorConfig = mapOf(
    "yellow" to Color(0xFFEAB308),  // Yellow
    "green" to Color(0xFF22C55E),   // Green
    "blue" to Color(0xFF3B82F6),    // Blue
    "pink" to Color(0xFFEC4899),    // Pink
    "purple" to Color(0xFF9333EA),  // Purple
    "indigo" to Color(0xFFBD82F2),  // Indigo
    "red" to Color(0xFFDC2626)      // Red
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onLogout: () -> Unit
) {
    // Collect the notes state and loading/error states
    val notes = viewModel.notes.collectAsState(initial = emptyList()).value
    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Notes",
                style = MaterialTheme.typography.headlineSmall,
            )

            Button(
                onClick = { onLogout() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                // Show a loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                // Show an error message with a retry button
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error: $errorMessage", color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchNotes() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            notes.isEmpty() -> {
                // Show empty state if there are no notes
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No notes available. Add some!")
                }
            }
            else -> {
                // Show notes if they exist
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notes) { note ->
                        if (isValidNote(note)) {
                            NoteCard(note)
                            Spacer(modifier = Modifier.height(8.dp))
                        } else {
                            Log.e("HomeScreen", "Invalid note detected: $note")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Validation function to check if the note has valid data
fun isValidNote(note: NoteModel): Boolean {
    return !note.title.isNullOrEmpty() &&
            !note.content.isNullOrEmpty() &&
            !note.createdAt.isNullOrEmpty() &&
            colorConfig.containsKey(note.color)
}


@Composable
fun NoteCard(note: NoteModel) {
    // Retrieve the color based on the note's color, with a default fallback to yellow
    val noteColor = colorConfig[note.color] ?: colorConfig["yellow"]!!

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = noteColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            // Content
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp),
                color = Color.White
            )

            // Priority
            Text(
                text = "Priority: ${note.priority}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )

            // Time Display
            TimeDisplay(createdAt = note.createdAt)
        }
    }
}

@Composable
fun TimeDisplay(createdAt: String) {
    val time = formatTime(createdAt)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.AccessTime,
            contentDescription = "Time",
            modifier = Modifier.size(18.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

fun formatTime(createdAt: String): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = format.parse(createdAt) ?: Date()
        val now = Date()
        val diffMs = now.time - date.time

        val diffMinutes = (diffMs / (1000 * 60)) % 60
        val diffHours = (diffMs / (1000 * 60 * 60)) % 24
        val diffDays = (diffMs / (1000 * 60 * 60 * 24))

        when {
            diffMinutes < 1 -> "Just now"
            diffHours < 1 -> "$diffMinutes min ago"
            diffDays < 1 -> "$diffHours hours ago"
            diffDays in 1..30 -> "$diffDays days ago"
            else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
        }
    } catch (e: Exception) {
        Log.e("TimeDisplay", "Error parsing date: $createdAt", e)
        "Unknown time"
    }
}

