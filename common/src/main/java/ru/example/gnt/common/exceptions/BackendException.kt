package ru.example.gnt.common.exceptions

import ru.example.gnt.common.model.Resource

class BackendException(
    val code: Int,
    override val cause: Throwable? = null,
    override val resource: Resource.String? = null
) :
    AppException(resource = resource)
