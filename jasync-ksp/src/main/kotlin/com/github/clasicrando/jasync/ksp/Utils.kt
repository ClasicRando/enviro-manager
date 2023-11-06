package com.github.clasicrando.jasync.ksp

import java.io.OutputStream

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}
