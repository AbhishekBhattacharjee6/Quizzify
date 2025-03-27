package com.example.quizzify.SimpletonObjects

object SaveListQuestionTrack {
    var matrix: MutableList<MutableList<Int>>? = null
    // Dynamic Initialization
    fun initialize(rows: Int, cols: Int, defaultValue: Int = 0) {
        if (rows <= 0 || cols <= 0) {
            println("Error: Rows and columns must be greater than 0")
            return
        }
        matrix = MutableList(rows) { MutableList(cols) { defaultValue } }
    }

    // Check if matrix is initialized
    fun isInitialized(): Boolean {
        return matrix != null
    }

    // Clear the matrix when not needed
    fun clear() {
        matrix = null
    }
}