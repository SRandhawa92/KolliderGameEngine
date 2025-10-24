# Kollider Game Engine

Kollider is a lightweight, Kotlin Multiplatform (KMP) engine for building 2D games that can target JVM, Android, iOS, and JavaScript from a shared codebase. It provides an ECS-driven architecture, scene management, and ready-to-use systems for input, physics, collisions, and rendering so you can concentrate on gameplay.

- ✅ Entity–Component–System core with cached views
- ✅ Coroutine-powered game loop and scene stack
- ✅ Input routing, physics integration, collision detection, and rendering out of the box
- ✅ Kotlin-friendly APIs with multiplatform expect/actual implementations

Looking for in-depth docs, code samples, and feature walkthroughs? Check out the full guide:

- [`docs/ENGINE_GUIDE.md`](docs/ENGINE_GUIDE.md)

## Quick Start

```kotlin
fun main() {
    createKolliderGame {
        title = "Hello Kollider"
        width = 960
        height = 540
    }.start { context ->
        object : Game(context) {}
    }
}
```

## Samples

- `samples/pong` – classic Pong with menus, AI paddles, and collisions.
- `samples/flappybird` – endless runner demonstrating procedural spawning and game states.

## License

MIT License. See [`LICENSE`](LICENSE) for details.
