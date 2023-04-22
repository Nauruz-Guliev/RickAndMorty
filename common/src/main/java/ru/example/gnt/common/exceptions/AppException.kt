package ru.example.gnt.common.exceptions

import ru.example.gnt.common.model.Resource

open class AppException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(resource: Resource.String?)
}
