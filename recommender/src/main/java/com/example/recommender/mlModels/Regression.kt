package com.example.recommender.mlModels

import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array

interface Regression {
    fun fit(x: D2Array<Float>, targets: D1Array<Float>)

    fun predict(x: D2Array<Float>): D2Array<Float>
}
