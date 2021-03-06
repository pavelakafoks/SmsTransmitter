package info.ininfo.smstransmitter.engine;

import android.content.Context;
import android.telephony.SmsManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import info.ininfo.smstransmitter.R;
import info.ininfo.smstransmitter.helpers.DateTimeHelper;
import info.ininfo.smstransmitter.helpers.DbHelper;
import info.ininfo.smstransmitter.models.EnumLogType;
import info.ininfo.smstransmitter.models.EnumMessageStatus;
import info.ininfo.smstransmitter.models.Message;
import info.ininfo.smstransmitter.models.ReceiverResponse;
import info.ininfo.smstransmitter.models.ReceiverResponseItem;
import info.ininfo.smstransmitter.models.Settings;
import io.reactivex.subjects.BehaviorSubject;

public class SmsWorker {

    private Settings settings;
    private boolean _isAlarm;
    private boolean inProcess;
    private DbHelper dbHelper;
    public final BehaviorSubject<Boolean> workingStatusPublisher = BehaviorSubject.create();


    public SmsWorker(Context context, boolean isAlarm) {
        this.settings = new Settings(context);
        this.dbHelper = new DbHelper(context);
        this._isAlarm = isAlarm;
    }

    public void Process() {

        if (inProcess) {
            return;
        }

        String key = settings.GetKey();
        if (key == null || key.isEmpty()) {
            return;
        }

        inProcess = true;
        notifyStatus();

        try {
            String response = "";

            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            boolean isSilentTime = (currentHour > 20 || currentHour < 9) && !settings.GetSendAnyTime();

            long timeFromLastRequest = System.currentTimeMillis() - settings.getLastRequestTime();
            long disallowedInterval = settings.GetFrequency() * 60_000 / 3;
            boolean isRequestTooFrequent = _isAlarm && timeFromLastRequest < disallowedInterval;

            if (isSilentTime || isRequestTooFrequent) {
                if (isSilentTime && !_isAlarm) {
                    dbHelper.LogInsert(R.string.log_begin_wrong_time, EnumLogType.Error);
                }

                inProcess = false;
                notifyStatus();
                return;
            }

            boolean isError = false;

            try {
                if (_isAlarm) {
                    dbHelper.LogInsert(R.string.log_begin_is_alarm, EnumLogType.Info);
                    //if (_batterySaveMode){
                    //    dbHelper.LogInsert("batterySaveMode", EnumLogType.Info);
                    //}
                } else {
                    dbHelper.LogInsert(R.string.log_begin, EnumLogType.Info);
                }

                // for tests
                /*
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                */

                URL url = new URL(settings.GetUrlGateway());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // parameters
                HashMap<String, String> postDataParams = new HashMap<>();
                postDataParams.put("privateKey", key);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(GetPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                    dbHelper.LogInsert(R.string.log_web_server_response_error, EnumLogType.Error);
                    isError = true;
                }
            } catch (Exception e) {
                //dbHelper.LogInsert(e);
                dbHelper.LogInsert(R.string.log_web_server_error, EnumLogType.Error);
                isError = true;
            }

            if (!isError) {
                settings.setLastRequestTime(System.currentTimeMillis());

                int maxAttemptCount = 3;
                int sendDelay = 3000;

                try {
                    ReceiverResponse receiverResponse = new Gson().fromJson(response, ReceiverResponse.class);
                    //ReceiverResponse receiverResponse = new GsonBuilder()
                    //        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    //        .create()
                    //        .fromJson(response, ReceiverResponse.class);

                    if (receiverResponse.wrongKey) {
                        dbHelper.LogInsert(R.string.log_key_wrong, EnumLogType.Error);
                    } else {
                        if (receiverResponse.messages != null && receiverResponse.messages.size() > 0) {
                            dbHelper.LogInsert(R.string.log_amount_sms_to_send, EnumLogType.Info, String.valueOf(receiverResponse.messages.size()));
                            SmsManager smsManager = SmsManager.getDefault();
                            for (int i = 0; i < receiverResponse.messages.size(); i++) {
                                ReceiverResponseItem msg = receiverResponse.messages.get(i);
                                int msgId = dbHelper.MessageInsert(msg.phone
                                        , msg.name
                                        , DateTimeHelper.ToDate(msg.dtEvent)
                                        , DateTimeHelper.GetDefaultDate()
                                        , EnumMessageStatus.New.GetValue()
                                        , msg.text
                                        , msg.messageId);


                                // if DtEvent is defined and DtEvent < CurrentDate
                                if (!DateTimeHelper.IsDefaultDate(DateTimeHelper.ToDate(msg.dtEvent))
                                        && DateTimeHelper.ToDate(msg.dtEvent).before(Calendar.getInstance().getTime())) {
                                    dbHelper.MessageUpdateStatusId(msgId, EnumMessageStatus.Error);
                                    dbHelper.LogInsert(R.string.log_send_error_expired, EnumLogType.Error, Message.PhoneFormatted(msg.phone));
                                } else {
                                    boolean isSent = false;
                                    int attempts = 0;
                                    Exception lastException = null;

                                    ArrayList<String> messageArray = smsManager.divideMessage(msg.text);
                                    /*
                                    ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                                    for(int j = 0; j < messageArray.size(); j++ ){     // check delivery status for, book page 733
                                        sentIntents.add(sentPI);
                                    }
                                    */
                                    while (!isSent && attempts < maxAttemptCount) {
                                        attempts++;
                                        try {
                                            smsManager.sendMultipartTextMessage("+" + msg.phone, null, messageArray, null, null);
                                            isSent = true;
                                        } catch (Exception e) {
                                            isSent = false;
                                            lastException = e;
                                        }
                                        Thread.sleep(sendDelay);
                                    }
                                    if (!isSent) {
                                        dbHelper.MessageUpdateStatusId(msgId, EnumMessageStatus.Error);
                                        dbHelper.LogInsert(lastException);
                                        dbHelper.LogInsert(R.string.log_send_error, EnumLogType.Error, Message.PhoneFormatted(msg.phone));
                                    } else {
                                        dbHelper.MessageUpdateStatusId(msgId, EnumMessageStatus.Sent);
                                        dbHelper.LogInsert(R.string.log_send_success, EnumLogType.Info, Message.PhoneFormatted(msg.phone));
                                    }
                                }
                            }
                        } else {
                            dbHelper.LogInsert(R.string.log_no_messages, EnumLogType.Info);
                            try {
                                Thread.sleep(1000); // for better view
                            } catch (InterruptedException e) {
                                dbHelper.LogInsert(e);
                                //e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    dbHelper.LogInsert(e);
                    dbHelper.LogInsert(R.string.log_send_error_global, EnumLogType.Error);
                }
            }

        } finally {
            inProcess = false;
            notifyStatus();
        }
    }

    private void notifyStatus() {
        workingStatusPublisher.onNext(inProcess);
    }

    private String GetPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) first = false;
            else result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
