package engine.launcher;

/**
 * Basic Logger that outputs messages to the console.
 *
 * @author Dan Bryce
 */
public class Logger {

    public static void log(Exception ex) {
        ex.printStackTrace();
    }

    public static void log(String message, Object... args){
        System.out.println(String.format(message, args));
    }

}
