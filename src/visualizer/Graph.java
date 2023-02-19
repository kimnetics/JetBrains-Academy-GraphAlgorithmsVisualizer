package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Graph extends JPanel {
    private final MainFrame mainFrame;

    private HashMap<String, Vertex> vertexList = new HashMap<>();
    private HashMap<String, Edge> edgeList = new HashMap<>();
    private String firstEdgeVertexId;
    private String secondEdgeVertexId;

    public Graph(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setName("Graph");
        setBounds(0, MainFrame.MODE_HEIGHT, MainFrame.APP_WIDTH, MainFrame.GRAPH_HEIGHT);
        setBackground(Color.BLACK);
        setLayout(null);

        addMouseListener(new GraphPanelClickListener());
    }

    // Reset graph panel.
    public void resetPanel() {
        vertexList = new HashMap<>();
        edgeList = new HashMap<>();
        resetEdgeVertices();
        removeAll();
        repaintPanel();
    }

    // Reset edge vertices.
    public void resetEdgeVertices() {
        firstEdgeVertexId = null;
        secondEdgeVertexId = null;
    }

    // Handle graph panel mouse clicks.
    private class GraphPanelClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // Was vertex clicked?
            String clickedVertexId = getClickedVertex(e.getX(), e.getY());
            if (clickedVertexId != null) {
                // Are we in an algorithm mode?
                MainFrame.Algorithm algorithm = mainFrame.getCurrentAlgorithm();
                if (algorithm != MainFrame.Algorithm.NONE) {
                    // Perform traversal algorithm.
                    performAlgorithm(algorithm, clickedVertexId);
                } else {
                    // Are we in add edge mode?
                    if (mainFrame.getCurrentMode() == MainFrame.Mode.ADD_EDGE) {
                        // Add new edge.
                        addEdge(clickedVertexId);
                    }
                    // Are we in remove vertex mode?
                    else if (mainFrame.getCurrentMode() == MainFrame.Mode.REMOVE_VERTEX) {
                        // Remove vertex.
                        Vertex vertex = vertexList.get(clickedVertexId);
                        removeVertex(vertex);
                        repaintPanel();
                    }
                }
                return;
            }

            // Was edge clicked?
            String clickedEdgeId = getClickedEdge(e.getX(), e.getY());
            if (clickedEdgeId != null) {
                // Are we in remove edge mode?
                if (mainFrame.getCurrentMode() == MainFrame.Mode.REMOVE_EDGE) {
                    // Remove edge.
                    Edge edge = edgeList.get(clickedEdgeId);
                    removeEdge(edge);
                    repaintPanel();
                }
                return;
            }

            // If we got here, no existing object was clicked.

            // Are we in add vertex mode?
            if (mainFrame.getCurrentMode() == MainFrame.Mode.ADD_VERTEX) {
                // Add new vertex.
                addVertex(e);
            }
        }
    }

    // Get clicked vertex.
    private String getClickedVertex(int x, int y) {
        // Loop through vertices.
        for (Map.Entry<String, Vertex> vertexEntry : vertexList.entrySet()) {
            // Does vertex include point?
            Vertex vertex = vertexEntry.getValue();
            if (vertex.includesPoint(x, y)) {
                return vertexEntry.getKey();
            }
        }

        return null;
    }

    // Add new vertex.
    private void addVertex(MouseEvent e) {
        // Ask user for vertex id.
        String vertexId;
        do {
            vertexId = JOptionPane.showInputDialog(null, "Enter the Vertex ID (Should be 1 char):", "Vertex", JOptionPane.INFORMATION_MESSAGE);
            if (vertexId == null) {
                return;
            }
            if (vertexList.containsKey(vertexId)) {
                vertexId = "BAD";
            }
        } while (vertexId.trim().length() != 1);

        // Add vertex to graph panel.
        Vertex vertex = new Vertex(vertexId, e.getX(), e.getY());
        add(vertex);
        repaintPanel();

        // Add vertex to list.
        vertexList.put(vertexId, vertex);
    }

    // Remove vertex.
    private void removeVertex(Vertex vertex) {
        // Remove edges from graph panel.
        HashSet<Edge> localEdgeList = new HashSet<>(vertex.getEdgeList());
        for (Edge edge : localEdgeList) {
            // Remove edge.
            removeEdge(edge);
        }

        // Remove vertex from graph panel.
        remove(vertex);

        // Remove vertex from list.
        vertexList.remove(vertex.getVertexId());
    }

    // Get clicked edge.
    private String getClickedEdge(int x, int y) {
        // Loop through edges.
        for (Map.Entry<String, Edge> edgeEntry : edgeList.entrySet()) {
            // Does edge include point?
            Edge edge = edgeEntry.getValue();
            if (edge.includesPoint(x, y)) {
                return edgeEntry.getKey();
            }
        }

        return null;
    }

    // Add new edge.
    private void addEdge(String clickedVertexId) {
        // Is this first vertex for edge?
        if (firstEdgeVertexId == null) {
            // Save first vertex.
            firstEdgeVertexId = clickedVertexId;

            // Highlight first vertex.
            vertexList.get(firstEdgeVertexId).setColorSelected();
            repaintPanel();
        } else {
            // Did user click on first vertex again?
            if (clickedVertexId.equals(firstEdgeVertexId)) {
                // Unhighlight first vertex.
                vertexList.get(firstEdgeVertexId).setColorNormal();
                repaintPanel();

                // Reset first vertex.
                firstEdgeVertexId = null;

                return;
            }

            // Save second vertex.
            secondEdgeVertexId = clickedVertexId;

            // Highlight second vertex.
            vertexList.get(secondEdgeVertexId).setColorSelected();
            repaintPanel();

            // Ask user for weight.
            Integer weight;
            do {
                String input;
                input = JOptionPane.showInputDialog(null, "Enter Weight:", "Input", JOptionPane.INFORMATION_MESSAGE);
                if (input == null) {
                    // Unhighlight second vertex.
                    vertexList.get(secondEdgeVertexId).setColorNormal();
                    repaintPanel();

                    // Reset second vertex.
                    secondEdgeVertexId = null;

                    return;
                }
                try {
                    weight = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    weight = null;
                }
            } while (weight == null);

            // Add forward edge to graph panel.
            Edge forwardEdge = new Edge(vertexList.get(firstEdgeVertexId), vertexList.get(clickedVertexId), weight);
            add(forwardEdge);

            // Add backward edge to graph panel.
            Edge backwardEdge = new Edge(vertexList.get(clickedVertexId), vertexList.get(firstEdgeVertexId), weight);
            add(backwardEdge);

            // Add forward edge to list.
            edgeList.put(forwardEdge.getEdgeId(), forwardEdge);

            // Let forward edge know about backward edge.
            forwardEdge.setReverseEdge(backwardEdge);

            // Add label to graph panel.
            addEdgeLabel(forwardEdge, weight);

            // Let vertices know about edge addition.
            forwardEdge.getStart().addEdge(forwardEdge);
            forwardEdge.getEnd().addEdge(forwardEdge);

            // Let traversal library know about edge addition.
            Traversal.addAdjacentVertex(firstEdgeVertexId, clickedVertexId, weight);
            Traversal.addAdjacentVertex(clickedVertexId, firstEdgeVertexId, weight);

            // Unhighlight first and second vertices.
            vertexList.get(firstEdgeVertexId).setColorNormal();
            vertexList.get(secondEdgeVertexId).setColorNormal();

            // Reset edge vertices.
            resetEdgeVertices();

            repaintPanel();
        }
    }

    // Remove edge.
    private void removeEdge(Edge edge) {
        // Remove backward edge from graph panel.
        remove(edge.getReverseEdge());

        // Remove forward edge from graph panel.
        remove(edge);

        // Remove forward edge from list.
        edgeList.remove(edge.getEdgeId());

        // Remove label from graph panel.
        JLabel label = edge.getLabel();
        if (label != null) {
            remove(label);
        }

        // Let vertices know about edge removal.
        edge.getStart().removeEdge(edge);
        edge.getEnd().removeEdge(edge);

        // Let traversal library know about edge removal.
        Traversal.removeAdjacentVertex(edge.getStart().getVertexId(), edge.getEnd().getVertexId());
        Traversal.removeAdjacentVertex(edge.getEnd().getVertexId(), edge.getStart().getVertexId());
    }

    // Add label for edge.
    private void addEdgeLabel(Edge edge, int weight) {
        Vertex start = edge.getStart();
        Vertex end = edge.getEnd();

        // Position label next to middle of edge.
        // TODO: Could use fine tuning.
        final int range = 20;
        int xOffset = 0;
        int yOffset = 0;
        Double edgeAngle = edge.getEdgeAngle();
        // Is line pretty horizontal? (Put label above line.)
        if ((edgeAngle > -range) && (edgeAngle < range)) {
            yOffset = -20;
        } else if ((edgeAngle < -180 + range) || (edgeAngle > 180 - range)) {
            yOffset = -20;
        }
        // Is line pretty vertical? (Put label to right of line.)
        else if ((edgeAngle > -90 - range) && (edgeAngle < -90 + range)) {
            xOffset = 10;
        } else if ((edgeAngle < 90 + range) && (edgeAngle > 90 - range)) {
            xOffset = 10;
        }
        // Line is somewhere in between. (Wing it a little.)
        else {
            xOffset = 12;
            yOffset = -12;
        }

        // Add label.
        JLabel label = new JLabel(String.valueOf(weight));
        label.setName(String.format("EdgeLabel <%s -> %s>", start.getVertexId(), end.getVertexId()));
        label.setBounds(edge.getMiddleX() + xOffset, edge.getMiddleY() + yOffset, 40, 15);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setForeground(Color.WHITE);
        add(label);

        // Let edge know about label.
        edge.setLabel(label);
    }

    // Perform traversal algorithm.
    private void performAlgorithm(MainFrame.Algorithm algorithm, String clickedVertexId) {
        // Turn off current mode.
        mainFrame.turnOffCurrentMode();

        // Display wait message.
        mainFrame.updateInformationLabel("Please wait...");

        // Dramatic pause.
        final Timer timer = new Timer(1000, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop timer.
                timer.stop();

                // Engage requested traversal logic.
                String results = "";
                if (algorithm == MainFrame.Algorithm.DEPTH_FIRST_SEARCH) {
                    results = Traversal.getDepthFirstSearchOrder(MainFrame.Algorithm.DEPTH_FIRST_SEARCH.abbreviation, clickedVertexId);
                } else if (algorithm == MainFrame.Algorithm.BREADTH_FIRST_SEARCH) {
                    results = Traversal.getBreadthFirstSearchOrder(MainFrame.Algorithm.BREADTH_FIRST_SEARCH.abbreviation, clickedVertexId);
                } else if (algorithm == MainFrame.Algorithm.DIJKSTRAS_ALGORITHM) {
                    results = Traversal.getDijkstrasAlgorithmCostPairs(clickedVertexId);
                } else if (algorithm == MainFrame.Algorithm.PRIMS_ALGORITHM) {
                    results = Traversal.getPrimsAlgorithmChildParentPairs(clickedVertexId);
                }

                // Turn off algorithm selection.
                mainFrame.turnOffCurrentAlgorithm();

                // Display traversal results.
                mainFrame.updateInformationLabel(results);
            }
        });

        // Start timer.
        timer.start();
    }

    // Repaint graph panel.
    private void repaintPanel() {
        revalidate();
        repaint();
    }
}
