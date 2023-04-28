package ru.example.gnt.common.exceptions

import ru.example.gnt.common.model.Resource

open class AppException(
    open val resource: Resource.String? = null,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause) {

    constructor() : this(null, "", null)

    constructor(message: String) : this(null, "", null)
    constructor(cause: Throwable) : this(null, "", null)

}
