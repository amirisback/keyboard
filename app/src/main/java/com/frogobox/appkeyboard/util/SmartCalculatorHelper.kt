package com.frogobox.appkeyboard.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Inline math calculation helper for soft keyboard.
 * Detects arithmetic expressions ending with '=' (e.g., "25000*4=") and computes the result.
 */
object SmartCalculatorHelper {

    data class MathResult(
        val expression: String,
        val evaluatedValue: String,
        val chipLabel: String
    )

    // Matches simple math sequences ending in '=' at the end of the text
    private val MATH_REGEX = Regex("""([0-9]+(?:\.[0-9]+)?(?:\s*[\+\-\*\/]\s*[0-9]+(?:\.[0-9]+)?)+)\s*=\s*$""")

    private val decimalFormat = DecimalFormat("#.######", DecimalFormatSymbols(Locale.US))

    /**
     * Evaluates text before cursor to detect if an arithmetic expression ending in '=' is present.
     * Returns [MathResult] if valid, or null otherwise.
     */
    fun evaluateMath(textBeforeCursor: String?): MathResult? {
        if (textBeforeCursor.isNullOrBlank()) return null

        val match = MATH_REGEX.find(textBeforeCursor) ?: return null
        val formula = match.groupValues[1].replace("\\s".toRegex(), "")
        val fullMatch = match.value

        val computed = calculateExpression(formula) ?: return null
        val formattedResult = decimalFormat.format(computed)

        return MathResult(
            expression = fullMatch,
            evaluatedValue = formattedResult,
            chipLabel = "= $formattedResult"
        )
    }

    /**
     * Evaluates standard arithmetic expressions with operator precedence (*, / before +, -).
     */
    fun calculateExpression(formula: String): Double? {
        return try {
            val tokens = tokenize(formula)
            if (tokens.isEmpty()) return null

            // Step 1: Process multiplication and division
            val postMulDiv = mutableListOf<Any>()
            var i = 0
            while (i < tokens.size) {
                val token = tokens[i]
                if (token == '*' || token == '/') {
                    val prev = postMulDiv.removeAt(postMulDiv.size - 1) as Double
                    val next = tokens[i + 1] as Double
                    if (token == '/' && next == 0.0) return null // Division by zero
                    val res = if (token == '*') prev * next else prev / next
                    postMulDiv.add(res)
                    i += 2
                } else {
                    postMulDiv.add(token)
                    i++
                }
            }

            // Step 2: Process addition and subtraction
            var total = postMulDiv[0] as Double
            var j = 1
            while (j < postMulDiv.size) {
                val op = postMulDiv[j] as Char
                val num = postMulDiv[j + 1] as Double
                total = if (op == '+') total + num else total - num
                j += 2
            }

            total
        } catch (_: Exception) {
            null
        }
    }

    private fun tokenize(formula: String): List<Any> {
        val tokens = mutableListOf<Any>()
        var currentNumber = StringBuilder()

        for (ch in formula) {
            if (ch.isDigit() || ch == '.') {
                currentNumber.append(ch)
            } else if (ch in "+-*/") {
                if (currentNumber.isNotEmpty()) {
                    tokens.add(currentNumber.toString().toDouble())
                    currentNumber = StringBuilder()
                }
                tokens.add(ch)
            }
        }
        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber.toString().toDouble())
        }
        return tokens
    }
}
