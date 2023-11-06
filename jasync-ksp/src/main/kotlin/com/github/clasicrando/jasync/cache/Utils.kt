package com.github.clasicrando.jasync.cache

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

inline fun <reified T, C : Any> C.getObjectField(
    className: String,
    fieldName: String,
): T? {
    val typeName = T::class.simpleName ?: ""
    val kls =
        this.javaClass
            .classLoader
            .loadClass(className)
            .kotlin
    val obj = kls.objectInstance ?: return null
    val cls = obj::class
    return cls.memberProperties
        .firstOrNull {
            val propertyTypeClass = it.returnType.classifier as? KClass<*>
            it.name == fieldName && propertyTypeClass?.simpleName == typeName
        }
        ?.getter
        ?.call(obj) as? T?
}
