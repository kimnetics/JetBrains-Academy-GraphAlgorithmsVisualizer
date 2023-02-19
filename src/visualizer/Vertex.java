package visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

public class Vertex extends JPanel {
    private static final int VERTEX_DIAMETER = 50;

    private final String vertexId;
    private final int centerX;
    private final int centerY;
    private final int radius;
    private Color color;

    private final HashSet<Edge> edgeList = new HashSet<>();

    public Vertex(String vertexId, int centerX, int centerY) {
        this.vertexId = vertexId;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = Vertex.VERTEX_DIAMETER / 2;
        int left = centerX - radius;
        int top = centerY - radius;
        setColorNormal();

        setName(String.format("Vertex %s", vertexId));
        setBounds(left, top, VERTEX_DIAMETER, VERTEX_DIAMETER);
        setBackground(Color.BLACK);
        setOpaque(false);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel(vertexId);
        label.setName(String.format("VertexLabel %s", vertexId));
        add(label);
    }

    public String getVertexId() {
        return vertexId;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setColorNormal() {
        this.color = Color.WHITE;
    }

    public void setColorSelected() {
        this.color = Color.GREEN;
    }

    public HashSet<Edge> getEdgeList() {
        return edgeList;
    }

    public void addEdge(Edge edge) {
        edgeList.add(edge);
    }

    public void removeEdge(Edge edge) {
        edgeList.remove(edge);
    }

    // Does vertex include point?
    public boolean includesPoint(int x, int y) {
        // Calculate distance between point and center of vertex.
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        // Is distance within radius of vertex circle?
        return (distance <= radius);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(color);
        g.fillOval(0, 0, VERTEX_DIAMETER, VERTEX_DIAMETER);
    }
}
