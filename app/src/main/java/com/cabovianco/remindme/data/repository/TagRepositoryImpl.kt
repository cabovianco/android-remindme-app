package com.cabovianco.remindme.data.repository

import android.util.Log
import com.cabovianco.remindme.data.local.dao.TagDao
import com.cabovianco.remindme.data.local.entity.toDomain
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.model.toEntity
import com.cabovianco.remindme.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "TagRepository"

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {
    override fun getAll(): Flow<List<Tag>> =
        tagDao.getAll().map { it.map { entity -> entity.toDomain() } }
            .catch { ex ->
                Log.e(TAG, "TagRepository::getAll", ex)
                throw ex
            }

    override suspend fun insert(tag: Tag): Result<Long> = try {
        val id = tagDao.insert(tag.toEntity())
        Result.success(id)

    } catch (ex: Exception) {
        Log.e(TAG, "TagRepository::insert", ex)
        Result.failure(ex)
    }

    override suspend fun delete(tag: Tag): Result<Unit> = try {
        tagDao.delete(tag.toEntity())
        Result.success(Unit)

    } catch (ex: Exception) {
        Log.e(TAG, "TagRepository::delete", ex)
        Result.failure(ex)
    }
}
