package xyz.qwewqa.relive.simulator.core.stage.loadout

import xyz.qwewqa.relive.simulator.core.stage.accessory.Accessory
import xyz.qwewqa.relive.simulator.core.stage.actor.Actor
import xyz.qwewqa.relive.simulator.core.stage.dress.Dress
import xyz.qwewqa.relive.simulator.core.stage.memoir.Memoir

data class ActorLoadout(
    val name: String,
    val dress: Dress,
    val memoir: Memoir? = null,
    val accessory: Accessory? = null,
    val isSupport: Boolean = false,
) {
  fun create() = Actor(name, dress, memoir, accessory, isSupport)
}
