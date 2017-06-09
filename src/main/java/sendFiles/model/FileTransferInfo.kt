package sendFiles.model

import sendFiles.model.network.FileConnection
import sendFiles.model.network.NetworkConnection
import sendFiles.util.md5
import tornadofx.*
import java.io.File
import java.util.*

/**
 * Created by David on 08/06/2017.
 */
class FileTransferInfo<T : FileConnection>(file: File, size: Long? = null, md5: ByteArray? = null, val handler: T) : ProgressInfo<File>(file) {

    var size by property(size ?: file.length())
    fun sizeProperty() = getProperty(FileTransferInfo<T>::size)

    var md5 by property(md5 ?: file.md5())
    fun md5Property() = getProperty(FileTransferInfo<T>::md5)

    override fun equals(other: Any?): Boolean = when(other) {
        !is FileTransferInfo<*> -> false
        else -> value.name == other.value.name
                && Arrays.equals(md5, other.md5)
                && value.length() == other.value.length()
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