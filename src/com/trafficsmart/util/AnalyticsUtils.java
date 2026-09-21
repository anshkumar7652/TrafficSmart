package com.trafficsmart.util;

import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Functional and analytical processing utility demonstrating Unit 2 & Unit 5 concepts:
 * 1. Generic static methods
 * 2. Lambdas and Functional Interfaces (Predicate, Function, Consumer)
 * 3. Method references
 * 4. Advanced Collections processing (TreeMap, sorting, iterators).
 *
 * @author Ansh
 * @version 1.0
 */
public final class AnalyticsUtils {

    private AnalyticsUtils() {
        // Utility class
    }

    /**
     * Generic filter method applying a functional {@link Predicate} to any collection.
     * Demonstrates generic method design: {@code <T> List<T> filter(List<T>, Predicate<T>)}.
     *
     * @param items list of input items
     * @param predicate filter criteria lambda
     * @param <T> element type
     * @return filtered list
     */
    public static <T> List<T> filter(List<T> items, Predicate<T> predicate) {
        List<T> filtered = new ArrayList<>();
        if (items != null && predicate != null) {
            for (T item : items) {
                if (predicate.test(item)) {
                    filtered.add(item);
                }
            }
        }
        return filtered;
    }

    /**
     * Generic transformation method applying a {@link Function} across elements.
     *
     * @param items input collection
     * @param mapper conversion function
     * @param <T> source type
     * @param <R> destination type
     * @return list of transformed elements
     */
    public static <T, R> List<R> transform(List<T> items, Function<T, R> mapper) {
        List<R> transformed = new ArrayList<>();
        if (items != null && mapper != null) {
            for (T item : items) {
                transformed.add(mapper.apply(item));
            }
        }
        return transformed;
    }

    /**
     * Iterates over a collection and executes a {@link Consumer} on each element.
     *
     * @param items collection
     * @param consumer action lambda
     * @param <T> element type
     */
    public static <T> void forEach(List<T> items, Consumer<T> consumer) {
        if (items != null && consumer != null) {
            for (T item : items) {
                consumer.accept(item);
            }
        }
    }

    /**
     * Calculates the average speed of a vehicle fleet using method references and lambdas.
     *
     * @param vehicles list of vehicles
     * @return average speed in km/h
     */
    public static double computeAverageSpeed(List<Vehicle> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) {
            return 0.0;
        }
        double totalSpeed = 0.0;
        for (Vehicle v : vehicles) {
            totalSpeed += v.getSpeed();
        }
        return totalSpeed / vehicles.size();
    }

    /**
     * Organizes intersections into a {@link TreeMap} sorted in descending order of congestion.
     * Demonstrates TreeMap collection usage.
     *
     * @param intersections list of intersections
     * @return sorted TreeMap mapping congestion percentage to intersection
     */
    public static Map<Double, Intersection> rankByCongestion(List<Intersection> intersections) {
        // Reverse order comparator for highest congestion first
        Map<Double, Intersection> rankedMap = new TreeMap<>(Comparator.reverseOrder());
        if (intersections != null) {
            for (Intersection inter : intersections) {
                rankedMap.put(inter.getCongestionLevel(), inter);
            }
        }
        return Collections.unmodifiableMap(rankedMap);
    }
}
