import com.diacht.ktest.compose.startTestUi
import com.diacht.ktest.library.BuildConfig
import kotlinx.coroutines.*
import java.net.URL
import kotlin.math.cbrt
import kotlin.math.pow

// Function to retrieve number from server
suspend fun getNumberFromServer(message: String): Int = withContext(Dispatchers.IO) {
    val url = URL("http://diacht.2vsoft.com/api/send-number?message=$message")
    val connection = url.openConnection()
    connection.connect()

    val input = connection.getInputStream()
    val buffer = ByteArray(128)
    val bytesRead = input.read(buffer)

    input.close()
    String(buffer, 0, bytesRead).toInt()
}

// Function to calculate the result based on the server responses
suspend fun serverDataCalculate(strList: List<String>): Double = coroutineScope {
    // Sending elements to server asynchronously
    val deferredResults = strList.map { data ->
        async { getNumberFromServer(data) }
    }

    // Waiting for all responses
    val results = deferredResults.map { it.await() }

    // Calculating the result using the provided formula
    val sumOfSquares = results.map { it.toDouble().pow(2) }.sum()
    println("Intermediate results: ${results.joinToString(", ")}")
    println("Sum of squares: $sumOfSquares")

    return@coroutineScope cbrt(sumOfSquares)
}

// Main function
fun seed(): String = "Sergey1Getman"
fun labNumber(): Int = BuildConfig.LAB_NUMBER

fun main(args: Array<String>) = runBlocking {
    println("Лабораторна робота №${labNumber()} користувача ${seed()}")
    //startTestUi(seed(), labNumber())

    // Example usage of serverDataCalculate
    val strList = listOf("apple", "banana", "cherry", "date", "fig", "grape")
    val result = serverDataCalculate(strList)
    println("Result: $result")
}
