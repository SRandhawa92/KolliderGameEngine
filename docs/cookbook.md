# Kollider Game Engine Cookbook

The Kollider Cookbook is the hands-on companion to the API reference. Each
recipe shows how to compose the engine’s building blocks into real gameplay
features. Follow the chapters in order if you are new, or dip into the sections
that are most relevant to your game.

---

## Table of contents

1. [Prerequisites & Project Setup](#1-prerequisites--project-setup)
2. [Creating & Switching Scenes](#2-creating--switching-scenes)
3. [Entities, Components & Prefabs](#3-entities-components--prefabs)
4. [Writing Systems & Using Views](#4-writing-systems--using-views)
5. [Rendering Recipes](#5-rendering-recipes)
6. [Handling Input](#6-handling-input)
7. [Physics & Collision](#7-physics--collision)
8. [Asset Loading & Sprite Sheets](#8-asset-loading--sprite-sheets)
9. [Animation Techniques](#9-animation-techniques)
10. [UI & HUD Overlays](#10-ui--hud-overlays)
11. [Persistence & Profiles](#11-persistence--profiles)
12. [Debugging & Testing](#12-debugging--testing)
13. [Packaging & Deployment](#13-packaging--deployment)
14. [Extending the Engine](#14-extending-the-engine)
15. [Appendix: Reference Tables](#appendix-reference-tables)

---

## 1. Prerequisites & Project Setup

### 1.1 Toolchain check-list

- JDK 17 or newer (preferably from Temurin).
- Kotlin 2.1+ with the multiplatform plugin (bundled with Android Studio Giraffe
  and IntelliJ IDEA 2023.3+).
- Platform SDKs as needed: Android SDK, Xcode command line tools, Node.js for
  JS builds.
- Gradle 8.7 (wrapper provided).

### 1.2 Cloning the repository

```bash
git clone https://github.com/<you>/KolliderGameEngine.git
cd KolliderGameEngine
```

### 1.3 Running a sample (JVM)

```bash
./gradlew :samples:pong:runJvm
```

Use this target to confirm your environment works before starting your own
game.

### 1.4 Creating a new game module

1. Copy `samples/pong` as a starting point or create a fresh multiplatform
   module.
2. Add the engine dependency to your Gradle file:

   ```kotlin
   kotlin {
       sourceSets {
           val commonMain by getting {
               dependencies {
                   implementation(project(":engine"))
               }
           }
       }
   }
   ```

3. Implement an entry point similar to `Pong.createGame()` to launch your
   scenes.

---

## 2. Creating & Switching Scenes

Scenes are high-level states managed by `GameEngine`. The pattern is:

1. Subclass `Game` to push the initial scene.
2. Implement `Scene` objects for menus, gameplay, etc.
3. Use `SceneScope` inside `onEnter` to create entities/systems.

### 2.1 Minimal gameplay scene

```kotlin
class MyGameplayScene : Scene {
    override fun onEnter(scope: SceneScope) {
        // Add systems
        scope.addSystem(PlayerControlSystem())

        // Spawn player entity
        scope.createEntity {
            add(Position(120f, 400f))
            add(Velocity())
            add(Drawable.Rect(32f, 32f, 0xFF2196F3.toInt()))
        }
    }

    override fun onUpdate(deltaTime: Float, scope: SceneScope) {
        if (/* game over */ false) {
            scope.engine.replaceScene(GameOverScene())
        }
    }
}
```

### 2.2 Scene transitions

- `engine.pushScene(scene)` – overlays a new scene (pause menu).
- `engine.replaceScene(scene)` – swaps the current scene (restart).
- `engine.popScene()` – returns to the previous scene.

All entities and systems spawned in a scene are tracked by `SceneScope` and
disposed automatically on exit.

---

## 3. Entities, Components & Prefabs

### 3.1 Components

Components extend `Component` and hold data only. Example:

```kotlin
data class HealthComponent(var current: Int = 3, var max: Int = 3) : Component()
```

Attach components via `Entity.add(component)` or reified helpers:

```kotlin
entity.add(HealthComponent())
val health = entity.require<HealthComponent>()
```

Duplicate adds throw an error, protecting you from silent overwrites.

### 3.2 Prefabs

Prefab functions build reusable entity blueprints inside a `SceneScope`:

```kotlin
fun SceneScope.player(config: GameConfig) = createEntity {
    add(Position(config.width / 2f, config.height / 2f))
    add(Velocity())
    add(Collider(width = 32f, height = 32f))
    add(Drawable.Rect(32f, 32f, 0xFF03A9F4.toInt()))
}
```

Use prefabs to keep scene code declarative and maintainable.

### 3.3 Tracking external entities

If you construct entities outside a scene (e.g., shared pools), register them
with `scope.track(entity)` so they’re removed when the scene exits.

---

## 4. Writing Systems & Using Views

Systems encapsulate behavior. A simple gravity system:

```kotlin
class GravitySystem(private val gravity: Float) : System() {
    private lateinit var view: EntityView

    override fun onAttach(world: World) {
        view = world.viewOf<Velocity, Position>()
    }

    override fun update(entities: List<Entity>, deltaTime: Float) {
        view.asSequence().forEach { entity ->
            val velocity = entity.require<Velocity>()
            velocity.vy += gravity * deltaTime
        }
    }
}
```

Key tips:

- Use `world.viewOf<A, B>()` helpers for type-safe lookups.
- Convert an `EntityView` to a Kotlin sequence using `asSequence()` for idiomatic iteration.
- Override `runsWhilePaused` to keep background systems active during pause states.

---

## 5. Rendering Recipes

### 5.1 Drawables 101

- `Drawable.Rect(width, height, color)` – filled rectangle.
- `Drawable.Circle(radius, color)` – filled circle.
- `Drawable.Text(text, size, color)` – simple text with platform font.
- `Drawable.Sprite(spriteAsset, width, height)` – textured quad.
- `Drawable.Composite(elements)` – groups multiple drawables (e.g., Flappy Bird).

Attach a drawable to an entity alongside `Position`. `RenderSystem` reads the
`(Position + Drawable)` view each frame.

### 5.2 Virtual resolution & scaling

`GameConfig.width/height` define the logical canvas. Override
`renderWidthOverride/renderHeightOverride` for high-DPI screens. `RenderSystem`
letterboxes, so keep coordinates in virtual units.

### 5.3 Custom sprites

```kotlin
val playerSprite = UrlSpriteSheetAsset(
    name = "player_idle",
    config = config,
    imageUrl = "https://…/player.png",
    rows = 1,
    cols = 4,
)

playerSprite.handle.onReady(config.scope) {
    entity.add(Drawable.Sprite(playerSprite, width = 48f, height = 48f))
}
```

### 5.4 Lines & polygons

Use `Drawable.Line` for UI accents and `Drawable.Polygon` for custom shapes or
low-poly sprites.

---

## 6. Handling Input

### 6.1 Binding actions

```kotlin
val inputComponent = InputComponent().apply {
    bindAction(Shoot)
}

entity.add(inputComponent)
config.inputRouter.routeAction(Shoot, entity.id)
```

Inside a system, check `inputComponent.shoot` or listen via `registerListener`.

### 6.2 Movement routing

Analog movement is retrieved through `inputComponent.movement`. Route it to the
current entity with `config.inputRouter.routeMovement(entity.id)`.

### 6.3 Platform-specific mappings

- JVM: WASD & Space.
- Android: touch gestures via `AndroidInputHandler`.
- Extend `InputHandler` on other platforms to push events into the common
  `InputDispatcher`.

---

## 7. Physics & Collision

### 7.1 Position + Velocity

`PhysicsSystem` applies velocity each frame. Ensure both components are present.

### 7.2 Colliders

`Collider(width, height)` stores the AABB and a mutable list of `CollisionEvent`s
processed by `CollisionSystem`.

### 7.3 Responding to collisions

Iterate `collider.collisions` and inspect `CollisionType`. After handling, remove
entries so they’re not reprocessed.

### 7.4 World bounds

Configure `GameConfig.width/height` to define the playable area. Collisions with
`BOUNDARY_*` types let you constrain entities.

---

## 8. Asset Loading & Sprite Sheets

### 8.1 Asset manager basics

```kotlin
val textureHandle = config.assets.load("background") {
    UrlSpriteSheetAsset.loadImageFromUrl("https://…/bg.png")
}

textureHandle.onReady(config.scope) { image ->
    // use image (platform type)
}
```

### 8.2 Sprite sheet slicing

`UrlSpriteSheetAsset` automatically computes frames. Use
`spriteAsset.frames?.get(index)` in a custom renderer or animation component.

### 8.3 Disposing assets

Call `config.assets.dispose(name)` when you no longer need an asset, or rely on
scene disposal if it’s scoped.

---

## 9. Animation Techniques

### 9.1 Geometry animation

Mutate drawable parameters each frame, as in the Flappy Bird wing system:

```kotlin
val points = wingPolygon.points
points[1].y = baseY - sin(phase) * amplitude
```

### 9.2 Sprite-sheet animation

Change the frame index based on elapsed time and replace the drawable with a
new `Drawable.Sprite` referencing the current frame.

### 9.3 Tweening helpers

Implement small coroutine-based tweens:

```kotlin
config.scope.launch {
    val duration = 0.5f
    var elapsed = 0f
    while (elapsed < duration) {
        val t = elapsed / duration
        position.y = lerp(startY, targetY, t)
        elapsed += frameDelta()
        yield()
    }
}
```

Provide utilities in a shared module if you find yourself repeating patterns.

---

## 10. UI & HUD Overlays

### 10.1 Framed score panels

Combine `Drawable.Rect` and `Drawable.Text` with offsets to construct panels,
as shown in `flappybird/prefabs/scoreboard.kt`.

### 10.2 Pause or notification overlays

Push a modal scene that renders semi-transparent background + menu options. Use
`SceneScope.engine.popScene()` when the menu closes.

### 10.3 Debug overlays

Spawn entities with `Drawable.Text` showing FPS, entity counts, or collision
states. Toggle them on/off with a debug action.

---

## 11. Persistence & Profiles

`GameContext.storage` exposes a `KeyValueStorage` backed by the current
platform:

```kotlin
class HighScoreRepo(private val storage: KeyValueStorage) {
    suspend fun update(score: Int) {
        val best = storage.getInt("high_score", 0)
        if (score > best) storage.putInt("high_score", score)
    }
}
```

Fetcher example inside a scene:

```kotlin
config.scope.launch {
    val profileName = context.storage.getString("profile", "Guest")
    println("Welcome $profileName")
}
```

Override `GameConfig.storageFactory` to plug in custom storage (e.g., encrypted
files or cloud sync).

---

## 12. Debugging & Testing

### 12.1 Common tests

- Scene disposal: ensure `SceneScope.dispose()` removes systems and entities.
- Pause/resume behaviour: systems with `runsWhilePaused` should keep ticking.
- Rendering scale: record renderer calls to verify letterboxing math.

Example test skeleton:

```kotlin
class GravitySystemTest {
    @Test fun `entities fall`() {
        val world = World()
        val system = GravitySystem(9.81f)
        world.addSystem(system)
        // ...
    }
}
```

### 12.2 Debug instrumentation

- Add logging inside systems (`println` or a logging framework) to watch game
  state.
- Use custom `Drawable.Composite` overlays to visualise hitboxes or navigation
  nodes.

---

## 13. Packaging & Deployment

### 13.1 Desktop (JVM)

```bash
./gradlew :<sample>:packageUberJarForCurrentOS
```

Distribute the resulting fat jar. Consider packaging with JavaFX or LWJGL if you
need richer graphics/audio.

### 13.2 Android

- Add an activity similar to `samples/flappybird/src/androidMain/…/MainActivity.kt`.
- Ensure `AppContext.set(this)` is called before launching the game.
- Build with `./gradlew :<sample>:assembleDebug`.

### 13.3 JavaScript

- Implement a JS renderer/input handler in `engine/src/jsMain` (placeholder
  provided).
- Run `./gradlew :<sample>:browserDistribution` and host the output.

### 13.4 iOS

- Provide Metal/swift UI bindings in `engine/src/iosMain`.
- Use Kotlin/Native or integrate into Swift via shared frameworks.

---

## 14. Extending the Engine

### 14.1 Custom renderer

Implement `Renderer` and `Canvas` for your platform, then register it in
`createRenderer`/`createCanvas` expect/actuals.

### 14.2 Alternate storage backends

Override `GameConfig.storageFactory` to return your implementation.

```kotlin
createKolliderGame {
    storageFactory = { config -> MyEncryptedStorage(config) }
}
```

### 14.3 New drawables

Extend the `Drawable` sealed class with platform support in each renderer. Add
rendering logic to `RenderSystem.renderDrawable` and renderer implementations.

### 14.4 Input systems

Provide a new `InputHandler` implementation that feeds events into
`InputDispatcher`. Register it via `createInputHandler(config)` expect/actual.

---

## Appendix: Reference Tables

### Built-in components

| Component      | Purpose                                   |
|----------------|-------------------------------------------|
| `Position`     | 2D coordinates                            |
| `Velocity`     | Velocity in units/second                  |
| `Collider`     | Axis-aligned bounding box & collision log |
| `Drawable.*`   | Rendering primitives                      |
| `InputComponent` | Stores movement/action states           |
| `SpriteAsset`  | Asynchronous image descriptor             |

### Default systems (auto-added by `createWorld`)

| System            | Responsibility                    |
|-------------------|------------------------------------|
| `InputSystem`     | Propagate platform input           |
| `PhysicsSystem`   | Integrate velocities               |
| `CollisionSystem` | Detect & report collisions         |
| `RenderSystem`    | Draw entities with `Drawable`s     |

### Helpful extensions

| Helper                       | Description                                |
|------------------------------|--------------------------------------------|
| `Entity.require<T>()`        | Throws if component missing (fast fail)     |
| `World.viewOf<A, B>()`       | Type-safe entity view creation              |
| `EntityView.asSequence()`    | Iterate using Kotlin sequence operators     |
| `SceneScope.track(entity)`   | Auto-remove unmanaged entities on scene exit|

---

Happy coding! Share your creations and extensions—feedback helps shape the next
release of the Kollider engine.
