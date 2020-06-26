package com.ibc.android.demo.appslist.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.wefika.calendar.CollapseCalendarView;
import com.wefika.calendar.manager.CalendarManager;

import java.time.LocalDate;
import java.util.List;

import static com.wefika.calendar.manager.CalendarManager.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // CalendarManager manager = new CalendarManager(LocalDate.now(), State.MONTH, LocalDate.now(), LocalDate.now().plusYears(1));

        CollapseCalendarView calendarView = (CollapseCalendarView) findViewById(R.id.calendar);





    }
}
