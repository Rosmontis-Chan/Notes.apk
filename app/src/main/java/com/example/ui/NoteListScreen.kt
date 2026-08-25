package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NoteEntity
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.NoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    state: NotesUiState,
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    val pinnedNotes = remember(state.notes) { state.notes.filter { it.isPinned } }
    val unpinnedNotes = remember(state.notes) { state.notes.filter { !it.isPinned } }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("note_list_screen"),
        floatingActionButton = {
            if (state.activeTab == "Catatan") {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.openNewNote() },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Tulis Catatan", fontWeight = FontWeight.SemiBold) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("fab_add_note")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // Header: App Title & Top Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Catatan Lite",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Ringan, Cepat & Indah",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Stats / Analytics Button
                    IconButton(
                        onClick = { viewModel.showStatsDialog(true) },
                        modifier = Modifier.testTag("button_stats")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Analytics,
                            contentDescription = "Statistik Catatan",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // View Mode Toggle (Grid / List)
                    IconButton(
                        onClick = { viewModel.toggleViewMode() },
                        modifier = Modifier.testTag("button_toggle_view")
                    ) {
                        Icon(
                            imageVector = if (state.isGridView) Icons.Default.ViewAgenda else Icons.Default.GridView,
                            contentDescription = "Ganti Tampilan",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // About App Button
                    IconButton(
                        onClick = { viewModel.showAboutDialog(true) },
                        modifier = Modifier.testTag("button_about")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Tentang Aplikasi",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Search Bar & Sort
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Cari catatan atau kata kunci...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.onSearchQueryChanged("") },
                                modifier = Modifier.testTag("clear_search_button")
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Hapus Pencarian")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_notes_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("sort_notes_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Urutkan",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOrder.values().forEach { order ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = order.label,
                                        fontWeight = if (state.sortOrder == order) FontWeight.Bold else FontWeight.Normal,
                                        color = if (state.sortOrder == order) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    showSortMenu = false
                                    viewModel.setSortOrder(order)
                                }
                            )
                        }
                    }
                }
            }

            // Tabs: Semua Catatan vs Arsip
            TabRow(
                selectedTabIndex = if (state.activeTab == "Catatan") 0 else 1,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (state.activeTab == "Catatan") 0 else 1]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = state.activeTab == "Catatan",
                    onClick = { viewModel.setActiveTab("Catatan") },
                    text = { Text("Catatan (${state.activeCount})", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_active_notes")
                )
                Tab(
                    selected = state.activeTab == "Arsip",
                    onClick = { viewModel.setActiveTab("Arsip") },
                    text = { Text("Arsip (${state.archivedNotes.size})", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_archived_notes")
                )
            }

            // Category Chips Row
            CategoryFilterRow(
                selectedCategory = state.selectedCategory,
                onCategorySelected = { viewModel.onCategorySelected(it) },
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Content List / Grid / Empty State
            if (state.notes.isEmpty()) {
                EmptyNotesView(
                    searchQuery = state.searchQuery,
                    selectedCategory = state.selectedCategory,
                    isArchiveTab = state.activeTab == "Arsip",
                    onAddClick = { viewModel.openNewNote() }
                )
            } else {
                if (state.isGridView) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalItemSpacing = 10.dp,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("notes_staggered_grid")
                    ) {
                        if (pinnedNotes.isNotEmpty() && state.activeTab == "Catatan") {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DISMATKAN (${pinnedNotes.size})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            items(pinnedNotes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    onClick = { viewModel.openEditNote(note) },
                                    onTogglePin = { viewModel.togglePin(note) },
                                    onToggleArchive = { viewModel.toggleArchive(note) },
                                    onDuplicate = { viewModel.duplicateNote(note) },
                                    onDelete = { viewModel.confirmDeleteNote(note) }
                                )
                            }

                            if (unpinnedNotes.isNotEmpty()) {
                                item(span = StaggeredGridItemSpan.FullLine) {
                                    Text(
                                        text = "CATATAN LAINNYA",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        val regularList = if (pinnedNotes.isNotEmpty() && state.activeTab == "Catatan") unpinnedNotes else state.notes
                        items(regularList, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { viewModel.openEditNote(note) },
                                onTogglePin = { viewModel.togglePin(note) },
                                onToggleArchive = { viewModel.toggleArchive(note) },
                                onDuplicate = { viewModel.duplicateNote(note) },
                                onDelete = { viewModel.confirmDeleteNote(note) }
                            )
                        }
                    }
                } else {
                    // Single column list view
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("notes_linear_list")
                    ) {
                        if (pinnedNotes.isNotEmpty() && state.activeTab == "Catatan") {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DISMATKAN (${pinnedNotes.size})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            items(pinnedNotes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    onClick = { viewModel.openEditNote(note) },
                                    onTogglePin = { viewModel.togglePin(note) },
                                    onToggleArchive = { viewModel.toggleArchive(note) },
                                    onDuplicate = { viewModel.duplicateNote(note) },
                                    onDelete = { viewModel.confirmDeleteNote(note) }
                                )
                            }

                            if (unpinnedNotes.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "CATATAN LAINNYA",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        val regularList = if (pinnedNotes.isNotEmpty() && state.activeTab == "Catatan") unpinnedNotes else state.notes
                        items(regularList, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { viewModel.openEditNote(note) },
                                onTogglePin = { viewModel.togglePin(note) },
                                onToggleArchive = { viewModel.toggleArchive(note) },
                                onDuplicate = { viewModel.duplicateNote(note) },
                                onDelete = { viewModel.confirmDeleteNote(note) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (state.noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelDeleteNote() },
            title = { Text("Hapus Catatan?") },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus catatan \"${state.noteToDelete.title.take(30)}\"? Tindakan ini tidak dapat dibatalkan."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.executeDeleteNote() },
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cancelDeleteNote() },
                    modifier = Modifier.testTag("cancel_delete_button")
                ) {
                    Text("Batal")
                }
            }
        )
    }

    // About App Dialog
    if (state.showAboutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAboutDialog(false) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Catatan Lite")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Aplikasi Catatan Super Ringan (~1MB), Cepat & Indah.",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "• 🪶 Ukuran aplikasi sangat kecil & hemat penyimpanan\n" +
                                "• ⚡ Akses instan tanpa loading dengan Room SQLite\n" +
                                "• 🎨 Palet warna pastel & kategori dinamis\n" +
                                "• 🔒 100% Offline & privasi terjaga\n" +
                                "• 📥 APK dapat diunduh langsung melalui menu AI Studio",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.showAboutDialog(false) }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Stats Dialog
    if (state.showStatsDialog) {
        val totalActive = state.activeCount
        val totalArchived = state.archivedNotes.size
        val totalWords = (state.notes + state.archivedNotes).sumOf { note ->
            note.content.trim().split("\\s+".toRegex()).count { it.isNotEmpty() }
        }

        AlertDialog(
            onDismissRequest = { viewModel.showStatsDialog(false) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Statistik Catatan")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatRow(label = "Catatan Aktif", value = "$totalActive catatan")
                    StatRow(label = "Catatan Diarsipkan", value = "$totalArchived catatan")
                    StatRow(label = "Total Kata Ditulis", value = "$totalWords kata")
                    StatRow(label = "Penyimpanan", value = "Room SQLite (Ultra Ringan)")
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.showStatsDialog(false) }) {
                    Text("Selesai", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun EmptyNotesView(
    searchQuery: String,
    selectedCategory: String,
    isArchiveTab: Boolean,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isArchiveTab) Icons.Default.Archive else if (searchQuery.isNotEmpty()) Icons.Default.Search else Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val title = when {
                searchQuery.isNotEmpty() -> "Tidak Ditemukan"
                isArchiveTab -> "Belum Ada Arsip"
                selectedCategory != "Semua" -> "Kategori Kosong"
                else -> "Mulai Menulis Ide!"
            }

            val subtitle = when {
                searchQuery.isNotEmpty() -> "Tidak ada catatan yang cocok dengan \"$searchQuery\"."
                isArchiveTab -> "Catatan yang Anda arsipkan akan muncul di sini."
                selectedCategory != "Semua" -> "Belum ada catatan dengan kategori \"$selectedCategory\"."
                else -> "Catatan Lite siap membantumu mencatat to-do list, ide kreatif, dan hal penting lainnya."
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (!isArchiveTab && searchQuery.isEmpty() && selectedCategory == "Semua") {
                Spacer(modifier = Modifier.height(20.dp))
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("empty_state_add_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Buat Catatan")
                }
            }
        }
    }
}
