package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.repository.TagRepository
import javax.inject.Inject

class InsertTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tag: Tag): Result<Long> =
        tagRepository.insert(tag)
}
