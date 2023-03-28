package command.cache

import com.google.zxing.MultiFormatReader
import com.google.zxing.MultiFormatWriter

object QRCache {

    var formatReader: MultiFormatReader? = null
    var formatWriter: MultiFormatWriter? = null

    fun init(): Boolean {
        formatReader = MultiFormatReader()
        formatWriter = MultiFormatWriter()
        return true
    }
}