package edison.util

object IntBytes {
  implicit class IntBytes(size: Int) {
    val KB = size * 1024 // KiB actually
    val MB = size * 1024 * 1024
  }
}
