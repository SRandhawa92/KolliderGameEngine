package com.kollider.engine.core

import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.input.InputHandler
import com.kollider.engine.ecs.input.InputSystem
import com.kollider.engine.ecs.input.createInputHandler
import com.kollider.engine.ecs.physics.PhysicsSystem
import com.kollider.engine.ecs.rendering.RenderSystem
import com.kollider.engine.ecs.rendering.Renderer
import com.kollider.engine.ecs.rendering.createCanvas
import com.kollider.engine.ecs.rendering.createRenderer

/**
 * A helper DSL builder for registering custom systems.
 */
class SystemsBuilder {
    internal val systems = mutableListOf<(GameConfig) -> System>()

    /**
     * Registers a system by providing a lambda that receives the GameConfig.
     */
    fun system(systemFactory: (GameConfig) -> System) {
        systems.add(systemFactory)
    }
}

/**
 * A helper DSL builder for registering custom entities.
 */
class EntitiesBuilder {
    internal val entityRegistrations = mutableListOf<World.(GameConfig) -> Unit>()

    /**
     * Registers an entity creation lambda.
     */
    fun entity(registration: World.(GameConfig) -> Unit) {
        entityRegistrations.add(registration)
    }
}

/**
 * Creates a World with default systems.
 *
 * @param inputHandler the input handler to use.
 * @param renderer the renderer to use.
 * @param config the game configuration.
 */
fun createWorld(
    inputHandler: InputHandler,
    renderer: Renderer,
    config: GameConfig,
): World {
    val world = World()
    world.addSystem(InputSystem(inputHandler))
    world.addSystem(PhysicsSystem(config.width, config.height))
    world.addSystem(RenderSystem(renderer))
    return world
}

/**
 * DSL function for creating a Kollider game.
 */
fun createKolliderGame(
    configure: GameConfig.() -> Unit,
): KolliderGameBuilder {
    return KolliderGameBuilder(GameConfig().apply(configure))
}


/**
 * Builder for creating and starting a Kollider game.
 */
class KolliderGameBuilder(
    private val config: GameConfig,
) {
    private val customSystems = mutableListOf<(GameConfig) -> System>()
    private val entityRegistrations = mutableListOf<World.(GameConfig) -> Unit>()

    /**
     * DSL block for registering custom systems.
     */
    fun systems(block: SystemsBuilder.() -> Unit): KolliderGameBuilder {
        val builder = SystemsBuilder().apply(block)
        customSystems.addAll(builder.systems)
        return this
    }

    /**
     * DSL block for registering custom entities.
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
    fun start(gameFactory: (GameContext) -> Game)  {
        val inputHandler = createInputHandler(config)
        val canvas = createCanvas(config)
        val renderer = createRenderer(canvas, inputHandler)
        val world = createWorld(inputHandler, renderer, config)

        // Apply entity registrations, each receiving the config.
        entityRegistrations.forEach { registration -> registration.invoke(world, config) }

        // Add custom systems, passing in the config.
        customSystems.forEach { systemFactory -> world.addSystem(systemFactory(config)) }

        val engine = GameEngine(world, config.scope).apply { start() }
        val context = GameContext(config, world, engine)

        gameFactory(context)
    }
}