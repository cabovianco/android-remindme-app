package com.cabovianco.remindme.domain.repository

import com.cabovianco.remindme.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getAll(): Flow<List<Tag>>
    suspend fun insert(tag: Tag): Result<Long>
    suspend fun delete(tag: Tag): Result<Unit>
}
