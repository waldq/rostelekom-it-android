package dev.waldq.rostelekom.models

import kotlinx.serialization.Serializable

@Serializable
data class PageDto<T>(
    val content: List<T>,
    val page: PageMetadataDto,
)

@Serializable
data class PageMetadataDto(
    val size: Int,
    val number: Int,
    val totalElements: Long,
    val totalPages: Int,
)