package sendFiles.util

val Int.K: Int get() = this * 1024
val Int.M: Int get() = K * 1024
val Int.G: Int get() = M * 1024