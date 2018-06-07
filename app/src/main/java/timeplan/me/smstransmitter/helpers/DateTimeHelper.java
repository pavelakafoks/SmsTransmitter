package timeplan.me.smstransmitter.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeHelper {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
    private static final String defaultDate = "2000-01-01 01-01-01";

    public static Date ToDate(String str){
        Date toRet = new Date();
        try{
            toRet = format.parse(str);  //
        } catch (Exception e) {
            //noinspection EmptyCatchBlock
            try {
                toRet = format.parse(defaultDate);
            } catch (ParseException e1) {
            }
        }
        return toRet;
    }

    public static String ToString(Date date){
        String toRet = defaultDate;
        //noinspection EmptyCatchBlock
        try{
            toRet = format.format(date);
        }catch (Exception e){
        }
        return toRet;
    }

    public static Date GetDefaultDate(){
        return ToDate(defaultDate);
    }

    public static Boolean IsDefaultDate(String date){
        //noinspection StringEquality
        return (defaultDate == date);
    }

    public static Boolean IsDefaultDate(Date date){
        //noinspection StringEquality
        try {
            return (format.parse(defaultDate) == date);
        } catch (ParseException e) {
            return true;
        }
    }
}
