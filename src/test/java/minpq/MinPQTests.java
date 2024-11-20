package minpq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Abstract class providing test cases for all {@link MinPQ} implementations.
 *
 * @see MinPQ
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class MinPQTests {
    /**
     * Returns an empty {@link MinPQ}.
     *
     * @return an empty {@link MinPQ}
     */
    public abstract <E> MinPQ<E> createMinPQ();

    @Test
    public void wcagIndexAsPriority() throws FileNotFoundException {
        File inputFile = new File("data/wcag.tsv");
        MinPQ<String> reference = new DoubleMapMinPQ<>();
        MinPQ<String> testing = createMinPQ();
        Scanner scanner = new Scanner(inputFile);
        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("\t", 2);
            int index = Integer.parseInt(line[0].replace(".", ""));
            String title = line[1];
            reference.add(title, index);
            testing.add(title, index);
        }
        while (!reference.isEmpty()) {
            assertEquals(reference.removeMin(), testing.removeMin());
        }
        assertTrue(testing.isEmpty());
    }

    @Test
    public void randomPriorities() {
        int[] elements = new int[1000];
        for (int i = 0; i < elements.length; i = i + 1) {
            elements[i] = i;
        }
        Random random = new Random(373);
        int[] priorities = new int[elements.length];
        for (int i = 0; i < priorities.length; i = i + 1) {
            priorities[i] = random.nextInt(priorities.length);
        }

        MinPQ<Integer> reference = new DoubleMapMinPQ<>();
        MinPQ<Integer> testing = createMinPQ();
        for (int i = 0; i < elements.length; i = i + 1) {
            reference.add(elements[i], priorities[i]);
            testing.add(elements[i], priorities[i]);
        }

        for (int i = 0; i < elements.length; i = i + 1) {
            int expected = reference.removeMin();
            int actual = testing.removeMin();

            if (expected != actual) {
                int expectedPriority = priorities[expected];
                int actualPriority = priorities[actual];
                assertEquals(expectedPriority, actualPriority);
            }
        }
    }

    @Test
    public void randomTestWithWcagTags() throws FileNotFoundException {
        // Read the list of WCAG tags from the file
        File inputFile = new File("data/wcag.tsv");
        Scanner scanner = new Scanner(inputFile);
        List<String> tags = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("\t", 2);
            String title = line[1];
            tags.add(title);
        }
        scanner.close();

        // Define the top N tags to upweight
        List<String> topTags = Arrays.asList(
                "WCAG 1.1.1 Non-text Content",
                "WCAG 1.3.1 Info and Relationships",
                "WCAG 4.1.2 Name, Role, Value"
        );

        // Define the upweight factor
        int upweightFactor = 10;

        // Create a tag pool with upweighted top tags
        List<String> tagPool = new ArrayList<>();
        for (String tag : tags) {
            if (topTags.contains(tag)) {
                for (int i = 0; i < upweightFactor; i++) {
                    tagPool.add(tag);
                }
            } else {
                tagPool.add(tag);
            }
        }

        // Initialize the MinPQs and a map to keep track of counts
        MinPQ<String> reference = new DoubleMapMinPQ<>();
        MinPQ<String> testing = createMinPQ();
        Map<String, Integer> counts = new HashMap<>();

        Random random = new Random(42);
        int totalCounts = 0;
        int targetCounts = 10000;

        // Randomly sample tags and update counts
        while (totalCounts < targetCounts) {
            String tag = tagPool.get(random.nextInt(tagPool.size()));
            counts.put(tag, counts.getOrDefault(tag, 0) + 1);
            int newCount = counts.get(tag);

            // Update or add the tag in both MinPQs
            if (reference.contains(tag)) {
                reference.changePriority(tag, newCount);
                testing.changePriority(tag, newCount);
            } else {
                reference.add(tag, newCount);
                testing.add(tag, newCount);
            }
            totalCounts++;
        }

        // Remove all tags and compare the removal order
        List<String> referenceOrder = new ArrayList<>();
        while (!reference.isEmpty()) {
            referenceOrder.add(reference.removeMin());
        }

        List<String> testingOrder = new ArrayList<>();
        while (!testing.isEmpty()) {
            testingOrder.add(testing.removeMin());
        }

        // Verify that the counts in removal order are consistent
        List<Integer> referenceCounts = referenceOrder.stream().map(counts::get).collect(Collectors.toList());
        List<Integer> testingCounts = testingOrder.stream().map(counts::get).collect(Collectors.toList());
        assertEquals(referenceCounts, testingCounts, "The counts in removal order should match.");

        // Verify that for tags with the same count, the sets are equal (order may vary due to ties)
        Map<Integer, Set<String>> referenceGroups = groupTagsByCount(referenceOrder, counts);
        Map<Integer, Set<String>> testingGroups = groupTagsByCount(testingOrder, counts);
        assertEquals(referenceGroups, testingGroups, "The groups of tags with the same count should match.");
    }

    // Helper method to group tags by their counts
    private Map<Integer, Set<String>> groupTagsByCount(List<String> tags, Map<String, Integer> counts) {
        Map<Integer, Set<String>> groups = new HashMap<>();
        for (String tag : tags) {
            int count = counts.get(tag);
            groups.computeIfAbsent(count, k -> new HashSet<>()).add(tag);
        }
        return groups;
    }
}
