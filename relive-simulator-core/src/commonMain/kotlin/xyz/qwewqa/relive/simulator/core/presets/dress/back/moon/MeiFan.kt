package xyz.qwewqa.relive.simulator.core.presets.dress.back.moon

import xyz.qwewqa.relive.simulator.core.presets.condition.BackOnlyCondition
import xyz.qwewqa.relive.simulator.core.presets.condition.MeiFanOnlyCondition
import xyz.qwewqa.relive.simulator.core.presets.condition.SiegfeldOnlyCondition
import xyz.qwewqa.relive.simulator.core.presets.condition.YachiyoOnlyCondition
import xyz.qwewqa.relive.simulator.core.presets.dress.generated.dress4030014
import xyz.qwewqa.relive.simulator.core.stage.Act
import xyz.qwewqa.relive.simulator.core.stage.actor.ActType
import xyz.qwewqa.relive.simulator.core.stage.autoskill.new
import xyz.qwewqa.relive.simulator.core.stage.buff.*
import xyz.qwewqa.relive.simulator.core.stage.dress.blueprint
import xyz.qwewqa.relive.simulator.core.stage.passive.*

val FuMeiFan = dress4030014(
    name = "Second Lieutenant Fu Mei Fan",
    acts = listOf(
        ActType.Act1.blueprint("Gale Dance") {
            Act {
                targetAllyAoe().act {
                    dispelTimed(BuffCategory.Negative)
                    applyBuff(
                        effect = NegativeEffectResistanceBuff,
                        value = values2,
                        turns = times2,
                    )
                }
                targetAoe().act {
                    attack(
                        modifier = values3,
                        hitCount = 2,
                    )
                }
            }
        },
        ActType.Act2.blueprint("Forced Breakthrough") {
            Act {
                targetBack().act {
                    attack(
                        modifier = values1,
                        hitCount = 2,
                    )
                }
                targetAllyAoe().act {
                    applyBuff(
                        effect = ApDownBuff,
                        turns = times2,
                    )
                    applyBuff(
                        effect = EffectiveDamageDealtUpBuff,
                        value = values3,
                        turns = times3,
                    )
                }
            }
        },
        ActType.Act3.blueprint("Limitless Radiance") {
            Act {
                targetBack().act {
                    attack(
                        modifier = values1,
                        hitCount = 2,
                    )
                }
                targetAllyAoe().act {
                    applyBuff(
                        effect = ActPowerUpBuff,
                        value = values2,
                        turns = times2,
                    )
                    applyBuff(
                        effect = DexterityUpBuff,
                        value = values3,
                        turns = times3,
                    )
                }
            }
        },
        ActType.ClimaxAct.blueprint("Oni's Fist") {
            Act {
                targetAoe().act {
                    dispelTimed(BuffCategory.Positive)
                }
                targetAnyRandom(10).act {
                    attack(
                        modifier = values2,
                        hitCount = 10,
                    )
                    applyBuff(
                        effect = StopBuff,
                        turns = 1,
                        chance = 33,
                    )
                }
            }
        }
    ),
    autoSkills = listOf(
        listOf(
            TeamDexterityUpBuffPassive.new(20, 3),
            TeamCriticalUpBuffPassive.new(20, 3),
        ),
        listOf(
            TeamHpUpPassive.new(100) + SiegfeldOnlyCondition,
        ),
        listOf(
            TeamBrillianceRecoveryPassive.new(20) + MeiFanOnlyCondition,
            TeamBrillianceRecoveryPassive.new(20) + YachiyoOnlyCondition,
        ),
    ),
    unitSkill = ActCritical30UnitSkill + BackOnlyCondition,
)
