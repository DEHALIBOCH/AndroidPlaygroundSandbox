package kz.dehaliboch.otp_test.concurrency

import java.io.PipedReader
import java.io.PipedWriter

class PipedWriterReader {

    val bufferSize = 1024 * 4
    val reader = PipedReader(bufferSize)
    val writer = PipedWriter()

    init {
        writer.connect(reader)

        writer.write("asdasdasdasd")
        writer.flush()

        var i = 0
        val str = StringBuilder()

        while ((reader.read().also { i = it }) != -1) {
            str.append(i.toChar())
        }

        writer.close()
        reader.close()
    }
}