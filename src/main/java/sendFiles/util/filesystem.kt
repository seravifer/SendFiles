package sendFiles.util

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by David on 04/06/2017.
 */
fun String.toPath(): Path = Paths.get(this)
