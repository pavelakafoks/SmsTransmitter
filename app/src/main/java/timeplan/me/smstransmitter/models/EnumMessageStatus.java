package timeplan.me.smstransmitter.models;

public enum EnumMessageStatus {
    New(0),
    Sent(1),
    Error(2);

    private final int value;
    EnumMessageStatus(int value) {
        this.value = value;
    }

    public int GetValue() {
        return value;
    }

    public static EnumMessageStatus SetValue(int value) {
        if (value == 0) return New;
        else if (value == 1) return Sent;
        return Error;
    }
}
