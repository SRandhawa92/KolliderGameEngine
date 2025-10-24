package com.kollider.flappybird.systems

import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.engine.ecs.require
import com.kollider.flappybird.FlappyBirdGameState
import com.kollider.flappybird.components.BirdComponent
import com.kollider.flappybird.components.ObstacleComponent
import com.kollider.flappybird.components.ScoreComponent
import com.kollider.flappybird.prefabs.formatScoreText

fun SceneScope.scoreSystem(state: FlappyBirdGameState) {
    addSystem(ScoreSystem(state))
}

private class ScoreSystem(
    private val state: FlappyBirdGameState,
) : System() {
    private lateinit var scoreView: EntityView
    private lateinit var obstacleView: EntityView
    private lateinit var birdView: EntityView

    private val scoredObstacleIds = mutableSetOf<Int>()
    private var wasRunning = true

    override fun onAttach(world: World) {
        scoreView = world.view(ScoreComponent::class, Drawable::class)
        obstacleView = world.view(ObstacleComponent::class, Position::class, Collider::class)
        birdView = world.view(BirdComponent::class, Position::class)
    }

    @Suppress("UNUSED_PARAMETER")
    override fun update(entities: List<Entity>, deltaTime: Float) {
        val running = state.isRunning
        if (!running) {
            if (wasRunning) {
                resetScore()
            }
            wasRunning = false
            return
        }

        if (!wasRunning) {
            resetScore()
            wasRunning = true
        }

        val birdIterator = birdView.iterator()
        if (!birdIterator.hasNext()) return
        val birdX = birdIterator.next().require<Position>().x

        val scoreIterator = scoreView.iterator()
        if (!scoreIterator.hasNext()) return
        val scoreEntity = scoreIterator.next()
        val scoreComponent = scoreEntity.require<ScoreComponent>()

        val activeObstacleIds = mutableSetOf<Int>()
        obstacleView.forEach { obstacle ->
            val obstacleId = obstacle.id
            activeObstacleIds.add(obstacleId)

            if (scoredObstacleIds.contains(obstacleId)) return@forEach

            val position = obstacle.require<Position>()
            val collider = obstacle.require<Collider>()
            val trailingEdge = position.x + collider.width

            if (trailingEdge < birdX) {
                scoredObstacleIds.add(obstacleId)
                scoreComponent.value += 1
                updateScoreText(scoreEntity, scoreComponent.value)
            }
        }

        scoredObstacleIds.retainAll(activeObstacleIds)
    }

    private fun resetScore() {
        scoredObstacleIds.clear()

        val scoreIterator = scoreView.iterator()
        if (!scoreIterator.hasNext()) return

        val scoreEntity = scoreIterator.next()
        val scoreComponent = scoreEntity.require<ScoreComponent>()
        if (scoreComponent.value != 0) {
            scoreComponent.value = 0
        }
        updateScoreText(scoreEntity, scoreComponent.value)
    }

    private fun updateScoreText(scoreEntity: Entity, score: Int) {
        val drawable = scoreEntity.get(Drawable::class)
        if (drawable is Drawable.Text) {
            val text = formatScoreText(score)
            if (drawable.text != text) {
                scoreEntity.add(drawable.copy(text = text))
            }
        }
    }
}
