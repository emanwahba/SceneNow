package com.emanwahba.scenenow.core.domain.model

enum class SortField { POPULARITY, TITLE, RELEASE_DATE }

enum class SortDirection { ASCENDING, DESCENDING }

data class SortOption(
    val field: SortField = SortField.POPULARITY,
    val direction: SortDirection = SortDirection.DESCENDING,
)
