package kz.dehaliboch.otp_test.concurrency

import java.util.LinkedList

class ConsumerProducer {

    private val list = LinkedList<Int>()
    private val limit = 10
    private val lock = Object()

    fun produce() {
        var value = 0

        while (true) {
            synchronized(lock) {
                while (list.size == limit) {
                    lock.wait()
                }
                list.add(value++)
                lock.notify()
            }
        }
    }

    fun consume() {
        while (true) {
            synchronized(lock) {
                while (list.size == 0) {
                    lock.wait()
                }
                list.removeFirst()
                lock.notify()
            }
        }
    }
}

private class Test {

    val consumerProducer = ConsumerProducer()

    val t1 = Thread {
        try {
            consumerProducer.produce()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }.apply { start() }

    val t2 = Thread {
        try {
            consumerProducer.consume()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }.apply { start() }

}