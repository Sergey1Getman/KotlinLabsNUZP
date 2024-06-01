import com.diacht.ktest.Storage
import com.diacht.ktest.compose.startTestUi
import com.diacht.ktest.library.BuildConfig
import com.diacht.ktest.Product
import com.diacht.ktest.ProductType
import java.util.concurrent.TimeUnit

object WATER : ProductType()
object SUGAR : ProductType()
object APPLE : ProductType()
object ORANGE : ProductType()
object CARROT : ProductType()
object TOMATO : ProductType()
object SALT : ProductType()

object ORANGE_JUICE : ProductType()
object APPLE_JUICE : ProductType()
object APPLE_CARROT_JUICE : ProductType()
object TOMATO_CARROT_JUICE : ProductType()
object TOMATO_JUICE : ProductType()
object NONE : ProductType()

data class Product(val type: ProductType, val count: Int)

open class Receipt(
    val products: List<Product>,
    val time: Long,
    val timeUnit: TimeUnit,
    val outProductType: ProductType,
    val price: Int
)

object OrangeJuiceReceipt : Receipt(
    products = listOf(
        Product(ORANGE, 1200),
        Product(WATER, 250),
        Product(SUGAR, 25)
    ),
    time = 8,
    timeUnit = TimeUnit.SECONDS,
    outProductType = ORANGE_JUICE,
    price = 50
)

object AppleJuiceReceipt : Receipt(
    products = listOf(
        Product(APPLE, 1500),
        Product(WATER, 350),
        Product(SUGAR, 35)
    ),
    time = 10,
    timeUnit = TimeUnit.SECONDS,
    outProductType = APPLE_JUICE,
    price = 30
)

object AppleCarrotJuiceReceipt : Receipt(
    products = listOf(
        Product(APPLE, 800),
        Product(CARROT, 700),
        Product(WATER, 340),
        Product(SUGAR, 40)
    ),
    time = 12,
    timeUnit = TimeUnit.SECONDS,
    outProductType = APPLE_CARROT_JUICE,
    price = 38
)

object TomatoCarrotJuiceReceipt : Receipt(
    products = listOf(
        Product(TOMATO, 1000),
        Product(CARROT, 400),
        Product(WATER, 250),
        Product(SALT, 8)
    ),
    time = 11,
    timeUnit = TimeUnit.SECONDS,
    outProductType = TOMATO_CARROT_JUICE,
    price = 41
)

object TomatoJuiceReceipt : Receipt(
    products = listOf(
        Product(TOMATO, 1300),
        Product(WATER, 200),
        Product(SALT, 6)
    ),
    time = 7,
    timeUnit = TimeUnit.SECONDS,
    outProductType = TOMATO_JUICE,
    price = 39
)

interface Storage {
    fun addProduct(product: Product)
    fun checkProductCount(type: ProductType): Int
    fun getProduct(productType: ProductType, count: Int): Product
    fun getLeftovers(): List<Product>
    fun resetSimulation()
}

class StorageImpl : Storage {
    private val storage = mutableMapOf<ProductType, Int>()

    override fun addProduct(product: Product) {
        storage[product.type] = storage.getOrDefault(product.type, 0) + product.count
    }

    override fun checkProductCount(type: ProductType): Int {
        return storage.getOrDefault(type, 0)
    }

    override fun getProduct(productType: ProductType, count: Int): Product {
        val availableCount = storage.getOrDefault(productType, 0)
        if (availableCount < count) {
            throw IllegalStateException("Продукту $productType не вистачає")
        }
        storage[productType] = availableCount - count
        return Product(productType, count)
    }

    override fun getLeftovers(): List<Product> {
        return storage.filter { it.value > 0 }.map { Product(it.key, it.value) }
    }

    override fun resetSimulation() {
        storage.clear()
    }
}

open class Machine(private val storage: Storage) {
    private var currentReciept: Receipt? = null

    fun getLeftovers() = storage.getLeftovers()

    fun setReceipt(receipt: Receipt) {
        currentReciept = receipt
    }

