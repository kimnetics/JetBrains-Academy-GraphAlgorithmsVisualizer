package visualizer;

import java.util.*;

public class Traversal {
    private static TreeMap<String, TreeSet<Map.Entry<String, Integer>>> vertexAdjacencyList = new TreeMap<>();

    private static LinkedHashSet<String> processedVerticesList = new LinkedHashSet<>();
    private static final StringBuilder results = new StringBuilder();

    // Reset vertex adjacency list.
    public static void resetAdjacencyList() {
        vertexAdjacencyList = new TreeMap<>();
    }

    // Add adjacency list entry.
    public static void addAdjacentVertex(String fromVertexId, String toVertexId, int weight) {
        // Add from vertex to adjacency list if it does not yet exist.
        if (!vertexAdjacencyList.containsKey(fromVertexId)) {
            // Configure comparator to sort entries in from vertex list by weight.
            vertexAdjacencyList.put(fromVertexId, new TreeSet<>(
                    new Comparator<>() {
                        @Override
                        public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                            int e1Value = e1.getValue();
                            int e2Value = e2.getValue();
                            if (e1Value < e2Value) {
                                return -1;
                            } else if (e1Value > e2Value) {
                                return 1;
                            } else {
                                return e1.getKey().compareTo(e2.getKey());
                            }
                        }
                    }));
        }

