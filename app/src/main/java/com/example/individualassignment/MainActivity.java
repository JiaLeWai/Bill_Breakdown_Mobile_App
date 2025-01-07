package com.example.individualassignment;

import android.adservices.customaudience.CustomAudienceManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.icu.text.DateFormat;
import android.icu.text.ListFormatter;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.math.RoundingMode;
import java.sql.Date;

public class MainActivity extends AppCompatActivity {

    SQLiteAdapter sqliteAdapter;

    LinearLayout cal_p;
    final String Equally = "Equally";
    final String Percentage = "Percentage";
    final String Custom = "Custom";

    Spinner splitChoice;

    TableLayout participant_table;

    EditText[] split_amount_text = {};
    EditText[] percentage_text = {};
    ImageView[] sent_icon;

    EditText ppl_num_text;
    EditText total_text;

    Button confirmChoice;

    double totalAmount = 0;
    double accuPer = 0; //accumulated Amount for percentage choice
    double accuAmount = 0;
    TextView accu_text;

    boolean isWhatsappInstalled; //check whatsapp is Installed or nt
    DrawerLayout bg_ll;
    ActionBarDrawerToggle drawer_button;
    NavigationView nav;

    EditText sum_name_enter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sqliteAdapter = new SQLiteAdapter(this);

        //DrawerLayout will be the main layout of the Calculator and History Activity
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
                    case R.id.History:
                            Intent gg = new Intent(MainActivity.this,History.class);
                            startActivity(gg);
                            bg_ll.closeDrawers();

