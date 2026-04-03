package com.meadow.core.utils.compare


object CompareUtils {

    fun <T : Comparable<T>> greater(a: T?, b: T?): Boolean =
        (a != null && b != null) && a > b

    fun <T : Comparable<T>> lesser(a: T?, b: T?): Boolean =
        (a != null && b != null) && a < b

    fun <T> isEqual(a: T?, b: T?): Boolean =
        a == b


    fun <T> listEqual(a: List<T>?, b: List<T>?): Boolean =
        a?.size == b?.size && a == b
}