package com.example.codechecker.algorithm.tokenizer

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Python tokenizer for code analysis
 *
 * Converts Python source code into tokens for similarity analysis
 */
@Singleton
class PythonTokenizer @Inject constructor() {

    data class Token(
        val type: TokenType,
        val value: String,
        val position: Int
    )

    enum class TokenType {
        IDENTIFIER,
        KEYWORD,
        OPERATOR,
        DELIMITER,
        LITERAL,
        COMMENT
    }

    private val keywords = setOf(
        "False", "class", "finally", "is", "return",
        "None", "continue", "for", "lambda", "try",
        "True", "def", "from", "nonlocal", "while",
        "and", "del", "global", "not", "with",
        "as", "elif", "if", "or", "yield",
        "assert", "else", "import", "pass",
        "break", "except", "in", "raise",
        "str", "int", "float", "bool", "list", "dict", "tuple", "set"
    )

    /**
     * Tokenize Python code into tokens
     * Removes comments and normalizes the code for comparison
     */
    fun tokenize(code: String): List<Token> {
        val tokens = mutableListOf<Token>()
        val cleanedCode = removeComments(code)
        var position = 0

        var i = 0
        while (i < cleanedCode.length) {
            val char = cleanedCode[i]

            when {
                // Whitespace
                char.isWhitespace() -> {
                    i++
                }

                // Comments
                char == '#' -> {
                    while (i < cleanedCode.length && cleanedCode[i] != '\n') {
                        i++
                    }
                }

                // String literals
                char == '"' || char == '\'' -> {
                    val (stringToken, endIndex) = readString(cleanedCode, i)
                    tokens.add(Token(TokenType.LITERAL, stringToken, position++))
                    i = endIndex
                }

                // Numbers
                char.isDigit() -> {
                    val (numberToken, endIndex) = readNumber(cleanedCode, i)
                    tokens.add(Token(TokenType.LITERAL, numberToken, position++))
                    i = endIndex
                }

                // Identifiers
                char.isJavaIdentifierStart() -> {
                    val (identifier, endIndex) = readIdentifier(cleanedCode, i)
                    val tokenType = if (keywords.contains(identifier)) {
                        TokenType.KEYWORD
                    } else {
                        TokenType.IDENTIFIER
                    }
                    tokens.add(Token(tokenType, identifier, position++))
                    i = endIndex
                }

                // Operators and delimiters
                else -> {
                    val (operator, endIndex) = readOperator(cleanedCode, i)
                    val tokenType = if (operator.length == 1 && operator in "()[]{}:.,;") {
                        TokenType.DELIMITER
                    } else {
                        TokenType.OPERATOR
                    }
                    tokens.add(Token(tokenType, operator, position++))
                    i = endIndex
                }
            }
        }

        return tokens
    }

    private fun removeComments(code: String): String {
        val lines = code.split("\n")
        val result = StringBuilder()

        for (line in lines) {
            var inSingleQuote = false
            var inDoubleQuote = false
            var inTripleQuote = false
            var i = 0

            while (i < line.length) {
                val char = line[i]
                val nextChar = if (i + 1 < line.length) line[i + 1] else null

                when {
                    !inSingleQuote && !inDoubleQuote && !inTripleQuote -> {
                        // Check for comments
                        if (char == '#') {
                            break
                        }

                        // Check for triple quotes
                        if (char == '"' && nextChar == '"') {
                            val tripleQuoteEnd = line.indexOf("\"\"\"", i + 3)
                            if (tripleQuoteEnd != -1) {
                                result.append(" ".repeat(tripleQuoteEnd - i))
                                i = tripleQuoteEnd + 3
                                continue
                            }
                        }
                        if (char == '\'' && nextChar == '\'') {
                            val tripleQuoteEnd = line.indexOf("'''", i + 3)
                            if (tripleQuoteEnd != -1) {
                                result.append(" ".repeat(tripleQuoteEnd - i))
                                i = tripleQuoteEnd + 3
                                continue
                            }
                        }

                        // Handle quotes
                        if (char == '"') inDoubleQuote = !inDoubleQuote
                        if (char == '\'') inSingleQuote = !inSingleQuote

                        result.append(char)
                        i++
                    }

                    inTripleQuote -> {
                        if (char == '"' && nextChar == '"') {
                            val endTripleQuote = line.indexOf("\"\"\"", i + 3)
                            if (endTripleQuote != -1) {
                                result.append(" ".repeat(endTripleQuote - i))
                                i = endTripleQuote + 3
                                inTripleQuote = false
                            } else {
                                result.append(line.substring(i))
                                break
                            }
                        } else {
                            result.append(char)
                            i++
                        }
                    }

                    inSingleQuote -> {
                        if (char == '\\' && i + 1 < line.length) {
                            result.append("  ")
                            i += 2
                        } else if (char == '\'') {
                            inSingleQuote = false
                            result.append(char)
                            i++
                        } else {
                            result.append(char)
                            i++
                        }
                    }

                    inDoubleQuote -> {
                        if (char == '\\' && i + 1 < line.length) {
                            result.append("  ")
                            i += 2
                        } else if (char == '"') {
                            inDoubleQuote = false
                            result.append(char)
                            i++
                        } else {
                            result.append(char)
                            i++
                        }
                    }
                }
            }
            result.append("\n")
        }

        return result.toString()
    }

    private fun readString(code: String, start: Int): Pair<String, Int> {
        val quote = code[start]
        var i = start + 1
        var escaped = false

        while (i < code.length) {
            val char = code[i]
            if (escaped) {
                escaped = false
            } else if (char == '\\') {
                escaped = true
            } else if (char == quote) {
                i++
                break
            }
            i++
        }

        return Pair("STRING_LITERAL", i)
    }

    private fun readNumber(code: String, start: Int): Pair<String, Int> {
        var i = start
        while (i < code.length && (code[i].isDigit() || code[i] == '.' || code[i] == 'e' || code[i] == 'E' || code[i] == '+' || code[i] == '-')) {
            i++
        }
        return Pair("NUMBER", i)
    }

    private fun readIdentifier(code: String, start: Int): Pair<String, Int> {
        var i = start + 1
        while (i < code.length && code[i].isJavaIdentifierPart()) {
            i++
        }
        return Pair(code.substring(start, i), i)
    }

    private fun readOperator(code: String, start: Int): Pair<String, Int> {
        // Try to match multi-character operators first
        val twoChar = if (start + 1 < code.length) code.substring(start, start + 2) else ""
        val threeChar = if (start + 2 < code.length) code.substring(start, start + 3) else ""

        when {
            threeChar in setOf("**=", "//=", "<<=", ">>=") -> return Pair(threeChar, start + 3)
            twoChar in setOf("**", "//", "==", "!=", ">=", "<=", "<<", ">>", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "->", ":=", "in", "is", "or", "and") -> return Pair(twoChar, start + 2)
            code[start] in setOf('+', '-', '*', '/', '%', '&', '|', '^', '~', '<', '>', '=', '!') -> return Pair(code[start].toString(), start + 1)
            code[start] in setOf('(', ')', '[', ']', '{', '}', ':', '.', ',', ';', '@') -> return Pair(code[start].toString(), start + 1)
            else -> return Pair(code[start].toString(), start + 1)
        }
    }
}
