package ru.example.gnt.common.utils

import android.webkit.URLUtil

object UrlIdExtractor {

    private val URL_REGEX = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})\n".toRegex()

    /** @param url url от апишки Rick And Morty, которая содержит id в виде числа.
     * Если применены параметры с числами, то вернется первое число с url.
     * Пример:
     *
     * url: https://rickandmortyapi.com/api/location/123?value=31&value=515.html
     * return: id = 123
     *
     */
    fun extract(url: String): Int? {
        if (url.matches(URL_REGEX)) throw IllegalArgumentException("Given url is not valid!")
        val regex = "[0-9]+".toRegex()
        var result = regex.find(url)?.value
        if (result != null) {
            if (result.startsWith("0") && result.length > 1) {
                result = result.replaceFirst("0", "")
            }
            return result.toInt()
        }
        return null
    }

}
