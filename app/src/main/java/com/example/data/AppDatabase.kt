package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "catatan_lite.db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialNotes(database.noteDao())
                    }
                }
            }
        }

        suspend fun populateInitialNotes(noteDao: NoteDao) {
            val now = System.currentTimeMillis()
            noteDao.insertNote(
                NoteEntity(
                    title = "Selamat Datang di Catatan Lite! ✨",
                    content = "Aplikasi catatan super cepat, ringan (~1MB), dan indah.\n\n" +
                            "Fitur Utama:\n" +
                            "• 📝 Tulis catatan & ide dengan cepat\n" +
                            "• 🎨 Beri warna-warni cantik pada catatan\n" +
                            "• 📌 Pin catatan penting di bagian atas\n" +
                            "• 🏷️ Kategorikan dengan tag (Pribadi, Kerja, Ide, Tugas)\n" +
                            "• 🔍 Pencarian instan dan responsif\n" +
                            "• 📤 Bagikan catatan ke WhatsApp, Email, & medsos\n" +
                            "• 🔒 100% Offline & hemat memori perangkat",
                    category = "Ide",
                    colorHex = "#EDE9FE", // Soft Purple
                    isPinned = true,
                    createdAt = now,
                    updatedAt = now
                )
            )
            noteDao.insertNote(
                NoteEntity(
                    title = "Daftar Belanja Mingguan 🛒",
                    content = "- [ ] Buah Apel & Jeruk Segar\n" +
                            "- [ ] Susu UHT Rendah Lemak\n" +
                            "- [ ] Roti Gandum Utuh\n" +
                            "- [ ] Kopi Arabika\n" +
                            "- [ ] Sayur Bayam & Wortel",
                    category = "Tugas",
                    colorHex = "#FEF3C7", // Soft Amber
                    isPinned = false,
                    createdAt = now - 3600000,
                    updatedAt = now - 3600000
                )
            )
        }
    }
}
