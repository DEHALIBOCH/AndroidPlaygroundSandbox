package kz.dehaliboch.otp_test.concurrency

import java.util.LinkedList
import java.util.concurrent.LinkedBlockingQueue

class ConsumerProducer {

    //    private val list = LinkedList<Int>()
    private val limit = 10
    //    private val lock = Object()

    private val blockingQueue = LinkedBlockingQueue<Int>(limit)

    fun produce() {
        var value = 0

        while (true) {
//            synchronized(lock) {
//                while (list.size == limit) {
//                    lock.wait()
//                }
//                list.add(value++)
//                lock.notify()
//            }
            blockingQueue.put(value++) // Если очередь заполнена заблокирует вызывающий объект
        }
    }

    fun consume() {
        while (true) {
//            synchronized(lock) {
//                while (list.size == 0) {
//                    lock.wait()
//                }
//                list.removeFirst()
//                lock.notify()
//            }
            val value = blockingQueue.take() // Заблокирует если очередь пуста (т.е. нет ни одного элемента

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