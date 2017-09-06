package com.dokufitness.finnvonholten.doku_fitness

data class FitnessElement(var tool: String = "", var duration: String = "00:00:00", var sets: String = "0", var reps: List<Int> = listOf(0), var rating: Double = 0.0, var timestamp: String?)
