package xyz.qwewqa.relive.simulator.core.stage.actor

import xyz.qwewqa.relive.simulator.core.stage.modifier.*

data class StatData(
    val hp: Int = 0,
    val actPower: Int = 0,
    val normalDefense: Int = 0,
    val specialDefense: Int = 0,
    val agility: Int = 0,
    val dexterity: Int = 0,
    val critical: Int = 0,
    val accuracy: Int = 0,
    val evasion: Int = 0,
) {
    fun growthPart() = StatData(
        hp = hp,
        actPower = actPower,
        normalDefense = normalDefense,
        specialDefense = specialDefense,
        agility = agility,
    )

    fun fixedPart() = StatData(
        dexterity = dexterity,
        critical = critical,
        accuracy = accuracy,
        evasion = evasion
    )

    fun display() = """{
        |  hp: $hp,
        |  actPower: $actPower,
        |  normalDefense: $normalDefense,
        |  specialDefense: $specialDefense,
        |  agility: $agility,
        |  dexterity: $dexterity,
        |  critical: $critical,
        |  accuracy: $accuracy,
        |  evasion: $evasion,
        |}""".trimMargin()

    fun displayGrowth() = """{
        |  hp: $hp,
        |  actPower: $actPower,
        |  normalDefense: $normalDefense,
        |  specialDefense: $specialDefense,
        |  agility: $agility,
        |}""".trimMargin()

    fun addToActor(actor: Actor) {
        actor.mod {
            baseMaxHp += this@StatData.hp
            baseActPower += this@StatData.actPower
            baseNormalDefense += this@StatData.normalDefense
            baseSpecialDefense += this@StatData.specialDefense
            baseAgility += this@StatData.agility
            baseDexterity += this@StatData.dexterity
            baseCritical += this@StatData.critical
            baseAccuracy += this@StatData.accuracy
            baseEvasion += this@StatData.evasion
        }
    }

    operator fun plus(other: StatData) = StatData(
        hp + other.hp,
        actPower + other.actPower,
        normalDefense + other.normalDefense,
        specialDefense + other.specialDefense,
        agility + other.agility,
        dexterity + other.dexterity,
        critical + other.critical,
        accuracy + other.accuracy,
        evasion + other.evasion,
    )

    operator fun times(value: Int) = StatData(
        hp * value,
        actPower * value,
        normalDefense * value,
        specialDefense * value,
        agility * value,
        dexterity * value,
        critical * value,
        accuracy * value,
        evasion * value,
    )

    operator fun div(value: Int) = StatData(
        hp / value,
        actPower / value,
        normalDefense / value,
        specialDefense / value,
        agility / value,
        dexterity / value,
        critical / value,
        accuracy / value,
        evasion / value,
    )
}

val defaultDressStats
    get() = StatData(
        dexterity = 5,
        critical = 50,
    )
