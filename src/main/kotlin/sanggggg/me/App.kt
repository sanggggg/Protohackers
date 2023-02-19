package sanggggg.me

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main() {
    runBlocking {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager)
            .tcp()
            .bind(port = System.getenv()["PORT"]?.toIntOrNull() ?: 5000)
        while (true) {
            val socket = serverSocket.accept()
            launch {
                val receiveChannel = socket.openReadChannel()
                val sendChannel = socket.openWriteChannel(autoFlush = true)
                try {
                    val body = receiveChannel.readUTF8Line()!!
                    sendChannel.writeStringUtf8(body)
                } finally {
                    withContext(Dispatchers.IO) {
                        sendChannel.close()
                    }
                }
            }
        }
    }
}