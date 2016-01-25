package de.htw_berlin.sharkandroidstack.system_modules.log;

import net.sharkfw.system.L;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import de.htw_berlin.sharkandroidstack.system_modules.settings.SettingsManager;

/**
 * Created by mn-io on 25.01.2016.
 */
public class LogStreamHelper extends PrintStream {

    public static final int[] logLevels = new int[]{L.LOGLEVEL_SILENT, L.LOGLEVEL_ERROR, L.LOGLEVEL_WARNING, L.LOGLEVEL_DEBUG, L.LOGLEVEL_ALL};
    public static final String[] logLevelNames = new String[]{"silent", "error", "warning", "debug", "all"};

    private static String logId;
    private LogManager.LogEntry currentEntry;
    private final int prio;

    public static void init(String logId) {
        LogStreamHelper.logId = logId;
        setLogLevelFromPreferences();

        PrintStream out = new LogStreamHelper(System.out, 1);
        PrintStream err = new LogStreamHelper(System.err, 2);
        L.setLogStreams(out, err);
    }

    public static void setLogLevelFromPreferences() {
        final String value = SettingsManager.getValue(SettingsManager.KEY_CONNECTION_LOG_LEVEL, LogStreamHelper.logLevelNames[4]);
        final int indexOfLogLevel = Arrays.asList(LogStreamHelper.logLevelNames).indexOf(value);
        final int logLevel = LogStreamHelper.logLevels[indexOfLogLevel];

        L.setLogLevel(logLevel);
    }

    public LogStreamHelper(OutputStream out, int prio) {
        super(out);
        this.prio = prio;

        createNewEntry();
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        currentEntry.msg += String.format(format, args);
        return super.printf(format, args);
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        currentEntry.msg += String.format(l, format, args);
        return super.printf(l, format, args);
    }

    @Override
    public void print(char[] chars) {
        currentEntry.msg += chars;
        super.print(chars);
    }

    @Override
    public void print(char c) {
        currentEntry.msg += c;
        super.print(c);
    }

    @Override
    public void print(double d) {
        currentEntry.msg += d;
        super.print(d);
    }

    @Override
    public void print(float f) {
        currentEntry.msg += f;
        super.print(f);
    }

    @Override
    public void print(int i) {
        currentEntry.msg += i;
        super.print(i);
    }

    @Override
    public void print(long l) {
        currentEntry.msg += l;
        super.print(l);
    }

    @Override
    public void print(Object o) {
        currentEntry.msg += o;
        super.print(o);
    }

    @Override
    public synchronized void print(String str) {
        currentEntry.msg += str;
        super.print(str);
    }

    @Override
    public void print(boolean b) {
        currentEntry.msg += b;
        super.print(b);
    }

    @Override
    public void println() {
        createNewEntry();

        super.println();
    }

    @Override
    public void println(char[] chars) {
        currentEntry.msg += chars;
        createNewEntry();

        super.println(chars);
    }

    @Override
    public void println(char c) {
        currentEntry.msg += c;
        createNewEntry();

        super.println(c);
    }

    @Override
    public void println(double d) {
        currentEntry.msg += d;
        createNewEntry();

        super.println(d);
    }

    @Override
    public void println(float f) {
        currentEntry.msg += f;
        createNewEntry();

        super.println(f);
    }

    @Override
    public void println(int i) {
        currentEntry.msg += i;
        createNewEntry();

        super.println(i);
    }

    @Override
    public void println(long l) {
        currentEntry.msg += l;
        createNewEntry();

        super.println(l);
    }

    @Override
    public void println(Object o) {
        currentEntry.msg += o;
        createNewEntry();

        super.println(o);
    }

    @Override
    public synchronized void println(String str) {
        currentEntry.msg += str;
        createNewEntry();

        super.println(str);
    }

    @Override
    public void println(boolean b) {
        currentEntry.msg += b;
        createNewEntry();

        super.println(b);
    }

    @Override
    public synchronized void flush() {
        super.flush();
        createNewEntry();
    }

    private void createNewEntry() {
        if (currentEntry != null && !currentEntry.msg.isEmpty()) {
            LogManager.addEntry(logId, currentEntry);
        }
        currentEntry = new LogManager.LogEntry();
        currentEntry.prio = prio;
    }
}
