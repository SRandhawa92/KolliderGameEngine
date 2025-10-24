package com.kollider.engine.ecs

inline fun <reified C : Component> Entity.require(): C =
    get(C::class) ?: error("Entity $id is missing ${C::class.simpleName}")
