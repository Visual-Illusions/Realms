package net.visualillusionsent.realms.io;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public final class LogFormat extends SimpleFormatter {
    private SimpleDateFormat dateform = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String linesep = System.getProperty("line.separator");

    public LogFormat() {
        super();
    }

    @Override
    public final String format(LogRecord rec) {
        StringBuilder message = new StringBuilder();

        message.append(dateform.format(Long.valueOf(rec.getMillis())));
        Level lvl = rec.getLevel();

        message.append(" [DEBUG] ");
        message.append("[");
        message.append(lvl.getName());
        message.append("] ");

        message.append(rec.getMessage());
        message.append(linesep);

        if (rec.getThrown() != null) {
            StringWriter stringwriter = new StringWriter();
            rec.getThrown().printStackTrace(new PrintWriter(stringwriter));
            message.append(stringwriter.toString());
        }

        return message.toString();
    }
}
