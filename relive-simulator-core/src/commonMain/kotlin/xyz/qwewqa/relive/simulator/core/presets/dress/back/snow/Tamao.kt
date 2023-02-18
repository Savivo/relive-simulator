package xyz.qwewqa.relive.simulator.core.presets.dress.back.snow

import xyz.qwewqa.relive.simulator.core.presets.condition.SnowOnlyCondition
import xyz.qwewqa.relive.simulator.core.presets.condition.SunOnlyCondition
import xyz.qwewqa.relive.simulator.core.presets.dress.generated.dress2010015
import xyz.qwewqa.relive.simulator.core.stage.Act
import xyz.qwewqa.relive.simulator.core.stage.actor.ActType
import xyz.qwewqa.relive.simulator.core.stage.actor.CountableBuff
import xyz.qwewqa.relive.simulator.core.stage.autoskill.new
import xyz.qwewqa.relive.simulator.core.stage.buff.DexterityUpBuff
import xyz.qwewqa.relive.simulator.core.stage.buff.MarkBuff
import xyz.qwewqa.relive.simulator.core.stage.buff.NegativeEffectResistanceUpBuff
import xyz.qwewqa.relive.simulator.core.stage.buff.NormalDefenseDownBuff
import xyz.qwewqa.relive.simulator.core.stage.dress.DressCategory
import xyz.qwewqa.relive.simulator.core.stage.dress.blueprint
import xyz.qwewqa.relive.simulator.core.stage.passive.ActCritical50UnitSkill
import xyz.qwewqa.relive.simulator.core.stage.passive.EnemyBack1ConfusionBuffPassive
import xyz.qwewqa.relive.simulator.core.stage.passive.EnemyBrillianceDrainPassive
import xyz.qwewqa.relive.simulator.core.stage.passive.SelfReviveBuffPassive

val DeathTamao = dress2010015(
    name = "Death Tamao",
    acts = listOf(
        ActType.Act1.blueprint("Ardent Slash") {
            Act {
                targetBack().act {
                    attack(
                        modifier = values1,
                        hitCount = 2,
                    )
                }
            }
        },
        ActType.Act2.blueprint("Soul Rebirth") {
            Act {
                targetBack().act {
                    attack(
                        modifier = values1,
                        hitCount = 2,
                    )
                }
                targetAllyAoe().act {
                    applyBuff(
                        DexterityUpBuff,
                        value = values2,
                        turns = times2,
                    )
                    applyBuff(
                        NegativeEffectResistanceUpBuff,
                        value = values3,
                        turns = times3,
                    )
                }
            }
        },
        ActType.Act3.blueprint("Death's Whisper") {
            Act {
                targetBack().act {
                    attack(
                        modifier = values1,
                        hitCount = 2,
                    )
                }
                targetAoe().act {
                    applyBuff(
                        MarkBuff,
                        turns = times2,
                    )
                    applyBuff(
                        NormalDefenseDownBuff,
                        value = values3,
                        turns = times3,
                    )
                }
            }
        },
        ActType.ClimaxAct.blueprint("Memento Mori") {
            Act {
                focus {
                    targetAoe().act {
                        attack(
                            modifier = values1,
                            hitCount = 3,
                        )
                    }
                }
                targetAllyBack(2).act {
                    applyCountableBuff(
                        CountableBuff.Revive,
                        count = times2,
                    )
                }
            }
        }
    ),
    autoSkills = listOf(
        listOf(
            EnemyBrillianceDrainPassive.new(20),
        ),
        listOf(
            EnemyBack1ConfusionBuffPassive.new(time = 2),
        ),
        listOf(
            SelfReviveBuffPassive.new(50, time = 1),
        ),
    ),
    unitSkill = ActCritical50UnitSkill + (SnowOnlyCondition or SunOnlyCondition),
    categories = setOf(DressCategory.Arcana),
)
