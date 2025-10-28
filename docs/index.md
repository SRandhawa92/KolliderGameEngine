# Kollider Game Engine

Kollider is a lightweight, Kotlin Multiplatform (KMP) engine for building 2D games that run on JVM (desktop), Android, iOS, and JavaScript. It wraps a clean Entity–Component–System (ECS) core with opinionated defaults for input, physics, collisions, rendering, asset loading, and scene management, so you can focus on gameplay instead of boilerplate.

[Browse the full API reference generated with Dokka.](api/index.html) ·
[Read the Kollider Cookbook](cookbook.md)

---

## ✨ Features

- **Kotlin Multiplatform ready** – common gameplay logic with platform-specific renderers and input handlers.
- **Entity–Component–System architecture** – composable entities, systems with lifecycle hooks, cached views for performance.
- **Scene stack & game loop** – push/pop/replace scenes, orchestrated by a coroutine-powered `GameEngine`.
- **Out-of-the-box systems** – input routing, physics integration, spatial collision detection, and rendering.
- **Asset pipeline** – asynchronous loading via `AssetManager`, awaitable handles, sprite sheet helpers.
- **Input routing** – shared analog movement and targeted discrete actions through `InputRouter`.
- **Extensible rendering** – platform `Canvas`/`Renderer` pairs with `Drawable` components (sprites, rects, circles, text).
- **Samples included** – Pong and Flappy Bird demonstrate end-to-end usage.

---

## 📦 Repository Structure

```
engine/
 ├─ src/commonMain/…       # Shared gameplay, ECS, systems, assets
 ├─ src/jvmMain/…          # Desktop renderer & input
 ├─ src/androidMain/…      # Android renderer & input
 ├─ src/jsMain/…           # JS renderer & input
 └─ src/iosMain/…          # iOS placeholders
docs/
 ├─ ENGINE_GUIDE.md        # This guide
 └─ api/                   # Dokka API reference (`docs/api/index.html`)
samples/
 ├─ pong/…                 # Classic Pong sample
 └─ flappybird/…           # Endless runner sample
```

---

## 🚀 Getting Started

### Prerequisites

- JDK 17+ (for JVM/Android builds)
- Kotlin 2.1 or later
- Android Studio / IntelliJ IDEA with Kotlin Multiplatform plugin
- (Optional) Xcode, Node.js, or platform toolchains for iOS/JS

### Cloning & building

```bash
git clone https://github.com/<you>/KolliderGameEngine.git
cd KolliderGameEngine
./gradlew :samples:pong:runJvm
```

---

## 🛠️ Creating Your First Game

The quickest way to bootstrap a game is through the `createKolliderGame` DSL. Below is a complete minimal example:

```kotlin
fun main() {
    createKolliderGame {
        title = "Hello Kollider"
        width = 960
        height = 540
    }.entities {
        entity { config ->
            // Create a basic entity with position & sprite
            add(Position(config.width / 2f, config.height / 2f))
            add(Drawable.Rect(width = 64f, height = 64f, color = 0xFF2196F3.toInt()))
        }
    }.start { context ->
        object : Game(context) {}
    }
}
```

### Step-by-step

1. **Configure** – customize `GameConfig` (title, size, assets, coroutines).
2. **Register entities** – use the `entities {}` DSL or populate inside a scene/game class.
3. **Add systems** – default world includes input, physics, collision, and rendering. Register extras via `systems {}`.
4. **Start the engine** – provide a factory that returns your `Game` subclass or scene loader.
5. **Run & iterate** – update your systems/entities, leverage asset manager and router as needed.

---

## 🧠 Core Concepts

### GameConfig & GameContext
- `GameConfig` is mutable and passed through the builder. It exposes `assets`, `inputRouter`, and `worldBounds`.
- `GameContext` bundles the config, world, and engine. It is available inside your `Game` or scenes.

### Entities & Components
- `Entity` is just an ID plus a component map.
- Components are simple data classes extending `Component` (e.g., `Position`, `Velocity`, `Drawable.Rect`).
- Use `entity.require<Foo>()` to safely retrieve a component.

