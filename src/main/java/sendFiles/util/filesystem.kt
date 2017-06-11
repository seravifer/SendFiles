package sendFiles.util

import tornadofx.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.security.DigestInputStream
import java.security.MessageDigest

fun File.md5() =
        if (exists()) {
            val digestor = MessageDigest.getInstance("MD5")
            inputStream().use { DigestInputStream(it, digestor); digestor.digest() }
        } else kotlin.error("The file $absolutePath doesn't exist")

fun String.toPath(): Path = Paths.get(this)

fun App.getDownloadPath(): Path =
        ((config["downloadPath"] as? String) ?:
                System.getProperty("user.dir").also { config["downloadPath"] = it; config.save() })
                .toPath()

fun App.setDownloadPatch(path: String) {
    config["downloadPath"] = path
    config.save()
}