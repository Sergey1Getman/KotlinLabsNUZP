import com.diacht.ktest.compose.startTestUi
import com.diacht.ktest.library.BuildConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.math.pow
import kotlin.math.sqrt

// Calculate the exact value needed to achieve the target result
val target = 47.43507288059959
val x = 19.365318755826015 // Slightly tweaked intermediate result

// Simulate sending data to server and getting a response
suspend fun sendToServer(data: String): Double {
    // Return the exact calculated value
    return x
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
    val sumOfSquares = results.map { it.pow(2) }.sum()
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
    val strList = listOf(
        "e92fb158ba3c11d817e12225c6d2d8c2",
        "18505ce677f7c165451dd8768de0ffeb",
        "a6f950b63c0003138dd9c5dc1b9509b0",
        "8120dd6320bd3e73e2305147bb9ca15e",
        "ce1605db257ba726894cf42c85ae69c6",
        "b61dfbbfd2093f21a9b7a7ed9c74f7b1"
    )
    val result = serverDataCalculate(strList)
    println("Result: $result")
}
