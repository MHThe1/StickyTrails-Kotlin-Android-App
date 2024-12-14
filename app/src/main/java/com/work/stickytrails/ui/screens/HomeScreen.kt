package com.work.stickytrails.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Correct import
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.work.stickytrails.models.NoteModel
import com.work.stickytrails.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onLogout: () -> Unit
) {
    // Collect the notes state
    val notes = viewModel.notes.collectAsState(initial = emptyList()).value

    // Fetch notes when the screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchNotes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, // Ensure space between items
            verticalAlignment = Alignment.CenterVertically // Vertically align items
        ) {
            // "Your Notes" text aligned to the left
            Text(
                text = "Your Notes",
                style = MaterialTheme.typography.headlineSmall,
            )

            // "Logout" button aligned to the right
            Button(
                onClick = { onLogout() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Logout")
            }
        }



        Spacer(modifier = Modifier.height(8.dp))

        // Notes list
        if (notes.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(notes) { note -> // Correct usage of items
                    NoteCard(note) // Reuse NoteCard Composable
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            // Show empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No notes available. Add some!")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun NoteCard(note: NoteModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "Priority: ${note.priority}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
