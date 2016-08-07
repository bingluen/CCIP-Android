package org.coscup.ccip.application;

import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import org.coscup.ccip.activity.ProgramDetailActivity;
import org.coscup.ccip.model.Program;

public class ScheduleNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Gson gson = new Gson();
        final Program program = gson.fromJson(intent.getStringExtra(ProgramDetailActivity.INTENT_EXTRA_PROGRAM), Program.class);
        Log.d("asd", program.getSubject());
        Log.d("asd", "Hello");
    }
}
