package timeplan.me.smstransmitter.models;

import java.util.Date;

public class Message {
    public int Id;
    public String Phone;
    public String Name;
    public Date DtCreate;
    public Date DtEvent;
    public Date DtSend;
    public EnumMessageStatus StatusId;
    public String Message;
    public int ServerId;

    public static String PhoneFormatted(String val){
        if (! val.startsWith("+")){
            val = "+" + val;
        }
        if(val.length() == 12){
            return val.substring(0, 2)
                    + "-" + val.substring(2, 5)
                    + "-" + val.substring(5, 8)
                    + "-" + val.substring(8, 10)
                    + "-" + val.substring(10, 12);
        }
        else if(val.length() == 11){
            return val.substring(0, 2)
                    + "-" + val.substring(2, 5)
                    + "-" + val.substring(5, 8)
                    + "-" + val.substring(8, 11);
        }
        else return val;
    }

    public String PhoneFormatted(){
        return PhoneFormatted(Phone);
    }
}
