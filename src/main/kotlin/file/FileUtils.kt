package file

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun convertToTemp(b: ByteArray, prefix: String, suffix: String): File {
        val tempAudio = File.createTempFile(prefix, suffix)
        val bufferedOutput = BufferedOutputStream(FileOutputStream(tempAudio))
        bufferedOutput.write(b)
        return tempAudio
    }
}