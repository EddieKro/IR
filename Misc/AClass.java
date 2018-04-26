package Misc;
public class AClass {

    private int intVal;
    private boolean boolVal;
    private String stringVal;
    private Long longVal;
    private int anotherIntVal;

    public int readIntVal() {
        return intVal;
    }

    public boolean isBoolVal() {
        return boolVal;
    }

    public String getStringVal() {
        return stringVal != null ? stringVal : "Unknown";
    }

    public Long getLongVal() {
        return longVal;
    }

}