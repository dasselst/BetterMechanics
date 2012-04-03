package net.edoxile.debug;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class DebugManager {

    private static final Logger logger = Logger.getLogger("minecraft");

    public static void log(String message) {
        logger.info("[Debug] " + message);
    }

    public static void log(String message, Level level) {
        logger.log(level, "[Debug] " + message);
    }

    private ArrayList<Debugger> debuggers = new ArrayList<Debugger>();
    private static DebugManager instance = new DebugManager();

    private DebugManager() {
    }

    public static DebugManager getInstance() {
        return instance;
    }

    public void registerDebugger(Debugger debugger) {
        debuggers.add(debugger);
    }

    public void runDebug() {
        for (Debugger debugger : debuggers) {
            if (debugger.testCode()) {
                log("Debugging of '" + debugger.getName() + "' successful!");
            } else {
                log("Failed debugging '" + debugger.getName() + "'");
            }
        }
    }
}
