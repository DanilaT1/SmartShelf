package com.example.bookworm.model

data class LevelInfo(
    val level: Int,
    val name: String,
    val threshold: Int,
    val nextThreshold: Int,
)

object Levels {
    private val names = listOf(
        "Curious Caterpillar",
        "Page Nibbler",
        "Word Collector",
        "Chapter Explorer",
        "Story Weaver",
        "Tome Tamer",
        "Library Sage",
        "BookWorm Legend",
    )

    fun thresholdFor(level: Int): Int = if (level <= 1) 0 else (level - 1) * 100

    fun levelForXp(xp: Int): Int = (xp / 100) + 1

    fun nameFor(level: Int): String = names.getOrElse((level - 1).coerceAtLeast(0)) { names.last() }

    fun infoFor(xp: Int): LevelInfo {
        val level = levelForXp(xp)
        return LevelInfo(
            level = level,
            name = nameFor(level),
            threshold = thresholdFor(level),
            nextThreshold = thresholdFor(level + 1),
        )
    }
}