    fun consumeProducts(products: List<Product>) = products.forEach {
        storage.getProduct(it.type, it.count)
    }

    fun executeProcess(): Product {
        val receipt = currentReciept ?: throw IllegalStateException("Receipt isn't set")
        consumeProducts(receipt.products)
        TimeUnit.SECONDS.sleep(receipt.time)
        return Product(receipt.outProductType, 1)
    }
}

class JuicePress(storage: Storage) : Machine(storage) {
    fun makeJuice(receipt: Receipt): Product {
        setReceipt(receipt)
        return executeProcess()
    }
}

abstract class FactoryItf {
    abstract fun resetSimulation()
    abstract fun loadProducts(productsFromSupplier: List<Product>)
    abstract fun order(order: List<Pair<ProductType, Int>>): List<Product>
    abstract fun getLeftovers(): List<Product>
}

class JuiceFactory(val storage: Storage, private val juicePress: JuicePress) : FactoryItf() {
    private val orderStatistics = mutableListOf<Product>()
    private var earnings = 0

    override fun resetSimulation() {
        storage.resetSimulation()
        orderStatistics.clear()
        earnings = 0
    }

    override fun loadProducts(productsFromSupplier: List<Product>) {
        productsFromSupplier.forEach { storage.addProduct(it) }
    }

    override fun order(order: List<Pair<ProductType, Int>>): List<Product> {
        val completedOrders = mutableListOf<Product>()
        for ((type, quantity) in order) {
            val receipt = when (type) {
                ORANGE_JUICE -> OrangeJuiceReceipt
                APPLE_JUICE -> AppleJuiceReceipt
                APPLE_CARROT_JUICE -> AppleCarrotJuiceReceipt
                TOMATO_CARROT_JUICE -> TomatoCarrotJuiceReceipt
                TOMATO_JUICE -> TomatoJuiceReceipt
                else -> continue
            }
            repeat(quantity) {
                val product = juicePress.makeJuice(receipt)
                completedOrders.add(product)
                orderStatistics.add(product)
                earnings += receipt.price
            }
        }
        return completedOrders
    }

    override fun getLeftovers(): List<Product> {
        return storage.getLeftovers()
    }
}

fun getSimulationObject(): FactoryItf {
    val storage = StorageImpl()
    val juicePress = JuicePress(storage)
    return JuiceFactory(storage, juicePress)
}

fun seed(): String = "Sergey1Getman"
fun labNumber(): Int = BuildConfig.LAB_NUMBER

fun main(args: Array<String>) {
    println("Лабораторна робота №${labNumber()} користувача ${seed()}")
   // startTestUi(seed(), labNumber())

    val juiceFactory = getSimulationObject() as JuiceFactory

    juiceFactory.loadProducts(listOf(
        Product(WATER, 10000), Product(SUGAR, 1000), Product(APPLE, 10000),
        Product(ORANGE, 10000), Product(CARROT, 10000), Product(TOMATO, 10000), Product(SALT, 1000)
    ))

    val storage = juiceFactory.storage
    println("Count of WATER: ${storage.checkProductCount(WATER)}")
    println("Count of SUGAR: ${storage.checkProductCount(SUGAR)}")
    println("Count of APPLE: ${storage.checkProductCount(APPLE)}")
    println("Count of ORANGE: ${storage.checkProductCount(ORANGE)}")
    println("Count of CARROT: ${storage.checkProductCount(CARROT)}")
    println("Count of TOMATO: ${storage.checkProductCount(TOMATO)}")
    println("Count of SALT: ${storage.checkProductCount(SALT)}")

    val completedOrders = juiceFactory.order(listOf(
        ORANGE_JUICE to 2,
        APPLE_JUICE to 1,
        TOMATO_CARROT_JUICE to 1
    ))

    completedOrders.forEach { println("Completed order: ${it.type}, quantity: ${it.count}") }

    val leftovers = juiceFactory.getLeftovers()
    println("Leftovers: $leftovers")

    juiceFactory.resetSimulation()
    println("Count of WATER after reset: ${storage.checkProductCount(WATER)}")
}