        // Add to vertex to from vertex list.
        TreeSet<Map.Entry<String, Integer>> fromVertexList = vertexAdjacencyList.get(fromVertexId);
        fromVertexList.add(new AbstractMap.SimpleEntry<>(toVertexId, weight));
    }

    // Remove adjacency list entry.
    public static void removeAdjacentVertex(String fromVertexId, String toVertexId) {
        // Throw exception if from vertex is not in adjacency list.
        if (!vertexAdjacencyList.containsKey(fromVertexId)) {
            throw new IllegalArgumentException("From vertex id is not in adjacency list.");
        }

        // Remove to vertex from from vertex list.
        TreeSet<Map.Entry<String, Integer>> fromVertexList = vertexAdjacencyList.get(fromVertexId);
        Map.Entry<String, Integer> entryToDelete = null;
        for (Map.Entry<String, Integer> fromVertexListEntry : fromVertexList) {
            if (fromVertexListEntry.getKey().equals(toVertexId)) {
                entryToDelete = fromVertexListEntry;
                break;
            }
        }
        if (entryToDelete != null) {
            fromVertexList.remove(entryToDelete);
        } else {
            // Throw exception if to vertex is not in from vertex list.
            throw new IllegalArgumentException("To vertex id is not in from vertex list.");
        }
    }

    // Get depth first search order results.
    public static String getDepthFirstSearchOrder(String searchAbbreviation, String startVertexId) {
        // Reset processed vertices list.
        processedVerticesList = new LinkedHashSet<>();

        // Reset results.
        results.setLength(0);

        // Process vertices.
        processDepthFirstSearchVertex(startVertexId);
        return String.format("%s : %s", searchAbbreviation, results);
    }

    private static void processDepthFirstSearchVertex(String vertexId) {
        // Exit if vertex was already processed.
        if (processedVerticesList.contains(vertexId)) {
            return;
        }

        // Add vertex id to results.
        if (!results.isEmpty()) {
            results.append(" -> ");
        }
        results.append(vertexId);

        // Mark vertex as processed.
        processedVerticesList.add(vertexId);

        // Loop through connected vertices.
        for (Map.Entry<String, Integer> vertexAdjacencyListEntry : vertexAdjacencyList.get(vertexId)) {
            // Process vertex.
            processDepthFirstSearchVertex(vertexAdjacencyListEntry.getKey());
        }
    }

    // Get breadth first search order results.
    public static String getBreadthFirstSearchOrder(String searchAbbreviation, String startVertexId) {
        // Reset processed vertices list.
        processedVerticesList = new LinkedHashSet<>();

        // Reset results.
        results.setLength(0);

        // Reset queue.
        ArrayDeque<String> verticesToProcessQueue = new ArrayDeque<>();

        String vertexId = startVertexId;
        while (true) {
            // Process vertex.
            processBreadthFirstSearchVertex(vertexId, verticesToProcessQueue);
            // Exit if queue is empty.
            if (verticesToProcessQueue.isEmpty()) {
                break;
            }
            // Get next vertex from queue.
            vertexId = verticesToProcessQueue.remove();
        }
        return String.format("%s : %s", searchAbbreviation, results);
    }

    private static void processBreadthFirstSearchVertex(String vertexId, ArrayDeque<String> verticesToProcessQueue) {
        // Exit if vertex was already processed.
        if (processedVerticesList.contains(vertexId)) {
            return;
        }

        // Add vertex id to results.
        if (!results.isEmpty()) {
            results.append(" -> ");
        }
        results.append(vertexId);

        // Mark vertex as processed.
        processedVerticesList.add(vertexId);

        // Loop through connected vertices.
        for (Map.Entry<String, Integer> vertexAdjacencyListEntry : vertexAdjacencyList.get(vertexId)) {
            // Add to queue.
            verticesToProcessQueue.add(vertexAdjacencyListEntry.getKey());
        }
    }

    // Get Dijkstra's Algorithm cost pair results.
    public static String getDijkstrasAlgorithmCostPairs(String startVertexId) {
        // Reset processed vertices list.
        processedVerticesList = new LinkedHashSet<>();

        // Reset results.
        results.setLength(0);

        // Initialize distance to source map.
        HashMap<String, Integer> vertexDistanceMap = new HashMap<>();
        for (Map.Entry<String, TreeSet<Map.Entry<String, Integer>>> vertexAdjacencyListEntry : vertexAdjacencyList.entrySet()) {
            String vertexId = vertexAdjacencyListEntry.getKey();
            vertexDistanceMap.put(vertexId, Integer.MAX_VALUE);
        }
        vertexDistanceMap.put(startVertexId, 0);

        // Loop until all vertices are processed.
        while (true) {
            // Find unprocessed vertex with smallest distance.
            String smallestDistanceVertexId = null;
            int smallestDistance = Integer.MAX_VALUE;
            for (Map.Entry<String, Integer> vertexDistanceMapEntry : vertexDistanceMap.entrySet()) {
                String vertexId = vertexDistanceMapEntry.getKey();
                int distance = vertexDistanceMapEntry.getValue();
                if (!processedVerticesList.contains(vertexId)) {
                    if (distance < smallestDistance) {
                        smallestDistanceVertexId = vertexId;
                        smallestDistance = distance;
                    }
                }
            }

            // Are there no unprocessed vertices?
            if (smallestDistanceVertexId == null) {
                break;
            }

            // Process neighbors for unprocessed vertex with smallest distance.
            TreeSet<Map.Entry<String, Integer>> fromVertexList = vertexAdjacencyList.get(smallestDistanceVertexId);
            for (Map.Entry<String, Integer> fromVertexListEntry : fromVertexList) {
                processDijkstrasAlgorithmNeighborVertex(smallestDistanceVertexId, fromVertexListEntry.getKey(), fromVertexListEntry.getValue(), vertexDistanceMap);
            }

            // Mark vertex as processed.
            processedVerticesList.add(smallestDistanceVertexId);
        }

        // Return output string.
        for (Map.Entry<String, Integer> vertexDistanceMapEntry : vertexDistanceMap.entrySet()) {
            // Are we not on start vertex?
            int distance = vertexDistanceMapEntry.getValue();
            if (!(distance == 0)) {
                if (!results.isEmpty()) {
                    results.append(", ");
                }
                results.append(String.format("%s=%d", vertexDistanceMapEntry.getKey(), distance));
            }
        }
        return results.toString();
    }

    private static void processDijkstrasAlgorithmNeighborVertex(String fromVertexId, String neighborVertexId, int weight, HashMap<String, Integer> vertexDistanceMap) {
        // Exit if neighbor vertex was already processed.
        if (processedVerticesList.contains(neighborVertexId)) {
            return;
        }

        // Update neighbor distance if from vertex provides shorter distance to source.
        int fromToNeighborDistance = vertexDistanceMap.get(fromVertexId) + weight;
        if (fromToNeighborDistance < vertexDistanceMap.get(neighborVertexId)) {
            vertexDistanceMap.put(neighborVertexId, fromToNeighborDistance);
        }
    }

    // Get Prim's Algorithm child parent pair results.
    public static String getPrimsAlgorithmChildParentPairs(String startVertexId) {
        // Reset results.
        results.setLength(0);

        // Initialize minimum spanning tree map.
        TreeMap<String, String> minimumSpanningTree = new TreeMap<>();
        minimumSpanningTree.put(startVertexId, null);

        // Loop until all vertices are processed.
        while (true) {
            // Find not yet processed neighbor of vertex in minimum spanning tree with smallest minimum weight.
            String parentVertexId = null;
            Map.Entry<String, Integer> childVertexListEntry = null;
            int minimumWeight = Integer.MAX_VALUE;
            // Loop through minimum spanning tree map.
            for (Map.Entry<String, String> minimumSpanningTreeEntry : minimumSpanningTree.entrySet()) {
                // Loop through adjacency list for vertex.
                TreeSet<Map.Entry<String, Integer>> fromVertexList = vertexAdjacencyList.get(minimumSpanningTreeEntry.getKey());
                for (Map.Entry<String, Integer> fromVertexListEntry : fromVertexList) {
                    // Is neighbor not already in minimum spanning tree map?
                    if (!minimumSpanningTree.containsKey(fromVertexListEntry.getKey())) {
                        int weight = fromVertexListEntry.getValue();
                        if (weight < minimumWeight) {
                            parentVertexId = minimumSpanningTreeEntry.getKey();
                            childVertexListEntry = fromVertexListEntry;
                            minimumWeight = weight;
                        }
                    }
                }
            }

            // Are there no unprocessed vertices?
            if (childVertexListEntry == null) {
                break;
            }

            // Add neighbor to minimum spanning tree.
            minimumSpanningTree.put(childVertexListEntry.getKey(), parentVertexId);
        }

        // Return output string.
        for (Map.Entry<String, String> minimumSpanningTreeEntry : minimumSpanningTree.entrySet()) {
            // Are we not on start vertex?
            String parentVertexId = minimumSpanningTreeEntry.getValue();
            if (!(parentVertexId == null)) {
                if (!results.isEmpty()) {
                    results.append(", ");
                }
                results.append(String.format("%s=%s", minimumSpanningTreeEntry.getKey(), parentVertexId));
            }
        }
        return results.toString();
    }
}
