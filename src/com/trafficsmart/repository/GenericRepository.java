package com.trafficsmart.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

/**
 * Generic, thread-safe in-memory repository demonstrating Generics and Collections framework.
 * Uses bounded type parameter {@code <T extends Comparable<? super T>>}.
 * Employs multiple Java collection types: {@link HashMap}, {@link ArrayList}, {@link HashSet}, and {@link TreeSet}.
 *
 * @param <T> entity type that must implement Comparable
 * @author Ansh
 * @version 1.0
 */
public class GenericRepository<T extends Comparable<? super T>> {

    private final Map<String, T> storageMap;
    private final List<T> orderedList;
    private final Set<String> idSet;

    /**
     * Constructs a new GenericRepository initializing underlying collection structures.
     */
    public GenericRepository() {
        this.storageMap = new HashMap<>();
        this.orderedList = new ArrayList<>();
        this.idSet = new HashSet<>();
    }

    /**
     * Stores an entity with a unique key.
     *
     * @param id unique string identifier
     * @param entity item to save
     */
    public synchronized void save(String id, T entity) {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(entity, "Entity cannot be null");

        if (idSet.contains(id)) {
            // Remove previous instance from orderedList to update position
            orderedList.remove(storageMap.get(id));
        }

        storageMap.put(id, entity);
        orderedList.add(entity);
        idSet.add(id);
    }

    /**
     * Bulk saves a list of items using wildcards ({@code <? extends T>}).
     *
     * @param items list of items extending T
     * @param idExtractor lambda function to extract the string ID
     */
    public synchronized void saveAll(List<? extends T> items, java.util.function.Function<T, String> idExtractor) {
        if (items != null && idExtractor != null) {
            for (T item : items) {
                save(idExtractor.apply(item), item);
            }
        }
    }

    /**
     * Retrieves an entity by its key.
     *
     * @param id string ID
     * @return found entity or null
     */
    public synchronized T findById(String id) {
        return storageMap.get(id);
    }

    /**
     * Returns an unmodifiable snapshot of all stored entities in insertion order.
     *
     * @return unmodifiable list of entities
     */
    public synchronized List<T> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(orderedList));
    }

    /**
     * Removes an entity by key using an explicit Iterator to demonstrate collections traversal.
     *
     * @param id key to remove
     * @return true if found and removed
     */
    public synchronized boolean deleteById(String id) {
        if (!idSet.contains(id)) {
            return false;
        }

        T removedEntity = storageMap.remove(id);
        idSet.remove(id);

        // Demonstrate explicit Iterator usage (Unit 5 syllabus requirement)
        Iterator<T> iterator = orderedList.iterator();
        while (iterator.hasNext()) {
            T current = iterator.next();
            if (Objects.equals(current, removedEntity)) {
                iterator.remove();
                break;
            }
        }
        return true;
    }

    /**
     * Returns a naturally sorted {@link TreeSet} containing all items in the repository.
     * Leverages the bounded type parameter {@code <T extends Comparable<T>>}.
     *
     * @return sorted TreeSet of entities
     */
    public synchronized TreeSet<T> findSorted() {
        return new TreeSet<>(orderedList);
    }

    /**
     * Generic query method filtering repository items matching a functional {@link Predicate}.
     *
     * @param predicate functional filter condition
     * @return filtered list of entities
     */
    public synchronized List<T> filterBy(Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : orderedList) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Returns items sorted by a specific custom {@link Comparator}.
     *
     * @param comparator comparison strategy
     * @return sorted list
     */
    public synchronized List<T> getSorted(Comparator<T> comparator) {
        List<T> copy = new ArrayList<>(orderedList);
        copy.sort(comparator);
        return copy;
    }

    public synchronized boolean exists(String id) {
        return idSet.contains(id);
    }

    public synchronized int count() {
        return orderedList.size();
    }

    public synchronized void clear() {
        storageMap.clear();
        orderedList.clear();
        idSet.clear();
    }
}
