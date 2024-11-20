package graphs.shortestpaths;

import graphs.Edge;
import graphs.Graph;

import java.util.*;

/**
 * Topological sorting implementation of the {@link ShortestPathSolver} interface for <b>directed acyclic graphs</b>.
 *
 * @param <V> the type of vertices.
 * @see ShortestPathSolver
 */
public class ToposortDAGSolver<V> implements ShortestPathSolver<V> {
    private final Map<V, Edge<V>> edgeTo;
    private final Map<V, Double> distTo;

    /**
     * Constructs a new instance by executing the toposort-DAG-shortest-paths algorithm on the graph from the start.
     *
     * @param graph the input graph.
     * @param start the start vertex.
     */
    public ToposortDAGSolver(Graph<V> graph, V start) {
        edgeTo = new HashMap<>();
        distTo = new HashMap<>();

        // Initialize the distance to all vertices as positive infinity except for the start vertex
        for (V vertex : getAllVertices(graph, start)) {
            distTo.put(vertex, Double.POSITIVE_INFINITY);
        }
        distTo.put(start, 0.0);

        // Perform depth-first search to determine topological order in reverse postorder
        List<V> reversePostOrder = new ArrayList<>();
        Set<V> visited = new HashSet<>();
        dfsPostOrder(graph, start, visited, reversePostOrder);
        Collections.reverse(reversePostOrder);  // Reverse the list to get the correct topological order

        // Relax each vertex in topological order
        for (V vertex : reversePostOrder) {
            for (Edge<V> edge : graph.neighbors(vertex)) {
                relax(edge);
            }
        }
    }

    /**
     * Recursively adds nodes from the graph to the result in DFS postorder from the start vertex.
     *
     * @param graph   the input graph.
     * @param start   the start vertex.
     * @param visited the set of visited vertices.
     * @param result  the destination for adding nodes.
     */
    private void dfsPostOrder(Graph<V> graph, V start, Set<V> visited, List<V> result) {
        // Mark the start vertex as visited
        visited.add(start);

        // Visit all unvisited neighbors recursively
        for (Edge<V> edge : graph.neighbors(start)) {
            V neighbor = edge.to;
            if (!visited.contains(neighbor)) {
                dfsPostOrder(graph, neighbor, visited, result);
            }
        }

        // Add the current vertex to the result list after all its neighbors have been visited
        result.add(start);
    }

    private Set<V> getAllVertices(Graph<V> graph, V start) {
        Set<V> reachable = new HashSet<>();
        Queue<V> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            V current = queue.poll();
            if (!reachable.contains(current)) {
                reachable.add(current);
                for (Edge<V> edge : graph.neighbors(current)) {
                    queue.add(edge.to);
                }
            }
        }

        return reachable;
    }

    // Edge relaxation method
    private void relax(Edge<V> edge) {
        V from = edge.from;
        V to = edge.to;
        double weight = edge.weight;

        // If a shorter path to the "to" vertex is found
        if (distTo.get(from) + weight < distTo.get(to)) {
            distTo.put(to, distTo.get(from) + weight);
            edgeTo.put(to, edge);
        }
    }

    @Override
    public List<V> solution(V goal) {
        List<V> path = new ArrayList<>();
        V curr = goal;
        path.add(curr);
        while (edgeTo.get(curr) != null) {
            curr = edgeTo.get(curr).from;
            path.add(curr);
        }
        Collections.reverse(path);
        return path;
    }
}