### Systems & World
- `World` manages entities and systems, supports cached `EntityView`s (`world.view(…)`) for fast iteration.
- Systems extend `System` and implement `update`. Override `onAttach`, `resize`, `pause`, `resume`, `dispose` when needed.
- Built-in systems:
  - `InputSystem`
  - `PhysicsSystem`
  - `CollisionSystem`
  - `RenderSystem`

### Scene Management
- Implement `Scene` with `onEnter`, `onUpdate`, and `onExit`.
- `GameEngine` runs the loop, updates current scene, and pushes/pops scenes via a thread-safe queue.
- `SceneScope` tracks entities/systems created during a scene and cleans them up automatically.

### Input Routing
- `InputComponent` stores analog movement, action flags, and per-action listeners.
- `InputRouter` can route actions/movement to specific entity IDs (`routeAction`, `routeMovement`).
- Platform-specific handlers feed `InputDispatcher` events that the `InputSystem` consumes.

### Physics & Collision
- `PhysicsSystem` applies velocities to positions (semi-implicit Euler).
- `CollisionSystem` uses a uniform grid broad phase and populates `Collider.collisions` with hit data.
- `WorldBounds` prevents entities from leaving playable space (boundary collision events).

### Rendering
- `Canvas`/`Renderer` pairs are defined per platform (Swing, Android Canvas, HTML Canvas).
- Use `Drawable.Sprite/Rect/Circle/Text` to visualize entities.
- `RenderSystem` clears the frame, iterates cached view, draws each entity, then calls `present`.
- `SpriteAsset` and `UrlSpriteSheetAsset` support asynchronous image loading and sprite-sheet slicing.

### Asset Loading
- `AssetManager` loads assets off-thread. Access through `GameConfig.assets`.
- `AssetHandle.awaitReady()` suspends until ready; `onReady(scope) { … }` executes a block on completion.
- Useful for textures, sounds, or JSON data across platforms.

---

## 📚 Example: Custom System & Scene

```kotlin
class StarfieldSystem : System() {
    private lateinit var stars: EntityView

    override fun onAttach(world: World) {
        stars = world.view(Position::class, Velocity::class)
    }

    override fun update(entities: List<Entity>, deltaTime: Float) {
        for (star in stars) {
            val pos = star.require<Position>()
            val vel = star.require<Velocity>()
            pos.y = (pos.y + vel.vy * deltaTime).mod(worldBounds.height)
        }
    }
}

class GameplayScene : Scene {
    override fun onEnter(scope: SceneScope) {
        scope.addSystem(StarfieldSystem())
        repeat(100) {
            scope.createEntity {
                add(Position(Random.nextFloat() * scope.config.width, Random.nextFloat() * scope.config.height))
                add(Velocity(0f, 50f))
                add(Drawable.Rect(2f, 2f, 0xFFFFFFFF.toInt()))
            }
        }
    }
}
```

---

## 🌍 Platform Status

| Platform | Renderer | Input | Clock | Notes |
|----------|----------|-------|-------|-------|
| JVM      | Swing    | Keyboard (WASD/Space) | `System.nanoTime()` | Ready for desktop |
| Android  | Canvas   | Touch & gesture       | `System.nanoTime()` | Requires calling `AppContext.set(context)` |
| JavaScript | HTML Canvas | Placeholder | `performance.now()` | Fill in rendering/input for your app |
| iOS      | Placeholder | Placeholder | `clock_gettime` | Extend with Metal/UIKit as needed |

---

## 📁 Samples

- **Pong** – menu, simple AI, collision handling (`samples/pong`).
- **Flappy Bird** – procedural obstacles, game state management (`samples/flappybird`).

Run a sample on JVM:

```bash
./gradlew :samples:pong:runJvm
```

---

## 🧪 Testing

The engine ships with KMP unit tests (see `engine/src/commonTest`). Add your own by targeting `commonTest`, `jvmTest`, etc.

```bash
./gradlew :engine:allTests
```

---

## 🗺️ Roadmap Ideas

- Fill out iOS/JS renderer implementations.
- Add audio subsystem and particle effects.
- Provide a CLI template generator and additional starter scenes.

---

## 📄 License

This project is licensed under the **MIT License**. See `LICENSE` for details.
