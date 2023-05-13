package ru.example.gnt.common.utils

import ru.example.gnt.common.utils.extensions.isValidUrl
import javax.inject.Inject

class UrlIdExtractor @Inject constructor() {


    /** @param url url от апишки Rick And Morty, которая содержит id в виде числа.
     * Если применены параметры с числами, то вернется первое число с url.
     * @return id сущности, которая скрывается за ссылкой
     * Пример:
     *
     * url: https://rickandmortyapi.com/api/location/123?value=31&value=515.html
     * return: id = 123
     *
     * P.s можно улучшить регулярку
     */
    fun extract(url: String?): String {
        if(url == null || url.isEmpty()) return ""
        if (!url.isValidUrl()) throw IllegalArgumentException("Given url " + url +  "is not valid!")
        val regex = "[0-9]+".toRegex()
        var result = url.let { regex.find(it)?.value }
        if (result != null) {
            if (result.startsWith("0") && result.length > 1) {
                result = result.replaceFirst("0", "")
            }
            return result
        }
        return ""
    }

}
