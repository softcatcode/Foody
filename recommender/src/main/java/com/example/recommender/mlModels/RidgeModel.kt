package com.example.recommender.mlModels

import org.jetbrains.kotlinx.multik.api.*
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.abs

class RidgeModel(private val alpha: Float = 1.0f): Regression {

    // Вектор весов модели (W) размера (M x 1)
    private var weights: D2Array<Float>? = null

    // IDEF уровня 3, блок 2.2.3
    // 1. Обучение модели: W = (X^T * X + alpha * I)^(-1) * X^T * y
    override fun fit(x: D2Array<Float>, targets: D1Array<Float>) {
        val y = targets.reshape(targets.size, 1)
        val m = x.shape[1] // Количество признаков

        // Вычисляем X^T (транспонированная матрица признаков размера M x N)
        val xt = transpose(x)

        // Вычисляем XtX = X^T * X (размер M x M)
        val xtx = dot(xt, x)

        // Добавляем регуляризацию alpha * I к диагонали матрицы XtX
        for (i in 0 until m) {
            xtx[i, i] += alpha
        }

        // Вычисляем Xty = X^T * y (размер M x 1)
        val xty = dot(xt, y)

        // Находим обратную матрицу (XtX + alpha * I)^(-1)
        val invXtX = invertMatrix(xtx)

        // Итоговые веса: W = inv(XtX + alpha * I) * Xty
        this.weights = dot(invXtX, xty)
    }

    // 2. Предсказание: y = X_new * W
    override fun predict(x: D2Array<Float>): D2Array<Float> {
        val currentWeights = weights ?: throw IllegalStateException("Модель еще не обучена. Сначала вызовите fit().")
        return dot(x, currentWeights)
    }

    // --- Вспомогательные математические операции на Multik ---

    // Транспонирование матрицы
    private fun transpose(matrix: D2Array<Float>): D2Array<Float> {
        val rows = matrix.shape[0]
        val cols = matrix.shape[1]
        val result = mk.zeros<Float>(cols, rows)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[j, i] = matrix[i, j]
            }
        }
        return result
    }

    // Матричное умножение (Dot Product)
    private fun dot(a: D2Array<Float>, b: D2Array<Float>): D2Array<Float> {
        val rowsA = a.shape[0]
        val colsA = a.shape[1]
        val colsB = b.shape[1]
        val result = mk.zeros<Float>(rowsA, colsB)

        for (i in 0 until rowsA) {
            val rowA = b[i] // Извлекаем D1Array строку
            for (j in 0 until colsB) {
                // Извлекаем j-й столбец матрицы B вручную в D1Array
                val colB = mk.zeros<Float>(colsA)
                for (k in 0 until colsA) {
                    colB[k] = b[k, j]
                }
                // Скалярное произведение векторов одной операцией Multik
                result[i, j] = (rowA * colB).sum()
            }
        }
        return result
    }

    // Инвертирование квадратной матрицы методом Гаусса-Жордана
    private fun invertMatrix(matrix: D2Array<Float>): D2Array<Float> {
        val n = matrix.shape[0]

        // Создаем расширенную матрицу [A | I] размера N x 2N
        val augmented = mk.zeros<Float>(n, 2 * n)
        for (i in 0 until n) {
            for (j in 0 until n) {
                augmented[i, j] = matrix[i, j]
            }
            augmented[i, i + n] = 1.0f // Единичная матрица справа
        }

        // Прямой и обратный ход метода Гаусса
        for (i in 0 until n) {
            var pivot = augmented[i, i]
            if (abs(pivot) < 1e-6f) pivot = 1e-6f // Защита от деления на 0

            // Нормализуем строку (векторная операция Multik)
            augmented[i] = augmented[i] / pivot

            for (j in 0 until n) {
                if (i != j) {
                    val factor = augmented[j, i]
                    // Вычитание строк векторизованно
                    augmented[j] = augmented[j] - (augmented[i] * factor)
                }
            }
        }

        // Извлекаем правую половину расширенной матрицы (это и есть ответ)
        val inverse = mk.zeros<Float>(n, n)
        for (i in 0 until n) {
            for (j in 0 until n) {
                inverse[i, j] = augmented[i, j + n]
            }
        }
        return inverse
    }

    companion object {
        fun learn(features: D2Array<Float>, targets: D1Array<Float>): RidgeModel {
            return RidgeModel(11.5f).apply {
                fit(features, targets)
            }
        }
    }
}
