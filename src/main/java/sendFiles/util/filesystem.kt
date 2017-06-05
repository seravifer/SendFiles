package sendFiles.util

import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

fun String.toPath(): Path = Paths.get(this)

fun App.getDownloadPath(): String =
        (config["downloadPath"] as? String) ?:
                System.getProperty("user.dir").also { config["downloadPath"] = it; config.save() }
