package fastify_decorators.plugin.extensions

inline fun <reified R> Array<*>.findInstance(): R? {
    return this.find { R::class.isInstance(it) } as R?
}