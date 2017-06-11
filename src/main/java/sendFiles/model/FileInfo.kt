package sendFiles.model


/**
 * Created by David Olmos on 10/06/2017.
 */
interface FileInfo {
    val name: String
    val size: Long
    val md5: ByteArray

    companion object {
        fun create(name: String, size: Long, md5: ByteArray) = object : FileInfo {
            override val name: String = name
            override val size: Long = size
            override val md5: ByteArray = md5
        }
    }
}