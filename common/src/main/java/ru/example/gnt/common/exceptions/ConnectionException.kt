package ru.example.gnt.common.exceptions

import ru.example.gnt.common.model.Resource
import java.io.IOException

class ConnectionException(cause: Throwable? = null, val resource: Resource.String? = null) :
    IOException()
