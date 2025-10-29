package error;

import java.util.ArrayList;

public class ErrorManager {
    public static ArrayList<SysyError> errors = new ArrayList<SysyError>();
    public static boolean errorOn = true;
    public static void addError(SysyError error) {
        if(errorOn) {
            errors.add(error);
        }
    }
    public static void setErrorOn() {
        errorOn = true;
    }
    public static void setErrorOff() {
        errorOn = false;
    }
    public static boolean isEmpty(){
        return errors.isEmpty();
    }
}
