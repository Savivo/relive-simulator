package xyz.qwewqa.relive.simulator.client

import kotlinx.browser.document
import kotlinx.dom.addClass
import kotlinx.dom.clear
import kotlinx.dom.hasClass
import kotlinx.dom.removeClass
import kotlinx.html.dom.create
import kotlinx.html.option
import org.w3c.dom.*
import xyz.qwewqa.relive.simulator.common.DataSimulationOption
import xyz.qwewqa.relive.simulator.common.PlayerLoadoutParameters
import xyz.qwewqa.relive.simulator.common.SimulationOption
import xyz.qwewqa.relive.simulator.common.SongEffectParameter

fun HTMLInputElement.integerInput(default: Int) = IntegerInput(this, default)
fun HTMLInputElement.textInput() = TextInput(this)
fun HTMLSelectElement.singleSelect() = SingleSelect(this)
fun HTMLSelectElement.multipleSelect() = MultipleSelect(this)
fun HTMLDivElement.collapse() = Collapse(this)
fun Element?.integerInput(default: Int) = IntegerInput(this as HTMLInputElement, default)
fun Element?.textInput() = TextInput(this as HTMLInputElement)
fun Element?.singleSelect() = SingleSelect(this as HTMLSelectElement)
fun Element?.multipleSelect() = MultipleSelect(this as HTMLSelectElement)
fun Element?.collapse() = Collapse(this as HTMLDivElement)

val HTMLInputElement.valueOrPlaceholder: String get() = value.ifEmpty { placeholder }

class IntegerInput(val element: HTMLInputElement, val default: Int) {
    var value: Int
        get() = element.valueOrPlaceholder.toIntOrNull() ?: default
        set(value) {
            element.value = value.toString()
        }

    fun clear() {
        element.value = ""
    }
}

class TextInput(val element: HTMLInputElement) {
    var value: String
        get() = element.valueOrPlaceholder
        set(value) {
            element.value = value
        }
}

inline fun <reified T> HTMLCollection.single(): T = asList().filterIsInstance<T>().single()
inline fun <reified T> HTMLCollection.multiple(): List<T> = asList().filterIsInstance<T>()

fun refreshSelectPicker() {
    js("$('.selectpicker').selectpicker('refresh')")
}

class Collapse(val element: HTMLDivElement) {
    var show: Boolean
        get() = element.hasClass("show")
        set(value) {
            if (value) {
                element.addClass("show")
            } else {
                element.removeClass("show")
            }
        }
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
sealed class Select(val element: HTMLSelectElement) {
    fun HTMLSelectElement.getSelected() = selectedOptions.asList().map {
        it.asDynamic().value as String
    }

    fun HTMLSelectElement.setSelected(selected: List<String>) {
        this.options.asList().filterIsInstance<HTMLOptionElement>().forEach {
            it.selected = it.value in selected
        }
    }

    fun HTMLSelectElement.setSelected(selected: String) {
        this.options.asList().filterIsInstance<HTMLOptionElement>().forEach {
            it.selected = it.value == selected
        }
    }

    fun populate(values: Map<String, String>) {
        values.forEach {
            element.add(
                document.create.option {
                    value = it.key
                    +it.value
                } as UnionHTMLOptGroupElementOrHTMLOptionElement
            )
        }
    }

    fun populate(values: Iterable<String>) {
        values.forEach {
            element.add(
                document.create.option {
                    value = it
                    +it
                } as UnionHTMLOptGroupElementOrHTMLOptionElement
            )
        }
    }

    fun populate(mapping: Map<String, Map<String, String>>, locale: String) {
        mapping.forEach { (id, names) ->
            element.add(
                document.create.option {
                    value = id
                    +(names[locale] ?: id)
                } as UnionHTMLOptGroupElementOrHTMLOptionElement
            )
        }
    }

    fun populate(mapping: Map<String, SimulationOption>, locale: String) {
        mapping.forEach { (id, option) ->
            element.add(
                document.create.option {
                    value = id
                    attributes["data-subtext"] = option.description?.get(locale) ?: ""
                    attributes["data-tokens"] = option.tags?.get(locale)?.joinToString(" ") ?: ""
                    +option[locale]
                } as UnionHTMLOptGroupElementOrHTMLOptionElement
            )
        }
    }

    fun clear() {
        element.clear()
    }

    fun localize(mapping: Map<String, Map<String, String>>, locale: String) {
        element.options.asList().filterIsInstance<HTMLOptionElement>().forEach { option ->
            option.run {
                text = mapping[option.value]?.get(locale) ?: option.value
            }
        }
    }

    fun localize(mapping: Map<String, DataSimulationOption<*>>, locale: String) {
        element.options.asList().filterIsInstance<HTMLOptionElement>().forEach { selectOption ->
            val option = mapping[selectOption.value]
            val name = option?.get(locale) ?: selectOption.value
            val description = option?.description?.get(locale)
            selectOption.run {
                if (getAttribute("data-content") != null) {
                    setAttribute(
                        "data-content",
                        "${
                            if (option?.imagePath != null) {
                                "<img style=\"height: 1.65em; margin-top: -0.2em\" src=\"${option.imagePath}\"> "
                            } else {
                                ""
                            }
                        }$name${
                            if (description != null) {
                                "<small class=\"text-muted select-option-subtext\">$description</small>"
                            } else {
                                ""
                            }
                        }"
                    )
                }
                setAttribute("data-tokens", option?.tags?.get(locale)?.joinToString(" ") ?: "")
                text = name
            }
        }
    }

