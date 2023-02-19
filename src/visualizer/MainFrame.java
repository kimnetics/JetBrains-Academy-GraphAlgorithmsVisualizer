package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 600;

    public static final int MENU_HEIGHT = 50;
    public static final int MODE_HEIGHT = 20;
    public static final int INFORMATION_HEIGHT = 20;
    public static final int GRAPH_HEIGHT = APP_HEIGHT - MENU_HEIGHT - MODE_HEIGHT - INFORMATION_HEIGHT;

    private static final String MENU_FILE = "File";
    private static final String MENU_ITEM_NEW = "New";
    private static final String MENU_ITEM_EXIT = "Exit";

    private static final String MENU_MODE = "Mode";

    public enum Mode {
        ADD_VERTEX("Add a Vertex"),
        ADD_EDGE("Add an Edge"),
        REMOVE_VERTEX("Remove a Vertex"),
        REMOVE_EDGE("Remove an Edge"),
        NONE("None");

        public final String label;

        Mode(String label) {
            this.label = label;
        }
    }

    private static final String MENU_ALGORITHMS = "Algorithms";

    public enum Algorithm {
        DEPTH_FIRST_SEARCH("Depth-First Search", "DFS"),
        BREADTH_FIRST_SEARCH("Breadth-First Search", "BFS"),
        DIJKSTRAS_ALGORITHM("Dijkstra's Algorithm", "DA"),
        PRIMS_ALGORITHM("Prim's Algorithm", "PA"),
        NONE("None", "NONE");

        public final String name;
        public final String abbreviation;

        Algorithm(String name, String abbreviation) {
            this.name = name;
            this.abbreviation = abbreviation;
        }
    }

    private Mode currentMode = Mode.ADD_VERTEX;

    private Algorithm currentAlgorithm = Algorithm.NONE;

    private final JLabel currentModeLabel = new JLabel();

    private final Graph graphPanel = new Graph(this);

    private final JLabel informationLabel = new JLabel();

    // Initialize main frame.
    public MainFrame() {
        super("Graph-Algorithms Visualizer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(APP_WIDTH, APP_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(null);

        // Add menu bar.
        addMenuBar();

        // Add current mode panel and label.
        JPanel currentModePanel = new JPanel();
        currentModePanel.setBounds(0, 0, APP_WIDTH, MODE_HEIGHT);
        currentModePanel.setBackground(Color.BLACK);
        currentModePanel.setLayout(null);

        currentModeLabel.setName("Mode");
        currentModeLabel.setBounds(APP_WIDTH - 305, 0, 300, MODE_HEIGHT);
        currentModeLabel.setHorizontalAlignment(JLabel.RIGHT);
        currentModeLabel.setForeground(Color.WHITE);
        currentModePanel.add(currentModeLabel);
        add(currentModePanel);
        updateModeLabel();

        // Add graph panel.
        add(graphPanel);

        // Add information display panel and label.
        JPanel informationPanel = new JPanel();
        informationPanel.setBounds(0, APP_HEIGHT - MENU_HEIGHT - INFORMATION_HEIGHT, APP_WIDTH, INFORMATION_HEIGHT);
        informationPanel.setBackground(Color.WHITE);
        informationPanel.setLayout(null);

        informationLabel.setName("Display");
        informationLabel.setBounds(0, 0, APP_WIDTH, INFORMATION_HEIGHT);
        informationLabel.setHorizontalAlignment(JLabel.CENTER);
        informationLabel.setForeground(Color.BLACK);
        informationPanel.add(informationLabel);
        add(informationPanel);
        updateInformationLabel("");

        setVisible(true);
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    public void turnOffCurrentMode() {
        currentMode = Mode.NONE;
        updateModeLabel();
    }

    public Algorithm getCurrentAlgorithm() {
        return currentAlgorithm;
    }

    public void turnOffCurrentAlgorithm() {
        currentAlgorithm = Algorithm.NONE;
        updateInformationLabel("");
    }

    // Add menu bar.
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setName("MenuBar");

        // File menu.
        JMenu fileMenu = new JMenu(MENU_FILE);
        fileMenu.setName(MENU_FILE);

        JMenuItem newMenuItem = new JMenuItem(MENU_ITEM_NEW);
        newMenuItem.setName(MENU_ITEM_NEW);
        newMenuItem.addActionListener(new FileMenuActionListener(MENU_ITEM_NEW));

        JMenuItem exitMenuItem = new JMenuItem(MENU_ITEM_EXIT);
        exitMenuItem.setName(MENU_ITEM_EXIT);
        exitMenuItem.addActionListener(new FileMenuActionListener(MENU_ITEM_EXIT));

        fileMenu.add(newMenuItem);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        // Mode menu.
        JMenu modeMenu = new JMenu(MENU_MODE);
        modeMenu.setName(MENU_MODE);

        JMenuItem addVertexMenuItem = new JMenuItem(Mode.ADD_VERTEX.label);
        addVertexMenuItem.setName(Mode.ADD_VERTEX.label);
        addVertexMenuItem.addActionListener(new ModeMenuActionListener(Mode.ADD_VERTEX));

        JMenuItem addEdgeMenuItem = new JMenuItem(Mode.ADD_EDGE.label);
        addEdgeMenuItem.setName(Mode.ADD_EDGE.label);
        addEdgeMenuItem.addActionListener(new ModeMenuActionListener(Mode.ADD_EDGE));

        JMenuItem removeVertexMenuItem = new JMenuItem(Mode.REMOVE_VERTEX.label);
        removeVertexMenuItem.setName(Mode.REMOVE_VERTEX.label);
        removeVertexMenuItem.addActionListener(new ModeMenuActionListener(Mode.REMOVE_VERTEX));

        JMenuItem removeEdgeMenuItem = new JMenuItem(Mode.REMOVE_EDGE.label);
        removeEdgeMenuItem.setName(Mode.REMOVE_EDGE.label);
        removeEdgeMenuItem.addActionListener(new ModeMenuActionListener(Mode.REMOVE_EDGE));

        JMenuItem noneMenuItem = new JMenuItem(Mode.NONE.label);
        noneMenuItem.setName(Mode.NONE.label);
        noneMenuItem.addActionListener(new ModeMenuActionListener(Mode.NONE));

        modeMenu.add(addVertexMenuItem);
        modeMenu.add(addEdgeMenuItem);
        modeMenu.add(removeVertexMenuItem);
        modeMenu.add(removeEdgeMenuItem);
        modeMenu.add(noneMenuItem);
        menuBar.add(modeMenu);

        // Algorithms menu.
        JMenu algorithmsMenu = new JMenu(MENU_ALGORITHMS);
        algorithmsMenu.setName(MENU_ALGORITHMS);

        JMenuItem depthFirstSearchMenuItem = new JMenuItem(Algorithm.DEPTH_FIRST_SEARCH.name);
        depthFirstSearchMenuItem.setName(Algorithm.DEPTH_FIRST_SEARCH.name);
        depthFirstSearchMenuItem.addActionListener(new AlgorithmsMenuActionListener(Algorithm.DEPTH_FIRST_SEARCH));

        JMenuItem breadthFirstSearchMenuItem = new JMenuItem(Algorithm.BREADTH_FIRST_SEARCH.name);
        breadthFirstSearchMenuItem.setName(Algorithm.BREADTH_FIRST_SEARCH.name);
        breadthFirstSearchMenuItem.addActionListener(new AlgorithmsMenuActionListener(Algorithm.BREADTH_FIRST_SEARCH));

        JMenuItem dijkstrasAlgorithmMenuItem = new JMenuItem(Algorithm.DIJKSTRAS_ALGORITHM.name);
        dijkstrasAlgorithmMenuItem.setName(Algorithm.DIJKSTRAS_ALGORITHM.name);
        dijkstrasAlgorithmMenuItem.addActionListener(new AlgorithmsMenuActionListener(Algorithm.DIJKSTRAS_ALGORITHM));

        JMenuItem primsAlgorithmMenuItem = new JMenuItem(Algorithm.PRIMS_ALGORITHM.name);
        primsAlgorithmMenuItem.setName(Algorithm.PRIMS_ALGORITHM.name);
        primsAlgorithmMenuItem.addActionListener(new AlgorithmsMenuActionListener(Algorithm.PRIMS_ALGORITHM));

        algorithmsMenu.add(depthFirstSearchMenuItem);
        algorithmsMenu.add(breadthFirstSearchMenuItem);
        algorithmsMenu.add(dijkstrasAlgorithmMenuItem);
        algorithmsMenu.add(primsAlgorithmMenuItem);
        menuBar.add(algorithmsMenu);

        setJMenuBar(menuBar);
    }

    // Handle file menu item selection.
    private class FileMenuActionListener implements ActionListener {
        private final String action;

        public FileMenuActionListener(String action) {
            super();
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (action) {
                case MENU_ITEM_NEW -> {
                    currentMode = Mode.ADD_VERTEX;
                    updateModeLabel();
                    turnOffCurrentAlgorithm();
                    // Reset graph panel.
                    graphPanel.resetPanel();
                    // Reset vertex adjacency list.
                    Traversal.resetAdjacencyList();
                }
                case MENU_ITEM_EXIT -> {
                    // Exit application.
                    setVisible(false);
                    dispose();
                }
            }
        }
    }

    // Handle mode menu item selection.
    private class ModeMenuActionListener implements ActionListener {
        private final Mode mode;

        public ModeMenuActionListener(Mode mode) {
            super();
            this.mode = mode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            currentMode = mode;
            updateModeLabel();
        }
    }

    // Update current mode label.
    private void updateModeLabel() {
        currentModeLabel.setText(String.format("Current Mode -> %s", currentMode.label));
        graphPanel.resetEdgeVertices();
    }

    // Handle algorithms menu item selection.
    private class AlgorithmsMenuActionListener implements ActionListener {
        private final Algorithm algorithm;

        public AlgorithmsMenuActionListener(Algorithm algorithm) {
            super();
            this.algorithm = algorithm;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Is algorithm already selected?
            if (currentAlgorithm == algorithm) {
                // Turn off algorithm selection.
                turnOffCurrentAlgorithm();
                return;
            }
            currentAlgorithm = algorithm;

            // Ask user to choose starting vertex.
            updateInformationLabel("Please choose a starting vertex");
        }
    }

    // Update information display label.
    public void updateInformationLabel(String text) {
        informationLabel.setText(text);
    }
}
