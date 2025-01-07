package com.example.individualassignment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class History extends AppCompatActivity {
    ConstraintLayout[] history_row;
    View[] includedLayout;

    DrawerLayout bg_ll;
    ActionBarDrawerToggle drawer_button;
    NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    setContentView(R.layout.activity_history);


        SQLiteAdapter sql_history = new SQLiteAdapter(this); //prepare the SQLite Adapter
        sql_history.openToRead();


        bg_ll = new DrawerLayout(this);

        nav = new NavigationView(this);
        DrawerLayout.LayoutParams navLayoutParams = new DrawerLayout.LayoutParams(
                DrawerLayout.LayoutParams.WRAP_CONTENT,
                DrawerLayout.LayoutParams.MATCH_PARENT
        );
        navLayoutParams.gravity = Gravity.START;
        nav.setLayoutParams(navLayoutParams);
        nav.inflateMenu(R.menu.toolbar);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() { //set the navigation menu item listener
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.Calculator:

                        Intent gg = new Intent(History.this,MainActivity.class);
                        startActivity(gg);
                        bg_ll.closeDrawers();
                    case R.id.History:
                        bg_ll.closeDrawers();
                        return true;
                    default:
                        return true;
                }
            }
        });


        ScrollView history_ll_scroll = new ScrollView(this);
        bg_ll.addView(history_ll_scroll); // u must be added first or else nav cannot move
        bg_ll.addView(nav);


        drawer_button = new ActionBarDrawerToggle(History.this,bg_ll, R.string.nav_open , R.string.nav_close);
        bg_ll.addDrawerListener(drawer_button);
        drawer_button.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LinearLayout history_ll = new LinearLayout(this);
        history_ll.setOrientation(LinearLayout.VERTICAL);

        history_ll_scroll.addView(history_ll);
        Cursor his_cur = sql_history.getCursorAll();

        if (his_cur.getCount() > 0) { //check whether the cursor has item
            LinearLayout[] sum_box = new LinearLayout[his_cur.getCount()];

            LayoutInflater[] inflater = new LayoutInflater[his_cur.getCount()];
            includedLayout = new View[his_cur.getCount()];


            int i = 0;

            for (his_cur.moveToFirst(); !(his_cur.isAfterLast()); his_cur.moveToNext()) {

                sum_box[i] = new LinearLayout(this);
                if (i % 2 == 0)
                    sum_box[i].setBackgroundColor(Color.parseColor("#f0ffff"));
                else
                    sum_box[i].setBackgroundColor(Color.parseColor("#7df9ff"));

            inflater[i] = LayoutInflater.from(this);
            includedLayout[i] = inflater[i].inflate(R.layout.activity_history, history_ll, false);
            includedLayout[i].setId(i);
            ConstraintLayout test = includedLayout[i].findViewById(R.id.HistoryRow);
            test.setVisibility(View.VISIBLE);

            TextView sum_name = includedLayout[i].findViewById(R.id.textView0);
            int index = his_cur.getColumnIndex("sum_Name");
            if (index != -1) {
                sum_name.setText(his_cur.getString(index));
                sum_name.setMaxLines(1);
            }

            TextView choice = includedLayout[i].findViewById(R.id.textView1);
            index = his_cur.getColumnIndex("split_choice");
            if (index != -1) {
                choice.setText(his_cur.getString(index));
                choice.setMaxLines(1);
            }

            TextView Amount = includedLayout[i].findViewById(R.id.textView2);
            index = his_cur.getColumnIndex("total_Amount");
            if (index != -1) {
                Amount.setText("RM"+his_cur.getString(index));
                Amount.setMaxLines(1);
            }

            TextView date = includedLayout[i].findViewById(R.id.textView3);
            index = his_cur.getColumnIndex("date_time");
            if (index != -1) {
                date.setText(his_cur.getString(index));
                date.setMaxLines(1);
            }

            sum_box[i].addView(includedLayout[i]);
            history_ll.addView(sum_box[i]);
            i++;

        }

            setContentView(bg_ll);
    }
        else
        {
            setContentView(bg_ll);
        }

}


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawer_button.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}