# Kollider Game Engine

## 1. Overview

Kollider is designed to help developers create 2D games across Android, iOS, JavaScript, and JVM platforms.
The modular architecture separates concerns into independent modules such as core game loop, resource management, input, physics, rendering, etc.
The engine supports Test Driven Development (TDD) and manual dependency injection for full control over dependencies.

A developer can start a game as follows:

```kotlin
fun main() {
    createKolliderGame {
        width = 540
        height = 1024
        title = "Pong"
    }.start { context ->
        Pong(context)
    }
}
```

## 2. Package & Module Structure

```
com.kollider
 ├── engine
 │    ├── core            
 │    ├── input           
 │    ├── physics         
 │    ├── rendering       
 │    ├── resources       
 │    ├── animations      
 │    ├── components      
 ├── platform
 │    ├── android         
 │    ├── ios             
 │    ├── js              
 │    ├── jvm             
 └── sample
      └── pong           
```
Each module is self-contained with its own interfaces and implementations. Platform modules override necessary functionality.

## 3. Core Engine Code Examples

Example game configuration and main engine loop:

```kotlin
data class GameConfig(var width: Int = 800, var height: Int = 600, var title: String = "Kollider Game")

abstract class Game(val config: GameConfig) {
    abstract fun update(deltaTime: Float)
    abstract fun render(renderer: Renderer)
}

fun createKolliderGame(configure: GameConfig.() -> Unit): KolliderGameBuilder {
    val config = GameConfig().apply(configure)
    return KolliderGameBuilder(config)
}

class KolliderGameBuilder(private val config: GameConfig) {
    fun start(gameFactory: (GameContext) -> Game) { /*...*/ }
}

class GameEngine(private val game: Game) {
    fun start() { /* Game loop */ }
}
```

## 4. Platform-Specific Implementations

### Android Renderer

```kotlin
class AndroidRenderer(private val canvasProvider: () -> android.graphics.Canvas) : Renderer {
    override fun clear() { canvasProvider().drawColor(android.graphics.Color.BLACK) }
}
```

### JS Renderer

```kotlin
class JsRenderer(private val canvas: HTMLCanvasElement) : Renderer {
    private val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
    override fun clear() { ctx.fillStyle = "#000000"; ctx.fillRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble()) }
}
```

## 5. Pong Game Example

```kotlin
class Pong(context: GameContext) : Game(context.config) {
    override fun update(deltaTime: Float) { /* Ball movement & collision logic */ }
    override fun render(renderer: Renderer) { /* Render paddles and ball */ }
}

fun main() {
    createKolliderGame {
        width = 540
        height = 1024
        title = "Pong"
    }.start { context -> Pong(context) }
}
```

## 6. Build Instructions

Ensure you have the latest version of **IntelliJ IDEA** with Kotlin Multiplatform support. To build and run:

1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Sync Gradle dependencies.
4. Run the sample Pong game.

## 7. License

MIT License.
