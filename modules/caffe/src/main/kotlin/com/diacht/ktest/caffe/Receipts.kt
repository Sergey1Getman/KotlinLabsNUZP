package com.diacht.ktest.caffe

import com.diacht.ktest.Product
import com.diacht.ktest.Receipt
import com.diacht.ktest.WATER
import java.util.concurrent.TimeUnit

object EspressoReceipt : Receipt(
    products = listOf(
        Product(type = COFFEE, count = 7),
        Product(type = WATER, count = 25),
    ),
    time = 5,
    timeUnit = TimeUnit.SECONDS,
    outProductType = ESPRESSO,
    price = 25,
)
