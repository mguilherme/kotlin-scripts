#!/usr/bin/env kotlin
@file:CompilerOptions("-jvm-target", "11")

import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

val sourceFolder = "/Users/some-user/test/uncompressed"
val targetFolder = "/Users/some-user/test/compressed"

fun ZipOutputStream.write(zipEntry: ZipEntry, content: ByteArray) = run {
    putNextEntry(zipEntry)
    write(content)
    closeEntry()
}

fun File.compress(): ByteArrayOutputStream {
    val result = ByteArrayOutputStream()
    ZipOutputStream(result).use {
        val zipEntry = ZipEntry(this.name)
        it.write(zipEntry, this.readBytes())
    }
    return result
}

File(sourceFolder).walk()
    .filter { it.isFile }
    .filter { it.extension.toLowerCase() == "txt" }
    .forEach {
        println("Compressing ${it.name}")
        
        val pathName = Path.of(targetFolder, "${it.nameWithoutExtension}.zip").toString()
        val content = it.compress()
        File(pathName).writeBytes(content.toByteArray())
    }
