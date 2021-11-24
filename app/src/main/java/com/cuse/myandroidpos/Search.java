package com.cuse.myandroidpos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

public class Search extends AppCompatActivity {

    Button start_date;
    Button end_date;
    DatePickerDialog datePickerDialog;
    myDate current_start_date;
    myDate current_end_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // initiate the date picker and a button
        start_date = (Button) findViewById(R.id.start_date);
        end_date = (Button) findViewById(R.id.end_date);
        // perform click event on edit text
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                current_start_date = new myDate();
                current_start_date.setYear(mYear);
                current_start_date.setMonth(mMonth + 1);
                current_start_date.setDay(mDay);
                // date picker dialog
                datePickerDialog = new DatePickerDialog(Search.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                start_date.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                current_end_date = new myDate();
                current_end_date.setYear(mYear);
                current_end_date.setMonth(mMonth + 1);
                current_end_date.setDay(mDay);

                // date picker dialog
                datePickerDialog = new DatePickerDialog(Search.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                end_date.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        ItemListFragment itemListFragment = new ItemListFragment();

    }

    public static class myDate {
        private int day;
        private int month;
        private int year;

        public myDate(int day, int month, int year) {
            this.day = day;
            this.month = month;
            this.year = year;
        }

        public myDate() {
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}