package ru.example.gnt.common.exceptions

import ru.example.gnt.common.model.Resource

class ConnectionException(cause: Throwable? = null, override val resource: Resource.String? = null) :
    AppException(resource = resource)
