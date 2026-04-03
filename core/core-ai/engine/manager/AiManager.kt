package com.meadow.core.ai.engine.manager

import com.meadow.core.ai.engine.personas.bloom.BloomUseCases
import com.meadow.core.ai.engine.personas.bud.BudUseCases
import com.meadow.core.ai.engine.personas.meadow.MeadowUseCases
import com.meadow.core.ai.engine.personas.petal.PetalUseCases
import com.meadow.core.ai.engine.personas.sprout.SproutUseCases
import com.meadow.core.ai.engine.personas.vine.VineUseCases

class AiManager(
    val bud: BudUseCases,
    val sprout: SproutUseCases,
    val bloom: BloomUseCases,
    val petal: PetalUseCases,
    val vine: VineUseCases,
    val meadow: MeadowUseCases
)