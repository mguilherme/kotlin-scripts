#!/usr/bin/env kotlinc -J--add-opens=java.base/java.util=ALL-UNNAMED -script

@file:CompilerOptions("-jvm-target", "11")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.system.measureTimeMillis

val sourceFolder = "/Users/some-user/test/uncompressed"
val targetFolder = "/Users/some-user/test/compressed"
val fileExtension = "txt"

fun ZipOutputStream.write(zipEntry: ZipEntry, content: ByteArray) = run {
    putNextEntry(zipEntry)
    write(content)
    closeEntry()
}

fun File.compress() = ByteArrayOutputStream().apply output@{
    ZipOutputStream(this@output).use {
        val zipEntry = ZipEntry(this@compress.name)
        it.write(zipEntry, this@compress.readBytes())
    }
}

val time = measureTimeMillis {
    runBlocking {
        File(sourceFolder).walk()
            .filter { it.isFile }
            .filter { it.extension.toLowerCase() == fileExtension }
            .forEach {
                launch(Dispatchers.Default) {
                    println("[${Thread.currentThread().name}] Compressing '${it.name}'")

                    val pathName = Path.of(targetFolder, "${it.nameWithoutExtension}.zip").toString()
                    val content = it.compress()
                    File(pathName).writeBytes(content.toByteArray())
                }
            }
    }
}
println("Completed in ${time}ms")
