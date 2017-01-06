package com.example.kotlineapp.applogicexp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogin();
            }
        });


    }

    private void onLogin(){
        final String userId =  "ravikant@blazeautomation.com";
        final String displayName = "Ravikant Verma";
        String email = "ravikant@blazeautomation.com";

        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                // After successful registration with Applozic server the callback will come here

                ApplozicClient.getInstance(context).setContextBasedChat(true).setHandleDial(true);

                Map<ApplozicSetting.RequestCode, String> activityCallbacks = new HashMap<ApplozicSetting.RequestCode, String>();
                activityCallbacks.put(ApplozicSetting.RequestCode.USER_LOOUT, MainActivity.class.getName());
                ApplozicSetting.getInstance(context).setActivityCallbacks(activityCallbacks);

                if(MobiComUserPreference.getInstance(context).isRegistered()) {

                    PushNotificationTask pushNotificationTask = null;
                    PushNotificationTask.TaskListener listener = new PushNotificationTask.TaskListener() {
                        @Override
                        public void onSuccess(RegistrationResponse registrationResponse) {

                        }
                        @Override
                        public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                        }

                    };

                    pushNotificationTask = new PushNotificationTask(Applozic.getInstance(MainActivity.this).getDeviceRegistrationId(), listener, context);
                    pushNotificationTask.execute((Void) null);

                    buildContactData();
                    //starting main MainActivity
                    Intent mainActvity = new Intent(context, Main2Activity.class);
                    startActivity(mainActvity);
                    Intent intent = new Intent(context, ConversationActivity.class);
                    if(ApplozicClient.getInstance(MainActivity.this).isContextBasedChat()){
                        intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT,true);
                    }
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                // If any failure in registration the callback  will come here
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(exception.toString());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                if (!isFinishing()) {
                    alertDialog.show();
                }
            }};

        User user = new User();
        user.setUserId(userId); //userId it can be any unique user identifier
        user.setDisplayName(displayName); //displayName is the name of the user which will be shown in chat messages
        user.setEmail(email); //optional
        user.setImageLink("");//optional,pass your image link
        new UserLoginTask(user, listener, this).execute((Void) null);
    }

    private void buildContactData() {
        Context context = getApplicationContext();
        AppContactService appContactService = new AppContactService(context);
        appContactService.add(new Contact());
    }
}
