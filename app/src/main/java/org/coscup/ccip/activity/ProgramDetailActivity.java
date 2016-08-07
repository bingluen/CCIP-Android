package org.coscup.ccip.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.bind.util.ISO8601Utils;

import org.coscup.ccip.R;
import org.coscup.ccip.application.ScheduleNotificationReceiver;
import org.coscup.ccip.model.Program;
import org.coscup.ccip.util.PreferenceUtil;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProgramDetailActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_PROGRAM = "program";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_detail);

        mContext = this;

        final Gson gson = new Gson();
        final Program program = gson.fromJson(getIntent().getStringExtra(INTENT_EXTRA_PROGRAM), Program.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (getResources().getConfiguration().locale.getLanguage().startsWith("zh")) {
            toolbar.setTitle(program.getRoomname());
        } else {
            toolbar.setTitle(program.getRoom());
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        TextView speakername, subject, time, type, lang, speakerInfo, programAbstract;
        speakername = (TextView) findViewById(R.id.speakername);
        subject = (TextView) findViewById(R.id.subject);
        time = (TextView) findViewById(R.id.time);
        type = (TextView) findViewById(R.id.type);
        lang = (TextView) findViewById(R.id.lang);
        speakerInfo = (TextView) findViewById(R.id.speakerinfo);
        programAbstract = (TextView) findViewById(R.id.program_abstract);

        final boolean[] isStar = {PreferenceUtil.getStarPrograms(mContext).contains(program.getSlot())};
        if (isStar[0]) fab.setImageResource(R.drawable.ic_star_white_48dp);

        speakername.setText(program.getSpeakername());
        subject.setText(program.getSubject());

        try {
            StringBuffer timeString = new StringBuffer();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
            Date startDate = ISO8601Utils.parse(program.getStarttime(), new ParsePosition(0));
            timeString.append(sdf.format(startDate));
            timeString.append(" ~ ");
            sdf = new SimpleDateFormat("HH:mm");
            Date endDate = ISO8601Utils.parse(program.getEndtime(), new ParsePosition(0));
            timeString.append(sdf.format(endDate));

            timeString.append(", " + ((endDate.getTime() - startDate.getTime()) / 1000 / 60) + getResources().getString(R.string.min));

            time.setText(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (getResources().getConfiguration().locale.getLanguage().startsWith("zh")) {
            type.setText(program.getTypenamezh());
        } else {
            type.setText(program.getTypenameen());
        }
        lang.setText(program.getLang());
        speakerInfo.setText(program.getSpeakerintro());
        programAbstract.setText(program.getAbstract());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, ScheduleNotificationReceiver.class);

                if (!isStar[0]) {
                    isStar[0] = true;
                    fab.setImageResource(R.drawable.ic_star_white_48dp);

                    PreferenceUtil.starProgram(mContext, program.getSlot());

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, 10);

                    intent.putExtra(INTENT_EXTRA_PROGRAM, gson.toJson(program));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, program.getSlot().hashCode(), intent, PendingIntent.FLAG_ONE_SHOT);

                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    isStar[0] = false;
                    fab.setImageResource(R.drawable.ic_star_border_white_48dp);
                    PreferenceUtil.unstarProgram(mContext, program.getSlot());
                    am.cancel(PendingIntent.getBroadcast(mContext, program.getSlot().hashCode(), intent, PendingIntent.FLAG_ONE_SHOT));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
