package com.example.recommender.mlModels

import org.jetbrains.kotlinx.multik.api.*
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.linalg.inv
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.collections.component1
import kotlin.collections.component2

class RidgeModel(private val alpha: Float = 1.0f): Regression {

    private var weights: D2Array<Float>? = null

    override fun fit(x: D2Array<Float>, targets: D1Array<Float>) {
        val (n, m) = x.shape
        require(n == targets.size) {
            "Learn data is incorrect: features and targets shapes are incompatible."
        }

        val x = x.cat(mk.ones(n, 1), axis = 1)
        val xt = x.transpose()
        val xtx = xt dot x
        val regMatrix = mk.identity<Float>(m + 1) * alpha
        regMatrix[0, 0] = 0f

        val invXtX = mk.linalg.inv(xtx + regMatrix)
        val xty = xt dot targets.reshape(targets.size, 1)
        weights = invXtX dot xty
    }

    override fun predict(x: D2Array<Float>): D2Array<Float> {
        val currentWeights = weights
            ?: throw IllegalStateException("Model is not learned yet: invoke fit firstly.")
        val x = x.cat(mk.ones(x.shape[0], 1), axis = 1)
        return x dot currentWeights
    }

    companion object {
        fun learn(features: D2Array<Float>, targets: D1Array<Float>): RidgeModel {
            return RidgeModel(11.5f).apply {
                fit(features, targets)
            }
        }
    }
}