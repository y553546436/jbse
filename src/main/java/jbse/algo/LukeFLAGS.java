package jbse.algo;
public class LukeFLAGS {
    public static boolean MAY_BE_VICTIM = false;
    public static boolean MAY_BE_POLLUTER = false;
    public static boolean ABSTRACT_METHOD_ERROR = false;
    public static boolean SYMBOLIC_READ = false;
    public static boolean SYMBOLIC_WRITE = false;
    public static boolean JBSE_UNMANAGABLE_ERROR = false;
    public static boolean EXCEPTION_RAISED = false;
    public static void print() {
        if (MAY_BE_VICTIM) 
            System.out.println("MAY_BE_VICTIM ");
        if (MAY_BE_POLLUTER)
            System.out.println("MAY_BE_POLLUTER");
        if (ABSTRACT_METHOD_ERROR)
            System.out.println("ABSTRACT_METHOD_ERROR");
        if (SYMBOLIC_READ)
            System.out.println("SYMBOLIC_READ");
        if (SYMBOLIC_WRITE)
            System.out.println("SYMBOLIC_WRITE");
        if (JBSE_UNMANAGABLE_ERROR)
            System.out.println("JBSE_UNMANAGABLE_ERROR");
        if (EXCEPTION_RAISED)
            System.out.println("EXCEPTION_RAISED");
    }
}
