package com.softcat.foody.screens

fun sortBubble(a: MutableList<Int>) {
    val n = a.size
    for (i in 0 until n - 1) {
        for (j in 0 until n - i - 1) {
            if (a[j] < a[j + 1]) {
                val tmp = a[j]
                a[j] = a[j + 1]
                a[j + 1] = tmp
            }
        }
    }
}