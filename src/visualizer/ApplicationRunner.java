package visualizer;

import java.lang.System.Logger;

public class ApplicationRunner {
    public static final Logger logger = System.getLogger(ApplicationRunner.class.getName());

    public static void main(String[] args) {
        // Initialize main frame.
        new MainFrame();
    }
}
