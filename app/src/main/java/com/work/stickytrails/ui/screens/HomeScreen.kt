package com.work.stickytrails.ui.screens

import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No notes available. Add some!")
                }
            }
            else -> {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = getColorIcon(note.color),
                    contentDescription = "Note Color Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }

            // Content
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp),
                color = Color.White
            )

            // Time Display
            TimeDisplay(createdAt = note.createdAt)
        }
    }
}

// Function to get icon based on the note's color
fun getColorIcon(color: String): ImageVector {
    return when (color) {
        "yellow" -> Icons.Filled.Star
        "green" -> Icons.Filled.Check
        "blue" -> Icons.Filled.Cloud
        "pink" -> Icons.Filled.Favorite
        "purple" -> Icons.Filled.Palette
        "indigo" -> Icons.Filled.StarBorder
        "red" -> Icons.Filled.Warning
        else -> Icons.Filled.Help
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

