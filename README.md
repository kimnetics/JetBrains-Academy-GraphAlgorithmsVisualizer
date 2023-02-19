# JetBrains Academy Graph-Algorithms Visualizer Project

An example of a passing solution to the final phase of the JetBrains Academy Java Graph-Algorithms Visualizer project.

## Description

The relative directory structure was kept the same as the one used in my JetBrains Academy solution.

The JetBrains Academy testing robot had trouble reliably clicking on edges between vertices. A workaround was to make the bounds for the JComponent for the edge be only the size of a clickable area for the edge. This made the testing robot happy, but humans can no longer actually see edges on the graph. If you want to see edges show up on the graph, swap which line is commented out in the constructor in the Edge.java file.

The logger in the ApplicationRunner.java file is not being used, but I left the reference there because it was handy to have around to do logging while debugging. I used a line like the following to log:

    ApplicationRunner.logger.log(System.Logger.Level.INFO, vertexAdjacencyList);
