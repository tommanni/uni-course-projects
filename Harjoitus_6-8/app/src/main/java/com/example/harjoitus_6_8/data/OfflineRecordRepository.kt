package com.example.harjoitus_6_8.data

import kotlinx.coroutines.flow.Flow

class OfflineRecordRepository(private val recordDao: RecordDao) : RecordRepository {
    override fun getAllRecords(): Flow<List<Record>> = recordDao.getAllRecords()

    override fun sortByDate(): Flow<List<Record>> = recordDao.sortByDate()

    override fun sortByWeight(): Flow<List<Record>> = recordDao.sortByWeight()

    override fun sortByName(): Flow<List<Record>> = recordDao.sortByName()

    override fun sortByMuscleGroup(): Flow<List<Record>> = recordDao.sortByMuscleGroup()

    override fun getRecord(id: Int): Flow<Record> = recordDao.getRecord(id)

    override fun getRecordHistory(name: String): Flow<List<Record>> = recordDao.getRecordHistory(name)

    override suspend fun insert(record: Record) = recordDao.insert(record)

    override suspend fun delete(record: Record) = recordDao.delete(record)

    override suspend fun update(record: Record) = recordDao.update(record)

}