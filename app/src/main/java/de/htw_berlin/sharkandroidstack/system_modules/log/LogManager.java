package de.htw_berlin.sharkandroidstack.system_modules.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mario Neises
 *         <p/>
 *         This implemantation is not thread save (yet).
 *         Android is usually running single threaded.
 */
public class LogManager {

    public final static ArrayList<ArrayList<LogEntry>> logEntries = new ArrayList<>();

    public final static ArrayList<String> logIds = new ArrayList<>();
    public final static ArrayList<String> logNames = new ArrayList<>();

    public final static ArrayList<ArrayList<LogChangeListener>> listeners = new ArrayList<>();

    public static void init() {
        registerLog("sys", "System");
    }

    public static List<String> getAllLogIds() {
        return Collections.unmodifiableList(logIds);
    }

    public static List<String> getAllNames() {
        return Collections.unmodifiableList(logNames);
    }

    public static String findLogIdByName(String name) {
        int index = logNames.indexOf(name);
        if (index == -1) {
            throw new IllegalStateException("Log with name '" + name + "' not registered.");
        }

        return logIds.get(index);
    }

    public static void registerLog(String logId, String name) {
        int index = logIds.indexOf(logId);
        if (index != -1) {
            logIds.remove(index);
            logNames.remove(index);
            logEntries.remove(index);
            listeners.remove(index);
            index = logIds.indexOf(logId);
        }

        if (index == -1) {
            logIds.add(logId);
            logNames.add(name);
            logEntries.add(new ArrayList<LogEntry>());
            listeners.add(new ArrayList<LogChangeListener>());
        }
    }

    public static void unregisterLog(String logId) {
        int index = logIds.indexOf(logId);
        if (index != -1) {
            logIds.remove(index);
            logNames.remove(index);
            logEntries.remove(index);
            listeners.remove(index);
        }
    }


    public static void addEntry(String logId, Object msg, int prio) {
        int index = findIndexByLogId(logId);

        String text = msg != null ? msg.toString() : "";
        LogEntry entry = new LogEntry(text, prio);
        logEntries.get(index).add(entry);
        notify(logId, entry);
    }

    public static void addThrowable(String logId, Throwable throwable) {
        int index = findIndexByLogId(logId);

        final String message = throwable.getMessage();
        final StackTraceElement element = throwable.getStackTrace()[0];

        String text = message + "\n\n" + element.toString();
        LogEntry entry = new LogEntry(text, 5);
        logEntries.get(index).add(entry);
        notify(logId, entry);
    }

    public static void addListener(LogChangeListener listener, String logId) {
        int index = findIndexByLogId(logId);
        ArrayList<LogChangeListener> list = listeners.get(index);
        list.add(listener);
    }

    public static void removeListener(LogChangeListener listener, String logId) {
        int index = findIndexByLogId(logId);
        ArrayList<LogChangeListener> list = listeners.get(index);
        list.remove(listener);
    }

    private static int findIndexByLogId(String logId) {
        int index = logIds.indexOf(logId);
        if (index != -1) {
            return index;
        }
        throw new IllegalStateException("Log with ID '" + logId + "' not registered.");
    }

    private static void notify(String logId, LogEntry entry) {
        int index = findIndexByLogId(logId);
        ArrayList<LogChangeListener> logChangeListeners = listeners.get(index);

        for (LogChangeListener listener : logChangeListeners) {
            listener.update(logId, entry);
        }
    }

    public static List<LogEntry> getAllEntries(String logId) {
        int index = findIndexByLogId(logId);
        return logEntries.get(index);
    }

    public interface LogChangeListener {
        void update(String name, LogEntry msgAndPrio);
    }

    public static class LogEntry {

        public LogEntry(String msg, int prio) {
            this.prio = prio;
            this.msg = msg;
        }

        public int prio = 0;
        public String msg = "";
    }
}
