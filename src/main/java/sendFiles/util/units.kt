package sendFiles.util

/**
 * Created by David on 04/06/2017.
 */
val Int.K: Int get() = this * 1024
val Int.M: Int get() = K * 1024
val Int.G: Int get() = M * 1024