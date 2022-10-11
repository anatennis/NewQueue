/**
 * Type parameters:
 * <T> – the type of elements held in this queue
 * We need to implement a queue with following property.
 * If we are to add an object which is already in the queue (using equals semantic) nothing should happen.
 * If there is still no such object in the queue (again, using equals semantic)
 * then the object is added to the end of the queue.
 * Rationale is that we do not want to have several same objects in the queue.
 * This could be especially useful when objects are events to recalculate something
 * and there is no sense to have several such events in the queue.
 * <p>
 * Let's call our queue UniqueEventsQueue. Let it has just two methods like add() and get().
 */
public interface UniqueQueue<T> {
    boolean add(T el);
    T get();
}
