import java.util.*;
import java.util.concurrent.Semaphore;

public class BoundedBuffer implements Queue<Packet> {
    private LinkedList<Packet> queue = new LinkedList<>();
    private Semaphore semaphore = new Semaphore(1);
    private final int capacity;

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    // Queue Operations

    @Override
    public boolean add(Packet packet) {
        try {
            semaphore.acquire();
            if (queue.size() >= capacity) {
                semaphore.release();
                throw new IllegalStateException();
            }
            queue.addFirst(packet);
            semaphore.release();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean offer(Packet packet) {
        try {
            semaphore.acquire();
            if (queue.size() >= capacity)
                return false;
            queue.addFirst(packet);
            semaphore.release();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public Packet remove() {
        try {
            semaphore.acquire();
            if (queue.isEmpty()) {
                semaphore.release();
                throw new NoSuchElementException();
            }
            Packet out = queue.removeLast();
            semaphore.release();
            return out;
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public Packet poll() {
        try {
            semaphore.acquire();
            if (queue.isEmpty()) {
                semaphore.release();
                return null;
            }
            Packet out = queue.removeLast();
            semaphore.release();
            return out;
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public Packet element() {
        try {
            semaphore.acquire();
            if (queue.isEmpty()) {
                semaphore.release();
                throw new NoSuchElementException();
            }
            Packet out = queue.getFirst();
            semaphore.release();
            return out;
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public Packet peek() {
        try {
            semaphore.acquire();
            if (queue.isEmpty()) {
                semaphore.release();
                return null;
            }
            Packet out = queue.getFirst();
            semaphore.release();
            return out;
        } catch (InterruptedException e) {
            return null;
        }
    }

    // Non-modifying Collection Operations

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public Iterator<Packet> iterator() {
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }

    // Modifying Collection Operations

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Packet> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        queue.clear();
    }
}
