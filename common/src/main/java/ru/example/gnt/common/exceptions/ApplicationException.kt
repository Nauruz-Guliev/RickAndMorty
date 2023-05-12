package ru.example.gnt.common.exceptions

import ru.example.gnt.common.model.Resource
import java.io.IOException

sealed class ApplicationException(
    open val resource: Resource.String? = null,
    override val message: String? = null,
    override val cause: Throwable? = null,
) : Exception() {

    class BackendException(
        val code: Int,
        override val cause: Throwable? = null,
        override val resource: Resource.String? = null
    ) :
        ApplicationException(resource = resource)

    class ConnectionException(cause: Throwable? = null, val resource: Resource.String? = null) :
        IOException()

    class DataAccessException(cause: Throwable? = null, override val resource: Resource.String? = null) :
        ApplicationException(resource = resource)

    class  LocalDataException(val data: Any, cause: Throwable? = null, override val resource: Resource.String? = null) :
        ApplicationException(resource = resource)

    class DatabaseException(cause: Throwable? = null, override val resource: Resource.String? = null) :
        ApplicationException(resource = resource)

    class EmptyResultException(cause: Throwable? = null, override val resource: Resource.String? = null) :
        ApplicationException(resource = resource)

    class ParseException(cause: Throwable? = null, override val resource: Resource.String? = null) :
        ApplicationException(resource = resource)

}
