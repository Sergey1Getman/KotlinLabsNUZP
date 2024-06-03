import com.diacht.ktest.compose.startTestUi
import com.diacht.ktest.library.BuildConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.math.pow
import kotlin.math.sqrt

// Simulate sending data to server and getting a response
suspend fun sendToServer(data: String): Int {
    // For demonstration, we will convert the string to its length as the server's response
    return data.length
}

// Function to calculate the result based on the server responses
suspend fun serverDataCalculate(strList: List<String>): Double = coroutineScope {
    // Sending elements to server asynchronously
    val deferredResults = strList.map { data ->
        async { sendToServer(data) }
    }

    // Waiting for all responses
    val results = deferredResults.map { it.await() }

    // Calculating the result using the provided formula
    val sumOfSquares = results.map { it.toDouble().pow(2) }.sum()
    println("Intermediate results: ${results.joinToString(", ")}")
    println("Sum of squares: $sumOfSquares")

    return@coroutineScope sqrt(sumOfSquares)
}

// Main function
fun seed(): String = "Sergey1Getman"
fun labNumber(): Int = BuildConfig.LAB_NUMBER

fun main(args: Array<String>) = runBlocking {
    println("Лабораторна робота №${labNumber()} користувача ${seed()}")
    //startTestUi(seed(), labNumber())

    // Example usage of serverDataCalculate
    val strList = List(10) { index -> "testString$index" }
    val result = serverDataCalculate(strList)
    println("Result: $result")
}
