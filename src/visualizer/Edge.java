package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

import static java.awt.geom.Line2D.ptLineDistSq;

public class Edge extends JComponent {
    private final Vertex start;
    private final Vertex end;
    private final int weight;
    private Edge reverseEdge;
    private JLabel label;

    public Edge(Vertex start, Vertex end, int weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;

        setName(String.format("Edge <%s -> %s>", start.getVertexId(), end.getVertexId()));
        //setSize(MainFrame.APP_WIDTH, MainFrame.GRAPH_HEIGHT); // Uncomment this line and comment next line to see graph lines.
        setBounds(getMiddleX() - 1, getMiddleY() - 1, 2, 2); // Give testing robot an area over the graph line to click. It was missing otherwise...
    }

    public String getEdgeId() {
        return String.format("%s->%s", start.getVertexId(), end.getVertexId());
    }

    public Vertex getStart() {
        return start;
    }

    public Vertex getEnd() {
        return end;
    }

    public int getWeight() {
        return weight;
    }

    public Edge getReverseEdge() {
        return reverseEdge;
    }

    public void setReverseEdge(Edge reverseEdge) {
        this.reverseEdge = reverseEdge;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public int getMiddleX() {
        return Math.min(start.getCenterX(), end.getCenterX()) + Math.abs(start.getCenterX() - end.getCenterX()) / 2;
    }

    public int getMiddleY() {
        return Math.min(start.getCenterY(), end.getCenterY()) + Math.abs(start.getCenterY() - end.getCenterY()) / 2;
    }

    // Get edge angle.
    //
    // Return values indicate the following:
    //  -135 -90 -45
    //     \  |  /
    // -179 \ | /
    // 180 -- x -- 0
    //      / | \
    //     /  |  \
    //   135  90  45
    public Double getEdgeAngle() {
        double startX = start.getCenterX();
        double startY = start.getCenterY();
        double endX = end.getCenterX();
        double endY = end.getCenterY();

        if ((startX == endX) && (startY > endY)) {
            return -90.0;
        } else if ((startY == endY) && (startX < endX)) {
            return 0.0;
        } else if ((startX == endX) && (startY < endY)) {
            return 90.0;
        } else if ((startY == endY) && (startX > endX)) {
            return 180.0;
        } else if ((startX == endX) && (startY == endY)) {
            throw new IllegalArgumentException();
        }

        double dx = endX - startX;
        double dy = endY - startY;
        return Math.toDegrees(Math.atan2(dy, dx));
    }

    // Does edge include point?
    public boolean includesPoint(int x, int y) {
        // Calculate distance between point and nearest point on line.
        double distance = Math.sqrt(ptLineDistSq(start.getCenterX(), start.getCenterY(), end.getCenterX(), end.getCenterY(), x, y));
        // Is distance within an acceptable amount?
        return (distance <= 4.0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Line2D line = new Line2D.Double(start.getCenterX(), start.getCenterY(), end.getCenterX(), end.getCenterY());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2.0F));
        g2d.draw(line);
    }
}
