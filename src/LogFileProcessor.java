import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class LogFileProcessor {
    private static final String INFO = "INFO";
    private static final String WARN = "WARN";
    private static final String ERROR = "ERROR";

    private Queue<String> logQueue;
    private Stack<String> errorStack;
    private int infoCount, warnCount, errorCount, memoryWarnCount;

    public LogFileProcessor() {
        logQueue = new LinkedList<>();
        errorStack = new Stack<>();
        infoCount = warnCount = errorCount = memoryWarnCount = 0;
    }

    public void enqueueLogEntries(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        reader.readLine();
        while ((line = reader.readLine()) != null) {
            logQueue.offer(line);
        }
        reader.close();
    }

    public void dequeueAndProcessLogs() {
        Queue<String> recentErrors = new LinkedList<>();

        while (!logQueue.isEmpty()) {
            String logEntry = logQueue.poll();
            String logLevel = getLogLevel(logEntry);

            switch (logLevel) {
                case INFO:
                    infoCount++;
                    break;
                case WARN:
                    warnCount++;
                    if (logEntry.contains("Memory")) {
                        memoryWarnCount++;
                    }
                    break;
                case ERROR:
                    errorCount++;
                    errorStack.push(logEntry);
                    if (recentErrors.size() == 100) {
                        recentErrors.poll();
                    }
                    recentErrors.offer(logEntry);
                    break;
            }
        }

        System.out.println("Log Level Counts:");
        System.out.println("INFO: " + infoCount);
        System.out.println("WARN: " + warnCount);
        System.out.println("ERROR: " + errorCount);
        System.out.println("Memory Warnings: " + memoryWarnCount);

        System.out.println("\nRecent 100 Error Logs:");
        for (String error : recentErrors) {
            System.out.println(error);
        }
    }

    private String getLogLevel(String logEntry) {
        if (logEntry.contains(INFO)) {
            return INFO;
        } else if (logEntry.contains(WARN)) {
            return WARN;
        } else if (logEntry.contains(ERROR)) {
            return ERROR;
        }
        return "";
    }

    public static void main(String[] args) {
        LogFileProcessor processor = new LogFileProcessor();
        try {
            processor.enqueueLogEntries("lib/log-data.csv");
            processor.dequeueAndProcessLogs();
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }
    }
}
