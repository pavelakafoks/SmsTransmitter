package timeplan.me.smstransmitter.models;

public enum EnumLogType {
    Info(0),
    Error(1);

    private final int value;
    EnumLogType(int value) {
        this.value = value;
    }

    public int GetValue() {
        return value;
    }

    public static EnumLogType SetValue(int value) {
        if (value == 0) return Info;
        return Error;
    }
}
