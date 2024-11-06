import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Display the most commonly-reported WCAG recommendations.
 */
public class ReportAnalyzer {
    public static void main(String[] args) throws IOException {
        File inputFile = new File("data/wcag.tsv");
        Map<String, String> wcagDefinitions = new LinkedHashMap<>();
        Scanner scanner = new Scanner(inputFile);
        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("\t", 2);
            String index = "wcag" + line[0].replace(".", "");
            String title = line[1];
            wcagDefinitions.put(index, title);
        }

        Pattern re = Pattern.compile("wcag\\d{3,4}");
        List<String> wcagTags = Files.walk(Paths.get("data/reports"))
                .map(path -> {
                    try {
                        return Files.readString(path);
                    } catch (IOException e) {
                        return "";
                    }
                })
                .flatMap(contents -> re.matcher(contents).results())
                .map(MatchResult::group)
                .toList();

        Map<String, Integer> tagFrequency = new HashMap<>();
        for (String tag : wcagTags) {
            tagFrequency.put(tag, tagFrequency.getOrDefault(tag, 0) + 1);
        }

        PriorityQueue<Map.Entry<String, Integer>> minPQ = new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
            minPQ.offer(entry);
            if (minPQ.size() > 3) {
                minPQ.poll();
            }
        }

        List<Map.Entry<String, Integer>> topTags = new ArrayList<>(minPQ);
        topTags.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Integer> entry : topTags) {
            String description = wcagDefinitions.getOrDefault(entry.getKey(), "Unknown Description");
            System.out.println(description + " - Reported " + entry.getValue() + " times");
        }
    }
}