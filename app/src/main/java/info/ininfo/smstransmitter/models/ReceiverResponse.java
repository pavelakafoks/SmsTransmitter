package info.ininfo.smstransmitter.models;

import java.util.List;

public class ReceiverResponse {
    public List<ReceiverResponseItem> messages;
    // public String Hash; - not using. Not sure that it is improve safety
    public boolean wrongKey;
}