    fun refreshSelectPicker() {
        val jquery = js("$")
        jquery(element).selectpicker("refresh")
    }
}

class SingleSelect(element: HTMLSelectElement) : Select(element) {
    var value: String
        get() = element.getSelected().single()
        set(value) {
            element.setSelected(value)
        }
}

class MultipleSelect(element: HTMLSelectElement) : Select(element) {
    var value: List<String>
        get() = element.getSelected()
        set(value) {
            element.setSelected(value)
        }
}

class ActorOptions(element: Element) {
    val name = TextInput(element.getElementsByClassName("actor-name").single())
    val dress = SingleSelect(element.getElementsByClassName("actor-dress").single())
    val memoir = SingleSelect(element.getElementsByClassName("actor-memoir").single())
    val memoirLevel = IntegerInput(element.getElementsByClassName("actor-memoir-level").single(), 1)
    val memoirUnbind = SingleSelect(element.getElementsByClassName("actor-memoir-unbind").single())
    val unitSkillLevel = IntegerInput(element.getElementsByClassName("actor-unit-skill").single(), 21)
    val level = IntegerInput(element.getElementsByClassName("actor-level").single(), 80)
    val rarity = SingleSelect(element.getElementsByClassName("actor-rarity").single())
    val friendship = IntegerInput(element.getElementsByClassName("actor-friendship").single(), 30)
    val rank = SingleSelect(element.getElementsByClassName("actor-rank").single())
    val rankPanelPattern = SingleSelect(element.getElementsByClassName("actor-rank-panel-pattern").single())
    val remake = SingleSelect(element.getElementsByClassName("actor-remake").single())
    val remakeSkill = SingleSelect(element.getElementsByClassName("actor-remake-skill").single())

    val remakeSkillText = element.getElementsByClassName("actor-remake-skill-text").single() as HTMLParagraphElement


    companion object {
        val rankPanelPatterns = mapOf(
            "All" to listOf(true, true, true, true, true, true, true, true),
            "Left 7" to listOf(true, true, true, true, false, true, true, true),
            "Upper 5" to listOf(true, true, true, true, true, false, false, false),
            "Lower 5" to listOf(true, false, false, false, true, true, true, true),
            "Upper 3" to listOf(true, true, true, false, false, false, false, false),
            "Lower 3" to listOf(true, false, false, false, false, false, true, true),
            "None" to listOf(false, false, false, false, false, false, false, false),
        )
        val rankPanelIds = rankPanelPatterns.keys.associateWith {
            "${it.lowercase().replace(" ", "-")}-rank-panels"
        }
        val inverseRankPanelPatterns = rankPanelPatterns.map { (k, v) -> v to k }.toMap()
    }

    var parameters: PlayerLoadoutParameters
        get() = PlayerLoadoutParameters(
            name.value,
            dress.value,
            memoir.value,
            memoirLevel.value,
            memoirUnbind.value.toInt(),
            unitSkillLevel.value,
            level.value,
            rarity.value.toInt(),
            friendship.value,
            rank.value.toInt(),
            rankPanelPatterns[rankPanelPattern.value]!!,
            remake.value.toInt(),
            remakeSkill.value,
        )
        set(param) {
            name.value = param.name
            dress.value = param.dress
            memoir.value = param.memoir
            memoirLevel.value = param.memoirLevel
            memoirUnbind.value = param.memoirLimitBreak.toString()
            unitSkillLevel.value = param.unitSkillLevel
            level.value = param.level
            rarity.value = param.rarity.toString()
            friendship.value = param.friendship
            rank.value = param.rank.toString()
            rankPanelPattern.value = inverseRankPanelPatterns[param.rankPanelPattern] ?: "Full"
            remake.value = param.remake.toString()
            remakeSkill.value = param.remakeSkill ?: "None"

            remakeSkill.element.asDynamic().disabled = param.remake < 4

            dress.refreshSelectPicker()
            memoir.refreshSelectPicker()
            remakeSkill.refreshSelectPicker()

            remakeSkillText.textContent = remakeSkill.element.selectedOptions.single<HTMLElement>().textContent

            remake.element.setAttribute("data-prev-value", param.remake.toString())
            memoirUnbind.element.setAttribute("data-prev-value", param.memoirLimitBreak.toString())
        }
}

class SongEffect(element: Element) {
    private val type = SingleSelect(element.getElementsByClassName("song-effect-type").single())
    private val value = IntegerInput(element.getElementsByClassName("song-effect-value").single(), 0)
    private val conditions = element.getElementsByClassName("song-effect-condition").multiple<HTMLSelectElement>().map {
        MultipleSelect(it)
    }

    var parameters: SongEffectParameter?
        get() = SongEffectParameter(
            type.value,
            value.value,
            conditions.map { it.value }.filter { it.isNotEmpty() },
        ).takeIf { type.value != "none" }
        set(param) {
            type.value = param?.name ?: "none"
            value.value = param?.value ?: 0
            conditions.zip(param?.conditions ?: emptyList()).forEach { (a, b) ->
                a.value = b
            }
            conditions.takeLast(conditions.size - (param?.conditions?.size ?: 0)).forEach {
                it.value = emptyList()
            }
        }
}
