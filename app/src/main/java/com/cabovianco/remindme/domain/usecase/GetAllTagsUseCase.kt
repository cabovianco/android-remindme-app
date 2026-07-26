package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    operator fun invoke(): Flow<List<Tag>> =
        tagRepository.getAll()
}
