package ru.example.gnt.common.exceptions

import ru.example.gnt.common.model.Resource

class DatabaseException(cause: Throwable? = null, val resource: Resource.String? = null) :
    AppException(resource = resource)
