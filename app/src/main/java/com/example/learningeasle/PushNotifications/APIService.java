package com.example.learningeasle.PushNotifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAWO42Wr4:APA91bEWvv4GIhXjx9A1o3WxaBmXhk7xBZY09l8EOggCwgL2xc3FO9CprvYz_yui95Ftn34s2MEZL3DhaGItMzxU148yCbu0uYWtp48SM9KGC8Sdn-lSancB1tOLr-eznwUfQlW62heK" // Your server key refer to video for finding your server key
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
