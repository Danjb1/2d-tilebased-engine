package com.danjb.engine.application;

/**
 * Logger class used by the engine.
 *
 * <p>It is expected that games will implement their own logging system.
 * However, in cases where the engine itself needs to output some log messages,
 * this class is used.
 *
 * <p>This can be subclassed as needed to redirect the log messages elsewhere.
 *
 * @author Dan Bryce
 */
public abstract class Logger {

    private static Logger logger;

    public static void use(Logger logger) {
        Logger.logger = logger;
    }

    public static Logger get() {
        if (logger == null) {
            use(new SilentLogger());
        }
        return logger;
    }

    public abstract void log(Exception ex);

    public abstract void log(String message, Object... args);

    ////////////////////////////////////////////////////////////////////////////
    // SilentLogger
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Logger that silently discards messages.
     */
    public static class SilentLogger extends Logger {

        @Override
        public void log(Exception ex) {
            // Do nothing!
        }

        @Override
        public void log(String message, Object... args) {
            // Do nothing!
        }

    }

    ////////////////////////////////////////////////////////////////////////////
    // SimpleLogger
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Logger that outputs messages to the console.
     */
    public static class SimpleLogger extends Logger {

        @Override
        public void log(Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void log(String message, Object... args) {
            System.out.println(String.format(message, args));
        }

    }

}