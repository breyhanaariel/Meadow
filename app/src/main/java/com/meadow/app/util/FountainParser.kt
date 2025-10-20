package com.meadow.app.util

sealed class FountainBlock {
    data class SceneHeading(val text: String): FountainBlock()
    data class Action(val text: String): FountainBlock()
    data class Character(val name: String): FountainBlock()
    data class Parenthetical(val text: String): FountainBlock()
    data class Dialogue(val text: String): FountainBlock()
    data class Transition(val text: String): FountainBlock()
}

fun parseFountain(script: String): List<FountainBlock> {
    val lines = script.lines()
    val out = mutableListOf<FountainBlock>()
    var last: FountainBlock? = null
    for (raw in lines) {
        val line = raw.trim()
        if (line.isEmpty()) continue
        when {
            line.startsWith("INT.") || line.startsWith("EXT.") -> out += FountainBlock.SceneHeading(line)
            line.endsWith("TO:") -> out += FountainBlock.Transition(line)
            line == line.uppercase() && line.any { it.isLetter() } && line.length > 2 -> { out += FountainBlock.Character(line); last = out.last() }
            line.startsWith("(") && line.endsWith(")") -> { out += FountainBlock.Parenthetical(line); last = out.last() }
            last is FountainBlock.Character || last is FountainBlock.Parenthetical -> { out += FountainBlock.Dialogue(line); last = out.last() }
            else -> { out += FountainBlock.Action(line); last = out.last() }
        }
    }
    return out
}
