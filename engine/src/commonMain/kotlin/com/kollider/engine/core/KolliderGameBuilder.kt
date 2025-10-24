package com.kollider.engine.core

import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.input.InputHandler
import com.kollider.engine.ecs.input.InputSystem
import com.kollider.engine.ecs.input.createInputHandler
import com.kollider.engine.ecs.physics.CollisionSystem
import com.kollider.engine.ecs.physics.PhysicsSystem
import com.kollider.engine.ecs.rendering.RenderSystem
import com.kollider.engine.ecs.rendering.Renderer
import com.kollider.engine.ecs.rendering.createCanvas
import com.kollider.engine.ecs.rendering.createRenderer

/**
 * DSL helper used by [KolliderGameBuilder.systems] to register additional systems.
 */
class SystemsBuilder {
    internal val systems = mutableListOf<(GameConfig) -> System>()

    /**
     * Registers a system by providing a lambda that receives the GameConfig.
     *
     * ```kotlin
     * systems {
     *     system { config -> DayNightCycleSystem(config.assets) }
     * }
     * ```
     */
    fun system(systemFactory: (GameConfig) -> System) {
        systems.add(systemFactory)
    }
}

/**
 * DSL helper used by [KolliderGameBuilder.entities] to create initial entities.
 */
class EntitiesBuilder {
    internal val entityRegistrations = mutableListOf<World.(GameConfig) -> Unit>()

    /**
     * Registers an entity creation lambda.
     *
     * ```kotlin
     * entities {
     *     entity { config ->
     *         createEntity().add(Position(config.width / 2f, config.height / 2f))
     *     }
     * }
     * ```
     */
    fun entity(registration: World.(GameConfig) -> Unit) {
        entityRegistrations.add(registration)
    }
}

/**
 * Creates a [World] populated with the default engine systems (input, physics, collision, render).
 *
 * ```kotlin
 * val world = createWorld(inputHandler, renderer, config)
 * ```
 */
fun createWorld(
    inputHandler: InputHandler,
    renderer: Renderer,
    config: GameConfig,
): World {
    val world = World()
    world.addSystem(InputSystem(inputHandler, config.inputRouter))
    world.addSystem(PhysicsSystem())
    world.addSystem(CollisionSystem(config.worldBounds))
    world.addSystem(RenderSystem(renderer))
    return world
}

/**
 * Entry point for configuring and launching a Kollider game instance.
 *
 * ```kotlin
 * createKolliderGame {
 *     title = "Pong"
 * }.start { context -> Pong(context) }
 * ```
 */
fun createKolliderGame(
    configure: GameConfig.() -> Unit,
): KolliderGameBuilder {
    return KolliderGameBuilder(GameConfig().apply(configure))
}


/**
 * Fluent builder that composes the world, systems, and game factory before launching.
 */
class KolliderGameBuilder(
    private val config: GameConfig,
) {
    private val customSystems = mutableListOf<(GameConfig) -> System>()
    private val entityRegistrations = mutableListOf<World.(GameConfig) -> Unit>()

    /**
     * Registers extra systems via a DSL block.
     */
    fun systems(block: SystemsBuilder.() -> Unit): KolliderGameBuilder {
        val builder = SystemsBuilder().apply(block)
        customSystems.addAll(builder.systems)
        return this
    }

    /**
     * Registers entity factories that run immediately after the world is created.
     */
    fun entities(block: EntitiesBuilder.() -> Unit): KolliderGameBuilder {
        val builder = EntitiesBuilder().apply(block)
        entityRegistrations.addAll(builder.entityRegistrations)
        return this
    }

    /**
     * Creates the ECS world, builds a GameContext, instantiates the game via [gameFactory],
     * and starts the GameEngine.
     *
     * @param gameFactory the factory function for creating the game.
     */
    /**
     * Finalises configuration, starts the engine, and instantiates your [Game].
     *
     * ```kotlin
     * createKolliderGame { }
     *     .systems { }
     *     .start { context -> SampleGame(context) }
     * ```
     */
    fun start(gameFactory: (GameContext) -> Game)  {
        val inputHandler = createInputHandler(config)
        val canvas = createCanvas(config)
        val renderer = createRenderer(canvas, inputHandler)
        val world = createWorld(inputHandler, renderer, config)

        // Apply entity registrations, each receiving the config.
        entityRegistrations.forEach { registration -> registration.invoke(world, config) }

        // Add custom systems, passing in the config.
        customSystems.forEach { systemFactory -> world.addSystem(systemFactory(config)) }

        val engine = GameEngine(world, config.scope)
        val context = GameContext(config, world, engine)
        engine.attachContext(context)
        engine.start()

        gameFactory(context)
    }
}
