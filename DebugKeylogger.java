import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

public class DebugKeylogger implements AWTEventListener {
    private PrintWriter logFile;
    private SimpleDateFormat dateFormat;
    private boolean running;

    public DebugKeylogger(String filename) {
        try {
            logFile = new PrintWriter(new FileWriter(filename, true), true);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            running = false;

            String timestamp = dateFormat.format(new Date());
            logFile.println(timestamp + ": Keylogger started");
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        if (event.getID() == KeyEvent.KEY_PRESSED) {
            KeyEvent keyEvent = (KeyEvent) event;
            String keyText = KeyEvent.getKeyText(keyEvent.getKeyCode());
            String timestamp = dateFormat.format(new Date());

            // Debugging output to console
            System.out.println("Key Pressed: " + keyText);
            logFile.println(timestamp + ": " + keyText);
        }
    }

    public void start() {
        if (running) {
            return;
        }

        running = true;
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);

        System.out.println("Keylogger started. Recording keystrokes to log file.");
        System.out.println("Press Ctrl+C in the console to stop.");
    }

    public void stop() {
        if (!running) {
            return;
        }

        running = false;
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);

        String timestamp = dateFormat.format(new Date());
        logFile.println(timestamp + ": Keylogger stopped");
        logFile.close();

        System.out.println("Keylogger stopped.");
    }

    public static void main(String[] args) {
        final DebugKeylogger keylogger = new DebugKeylogger("keylog.txt");
        keylogger.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                keylogger.stop();
            }
        });

        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            // Exit when interrupted
        }
    }
}