                    case R.id.Calculator:
                        bg_ll.closeDrawers();
                        return true;
                    default:
                        return true;
                }
            }
        });



        ScrollView mainPage = new ScrollView(this); //ScrollView of the activity
        bg_ll.addView(mainPage);
        bg_ll.addView(nav);

        cal_p = new LinearLayout(this);

        drawer_button = new ActionBarDrawerToggle(this,bg_ll, R.string.nav_open , R.string.nav_close);
        bg_ll.addDrawerListener(drawer_button);
        drawer_button.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cal_p.setOrientation(LinearLayout.VERTICAL);
        cal_p.removeView(participant_table);
        cal_p.removeView(accu_text);
        cal_p.setBackgroundColor(Color.WHITE);



        LinearLayout total_text_ll = new LinearLayout(this);
        total_text_ll.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        childParams.setMargins(50,15,15,15);

        total_text = new EditText(this);
        total_text.setTextSize(20.0f);
        total_text.setHint("Total Amount");
        total_text.setLayoutParams(childParams);
        total_text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        ImageView total_text_p1 = new ImageView(this);
        total_text_p1.setBackgroundResource(R.drawable.p1);
        total_text_p1.setLayoutParams(childParams);


        total_text_ll.addView(total_text_p1);
        total_text_ll.addView(total_text);


        LinearLayout ppl_text_ll = new LinearLayout(this);
        ppl_text_ll.setOrientation(LinearLayout.HORIZONTAL);

        ImageView ppl_text_p = new ImageView(this);
        ppl_text_p.setBackgroundResource(R.drawable.p2);
        ppl_text_p.setLayoutParams(childParams);

        ppl_num_text = new EditText(this);
        ppl_num_text.setTextSize(20.0f);
        ppl_num_text.setHint("Number of people");
        ppl_num_text.setLayoutParams(childParams);
        ppl_num_text.setInputType(InputType.TYPE_CLASS_NUMBER);


        ppl_text_ll.addView(ppl_text_p);
        ppl_text_ll.addView(ppl_num_text);

        cal_p.addView(total_text_ll);
        cal_p.addView(ppl_text_ll);


        LinearLayout choice_ll = new LinearLayout(this);
        choice_ll.setOrientation(LinearLayout.HORIZONTAL);





        ImageView choice_p = new ImageView(this);
        choice_p.setBackgroundResource(R.drawable.p3);
        choice_p.setLayoutParams(childParams);

        //choose split choice
        confirmChoice = new Button(this);
        confirmChoice.setTextSize(20.0f);
        confirmChoice.setText("Confirm");
        confirmChoice.setLayoutParams(childParams);

        splitChoice = new Spinner(this);
        splitChoice.setLayoutParams(childParams);
        String[] choices = new String[]{Equally, Percentage, Custom};
        ArrayAdapter<String> choiceAdap = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, choices);
        splitChoice.setAdapter(choiceAdap);
        splitChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //if another choice is chosem, then remove the table and enable the button
                confirmChoice.setFocusable(true);
                confirmChoice.setClickable(true);
                confirmChoice.setLongClickable(true);
                confirmChoice.setEnabled(true);
                cal_p.removeView(participant_table);
                cal_p.removeView(accu_text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        choice_ll.addView(choice_p);
        choice_ll.addView(splitChoice);
        cal_p.addView(choice_ll);

        cal_p.addView(confirmChoice);
        TextView gg = new TextView(this);




        //Set Confirm Button listener
        confirmChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                accuAmount = 0; //initialize

                //if a choice is chosen, the button will be unabled
                confirmChoice.setFocusable(false);
                confirmChoice.setClickable(false);
                confirmChoice.setLongClickable(false);
                confirmChoice.setEnabled(false);

                if((total_text.getText().toString().equals(""))||(total_text.getText().toString().equals("0")) || (ppl_num_text.getText().toString().equals(""))||(ppl_num_text.getText().toString().equals("0"))) //set the button enabled again
                {
                    confirmChoice.setFocusable(true);
                    confirmChoice.setClickable(true);
                    confirmChoice.setLongClickable(true);
                    confirmChoice.setEnabled(true);
                }

                if (total_text.getText().toString().equals("")) //check the user enter amount or not
                    total_text.setText("0");
                if (ppl_num_text.getText().toString().equals("")) //check the user enter ppl total or not
                    ppl_num_text.setText("0");



                totalAmount = Double.parseDouble(total_text.getText().toString());
                int ppl_num = Integer.parseInt(ppl_num_text.getText().toString());
                participant_table = new TableLayout(view.getContext());
                participant_table.setStretchAllColumns(false);
                participant_table.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));




                if (ppl_num > 0) {
                    accu_text = new TextView(view.getContext());
                    LinearLayout.LayoutParams accu_text_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    accu_text_lp.gravity= Gravity.CENTER;

                    accu_text.setLayoutParams(accu_text_lp);
                    accu_text.setTextSize(20.0f);
                    accu_text.setTypeface(null, Typeface.BOLD);


                    String choice = splitChoice.getSelectedItem().toString();


                    //add participant table
                    addParticipant(view, participant_table, choice, ppl_num);
                    cal_p.addView(participant_table);


                    if (choice.equals(Equally)) {
                        double shareAmount = 0;
                        shareAmount = (double)(Math.round((totalAmount / ppl_num)*100))/100;

                        for (int i = 0; i < ppl_num; i++) {
                            //set shareAmount
                            ((TextView) ((TableRow) participant_table.getChildAt(i+1)).getChildAt(1)).setText(String.format("%.2f", shareAmount)); //jiale:would modify to global variable
                        }
                        double remaining = totalAmount-(shareAmount*ppl_num);
                        if(remaining!=0)
                        {
                            double split_to = 0;
                            split_to = (remaining/0.01); //get the number of ppl to be added with or subtracted by 0.01
                            for (int i = 0; i < Math.abs(Math.round(split_to)); i++) {
                                if (split_to > 0)
                                    ((TextView) ((TableRow) participant_table.getChildAt(i + 1)).getChildAt(1)).setText(String.format("%.2f", shareAmount + 0.01));
                                else
                                    ((TextView) ((TableRow) participant_table.getChildAt(i + 1)).getChildAt(1)).setText(String.format("%.2f", shareAmount - 0.01));
                            }
                        }
                    } else if (choice.equals(Percentage)) {
                        for (int i = 0; i < ppl_num; i++) {
                            int a;
                            ((EditText) ((TextView) ((TableRow) participant_table.getChildAt(i+1)).getChildAt(1))).addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int a, int a1, int a2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {


                                    accuPer = 0;
                                    accuAmount = 0;
                                    for (int i = 0; i < split_amount_text.length; i++) {
                                        String percentage_str = percentage_text[i].getText().toString();
                                        if (!percentage_str.equals("")) {

                                            double percentage = Double.parseDouble(percentage_str);
                                            double split_amount = (double) Math.round(totalAmount * (percentage / 100) * 100) / 100;

                                            split_amount_text[i].setText(String.format("%.2f", split_amount));
                                            accuPer += Double.parseDouble(percentage_str);
                                            accuAmount += split_amount;

                                            if (accuPer > 100) //check whether the percentage is greater 100%
                                            {
                                                accu_text.setTextColor(Color.RED);
                                            } else {
                                                accu_text.setTextColor(Color.BLACK);
                                            }

                                            accu_text.setText(String.format("%.2f of 100%%\nRM%.2f of RM%.2f", accuPer, accuAmount, totalAmount));

                                        } else {

                                             split_amount_text[i].setText(""+0);
                                        }

                                    }


                                    //checking the amount entered whether is compatible to the total amount when the accumulated percentage is 100%
                                    if (accuPer == 100) {
                                        double remaining1 = totalAmount - (accuAmount);

                                        int[] highest_round_index;
                                        if (remaining1 != 0) {
                                             //get the remaning amount
                                            int ppl_num =(int) ((double) remaining1 / 0.01);;
                                            double[] split_near_round = new double[split_amount_text.length];
//                                        }

                                            for (int i = 0; i < split_amount_text.length; i++) {

                                                Integer percentage = Integer.parseInt(percentage_text[i].getText().toString());
                                                if (percentage > 0) {


                                                    double curr_percentage = Double.parseDouble(percentage_text[i].getText().toString());
                                                    double split_amount = (double) totalAmount * (percentage / 100);
                                                    double split_amount_show =  (double) Math.round(totalAmount * (curr_percentage / 100) * 100) / 100;
                                                    double floating = (split_amount - split_amount_show); // get the range between the display amount and the actual amount
                                                    double range;

                                                    if (remaining1 > 0) { //if the remaining is larger than 0, remaining that we need to get the those value with the largest range between the actual amount, details is explained in report


                                                        if (floating > 0) { //range is positive
                                                            split_near_round[i] = Math.abs(floating);
                                                        } else {
                                                            split_near_round[i] = 0;
                                                        }


                                                    } else {
                                                        if (floating < 0) {  //range will be negative
                                                            split_near_round[i] = Math.abs(floating);
                                                        } else {
                                                            split_near_round[i] = 0;
                                                        }
                                                    }
                                                } else {
                                                    split_near_round[i] = 0;
                                                }
                                            }

                                            highest_round_index = new int[ppl_num];


                                            //save the highest one(sorting)
                                            for (int i = 0; i < split_near_round.length; i++) {
                                                if (i < ppl_num) {
                                                    if (split_near_round[i] != 0) {
                                                        highest_round_index[i] = i;
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    for (int j = 0; j < ppl_num; j++) {
                                                        if (split_near_round[i] > 0) {

                                                            if (split_near_round[i] > split_near_round[highest_round_index[j]]) {
                                                                highest_round_index[j] = i; //choose the highest range, and save its index
                                                                break;
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                            //change the value of the show amounts
                                            for (int i = 0; i < ppl_num; i++) {
                                                int index = highest_round_index[i];
                                                double ori_amount =  (double) Math.round(totalAmount * (Double.parseDouble(percentage_text[index].getText().toString()) / 100) * 100) / 100;
                                                //added with 0.01 or subtracted by 0.01 to those amount to make the total amount and total split amount same
                                                if (remaining1 > 0) {
                                                    split_amount_text[index].setText(String.format("%.2f", ori_amount + 0.01));
                                                    accuAmount+=0.01;
                                                } else if (remaining1 < 0) {
                                                    split_amount_text[index].setText(String.format("%.2f", ori_amount - 0.01));
                                                    accuAmount-=0.01;
                                                }
                                            }


                                            accu_text.setText(String.format("%.2f of 100%%\nRM%.2f of RM%.2f", accuPer, accuAmount, totalAmount));


                                        }
                                    }


                                }
                            });


                        }
                        accu_text.setText(String.format("%.2f of 100%%\nRM%.2f of RM%.2f", accuPer, accuAmount, totalAmount));
                        cal_p.addView(accu_text);
                    } else if (choice.equals(Custom)) {
                        for (int i = 0; i < ppl_num; i++) {
                            split_amount_text[i].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    accuAmount = 0;
                                    for (int i = 0; i < split_amount_text.length; i++) {
                                        if (!split_amount_text[i].getText().toString().equals("")) {
                                            accuAmount += Double.parseDouble(split_amount_text[i].getText().toString());
                                            if (accuAmount > totalAmount) //check whether the percentage is greater 100%
                                            {
                                                accu_text.setTextColor(Color.RED);
                                            } else {
                                                accu_text.setTextColor(Color.BLACK);
                                            }

                                            accu_text.setText(String.format("RM%.2f of RM%.2f", accuAmount, totalAmount));

                                        }

                                    }

                                }
                            });
                        }
                        accu_text.setText(String.format("RM%.2f of RM%.2f", accuAmount, totalAmount));
                        cal_p.addView(accu_text);

                    }

                }
            }
        });


        mainPage.addView(cal_p);
        setContentView(bg_ll);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawer_button.onOptionsItemSelected(item)) {
            return true;
        }
        else
        {


            if(item.getItemId() == R.id.Save)
            {
                //Save as function
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Save As");
                builder.setMessage("Please enter a name for this summary");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ia_save);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String sum_name_str = sum_name_enter.getText().toString();
                        sqliteAdapter.openToWrite();
                        if(!sum_name_str.equals(""))
                        {

                            sqliteAdapter.insert(sum_name_str, String.valueOf(totalAmount), ppl_num_text.getText().toString(), splitChoice.getSelectedItem().toString(), DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())));

                        }
                        else
                        {

                            sqliteAdapter.insert("Default", String.valueOf(totalAmount), ppl_num_text.getText().toString(), splitChoice.getSelectedItem().toString(), DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())));
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                sum_name_enter = new EditText(getApplicationContext());
                sum_name_enter.setHint("Enter Here");
                sum_name_enter.setMaxLines(1);
                sum_name_enter.setTextSize(20);
             //   builder.setIcon(R.drawable.gg);
                builder.setView(sum_name_enter);
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
            else if (item.getItemId() == R.id.Reset)
            {

                cal_p.removeView(participant_table);
                cal_p.removeView(accu_text);

                confirmChoice.setFocusable(true);
                confirmChoice.setClickable(true);
                confirmChoice.setLongClickable(true); //set the amount cannot be edited manually
                confirmChoice.setEnabled(true);

                total_text.setText("");
                ppl_num_text.setText("");
            }
        }
        return super.onOptionsItemSelected(item);
    }



    private void addParticipant(View view, TableLayout tb, String choice, int ppl_num) {
        split_amount_text = new EditText[ppl_num];
        sent_icon = new ImageView[ppl_num];


        isWhatsappInstalled = isWhatsAppIns();

        TableRow IndexRow = new TableRow(view.getContext());

        TableRow.LayoutParams friend_txt_l = new TableRow.LayoutParams();


        //Label row
        TextView friend_text = new TextView(view.getContext());
        friend_text.setText("Friend");
        friend_text.setTextSize(25.0f);
        friend_text.setTypeface(null, Typeface.BOLD);
        friend_text.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3));

        friend_text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView percentage_text_tv = new TextView(view.getContext());
        percentage_text_tv.setText("%");
        percentage_text_tv.setTextSize(25.0f);
        percentage_text_tv.setTypeface(null, Typeface.BOLD);
        percentage_text_tv.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
        percentage_text_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView amount_text = new TextView(view.getContext());
        amount_text.setText("RM");
        amount_text.setTextSize(25.0f);
        amount_text.setTypeface(null, Typeface.BOLD);
        amount_text.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
        amount_text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        TextView send_text = new TextView(view.getContext());
        send_text.setText("Send");
        send_text.setTextSize(25.0f);
        send_text.setTypeface(null, Typeface.BOLD);
        send_text.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
        send_text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        if(choice.equals(Equally))
        {

            IndexRow.addView(friend_text);
            IndexRow.addView(amount_text);
            IndexRow.addView(send_text);
            tb.addView(IndexRow);
        }

        //   create percentage array
        else if (choice.equals(Percentage)) {
            percentage_text = new EditText[ppl_num];
            IndexRow.addView(friend_text);
            IndexRow.addView(percentage_text_tv);
            IndexRow.addView(amount_text);
            IndexRow.addView(send_text);
            tb.addView(IndexRow);
        }

        else if (choice.equals(Custom))
        {
            IndexRow.addView(friend_text);
            IndexRow.addView(amount_text);
            IndexRow.addView(send_text);
            tb.addView(IndexRow);
        }



        //start to add the table row based on the chocie chosen
        for (int i = 0; i < ppl_num; i++) {
            LinearLayout.LayoutParams row_lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            row_lp.setMargins(30, 30,30,30);

            TableRow row = new TableRow(view.getContext());
            row.setLayoutParams(row_lp);

            LinearLayout participant_edit_ll = new LinearLayout(view.getContext());
            participant_edit_ll.setOrientation(LinearLayout.HORIZONTAL);
         //   participant_edit_ll.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            participant_edit_ll.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3));




            TextView participants_seq = new TextView(view.getContext());
            participants_seq.setText("Friend " + (i + 1));
            participants_seq.setTextSize(20.0f);
            participants_seq.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
            participants_seq.setPadding(10,10,10,10);
            participants_seq.setMaxLines(1);
            participants_seq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            participant_edit_ll.addView(participants_seq);




            if (choice.equals(Equally) || choice.equals(Custom)) {

                //split_amount column
                split_amount_text[i] = new EditText(view.getContext());
                split_amount_text[i].setTextSize(20.0f);
                split_amount_text[i].setPadding(10,10,10,10);
                split_amount_text[i].setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
                split_amount_text[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                split_amount_text[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                if (choice.equals(Equally)) {
                    split_amount_text[i].setFocusable(false);
                    split_amount_text[i].setClickable(false);
                    split_amount_text[i].setLongClickable(false); //set the amount cannot be edited manually

                }

                split_amount_text[i].setText("0"); //initialize all the amount to 0 first

                //sent_button column
                sent_icon[i] = new ImageView(view.getContext());
                sent_icon[i].setImageResource(R.drawable.ia_send);
                sent_icon[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                sent_icon[i].setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
                sent_icon[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int icon_index = 0;
                        for(int i=0; i<sent_icon.length; i++)
                        {
                            if(view == sent_icon[i])
                            {
                                icon_index = i;
                            }
                        }
                        if (isWhatsappInstalled) {

                            Intent whatsApp = new Intent();
                            whatsApp.setAction(Intent.ACTION_SEND);
                            whatsApp.putExtra(Intent.EXTRA_TEXT, "Hi, I use eZSplit to split our amount and this is your amount.\n\n*RM "+ split_amount_text[icon_index].getText().toString()+"* \n\nYou can pay to me later!");
                            whatsApp.setType("text/plain");
                            whatsApp.setPackage("com.whatsapp");
                            startActivity((whatsApp));
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Whatsapp is not installed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                row.addView(participant_edit_ll);
                row.addView(split_amount_text[i]);
                row.addView(sent_icon[i]);

                tb.addView(row);
            } else if (choice.equals(Percentage)) {
                split_amount_text[i] = new EditText(view.getContext());
                split_amount_text[i].setFocusable(false);
                split_amount_text[i].setClickable(false);
                split_amount_text[i].setLongClickable(false);//set the amount cannot be edited manually
                split_amount_text[i].setText("0");
                split_amount_text[i].setTextSize(20.0f);
                split_amount_text[i].setPadding(10,10,10,10);
                split_amount_text[i].setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
                split_amount_text[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                split_amount_text[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


                //percentage amount column
                percentage_text[i] = new EditText(view.getContext());
                //  percentage_text[i].setTextSize(20.0f);
                percentage_text[i].setText("0");
                percentage_text[i].setPadding(10,10,10,10);
                percentage_text[i].setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
                percentage_text[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                percentage_text[i].setTextSize(20.0f);
                percentage_text[i].setInputType(InputType.TYPE_CLASS_NUMBER);


                sent_icon[i] = new ImageView(view.getContext());
                sent_icon[i].setImageResource(R.drawable.ia_send);
                sent_icon[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                sent_icon[i].setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
                sent_icon[i].setOnClickListener(new View.OnClickListener() {
                    //check whatsapp whether is installed
                    @Override
                    public void onClick(View view) {
                        int icon_index = 0;
                        for(int i=0; i<sent_icon.length; i++)
                        {
                            if(view == sent_icon[i])
                            {
                                icon_index = i;
                            }
                        }
                        if (isWhatsappInstalled) {

                            Intent whatsApp = new Intent();
                            whatsApp.setAction(Intent.ACTION_SEND);
                            whatsApp.putExtra(Intent.EXTRA_TEXT, "Hi, I use eZSplit to split our amount and this is your amount.\n\n*RM "+ split_amount_text[icon_index].getText().toString()+"* \n\nYou can pay to me later!");
                            whatsApp.setType("text/plain");
                            whatsApp.setPackage("com.whatsapp");
                            startActivity((whatsApp));
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Whatsapp is not installed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                row.addView(participant_edit_ll);
                row.addView(percentage_text[i]);
                row.addView(split_amount_text[i]);
                row.addView(sent_icon[i]);


                tb.addView(row);


            }

        }


    }

    private boolean isWhatsAppIns()
    {
        PackageManager pckM = getPackageManager();
        boolean isIns;

        try
        {
            pckM.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            isIns = true;

        }catch (PackageManager.NameNotFoundException e)
        {
            isIns = false;

        }

        return isIns;
    }


}