package ru.example.gnt.common.utils

class ApiListQueryGenerator private constructor(
    var baseUrl: String,
    var path: String,
    var divider: String = ",",
) {
    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    interface BaseUrl {
        fun baseUrl(baseUrl: String): Path
    }

    interface Path {
        fun path(path: String): Build
    }

    interface Build {
        fun divider(divider: String): Build
        fun build(): ApiListQueryGenerator
    }

    class Builder : Build, BaseUrl, Path {
        var baseUrl: String = ""
        var path: String = ""
        var divider: String = ","
        override fun baseUrl(baseUrl: String): Path = apply {
            this.baseUrl = baseUrl
        }

        override fun path(path: String): Build = apply {
            this.path = path
        }

        override fun divider(divider: String): Build = apply {
            this.divider = divider
        }

        override fun build(): ApiListQueryGenerator =
            ApiListQueryGenerator(
                baseUrl,
                path,
                divider
            )

    }

    /**
     * @param list List of ids will be turned into proper url
     * example listOf(5,6,7)
     * will turn into:
     * https://rickandmortyapi.com/api/**/5,6,7
     */
    fun generate(list: List<String>): String {
        var query = if (!baseUrl.endsWith("/")) "$baseUrl/$path" else baseUrl + path
        if (list.isEmpty()) return query
        query += list[0]
        list.forEach { id ->
            query += ",$id"
        }
        return query
    }

}


