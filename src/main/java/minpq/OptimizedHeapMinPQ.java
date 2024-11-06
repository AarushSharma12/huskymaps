package minpq;

import java.util.*;

/**
 * Optimized binary heap implementation of the {@link MinPQ} interface.
 *
 * @param <E> the type of elements in this priority queue.
 * @see MinPQ
 */
public class OptimizedHeapMinPQ<E> implements MinPQ<E> {
    /**
     * {@link List} of {@link PriorityNode} objects representing the heap of element-priority pairs.
     */
    private final List<PriorityNode<E>> elements;
    /**
     * {@link Map} of each element to its associated index in the {@code elements} heap.
     */
    private final Map<E, Integer> elementsToIndex;

    /**
     * Constructs an empty instance.
     */
    public OptimizedHeapMinPQ() {
        elements = new ArrayList<>();
        elementsToIndex = new HashMap<>();
        elements.add(null);
    }

    /**
     * Constructs an instance containing all the given elements and their priority values.
     *
     * @param elementsAndPriorities each element and its corresponding priority.
     */
    public OptimizedHeapMinPQ(Map<E, Double> elementsAndPriorities) {
        elements = new ArrayList<>(elementsAndPriorities.size());
        elementsToIndex = new HashMap<>(elementsAndPriorities.size());

        for (Map.Entry<E, Double> entry : elementsAndPriorities.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void add(E element, double priority) {
        if (contains(element)) {
            throw new IllegalArgumentException("Already contains " + element);
        }

        PriorityNode<E> newNode = new PriorityNode<>(element, priority);
        elements.add(newNode);

        int index = elements.size() - 1;
        elementsToIndex.put(element, index);

        percolateUp(index);
    }

    @Override
    public boolean contains(E element) {
        return elementsToIndex.containsKey(element);
    }

    @Override
    public double getPriority(E element) {
        if (!contains(element)) {
            throw new NoSuchElementException("Priority queue does not contain " + element);
        }
        int index = elementsToIndex.get(element);
        return elements.get(index).getPriority();
    }

    @Override
    public E peekMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("PQ is empty");
        }
        return elements.get(1).getElement();
    }

    @Override
    public E removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("PQ is empty");
        }
        E minElement = elements.get(1).getElement();
        swap(1, elements.size() - 1);
        elements.remove(elements.size() - 1);
        elementsToIndex.remove(minElement);

        if (!isEmpty()) {
            percolateDown(1);
        }

        return minElement;
    }

    @Override
    public void changePriority(E element, double priority) {
        if (!contains(element)) {
            throw new NoSuchElementException("PQ does not contain " + element);
        }

        int index = elementsToIndex.get(element);
        double oldPriority = elements.get(index).getPriority();
        elements.get(index).setPriority(priority);

        if (priority < oldPriority) {
            percolateUp(index);
        } else {
            percolateDown(index);
        }
    }

    @Override
    public int size() {
        return elements.size() - 1;
    }

    // Extra Methods:
    public boolean isEmpty() {
        return size() == 0;
    }

    private void percolateUp(int index) {
        while (index > 1) {
            int parentIndex = index / 2;
            if (elements.get(index).getPriority() < elements.get(parentIndex).getPriority()) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                index = 1;
            }
        }
    }

    private void percolateDown(int index) {
        int size = elements.size();
        while (2 * index < size) {
            int leftChild = 2 * index;
            int rightChild = leftChild + 1;
            int smallestChild = leftChild;

            if (rightChild < size && elements.get(rightChild).getPriority() < elements.get(leftChild).getPriority()) {
                smallestChild = rightChild;
            }

            if (elements.get(index).getPriority() > elements.get(smallestChild).getPriority()) {
                swap(index, smallestChild);
                index = smallestChild;
            } else {
                index = size;
            }
        }
    }

    private void swap(int i, int j) {
        PriorityNode<E> temp = elements.get(i);
        elements.set(i, elements.get(j));
        elements.set(j, temp);

        elementsToIndex.put(elements.get(i).getElement(), i);
        elementsToIndex.put(elements.get(j).getElement(), j);
    }
}