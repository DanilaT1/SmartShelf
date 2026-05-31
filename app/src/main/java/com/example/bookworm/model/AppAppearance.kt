package com.example.bookworm.model

data class AppAppearance(
    val backgroundColor: AppColorChoice = AppColorChoice.SYSTEM,
    val fontColor: AppColorChoice = AppColorChoice.SYSTEM,
)

enum class AppColorChoice(val storageKey: String, val label: String) {
    SYSTEM("system", "System default"),
    CREAM("cream", "Cream"),
    MINT("mint", "Mint"),
    LAVENDER("lavender", "Lavender"),
    CHARCOAL("charcoal", "Charcoal"),
    WHITE("white", "White"),
    BLACK("black", "Black"),
    BLUE("blue", "Blue"),
    GREEN("green", "Green"),
    PURPLE("purple", "Purple"),
    BROWN("brown", "Brown"),
    ;

    companion object {
        fun fromStorageKey(key: String?): AppColorChoice = entries.firstOrNull { it.storageKey == key } ?: SYSTEM
    }
}
