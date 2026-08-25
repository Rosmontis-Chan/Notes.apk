package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.NoteEntity
import com.example.data.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOrder(val label: String) {
    DATE_DESC("Terbaru"),
    DATE_ASC("Terlama"),
    TITLE_AZ("Judul A-Z"),
    TITLE_ZA("Judul Z-A")
}

data class NotesUiState(
    val notes: List<NoteEntity> = emptyList(),
    val archivedNotes: List<NoteEntity> = emptyList(),
    val activeCount: Int = 0,
    val searchQuery: String = "",
    val selectedCategory: String = "Semua",
    val isGridView: Boolean = true,
    val sortOrder: SortOrder = SortOrder.DATE_DESC,
    val activeTab: String = "Catatan", // "Catatan" or "Arsip"
    val editingNote: NoteEntity? = null,
    val isEditorOpen: Boolean = false,
    val noteToDelete: NoteEntity? = null,
    val showAboutDialog: Boolean = false,
    val showStatsDialog: Boolean = false
)

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("Semua")
    private val _isGridView = MutableStateFlow(true)
    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)
    private val _activeTab = MutableStateFlow("Catatan")
    private val _editingNote = MutableStateFlow<NoteEntity?>(null)
    private val _isEditorOpen = MutableStateFlow(false)
    private val _noteToDelete = MutableStateFlow<NoteEntity?>(null)
    private val _showAboutDialog = MutableStateFlow(false)
    private val _showStatsDialog = MutableStateFlow(false)

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = NoteRepository(database.noteDao())
    }

    val uiState: StateFlow<NotesUiState> = combine(
        repository.allActiveNotes,
        repository.archivedNotes,
        _searchQuery,
        _selectedCategory,
        _isGridView,
        _sortOrder,
        _activeTab,
        _editingNote,
        _isEditorOpen,
        _noteToDelete,
        _showAboutDialog,
        _showStatsDialog
    ) { params ->
        val rawActiveNotes = params[0] as List<NoteEntity>
        val rawArchivedNotes = params[1] as List<NoteEntity>
        val query = params[2] as String
        val category = params[3] as String
        val isGrid = params[4] as Boolean
        val sort = params[5] as SortOrder
        val tab = params[6] as String
        val editing = params[7] as NoteEntity?
        val isEditorVisible = params[8] as Boolean
        val deleting = params[9] as NoteEntity?
        val aboutDialog = params[10] as Boolean
        val statsDialog = params[11] as Boolean

        val targetList = if (tab == "Arsip") rawArchivedNotes else rawActiveNotes

        val filteredNotes = targetList
            .filter { note ->
                val matchesQuery = query.isBlank() ||
                        note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true)
                val matchesCategory = category == "Semua" || note.category.equals(category, ignoreCase = true)
                matchesQuery && matchesCategory
            }
            .let { list ->
                when (sort) {
                    SortOrder.DATE_DESC -> list.sortedWith(compareByDescending<NoteEntity> { it.isPinned }.thenByDescending { it.updatedAt })
                    SortOrder.DATE_ASC -> list.sortedWith(compareByDescending<NoteEntity> { it.isPinned }.thenBy { it.updatedAt })
                    SortOrder.TITLE_AZ -> list.sortedWith(compareByDescending<NoteEntity> { it.isPinned }.thenBy { it.title.lowercase() })
                    SortOrder.TITLE_ZA -> list.sortedWith(compareByDescending<NoteEntity> { it.isPinned }.thenByDescending { it.title.lowercase() })
                }
            }

        NotesUiState(
            notes = filteredNotes,
            archivedNotes = rawArchivedNotes,
            activeCount = rawActiveNotes.size,
            searchQuery = query,
            selectedCategory = category,
            isGridView = isGrid,
            sortOrder = sort,
            activeTab = tab,
            editingNote = editing,
            isEditorOpen = isEditorVisible,
            noteToDelete = deleting,
            showAboutDialog = aboutDialog,
            showStatsDialog = statsDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _sortOrder.value = sortOrder
    }

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    fun openNewNote() {
        _editingNote.value = null
        _isEditorOpen.value = true
    }

    fun openEditNote(note: NoteEntity) {
        _editingNote.value = note
        _isEditorOpen.value = true
    }

    fun closeEditor() {
        _isEditorOpen.value = false
        _editingNote.value = null
    }

    fun saveNote(
        title: String,
        content: String,
        category: String,
        colorHex: String,
        isPinned: Boolean
    ) {
        val trimmedTitle = title.trim()
        val trimmedContent = content.trim()

        if (trimmedTitle.isEmpty() && trimmedContent.isEmpty()) {
            closeEditor()
            return
        }

        val resolvedTitle = if (trimmedTitle.isEmpty()) "Tanpa Judul" else trimmedTitle
        val now = System.currentTimeMillis()

        viewModelScope.launch {
            val current = _editingNote.value
            if (current != null) {
                repository.updateNote(
                    current.copy(
                        title = resolvedTitle,
                        content = trimmedContent,
                        category = category,
                        colorHex = colorHex,
                        isPinned = isPinned,
                        updatedAt = now
                    )
                )
            } else {
                repository.insertNote(
                    NoteEntity(
                        title = resolvedTitle,
                        content = trimmedContent,
                        category = category,
                        colorHex = colorHex,
                        isPinned = isPinned,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            }
            closeEditor()
        }
    }

    fun confirmDeleteNote(note: NoteEntity) {
        _noteToDelete.value = note
    }

    fun cancelDeleteNote() {
        _noteToDelete.value = null
    }

    fun executeDeleteNote() {
        _noteToDelete.value?.let { note ->
            viewModelScope.launch {
                repository.deleteNote(note)
                _noteToDelete.value = null
                if (_editingNote.value?.id == note.id) {
                    closeEditor()
                }
            }
        }
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.togglePin(note)
        }
    }

    fun toggleArchive(note: NoteEntity) {
        viewModelScope.launch {
            repository.toggleArchive(note)
            if (_editingNote.value?.id == note.id) {
                closeEditor()
            }
        }
    }

    fun duplicateNote(note: NoteEntity) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            repository.insertNote(
                note.copy(
                    id = 0,
                    title = "${note.title} (Salinan)",
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun showAboutDialog(show: Boolean) {
        _showAboutDialog.value = show
    }

    fun showStatsDialog(show: Boolean) {
        _showStatsDialog.value = show
    }
}
