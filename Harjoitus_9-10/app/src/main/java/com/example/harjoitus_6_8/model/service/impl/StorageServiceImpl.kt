package com.example.harjoitus_6_8.model.service.impl

import androidx.core.os.trace
import com.example.harjoitus_6_8.model.Record
import com.example.harjoitus_6_8.model.service.AccountService
import com.example.harjoitus_6_8.model.service.StorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
): StorageService {

    override fun sortByDate(): Flow<List<Record>> = callbackFlow {
        val recordsCollection = firestore.collection(RECORD_COLLECTION)
        val listenerRegistration = recordsCollection
            .whereEqualTo(USER_ID_FIELD, auth.currentUserId)
            .orderBy(DATE_FIELD, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val records = snapshot?.documents?.mapNotNull { document ->
                    document.toObject<Record>()
                } ?: emptyList()

                try {
                    trySend(records)
                } catch (e: Exception) {
                    // Handle exception
                }
            }

        awaitClose { listenerRegistration.remove() } // Remove the listener when the flow is closed
    }

    override fun sortByWeight(): Flow<List<Record>> = callbackFlow {
        val recordsCollection = firestore.collection(RECORD_COLLECTION)
        val listenerRegistration = recordsCollection
            .whereEqualTo(USER_ID_FIELD, auth.currentUserId)
            .orderBy(WEIGHT_FIELD, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val records = snapshot?.documents?.mapNotNull { document ->
                    document.toObject<Record>()
                } ?: emptyList()

                try {
                    trySend(records)
                } catch (e: Exception) {
                    // Handle exception
                }
            }

        awaitClose { listenerRegistration.remove() } // Remove the listener when the flow is closed
    }

    override fun sortByName(): Flow<List<Record>> = callbackFlow {
        val recordsCollection = firestore.collection(RECORD_COLLECTION)
        val listenerRegistration = recordsCollection
            .whereEqualTo(USER_ID_FIELD, auth.currentUserId)
            .orderBy(NAME_FIELD, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val records = snapshot?.documents?.mapNotNull { document ->
                    document.toObject<Record>()
                } ?: emptyList()

                try {
                    trySend(records)
                } catch (e: Exception) {
                    // Handle exception
                }
            }

        awaitClose { listenerRegistration.remove() } // Remove the listener when the flow is closed
    }

    override suspend fun getRecord(id: String): Flow<Record?> = callbackFlow {
        val recordsCollection = firestore.collection(RECORD_COLLECTION)
        val listenerRegistration = recordsCollection.document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val record = value?.toObject<Record?>()

                try {
                    trySend(record)
                } catch (e: Exception) {
                    // Handle exception
                }
            }

        awaitClose { listenerRegistration.remove() }
    }



    override suspend fun insert(record: Record): String =
        trace(SAVE_RECORD_TRACE) {
            val updatedRecord = record.copy(userId = auth.currentUserId)
            firestore.collection(RECORD_COLLECTION).add(updatedRecord).await().id
        }

    override suspend fun delete(record: Record) {
        firestore.collection(RECORD_COLLECTION).document(record.id).delete().await()
    }

    override suspend fun update(record: Record): Unit =
        trace(UPDATE_RECORD_TRACE) {
            firestore.collection(RECORD_COLLECTION).document(record.id).set(record).await()
        }

    companion object {
        private const val RECORD_COLLECTION = "records"
        private const val DATE_FIELD = "date"
        private const val WEIGHT_FIELD = "weight"
        private const val NAME_FIELD = "name"
        private const val USER_ID_FIELD = "userId"
        private const val SAVE_RECORD_TRACE = "saveRecord"
        private const val UPDATE_RECORD_TRACE = "updateRecord"
    }
}