package xyz.qwewqa.relive.simulator.core.presets.dress.front.moon

import xyz.qwewqa.relive.simulator.core.presets.condition.MoonOnlyCondition
import xyz.qwewqa.relive.simulator.core.presets.condition.StarOnlyCondition
import xyz.qwewqa.relive.simulator.core.presets.dress.generated.dress3020015
import xyz.qwewqa.relive.simulator.core.stage.Act
import xyz.qwewqa.relive.simulator.core.stage.actor.ActType
import xyz.qwewqa.relive.simulator.core.stage.autoskill.new
import xyz.qwewqa.relive.simulator.core.stage.buff.*
import xyz.qwewqa.relive.simulator.core.stage.dress.DressCategory
import xyz.qwewqa.relive.simulator.core.stage.dress.blueprint
import xyz.qwewqa.relive.simulator.core.stage.modifier.actPower
import xyz.qwewqa.relive.simulator.core.stage.passive.HPDef75UnitSkill
import xyz.qwewqa.relive.simulator.core.stage.passive.TeamConfusionResistanceUpBuffPassive
import xyz.qwewqa.relive.simulator.core.stage.passive.TeamStopResistanceUpBuffPassive
import xyz.qwewqa.relive.simulator.core.stage.passive.TeamStunResistanceUpBuffPassive

val FaithMisora = dress3020015(
    name = "Faith Misora",
    acts = listOf(
        ActType.Act1.blueprint("Strong Thrust") {
            Act {
                targetFront().act {
                    attack(
                        modifier = values1,
                        hitCount = 1,
                    )
                }
            }
        },
        ActType.Act2.blueprint("Healing Prayer") {
            Act {
                targetFront().act {
                    attack(
                        modifier = values1,
                        hitCount = 2,
                    )
                }
                targetAllyAoe().act {
                    applyBuff(
                        effect = CounterHealBuff,
                        value = values2,
                        turns = times2,
                    )
                }
            }
        },
        ActType.Act3.blueprint("Solid Shield") {
            Act {
                targetAoe().act {
                    applyBuff(
                        effect = AggroBuff,
                        turns = times1,
                    )
                }
                targetSelf().act {
                    applyBuff(
                        effect = NormalBarrierBuff,
                        value = values2,
                        turns = times2,
                    )
                    applyBuff(
                        effect = SpecialBarrierBuff,
                        value = values3,
                        turns = times3,
                    )
                }
            }
        },
        ActType.ClimaxAct.blueprint("Moonlight Sacrament") {
            Act {
                targetByHighest { it.mod { +actPower } }.act {
                    attack(
                        modifier = values1,
                        hitCount = 4,
                    )
                }
                targetAllyAoe().act {
                    applyBuff(
                        effect = AbsorbBuff,
                        value = values2,
                        turns = times2,
                    )
                    applyBuff(
                        effect = DamageReceivedDownBuff,
                        value = values3,
                        turns = times3,
                    )
                }
            }
        }
    ),
    autoSkills = listOf(
        listOf(
            TeamConfusionResistanceUpBuffPassive.new(time = 6),
        ),
        listOf(
            TeamStunResistanceUpBuffPassive.new(time = 6),
        ),
        listOf(
            TeamStopResistanceUpBuffPassive.new(time = 6),
        ),
    ),
    unitSkill = HPDef75UnitSkill + (MoonOnlyCondition or StarOnlyCondition),
    categories = setOf(DressCategory.Arcana, DressCategory.TroupeRevueShop),
)
