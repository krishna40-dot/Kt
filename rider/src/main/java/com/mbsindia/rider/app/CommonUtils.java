package com.mbsindia.rider.app;

import android.content.Context;
import android.widget.Toast;

import com.taxiappclone.common.helper.SQLiteHandler;
import com.taxiappclone.common.helper.SessionManager;

public class CommonUtils {
    public static void showToast(String msg){
        Toast.makeText(AppController.getInstance().getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public void logoutUser(Context context) {
        SessionManager session = AppController.getInstance().getSessionManager();
        session.setLogin(false);
        SQLiteHandler db = new SQLiteHandler(context);
        db.deleteDatabse();
        // Launching the login activity
    /*Intent intent = new Intent(context, LoginActivity.class);
    context.startActivity(intent);*/
        //finish();
    }
}
