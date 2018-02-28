package launcher;

public class Logger {

    public static void log(Exception ex) {
        ex.printStackTrace();
    }

    public static void log(String message, Object... args){
        System.out.println(String.format(message, args));
    }

}
