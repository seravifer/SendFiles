package sendFiles.model

import sendFiles.model.network.FileConnection
import sendFiles.util.md5
import tornadofx.*
import java.io.File
import java.util.*

/**
 * Created by David on 08/06/2017.
 */
class FileTransferInfo<T : FileConnection<FileInfo>>(
        file: File,
        size: Long = file.length(),
        md5: ByteArray = file.md5(),
        val handler: T
) : ProgressInfo<File>(file), FileInfo by FileInfo.create(file.name, size, md5) {

    override fun equals(other: Any?): Boolean = when(other) {
        is FileInfo -> name == other.name && size == other.size && Arrays.equals(md5, other.md5)
        else -> false
    }

    enum class FileState {
        READY,
        WAITING,
        ACCEPTED,

        TRANSFERRING,
        COMPLETED,

        FAILED,
        CANCELED,
    }
}