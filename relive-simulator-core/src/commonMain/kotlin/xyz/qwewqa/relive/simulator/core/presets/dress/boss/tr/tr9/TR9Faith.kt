package xyz.qwewqa.relive.simulator.core.presets.dress.boss.tr.tr9

import xyz.qwewqa.relive.simulator.core.presets.dress.back.snow.DeathTamao
import xyz.qwewqa.relive.simulator.core.presets.dress.boss.tr.trEventBonusPassive
import xyz.qwewqa.relive.simulator.core.stage.actor.*
import xyz.qwewqa.relive.simulator.core.stage.autoskill.new
import xyz.qwewqa.relive.simulator.core.stage.buff.*
import xyz.qwewqa.relive.simulator.core.stage.dress.Dress
import xyz.qwewqa.relive.simulator.core.stage.loadout.ActorLoadout
import xyz.qwewqa.relive.simulator.core.stage.passive.AbnormalResistPassiveA
import xyz.qwewqa.relive.simulator.core.stage.passive.BossElementResistPassive
import xyz.qwewqa.relive.simulator.core.stage.strategy.FixedStrategy
import xyz.qwewqa.relive.simulator.stage.character.Character
import xyz.qwewqa.relive.simulator.stage.character.DamageType
import xyz.qwewqa.relive.simulator.stage.character.Position

val tr9FaithMisora =
    ActorLoadout(
        "TR9 Faith Misora",
        Dress(
            name = "Faith Misora",
            character = Character.Misora,
            attribute = Attribute.Flower,
            damageType = DamageType.Special,
            position = Position.None,
            stats =
                defaultDressStats.copy(
                    hp = 5_000_000,
                    actPower = 1900,
                    normalDefense = 650,
                    specialDefense = 650,
                    agility = 1,
                ),
            acts =
                actsOf(
                    ActType.Act1("Violent Thrust", 2) {
                      targetBack().act {
                        attack(
                            modifier = 100,
                            hitCount = 1,
                        )
                      }
                    },
                    ActType.Act2("Violent Dual Thrust", 2) {
                      targetBack(2).act {
                        attack(
                            modifier = 120,
                            hitCount = 2,
                        )
                      }
                    },
                    ActType.Act3("Violent Triple Thrust", 2) {
                      targetBack(3).act {
                        attack(
                            modifier = 100,
                            hitCount = 3,
                        )
                      }
                    },
                    ActType.Act4("Counter Concerto", 2) {
                      targetAoe().act {
                        attack(
                            modifier = 120,
                            hitCount = 2,
                        )
                      }
                    },
                    ActType.Act5("Pursuit Concerto", 2) {
                      targetAoe().act {
                        attack(
                            modifier = 150,
                            hitCount = 4,
                        )
                      }
                    },
                    ActType.Act6("Dim Concerto", 2) {
                      targetAoe().act {
                        attack(
                            modifier = 120,
                            hitCount = 3,
                        )
                        applyTimedBuff(
                            effect = BrillianceGainDownBuff,
                            value = 80,
                            turns = 2,
                        )
                      }
                    },
                    ActType.Act7("Inspiring Gust", 2) {
                      targetSelf().act {
                        applyTimedBuff(
                            effect = ActPowerUpBuff,
                            value = 50,
                            turns = 3,
                        )
                        applyTimedBuff(
                            effect = DexterityUpBuff,
                            value = 50,
                            turns = 3,
                        )
                        applyTimedBuff(
                            effect = CriticalUpBuff,
                            value = 50,
                            turns = 3,
                        )
                      }
                    },
                    ActType.Act8("Drowsy Concerto", 2) {
                      targetAoe().act {
                        attack(
                            modifier = 150,
                            hitCount = 4,
                        )
                        applyTimedBuff(
                            effect = SleepBuff,
                            turns = 2,
                            chance = 80,
                        )
                      }
                    },
                    ActType.ClimaxAct("Moonlight Sacrament NEO", 2) {
                      targetAoe().act {
                        attack(
                            modifier = 200,
                            hitCount = 4,
                        )
                        applyTimedBuff(
                            effect = SleepBuff,
                            turns = 2,
                            chance = 80,
                        )
                      }
                    },
                    ActType.ConfusionAct("Thrust", 2) {
                      targetAllyRandom().act {
                        attack(
                            modifier = 105,
                            hitCount = 1,
                        )
                      }
                    },
                ),
            autoSkills =
                listOf(
                    trEventBonusPassive(DeathTamao),
                    AbnormalResistPassiveA.new(100),
                    BossElementResistPassive.new(50),
                ),
        ),
    )

val tr9FaithMisoraStrategy = FixedStrategy {
  val boss = this.team.actors.values.first()

  when (turn) {
    1 -> {
      +boss[ActType.Act1]
      +boss[ActType.Act2]
      +boss[ActType.Act6]
    }
    2 -> {
      +boss[ActType.Act7]
      +boss[ActType.Act3]
      +boss[ActType.Act4]
    }
    3 -> {
      +boss[ActType.ClimaxAct]
      +boss[ActType.Act2]
      +boss[ActType.Act3]
    }
  }
}
