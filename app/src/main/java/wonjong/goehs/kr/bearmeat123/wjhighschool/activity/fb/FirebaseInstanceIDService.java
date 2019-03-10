package wonjong.goehs.kr.bearmeat123.wjhighschool.activity.fb;

/**
 * Created by Windows on 2018-03-13.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.RemoteMessage;

import wonjong.goehs.kr.bearmeat123.wjhighschool.R;
import wonjong.goehs.kr.bearmeat123.wjhighschool.activity.main.MainActivity;
import wonjong.goehs.kr.bearmeat123.wjhighschool.activity.notice.NoticeActivity;
import wonjong.goehs.kr.bearmeat123.wjhighschool.activity.timetable.TimeTableActivity;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebase";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token){
        //기타 작업으로 활용

    }

}
