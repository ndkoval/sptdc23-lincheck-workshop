import java.util.concurrent.atomic.AtomicReference

class SingleConsumerMSQueue<T> {
    @Volatile
    private var head: Node<T>

    private val tail: AtomicReference<Node<T>>

    init {
        val dummy = Node<T>(null)
        head = dummy
        tail = AtomicReference(dummy)
    }

    fun add(item: T) {
        val newNode = Node(item)
        while (true) {
            val curTail = tail.get()
            if (curTail.next.compareAndSet(null, newNode)) {
                tail.compareAndSet(curTail, newNode)
                return
            }
        }
    }

    fun poll(): T? {
        val headNext = head.next.get() ?: return null
        head = headNext
        return headNext.element
    }
}

private class Node<T>(
    var element: T?
) {
    val next = AtomicReference<Node<T>?>(null)
}